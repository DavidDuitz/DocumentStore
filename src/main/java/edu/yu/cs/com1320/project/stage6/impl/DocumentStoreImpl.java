package edu.yu.cs.com1320.project.stage6.impl;

import edu.yu.cs.com1320.project.impl.BTreeImpl;
import edu.yu.cs.com1320.project.impl.MinHeapImpl;
import edu.yu.cs.com1320.project.impl.StackImpl;
import edu.yu.cs.com1320.project.impl.TrieImpl;
import edu.yu.cs.com1320.project.stage6.Document;
import edu.yu.cs.com1320.project.stage6.DocumentStore;
import edu.yu.cs.com1320.project.undo.CommandSet;
import edu.yu.cs.com1320.project.undo.GenericCommand;
import edu.yu.cs.com1320.project.undo.Undoable;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;

public class DocumentStoreImpl implements DocumentStore{
    protected BTreeImpl<URI, Document> documents;
    private StackImpl<Undoable> commandStack;
    private HashSet<URI> docsInMemory;
    private TrieImpl<URI> documentTrie;
    //Make 2 tries to store the uris that have metadata keys and values]
    private TrieImpl<URI> metaKeysTrie;
    private TrieImpl<URI> metaValuesTrie;
    private MinHeapImpl<DocMin> docMinHeap;
    private int numDocs;
    private int numBytes;
    private int maxDocumentCount = Integer.MAX_VALUE;
    private int maxDocumentBytes = Integer.MAX_VALUE;
    //Instance variable to keep track of undos which have been popped from main stack
    private StackImpl<Undoable> tempStack;
    //Instance variable to hold onto current CommandSet that has been popped
    private CommandSet<URI> currentCommandSet;

    /// Private class to use in the minheap
    private class DocMin implements Comparable<DocMin>{
        private URI uri;

        public DocMin(URI uri){
            this.uri = uri;
        }

        @Override
        public boolean equals(Object obj){
            if(!(obj instanceof DocMin)){
                return false;
            }
            DocMin other = (DocMin) obj;
            if(this.uri.equals(other.uri)){
                return true;
            }
            else{
                return false;
            }
        }

        @Override
        public int compareTo(DocMin other) {
            Document myDoc = documents.get(this.uri);
            Document otherDoc = documents.get(other.uri);
            return myDoc.compareTo(otherDoc);
        }
    }

