package edu.yu.cs.com1320.project.impl;

import com.google.gson.Gson;
import edu.yu.cs.com1320.project.BTree;
import edu.yu.cs.com1320.project.stage6.Document;
import edu.yu.cs.com1320.project.stage6.impl.DocumentPersistenceManager;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BTreeImplTest {

    @Test
    void testget(){
        BTree<Integer, String> myBTree = new BTreeImpl<>();
        myBTree.put(1, "one");
        myBTree.put(2, "two");
        myBTree.put(3, "three");
        myBTree.put(4, "four");
        myBTree.put(5, "five");
        myBTree.put(6, "six");
        myBTree.put(7, "seven");
        myBTree.put(8, "eight");
        //test the get method
        assertEquals("one", myBTree.get(1));
        assertEquals("two", myBTree.get(2));
        assertEquals("three", myBTree.get(3));
        assertEquals("four", myBTree.get(4));
        assertEquals("five", myBTree.get(5));
        assertEquals("six", myBTree.get(6));
        assertEquals("seven", myBTree.get(7));
        assertEquals("eight", myBTree.get(8));
        //Now do some overwrites and test
        myBTree.put(5, "new five");
        assertEquals("new five", myBTree.get(5));
        myBTree.put(5, "new five number 2");
        assertEquals("new five number 2", myBTree.get(5));
        myBTree.put(8, "new eight");
        assertEquals("new eight", myBTree.get(8));
    }

    @Test
    void testWithNoSentinel(){
        BTree<Integer, String> myBTree = new BTreeImpl<>();
        myBTree.put(2, "two");
        myBTree.put(3, "three");
        myBTree.put(4, "four");
        myBTree.put(5, "five");
        myBTree.put(6, "six");
        //Put it one which should be lowest
        myBTree.put(1, "one");
        //Test that I can get 1
        assertEquals("one", myBTree.get(1));
    }

    @Test
    void testput(){
        BTree<Integer, String> myBTree = new BTreeImpl<>();
        assertNull(myBTree.put(1, "one"));
        myBTree.put(2, "two");
        myBTree.put(3, "three");
        myBTree.put(4, "four");
        myBTree.put(5, "five");
        //Check that the overwrites return the old value
        assertEquals("two", myBTree.put(2, "new two"));
        assertEquals("three", myBTree.put(3, "new three"));
        //What happens when I put a null value?
        assertEquals("four", myBTree.put(4, null));
        //assertNull(myBTree.get(4));
    }

}