package edu.yu.cs.com1320.project.stage6.impl;

import com.google.gson.*;
import edu.yu.cs.com1320.project.stage6.Document;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Type;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class DocumentImplTest {


    //---- NEW TESTS

    @Test
    void wordCount() throws URISyntaxException {
        String docTxt = "Test this is a Test, is is test ";
        URI uri = new URI("https:tester.com");
        DocumentImpl mydoc = new DocumentImpl(uri, docTxt, null);
        assertEquals(2, mydoc.wordCount("Test"));
        assertEquals(1, mydoc.wordCount("this"));
        assertEquals(3, mydoc.wordCount("is"));
        assertEquals(1, mydoc.wordCount("a"));
        assertEquals(1, mydoc.wordCount("test"));
        assertEquals(0, mydoc.wordCount("Word"));
    }

    @Test
    void wordCountMapPassedIn() throws URISyntaxException {
        String docTxt = "Test this is a Test, is is test ";
        URI uri = new URI("https:tester.com");
        Map<String, Integer> wordCountMap = new HashMap<>();
        wordCountMap.put("Word", 1);
        wordCountMap.put("Count", 2);
        wordCountMap.put("Map", 3);
        DocumentImpl mydoc = new DocumentImpl(uri, docTxt, wordCountMap);
        //Check if it took the word map passed in
        assertEquals(0, mydoc.wordCount("Test"));
        assertEquals(0, mydoc.wordCount("this"));
        assertEquals(1, mydoc.wordCount("Word"));
        assertEquals(2, mydoc.wordCount("Count"));
        assertEquals(3, mydoc.wordCount("Map"));
    }


    //---- OLD TESTS

    @Test
    void txtConstructorNullURITest() {
        assertThrows(IllegalArgumentException.class, () -> {new DocumentImpl(null, "Test", null);});
    }

    @Test
    void txtConstructorBlankURITest() throws URISyntaxException{
        URI uri = new URI("");
        assertThrows(IllegalArgumentException.class, () -> {new DocumentImpl(uri, "Test", null);});
    }

    @Test
    void txtConstructorNullTextTest() throws URISyntaxException{
        URI uri = new URI("https:tester.com");
        String nullString = null;
        assertThrows(IllegalArgumentException.class, () -> {new DocumentImpl(uri, nullString, null);});
    }

    @Test
    void txtConstructorBlankTextTest() throws URISyntaxException{
        URI uri = new URI("https:tester.com");
        assertThrows(IllegalArgumentException.class, () -> {new DocumentImpl(uri, "  ", null);});
    }

    @Test
    void binaryConstructorNullURITest() {
        byte[] bytes = {1, 2, 3};
        assertThrows(IllegalArgumentException.class, () -> {new DocumentImpl(null, bytes);});
    }

    @Test
    void binaryConstructorBlankURITest() throws URISyntaxException{
        URI uri = new URI("");
        byte[] bytes = {1, 2, 3};
        assertThrows(IllegalArgumentException.class, () -> {new DocumentImpl(uri, bytes);});
    }

    @Test
    void binaryConstructorNullByteTest() throws URISyntaxException{
        URI uri = new URI("https:tester.com");
        byte[] nullBytes = null;
        assertThrows(IllegalArgumentException.class, () -> {new DocumentImpl(uri, nullBytes);});
    }

    @Test
    void binaryConstructorBlankTextTest() throws URISyntaxException{
        URI uri = new URI("https:tester.com");
        byte[] emptyBytes = {};
        assertThrows(IllegalArgumentException.class, () -> {new DocumentImpl(uri, emptyBytes);});
    }

    @Test
    void setMetadataValueNullReturnTest() throws URISyntaxException{
        URI uri = new URI("https:tester.com");
        String docText = "Testing the getMetadata() method";
        DocumentImpl document = new DocumentImpl(uri, docText, null);
        String result = document.setMetadataValue("key", "value");
        assertNull(result);
    }

    @Test
    void setMetadataValueOldReturnTest() throws URISyntaxException{
        URI uri = new URI("https:tester.com");
        String docText = "Testing the getMetadata() method";
        DocumentImpl document = new DocumentImpl(uri, docText, null);
        document.setMetadataValue("key", "value");
        String result = document.setMetadataValue("key", "new value");
        assertEquals("value", result);
    }

    @Test
    void setMetadataValueNullKeyTest() throws URISyntaxException{
        URI uri = new URI("https:tester.com");
        DocumentImpl document = new DocumentImpl(uri, "Null key test", null);
        document.setMetadataValue("key", "value");
        assertThrows(IllegalArgumentException.class, () -> {document.setMetadataValue(null, "value");});
    }

    @Test
    void setMetadataValueBlankKeyTest() throws URISyntaxException{
        URI uri = new URI("https:tester.com");
        DocumentImpl document = new DocumentImpl(uri, "Null key test",  null);
        document.setMetadataValue("key", "value");
        assertThrows(IllegalArgumentException.class, () -> {document.setMetadataValue("   ", "value");});
    }

    @Test
    void getMetadataValueNullKeyTest() throws URISyntaxException{
        URI uri = new URI("https:tester.com");
        DocumentImpl document = new DocumentImpl(uri, "Null key test", null);
        document.setMetadataValue("key", "value");
        assertThrows(IllegalArgumentException.class, () -> {document.getMetadataValue(null);});
    }

    @Test
    void getMetadataValueBlankKeyTest() throws URISyntaxException{
        URI uri = new URI("https:tester.com");
        DocumentImpl document = new DocumentImpl(uri, "Null key test", null);
        document.setMetadataValue("key", "value");
        assertThrows(IllegalArgumentException.class, () -> {document.getMetadataValue("  ");});
    }

    @Test
    void getMetadataValueNullReturnTest() throws URISyntaxException{
        URI uri = new URI("https:tester.com");
        DocumentImpl document = new DocumentImpl(uri, "Null key test", null);
        assertNull(document.getMetadataValue("key"));
    }

    @Test
    void getMetadataValueNormalReturnTest() throws URISyntaxException{
        URI uri = new URI("https:tester.com");
        DocumentImpl document = new DocumentImpl(uri, "Null key test", null);
        document.setMetadataValue("key", "value");
        assertEquals("value", document.getMetadataValue("key"));
    }

    @Test
    void getMetadataTest() throws URISyntaxException{
        URI uri = new URI("https:tester.com");
        String docText = "Testing the getMetadata() method";
        DocumentImpl document = new DocumentImpl(uri, docText, null);
        assertNotNull(document);
        document.setMetadataValue("key", "value");
        assertEquals("value", document.metadata.get("key"));
        assertEquals("value", document.getMetadata().get("key"));
    }

    @Test
    void getDocumentTxtTest() throws URISyntaxException{
        URI uri = new URI("https:tester.com");
        String docText = "Testing the getDocumentTxt() method";
        DocumentImpl document = new DocumentImpl(uri, docText, null);
        assertNotNull(document);
        assertEquals(document.documentTxt, document.getDocumentTxt());
    }

    @Test
    void getDocumentBinaryDataTest() throws URISyntaxException{
        URI uri = new URI("https:tester.com");
        byte[] docBinary = {1, 2, 3, 4};
        DocumentImpl document = new DocumentImpl(uri, docBinary);
        assertNotNull(document);
        assertEquals(document.documentBinaryData, document.getDocumentBinaryData());
    }

    @Test
    void getKeyTest() throws URISyntaxException{
        URI uri = new URI("https:tester.com");
        DocumentImpl document = new DocumentImpl(uri, "Testing the getKey() method", null);
        assertNotNull(document);
        assertEquals(document.uri, document.getKey());
    }

    @Test
    void testEquals() throws URISyntaxException{
        URI uriTxt = new URI("https:testerTxt.com");
        String docText = "Testing the Equals() method";
        DocumentImpl documentTxt = new DocumentImpl(uriTxt, docText, null);
        URI uri = new URI("https:tester.com");
        byte[] docBinary = {1, 2, 3, 4};
        DocumentImpl document = new DocumentImpl(uri, docBinary);
        assertNotNull(document);
        assertTrue(document.equals(document));
        assertFalse(document.equals(documentTxt));
        //System.out.println(documentTxt.hashCode());
    }

    @Test
    void testWordCount() throws URISyntaxException{
        URI uri = new URI("https:tester.com");
        String docText = "How much wood could a wood chuck chuck if a wood chuck could chuck wood";
        DocumentImpl document = new DocumentImpl(uri, docText, null);
        assertEquals(1, document.wordCount("How"));
        assertEquals(0, document.wordCount("how"));
        assertEquals(4, document.wordCount("wood"));
        assertEquals(4, document.wordCount("chuck"));
        URI uri2 = new URI("https:tester.com");
        byte[] docBinary = {1, 2, 3, 4};
        DocumentImpl documentBinary = new DocumentImpl(uri2, docBinary);
        assertEquals(0, documentBinary.wordCount("chuck"));
    }

    @Test
    void testgetWords() throws URISyntaxException{
        URI uri = new URI("https:tester.com");
        String docText = "Isn't this gr8t,I am splitting onl_y by spaces t./:'*h^%$e!@.,";
        DocumentImpl document = new DocumentImpl(uri, docText, null);
        assertEquals(9, document.getWords().size());
        Set<String> mySet = document.getWords();
        for(String string : mySet){
            System.out.println(string);
        }
        URI uri2 = new URI("https:tester.com");
        byte[] docBinary = {1, 2, 3, 4};
        DocumentImpl documentBinary = new DocumentImpl(uri2, docBinary);
        assertEquals(0, documentBinary.getWords().size());
    }

    @Test
    void testMapWords() throws URISyntaxException{
        URI uri = new URI("https:tester.com");
        String docText = "How much wood could a wood chuck chuck if a wood chuck could chuck wood";
        DocumentImpl document = new DocumentImpl(uri, docText, null);
        assertEquals(4, document.wordMap.get("wood"));
        assertEquals(null, document.wordMap.get("how"));
        assertEquals(4, document.wordMap.get("chuck"));
        assertEquals(2, document.wordMap.get("a"));
        assertEquals(1, document.wordMap.get("if"));
    }

    @Test
    void testLastTimeUsed() throws URISyntaxException{
        URI uri = new URI("https:tester.com");
        String docText = "Testing the setter and getter for last time used";
        DocumentImpl document = new DocumentImpl(uri, docText, null);
        assertEquals(0, document.getLastUseTime());
        //Set its time
        long time = System.nanoTime();
        //System.out.println("Time = " + time);
        document.setLastUseTime(time);
        assertEquals(time, document.getLastUseTime());
    }

}