    //First constructor
    public DocumentStoreImpl(){
        this.documents = new BTreeImpl<URI, Document>();
        this.commandStack = new StackImpl<>();
        this.tempStack = new StackImpl<>();
        this.currentCommandSet = new CommandSet<>();
        this.documentTrie = new TrieImpl<>();
        this.metaKeysTrie = new TrieImpl<>();
        this.metaValuesTrie = new TrieImpl<>();
        this.docMinHeap = new MinHeapImpl<>();
        this.docsInMemory = new HashSet<>();
        DocumentPersistenceManager myDocPers = new DocumentPersistenceManager(null);
        this.documents.setPersistenceManager(myDocPers);
        //add a sentinel
        try {
            URI sentinel = new URI("http://!");
            this.documents.put(sentinel, null);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    //Constructor that accepts a base directory
    public DocumentStoreImpl(File baseDir){
        this.documents = new BTreeImpl<URI, Document>();
        this.commandStack = new StackImpl<>();
        this.tempStack = new StackImpl<>();
        this.currentCommandSet = new CommandSet<>();
        this.documentTrie = new TrieImpl<>();
        this.metaKeysTrie = new TrieImpl<>();
        this.metaValuesTrie = new TrieImpl<>();
        this.docMinHeap = new MinHeapImpl<>();
        this.docsInMemory = new HashSet<>();
        DocumentPersistenceManager myDocPers = new DocumentPersistenceManager(baseDir);
        this.documents.setPersistenceManager(myDocPers);
        //add a sentinel
        try {
            URI sentinel = new URI("http://!");
            this.documents.put(sentinel, null);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * set the given key-value metadata pair for the document at the given uri
     * @param uri
     * @param key
     * @param value
     * @return the old value, or null if there was no previous value
     * @throws IllegalArgumentException if the uri is null or blank, if there is no document stored at that uri, or if the key is null or blank
     */
    @Override
    public String setMetadata(URI uri, String key, String value) throws IOException{
        if(uri == null || uri.toString().isBlank()){
            throw new IllegalArgumentException("uri is null or blank");
        }
        Document doc = this.get(uri);
        if(doc == null){
            throw new IllegalArgumentException("No Document stored at that uri");
        }
        try{
            String stringToReturn = doc.setMetadataValue(key, value);
            //Update the meta-Tries for its new key and value
            if(stringToReturn != null) this.metaValuesTrie.delete(stringToReturn, uri);
            this.metaKeysTrie.put(key, uri);
            if(value != null) this.metaValuesTrie.put(value, uri);
            GenericCommand<URI> setMetaDataGenericCommand = new GenericCommand<>(uri, input -> {
                //Get the doc whose metadata is to be reverted, whether from disk or from memory
                Document revertedDoc;
                try {
                    revertedDoc = this.get(uri);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                String undoneValue = revertedDoc.setMetadataValue(key, stringToReturn);
                if(undoneValue != null) this.metaValuesTrie.delete(undoneValue, uri);
                if(stringToReturn != null) this.metaValuesTrie.put(stringToReturn, uri);
            });
            this.commandStack.push(setMetaDataGenericCommand);
            return stringToReturn;
        }catch (IllegalArgumentException e){
            throw new IllegalArgumentException("Key is null or blank");
        }
    }

    /**
     * get the value corresponding to the given metadata key for the document at the given uri
     * @param uri
     * @param key
     * @return the value, or null if there was no value
     * @throws IllegalArgumentException if the uri is null or blank, if there is no document stored at that uri, or if the key is null or blank
     */
    @Override
    public String getMetadata(URI uri, String key) throws IOException{
        if(uri == null || uri.toString().isBlank()){
            throw new IllegalArgumentException("uri is null or blank");
        }
        Document doc = this.get(uri);
        if(doc == null){
            throw new IllegalArgumentException("No Document stored at that uri");
        }
        try{
            String stringToReturn = doc.getMetadataValue(key);
            //[This docs last used time should have been updated by this.get()]
            return stringToReturn;
        }catch (IllegalArgumentException e){
            throw new IllegalArgumentException("Key is null or blank");
        }
    }

    /**
     * @param input the document being put
     * @param url unique identifier for the document
     * @param format indicates which type of document format is being passed
     * @return if there is no previous doc at the given URI, return 0. If there is a previous doc, return the hashCode of the previous doc.
     * If InputStream is null, this is a delete, and thus return either the hashCode of the deleted doc or 0 if there is no doc to delete.
     * @throws IOException if there is an issue reading input
     * @throws IllegalArgumentException if uri is null or empty, or format is null
     */
    @Override
    public int put(InputStream input, URI url, DocumentStore.DocumentFormat format) throws IOException{
        if(format == null || url == null || url.toString().isBlank()) throw new IllegalArgumentException("Format is null or Uri is null/blank");
        //If the input is null -- it is a call to delete the doc
        if(input == null){
            Document deletedDoc = this.documents.get(url);
            //If there was no doc with that uri, just return 0
            if(deletedDoc == null){
                return 0;
            }
            //Else, if the doc was removed from disk, put it back and call delete
            else{
                if(!docsInMemory.contains(url)){
                    this.documents.put(url, deletedDoc);
                    this.documents.moveToDisk(url);
                }
            }
            this.delete(url);
            return deletedDoc.hashCode();
        }
        return this.putDocInStore(input, url, format);
    }

    ///Private method to create a document and put it in the store
    private int putDocInStore(InputStream input, URI url, DocumentStore.DocumentFormat format) throws IOException{
        Document myDoc;
        if(format == DocumentFormat.TXT){
            myDoc = new DocumentImpl(url, this.convertInputToString(input), null);
        }
        else myDoc = new DocumentImpl(url, this.convertInputToByteArray(input));
        //Ensure there is enough room in the memory to add it to the minHeap
        //Get the document that is to be removed -- may be null
        Document toBeRemoved = this.documents.get(url);
        //Only account for the to be removed doc if it is currently in memory
        int toBeRemovedDocCount = 0;
        int toBeRemovedByteCount = 0;
        if(this.docsInMemory.contains(url)){
            toBeRemovedDocCount = 1;
            toBeRemovedByteCount = this.getDocBytes(toBeRemoved);
        }
        while(this.numBytes + this.getDocBytes(myDoc) - toBeRemovedByteCount > this.maxDocumentBytes || this.numDocs + 1 - toBeRemovedDocCount > this.maxDocumentCount){
            DocMin movedDocMin = this.docMinHeap.peek();
            Document movedDoc = this.documents.get(movedDocMin.uri);
            this.moveAllTraces(movedDoc);
        }
        //If the replaced doc was in memory -- completely remove it from memory
        if(this.docsInMemory.contains(url)) this.removeFromMemory(url, toBeRemoved);
        //At least delete it from the trie if it was a text doc
        else if(toBeRemoved != null && toBeRemoved.getDocumentTxt() != null) this.deleteAllTracesInTrie(toBeRemoved);
        //If it is not null at least delete its metadata from the meta-Tries
        if(toBeRemoved != null) this.deleteFromMetaTries(url, toBeRemoved);
        //Add doc to memory using helper method
        this.addToMemory(url, myDoc);
        //Call private method to create GenCommand and push it on command stack
        this.addPutGenCommand(url, toBeRemoved, myDoc);
        return toBeRemoved == null ? 0 : toBeRemoved.hashCode();
    }

    ///Private method to add a GenCommand within the put method
    private void addPutGenCommand(URI url, Document oldDoc, Document myDoc){
        GenericCommand<URI> putGenericCommand = new GenericCommand<>(url, holder -> {
            //Check if myDoc is in memory, and adjust accordingly
            Document myDocMemory = this.docsInMemory.contains(url) ? myDoc : null;
            int myDocDocCount = myDocMemory == null ? 0 : 1;
            int oldDocCount = oldDoc == null ? 0 : 1;
            while(this.numBytes + this.getDocBytes(oldDoc) - this.getDocBytes(myDocMemory) > this.maxDocumentBytes || this.numDocs + oldDocCount - myDocDocCount > this.maxDocumentCount){
                DocMin movedDocMin = this.docMinHeap.peek();
                Document movedDoc = this.documents.get(movedDocMin.uri);
                this.moveAllTraces(movedDoc);
            }
            //If url is in memory remove myDoc from memory
            if(this.docsInMemory.contains(url)) this.removeFromMemory(url, myDoc);
            //If myDoc is a text doc at least delete it from the trie
            if(myDoc.getDocumentTxt() != null) this.deleteAllTracesInTrie(myDoc);
            //Regardless delete it from meta-Tries, (Remember that myDoc can't be null)
            this.deleteFromMetaTries(url, myDoc);
            //If the oldDoc isn't null, add it to memory
            if(oldDoc != null) this.addToMemory(url, oldDoc);
            //If it is null, just "add it to the BTree" and overwrite myDoc
            else this.documents.put(url, oldDoc);
        });
        this.commandStack.push(putGenericCommand);
    }

    /// Private method to remove a document from memory
    private void removeFromMemory(URI url, Document doc){
        //If it was a text doc, it must be removed from the trie
        if(doc.getDocumentTxt() != null) this.deleteAllTracesInTrie(doc);
        //Remove it from the meta-Tries
        this.deleteFromMetaTries(url, doc);
        //Remove this docs' uri from the doc minHeap
        this.deleteURIFromHeap(new DocMin(url));
        //Lower memory counts
        this.numDocs--;
        this.numBytes -= this.getDocBytes(doc);
        //Erase it from docsToMemory map
        this.docsInMemory.remove(url);
    }

    ///Private method to delete a specific document from the minHeap
    private void deleteURIFromHeap(DocMin docMin){
        //Create temporary heap to store all the removed docs
        MinHeapImpl<DocMin> tempHeap = new MinHeapImpl<>();
        boolean addedToTempHeap = false;
        while(this.docMinHeap.peek() != null){
            DocMin removedDocMin = this.docMinHeap.remove();
            if(docMin.equals(removedDocMin)) break;
            addedToTempHeap = true;
            tempHeap.insert(removedDocMin);
        }
        //Add all the docs back into the original heap
        if(addedToTempHeap){
            while(tempHeap.peek() != null) {
                this.docMinHeap.insert(tempHeap.remove());
            }
        }
    }

    /// Private method to add a document to memory
    private void addToMemory(URI url, Document doc){
        //Add it to the BTree, possibly overwriting an old doc
        this.documents.put(url, doc);
        //Update its last used time
        doc.setLastUseTime(System.nanoTime());
        //Add it to the docs to memory HashMap
        this.docsInMemory.add(url);
        //Increment the doc count and the byte count
        this.numDocs++;
        this.numBytes += this.getDocBytes(doc);
        //Add it to the minHeap (as a DocMin) which tracks memory
        this.docMinHeap.insert(new DocMin(url));
        //Add its uri to the trie if it's a text document
        if(doc.getDocumentTxt() != null){
            this.addURIToTrie(doc);
        }
        //No matter what kind of doc it is, add its metadata to the meta-Tries
        this.addURItoMetaTries(url, doc);
    }

    //Private method to move the document from EVERYWHERE in memory and put it onto disk
    //DON'T Assume it was removed from the heap and then passed as an argument
    private void moveAllTraces(Document doc){
        //Take it out of the minHeap
        this.docMinHeap.remove();
        URI docUrl = doc.getKey();
        //Erase it from docsInMemory set
        this.docsInMemory.remove(docUrl);
        //Lower the doc count and byte count
        this.numDocs--;
        this.numBytes -= this.getDocBytes(doc);
        //Have the BTree move it to disk
        try{
            this.documents.moveToDisk(docUrl);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    //Method to delete all traces of a doc's URI in the trie
    private void deleteAllTracesInTrie(Document doc){
        //Go through all the words in each doc and delete references to the doc's URI from the trie
        URI docURI = doc.getKey();
        Set<String> wordsInDoc = doc.getWords();
        for(String word : wordsInDoc){
            this.documentTrie.delete(word, docURI);
        }
    }

    //Method to delete doc's uri from meta-Tries
    private void deleteFromMetaTries(URI url, Document doc){
        //Delete URI from meta tries
        HashMap<String, String> mapOfMetadata = doc.getMetadata();
        for(Map.Entry<String, String> entry : mapOfMetadata.entrySet()){
            this.metaKeysTrie.delete(entry.getKey(), url);
            //If there is a value stored there, then delete the uri from the values trie
            if(entry.getValue() != null) this.metaValuesTrie.delete(entry.getValue(), url);
        }
    }

    private String convertInputToString(InputStream input) throws IOException{
        String result = "";
        int bytesRead;
        byte[] buffer = new byte[1024];
        try{
            while((bytesRead = input.read(buffer)) != -1){
                result += new String(buffer, 0, bytesRead);
            }
            return result;
        }catch (IOException e){
            throw new IOException("Issue in convertInputToString");
        }finally {
            input.close();
        }
    }

    private byte[] convertInputToByteArray(InputStream input) throws IOException{
        byte[] result = new byte[0];
        int bytesRead;
        byte[] buffer = new byte[1024];
        try{
            while((bytesRead = input.read(buffer)) != -1) {
                //Increase the size of my byte[] corresponding to how many bytes I just read
                byte[] larger = new byte[result.length + bytesRead];
                for (int i = 0; i < result.length; i++) {
                    larger[i] = result[i];
                }
                //Copy all the new bytes into the larger array
                for (int i = result.length; i < larger.length; i++) {
                    larger[i] = buffer[i];
                }
                result = larger;
            }
            return result;
        }catch (IOException e){
            throw new IOException("Issue in convertInputToByteArray");
        }finally {
            input.close();
        }
    }

    ///Adds the URI of the doc passed as an argument to the trie for each word in the doc
    private void addURIToTrie(Document doc){
        Set<String> setOfDistinctWords = doc.getWords();
        for(String word : setOfDistinctWords){
            this.documentTrie.put(word, doc.getKey());
        }
    }

    /// Adds the URI of the doc passed in to the meta-Tries
    private void addURItoMetaTries(URI url, Document doc){
        //Also add all of its meta keys and values to their respective tries
        HashMap<String, String> mapOfMetadata = doc.getMetadata();
        for(String metaKey : mapOfMetadata.keySet()){
            this.metaKeysTrie.put(metaKey, url);
            //Only add to the values trie if the value is not null
            if(mapOfMetadata.get(metaKey) != null) this.metaValuesTrie.put(mapOfMetadata.get(metaKey), url);
        }
    }

    /**
     * @param url the unique identifier of the document to get
     * @return the given document
     */
    @Override
    public Document get(URI url) throws IOException{
        //NEW GET METHOD
        //First get it from the BTree
        Document doc = this.documents.get(url);
        //If it was in memory, then update its time
        if(this.docsInMemory.contains(url)){
            doc.setLastUseTime(System.nanoTime());
            this.docMinHeap.reHeapify(new DocMin(url));
        }
        //Otherwise it wasn't in memory, so either BTree.get fetched it from disk, which deleted it from disk and updated its time in deserialize,
        //Or it wasn't on disk, so BTree.get returned null
        else if(doc != null){
            //check if other docs need to be booted from memory
            while(this.numBytes + this.getDocBytes(doc) > this.maxDocumentBytes || this.numDocs + 1 > this.maxDocumentCount){
                DocMin movedDocMin = this.docMinHeap.peek();
                Document movedDoc = this.documents.get(movedDocMin.uri);
                this.moveAllTraces(movedDoc);
            }
            //Add the doc to memory
            this.addToMemory(url, doc);
        }
        //Return the doc
        return doc;
    }

    /**
     * @param url the unique identifier of the document to delete
     * @return true if the document is deleted, false if no document exists with that URI
     */
    @Override
    public boolean delete(URI url){
        Document removedDoc = this.documents.put(url, null);
        if(removedDoc == null){
            return false;
        }
        //This call actually deleted a document
        else{
            GenericCommand<URI> deleteGenericCommand = new GenericCommand<>(url, input -> {
                while(this.numBytes + this.getDocBytes(removedDoc) > this.maxDocumentBytes || this.numDocs + 1 > this.maxDocumentCount){
                    DocMin movedDocMin = this.docMinHeap.peek();
                    Document movedDoc = this.documents.get(movedDocMin.uri);
                    this.moveAllTraces(movedDoc);
                }
                this.addToMemory(url, removedDoc);
            });
            this.commandStack.push(deleteGenericCommand);
            //If this url was in memory, completely remove it from memory
            if(this.docsInMemory.contains(url)) this.removeFromMemory(url, removedDoc);
            //At least remove it from trie if it was a text document
            else if(removedDoc.getDocumentTxt() != null) this.deleteAllTracesInTrie(removedDoc);
            //Regardless, remove it from the meta_Tries
            this.deleteFromMetaTries(url, removedDoc);
            return true;
        }
    }

    //**********STAGE 3 ADDITIONS

    /**
     * undo the last put or delete command
     * @throws IllegalStateException if there are no actions to be undone, i.e. the command stack is empty
     */
    @Override
    public void undo() throws IllegalStateException{
        if(this.commandStack.size() == 0){
            throw new IllegalStateException("Command stack is empty");
        }
        //If the last thing on the stack is a singular GenericCommand, undo that command and pop it off the stack
        if(this.commandStack.peek() instanceof GenericCommand){
            GenericCommand<URI> genCommand = (GenericCommand<URI>) this.commandStack.pop();
            genCommand.undo();
        }
        //Otherwise the last thing on the stack is a CommandSet
        else{
            CommandSet<URI> commandSet = (CommandSet<URI>) this.commandStack.pop();
            commandSet.undo();
        }
    }

    /**
     * undo the last put or delete that was done with the given URI as its key
     * @param url
     * @throws IllegalStateException if there are no actions on the command stack for the given URI
     */
    @Override
    public void undo(URI url) throws IllegalStateException{
        if(this.commandStack.size() == 0) throw new IllegalStateException("Command stack is empty");
        //StackImpl<Undoable> tempStack = new StackImpl<>();
        boolean successfulUndo = false;
        //Pop all the Commands off of the command stack and store them in a temporary stack
        while(this.commandStack.size() != 0){
            Undoable tempCommand = this.commandStack.pop();
            //If a command matches the url, call undo on that command
            //Must differentiate if the last pop was a GenCommand or a CommandSet
            if(tempCommand instanceof GenericCommand){
                GenericCommand<URI> tempGenCommand = new GenericCommand<>(url, null);
                //If they have the same "target" (url) then we have a hit
                if(tempGenCommand.equals(tempCommand)){
                    GenericCommand<URI> genCommand = (GenericCommand<URI>) tempCommand;
                    successfulUndo = genCommand.undo();
                    break;
                }
            }
            //Otherwise the last popped item must have been a command stack
            else{
                this.currentCommandSet = (CommandSet<URI>) tempCommand;
                if(this.currentCommandSet.containsTarget(url)){
                    successfulUndo = this.currentCommandSet.undo(url);
                    //If there are still commands in the command set push it back onto the stack
                    if(!this.currentCommandSet.isEmpty()) this.tempStack.push(this.currentCommandSet);
                    break;
                }
            }
            this.tempStack.push(tempCommand);
        }
        //Pop all the elements off of the temporary stack and push them onto the command stack
        while(this.tempStack.size() != 0){
            Undoable revert = this.tempStack.pop();
            this.commandStack.push(revert);
        }
        //If the url wasn't in the stack of commands, throw an exception
        if(!successfulUndo) throw new IllegalStateException("URI wasn't in the stack");
    }

    //**********STAGE 4 ADDITIONS

    /**
     * Retrieve all documents whose text contains the given keyword.
     * Documents are returned in sorted, descending order, sorted by the number of times the keyword appears in the document.
     * Search is CASE SENSITIVE.
     * @param keyword
     * @return a List of the matches. If there are no matches, return an empty list.
     */
    @Override
    public List<Document> search(String keyword) throws IOException{
        //Get the sorted list of uris to be converted to docs to be returned
        //Being that we call this.get(uri) for each one in the custom sorter, they have all been brought back into memory
        List<URI> listOfSearchedURIs = this.documentTrie.getSorted(keyword, (u1, u2) -> {
            try {
                return Integer.compare(this.get(u2).wordCount(keyword), this.get(u1).wordCount(keyword));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        List<Document> listOfSearchedDocs = new ArrayList<>();
        //Loop through the uris, and add the document they represent into the list of docs
        for(URI url : listOfSearchedURIs){
            listOfSearchedDocs.add(this.get(url));
        }
        return listOfSearchedDocs;
    }

    /**
     * Retrieve all documents containing a word that starts with the given prefix
     * Documents are returned in sorted, descending order, sorted by the number of times the prefix appears in the document.
     * Search is CASE SENSITIVE.
     * @param keywordPrefix
     * @return a List of the matches. If there are no matches, return an empty list.
     */
    @Override
    public List<Document> searchByPrefix(String keywordPrefix) throws IOException{
        //Get the sorted list of uris to be converted to docs to be returned
        List<URI> listOfPrefixURIs = this.documentTrie.getAllWithPrefixSorted(keywordPrefix, (u1, u2) -> {
            try {
                return Integer.compare(this.prefixWordCount(keywordPrefix, this.get(u2)), this.prefixWordCount(keywordPrefix, this.get(u1)));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        List<Document> listOfPrefixDocs = new ArrayList<>();
        //Loop through the uris, and add the document they represent into the list of docs
        for(URI url : listOfPrefixURIs){
            listOfPrefixDocs.add(this.get(url));
        }
        return listOfPrefixDocs;
    }

    //Method to get a count of how many times a prefix appears in a doc
    private int prefixWordCount(String prefix, Document doc){
        int count = 0;
        Set<String> wordsInDoc = doc.getWords();
        for(String word : wordsInDoc){
            if(word.startsWith(prefix)) count += doc.wordCount(word);
        }
        return count;
    }

    /**
     * Completely remove any trace of any document which contains the given keyword
     * Search is CASE SENSITIVE.
     * @param keyword
     * @return a Set of URIs of the documents that were deleted.
     */
    @Override
    public Set<URI> deleteAll(String keyword){
        Set<URI> deletedDocsURIs = this.documentTrie.deleteAll(keyword);
        CommandSet<URI> deleteAllCommandSet = new CommandSet<>();
        //Loop through the set, retrieving the doc corresponding to the URI and deleting it from everywhere
        for(URI url : deletedDocsURIs){
            Document deletedDoc = this.documents.get(url);
            //Call method to create a generic command to undo this delete
            deleteAllCommandSet.addCommand(this.deleteAllGenCommand(deletedDoc));
            //Remove the doc from memory, if it was in memory
            if(this.docsInMemory.contains(url)) this.removeFromMemory(url, deletedDoc);
            //If not in memory, at least remove it from the trie
            else this.deleteAllTracesInTrie(deletedDoc);
            //Regardless, delete it from meta-Tries
            this.deleteFromMetaTries(url, deletedDoc);
            //Delete it from the btree (If it was initially on disk, then its value was already null in BTree, and documents.get deleted it from disk)
            this.documents.put(url, null);
        }
        this.commandStack.push(deleteAllCommandSet);
        return deletedDocsURIs;
    }

    /// Private method for undoing bulk deletes on docs byb keywords/metadata
    private GenericCommand<URI> deleteAllGenCommand(Document doc){
        URI url = doc.getKey();
        GenericCommand<URI> genCommand = new GenericCommand<>(url, input -> {
            //Clear space to add this docs back to memory
            while(this.numBytes + this.getDocBytes(doc) > this.maxDocumentBytes || this.numDocs + 1 > this.maxDocumentCount){
                DocMin movedDocMin = this.docMinHeap.peek();
                Document movedDoc = this.documents.get(movedDocMin.uri);
                this.moveAllTraces(movedDoc);
            }
            this.addToMemory(url, doc);
        });
        return genCommand;
    }

    /**
     * Completely remove any trace of any document which contains a word that has the given prefix
     * Search is CASE SENSITIVE.
     * @param keywordPrefix
     * @return a Set of URIs of the documents that were deleted.
     */
    @Override
    public Set<URI> deleteAllWithPrefix(String keywordPrefix){
        Set<URI> deletedDocsPrefixURIs = this.documentTrie.deleteAllWithPrefix(keywordPrefix);
        CommandSet<URI> deleteAllWithPrefixCommandSet = new CommandSet<>();
        //Loop through the set, retrieving the doc corresponding to the URI and deleting it from everywhere
        for(URI url : deletedDocsPrefixURIs){
            Document deletedDocWithPrefix = this.documents.get(url);
            //Call method to create a generic command to undo this delete
            deleteAllWithPrefixCommandSet.addCommand(this.deleteAllGenCommand(deletedDocWithPrefix));
            //Remove the doc from memory, if it was in memory
            if(this.docsInMemory.contains(url)) this.removeFromMemory(url, deletedDocWithPrefix);
            //If not in memory, at least remove it from the trie
            else this.deleteAllTracesInTrie(deletedDocWithPrefix);
            //Regardless, delete it from meta_Tries
            this.deleteFromMetaTries(url, deletedDocWithPrefix);
            //Delete it from the btree
            this.documents.put(url, null);
        }
        this.commandStack.push(deleteAllWithPrefixCommandSet);
        return deletedDocsPrefixURIs;
    }

    /**
     * @param keysValues metadata key-value pairs to search for
     * @return a List of all documents whose metadata contains ALL OF the given values for the given keys. If no documents contain all the given key-value pairs, return an empty list.
     */
    @Override
    public List<Document> searchByMetadata(Map<String,String> keysValues) throws IOException{
        //Create a Set of URIs
        Set<URI> docsWithMetadata = new HashSet<>();
        List<Document> docsListWithMetadata = new ArrayList<>();
        //Initialize the set to contain all the URIs who had the first metaKey in the keysValues set
        for(String metaKey : keysValues.keySet()){
            docsWithMetadata.addAll(this.metaKeysTrie.get(metaKey));
            break;
        }
        //Loop through all the metadata key value pairs in the metaMap
        for(Map.Entry<String, String> entry : keysValues.entrySet()){
            //Only keep the URIs that appear in every search from both meta-Tries
            docsWithMetadata.retainAll(this.metaKeysTrie.get(entry.getKey()));
            docsWithMetadata.retainAll(this.metaValuesTrie.get(entry.getValue()));
        }
        for(URI url : docsWithMetadata){
            docsListWithMetadata.add(this.get(url));
        }
        return docsListWithMetadata;
    }

    //Helper method to get a Set of URIs who have the matching metadata
    private Set<URI> searchByMetadataHelper(Map<String,String> keysValues) throws IOException{
        //Create a Set of URIs
        Set<URI> urisWithMetadata = new HashSet<>();
        //Initialize the set to contain all the URIs who had the first metaKey in the keysValues set
        for(String metaKey : keysValues.keySet()){
            urisWithMetadata.addAll(this.metaKeysTrie.get(metaKey));
            break;
        }
        //Loop through all the metadata key value pairs in the metaMap
        for(Map.Entry<String, String> entry : keysValues.entrySet()){
            //Only keep the URIs that appear in every search from both meta-Tries
            urisWithMetadata.retainAll(this.metaKeysTrie.get(entry.getKey()));
            urisWithMetadata.retainAll(this.metaValuesTrie.get(entry.getValue()));
        }
        //Return the set
        return urisWithMetadata;
    }

    /**
     * Retrieve all documents whose text contains the given keyword AND which has the given key-value pairs in its metadata
     * Documents are returned in sorted, descending order, sorted by the number of times the keyword appears in the document.
     * Search is CASE SENSITIVE.
     * @param keyword
     * @param keysValues
     * @return a List of the matches. If there are no matches, return an empty list.
     */
    @Override
    public List<Document> searchByKeywordAndMetadata(String keyword, Map<String,String> keysValues) throws IOException{
        //Get a set of URIs of docs with the given keyword
        Set<URI> urisWithWord = this.documentTrie.get(keyword);
        //Get the uris that have the metadata without changing around anything in memory
        Set<URI> urisWithMetadata = this.searchByMetadataHelper(keysValues);
        //Get the intersection of the two sets
        urisWithWord.retainAll(urisWithMetadata);
        List<Document> listWithWordAndMetadata = new ArrayList<>();
        //Loop through the uris and add their documents to a List of docs to be returned
        for(URI url : urisWithWord){
            listWithWordAndMetadata.add(this.get(url));
        }
        Comparator<Document> comparator = (d1, d2) -> Integer.compare(d2.wordCount(keyword), d1.wordCount(keyword));
        listWithWordAndMetadata.sort(comparator);
        return listWithWordAndMetadata;
    }

    /**
     * Retrieve all documents that contain text which starts with the given prefix AND which has the given key-value pairs in its metadata
     * Documents are returned in sorted, descending order, sorted by the number of times the prefix appears in the document.
     * Search is CASE SENSITIVE.
     * @param keywordPrefix
     * @return a List of the matches. If there are no matches, return an empty list.
     */
    @Override
    public List<Document> searchByPrefixAndMetadata(String keywordPrefix, Map<String,String> keysValues) throws IOException{
        List<Document> docsWithPrefixAndMetadata = new ArrayList<>();
        //Get the sorted list of uris with prefix without changing memory
        List<URI> listOfPrefixURIs = this.documentTrie.getAllWithPrefixSorted(keywordPrefix, (u1, u2) -> {
            try{
                //First get the document from the btree
                Document d1 = this.documents.get(u1);
                Document d2 = this.documents.get(u2);
                //If the document was just brought from disk, make sure to put it back on disk
                if(!this.docsInMemory.contains(u1)) {
                    this.documents.put(u1, d1);
                    this.documents.moveToDisk(u1);
                }
                if(!this.docsInMemory.contains(u2)){
                    this.documents.put(u2, d2);
                    this.documents.moveToDisk(u2);
                }
                return Integer.compare(this.prefixWordCount(keywordPrefix, d2), this.prefixWordCount(keywordPrefix, d1));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        //Now get all the docs URIs who had the metadata
        Set<URI> urisWithMetadata = this.searchByMetadataHelper(keysValues);
        //Take the intersection of the two collections
        listOfPrefixURIs.retainAll(urisWithMetadata);
        //Loop through the uris and add their documents to a List of docs to be returned
        for(URI url : listOfPrefixURIs){
            docsWithPrefixAndMetadata.add(this.get(url));
        }
        return docsWithPrefixAndMetadata;
    }

    /**
     * Completely remove any trace of any document which has the given key-value pairs in its metadata
     * Search is CASE SENSITIVE.
     * @return a Set of URIs of the documents that were deleted.
     */
    @Override
    public Set<URI> deleteAllWithMetadata(Map<String,String> keysValues) throws IOException{
        Set<URI> urisWithMetadata = this.searchByMetadataHelper(keysValues);
        CommandSet<URI> deleteAllWithMetadataCommandSet = new CommandSet<>();
        //Loop through the set, retrieving the doc corresponding to the URI and deleting it from everywhere
        for(URI url : urisWithMetadata){
            Document deletedDoc = this.documents.get(url);
            //Call method to create a generic command to undo this delete
            deleteAllWithMetadataCommandSet.addCommand(this.deleteAllGenCommand(deletedDoc));
            //Remove the doc from memory, if it was in memory
            if(this.docsInMemory.contains(url)) this.removeFromMemory(url, deletedDoc);
            //If not in memory, at least remove it from the trie if it is a text doc
            else if(deletedDoc.getDocumentTxt() != null) this.deleteAllTracesInTrie(deletedDoc);
            //Regardless, delete it from meta-Tries
            this.deleteFromMetaTries(url, deletedDoc);
            //Delete it from the btree (If it was initially on disk, then its value was already null in BTree, and documents.get deleted it from disk)
            this.documents.put(url, null);
        }
        this.commandStack.push(deleteAllWithMetadataCommandSet);
        return urisWithMetadata;
    }

    //Helper method to get URIs with keyword and metadata without bringing them into memory
    private Set<URI> searchByKeywordAndMetadataHelper(String keyword, Map<String,String> keysValues) throws IOException{
        //Get a set of URIs of docs with the given keyword
        Set<URI> urisWithWord = this.documentTrie.get(keyword);
        Set<URI> urisWithMetadata = this.searchByMetadataHelper(keysValues);
        urisWithWord.retainAll(urisWithMetadata);
        return urisWithWord;
    }

    /**
     * Completely remove any trace of any document which contains the given keyword AND which has the given key-value pairs in its metadata
     * Search is CASE SENSITIVE.
     * @param keyword
     * @return a Set of URIs of the documents that were deleted.
     */
    @Override
    public Set<URI> deleteAllWithKeywordAndMetadata(String keyword, Map<String,String> keysValues) throws IOException{
        Set<URI> urisWithWordAndMetadata = new HashSet<>(this.searchByKeywordAndMetadataHelper(keyword, keysValues));
        CommandSet<URI> deleteAllWithKeywordAndMetadataCommandSet = new CommandSet<>();
        //Loop through the set, retrieving the doc corresponding to the URI and deleting it from everywhere
        for(URI url : urisWithWordAndMetadata){
            Document deletedDoc = this.documents.get(url);
            //Call method to create a generic command to undo this delete
            deleteAllWithKeywordAndMetadataCommandSet.addCommand(this.deleteAllGenCommand(deletedDoc));
            //Remove the doc from memory, if it was in memory
            if(this.docsInMemory.contains(url)) this.removeFromMemory(url, deletedDoc);
            //If not in memory, at least remove it from the trie
            else this.deleteAllTracesInTrie(deletedDoc);
            //Regardless, delete it from meta-Tries
            this.deleteFromMetaTries(url, deletedDoc);
            //Delete it from the btree (If it was initially on disk, then its value was already null in BTree, and documents.get deleted it from disk)
            this.documents.put(url, null);
        }
        this.commandStack.push(deleteAllWithKeywordAndMetadataCommandSet);
        return urisWithWordAndMetadata;
    }

    //Helper method to get the uris with prefix and metadata without affecting memory
    private Set<URI> searchByPrefixAndMetadataHelper(String keywordPrefix, Map<String,String> keysValues) throws IOException{
        //Get the sorted list of uris with prefix without changing memory
        List<URI> listOfPrefixURIs = this.documentTrie.getAllWithPrefixSorted(keywordPrefix, (u1, u2) -> {
            try{
                //First get the document from the btree
                Document d1 = this.documents.get(u1);
                Document d2 = this.documents.get(u2);
                //If the document was just brought from disk, make sure to put it back on disk
                if(!this.docsInMemory.contains(u1)) {
                    this.documents.put(u1, d1);
                    this.documents.moveToDisk(u1);
                }
                if(!this.docsInMemory.contains(u2)){
                    this.documents.put(u2, d2);
                    this.documents.moveToDisk(u2);
                }
                return Integer.compare(this.prefixWordCount(keywordPrefix, d2), this.prefixWordCount(keywordPrefix, d1));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        //Now get all the docs URIs who had the metadata
        Set<URI> urisWithMetadata = this.searchByMetadataHelper(keysValues);
        //Take the intersection of the two collections
        urisWithMetadata.retainAll(listOfPrefixURIs);
        //Return the uris that have both the prefix and the metadata
        return urisWithMetadata;
    }

    /**
     * Completely remove any trace of any document which contains a word that has the given prefix AND which has the given key-value pairs in its metadata
     * Search is CASE SENSITIVE.
     * @param keywordPrefix
     * @return a Set of URIs of the documents that were deleted.
     */
    @Override
    public Set<URI> deleteAllWithPrefixAndMetadata(String keywordPrefix,Map<String,String> keysValues) throws IOException{
        Set<URI> urisWithPrefixAndMetadata = new HashSet<>(this.searchByPrefixAndMetadataHelper(keywordPrefix, keysValues));
        CommandSet<URI> deleteAllWithPrefixAndMetadataCommandSet = new CommandSet<>();
        //Loop through the set, retrieving the doc corresponding to the URI and deleting it from everywhere
        for(URI url : urisWithPrefixAndMetadata){
            Document deletedDoc = this.documents.get(url);
            //Call method to create a generic command to undo this delete
            deleteAllWithPrefixAndMetadataCommandSet.addCommand(this.deleteAllGenCommand(deletedDoc));
            //Remove the doc from memory, if it was in memory
            if(this.docsInMemory.contains(url)) this.removeFromMemory(url, deletedDoc);
            //If not in memory, at least remove it from the trie
            else this.deleteAllTracesInTrie(deletedDoc);
            //Regardless, delete it from meta-Tries
            this.deleteFromMetaTries(url, deletedDoc);
            //Delete it from the btree (If it was initially on disk, then its value was already null in BTree, and documents.get deleted it from disk)
            this.documents.put(url, null);
        }
        this.commandStack.push(deleteAllWithPrefixAndMetadataCommandSet);
        return urisWithPrefixAndMetadata;
    }

    //**********STAGE 5 ADDITIONS

    /**
     * set maximum number of documents that may be stored
     * @param limit
     * @throws IllegalArgumentException if limit < 1
     */
    @Override
    public void setMaxDocumentCount(int limit){
        //Throw exception if limit is < 1
        if(limit < 1) throw new IllegalArgumentException("Limit must be > 1");
        this.maxDocumentCount = limit;
        //Move any docs that put us over the limit to disk
        while(this.numDocs > this.maxDocumentCount){
            DocMin movedDocMin = this.docMinHeap.peek();
            Document movedDoc = this.documents.get(movedDocMin.uri);
            this.moveAllTraces(movedDoc);
        }
    }

    /**
     * set maximum number of bytes of memory that may be used by all the documents in memory combined
     * @param limit
     * @throws IllegalArgumentException if limit < 1
     */
    @Override
    public void setMaxDocumentBytes(int limit){
        //Throw exception if limit is < 1
        if(limit < 1) throw new IllegalArgumentException("Limit must be > 1");
        this.maxDocumentBytes = limit;
        //Move any docs that put us over the limit to disk
        while(this.numBytes > this.maxDocumentBytes){
            DocMin movedDocMin = this.docMinHeap.peek();
            Document movedDoc = this.documents.get(movedDocMin.uri);
            this.moveAllTraces(movedDoc);
        }
    }

    //Private method to get the number of bytes in a document
    private int getDocBytes(Document doc){
        //If the document is null, return 0
        if(doc == null) return 0;
        if(doc.getDocumentTxt() != null){
            return doc.getDocumentTxt().getBytes().length;
        }
        else{
            return doc.getDocumentBinaryData().length;
        }
    }

}