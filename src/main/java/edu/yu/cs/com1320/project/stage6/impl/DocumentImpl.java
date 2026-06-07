package edu.yu.cs.com1320.project.stage6.impl;

import edu.yu.cs.com1320.project.stage6.Document;
import java.net.URI;
import java.util.*;

public class DocumentImpl implements Document{
    protected Map<String, String> metadata;
    protected String documentTxt;
    protected byte[] documentBinaryData;
    protected URI uri;
    protected Map<String, Integer> wordMap;
    protected long lastUsedTime;

    //Constructor for a document that is made of text
    public DocumentImpl(URI uri, String text, Map<String, Integer> wordCountMap){
        if(uri == null || text == null || uri.toString().isBlank() || text.isBlank()) {
            throw new IllegalArgumentException("Either uri or txt is null/blank");
        }
        this.uri = uri;
        this.documentTxt = text;
        this.metadata = new HashMap<String, String>();
        if(wordCountMap != null){
            this.wordMap = wordCountMap;
        }
        else{
            //Make the wordCount map
            this.wordMap = new HashMap<>();
            String docTxtWithoutPunctuation = this.documentTxt.replaceAll("[^a-zA-Z0-9\\s]", "");
            String[] arrayOfTxt = docTxtWithoutPunctuation.split("\\s+");
            for(String word : arrayOfTxt){
                Integer countOfWord = this.wordMap.get(word);
                if(countOfWord == null) countOfWord = 0;
                this.wordMap.put(word, countOfWord + 1);
            }
        }
    }

    //Constructor for a document that is made of binaryData
    public DocumentImpl(URI uri, byte[] binaryData){
        if(uri == null || binaryData == null || uri.toString().isBlank() || binaryData.length == 0){
            throw new IllegalArgumentException("Either uri or binary is null/blank");
        }
        this.uri = uri;
        this.documentBinaryData = binaryData;
        this.metadata = new HashMap<>();
    }

    /**
     * @param key   key of document metadata to store a value for
     * @param value value to store
     * @return old value, or null if there was no old value
     * @throws IllegalArgumentException if the key is null or blank
     */
    @Override
    public String setMetadataValue(String key, String value){
        if(key == null || key.isBlank()){
            throw new IllegalArgumentException("Key is either null or blank");
        }
        return this.metadata.put(key, value);
    }

    /**
     * @param key metadata key whose value we want to retrieve
     * @return corresponding value, or null if there is no such key
     * @throws IllegalArgumentException if the key is null or blank
     */
    @Override
    public String getMetadataValue(String key){
        if(key == null || key.isBlank()){
            throw new IllegalArgumentException("Key is either null or blank");
        }
        return this.metadata.get(key);
    }

    /**
     * @return a COPY of the metadata saved in this document
     */
    @Override
    public HashMap<String, String> getMetadata(){
        HashMap<String, String> metaDataCopy = new HashMap<>(this.metadata);
        return metaDataCopy;
    }

    @Override
    public void setMetadata(HashMap<String, String> metadata){
        this.metadata = metadata;
    }

    /**
     * @return content of text document
     */
    @Override
    public String getDocumentTxt(){
        return this.documentTxt;
    }

    /**
     * @return content of binary data document
     */
    public byte[] getDocumentBinaryData(){
        return this.documentBinaryData;
    }

    /**
     * @return URI which uniquely identifies this document
     */
    @Override
    public URI getKey(){
        return this.uri;
    }

    @Override
    public int hashCode() {
        int result = uri.hashCode();
        result = 31 * result + (this.documentTxt != null ? this.documentTxt.hashCode() : 0);
        result = 31 * result + Arrays.hashCode(this.documentBinaryData);
        return Math.abs(result);
    }

    @Override
    public boolean equals(Object obj){
        if(!(obj instanceof Document)){
            return false;
        }
        else if(this.hashCode() == obj.hashCode()){
            return true;
        }
        else{
            return false;
        }
    }

    //***************STAGE 4 ADDITIONS

    /**
     * how many times does the given word appear in the document?
     *
     * @param word
     * @return the number of times the given words appears in the document. If it's a binary document, return 0.
     */
    @Override
    public int wordCount(String word){
        if(this.getDocumentBinaryData() != null) return 0;
        if(this.wordMap.get(word) == null){
            return 0;
        }
        else{
            return this.wordMap.get(word);
        }
    }

    /**
     * @return all the words that appear in the document
     */
    @Override
    public Set<String> getWords(){
        Set<String> setofWords = new HashSet<>();
        if(this.getDocumentBinaryData() != null) return setofWords;
        String docTxtWithoutPunctuation = this.documentTxt.replaceAll("[^a-zA-Z0-9\\s]", "");
        String[] arrayOfTxt = docTxtWithoutPunctuation.split("\\s+");
        for(String word : arrayOfTxt){
            setofWords.add(word);
        }
        return setofWords;
    }

    /**
     * return the last time this document was used, via put/get or via a search result
     * (for stage 4 of project)
     */
    @Override
    public long getLastUseTime(){
        return this.lastUsedTime;
    }

    @Override
    public void setLastUseTime(long timeInNanoseconds){
        this.lastUsedTime = timeInNanoseconds;
    }

    /**
     * @return a copy of the word to count map so it can be serialized
     */
    @Override
    public HashMap<String, Integer> getWordMap(){
        if(this.getDocumentBinaryData() != null) return null;
        HashMap<String, Integer> wordMapCopy = new HashMap<>(this.wordMap);
        return wordMapCopy;
    }

    /**
     * This must set the word to count map during deserialization
     *
     * @param wordMap
     */
    @Override
    public void setWordMap(HashMap<String, Integer> wordMap){
        this.wordMap = wordMap;
    }

    //Compare the documents by their last used time
    @Override
    public int compareTo(Document o) {
        if(this.getLastUseTime() > o.getLastUseTime()){
            return 1;
        }
        else if(this.getLastUseTime() < o.getLastUseTime()){
            return -1;
        }
        else{
            return 0;
        }
    }


}
