package edu.yu.cs.com1320.project.stage6.impl;

import com.google.gson.*;
import edu.yu.cs.com1320.project.stage6.Document;
import jakarta.xml.bind.DatatypeConverter;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class DocumentPersistenceManagerTest {

    @Test
    void serializeTest() throws URISyntaxException, IOException {
        //Make it independent of deserialize
        DocumentPersistenceManager docPers = new DocumentPersistenceManager(null);
        String docTxt = "Doc to test my serialize method test";
        byte[] docBinary = {1, 2, 3, 4, 5};
        URI uri = new URI("http://www.yu.edu/documents/doc1A");
        Document mydoc = new DocumentImpl(uri, docTxt, null);
        mydoc.setMetadataValue("metaKey", "metaValue");
        mydoc.setMetadataValue("metaKey2", "metaValue2");
        Document mydocBinary = new DocumentImpl(uri, docBinary);
        mydocBinary.setMetadataValue("metaKeyBin", "metaValueBin");
        docPers.serialize(uri, mydocBinary);
        //docPers.serialize(uri, mydoc);
    }

    @Test
    void miniTest() throws URISyntaxException, IOException {
        DocumentPersistenceManager docPers = new DocumentPersistenceManager(null);
        URI uri = new URI("http://www.yu.edu/documents/doc1");
        String str = uri.toString();
        if(str.charAt(4) == ':'){
            str = str.substring(7) + ".json";
        }
        else{
            str = str.substring(8) + ".json";
        }
        System.out.println(str);
    }

    @Test
    void deserializeTest() throws URISyntaxException, IOException {
        //Make it independent of serialize tester
        DocumentPersistenceManager docPers = new DocumentPersistenceManager(null);
        URI uri = new URI("http://www.yu.edu/documents/doc1B");
        byte[] docBinary = {1, 2, 3, 4, 5};
        Document mydocBinary = new DocumentImpl(uri, docBinary);
        mydocBinary.setMetadataValue("metaKeyBin", "metaValueBin");
        //Before serializing the document check that the deserializer throws io exception
        assertThrows(IOException.class, () -> {docPers.deserialize(uri);});
        //Serialize the document
        docPers.serialize(uri, mydocBinary);
        //Deserialize it
        Document deserDoc = docPers.deserialize(uri);
        System.out.println("My Doc's info: ");
        System.out.println("Document Binary = " + deserDoc.getDocumentBinaryData()[0] + deserDoc.getDocumentBinaryData()[1] + "...");
        System.out.println("Document Uri = " + deserDoc.getKey().toString());
        System.out.println("Document Metadata = " + deserDoc.getMetadataValue("metaKeyBin"));
        System.out.println("Document Time = " + deserDoc.getLastUseTime());
        assertNull(deserDoc.getDocumentTxt());
    }

    @Test
    void deleteTest() throws URISyntaxException, IOException {
        //Make it independent of other tests
        DocumentPersistenceManager docPers = new DocumentPersistenceManager(null);
        URI uri = new URI("https://www.yu.edu/documents/doc1C");
        byte[] docBinary = {1, 2, 3, 4, 5};
        Document mydocBinary = new DocumentImpl(uri, docBinary);
        mydocBinary.setMetadataValue("metaKeyBin", "metaValueBin");
        //Serialize the document
        docPers.serialize(uri, mydocBinary);
        //Delete the serialized doc
        assertTrue(docPers.delete(uri));
        assertFalse(docPers.delete(uri));
    }

    @Test
    void customBaseDirTest() throws URISyntaxException, IOException {
        File baseDir = new File(System.getProperty("user.dir"), File.separator + "newBase");
        DocumentPersistenceManager docPers = new DocumentPersistenceManager(baseDir);
        URI uri = new URI("https://www.yu.edu/documents/doc2");
        byte[] docBinary = {1, 2, 3, 4, 5};
        Document mydocBinary = new DocumentImpl(uri, docBinary);
        mydocBinary.setMetadataValue("metaKeyBin", "metaValueBin");
        docPers.serialize(uri, mydocBinary);
        Document deserDoc = docPers.deserialize(uri);
        System.out.println("My Doc's info: ");
        System.out.println("Document Binary = " + deserDoc.getDocumentBinaryData()[0] + deserDoc.getDocumentBinaryData()[1] + "...");
        System.out.println("Document Uri = " + deserDoc.getKey().toString());
        System.out.println("Document Metadata = " + deserDoc.getMetadataValue("metaKeyBin"));
        System.out.println("Document Time = " + deserDoc.getLastUseTime());
        assertNull(deserDoc.getDocumentTxt());
    }


}