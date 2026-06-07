package edu.yu.cs.com1320.project.impl;

import edu.yu.cs.com1320.project.stage6.Document;
import edu.yu.cs.com1320.project.stage6.impl.DocumentImpl;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

class MinHeapImplTest {

    @Test
    void testgetArrayIndex() throws URISyntaxException{
        MinHeapImpl<Document> myMinHeap = new MinHeapImpl<>();
        URI uri1 = new URI("https:tester1.com");
        byte[] byteArray1 = {1, 2, 3, 4};
        Document doc1 = new DocumentImpl(uri1, byteArray1);
        URI uri2 = new URI("https:tester2.com");
        String docText1 = "Testing the getArrayIndex() method";
        DocumentImpl doc2 = new DocumentImpl(uri2, docText1, null);
        //Add time to only doc1
        doc1.setLastUseTime(System.nanoTime());
        //Add both docs to the heap
        myMinHeap.insert(doc1);
        myMinHeap.insert(doc2);
        assertEquals(doc2, myMinHeap.peek());
        assertEquals(doc2, myMinHeap.remove());
        assertEquals(doc1, myMinHeap.peek());
        myMinHeap.insert(doc2);
        //Create and add docs 3 and 4 to the heap
        URI uri3 = new URI("https:tester3.com");
        byte[] byteArray2 = {1, 2, 3, 4, 5, 6, 7, 8};
        Document doc3 = new DocumentImpl(uri3, byteArray2);
        URI uri4 = new URI("https:tester4.com");
        String docText2 = "Testing the getArrayIndex() method take 2";
        DocumentImpl doc4 = new DocumentImpl(uri4, docText2, null);
        doc3.setLastUseTime(System.nanoTime());
        doc4.setLastUseTime(System.nanoTime());
        myMinHeap.insert(doc3);
        myMinHeap.insert(doc4);
        //My assumption of indices - { doc2:1, doc1:2, doc3:3, doc4:4}
        assertEquals(1, myMinHeap.getArrayIndex(doc2));
        assertEquals(2, myMinHeap.getArrayIndex(doc1));
        assertEquals(3, myMinHeap.getArrayIndex(doc3));
        assertEquals(4, myMinHeap.getArrayIndex(doc4));
        //Create a doc five but don't put it in the heap
        URI uri5 = new URI("https:tester5.com");
        String docText3 = "Testing the getArrayIndex() method take 3";
        DocumentImpl doc5 = new DocumentImpl(uri5, docText3, null);
        //assertEquals(3, myMinHeap.getArrayIndex(doc5));
        assertThrows(NoSuchElementException.class, () -> {myMinHeap.getArrayIndex(doc5);});
    }

    @Test
    void testreHeapify() throws URISyntaxException{
        MinHeapImpl<Document> myMinHeap = new MinHeapImpl<>();
        URI uri1 = new URI("https:tester1.com");
        byte[] byteArray1 = {1, 2, 3, 4};
        Document doc1 = new DocumentImpl(uri1, byteArray1);
        URI uri2 = new URI("https:tester2.com");
        String docText1 = "Testing the getArrayIndex() method";
        DocumentImpl doc2 = new DocumentImpl(uri2, docText1, null);
        //Add time to only doc1
        doc1.setLastUseTime(System.nanoTime());
        //Add both docs to the heap
        myMinHeap.insert(doc1);
        myMinHeap.insert(doc2);
        //Create and add docs 3 and 4 to the heap
        URI uri3 = new URI("https:tester3.com");
        byte[] byteArray2 = {1, 2, 3, 4, 5, 6, 7, 8};
        Document doc3 = new DocumentImpl(uri3, byteArray2);
        URI uri4 = new URI("https:tester4.com");
        String docText2 = "Testing the getArrayIndex() method take 2";
        DocumentImpl doc4 = new DocumentImpl(uri4, docText2, null);
        doc3.setLastUseTime(System.nanoTime());
        doc4.setLastUseTime(System.nanoTime());
        myMinHeap.insert(doc3);
        myMinHeap.insert(doc4);
        //My assumption of indices - { doc2:1, doc1:2, doc3:3, doc4:4}
        assertEquals(1, myMinHeap.getArrayIndex(doc2));
        assertEquals(2, myMinHeap.getArrayIndex(doc1));
        assertEquals(3, myMinHeap.getArrayIndex(doc3));
        assertEquals(4, myMinHeap.getArrayIndex(doc4));
        //Add time to doc2 and reHeapify -- check if doc2 was moved to the bottom of the heap
        doc2.setLastUseTime(System.nanoTime());
        myMinHeap.reHeapify(doc2);
        //doc2 should have swapped positions with doc4
        assertEquals(4, myMinHeap.getArrayIndex(doc2));
        assertEquals(2, myMinHeap.getArrayIndex(doc4));
        //Ensure that rehibpify throws exception if called on element not in heap
        URI uri5 = new URI("https:tester5.com");
        String docText3 = "Reheapify should throw and exception";
        DocumentImpl doc5 = new DocumentImpl(uri5, docText3, null);
        assertThrows(NoSuchElementException.class, () -> {myMinHeap.getArrayIndex(doc5);});
    }

}