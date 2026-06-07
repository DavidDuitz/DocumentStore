package edu.yu.cs.com1320.project.stage6.impl;

import edu.yu.cs.com1320.project.stage6.Document;
import org.junit.jupiter.api.Test;

import edu.yu.cs.com1320.project.stage6.DocumentStore;

import java.net.*;
import java.io.*;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class DocumentStoreImplTest {

    //-----OLD TESTS, NOT INCLUDING STAGE 5 (memory)

    @Test
    void setMetadataNullURITest() {
        DocumentStoreImpl docStore = new DocumentStoreImpl();
        assertThrows(IllegalArgumentException.class, () -> {docStore.setMetadata(null, "key", "value");});
    }

    @Test
    void setMetadataBlankURITest() throws URISyntaxException {
        DocumentStoreImpl docStore = new DocumentStoreImpl();
        URI uri = new URI("");
        assertThrows(IllegalArgumentException.class, () -> {docStore.setMetadata(uri, "key", "value");});
    }

    @Test
    void setMetadataMissingDocTest() throws URISyntaxException {
        DocumentStoreImpl docStore = new DocumentStoreImpl();
        URI uri = new URI("https:tester.com");
        assertThrows(IllegalArgumentException.class, () -> {docStore.setMetadata(uri, "key", "value");});
    }

    @Test
    void setMetadataNullKeyTest() throws URISyntaxException, IOException {
        DocumentStoreImpl docStore = new DocumentStoreImpl();
        URI uri = new URI("https:tester.com");
        DocumentImpl document = new DocumentImpl(uri, "Text", null);
        byte[] myData1 = "Text".getBytes();
        InputStream input1 = new ByteArrayInputStream(myData1);
        docStore.put(input1, uri, DocumentStore.DocumentFormat.TXT);
        assertThrows(IllegalArgumentException.class, () -> {docStore.setMetadata(uri, null, "value");});
    }

    @Test
    void setMetadataBlankKeyTest() throws URISyntaxException, IOException {
        DocumentStoreImpl docStore = new DocumentStoreImpl();
        URI uri = new URI("https:tester.com");
        DocumentImpl document = new DocumentImpl(uri, "Text", null);
        byte[] myData1 = "Text".getBytes();
        InputStream input1 = new ByteArrayInputStream(myData1);
        docStore.put(input1, uri, DocumentStore.DocumentFormat.TXT);
        assertThrows(IllegalArgumentException.class, () -> {docStore.setMetadata(uri, "  ", "value");});
    }

    @Test
    void setMetadataNullReturnTest() throws URISyntaxException, IOException {
        DocumentStoreImpl docStore = new DocumentStoreImpl();
        URI uri = new URI("https:tester.com");
        DocumentImpl document = new DocumentImpl(uri, "Text", null);
        byte[] myData1 = "Text".getBytes();
        InputStream input1 = new ByteArrayInputStream(myData1);
        docStore.put(input1, uri, DocumentStore.DocumentFormat.TXT);
        assertNull(docStore.setMetadata(uri, "key", "value"));
    }

    @Test
    void setMetadataNormalReturnTest() throws URISyntaxException, IOException {
        DocumentStoreImpl docStore = new DocumentStoreImpl();
        URI uri = new URI("https:tester.com");
        DocumentImpl document = new DocumentImpl(uri, "Text", null);
        byte[] myData1 = "Text".getBytes();
        InputStream input1 = new ByteArrayInputStream(myData1);
        docStore.put(input1, uri, DocumentStore.DocumentFormat.TXT);
        docStore.setMetadata(uri, "key", "oldValue");
        assertEquals("oldValue", docStore.setMetadata(uri, "key", "newValue"));
    }

    @Test
    void getMetadataNullOrBlankUriTest() throws URISyntaxException{
        DocumentStoreImpl docStore = new DocumentStoreImpl();
        URI uri = new URI("");
        assertThrows(IllegalArgumentException.class, () -> {docStore.getMetadata(null, "key");});
        assertThrows(IllegalArgumentException.class, () -> {docStore.getMetadata(uri, "key");});
    }

    @Test
    void getMetadataMissingDocTest() throws URISyntaxException{
        DocumentStoreImpl docStore = new DocumentStoreImpl();
        URI uri = new URI("https:tester.com");
        assertThrows(IllegalArgumentException.class, () -> {docStore.getMetadata(uri, "key");});
    }

    @Test
    void getMetadataNullOrBlankKeyTest() throws URISyntaxException, IOException {
        DocumentStoreImpl docStore = new DocumentStoreImpl();
        URI uri = new URI("https:tester.com");
        DocumentImpl document = new DocumentImpl(uri, "Text", null);
        byte[] myData1 = "Text".getBytes();
        InputStream input1 = new ByteArrayInputStream(myData1);
        docStore.put(input1, uri, DocumentStore.DocumentFormat.TXT);
        assertThrows(IllegalArgumentException.class, () -> {
            docStore.getMetadata(uri, null);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            docStore.getMetadata(uri, "  ");
        });
    }

    @Test
    void getMetadataNullReturnTest() throws URISyntaxException, IOException {
        DocumentStoreImpl docStore = new DocumentStoreImpl();
        URI uri = new URI("https:tester.com");
        DocumentImpl document = new DocumentImpl(uri, "Text", null);
        byte[] myData1 = "Text".getBytes();
        InputStream input1 = new ByteArrayInputStream(myData1);
        docStore.put(input1, uri, DocumentStore.DocumentFormat.TXT);
        assertNull(docStore.getMetadata(uri, "key"));
    }

    @Test
    void getMetadataNormalReturnTest() throws URISyntaxException, IOException {
        DocumentStoreImpl docStore = new DocumentStoreImpl();
        URI uri = new URI("https:tester.com");
        DocumentImpl document = new DocumentImpl(uri, "Text", null);
        byte[] myData1 = "Text".getBytes();
        InputStream input1 = new ByteArrayInputStream(myData1);
        docStore.put(input1, uri, DocumentStore.DocumentFormat.TXT);
        docStore.setMetadata(uri, "key", "oldValue");
        assertEquals("oldValue", docStore.getMetadata(uri, "key"));
    }

    @Test
    void putIllegalArgumentExceptionTest() throws URISyntaxException{
        DocumentStoreImpl docStore = new DocumentStoreImpl();
        URI uri = new URI("https:tester.com");
        URI blankUri = new URI("");
        byte[] myData = "TesterData".getBytes();
        InputStream input = new ByteArrayInputStream(myData);
        assertThrows(IllegalArgumentException.class, () -> {
            docStore.put(input, uri, null);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            docStore.put(input, null, DocumentStore.DocumentFormat.BINARY);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            docStore.put(input, blankUri, DocumentStore.DocumentFormat.BINARY);
        });
    }

    @Test
    void putDeleteDocReturn0Test() throws URISyntaxException, IOException {
        DocumentStoreImpl docStore = new DocumentStoreImpl();
        URI uri = new URI("https:tester.com");
        assertEquals(0, docStore.put(null, uri, DocumentStore.DocumentFormat.BINARY));
    }

    @Test
    void putDeleteDocNormalReturnTest() throws URISyntaxException, IOException {
        DocumentStoreImpl docStore = new DocumentStoreImpl();
        URI uri = new URI("https:tester.com");
        byte[] myData = "Tester Data".getBytes();
        InputStream input = new ByteArrayInputStream(myData);
        DocumentImpl document = new DocumentImpl(uri, "Soon to be deleted doc", null);
        byte[] myData1 = "Soon to be deleted doc".getBytes();
        InputStream input1 = new ByteArrayInputStream(myData1);
        docStore.put(input1, uri, DocumentStore.DocumentFormat.TXT);
        assertEquals(document, docStore.get(uri));
        assertEquals(document.hashCode(), docStore.put(null, uri, DocumentStore.DocumentFormat.TXT));
        assertNull(docStore.get(uri));
    }

    @Test
    void put0ReturnTxtInputTest() throws URISyntaxException, IOException {
        DocumentStoreImpl docStore = new DocumentStoreImpl();
        URI uri = new URI("https:tester.com");
        byte[] myData = "Tester Data for Text Document".getBytes();
        InputStream input = new ByteArrayInputStream(myData);
        assertEquals(0, docStore.put(input, uri, DocumentStore.DocumentFormat.TXT));
    }

    @Test
    void putOldDocReturnTxtInputTest() throws URISyntaxException, IOException {
        DocumentStoreImpl docStore = new DocumentStoreImpl();
        URI uri = new URI("https:tester.com");
        byte[] myData = "Tester Data for Text Document".getBytes();
        InputStream input = new ByteArrayInputStream(myData);
        Document oldDoc = new DocumentImpl(uri, "Old Document", null);
        docStore.documents.put(uri, oldDoc);
        assertEquals(oldDoc.hashCode(), docStore.put(input, uri, DocumentStore.DocumentFormat.TXT));
        Document checker = docStore.get(uri);
        String docString = checker.getDocumentTxt();
        System.out.println(docString);
    }

    @Test
    void put0ReturnByteArrayInputTest() throws URISyntaxException, IOException {
        DocumentStoreImpl docStore = new DocumentStoreImpl();
        URI uri = new URI("https:tester.com");
        byte[] myData = {1, 2, 3, 4};
        InputStream input = new ByteArrayInputStream(myData);
        assertEquals(0, docStore.put(input, uri, DocumentStore.DocumentFormat.BINARY));
    }

    @Test
    void putOldDocReturnByteArrayInputTest() throws URISyntaxException, IOException {
        DocumentStoreImpl docStore = new DocumentStoreImpl();
        URI uri = new URI("https:tester.com");
        byte[] myData = {1, 2, 3, 4};
        InputStream input = new ByteArrayInputStream(myData);
        Document oldDoc = new DocumentImpl(uri, "Old Document", null);
        docStore.documents.put(uri, oldDoc);
        assertEquals(oldDoc.hashCode(), docStore.put(input, uri, DocumentStore.DocumentFormat.BINARY));
        Document checker = docStore.get(uri);
        byte[] docBinData = checker.getDocumentBinaryData();
        for(byte i : docBinData){
            System.out.println(i);
        }
    }

    @Test
    void getTest() throws URISyntaxException, IOException{
        DocumentStoreImpl docStore = new DocumentStoreImpl();
        URI uri = new URI("https:tester.com");
        DocumentImpl document = new DocumentImpl(uri, "Text", null);
        assertNull(docStore.get(uri));
        byte[] myData1 = "Text".getBytes();
        InputStream input1 = new ByteArrayInputStream(myData1);
        docStore.put(input1, uri, DocumentStore.DocumentFormat.TXT);
        assertEquals(document, docStore.get(uri));
    }

    @Test
    void deleteTrueTest() throws URISyntaxException, IOException{
        DocumentStoreImpl docStore = new DocumentStoreImpl();
        URI uri = new URI("https:tester.com");
        DocumentImpl document = new DocumentImpl(uri, "Text", null);
        byte[] myData1 = "Text".getBytes();
        InputStream input1 = new ByteArrayInputStream(myData1);
        docStore.put(input1, uri, DocumentStore.DocumentFormat.TXT);
        assertTrue(docStore.delete(uri));
    }

    @Test
    void deleteFalseTest() throws URISyntaxException{
        DocumentStoreImpl docStore = new DocumentStoreImpl();
        URI uri = new URI("https:tester.com");
        DocumentImpl document = new DocumentImpl(uri, "Text", null);
        assertFalse(docStore.delete(uri));
    }

    @Test
    void testUndoPutAddedDoc() throws URISyntaxException, IOException{
        DocumentStoreImpl docStore = new DocumentStoreImpl();
        URI uri = new URI("https:tester.com");
        DocumentImpl document = new DocumentImpl(uri, "Tester Data", null);
        byte[] myData = "Tester Data".getBytes();
        InputStream input = new ByteArrayInputStream(myData);
        assertEquals(0, docStore.put(input, uri, DocumentStore.DocumentFormat.TXT));
        assertEquals(document, docStore.get(uri));
        //Test if the undo call removes the document from the store
        docStore.undo();
        assertNull(docStore.get(uri));
    }

    @Test
    void testUndoPutAddedDocMultiple() throws URISyntaxException, IOException{
        DocumentStoreImpl docStore = new DocumentStoreImpl();
        URI uri1 = new URI("https:tester1.com");
        byte[] myData1 = "Tester Data1".getBytes();
        InputStream input1 = new ByteArrayInputStream(myData1);
        assertEquals(0, docStore.put(input1, uri1, DocumentStore.DocumentFormat.TXT));
        URI uri2 = new URI("https:tester2.com");
        byte[] myData2 = "Tester Data2".getBytes();
        InputStream input2 = new ByteArrayInputStream(myData2);
        docStore.put(input2, uri2, DocumentStore.DocumentFormat.TXT);
        URI uri3 = new URI("https:tester3.com");
        byte[] myData3 = "Tester Data3".getBytes();
        InputStream input3 = new ByteArrayInputStream(myData3);
        docStore.put(input3, uri3, DocumentStore.DocumentFormat.TXT);
        DocumentImpl document = new DocumentImpl(uri3, "Tester Data3", null);
        assertEquals(document, docStore.get(uri3));
        //Test if the undo call removes the document from the store
        docStore.undo();
        assertNull(docStore.get(uri3));
    }

    @Test
    void testUndoPutOverriddenDoc() throws URISyntaxException, IOException{
        DocumentStoreImpl docStore = new DocumentStoreImpl();
        URI uri1 = new URI("https:tester1.com");
        byte[] myData1 = "Tester Data1".getBytes();
        InputStream input1 = new ByteArrayInputStream(myData1);
        assertEquals(0, docStore.put(input1, uri1, DocumentStore.DocumentFormat.TXT));
        byte[] myData2 = "Tester Data2".getBytes();
        InputStream input2 = new ByteArrayInputStream(myData2);
        docStore.put(input2, uri1, DocumentStore.DocumentFormat.TXT);
        DocumentImpl document = new DocumentImpl(uri1, "Tester Data1", null);
        assertNotEquals(document, docStore.get(uri1));
        //Test if the undo call puts the overwritten doc back in the store
        docStore.undo();
        assertEquals(document, docStore.get(uri1));
    }

    @Test
    void testUndoPutOverriddenDocMultiple() throws URISyntaxException, IOException{
        DocumentStoreImpl docStore = new DocumentStoreImpl();
        URI uri1 = new URI("https:tester1.com");
        byte[] myData1 = "Tester Data1".getBytes();
        InputStream input1 = new ByteArrayInputStream(myData1);
        //Add doc1 to the store
        assertEquals(0, docStore.put(input1, uri1, DocumentStore.DocumentFormat.TXT));
        URI uri2 = new URI("https:tester2.com");
        byte[] myData2 = "Tester Data2".getBytes();
        InputStream input2 = new ByteArrayInputStream(myData2);
        //add doc2 to the store
        docStore.put(input2, uri2, DocumentStore.DocumentFormat.TXT);
        byte[] myData3 = "Tester Data3".getBytes();
        InputStream input3 = new ByteArrayInputStream(myData3);
        //Overwrite doc2 with doc3
        docStore.put(input3, uri2, DocumentStore.DocumentFormat.TXT);
        DocumentImpl document2 = new DocumentImpl(uri2, "Tester Data2", null);
        assertNotEquals(document2, docStore.get(uri2));
        //Test if the undo call puts the overwritten doc back in the store
        docStore.undo();
        assertEquals(document2, docStore.get(uri2));
    }

    @Test
    void testUndoDelete() throws URISyntaxException, IOException{
        DocumentStoreImpl docStore = new DocumentStoreImpl();
        URI uri1 = new URI("https:tester1.com");
        byte[] myData1 = "Tester Data1".getBytes();
        InputStream input1 = new ByteArrayInputStream(myData1);
        //Add doc1 to the store
        assertEquals(0, docStore.put(input1, uri1, DocumentStore.DocumentFormat.TXT));
        //Delete the doc from the store
        assertTrue(docStore.delete(uri1));
        assertNull(docStore.get(uri1));
        //Test if the undo call add the document back into the store
        docStore.undo();
        DocumentImpl document = new DocumentImpl(uri1, "Tester Data1", null);
        assertEquals(document, docStore.get(uri1));
    }

    @Test
    void testUndoDeleteMultiple() throws URISyntaxException, IOException{
        DocumentStoreImpl docStore = new DocumentStoreImpl();
        URI uri1 = new URI("https:tester1.com");
        byte[] myData1 = "Tester Data1".getBytes();
        InputStream input1 = new ByteArrayInputStream(myData1);
        //Add doc1 to the store
        assertEquals(0, docStore.put(input1, uri1, DocumentStore.DocumentFormat.TXT));
        URI uri2 = new URI("https:tester2.com");
        byte[] myData2 = "Tester Data2".getBytes();
        InputStream input2 = new ByteArrayInputStream(myData2);
        //add doc2 to the store
        docStore.put(input2, uri2, DocumentStore.DocumentFormat.TXT);
        //Delete doc2 from the store
        assertTrue(docStore.delete(uri2));
        assertNull(docStore.get(uri2));
        //Test if the undo call add the document back into the store
        docStore.undo();
        DocumentImpl document = new DocumentImpl(uri2, "Tester Data2", null);
        assertEquals(document, docStore.get(uri2));
    }

    @Test
    void testUndoSetMetadataNull() throws URISyntaxException, IOException{
        DocumentStoreImpl docStore = new DocumentStoreImpl();
        URI uri = new URI("https:tester.com");
        DocumentImpl document = new DocumentImpl(uri, "Text", null);
        byte[] myData1 = "Text".getBytes();
        InputStream input1 = new ByteArrayInputStream(myData1);
        docStore.put(input1, uri, DocumentStore.DocumentFormat.TXT);
        assertNull(docStore.setMetadata(uri, "key", "value"));
        assertEquals("value", docStore.getMetadata(uri, "key"));
        //Test if the undo call sets the value to null
        docStore.undo();
        assertNull(docStore.getMetadata(uri, "key"));
    }

    @Test
    void testSeriesofUndos() throws URISyntaxException, IOException{
        DocumentStoreImpl docStore = new DocumentStoreImpl();
        URI uri1 = new URI("https:tester1.com");
        byte[] myData1 = "Tester Data1".getBytes();
        InputStream input1 = new ByteArrayInputStream(myData1);
        //Add doc1 to the store
        docStore.put(input1, uri1, DocumentStore.DocumentFormat.TXT);
        URI uri2 = new URI("https:tester2.com");
        byte[] myData2 = "Tester Data2".getBytes();
        InputStream input2 = new ByteArrayInputStream(myData2);
        //add doc2 to the store
        docStore.put(input2, uri2, DocumentStore.DocumentFormat.TXT);
        //Set doc1 and 2 metadata
        docStore.setMetadata(uri1, "key1", "value1");
        docStore.setMetadata(uri2, "key2", "value2");
        //Undo both metadata
        docStore.undo();
        docStore.undo();
        assertNull(docStore.getMetadata(uri2, "key2"));
        assertNull(docStore.getMetadata(uri1, "key1"));
        //Undo a put
        docStore.undo();
        assertNull(docStore.get(uri2));
        docStore.undo();
        assertNull(docStore.get(uri1));
        assertThrows(IllegalStateException.class, () -> {docStore.undo();});
    }

    @Test
    void testUndoSetMetadataOldValue() throws URISyntaxException, IOException{
        DocumentStoreImpl docStore = new DocumentStoreImpl();
        URI uri = new URI("https:tester.com");
        DocumentImpl document = new DocumentImpl(uri, "Text", null);
        byte[] myData1 = "Text".getBytes();
        InputStream input1 = new ByteArrayInputStream(myData1);
        docStore.put(input1, uri, DocumentStore.DocumentFormat.TXT);
        assertNull(docStore.setMetadata(uri, "key", "oldValue"));
        assertEquals("oldValue", docStore.getMetadata(uri, "key"));
        assertEquals("oldValue", docStore.setMetadata(uri, "key", "newValue"));
        //Test if the undo call reverts the value to its previous value
        docStore.undo();
        assertEquals("oldValue", docStore.getMetadata(uri, "key"));
    }

    @Test
    void testUndoEmptyStack() throws URISyntaxException, IOException{
        DocumentStoreImpl docStore = new DocumentStoreImpl();
        URI uri = new URI("https:tester.com");
        assertThrows(IllegalStateException.class, () -> {docStore.undo();});
        assertThrows(IllegalStateException.class, () -> {docStore.undo(uri);});
        URI uri1 = new URI("https:tester1.com");
        byte[] myData1 = "Tester Data1".getBytes();
        InputStream input1 = new ByteArrayInputStream(myData1);
        //Add doc1 to the store
        docStore.put(input1, uri1, DocumentStore.DocumentFormat.TXT);
        URI uri2 = new URI("https:tester2.com");
        byte[] myData2 = "Tester Data2".getBytes();
        InputStream input2 = new ByteArrayInputStream(myData2);
        //add doc2 to the store
        docStore.put(input2, uri2, DocumentStore.DocumentFormat.TXT);
        assertNotNull(docStore.get(uri1));
        docStore.undo(uri1);
        assertNull(docStore.get(uri1));
        assertThrows(IllegalStateException.class, () -> {docStore.undo(uri1);});
    }

    @Test
    void testURLUndoPutAddedDocMultiple() throws URISyntaxException, IOException{
        DocumentStoreImpl docStore = new DocumentStoreImpl();
        URI uri1 = new URI("https:tester1.com");
        byte[] myData1 = "Tester Data1".getBytes();
        InputStream input1 = new ByteArrayInputStream(myData1);
        assertEquals(0, docStore.put(input1, uri1, DocumentStore.DocumentFormat.TXT));
        URI uri2 = new URI("https:tester2.com");
        byte[] myData2 = "Tester Data2".getBytes();
        InputStream input2 = new ByteArrayInputStream(myData2);
        docStore.put(input2, uri2, DocumentStore.DocumentFormat.TXT);
        URI uri3 = new URI("https:tester3.com");
        byte[] myData3 = "Tester Data3".getBytes();
        InputStream input3 = new ByteArrayInputStream(myData3);
        docStore.put(input3, uri3, DocumentStore.DocumentFormat.TXT);
        DocumentImpl document = new DocumentImpl(uri2, "Tester Data2", null);
        assertEquals(document, docStore.get(uri2));
        //Test if the undo call removes doc2 from the store
        docStore.undo(uri2);
        assertNull(docStore.get(uri2));
        //Test if the undo call removes doc1 from the store
        assertNotNull(docStore.get(uri1));
        docStore.undo(uri1);
        assertNull(docStore.get(uri1));
    }

    @Test
    void testURLUndoPutOverriddenDocMultiple() throws URISyntaxException, IOException{
        DocumentStoreImpl docStore = new DocumentStoreImpl();
        URI uri1 = new URI("https:tester1.com");
        byte[] myData1 = "Tester Data1".getBytes();
        InputStream input1 = new ByteArrayInputStream(myData1);
        //Add doc1 to the store
        docStore.put(input1, uri1, DocumentStore.DocumentFormat.TXT);
        URI uri2 = new URI("https:tester2.com");
        byte[] myData2 = "Tester Data2".getBytes();
        InputStream input2 = new ByteArrayInputStream(myData2);
        //add doc2 to the store
        docStore.put(input2, uri2, DocumentStore.DocumentFormat.TXT);
        byte[] myData3 = "Tester Data3".getBytes();
        InputStream input3 = new ByteArrayInputStream(myData3);
        //Overwrite doc2 with doc3
        docStore.put(input3, uri2, DocumentStore.DocumentFormat.TXT);
        URI uri4 = new URI("https:tester4.com");
        byte[] myData4 = "Tester Data4".getBytes();
        InputStream input4 = new ByteArrayInputStream(myData4);
        //add doc4 to the store
        docStore.put(input4, uri4, DocumentStore.DocumentFormat.TXT);
        DocumentImpl document2 = new DocumentImpl(uri2, "Tester Data2", null);
        assertNotEquals(document2, docStore.get(uri2));
        //Test if the undo call puts the overwritten doc back in the store
        docStore.undo(uri2);
        assertEquals(document2, docStore.get(uri2));
    }

    @Test
    void testURLUndoDeleteMultiple() throws URISyntaxException, IOException{
        DocumentStoreImpl docStore = new DocumentStoreImpl();
        URI uri1 = new URI("https:tester1.com");
        byte[] myData1 = "Tester Data1".getBytes();
        InputStream input1 = new ByteArrayInputStream(myData1);
        //Add doc1 to the store
        docStore.put(input1, uri1, DocumentStore.DocumentFormat.TXT);
        URI uri2 = new URI("https:tester2.com");
        byte[] myData2 = "Tester Data2".getBytes();
        InputStream input2 = new ByteArrayInputStream(myData2);
        //add doc2 to the store
        docStore.put(input2, uri2, DocumentStore.DocumentFormat.TXT);
        //Delete doc1 from the store
        assertTrue(docStore.delete(uri1));
        assertNull(docStore.get(uri1));
        //Test if the undo call adds doc1 back into the store
        docStore.undo(uri1);
        DocumentImpl document = new DocumentImpl(uri1, "Tester Data1", null);
        assertEquals(document, docStore.get(uri1));
    }

    @Test
    void testURLUndoSetMetadataNull() throws URISyntaxException, IOException{
        DocumentStoreImpl docStore = new DocumentStoreImpl();
        URI uri1 = new URI("https:tester1.com");
        byte[] myData1 = "Tester Data1".getBytes();
        InputStream input1 = new ByteArrayInputStream(myData1);
        //Add doc1 to the store
        docStore.put(input1, uri1, DocumentStore.DocumentFormat.TXT);
        //Set the metadata of doc1
        docStore.setMetadata(uri1, "key", "value");
        assertEquals("value", docStore.getMetadata(uri1, "key"));
        URI uri2 = new URI("https:tester2.com");
        byte[] myData2 = "Tester Data2".getBytes();
        InputStream input2 = new ByteArrayInputStream(myData2);
        //add doc2 to the store
        docStore.put(input2, uri2, DocumentStore.DocumentFormat.TXT);
        //Test if the undo call sets doc1 metadata to null
        docStore.undo(uri1);
        assertNull(docStore.getMetadata(uri1, "key"));
    }

    @Test
    void testURLUndoSetMetadataOldValue() throws URISyntaxException, IOException{
        DocumentStoreImpl docStore = new DocumentStoreImpl();
        URI uri1 = new URI("https:tester1.com");
        byte[] myData1 = "Tester Data1".getBytes();
        InputStream input1 = new ByteArrayInputStream(myData1);
        //Add doc1 to the store
        docStore.put(input1, uri1, DocumentStore.DocumentFormat.TXT);
        URI uri2 = new URI("https:tester2.com");
        byte[] myData2 = "Tester Data2".getBytes();
        InputStream input2 = new ByteArrayInputStream(myData2);
        //add doc2 to the store
        docStore.put(input2, uri2, DocumentStore.DocumentFormat.TXT);
        //Set the metadata of doc1 and then overwrite it
        docStore.setMetadata(uri1, "key", "oldValue");
        assertEquals("oldValue", docStore.setMetadata(uri1, "key", "newValue"));
        assertEquals("newValue", docStore.getMetadata(uri1, "key"));
        //Test if the undo call sets doc1 metadata to null
        docStore.undo(uri1);
        assertEquals("oldValue", docStore.getMetadata(uri1, "key"));
        //Try an undo with a uri that isn't in the command stack
        URI uriTester = new URI("https:tester8.com");
        //docStore.undo(uriTester);
        assertThrows(IllegalStateException.class, () -> {docStore.undo(uriTester);});
    }

    //******Stage 4 Tests

    @Test
    void testSearch() throws URISyntaxException, IOException{
        DocumentStoreImpl docStore = new DocumentStoreImpl();
        URI uri1 = new URI("https:tester1.com");
        byte[] myData1 = "Tester Data1".getBytes();
        InputStream input1 = new ByteArrayInputStream(myData1);
        //Add doc1 to the store
        docStore.put(input1, uri1, DocumentStore.DocumentFormat.TXT);
        URI uri2 = new URI("https:tester2.com");
        byte[] myData2 = "Tester Data2 Tester Tester".getBytes();
        InputStream input2 = new ByteArrayInputStream(myData2);
        //add doc2 to the store
        docStore.put(input2, uri2, DocumentStore.DocumentFormat.TXT);
        URI uri3 = new URI("https:tester3.com");
        byte[] myData3 = "Tester Data3 Tester Tester Tester".getBytes();
        InputStream input3 = new ByteArrayInputStream(myData3);
        //add doc2 to the store
        docStore.put(input3, uri3, DocumentStore.DocumentFormat.TXT);
        List<Document> docList = docStore.search("Tester");
        assertEquals(3, docList.size());
        /*for(Document doc : docList){
            System.out.println(doc.getDocumentTxt());
        }*/
    }

    @Test
    void testSearchByPrefix() throws URISyntaxException, IOException{
        DocumentStoreImpl docStore = new DocumentStoreImpl();
        URI uri1 = new URI("https:tester1.com");
        byte[] myData1 = "Data1 Test Tester Tested".getBytes();
        InputStream input1 = new ByteArrayInputStream(myData1);
        //Add doc1 to the store
        docStore.put(input1, uri1, DocumentStore.DocumentFormat.TXT);
        URI uri2 = new URI("https:tester2.com");
        byte[] myData2 = "Data2 Tester Testing".getBytes();
        InputStream input2 = new ByteArrayInputStream(myData2);
        //add doc2 to the store
        docStore.put(input2, uri2, DocumentStore.DocumentFormat.TXT);
        URI uri3 = new URI("https:tester3.com");
        byte[] myData3 = "Data3 Tested Test Test Test Test Test, Test Test".getBytes();
        InputStream input3 = new ByteArrayInputStream(myData3);
        //add doc2 to the store
        docStore.put(input3, uri3, DocumentStore.DocumentFormat.TXT);
        List<Document> docList = docStore.searchByPrefix("Test");
        assertEquals(3, docList.size());
        /*for(Document doc : docList){
            System.out.println(doc.getDocumentTxt());
        }*/
    }

    @Test
    void testDeleteAll() throws URISyntaxException, IOException{
        DocumentStoreImpl docStore = new DocumentStoreImpl();
        URI uri1 = new URI("https:tester1.com");
        byte[] myData1 = "Data1 Test Tester Tested".getBytes();
        InputStream input1 = new ByteArrayInputStream(myData1);
        //Add doc1 to the store
        docStore.put(input1, uri1, DocumentStore.DocumentFormat.TXT);
        URI uri2 = new URI("https:tester2.com");
        byte[] myData2 = "Data2 Tester Testing".getBytes();
        InputStream input2 = new ByteArrayInputStream(myData2);
        //add doc2 to the store
        docStore.put(input2, uri2, DocumentStore.DocumentFormat.TXT);
        URI uri3 = new URI("https:tester3.com");
        byte[] myData3 = "Data3 Tested Test Test Test Test Test, Test Test".getBytes();
        InputStream input3 = new ByteArrayInputStream(myData3);
        //add doc3 to the store
        docStore.put(input3, uri3, DocumentStore.DocumentFormat.TXT);
        assertEquals(1, docStore.search("Data1").size());
        assertEquals(2, docStore.search("Test").size());
        //Check if the docs are currently in the
        assertNotNull(docStore.get(uri1));
        //Delete all traces of docs with word "Test" from the docStore
        Set<URI> setOfDocURIs = docStore.deleteAll("Test");
        assertEquals(2, setOfDocURIs.size());
        /*for(URI url : setOfDocURIs){
            System.out.println(url.toString());
        }*/
        //Check if they were actually removed from the trie and store
        assertEquals(0, docStore.search("Test").size());
        assertNull(docStore.get(uri1));
        assertNull(docStore.get(uri3));
        assertEquals(0, docStore.search("Data1").size());
    }

    @Test
    void testDeleteAllWithPrefix() throws URISyntaxException, IOException{
        DocumentStoreImpl docStore = new DocumentStoreImpl();
        URI uri1 = new URI("https:tester1.com");
        byte[] myData1 = "Data1 Testing Tester Tested".getBytes();
        InputStream input1 = new ByteArrayInputStream(myData1);
        //Add doc1 to the store
        docStore.put(input1, uri1, DocumentStore.DocumentFormat.TXT);
        URI uri2 = new URI("https:tester2.com");
        byte[] myData2 = "Data2 Tester Testing".getBytes();
        InputStream input2 = new ByteArrayInputStream(myData2);
        //add doc2 to the store
        docStore.put(input2, uri2, DocumentStore.DocumentFormat.TXT);
        URI uri3 = new URI("https:tester3.com");
        byte[] myData3 = "Data3 Tested Test Test Test Test Test, Test Test".getBytes();
        InputStream input3 = new ByteArrayInputStream(myData3);
        //add doc3 to the store
        docStore.put(input3, uri3, DocumentStore.DocumentFormat.TXT);
        assertEquals(3, docStore.searchByPrefix("Test").size());
        Set<URI> setOfDocURIs = docStore.deleteAllWithPrefix("Test");
        assertEquals(3, setOfDocURIs.size());
        /*for(URI url : setOfDocURIs){
            System.out.println(url.toString());
        }*/
        //Check if they were actually removed from the trie
        assertEquals(0, docStore.searchByPrefix("Test").size());
        assertEquals(0, docStore.searchByPrefix("Data").size());
        assertEquals(0, docStore.search("Tester").size());
        assertNull(docStore.get(uri1));
        assertNull(docStore.get(uri2));
        assertNull(docStore.get(uri3));
    }

    @Test
    void testSearchByMetadata() throws URISyntaxException, IOException{
        DocumentStoreImpl docStore = new DocumentStoreImpl();
        URI uri1 = new URI("https:tester1.com");
        byte[] myData1 = "Data1 Testing Tester Tested".getBytes();
        InputStream input1 = new ByteArrayInputStream(myData1);
        //Add doc1 to the store
        docStore.put(input1, uri1, DocumentStore.DocumentFormat.TXT);
        URI uri2 = new URI("https:tester2.com");
        byte[] myData2 = "Data2 Tester Testing".getBytes();
        InputStream input2 = new ByteArrayInputStream(myData2);
        //add doc2 to the store
        docStore.put(input2, uri2, DocumentStore.DocumentFormat.TXT);
        URI uri3 = new URI("https:tester3.com");
        byte[] myData3 = "Data3 Tested Test Test Test Test Test, Test Test".getBytes();
        InputStream input3 = new ByteArrayInputStream(myData3);
        //add doc3 to the store
        docStore.put(input3, uri3, DocumentStore.DocumentFormat.TXT);
        //Set Metadata for the docs
        docStore.setMetadata(uri1, "key1", "value1");
        docStore.setMetadata(uri2, "key1", "value1");
        docStore.setMetadata(uri3, "key1", "value1");
        docStore.setMetadata(uri1, "key2", "value2");
        docStore.setMetadata(uri2, "key2", "value2");
        docStore.setMetadata(uri3, "key2", "value2");
        docStore.setMetadata(uri1, "key3", "value3");
        //Check the method
        Map<String,String> keysValues = new HashMap<>();
        keysValues.put("key1", "value1");
        keysValues.put("key2", "value2");
        List<Document> docsMetaData = docStore.searchByMetadata(keysValues);
        assertEquals(3, docsMetaData.size());
        /*for(Document doc : docsMetaData){
            System.out.println(doc.getDocumentTxt());
        }*/
        keysValues.put("key3", "value3");
        assertEquals(1, docStore.searchByMetadata(keysValues).size());
        keysValues.put("key3", "value4");
        assertEquals(0, docStore.searchByMetadata(keysValues).size());
    }

    @Test
    void testSearchByKeywordAndMetadata() throws URISyntaxException, IOException{
        DocumentStoreImpl docStore = new DocumentStoreImpl();
        URI uri1 = new URI("https:tester1.com");
        byte[] myData1 = "Data1 Testing Tester Tested Test".getBytes();
        InputStream input1 = new ByteArrayInputStream(myData1);
        //Add doc1 to the store
        docStore.put(input1, uri1, DocumentStore.DocumentFormat.TXT);
        URI uri2 = new URI("https:tester2.com");
        byte[] myData2 = "Data2 Tester Testing Test Test".getBytes();
        InputStream input2 = new ByteArrayInputStream(myData2);
        //add doc2 to the store
        docStore.put(input2, uri2, DocumentStore.DocumentFormat.TXT);
        URI uri3 = new URI("https:tester3.com");
        byte[] myData3 = "Data3 Tested Test Test Test Test Tester".getBytes();
        InputStream input3 = new ByteArrayInputStream(myData3);
        //add doc3 to the store
        docStore.put(input3, uri3, DocumentStore.DocumentFormat.TXT);
        //Set Metadata for the docs
        docStore.setMetadata(uri1, "key1", "value1");
        docStore.setMetadata(uri2, "key1", "value1");
        docStore.setMetadata(uri3, "key1", "value1");
        docStore.setMetadata(uri1, "key2", "value2");
        docStore.setMetadata(uri2, "key2", "value2");
        docStore.setMetadata(uri3, "key2", "value2");
        docStore.setMetadata(uri1, "key3", "value3");
        //Check the method
        Map<String,String> keysValues = new HashMap<>();
        keysValues.put("key1", "value1");
        keysValues.put("key2", "value2");
        //keysValues.put("key3", "value3");
        List<Document> docsMetaData = docStore.searchByKeywordAndMetadata("Test", keysValues);
        assertEquals(3, docsMetaData.size());
        for(Document doc : docsMetaData){
            System.out.println(doc.getDocumentTxt());
        }
    }

    @Test
    void testSearchByPrefixAndMetadata() throws URISyntaxException, IOException{
        DocumentStoreImpl docStore = new DocumentStoreImpl();
        URI uri1 = new URI("https:tester1.com");
        byte[] myData1 = "Data1 Testing Tester Tested".getBytes();
        InputStream input1 = new ByteArrayInputStream(myData1);
        //Add doc1 to the store
        docStore.put(input1, uri1, DocumentStore.DocumentFormat.TXT);
        URI uri2 = new URI("https:tester2.com");
        byte[] myData2 = "Data2 Tester Testing Test Test Tested".getBytes();
        InputStream input2 = new ByteArrayInputStream(myData2);
        //add doc2 to the store
        docStore.put(input2, uri2, DocumentStore.DocumentFormat.TXT);
        URI uri3 = new URI("https:tester3.com");
        byte[] myData3 = "Data3 Tested".getBytes();
        InputStream input3 = new ByteArrayInputStream(myData3);
        //add doc3 to the store
        docStore.put(input3, uri3, DocumentStore.DocumentFormat.TXT);
        //Set Metadata for the docs
        docStore.setMetadata(uri1, "key1", "value1");
        docStore.setMetadata(uri2, "key1", "value1");
        docStore.setMetadata(uri3, "key1", "value1");
        docStore.setMetadata(uri1, "key2", "value2");
        docStore.setMetadata(uri2, "key2", "value2");
        docStore.setMetadata(uri3, "key2", "value2");
        docStore.setMetadata(uri1, "key3", "value3");
        //Check the method
        Map<String,String> keysValues = new HashMap<>();
        keysValues.put("key1", "value1");
        keysValues.put("key2", "value2");
        //keysValues.put("key3", "value3");
        List<Document> docsMetaData = docStore.searchByPrefixAndMetadata("Testing", keysValues);
        assertEquals(2, docsMetaData.size());
        for(Document doc : docsMetaData){
            System.out.println(doc.getDocumentTxt());
        }
    }

    @Test
    void testDeleteAllWithMetadata() throws URISyntaxException, IOException{
        DocumentStoreImpl docStore = new DocumentStoreImpl();
        URI uri1 = new URI("https:tester1.com");
        byte[] myData1 = "Data1 Testing Tester Tested".getBytes();
        InputStream input1 = new ByteArrayInputStream(myData1);
        //Add doc1 to the store
        docStore.put(input1, uri1, DocumentStore.DocumentFormat.TXT);
        URI uri2 = new URI("https:tester2.com");
        byte[] myData2 = "Data2 Tester Testing".getBytes();
        InputStream input2 = new ByteArrayInputStream(myData2);
        //add doc2 to the store
        docStore.put(input2, uri2, DocumentStore.DocumentFormat.TXT);
        URI uri3 = new URI("https:tester3.com");
        byte[] myData3 = "Data3 Tested Test Test Test Test Test, Test Test".getBytes();
        InputStream input3 = new ByteArrayInputStream(myData3);
        //add doc3 to the store
        docStore.put(input3, uri3, DocumentStore.DocumentFormat.TXT);
        //Set Metadata for the docs
        docStore.setMetadata(uri1, "key1", "value1");
        docStore.setMetadata(uri2, "key1", "value1");
        docStore.setMetadata(uri3, "key1", "value1");
        docStore.setMetadata(uri1, "key2", "value2");
        docStore.setMetadata(uri2, "key2", "value2");
        docStore.setMetadata(uri3, "key2", "value2");
        docStore.setMetadata(uri1, "key3", "value3");
        //Create the Map of metadata for the docs to delete
        Map<String,String> keysValues = new HashMap<>();
        keysValues.put("key3", "value3");
        //keysValues.put("key2", "value2");
        List<Document> docsMetaData = docStore.searchByMetadata(keysValues);
        assertEquals(1, docsMetaData.size());
        //How many docs have the prefix "Data"?
        assertEquals(3, docStore.searchByPrefix("Data").size());
        //Call the method to delete the docs with said metadata
        Set<URI> setOfDocURIs = docStore.deleteAllWithMetadata(keysValues);
        assertEquals(1, setOfDocURIs.size());
        assertNull(docStore.get(uri1));
        assertEquals(2, docStore.searchByPrefix("Data").size());
    }

    @Test
    void testDeleteAllWithKeywordAndMetadata() throws URISyntaxException, IOException{
        DocumentStoreImpl docStore = new DocumentStoreImpl();
        URI uri1 = new URI("https:tester1.com");
        byte[] myData1 = "Data1 Testing Tester Tested".getBytes();
        InputStream input1 = new ByteArrayInputStream(myData1);
        //Add doc1 to the store
        docStore.put(input1, uri1, DocumentStore.DocumentFormat.TXT);
        URI uri2 = new URI("https:tester2.com");
        byte[] myData2 = "Data2 Tester Testing".getBytes();
        InputStream input2 = new ByteArrayInputStream(myData2);
        //add doc2 to the store
        docStore.put(input2, uri2, DocumentStore.DocumentFormat.TXT);
        URI uri3 = new URI("https:tester3.com");
        byte[] myData3 = "Data3 Tested Test Test Test Test Tester".getBytes();
        InputStream input3 = new ByteArrayInputStream(myData3);
        //add doc3 to the store
        docStore.put(input3, uri3, DocumentStore.DocumentFormat.TXT);
        //Set Metadata for the docs
        //docStore.setMetadata(uri1, "key1", "value1");
        docStore.setMetadata(uri2, "key1", "value1");
        docStore.setMetadata(uri3, "key1", "value1");
        docStore.setMetadata(uri1, "key2", "value2");
        docStore.setMetadata(uri2, "key2", "value2");
        docStore.setMetadata(uri3, "key2", "value2");
        docStore.setMetadata(uri1, "key3", "value3");
        //Create the Map of metadata for the docs to delete
        Map<String,String> keysValues = new HashMap<>();
        keysValues.put("key1", "value1");
        keysValues.put("key2", "value2");
        //keysValues.put("key2", "value2");
        List<Document> docsMetaData = docStore.searchByKeywordAndMetadata("Tester", keysValues);
        assertEquals(2, docsMetaData.size());
        //How many docs have the prefix "Data"?
        assertEquals(3, docStore.searchByPrefix("Data").size());
        //Call the method to delete the docs with said metadata
        Set<URI> setOfDocURIs = docStore.deleteAllWithKeywordAndMetadata("Tester", keysValues);
        assertEquals(2, setOfDocURIs.size());
        assertNotNull(docStore.get(uri1));
        assertNull(docStore.get(uri2));
        assertNull(docStore.get(uri3));
        assertEquals(1, docStore.searchByPrefix("Data").size());
    }

    @Test
    void testDeleteAllWithPrefixAndMetadata() throws URISyntaxException, IOException{
        DocumentStoreImpl docStore = new DocumentStoreImpl();
        URI uri1 = new URI("https:tester1.com");
        byte[] myData1 = "Data1 Testing Tester Tested".getBytes();
        InputStream input1 = new ByteArrayInputStream(myData1);
        //Add doc1 to the store
        docStore.put(input1, uri1, DocumentStore.DocumentFormat.TXT);
        URI uri2 = new URI("https:tester2.com");
        byte[] myData2 = "Data2 Tester Testing".getBytes();
        InputStream input2 = new ByteArrayInputStream(myData2);
        //add doc2 to the store
        docStore.put(input2, uri2, DocumentStore.DocumentFormat.TXT);
        URI uri3 = new URI("https:tester3.com");
        byte[] myData3 = "Data3 Tested Test Test Test Test Tester".getBytes();
        InputStream input3 = new ByteArrayInputStream(myData3);
        //add doc3 to the store
        docStore.put(input3, uri3, DocumentStore.DocumentFormat.TXT);
        //Set Metadata for the docs
        docStore.setMetadata(uri1, "key1", "value1");
        docStore.setMetadata(uri2, "key1", "value1");
        docStore.setMetadata(uri3, "key1", "value1");
        docStore.setMetadata(uri1, "key2", "value2");
        docStore.setMetadata(uri2, "key2", "value2");
        docStore.setMetadata(uri3, "key2", "value2");
        docStore.setMetadata(uri1, "key3", "value3");
        //Create the Map of metadata for the docs to delete
        Map<String,String> keysValues = new HashMap<>();
        keysValues.put("key1", "value1");
        keysValues.put("key2", "value2");
        //keysValues.put("key2", "value2");
        List<Document> docsMetaData = docStore.searchByPrefixAndMetadata("Test", keysValues);
        assertEquals(3, docsMetaData.size());
        //How many docs have the prefix "Data"?
        assertEquals(3, docStore.searchByPrefix("Data").size());
        //Call the method to delete the docs with said metadata
        Set<URI> setOfDocURIs = docStore.deleteAllWithPrefixAndMetadata("Test", keysValues);
        assertEquals(3, setOfDocURIs.size());
        assertNull(docStore.get(uri1));
        assertNull(docStore.get(uri2));
        assertNull(docStore.get(uri3));
        assertEquals(0, docStore.searchByPrefix("Data").size());
    }

    @Test
    void testDeleteAllWithUndo() throws URISyntaxException, IOException{
        DocumentStoreImpl docStore = new DocumentStoreImpl();
        URI uri1 = new URI("https:tester1.com");
        byte[] myData1 = "Data1 Test Tester Tested".getBytes();
        InputStream input1 = new ByteArrayInputStream(myData1);
        //Add doc1 to the store
        docStore.put(input1, uri1, DocumentStore.DocumentFormat.TXT);
        URI uri2 = new URI("https:tester2.com");
        byte[] myData2 = "Data2 Tester Testing".getBytes();
        InputStream input2 = new ByteArrayInputStream(myData2);
        //add doc2 to the store
        docStore.put(input2, uri2, DocumentStore.DocumentFormat.TXT);
        URI uri3 = new URI("https:tester3.com");
        byte[] myData3 = "Data3 Tested Test Test Test Test Test, Test Test".getBytes();
        InputStream input3 = new ByteArrayInputStream(myData3);
        //add doc3 to the store
        docStore.put(input3, uri3, DocumentStore.DocumentFormat.TXT);
        assertEquals(1, docStore.search("Data1").size());
        assertEquals(2, docStore.search("Test").size());
        //Check if the docs are currently in the
        assertNotNull(docStore.get(uri1));
        //Delete all traces of docs with word "Test" from the docStore
        Set<URI> setOfDocURIs = docStore.deleteAll("Test");
        assertEquals(2, setOfDocURIs.size());
        //Check if they were actually removed from the trie and store
        assertEquals(0, docStore.search("Test").size());
        assertNull(docStore.get(uri1));
        assertNull(docStore.get(uri3));
        assertEquals(0, docStore.search("Data1").size());
        //Call undo() to undo the deleteAll("Test") and see if the dos are added back to the store and trie
        docStore.undo();
        assertNotNull(docStore.get(uri1));
        assertNotNull(docStore.get(uri3));
        assertEquals(3, docStore.searchByPrefix("Data").size());
        //Call undo on the last thing done with uri1, which should remove doc1 from the store
        docStore.undo(uri1);
        assertEquals(2, docStore.searchByPrefix("Data").size());
        assertNull(docStore.get(uri1));
        assertThrows(IllegalStateException.class, () -> {docStore.undo(uri1);});
    }

    @Test
    void testDeleteAllWithPrefixUndo() throws URISyntaxException, IOException{
        DocumentStoreImpl docStore = new DocumentStoreImpl();
        URI uri1 = new URI("https:tester1.com");
        byte[] myData1 = "Data1 Testing Tester Tested".getBytes();
        InputStream input1 = new ByteArrayInputStream(myData1);
        //Add doc1 to the store
        docStore.put(input1, uri1, DocumentStore.DocumentFormat.TXT);
        URI uri2 = new URI("https:tester2.com");
        byte[] myData2 = "Data2 Tester Testing".getBytes();
        InputStream input2 = new ByteArrayInputStream(myData2);
        //add doc2 to the store
        docStore.put(input2, uri2, DocumentStore.DocumentFormat.TXT);
        URI uri3 = new URI("https:tester3.com");
        byte[] myData3 = "Data3 Tested Test Test Test Test Test, Test Test".getBytes();
        InputStream input3 = new ByteArrayInputStream(myData3);
        //add doc3 to the store
        docStore.put(input3, uri3, DocumentStore.DocumentFormat.TXT);
        byte[] myData4 = "Data4 overwriting document 2".getBytes();
        InputStream input4 = new ByteArrayInputStream(myData4);
        //overwrite doc2 with doc 4
        docStore.put(input4, uri2, DocumentStore.DocumentFormat.TXT);
        assertEquals(2, docStore.searchByPrefix("Test").size());
        Set<URI> setOfDocURIs = docStore.deleteAllWithPrefix("Test");
        assertEquals(2, setOfDocURIs.size());
        //Check if they were actually removed from the trie
        assertEquals(0, docStore.searchByPrefix("Test").size());
        assertEquals(1, docStore.searchByPrefix("Data").size());
        assertEquals(0, docStore.search("Tester").size());
        assertNull(docStore.get(uri1));
        assertNull(docStore.get(uri3));
        //Call undo on uri1 and see if it just adds document one back into the store
        docStore.undo(uri1);
        assertEquals(2, docStore.searchByPrefix("Data").size());
        //Call undo on uri2 and see if it reverts to putting document 2 back in the store/trie
        docStore.undo(uri2);
        assertEquals(2, docStore.searchByPrefix("Test").size());
        //Call undo() and see if it puts doc 3 (the last doc that was removed with deleteAllWithPrefix) back in the store
        docStore.undo(uri3);
        assertEquals(3, docStore.searchByPrefix("Test").size());
        assertNotNull(docStore.get(uri3));
    }

    @Test
    void testDeleteAllWithMetadataUndo() throws URISyntaxException, IOException{
        DocumentStoreImpl docStore = new DocumentStoreImpl();
        URI uri1 = new URI("https:tester1.com");
        byte[] myData1 = "Data1 Testing Tester Tested".getBytes();
        InputStream input1 = new ByteArrayInputStream(myData1);
        //Add doc1 to the store
        docStore.put(input1, uri1, DocumentStore.DocumentFormat.TXT);
        URI uri2 = new URI("https:tester2.com");
        byte[] myData2 = "Data2 Tester Testing".getBytes();
        InputStream input2 = new ByteArrayInputStream(myData2);
        //add doc2 to the store
        docStore.put(input2, uri2, DocumentStore.DocumentFormat.TXT);
        URI uri3 = new URI("https:tester3.com");
        byte[] myData3 = "Data3 Tested Test Test Test Test Test, Test Test".getBytes();
        InputStream input3 = new ByteArrayInputStream(myData3);
        //add doc3 to the store
        docStore.put(input3, uri3, DocumentStore.DocumentFormat.TXT);
        //Set Metadata for the docs
        docStore.setMetadata(uri1, "key1", "value1");
        docStore.setMetadata(uri2, "key1", "value1");
        docStore.setMetadata(uri3, "key1", "value1");
        docStore.setMetadata(uri1, "key2", "value2");
        docStore.setMetadata(uri2, "key2", "value2");
        docStore.setMetadata(uri3, "key2", "value2");
        docStore.setMetadata(uri1, "key3", "value3");
        //Undo the last action with uri2 -- get rid of it's key2 metadata
        docStore.undo(uri2);
        //Create the Map of metadata for the docs to delete
        Map<String,String> keysValues = new HashMap<>();
        keysValues.put("key2", "value2");
        //keysValues.put("key2", "value2");
        List<Document> docsMetaData = docStore.searchByMetadata(keysValues);
        assertEquals(2, docsMetaData.size());
        //How many docs have the prefix "Data"?
        assertEquals(3, docStore.searchByPrefix("Data").size());
        //Call the method to delete the docs with said metadata
        Set<URI> setOfDocURIs = docStore.deleteAllWithMetadata(keysValues);
        assertEquals(2, setOfDocURIs.size());
        assertNull(docStore.get(uri1));
        assertEquals(1, docStore.searchByPrefix("Data").size());
        //Call undo() to put those two docs back in the store
        docStore.undo();
        assertEquals(3, docStore.searchByPrefix("Data").size());
    }

    @Test
    void testDeleteAllWithKeywordAndMetadataUndo() throws URISyntaxException, IOException{
        DocumentStoreImpl docStore = new DocumentStoreImpl();
        URI uri1 = new URI("https:tester1.com");
        byte[] myData1 = "Data1 Testing Tester Tested".getBytes();
        InputStream input1 = new ByteArrayInputStream(myData1);
        //Add doc1 to the store
        docStore.put(input1, uri1, DocumentStore.DocumentFormat.TXT);
        URI uri2 = new URI("https:tester2.com");
        byte[] myData2 = "Data2 Tester Testing".getBytes();
        InputStream input2 = new ByteArrayInputStream(myData2);
        //add doc2 to the store
        docStore.put(input2, uri2, DocumentStore.DocumentFormat.TXT);
        URI uri3 = new URI("https:tester3.com");
        byte[] myData3 = "Data3 Tested Test Test Test Test Tester".getBytes();
        InputStream input3 = new ByteArrayInputStream(myData3);
        //add doc3 to the store
        docStore.put(input3, uri3, DocumentStore.DocumentFormat.TXT);
        //Set Metadata for the docs
        //docStore.setMetadata(uri1, "key1", "value1");
        docStore.setMetadata(uri2, "key1", "value1");
        docStore.setMetadata(uri3, "key1", "value1");
        docStore.setMetadata(uri1, "key2", "value2");
        docStore.setMetadata(uri2, "key2", "value2");
        docStore.setMetadata(uri3, "key2", "value2");
        docStore.setMetadata(uri1, "key3", "value3");
        //Create the Map of metadata for the docs to delete
        Map<String,String> keysValues = new HashMap<>();
        keysValues.put("key1", "value1");
        keysValues.put("key2", "value2");
        //keysValues.put("key2", "value2");
        List<Document> docsMetaData = docStore.searchByKeywordAndMetadata("Tester", keysValues);
        assertEquals(2, docsMetaData.size());
        //How many docs have the prefix "Data"?
        assertEquals(3, docStore.searchByPrefix("Data").size());
        //Call the method to delete the docs with said metadata
        Set<URI> setOfDocURIs = docStore.deleteAllWithKeywordAndMetadata("Tester", keysValues);
        assertEquals(2, setOfDocURIs.size());
        assertNotNull(docStore.get(uri1));
        assertNull(docStore.get(uri2));
        assertNull(docStore.get(uri3));
        assertEquals(1, docStore.searchByPrefix("Data").size());
        //Put them back into the sore
        docStore.undo();
        assertEquals(3, docStore.searchByPrefix("Data").size());
        assertNotNull(docStore.get(uri3));
        //call undo on uri3 multiple times to remove it from the store
        docStore.undo(uri3);
        docStore.undo(uri3);
        docStore.undo(uri3);
        assertNull(docStore.get(uri3));
    }

    @Test
    void testDeleteAllWithPrefixAndMetadataUndo() throws URISyntaxException, IOException{
        DocumentStoreImpl docStore = new DocumentStoreImpl();
        URI uri1 = new URI("https:tester1.com");
        byte[] myData1 = "Data1 Testing Tester Tested".getBytes();
        InputStream input1 = new ByteArrayInputStream(myData1);
        //Add doc1 to the store
        docStore.put(input1, uri1, DocumentStore.DocumentFormat.TXT);
        URI uri2 = new URI("https:tester2.com");
        byte[] myData2 = "Data2 Tester Testing".getBytes();
        InputStream input2 = new ByteArrayInputStream(myData2);
        //add doc2 to the store
        docStore.put(input2, uri2, DocumentStore.DocumentFormat.TXT);
        URI uri3 = new URI("https:tester3.com");
        byte[] myData3 = "Data3 Tested Test Test Test Test Tester".getBytes();
        InputStream input3 = new ByteArrayInputStream(myData3);
        //add doc3 to the store
        docStore.put(input3, uri3, DocumentStore.DocumentFormat.TXT);
        //Set Metadata for the docs
        docStore.setMetadata(uri1, "key1", "value1");
        docStore.setMetadata(uri2, "key1", "value1");
        docStore.setMetadata(uri3, "key1", "value1");
        docStore.setMetadata(uri1, "key2", "value2");
        docStore.setMetadata(uri2, "key2", "value2");
        docStore.setMetadata(uri3, "key2", "value2");
        docStore.setMetadata(uri1, "key3", "value3");
        //Create the Map of metadata for the docs to delete
        Map<String,String> keysValues = new HashMap<>();
        keysValues.put("key1", "value1");
        keysValues.put("key2", "value2");
        //keysValues.put("key2", "value2");
        List<Document> docsMetaData = docStore.searchByPrefixAndMetadata("Test", keysValues);
        assertEquals(3, docsMetaData.size());
        //How many docs have the prefix "Data"?
        assertEquals(3, docStore.searchByPrefix("Data").size());
        //Call the method to delete the docs with said metadata
        Set<URI> setOfDocURIs = docStore.deleteAllWithPrefixAndMetadata("Test", keysValues);
        assertEquals(3, setOfDocURIs.size());
        assertNull(docStore.get(uri1));
        assertNull(docStore.get(uri2));
        assertNull(docStore.get(uri3));
        assertEquals(0, docStore.searchByPrefix("Data").size());
        //Put doc 2 back in
        docStore.undo(uri2);
        assertEquals(1, docStore.searchByPrefix("Data").size());
        //Keep undoing uri 2 until it's out again
        docStore.undo(uri2);
        docStore.undo(uri2);
        docStore.undo(uri2);
        assertEquals(0, docStore.searchByPrefix("Data").size());
        //Call undo() to put doc 1 and 3 back
        docStore.undo();
        assertEquals(2, docStore.searchByPrefix("Data").size());
    }

    //---END OF OLD TESTS

    @Test
    void setAndGetMetadata() throws URISyntaxException, IOException {
        //Add two docs to the store
        DocumentStoreImpl docStore = new DocumentStoreImpl();
        URI uri1 = new URI("http://www.yu.edu/documents/doc1");
        byte[] myData1 = "First doc in btree".getBytes();
        InputStream input1 = new ByteArrayInputStream(myData1);
        //Add doc1 to the store
        docStore.put(input1, uri1, DocumentStore.DocumentFormat.TXT);
        //Add a second doc to the store
        URI uri2 = new URI("http://www.yu.edu/documents/doc2");
        byte[] myData2 = "Second doc in btree".getBytes();
        InputStream input2 = new ByteArrayInputStream(myData2);
        docStore.put(input2, uri2, DocumentStore.DocumentFormat.TXT);
        //Set their metadata
        assertNull(docStore.setMetadata(uri2, "MetaKey1", "MetaValue1"));
        docStore.setMetadata(uri1, "MetaKey1", "MetaValue1");
        //Set the limit of docs to 1, booting doc 2 to disk
        docStore.setMaxDocumentCount(1);
        //Call get metadata on doc 2, bringing it back into memory and booting doc 1 to disk
        assertEquals("MetaValue1", docStore.getMetadata(uri2, "MetaKey1"));
    }

    @Test
    void getAndSetMetadataWithUndosInMemory() throws URISyntaxException, IOException {
        //Add two docs to the store
        DocumentStoreImpl docStore = new DocumentStoreImpl();
        URI uri1 = new URI("http://www.yu.edu/documents/doc1");
        byte[] myData1 = "First doc in btree".getBytes();
        InputStream input1 = new ByteArrayInputStream(myData1);
        //Add doc1 to the store
        docStore.put(input1, uri1, DocumentStore.DocumentFormat.TXT);
        //Add a second doc to the store
        URI uri2 = new URI("http://www.yu.edu/documents/doc2");
        byte[] myData2 = "Second doc in btree".getBytes();
        InputStream input2 = new ByteArrayInputStream(myData2);
        docStore.put(input2, uri2, DocumentStore.DocumentFormat.TXT);
        //Set their metadata
        assertNull(docStore.setMetadata(uri2, "MetaKey1", "MetaValue1"));
        docStore.setMetadata(uri1, "MetaKey1", "MetaValue1");
        //call undo on uri2 which should revert its metadata to nothing
        docStore.undo(uri2);
        //Now the most recently "used" doc was doc2, so a limit should boot 1 to disk
        docStore.setMaxDocumentCount(1);
        assertNull(docStore.getMetadata(uri2, "MetaKey1"));
    }

    @Test
    void getAndSetMetadataWithUndosFromDisk() throws URISyntaxException, IOException {
        //Add two docs to the store
        DocumentStoreImpl docStore = new DocumentStoreImpl();
        URI uri1 = new URI("http://www.yu.edu/documents/doc1");
        byte[] myData1 = "First doc in btree".getBytes();
        InputStream input1 = new ByteArrayInputStream(myData1);
        //Add doc1 to the store
        docStore.put(input1, uri1, DocumentStore.DocumentFormat.TXT);
        //Add a second doc to the store
        URI uri2 = new URI("http://www.yu.edu/documents/doc2");
        byte[] myData2 = "Second doc in btree".getBytes();
        InputStream input2 = new ByteArrayInputStream(myData2);
        docStore.put(input2, uri2, DocumentStore.DocumentFormat.TXT);
        //Set their metadata
        assertNull(docStore.setMetadata(uri2, "MetaKey1", "MetaValue1"));
        docStore.setMetadata(uri1, "MetaKey1", "MetaValue1");
        //Set the limit of docs to 1, booting doc 2 to disk
        docStore.setMaxDocumentCount(1);
        //call undo on uri2 which should revert its metadata to nothing, bring it back to memory and boot doc1 to disk
        docStore.undo(uri2);
        assertNull(docStore.getMetadata(uri2, "MetaKey1"));
    }

    @Test
    void putOverwriteDocOnDisk() throws URISyntaxException, IOException {
        //Add three docs to the store
        DocumentStoreImpl docStore = new DocumentStoreImpl();
        URI uri1 = new URI("http://www.yu.edu/documents/doc1");
        byte[] myData1 = "First doc in btree".getBytes();
        InputStream input1 = new ByteArrayInputStream(myData1);
        //Add doc1 to the store
        docStore.put(input1, uri1, DocumentStore.DocumentFormat.TXT);
        //Add a second doc to the store
        URI uri2 = new URI("http://www.yu.edu/documents/doc2");
        byte[] myData2 = "Second doc in btree".getBytes();
        InputStream input2 = new ByteArrayInputStream(myData2);
        docStore.put(input2, uri2, DocumentStore.DocumentFormat.TXT);
        //And a third
        URI uri3 = new URI("http://www.yu.edu/documents/doc3");
        byte[] myData3 = "Third doc in btree".getBytes();
        InputStream input3 = new ByteArrayInputStream(myData3);
        docStore.put(input3, uri3, DocumentStore.DocumentFormat.TXT);
        //Set the limit to 2 docs moving 1 to disk
        docStore.setMaxDocumentCount(2);
        //Overwrite doc 1 with doc 4, putting 4 in memory, deleting 1 from memory, and pushing 2 to disk
        byte[] myData4 = "Fourth doc in btree".getBytes();
        InputStream input4 = new ByteArrayInputStream(myData4);
        docStore.put(input4, uri1, DocumentStore.DocumentFormat.TXT);
        //A call to get(uri1) should return doc4. Doc1 should be deleted from disk
        DocumentImpl doc4Copy = new DocumentImpl(uri1, "Fourth doc in btree", null);
        assertEquals(doc4Copy, docStore.get(uri1));
    }

    @Test
    void putTestWithUndos() throws URISyntaxException, IOException {
        //Add two docs to the store
        DocumentStoreImpl docStore = new DocumentStoreImpl();
        URI uri1 = new URI("http://www.yu.edu/documents/doc1");
        byte[] myData1 = "First doc in btree".getBytes();
        InputStream input1 = new ByteArrayInputStream(myData1);
        //Add doc1 to the store
        docStore.put(input1, uri1, DocumentStore.DocumentFormat.TXT);
        //Add a second doc to the store
        URI uri2 = new URI("http://www.yu.edu/documents/doc2");
        byte[] myData2 = "Second doc in btree".getBytes();
        InputStream input2 = new ByteArrayInputStream(myData2);
        docStore.put(input2, uri2, DocumentStore.DocumentFormat.TXT);
        //Call undo() which should remove doc2 from the store
        docStore.undo();
        assertNull(docStore.get(uri2));
        //Add doc 2 back in
        InputStream input2A = new ByteArrayInputStream(myData2);
        docStore.put(input2A, uri2, DocumentStore.DocumentFormat.TXT);
        //Set limit to 1 doc pushing doc 1 to disk
        docStore.setMaxDocumentCount(1);
        //Call undo on uri1 which should delete it from the store and off disk
        docStore.undo(uri1);
    }

    @Test
    void putTestWithUndosAndOverwrites() throws URISyntaxException, IOException {
        DocumentStoreImpl docStore = new DocumentStoreImpl();
        URI uri1 = new URI("http://www.yu.edu/documents/doc1");
        byte[] myData1 = "First doc in btree".getBytes();
        InputStream input1 = new ByteArrayInputStream(myData1);
        //Add doc1 to the store
        docStore.put(input1, uri1, DocumentStore.DocumentFormat.TXT);
        //Overwrite doc one with doc1A
        byte[] myData1A = "First A doc in btree".getBytes();
        InputStream input1A = new ByteArrayInputStream(myData1A);
        docStore.put(input1A, uri1, DocumentStore.DocumentFormat.TXT);
        //Add a second doc to the store
        URI uri2 = new URI("http://www.yu.edu/documents/doc2");
        byte[] myData2 = "Second doc in btree".getBytes();
        InputStream input2 = new ByteArrayInputStream(myData2);
        docStore.put(input2, uri2, DocumentStore.DocumentFormat.TXT);
        //Set doc limit to 1 booting uri 1 to disk
        docStore.setMaxDocumentCount(1);
        //Call undo on uri 1, putting the initial doc1 into memory and booting doc2 to disk
        docStore.undo(uri1);
        DocumentImpl doc1Copy = new DocumentImpl(uri1, "First doc in btree", null);
        assertEquals(doc1Copy, docStore.get(uri1));
    }

    @Test
    void putTestWithCustomBase() throws URISyntaxException, IOException {
        File customBase = new File(System.getProperty("user.dir"), File.separator + "customBase");
        DocumentStoreImpl docStore = new DocumentStoreImpl(customBase);
        URI uri1 = new URI("http://www.yu.edu/documents/doc1");
        byte[] myData1 = "First doc in btree".getBytes();
        InputStream input1 = new ByteArrayInputStream(myData1);
        //Add doc1 to the store
        docStore.put(input1, uri1, DocumentStore.DocumentFormat.TXT);
        //Overwrite doc one with doc1A
        byte[] myData1A = "First A doc in btree".getBytes();
        InputStream input1A = new ByteArrayInputStream(myData1A);
        docStore.put(input1A, uri1, DocumentStore.DocumentFormat.TXT);
        //Add a second doc to the store
        URI uri2 = new URI("http://www.yu.edu/documents/doc2");
        byte[] myData2 = "Second doc in btree".getBytes();
        InputStream input2 = new ByteArrayInputStream(myData2);
        docStore.put(input2, uri2, DocumentStore.DocumentFormat.TXT);
        //Set doc limit to 1 booting uri 1 to disk
        docStore.setMaxDocumentCount(1);
        //Call undo on uri 1, putting the initial doc1 into memory and booting doc2 to disk
        docStore.undo(uri1);
        DocumentImpl doc1Copy = new DocumentImpl(uri1, "First doc in btree", null);
        assertEquals(doc1Copy, docStore.get(uri1));
    }

    @Test
    void putWithNullInput() throws URISyntaxException, IOException {
        DocumentStoreImpl docStore = new DocumentStoreImpl();
        URI uri1 = new URI("http://www.yu.edu/documents/doc1");
        byte[] myData1 = "First doc in btree".getBytes();
        InputStream input1 = new ByteArrayInputStream(myData1);
        //Add doc1 to the store
        docStore.put(input1, uri1, DocumentStore.DocumentFormat.TXT);
        //Add a second doc to the store
        URI uri2 = new URI("http://www.yu.edu/documents/doc2");
        byte[] myData2 = "Second doc in btree".getBytes();
        InputStream input2 = new ByteArrayInputStream(myData2);
        docStore.put(input2, uri2, DocumentStore.DocumentFormat.TXT);
        //Set doc limit to 1 booting uri 1 to disk
        docStore.setMaxDocumentCount(1);
        //Check that calling put with null input and uri not in store returns 0
        URI uriNotInStore = new URI("http://www.yu.edu/documents/doc4A");
        assertEquals(0, docStore.put(null, uriNotInStore, DocumentStore.DocumentFormat.TXT));
        //Call put with a null input on uri1 to delete it from the store
        docStore.put(null, uri1, DocumentStore.DocumentFormat.TXT);
        //Check that it is not in the store
        assertNull(docStore.get(uri1));
        //Check that it cannot be searched in a trie because it has been deleted
        assertEquals(1, docStore.search("btree").size());
        //Check that an undo brings it back into the store and pushed doc 2 to disk
        docStore.undo();
        DocumentImpl doc1Copy = new DocumentImpl(uri1, "First doc in btree", null);
        assertEquals(doc1Copy, docStore.get(uri1));
    }

    @Test
    void getTestWithoutUndosORMemoryLimits() throws URISyntaxException, IOException {
        DocumentStoreImpl docStore = new DocumentStoreImpl();
        URI uri1 = new URI("http://www.yu.edu/documents/doc1");
        byte[] myData1 = "First doc in btree".getBytes();
        InputStream input1 = new ByteArrayInputStream(myData1);
        //Add doc1 to the store
        docStore.put(input1, uri1, DocumentStore.DocumentFormat.TXT);
        DocumentImpl doc1Copy = new DocumentImpl(uri1, "First doc in btree", null);
        //Make sure the get method is returning the doc that was just put in
        assertEquals(doc1Copy, docStore.get(uri1));
        //Add a second doc to the store
        URI uri2 = new URI("http://www.yu.edu/documents/doc2");
        byte[] myData2 = "Second doc in btree".getBytes();
        InputStream input2 = new ByteArrayInputStream(myData2);
        docStore.put(input2, uri2, DocumentStore.DocumentFormat.TXT);
        //Check that its in there
        DocumentImpl doc2Copy = new DocumentImpl(uri2, "Second doc in btree", null);
        assertEquals(doc2Copy, docStore.get(uri2));
        //And a third
        URI uri3 = new URI("http://www.yu.edu/documents/doc3");
        byte[] myData3 = "Third doc in btree".getBytes();
        InputStream input3 = new ByteArrayInputStream(myData3);
        docStore.put(input3, uri3, DocumentStore.DocumentFormat.TXT);
        DocumentImpl doc3Copy = new DocumentImpl(uri3, "Third doc in btree", null);
        assertEquals(doc3Copy, docStore.get(uri3));
    }

    @Test
    void getTestURINotInBTree() throws URISyntaxException, IOException {
        DocumentStoreImpl docStore = new DocumentStoreImpl();
        URI uri1 = new URI("http://www.yu.edu/documents/doc1");
        byte[] myData1 = "First doc in btree".getBytes();
        InputStream input1 = new ByteArrayInputStream(myData1);
        //Add doc1 to the store
        docStore.put(input1, uri1, DocumentStore.DocumentFormat.TXT);
        DocumentImpl doc1Copy = new DocumentImpl(uri1, "First doc in btree", null);
        //Make sure the get method is returning the doc that was just put in
        assertEquals(doc1Copy, docStore.get(uri1));
        //Call get on a uri that isn't in the BTree
        URI uri2 = new URI("http://www.yu.edu/documents/doc2");
        assertNull(docStore.get(uri2));
    }

    @Test
    void getTestBootingOtherDocs() throws URISyntaxException, IOException {
        //Add 3 docs to the store
        DocumentStoreImpl docStore = new DocumentStoreImpl();
        URI uri1 = new URI("http://www.yu.edu/documents/doc1");
        byte[] myData1 = "First doc in btree".getBytes();
        InputStream input1 = new ByteArrayInputStream(myData1);
        //Add doc1 to the store
        docStore.put(input1, uri1, DocumentStore.DocumentFormat.TXT);
        //Add a second doc to the store
        URI uri2 = new URI("http://www.yu.edu/documents/doc2");
        byte[] myData2 = "Second doc in btree".getBytes();
        InputStream input2 = new ByteArrayInputStream(myData2);
        docStore.put(input2, uri2, DocumentStore.DocumentFormat.TXT);
        //And a third
        URI uri3 = new URI("http://www.yu.edu/documents/doc3");
        byte[] myData3 = "Third doc in btree".getBytes();
        InputStream input3 = new ByteArrayInputStream(myData3);
        docStore.put(input3, uri3, DocumentStore.DocumentFormat.TXT);
        //Set the doc limit to 2, booting doc 1 to disk
        docStore.setMaxDocumentCount(2);
        //Call get on doc 1 which should bring it back to memory and push doc 2 to disk
        DocumentImpl doc1Copy = new DocumentImpl(uri1, "First doc in btree", null);
        assertEquals(doc1Copy, docStore.get(uri1));
        //Call get on doc 2 which should bring it back to memory and push doc 3 to disk
        DocumentImpl doc2Copy = new DocumentImpl(uri2, "Second doc in btree", null);
        assertEquals(doc2Copy, docStore.get(uri2));
    }

    @Test
    void deleteFromMemory() throws URISyntaxException, IOException {
        DocumentStoreImpl docStore = new DocumentStoreImpl();
        URI uri1 = new URI("http://www.yu.edu/documents/doc1");
        byte[] myData1 = "First doc in btree".getBytes();
        InputStream input1 = new ByteArrayInputStream(myData1);
        //Before it is added to the store, check that a call to delete returns false
        assertFalse(docStore.delete(uri1));
        //Add doc1 to the store
        docStore.put(input1, uri1, DocumentStore.DocumentFormat.TXT);
        DocumentImpl doc1Copy = new DocumentImpl(uri1, "First doc in btree", null);
        //Make sure the get method is returning the doc that was just put in
        assertEquals(doc1Copy, docStore.get(uri1));
        //Call delete to delete the doc from the BTree and from memory
        assertTrue(docStore.delete(uri1));
        //Check that it is no longer in the BTree
        assertNull(docStore.get(uri1));
    }

    @Test
    void deleteFromDisk() throws URISyntaxException, IOException {
        DocumentStoreImpl docStore = new DocumentStoreImpl();
        URI uri1 = new URI("http://www.yu.edu/documents/doc1");
        byte[] myData1 = "First doc in btree".getBytes();
        InputStream input1 = new ByteArrayInputStream(myData1);
        //Add doc1 to the store
        docStore.put(input1, uri1, DocumentStore.DocumentFormat.TXT);
        //Add a second doc to the store
        URI uri2 = new URI("http://www.yu.edu/documents/doc2");
        byte[] myData2 = "Second doc in btree".getBytes();
        InputStream input2 = new ByteArrayInputStream(myData2);
        docStore.put(input2, uri2, DocumentStore.DocumentFormat.TXT);
        //Set doc limit to 1, moving doc1 to disk
        docStore.setMaxDocumentCount(1);
        //Delete doc 1 from disk
        assertTrue(docStore.delete(uri1));
    }

    @Test
    void deleteWithUndosInMemory() throws URISyntaxException, IOException {
        //Add two docs to the store
        DocumentStoreImpl docStore = new DocumentStoreImpl();
        URI uri1 = new URI("http://www.yu.edu/documents/doc1");
        byte[] myData1 = "First doc in btree".getBytes();
        InputStream input1 = new ByteArrayInputStream(myData1);
        //Add doc1 to the store
        docStore.put(input1, uri1, DocumentStore.DocumentFormat.TXT);
        //Add a second doc to the store
        URI uri2 = new URI("http://www.yu.edu/documents/doc2");
        byte[] myData2 = "Second doc in btree".getBytes();
        InputStream input2 = new ByteArrayInputStream(myData2);
        docStore.put(input2, uri2, DocumentStore.DocumentFormat.TXT);
        //Delete doc 2
        assertTrue(docStore.delete(uri2));
        assertNull(docStore.get(uri2));
        //Call undo and see if it was put back into the store
        docStore.undo();
        DocumentImpl doc2Copy = new DocumentImpl(uri2, "Second doc in btree", null);
        assertEquals(doc2Copy, docStore.get(uri2));
    }

    @Test
    void deleteWithUndosFromDisk() throws URISyntaxException, IOException {
        DocumentStoreImpl docStore = new DocumentStoreImpl();
        URI uri1 = new URI("http://www.yu.edu/documents/doc1");
        byte[] myData1 = "First doc in btree".getBytes();
        InputStream input1 = new ByteArrayInputStream(myData1);
        //Add doc1 to the store
        docStore.put(input1, uri1, DocumentStore.DocumentFormat.TXT);
        //Add a second doc to the store
        URI uri2 = new URI("http://www.yu.edu/documents/doc2");
        byte[] myData2 = "Second doc in btree".getBytes();
        InputStream input2 = new ByteArrayInputStream(myData2);
        docStore.put(input2, uri2, DocumentStore.DocumentFormat.TXT);
        //Set doc limit to 1, moving doc1 to disk
        docStore.setMaxDocumentCount(1);
        //Delete doc 1
        assertTrue(docStore.delete(uri1));
        //Call undo on uri1 which should bring it back into memory and boot doc 2 to disk
        docStore.undo(uri1);
        DocumentImpl doc1Copy = new DocumentImpl(uri1, "First doc in btree", null);
        assertEquals(doc1Copy, docStore.get(uri1));
    }

    @Test
    void undo() {
    }

    @Test
    void testUndo() {
    }

    @Test
    void searchNoMemoryLimits() throws URISyntaxException, IOException {
        DocumentStoreImpl docStore = new DocumentStoreImpl();
        URI uri1 = new URI("http://www.yu.edu/documents/doc1");
        byte[] myData1 = "First doc in btree".getBytes();
        InputStream input1 = new ByteArrayInputStream(myData1);
        //Add doc1 to the store
        docStore.put(input1, uri1, DocumentStore.DocumentFormat.TXT);
        //Add a second doc to the store
        URI uri2 = new URI("http://www.yu.edu/documents/doc2");
        byte[] myData2 = "Second doc in btree".getBytes();
        InputStream input2 = new ByteArrayInputStream(myData2);
        docStore.put(input2, uri2, DocumentStore.DocumentFormat.TXT);
        //And a third
        URI uri3 = new URI("http://www.yu.edu/documents/doc3");
        byte[] myData3 = "Third doc in ".getBytes();
        InputStream input3 = new ByteArrayInputStream(myData3);
        docStore.put(input3, uri3, DocumentStore.DocumentFormat.TXT);
        List<Document> searchedList = docStore.search("btree");
        //Check that that list contains docs 1 and 2
        DocumentImpl doc1Copy = new DocumentImpl(uri1, "First doc in btree", null);
        //assertEquals(doc1Copy, docStore.get(uri1));
        DocumentImpl doc2Copy = new DocumentImpl(uri2, "Second doc in btree", null);
        //assertEquals(doc2Copy, docStore.get(uri2));
        assertTrue(searchedList.contains(doc1Copy));
        assertTrue(searchedList.contains(doc2Copy));
        //Check that doc3 is booted if memory limit is set because docs 1 and 2 were recently returned in search
        docStore.setMaxDocumentCount(2);
    }

    @Test
    void searchWithMemoryLimits() throws URISyntaxException, IOException {
        DocumentStoreImpl docStore = new DocumentStoreImpl();
        URI uri1 = new URI("http://www.yu.edu/documents/doc1");
        byte[] myData1 = "First doc in btree".getBytes();
        InputStream input1 = new ByteArrayInputStream(myData1);
        //Add doc1 to the store
        docStore.put(input1, uri1, DocumentStore.DocumentFormat.TXT);
        //Add a second doc to the store
        URI uri2 = new URI("http://www.yu.edu/documents/doc2");
        byte[] myData2 = "Second doc in btree".getBytes();
        InputStream input2 = new ByteArrayInputStream(myData2);
        docStore.put(input2, uri2, DocumentStore.DocumentFormat.TXT);
        //And a third
        URI uri3 = new URI("http://www.yu.edu/documents/doc3");
        byte[] myData3 = "Third doc in ".getBytes();
        InputStream input3 = new ByteArrayInputStream(myData3);
        docStore.put(input3, uri3, DocumentStore.DocumentFormat.TXT);
        //Set limit to 2 -- booting doc1 to disk
        docStore.setMaxDocumentCount(2);
        //Call a search that should return docs 1 and 2 and boot doc 3 to disk
        List<Document> searchedList = docStore.search("btree");
        //Check that that list contains docs 1 and 2
        DocumentImpl doc1Copy = new DocumentImpl(uri1, "First doc in btree", null);
        DocumentImpl doc2Copy = new DocumentImpl(uri2, "Second doc in btree", null);
        assertTrue(searchedList.contains(doc1Copy));
        assertTrue(searchedList.contains(doc2Copy));
        //Check that a search on a keyword that doesn't exist returns an empty list
        assertEquals(0, docStore.search("NonKeyword").size());
    }

    @Test
    void searchByPrefixNoMemoryLimits() throws URISyntaxException, IOException {
        DocumentStoreImpl docStore = new DocumentStoreImpl();
        URI uri1 = new URI("http://www.yu.edu/documents/doc1");
        byte[] myData1 = "First doc in btree".getBytes();
        InputStream input1 = new ByteArrayInputStream(myData1);
        //Add doc1 to the store
        docStore.put(input1, uri1, DocumentStore.DocumentFormat.TXT);
        //Add a second doc to the store
        URI uri2 = new URI("http://www.yu.edu/documents/doc2");
        byte[] myData2 = "Second doc in btree".getBytes();
        InputStream input2 = new ByteArrayInputStream(myData2);
        docStore.put(input2, uri2, DocumentStore.DocumentFormat.TXT);
        //And a third
        URI uri3 = new URI("http://www.yu.edu/documents/doc3");
        byte[] myData3 = "Third doc in ".getBytes();
        InputStream input3 = new ByteArrayInputStream(myData3);
        docStore.put(input3, uri3, DocumentStore.DocumentFormat.TXT);
        List<Document> searchedList = docStore.searchByPrefix("bt");
        //Check that that list contains docs 1 and 2
        DocumentImpl doc1Copy = new DocumentImpl(uri1, "First doc in btree", null);
        //assertEquals(doc1Copy, docStore.get(uri1));
        DocumentImpl doc2Copy = new DocumentImpl(uri2, "Second doc in btree", null);
        //assertEquals(doc2Copy, docStore.get(uri2));
        assertTrue(searchedList.contains(doc1Copy));
        assertTrue(searchedList.contains(doc2Copy));
        //Check that doc3 is booted if memory limit is set because docs 1 and 2 were recently returned in search
        docStore.setMaxDocumentCount(2);
    }

    @Test
    void searchByPrefixWithMemoryLimits() throws URISyntaxException, IOException {
        DocumentStoreImpl docStore = new DocumentStoreImpl();
        URI uri1 = new URI("http://www.yu.edu/documents/doc1");
        byte[] myData1 = "First doc in btree".getBytes();
        InputStream input1 = new ByteArrayInputStream(myData1);
        //Add doc1 to the store
        docStore.put(input1, uri1, DocumentStore.DocumentFormat.TXT);
        //Add a second doc to the store
        URI uri2 = new URI("http://www.yu.edu/documents/doc2");
        byte[] myData2 = "Second doc in btree".getBytes();
        InputStream input2 = new ByteArrayInputStream(myData2);
        docStore.put(input2, uri2, DocumentStore.DocumentFormat.TXT);
        //And a third
        URI uri3 = new URI("http://www.yu.edu/documents/doc3");
        byte[] myData3 = "Third doc in ".getBytes();
        InputStream input3 = new ByteArrayInputStream(myData3);
        docStore.put(input3, uri3, DocumentStore.DocumentFormat.TXT);
        //Set limit to 2 -- booting doc1 to disk
        docStore.setMaxDocumentCount(2);
        //Call a search that should return docs 1 and 2 and boot doc 3 to disk
        List<Document> searchedList = docStore.searchByPrefix("bt");
        //Check that that list contains docs 1 and 2
        DocumentImpl doc1Copy = new DocumentImpl(uri1, "First doc in btree", null);
        DocumentImpl doc2Copy = new DocumentImpl(uri2, "Second doc in btree", null);
        assertTrue(searchedList.contains(doc1Copy));
        assertTrue(searchedList.contains(doc2Copy));
        //Check that a search on a keyword that doesn't exist returns an empty list
        assertEquals(0, docStore.searchByPrefix("Abc").size());
    }

    @Test
    void deleteAllFromMemory() throws URISyntaxException, IOException{
        DocumentStoreImpl docStore = new DocumentStoreImpl();
        URI uri1 = new URI("http://www.yu.edu/documents/doc1");
        byte[] myData1 = "First doc in btree".getBytes();
        InputStream input1 = new ByteArrayInputStream(myData1);
        //Add doc1 to the store
        docStore.put(input1, uri1, DocumentStore.DocumentFormat.TXT);
        //Add a second doc to the store
        URI uri2 = new URI("http://www.yu.edu/documents/doc2");
        byte[] myData2 = "Second doc in btree".getBytes();
        InputStream input2 = new ByteArrayInputStream(myData2);
        docStore.put(input2, uri2, DocumentStore.DocumentFormat.TXT);
        //And a third
        URI uri3 = new URI("http://www.yu.edu/documents/doc3");
        byte[] myData3 = "Third doc in ".getBytes();
        InputStream input3 = new ByteArrayInputStream(myData3);
        docStore.put(input3, uri3, DocumentStore.DocumentFormat.TXT);
        //Delete docs 1 and 2 from everywhere with a call to deleteAll
        Set<URI> deletedDocsURIs = docStore.deleteAll("btree");
        //Check that that Set contains uris 1 and 2
        assertTrue(deletedDocsURIs.contains(uri1));
        assertTrue(deletedDocsURIs.contains(uri2));
        //Check that they are not in the store anymore
        assertNotNull(docStore.get(uri3));
        //assertNull(docStore.get(uri1));
        //assertNull(docStore.get(uri2));
        //Check that you cannot search them in a trie
        assertEquals(0, docStore.search("btree").size());
        //Check that after a doc limit is set to 1, doc 3 is still in memory
        docStore.setMaxDocumentCount(1);
    }

    @Test
    void deleteAllFromDisk() throws URISyntaxException, IOException{
        DocumentStoreImpl docStore = new DocumentStoreImpl();
        URI uri1 = new URI("http://www.yu.edu/documents/doc1");
        byte[] myData1 = "First doc in btree".getBytes();
        InputStream input1 = new ByteArrayInputStream(myData1);
        //Add doc1 to the store
        docStore.put(input1, uri1, DocumentStore.DocumentFormat.TXT);
        //Add a second doc to the store
        URI uri2 = new URI("http://www.yu.edu/documents/doc2");
        byte[] myData2 = "Second doc in btree".getBytes();
        InputStream input2 = new ByteArrayInputStream(myData2);
        docStore.put(input2, uri2, DocumentStore.DocumentFormat.TXT);
        //And a third
        URI uri3 = new URI("http://www.yu.edu/documents/doc3");
        byte[] myData3 = "Third doc in ".getBytes();
        InputStream input3 = new ByteArrayInputStream(myData3);
        docStore.put(input3, uri3, DocumentStore.DocumentFormat.TXT);
        //Set memory limit to 2 docs, pushing doc 1 to disk
        docStore.setMaxDocumentCount(2);
        //Delete docs 1 and 2 from everywhere with a call to deleteAll
        Set<URI> deletedDocsURIs = docStore.deleteAll("btree");
        //Check that that Set contains uris 1 and 2
        assertTrue(deletedDocsURIs.contains(uri1));
        assertTrue(deletedDocsURIs.contains(uri2));
        //Check that they are not in the store anymore
        assertNotNull(docStore.get(uri3));
        assertNull(docStore.get(uri1));
        //assertNull(docStore.get(uri2));
        //Check that you cannot search them in a trie
        assertEquals(0, docStore.search("btree").size());
    }

    @Test
    void deleteAllWithUndosInMemory() throws URISyntaxException, IOException{
        DocumentStoreImpl docStore = new DocumentStoreImpl();
        URI uri1 = new URI("http://www.yu.edu/documents/doc1");
        byte[] myData1 = "First doc in btree".getBytes();
        InputStream input1 = new ByteArrayInputStream(myData1);
        //Add doc1 to the store
        docStore.put(input1, uri1, DocumentStore.DocumentFormat.TXT);
        //Add a second doc to the store
        URI uri2 = new URI("http://www.yu.edu/documents/doc2");
        byte[] myData2 = "Second doc in btree".getBytes();
        InputStream input2 = new ByteArrayInputStream(myData2);
        docStore.put(input2, uri2, DocumentStore.DocumentFormat.TXT);
        //And a third
        URI uri3 = new URI("http://www.yu.edu/documents/doc3");
        byte[] myData3 = "Third doc in ".getBytes();
        InputStream input3 = new ByteArrayInputStream(myData3);
        docStore.put(input3, uri3, DocumentStore.DocumentFormat.TXT);
        //Delete docs 1 and 2 from everywhere with a call to deleteAll
        Set<URI> deletedDocsURIs = docStore.deleteAll("btree");
        //Check that that Set contains uris 1 and 2
        assertTrue(deletedDocsURIs.contains(uri1));
        assertTrue(deletedDocsURIs.contains(uri2));
        //Check that you cannot search them in a trie
        assertEquals(0, docStore.search("btree").size());
        //Call undo, which should undo the bulk deleteAll and bring docs1 and 2 back
        docStore.undo();
        //Check that they are both back in the store
        /*DocumentImpl doc1Copy = new DocumentImpl(uri1, "First doc in btree", null);
        assertEquals(doc1Copy, docStore.get(uri1));
        DocumentImpl doc2Copy = new DocumentImpl(uri2, "Second doc in btree", null);
        assertEquals(doc2Copy, docStore.get(uri2));*/
        //Check that you can search them in a trie
        //assertEquals(2, docStore.search("btree").size());
        //Check that doc3 is booted if memory limit is set because docs 1 and 2 were recently returned in undo
        docStore.setMaxDocumentCount(2);
    }

    @Test
    void deleteAllWithUndosFromDisk() throws URISyntaxException, IOException{
        DocumentStoreImpl docStore = new DocumentStoreImpl();
        URI uri1 = new URI("http://www.yu.edu/documents/doc1");
        byte[] myData1 = "First doc in btree".getBytes();
        InputStream input1 = new ByteArrayInputStream(myData1);
        //Add doc1 to the store
        docStore.put(input1, uri1, DocumentStore.DocumentFormat.TXT);
        //Add a second doc to the store
        URI uri2 = new URI("http://www.yu.edu/documents/doc2");
        byte[] myData2 = "Second doc in btree".getBytes();
        InputStream input2 = new ByteArrayInputStream(myData2);
        docStore.put(input2, uri2, DocumentStore.DocumentFormat.TXT);
        //And a third
        URI uri3 = new URI("http://www.yu.edu/documents/doc3");
        byte[] myData3 = "Third doc in ".getBytes();
        InputStream input3 = new ByteArrayInputStream(myData3);
        docStore.put(input3, uri3, DocumentStore.DocumentFormat.TXT);
        //Set memory limit to 2 docs, pushing doc 1 to disk
        docStore.setMaxDocumentCount(2);
        //Delete docs 1 and 2 from everywhere with a call to deleteAll
        Set<URI> deletedDocsURIs = docStore.deleteAll("btree");
        //Check that that Set contains uris 1 and 2
        assertTrue(deletedDocsURIs.contains(uri1));
        assertTrue(deletedDocsURIs.contains(uri2));
        //Call undo, which should undo the bulk deleteAll and bring docs1 and 2 back
        docStore.undo();
        //Check that they are both back in the store
        DocumentImpl doc1Copy = new DocumentImpl(uri1, "First doc in btree", null);
        assertEquals(doc1Copy, docStore.get(uri1));
        DocumentImpl doc2Copy = new DocumentImpl(uri2, "Second doc in btree", null);
        assertEquals(doc2Copy, docStore.get(uri2));
        //Check that you can search them in a trie
        assertEquals(2, docStore.search("btree").size());
    }

    @Test
    void deleteAllWithSingleUndosFromDisk() throws URISyntaxException, IOException{
        DocumentStoreImpl docStore = new DocumentStoreImpl();
        URI uri1 = new URI("http://www.yu.edu/documents/doc1");
        byte[] myData1 = "First doc in btree".getBytes();
        InputStream input1 = new ByteArrayInputStream(myData1);
        //Add doc1 to the store
        docStore.put(input1, uri1, DocumentStore.DocumentFormat.TXT);
        //Add a second doc to the store
        URI uri2 = new URI("http://www.yu.edu/documents/doc2");
        byte[] myData2 = "Second doc in btree".getBytes();
        InputStream input2 = new ByteArrayInputStream(myData2);
        docStore.put(input2, uri2, DocumentStore.DocumentFormat.TXT);
        //And a third
        URI uri3 = new URI("http://www.yu.edu/documents/doc3");
        byte[] myData3 = "Third doc in ".getBytes();
        InputStream input3 = new ByteArrayInputStream(myData3);
        docStore.put(input3, uri3, DocumentStore.DocumentFormat.TXT);
        //Set memory limit to 2 docs, pushing doc 1 to disk
        docStore.setMaxDocumentCount(2);
        //Delete docs 1 and 2 from everywhere with a call to deleteAll
        Set<URI> deletedDocsURIs = docStore.deleteAll("btree");
        //Add a fourth doc to the store, so there are 2 currently in memory
        URI uri4 = new URI("http://www.yu.edu/documents/doc4");
        byte[] myData4 = "Fourth doc in btree".getBytes();
        InputStream input4 = new ByteArrayInputStream(myData4);
        docStore.put(input4, uri4, DocumentStore.DocumentFormat.TXT);
        //Check that an undo on uri1 brings it back into the store and boots doc 3 to disk
        docStore.undo(uri1);
        DocumentImpl doc1Copy = new DocumentImpl(uri1, "First doc in btree", null);
        assertEquals(doc1Copy, docStore.get(uri1));
    }

    @Test
    void deleteAllWithPrefixTest() throws URISyntaxException, IOException {
        DocumentStoreImpl docStore = new DocumentStoreImpl();
        URI uri1 = new URI("http://www.yu.edu/documents/doc1");
        byte[] myData1 = "First doc in btree".getBytes();
        InputStream input1 = new ByteArrayInputStream(myData1);
        //Add doc1 to the store
        docStore.put(input1, uri1, DocumentStore.DocumentFormat.TXT);
        //Add a second doc to the store
        URI uri2 = new URI("http://www.yu.edu/documents/doc2");
        byte[] myData2 = "Second doc in ".getBytes();
        InputStream input2 = new ByteArrayInputStream(myData2);
        docStore.put(input2, uri2, DocumentStore.DocumentFormat.TXT);
        //And a third
        URI uri3 = new URI("http://www.yu.edu/documents/doc3");
        byte[] myData3 = "Third doc in btree".getBytes();
        InputStream input3 = new ByteArrayInputStream(myData3);
        docStore.put(input3, uri3, DocumentStore.DocumentFormat.TXT);
        //Set memory limit to 2 docs, pushing doc 1 to disk
        docStore.setMaxDocumentCount(2);
        //Delete docs 1 and 3 from everywhere with a call to deleteAllWithPrefix
        Set<URI> deletedDocsWithPrefixURIs = docStore.deleteAllWithPrefix("bt");
        //Check that that Set contains uris 1 and 2
        assertTrue(deletedDocsWithPrefixURIs.contains(uri1));
        assertTrue(deletedDocsWithPrefixURIs.contains(uri3));
        //Check that you cannot search them in a trie
        assertEquals(0, docStore.searchByPrefix("bt").size());
        //Add a fourth doc to the store, so there are 2 currently in memory
        URI uri4 = new URI("http://www.yu.edu/documents/doc4");
        byte[] myData4 = "Fourth doc in btree".getBytes();
        InputStream input4 = new ByteArrayInputStream(myData4);
        docStore.put(input4, uri4, DocumentStore.DocumentFormat.TXT);
        //Check that an undo on uri1 brings it back into the store and boots doc 2 to disk
        docStore.undo(uri1);
        //Call undo twice to bring doc 3 back into the store
        docStore.undo();
        docStore.undo();
        DocumentImpl doc3Copy = new DocumentImpl(uri3, "Third doc in btree", null);
        assertEquals(doc3Copy, docStore.get(uri3));
    }

    @Test
    void searchByMetadataTest() throws URISyntaxException, IOException {
        //Add three docs in the BTree
        DocumentStoreImpl docStore = new DocumentStoreImpl();
        URI uri1 = new URI("http://www.yu.edu/documents/doc1");
        byte[] myData1 = "First doc in btree".getBytes();
        InputStream input1 = new ByteArrayInputStream(myData1);
        //Add doc1 to the store
        docStore.put(input1, uri1, DocumentStore.DocumentFormat.TXT);
        //Add a second doc to the store
        URI uri2 = new URI("http://www.yu.edu/documents/doc2");
        byte[] myData2 = "Second doc in btree".getBytes();
        InputStream input2 = new ByteArrayInputStream(myData2);
        docStore.put(input2, uri2, DocumentStore.DocumentFormat.TXT);
        //And a third
        URI uri3 = new URI("http://www.yu.edu/documents/doc3");
        byte[] myData3 = "Third doc in btree".getBytes();
        InputStream input3 = new ByteArrayInputStream(myData3);
        docStore.put(input3, uri3, DocumentStore.DocumentFormat.TXT);
        //Set their metadata
        docStore.setMetadata(uri1, "MetaKey1", "MetaValue1");
        docStore.setMetadata(uri2, "MetaKey1", "MetaValue1");
        docStore.setMetadata(uri3, "MetaKey1", "MetaValue1");
        docStore.setMetadata(uri1, "MetaKey2", "MetaValue2");
        docStore.setMetadata(uri2, "MetaKey2", "MetaValue2");
        docStore.setMetadata(uri1, "MetaKey3", "MetaValue3");
        HashMap<String, String> keysValues = new HashMap<>();
        keysValues.put("MetaKey1", "MetaValue1");
        keysValues.put("MetaKey2", "MetaValue2");
        keysValues.put("MetaKey3", "MetaValue3");
        List<Document> docsWithMetadata = docStore.searchByMetadata(keysValues);
        //Make copies of all the documents
        DocumentImpl doc1Copy = new DocumentImpl(uri1, "First doc in btree", null);
        DocumentImpl doc2Copy = new DocumentImpl(uri2, "Second doc in btree", null);
        DocumentImpl doc3Copy = new DocumentImpl(uri3, "Third doc in btree", null);
        assertTrue(docsWithMetadata.contains(doc1Copy));
        //assertTrue(docsWithMetadata.contains(doc2Copy));
        //assertTrue(docsWithMetadata.contains(doc3Copy));
        assertEquals(1, docsWithMetadata.size());
    }

    @Test
    void searchByMetadataWithMemoryLimits() throws URISyntaxException, IOException {
        //Add three docs in the BTree
        DocumentStoreImpl docStore = new DocumentStoreImpl();
        URI uri1 = new URI("http://www.yu.edu/documents/doc1");
        byte[] myData1 = "First doc in btree".getBytes();
        InputStream input1 = new ByteArrayInputStream(myData1);
        //Add doc1 to the store
        docStore.put(input1, uri1, DocumentStore.DocumentFormat.TXT);
        //Add a second doc to the store
        URI uri2 = new URI("http://www.yu.edu/documents/doc2");
        byte[] myData2 = {1, 2, 3, 4};
        InputStream input2 = new ByteArrayInputStream(myData2);
        docStore.put(input2, uri2, DocumentStore.DocumentFormat.BINARY);
        //And a third
        URI uri3 = new URI("http://www.yu.edu/documents/doc3");
        byte[] myData3 = "Third doc in btree".getBytes();
        InputStream input3 = new ByteArrayInputStream(myData3);
        docStore.put(input3, uri3, DocumentStore.DocumentFormat.TXT);
        //Set their metadata
        docStore.setMetadata(uri1, "MetaKey1", "MetaValue1");
        docStore.setMetadata(uri1, "MetaKey2", "MetaValue2");
        docStore.setMetadata(uri1, "MetaKey3", "MetaValue3");
        docStore.setMetadata(uri2, "MetaKey1", "MetaValue1");
        docStore.setMetadata(uri2, "MetaKey2", "MetaValue2");
        docStore.setMetadata(uri3, "MetaKey1", "MetaValue1");
        HashMap<String, String> keysValues = new HashMap<>();
        keysValues.put("MetaKey1", "MetaValue1");
        keysValues.put("MetaKey2", "MetaValue2");
        //keysValues.put("MetaKey3", "MetaValue3");
        //Set doc limit to 2, pushing 1 to disk
        docStore.setMaxDocumentCount(2);
        //Search docs 1 and 2 by metadata, pushing doc 3 to disk
        List<Document> docsWithMetadata = docStore.searchByMetadata(keysValues);
        //Make copies of all the documents
        DocumentImpl doc1Copy = new DocumentImpl(uri1, "First doc in btree", null);
        DocumentImpl doc2Copy = new DocumentImpl(uri2, myData2);
        DocumentImpl doc3Copy = new DocumentImpl(uri3, "Third doc in btree", null);
        assertTrue(docsWithMetadata.contains(doc1Copy));
        assertTrue(docsWithMetadata.contains(doc2Copy));
        //assertTrue(docsWithMetadata.contains(doc3Copy));
        assertEquals(2, docsWithMetadata.size());
        //Call undo on doc 3 to revert its metadata and bring it back from disk
        docStore.undo(uri3);
    }

    @Test
    void searchByMetadataReverting() throws URISyntaxException, IOException {
        //Add three docs in the BTree
        DocumentStoreImpl docStore = new DocumentStoreImpl();
        URI uri1 = new URI("http://www.yu.edu/documents/doc1");
        byte[] myData1 = "First doc in btree".getBytes();
        InputStream input1 = new ByteArrayInputStream(myData1);
        //Add doc1 to the store
        docStore.put(input1, uri1, DocumentStore.DocumentFormat.TXT);
        //Add a second doc to the store
        URI uri2 = new URI("http://www.yu.edu/documents/doc2");
        byte[] myData2 = {1, 2, 3, 4};
        InputStream input2 = new ByteArrayInputStream(myData2);
        docStore.put(input2, uri2, DocumentStore.DocumentFormat.BINARY);
        //And a third
        URI uri3 = new URI("http://www.yu.edu/documents/doc3");
        byte[] myData3 = "Third doc in btree".getBytes();
        InputStream input3 = new ByteArrayInputStream(myData3);
        docStore.put(input3, uri3, DocumentStore.DocumentFormat.TXT);
        //Set their metadata
        docStore.setMetadata(uri1, "MetaKey1", "MetaValue1");
        docStore.setMetadata(uri1, "MetaKey2", "MetaValue2");
        docStore.setMetadata(uri1, "MetaKey3", "MetaValue3");
        docStore.setMetadata(uri2, "MetaKey1", "MetaValue1");
        docStore.setMetadata(uri2, "MetaKey2", "MetaValue2");
        docStore.setMetadata(uri3, "MetaKey1", "MetaValue1");
        HashMap<String, String> keysValues = new HashMap<>();
        keysValues.put("MetaKey1", "MetaValue1");
        //Set limit to 2, pushing one to disk
        docStore.setMaxDocumentCount(2);
        //Overwrite docs one's metadata, bringing it back to memory
        docStore.setMetadata(uri1, "MetaKey1", "OverwrittenValue");
        //Call undo on uri3 to undo its metadata
        docStore.undo(uri3);
        //Search doc 2 by metadata, pushing doc 1 to disk
        List<Document> docsWithMetadata = docStore.searchByMetadata(keysValues);
        //Make a copy of doc 2 and check that it was searched
        DocumentImpl doc2Copy = new DocumentImpl(uri2, myData2);
        assertTrue(docsWithMetadata.contains(doc2Copy));
        assertEquals(1, docsWithMetadata.size());
        //Call undo on uri1 which should revert it back to having MetaValue2, then call search by metadata again
        docStore.undo(uri1);
        assertEquals(2, docStore.searchByMetadata(keysValues).size());
    }

    @Test
    void searchByKeywordAndMetadataTest() throws URISyntaxException, IOException {
        //Add three docs in the BTree
        DocumentStoreImpl docStore = new DocumentStoreImpl();
        URI uri1 = new URI("http://www.yu.edu/documents/doc1");
        byte[] myData1 = "First doc in btree".getBytes();
        InputStream input1 = new ByteArrayInputStream(myData1);
        //Add doc1 to the store
        docStore.put(input1, uri1, DocumentStore.DocumentFormat.TXT);
        //Add a second doc to the store
        URI uri2 = new URI("http://www.yu.edu/documents/doc2");
        byte[] myData2 = "Second doc in btree keyword".getBytes();
        InputStream input2 = new ByteArrayInputStream(myData2);
        docStore.put(input2, uri2, DocumentStore.DocumentFormat.TXT);
        //And a third
        URI uri3 = new URI("http://www.yu.edu/documents/doc3");
        byte[] myData3 = "Third doc in btree".getBytes();
        InputStream input3 = new ByteArrayInputStream(myData3);
        docStore.put(input3, uri3, DocumentStore.DocumentFormat.TXT);
        //Set their metadata
        docStore.setMetadata(uri2, "MetaKey1", "MetaValue1");
        docStore.setMetadata(uri2, "MetaKey2", "MetaValue2");
        docStore.setMetadata(uri1, "MetaKey1", "MetaValue1");
        docStore.setMetadata(uri1, "MetaKey2", "MetaValue2");
        docStore.setMetadata(uri1, "MetaKey3", "MetaValue3");
        docStore.setMetadata(uri3, "MetaKey1", "MetaValue1");
        HashMap<String, String> keysValues = new HashMap<>();
        keysValues.put("MetaKey1", "MetaValue1");
        keysValues.put("MetaKey2", "MetaValue2");
        //Set limit to 2 bumping doc 2 to disk
        docStore.setMaxDocumentCount(2);
        //call search by metadata and keyword to bring doc 2 back in and boot 1
        assertEquals(1, docStore.searchByKeywordAndMetadata("keyword", keysValues).size());
    }

    @Test
    void searchByKeywordAndMetadataOrdered() throws URISyntaxException, IOException {
        //Add three docs in the BTree
        DocumentStoreImpl docStore = new DocumentStoreImpl();
        URI uri1 = new URI("http://www.yu.edu/documents/doc1");
        byte[] myData1 = "First doc in btree keyword keyword".getBytes();
        InputStream input1 = new ByteArrayInputStream(myData1);
        //Add doc1 to the store
        docStore.put(input1, uri1, DocumentStore.DocumentFormat.TXT);
        //Add a second doc to the store
        URI uri2 = new URI("http://www.yu.edu/documents/doc2");
        byte[] myData2 = "Second doc in btree keyword keyword keyword".getBytes();
        InputStream input2 = new ByteArrayInputStream(myData2);
        docStore.put(input2, uri2, DocumentStore.DocumentFormat.TXT);
        //And a third
        URI uri3 = new URI("http://www.yu.edu/documents/doc3");
        byte[] myData3 = "Third doc in btree keyword".getBytes();
        InputStream input3 = new ByteArrayInputStream(myData3);
        docStore.put(input3, uri3, DocumentStore.DocumentFormat.TXT);
        //Set their metadata
        docStore.setMetadata(uri2, "MetaKey1", "MetaValue1");
        docStore.setMetadata(uri2, "MetaKey2", "MetaValue2");
        docStore.setMetadata(uri1, "MetaKey1", "MetaValue1");
        docStore.setMetadata(uri1, "MetaKey2", "MetaValue2");
        docStore.setMetadata(uri1, "MetaKey3", "MetaValue3");
        docStore.setMetadata(uri3, "MetaKey1", "MetaValue1");
        HashMap<String, String> keysValues = new HashMap<>();
        keysValues.put("MetaKey1", "MetaValue1");
        //Set limit to 2 bumping doc 2 to disk
        docStore.setMaxDocumentCount(2);
        List<Document> docsSearched = docStore.searchByKeywordAndMetadata("keyword", keysValues);
        //Check that they are sorted in the proper order --> 2, 1, 3
        //First make copies of the docs
        DocumentImpl doc1Copy = new DocumentImpl(uri1, "First doc in btree keyword keyword", null);
        DocumentImpl doc2Copy = new DocumentImpl(uri2, "Second doc in btree keyword keyword keyword", null);
        DocumentImpl doc3Copy = new DocumentImpl(uri3, "Third doc in btree keyword", null);
        assertEquals(doc2Copy, docsSearched.get(0));
        assertEquals(doc1Copy, docsSearched.get(1));
        assertEquals(doc3Copy, docsSearched.get(2));
    }

    @Test
    void searchByKeywordAndMetadataOnlyHasWord() throws URISyntaxException, IOException {
        //Add three docs in the BTree
        DocumentStoreImpl docStore = new DocumentStoreImpl();
        URI uri1 = new URI("http://www.yu.edu/documents/doc1");
        byte[] myData1 = "First doc in btree keyword keyword".getBytes();
        InputStream input1 = new ByteArrayInputStream(myData1);
        //Add doc1 to the store
        docStore.put(input1, uri1, DocumentStore.DocumentFormat.TXT);
        //Add a second doc to the store
        URI uri2 = new URI("http://www.yu.edu/documents/doc2");
        byte[] myData2 = "Second doc in btree keyword keyword keyword".getBytes();
        InputStream input2 = new ByteArrayInputStream(myData2);
        docStore.put(input2, uri2, DocumentStore.DocumentFormat.TXT);
        //And a third
        URI uri3 = new URI("http://www.yu.edu/documents/doc3");
        byte[] myData3 = "Third doc in btree keyword".getBytes();
        InputStream input3 = new ByteArrayInputStream(myData3);
        docStore.put(input3, uri3, DocumentStore.DocumentFormat.TXT);
        //Set their metadata
        docStore.setMetadata(uri2, "MetaKey1", "MetaValue1");
        docStore.setMetadata(uri2, "MetaKey2", "MetaValue2");
        docStore.setMetadata(uri1, "MetaKey1", "MetaValue1");
        docStore.setMetadata(uri1, "MetaKey2", "MetaValue2");
        docStore.setMetadata(uri1, "MetaKey3", "MetaValue3");
        HashMap<String, String> keysValues = new HashMap<>();
        keysValues.put("MetaKey1", "MetaValue1");
        //Set limit to 2 bumping doc 3 to disk
        docStore.setMaxDocumentCount(2);
        //Call search on docs 1 and 2, and make sure doc 3 isn't brought back from memory b/c it doesn't match metadata
        List<Document> docsSearched = docStore.searchByKeywordAndMetadata("keyword", keysValues);
        //Check that they are sorted in the proper order --> 2, 1
        //First make copies of the docs
        DocumentImpl doc1Copy = new DocumentImpl(uri1, "First doc in btree keyword keyword", null);
        DocumentImpl doc2Copy = new DocumentImpl(uri2, "Second doc in btree keyword keyword keyword", null);
        DocumentImpl doc3Copy = new DocumentImpl(uri3, "Third doc in btree keyword", null);
        assertEquals(doc2Copy, docsSearched.get(0));
        assertEquals(doc1Copy, docsSearched.get(1));
        assertFalse(docsSearched.contains(doc3Copy));
    }

    @Test
    void searchByPrefixAndMetadataTest() throws URISyntaxException, IOException {
        //Add three docs in the BTree
        DocumentStoreImpl docStore = new DocumentStoreImpl();
        URI uri1 = new URI("http://www.yu.edu/documents/doc1");
        byte[] myData1 = "First doc in btree keyword keyword".getBytes();
        InputStream input1 = new ByteArrayInputStream(myData1);
        //Add doc1 to the store
        docStore.put(input1, uri1, DocumentStore.DocumentFormat.TXT);
        //Add a second doc to the store
        URI uri2 = new URI("http://www.yu.edu/documents/doc2");
        byte[] myData2 = "Second doc in btree keyword keyword keys".getBytes();
        InputStream input2 = new ByteArrayInputStream(myData2);
        docStore.put(input2, uri2, DocumentStore.DocumentFormat.TXT);
        //And a third
        URI uri3 = new URI("http://www.yu.edu/documents/doc3");
        byte[] myData3 = "Third doc in btree keyword".getBytes();
        InputStream input3 = new ByteArrayInputStream(myData3);
        docStore.put(input3, uri3, DocumentStore.DocumentFormat.TXT);
        //Add a fourth
        URI uri4 = new URI("http://www.yu.edu/documents/doc4");
        byte[] myData4 = "Fourth doc in btree".getBytes();
        InputStream input4 = new ByteArrayInputStream(myData4);
        docStore.put(input4, uri4, DocumentStore.DocumentFormat.TXT);
        //Set their metadata
        docStore.setMetadata(uri2, "MetaKey1", "MetaValue1");
        docStore.setMetadata(uri2, "MetaKey2", "MetaValue2");
        docStore.setMetadata(uri1, "MetaKey1", "MetaValue1");
        docStore.setMetadata(uri1, "MetaKey2", "MetaValue2");
        docStore.setMetadata(uri1, "MetaKey3", "MetaValue3");
        docStore.setMetadata(uri3, "MetaKey1", "MetaValue1");
        HashMap<String, String> keysValues = new HashMap<>();
        keysValues.put("MetaKey1", "MetaValue1");
        //Set limit to 3 bumping doc 2 to disk
        docStore.setMaxDocumentCount(3);
        //Call search by prefix and metadata on docs 1 and 2
        List<Document> docsSearched = docStore.searchByPrefixAndMetadata("key", keysValues);
        //Check that they are sorted in the proper order --> 2, 1, 3
        //First make copies of the docs
        DocumentImpl doc1Copy = new DocumentImpl(uri1, "First doc in btree keyword keyword", null);
        DocumentImpl doc2Copy = new DocumentImpl(uri2, "Second doc in btree keyword keyword keys", null);
        DocumentImpl doc3Copy = new DocumentImpl(uri3, "Third doc in btree keyword", null);
        assertEquals(doc2Copy, docsSearched.get(0));
        assertEquals(doc1Copy, docsSearched.get(1));
        assertEquals(doc3Copy, docsSearched.get(2));
        //Doc 4 should be on disk
    }

    @Test
    void searchByPrefixAndMetadataOnlyHasPrefix() throws URISyntaxException, IOException {
        //Add three docs in the BTree
        DocumentStoreImpl docStore = new DocumentStoreImpl();
        URI uri1 = new URI("http://www.yu.edu/documents/doc1");
        byte[] myData1 = "First doc in btree keyword keyword".getBytes();
        InputStream input1 = new ByteArrayInputStream(myData1);
        //Add doc1 to the store
        docStore.put(input1, uri1, DocumentStore.DocumentFormat.TXT);
        //Add a second doc to the store
        URI uri2 = new URI("http://www.yu.edu/documents/doc2");
        byte[] myData2 = "Second doc in btree keyword keyword keys".getBytes();
        InputStream input2 = new ByteArrayInputStream(myData2);
        docStore.put(input2, uri2, DocumentStore.DocumentFormat.TXT);
        //And a third
        URI uri3 = new URI("http://www.yu.edu/documents/doc3");
        byte[] myData3 = "Third doc in btree keyword".getBytes();
        InputStream input3 = new ByteArrayInputStream(myData3);
        docStore.put(input3, uri3, DocumentStore.DocumentFormat.TXT);
        //Set their metadata
        docStore.setMetadata(uri2, "MetaKey1", "MetaValue1");
        docStore.setMetadata(uri2, "MetaKey2", "MetaValue2");
        docStore.setMetadata(uri1, "MetaKey1", "MetaValue1");
        docStore.setMetadata(uri1, "MetaKey2", "MetaValue2");
        docStore.setMetadata(uri1, "MetaKey3", "MetaValue3");
        HashMap<String, String> keysValues = new HashMap<>();
        keysValues.put("MetaKey1", "MetaValue1");
        //Set limit to 2 bumping doc 3 to disk
        docStore.setMaxDocumentCount(2);
        //Call search by prefix and metadata on docs 1 and 2
        //Make sure three wasn't brought back into memory just because it matched the prefix
        List<Document> docsSearched = docStore.searchByPrefixAndMetadata("key", keysValues);
        //Check that they are sorted in the proper order --> 2, 1
        //First make copies of the docs
        DocumentImpl doc1Copy = new DocumentImpl(uri1, "First doc in btree keyword keyword", null);
        DocumentImpl doc2Copy = new DocumentImpl(uri2, "Second doc in btree keyword keyword keys", null);
        DocumentImpl doc3Copy = new DocumentImpl(uri3, "Third doc in btree keyword", null);
        assertEquals(doc2Copy, docsSearched.get(0));
        assertEquals(doc1Copy, docsSearched.get(1));
        assertFalse(docsSearched.contains(doc3Copy));
    }

    @Test
    void deleteAllWithMetadataTest() throws URISyntaxException, IOException {
        DocumentStoreImpl docStore = new DocumentStoreImpl();
        URI uri1 = new URI("http://www.yu.edu/documents/doc1");
        byte[] myData1 = "First doc in btree".getBytes();
        InputStream input1 = new ByteArrayInputStream(myData1);
        //Add doc1 to the store
        docStore.put(input1, uri1, DocumentStore.DocumentFormat.TXT);
        //Add a second doc to the store
        URI uri2 = new URI("http://www.yu.edu/documents/doc2");
        byte[] myData2 = "Second doc in btree".getBytes();
        InputStream input2 = new ByteArrayInputStream(myData2);
        docStore.put(input2, uri2, DocumentStore.DocumentFormat.TXT);
        //And a third
        URI uri3 = new URI("http://www.yu.edu/documents/doc3");
        byte[] myData3 = "Third doc in btree".getBytes();
        InputStream input3 = new ByteArrayInputStream(myData3);
        docStore.put(input3, uri3, DocumentStore.DocumentFormat.TXT);
        //Set their metadata
        docStore.setMetadata(uri1, "MetaKey1", "MetaValue1");
        docStore.setMetadata(uri1, "MetaKey2", "MetaValue2");
        docStore.setMetadata(uri1, "MetaKey3", "MetaValue3");
        docStore.setMetadata(uri2, "MetaKey1", "MetaValue1");
        docStore.setMetadata(uri2, "MetaKey2", "MetaValue2");
        docStore.setMetadata(uri3, "MetaKey1", "MetaValue1");
        HashMap<String, String> keysValues = new HashMap<>();
        keysValues.put("MetaKey1", "MetaValue1");
        keysValues.put("MetaKey2", "MetaValue2");
        //Set memory limit to 2 docs, pushing doc 1 to disk
        docStore.setMaxDocumentCount(2);
        //Delete docs 1 and 2 from everywhere with a call to deleteAllWithMetdata
        Set<URI> deletedDocsWithMetadataURIs = docStore.deleteAllWithMetadata(keysValues);
        //Check that that Set contains uris 1 and 2
        assertTrue(deletedDocsWithMetadataURIs.contains(uri1));
        assertTrue(deletedDocsWithMetadataURIs.contains(uri2));
        //Check that you cannot search them in a trie
        assertEquals(0, docStore.searchByMetadata(keysValues).size());
        //Add a fourth doc to the store, so there are 2 currently in memory
        URI uri4 = new URI("http://www.yu.edu/documents/doc4");
        byte[] myData4 = "Fourth doc in btree".getBytes();
        InputStream input4 = new ByteArrayInputStream(myData4);
        docStore.put(input4, uri4, DocumentStore.DocumentFormat.TXT);
        //Check that an undo on uri1 brings it back into the store and boots doc 3 to disk
        docStore.undo(uri1);
        //Call undo twice to bring doc 2 back into the store
        docStore.undo();
        docStore.undo();
        DocumentImpl doc2Copy = new DocumentImpl(uri2, "Second doc in btree", null);
        assertEquals(doc2Copy, docStore.get(uri2));
    }

    @Test
    void deleteAllWithKeywordAndMetadataTest() throws URISyntaxException, IOException {
        DocumentStoreImpl docStore = new DocumentStoreImpl();
        URI uri1 = new URI("http://www.yu.edu/documents/doc1");
        byte[] myData1 = "First doc in btree keyword".getBytes();
        InputStream input1 = new ByteArrayInputStream(myData1);
        //Add doc1 to the store
        docStore.put(input1, uri1, DocumentStore.DocumentFormat.TXT);
        //Add a second doc to the store
        URI uri2 = new URI("http://www.yu.edu/documents/doc2");
        byte[] myData2 = "Second doc in btree".getBytes();
        InputStream input2 = new ByteArrayInputStream(myData2);
        docStore.put(input2, uri2, DocumentStore.DocumentFormat.TXT);
        //And a third
        URI uri3 = new URI("http://www.yu.edu/documents/doc3");
        byte[] myData3 = "Third doc in btree keyword".getBytes();
        InputStream input3 = new ByteArrayInputStream(myData3);
        docStore.put(input3, uri3, DocumentStore.DocumentFormat.TXT);
        //Set their metadata
        docStore.setMetadata(uri1, "MetaKey1", "MetaValue1");
        docStore.setMetadata(uri1, "MetaKey2", "MetaValue2");
        docStore.setMetadata(uri1, "MetaKey3", "MetaValue3");
        docStore.setMetadata(uri2, "MetaKey1", "MetaValue1");
        docStore.setMetadata(uri2, "MetaKey2", "MetaValue2");
        docStore.setMetadata(uri3, "MetaKey1", "MetaValue1");
        HashMap<String, String> keysValues = new HashMap<>();
        keysValues.put("MetaKey1", "MetaValue1");
        //Set memory limit to 2 docs, pushing doc 1 to disk
        docStore.setMaxDocumentCount(2);
        //Delete docs 1 and 3 from everywhere with a call to deleteAllWithKeywordAndMetdata
        Set<URI> deletedDocsWithKeywordAndMetadataURIs = docStore.deleteAllWithKeywordAndMetadata("keyword", keysValues);
        //Check that that Set contains uris 1 and 3
        assertTrue(deletedDocsWithKeywordAndMetadataURIs.contains(uri1));
        assertTrue(deletedDocsWithKeywordAndMetadataURIs.contains(uri3));
        //Check that you cannot search them in a trie
        assertEquals(0, docStore.searchByKeywordAndMetadata("keyword", keysValues).size());
        //Add a fourth doc to the store, so there are 2 currently in memory
        URI uri4 = new URI("http://www.yu.edu/documents/doc4");
        byte[] myData4 = "Fourth doc in btree".getBytes();
        InputStream input4 = new ByteArrayInputStream(myData4);
        docStore.put(input4, uri4, DocumentStore.DocumentFormat.TXT);
        //Check that an undo on uri1 brings it back into the store and boots doc 2 to disk
        docStore.undo(uri1);
        //Call undo twice to bring doc 3 back into the store
        docStore.undo();
        docStore.undo();
        DocumentImpl doc3Copy = new DocumentImpl(uri3, "Third doc in btree keyword", null);
        assertEquals(doc3Copy, docStore.get(uri3));
    }

    @Test
    void deleteAllWithPrefixAndMetadataTest() throws URISyntaxException, IOException{
        DocumentStoreImpl docStore = new DocumentStoreImpl();
        URI uri1 = new URI("http://www.yu.edu/documents/doc1");
        byte[] myData1 = "First doc in btree keys".getBytes();
        InputStream input1 = new ByteArrayInputStream(myData1);
        //Add doc1 to the store
        docStore.put(input1, uri1, DocumentStore.DocumentFormat.TXT);
        //Add a second doc to the store
        URI uri2 = new URI("http://www.yu.edu/documents/doc2");
        byte[] myData2 = "Second doc in btree".getBytes();
        InputStream input2 = new ByteArrayInputStream(myData2);
        docStore.put(input2, uri2, DocumentStore.DocumentFormat.TXT);
        //And a third
        URI uri3 = new URI("http://www.yu.edu/documents/doc3");
        byte[] myData3 = "Third doc in btree keyword".getBytes();
        InputStream input3 = new ByteArrayInputStream(myData3);
        docStore.put(input3, uri3, DocumentStore.DocumentFormat.TXT);
        //Set their metadata
        docStore.setMetadata(uri1, "MetaKey1", "MetaValue1");
        docStore.setMetadata(uri1, "MetaKey2", "MetaValue2");
        docStore.setMetadata(uri1, "MetaKey3", "MetaValue3");
        docStore.setMetadata(uri2, "MetaKey1", "MetaValue1");
        docStore.setMetadata(uri2, "MetaKey2", "MetaValue2");
        docStore.setMetadata(uri3, "MetaKey1", "MetaValue1");
        HashMap<String, String> keysValues = new HashMap<>();
        keysValues.put("MetaKey1", "MetaValue1");
        //Set memory limit to 2 docs, pushing doc 1 to disk
        docStore.setMaxDocumentCount(2);
        //Delete docs 1 and 3 from everywhere with a call to deleteAllWithPrefixAndMetdata
        Set<URI> deletedDocsWithKeywordAndMetadataURIs = docStore.deleteAllWithPrefixAndMetadata("key", keysValues);
        //Check that that Set contains uris 1 and 3
        assertTrue(deletedDocsWithKeywordAndMetadataURIs.contains(uri1));
        assertTrue(deletedDocsWithKeywordAndMetadataURIs.contains(uri3));
        //Check that you cannot search them in a trie
        assertEquals(0, docStore.searchByPrefixAndMetadata("key", keysValues).size());
        //Add a fourth doc to the store, so there are 2 currently in memory
        URI uri4 = new URI("http://www.yu.edu/documents/doc4");
        byte[] myData4 = "Fourth doc in btree".getBytes();
        InputStream input4 = new ByteArrayInputStream(myData4);
        docStore.put(input4, uri4, DocumentStore.DocumentFormat.TXT);
        //Check that an undo on uri1 brings it back into the store and boots doc 2 to disk
        docStore.undo(uri1);
        //Call undo twice to bring doc 3 back into the store
        docStore.undo();
        docStore.undo();
        DocumentImpl doc3Copy = new DocumentImpl(uri3, "Third doc in btree keyword", null);
        assertEquals(doc3Copy, docStore.get(uri3));
    }

    @Test
    void setMaxDocumentCountMove1DocToDisk() throws URISyntaxException, IOException {
        //Add three docs to the store
        DocumentStoreImpl docStore = new DocumentStoreImpl();
        URI uri1 = new URI("http://www.yu.edu/documents/doc1");
        byte[] myData1 = "First doc in btree".getBytes();
        InputStream input1 = new ByteArrayInputStream(myData1);
        //Add doc1 to the store
        docStore.put(input1, uri1, DocumentStore.DocumentFormat.TXT);
        //Add a second doc to the store
        URI uri2 = new URI("http://www.yu.edu/documents/doc2");
        byte[] myData2 = "Second doc in btree".getBytes();
        InputStream input2 = new ByteArrayInputStream(myData2);
        docStore.put(input2, uri2, DocumentStore.DocumentFormat.TXT);
        //And a third
        URI uri3 = new URI("http://www.yu.edu/documents/doc3");
        byte[] myData3 = "Third doc in btree".getBytes();
        InputStream input3 = new ByteArrayInputStream(myData3);
        docStore.put(input3, uri3, DocumentStore.DocumentFormat.TXT);
        //Set the limit to 3 docs
        docStore.setMaxDocumentCount(3);
        //Add a forth doc to the store thereby pushing doc1 to disk
        URI uri4 = new URI("http://www.yu.edu/documents/doc4");
        byte[] myData4 = "Fourth doc in btree".getBytes();
        InputStream input4 = new ByteArrayInputStream(myData4);
        docStore.put(input4, uri4, DocumentStore.DocumentFormat.TXT);
    }

    @Test
    void setMaxDocumentBytesMove1DocToDisk() throws URISyntaxException, IOException {
        //Add three docs to the store
        DocumentStoreImpl docStore = new DocumentStoreImpl();
        URI uri1 = new URI("http://www.yu.edu/documents/doc1");
        byte[] myData1 = {1, 2, 3, 4};
        InputStream input1 = new ByteArrayInputStream(myData1);
        //Add doc1 to the store
        docStore.put(input1, uri1, DocumentStore.DocumentFormat.BINARY);
        //Add a second doc to the store
        URI uri2 = new URI("http://www.yu.edu/documents/doc2");
        byte[] myData2 = {5, 6, 7, 8};
        InputStream input2 = new ByteArrayInputStream(myData2);
        docStore.put(input2, uri2, DocumentStore.DocumentFormat.BINARY);
        //And a third
        URI uri3 = new URI("http://www.yu.edu/documents/doc3");
        byte[] myData3 = {9, 10, 11, 12};
        InputStream input3 = new ByteArrayInputStream(myData3);
        docStore.put(input3, uri3, DocumentStore.DocumentFormat.BINARY);
        //Set the byte limit to 12 bytes
        docStore.setMaxDocumentBytes(12);
        //Add a forth doc to the store thereby pushing doc1 to disk
        URI uri4 = new URI("http://www.yu.edu/documents/doc4");
        byte[] myData4 = {13, 14, 15, 16};
        InputStream input4 = new ByteArrayInputStream(myData4);
        docStore.put(input4, uri4, DocumentStore.DocumentFormat.BINARY);
    }
}