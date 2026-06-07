package edu.yu.cs.com1320.project.impl;

import edu.yu.cs.com1320.project.MinHeap;

import java.util.NoSuchElementException;

public class MinHeapImpl<E extends Comparable<E>> extends MinHeap<E>{

    public MinHeapImpl(){
        this.elements = (E[]) new Comparable[1];
    }

    @Override
    public void reHeapify(E element){
        int IndOfElement = this.getArrayIndex(element);
        this.upHeap(IndOfElement);
        this.downHeap(IndOfElement);
    }

    @Override
    protected int getArrayIndex(E element){
        for(int i = 1; i <= this.count; i++){
            if(this.elements[i].equals(element)) return i;
        }
        throw new NoSuchElementException("Element not found in heap");
    }

    @Override
    protected void doubleArraySize(){
        E[] temp = (E[]) new Comparable[this.elements.length * 2];
        for(int i = 0; i < this.elements.length; i++){
            temp[i] = this.elements[i];
        }
        this.elements = temp;
    }

}
