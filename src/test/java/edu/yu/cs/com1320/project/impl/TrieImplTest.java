package edu.yu.cs.com1320.project.impl;

import edu.yu.cs.com1320.project.stage6.Document;
import edu.yu.cs.com1320.project.stage6.impl.DocumentImpl;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;

class TrieImplTest {

    @Test
    void allPurposeTester() throws URISyntaxException {
        TrieImpl<Document> myDocTrie = new TrieImpl<>();
        URI uri1 = new URI("https:tester1.com");
        String docText1 = "Testing the TrieImpl put method doc1 triers";
        DocumentImpl document1 = new DocumentImpl(uri1, docText1, null);
        String[] strings1 = docText1.split(" ");
        for(String string : strings1){
            myDocTrie.put(string, document1);
        }
        //Test the get method
        URI uri2 = new URI("https:tester2.com");
        String docText2 = "Testing the TrieImpl put method with another doc tried";
        DocumentImpl document2 = new DocumentImpl(uri2, docText2, null);
        String[] strings2 = docText2.split(" ");
        for(String string : strings2){
            myDocTrie.put(string, document2);
        }
        HashSet<Document> setOfDocs = (HashSet<Document>) myDocTrie.get("the");
        for(Document doc : setOfDocs){
            if(doc == document1) System.out.println("Hooray! - doc1 has 'the'");
            if(doc == document2) System.out.println("Double Hooray! - doc2 has 'the'");
        }
        //Test the delete method
        assertEquals(document2, myDocTrie.delete("the", document2));
        HashSet<Document> setOfDocs2 = (HashSet<Document>) myDocTrie.get("the");
        for(Document doc : setOfDocs2){
            if(doc == document1) System.out.println("Hooray! - doc1 still has 'the'");
            if(doc == document2) System.out.println("Double Hooray! - doc two removed from the :(");
        }
        HashSet<Document> setOfDocs3 = (HashSet<Document>) myDocTrie.get("put");
        for(Document doc : setOfDocs3){
            if(doc == document1) System.out.println("Hooray! - doc1 also has 'put'");
            if(doc == document2) System.out.println("Double Hooray! - at least doc2 still has 'put'");
        }
        //Test deleteAll method
        HashSet<Document> allDeletedDocs = (HashSet<Document>) myDocTrie.deleteAll("TrieImpl");
        assertEquals(2, allDeletedDocs.size());
        HashSet<Document> emptySetOfDocs = (HashSet<Document>) myDocTrie.get("TrieImpl");
        assertEquals(0, emptySetOfDocs.size());
        //Test deleteAllWithPrefix
        assertEquals(1, myDocTrie.get("tried").size());
        HashSet<Document> allDeletedDocWithPrefix = (HashSet<Document>) myDocTrie.deleteAllWithPrefix("trie");
        assertEquals(2, allDeletedDocWithPrefix.size());
        assertEquals(0, myDocTrie.get("tried").size());
    }


}