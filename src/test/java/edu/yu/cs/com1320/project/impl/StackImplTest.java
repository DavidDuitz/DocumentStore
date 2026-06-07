package edu.yu.cs.com1320.project.impl;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StackImplTest {

    @Test
    void testStack(){
        StackImpl<Integer> myStack = new StackImpl<>();
        myStack.push(1);
        assertEquals(1, myStack.peek());
        myStack.push(2);
        assertEquals(2, myStack.peek());
        //Test the size
        myStack.push(3);
        myStack.push(4);
        myStack.push(5);
        assertEquals(5, myStack.size());
        //Test with popping
        assertEquals(5, myStack.pop());
        assertEquals(4, myStack.peek());
        assertEquals(4,myStack.pop());
        assertEquals(3, myStack.peek());

    }

}