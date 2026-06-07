package edu.yu.cs.com1320.project.impl;

import edu.yu.cs.com1320.project.Trie;

import java.util.*;

public class TrieImpl<Value> implements Trie<Value> {
    private static final int alphabetSize = 128; // ASCII
    private Node<Value> root; // root of trie

    private static class Node<Value>{
        protected Set<Value> val = new HashSet<>();
        protected Node[] links = new Node[alphabetSize];
    }

    //Zero argument constructor
    public TrieImpl(){
        this.root = new Node();
    }

    /**
     * add the given value at the given key
     * @param key
     * @param val
     */
    @Override
    public void put(String key, Value val){
        //deleteAll the value from this key
        if(val == null){
            this.deleteAll(key);
        }
        else{
            this.root = put(this.root, key, val, 0);
        }
    }

    //Private put method
    private Node put(Node x, String key, Value val, int d){
        //create a new node
        if(x == null){
            x = new Node();
        }
        //we've reached the last node in the key,
        //set the value for the key and return the node
        if(d == key.length()){
            x.val.add(val);
            return x;
        }
        //proceed to the next node in the chain of nodes that
        //forms the desired key
        char c = key.charAt(d);
        x.links[c] = this.put(x.links[c], key, val, d + 1);
        return x;
    }

    /**
     * Get all exact matches for the given key, sorted in descending order, where "descending" is defined by the comparator.
     * NOTE FOR COM1320 PROJECT: FOR PURPOSES OF A *KEYWORD* SEARCH, THE COMPARATOR SHOULD DEFINE ORDER AS HOW MANY TIMES THE KEYWORD APPEARS IN THE DOCUMENT.
     * Search is CASE SENSITIVE.
     * @param key
     * @param comparator used to sort values
     * @return a List of matching Values. Empty List if no matches.
     */
    @Override
    public List<Value> getSorted(String key, Comparator<Value> comparator){
        List<Value> listOfVals= new ArrayList<>(this.get(key));
        if(listOfVals.size() == 0) return listOfVals;
        listOfVals.sort(comparator);
        return listOfVals;
    }

    /**
     * get all exact matches for the given key.
     * Search is CASE SENSITIVE.
     * @param key
     * @return a Set of matching Values. Empty set if no matches.
     */
    @Override
    public Set<Value> get(String key){
        Node x = this.get(this.root, key, 0);
        if(x == null){
            return new HashSet<Value>();
        }
        return (Set<Value>)x.val;
    }

    //Private get method
    private Node get(Node x, String key, int d){
        //link was null - return null, indicating a miss
        if(x == null){
            return null;
        }
        //we've reached the last node in the key,
        //return the node
        if(d == key.length()){
            return x;
        }
        //proceed to the next node in the chain of nodes that
        //forms the desired key
        char c = key.charAt(d);
        return this.get(x.links[c], key, d + 1);
    }

    /**
     * get all matches which contain a String with the given prefix, sorted in descending order, where "descending" is defined by the comparator.
     * NOTE FOR COM1320 PROJECT: FOR PURPOSES OF A *KEYWORD* SEARCH, THE COMPARATOR SHOULD DEFINE ORDER AS HOW MANY TIMES THE KEYWORD APPEARS IN THE DOCUMENT.
     * For example, if the key is "Too", you would return any value that contains "Tool", "Too", "Tooth", "Toodle", etc.
     * Search is CASE SENSITIVE.
     * @param prefix
     * @param comparator used to sort values
     * @return a List of all matching Values containing the given prefix, in descending order. Empty List if no matches.
     */
    @Override
    public List<Value> getAllWithPrefixSorted(String prefix, Comparator<Value> comparator){
        HashSet<Value> matchesSet = new HashSet<>();
        //Do a traversal through the trie and add any value to the set
        Node x = this.get(this.root, prefix, 0);
        if(x != null) this.addPrefixVals(x, matchesSet);
        List<Value> matchesList = new ArrayList<>(matchesSet);
        matchesList.sort(comparator);
        return matchesList;
    }

    //Private method to traverse down from the prefix and add all Values
    private void addPrefixVals(Node x, HashSet<Value> valuesSet){
        //If this node has values add them to the set
        if(x.val.size() != 0) valuesSet.addAll(x.val);
        //visit each not null child link
        for(int i = 0; i < TrieImpl.alphabetSize; i++){
            if(x.links[i] != null){
                this.addPrefixVals(x.links[i], valuesSet);
            }
        }
    }

    /**
     * Delete the subtree rooted at the last character of the prefix.
     * Search is CASE SENSITIVE.
     * @param prefix
     * @return a Set of all Values that were deleted.
     */
    @Override
    public Set<Value> deleteAllWithPrefix(String prefix){
        HashSet<Value> matchesSet = new HashSet<>();
        //Do a traversal through the trie and add any value to the set
        Node x = this.get(this.root, prefix, 0);
        if(x != null) this.addPrefixVals(x, matchesSet);
        this.delete(this.root, prefix, 0);
        return matchesSet;
    }

    /**
     * Delete all values from the node of the given key (do not remove the values from other nodes in the Trie)
     * @param key
     * @return a Set of all Values that were deleted.
     */
    @Override
    public Set<Value> deleteAll(String key){
        Set<Value> setToDelete = this.get(key);
        Node x = this.get(this.root, key, 0);
        if(x == null) return new HashSet<>();
        x.val = new HashSet<>();
        //If this node isn't a prefix, delete it and all its useless ancestors
        boolean hit = false;
        for(int i = 0; i < TrieImpl.alphabetSize; i++){
            if(x.links[i] != null){
                hit = true;
                break;
            }
        }
        if(!hit) this.delete(this.root, key, 0);
        return setToDelete;
    }

    //Method to delete a key from the trie
    private Node delete(Node x, String key, int d){
        if(x == null) return null;
        //If we reached the last node set it to null in its parent's links array
        if(d == key.length()) return null;
        char c = key.charAt(d);
        x.links[c] = this.delete(x.links[c], key, d+1);
        //This node has a value so do nothing
        if(x.val.size() != 0) return x;
        //Check if all its links are null
        for(int i = 0; i < TrieImpl.alphabetSize; i++){
            if(x.links[i] != null){
                return x;
            }
        }
        //It doesn't have a value and its links array is all null -- set it to null in its parent
        return null;
    }

    /**
     * Remove the given value from the node of the given key (do not remove the value from other nodes in the Trie)
     * @param key
     * @param val
     * @return the value which was deleted. If the key did not contain the given value, return null.
     */
    @Override
    public Value delete(String key, Value val){
        Node x = this.get(this.root, key, 0);
        if(x == null) return null;
        boolean deleted = x.val.remove(val);
        //Check if this node should be deleted
        if(x.val.isEmpty()){
            boolean hit = false;
            for(int i = 0; i < TrieImpl.alphabetSize; i++){
                if(x.links[i] != null){
                    hit = true;
                    break;
                }
            }
            if(!hit) this.delete(this.root, key, 0);
        }
        return deleted ? val : null;
    }

}
