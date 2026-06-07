package edu.yu.cs.com1320.project.impl;

import edu.yu.cs.com1320.project.Stack;

public class StackImpl<T> implements Stack<T> {
    private T[] data;
    private int top;

    public StackImpl(){
        this.data = (T[]) new Object[2];
        this.top = -1;
    }

    /**
     * @param element object to add to the Stack
     */
    @Override
    public void push(T element){
        if (this.top == this.data.length - 1){
            this.doubleStack();
        }
        this.top++;
        this.data[top] = element;
    }

    //Method to double data array when stack is full
    private void doubleStack(){
        T[] temp = (T[]) new Object[this.data.length * 2];
        for(int i = 0; i < this.data.length; i++){
            temp[i] = this.data[i];
        }
        this.data = temp;
    }

    /**
     * removes and returns element at the top of the stack
     * @return element at the top of the stack, null if the stack is empty
     */
    @Override
    public T pop(){
        if(this.top == -1){
            return null;
        }
        T element = this.data[this.top];
        this.data[this.top] = null;
        this.top--;
        return element;
    }

    /**
     *
     * @return the element at the top of the stack without removing it
     */
    @Override
    public T peek(){
        if(this.top == -1){
            return null;
        }
        return this.data[this.top];
    }

    /**
     *
     * @return how many elements are currently in the stack
     */
    @Override
    public int size(){
        int count = 0;
        for(int i = 0; i < this.data.length; i++){
            if(this.data[i] != null){
                count++;
            }
        }
        return count;
    }

}