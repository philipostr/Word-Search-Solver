import java.util.HashMap;
import java.util.Iterator;

public class Trie {

    private Node head; // root of the trie. Does not represent any character

    public Trie() {
        head = new Node();
    }

    /**
     * Adds the specified word into the trie, forming and following branches as necessary.
     * 
     * @param w is the word to be added.
     * @return true if the word was added, false otherwise.
     */
    public boolean addWord(String w) {
        Node curr = head;

        for (int i = 0; i < w.length(); i++) { // iteratively traverse the trie
            char c = w.charAt(i);

            curr.addChild(c);
            curr = curr.children.get(c);
        }

        if (curr.wordEnd) { // the word was not added if it was already there
            return false;
        }
        curr.wordEnd = true; // solidifies the addition of the word
        return true;
    }

    /**
     * Checks whether a specified word is present in the trie, ending with a node classified
     * as a wordEnd.
     * 
     * @param w is the word to check.
     * @return true if the word is present, false otherwise.
     */
    public boolean containsWord(String w) {
        Node curr = head;

        for (int i = 0; i < w.length(); i++) { // iteratively traverse the trie
            char c = w.charAt(i);

            if (!curr.children.containsKey(c)) { // word is not present
                return false;
            }
            curr = curr.children.get(c);
        }

        return curr.wordEnd; // if curr is not a wordEnd, the word is not technically in the trie,
                             // but is rather contained in a larger word.
    }

    /**
     * @return an iterator for this trie.
     */
    public TrieIterator iterator() {
        return new TrieIterator();
    }

    /**
     * Used to represent characters within the trie. Contains children nodes, connecting the trie
     * internally.
     */
    private class Node {

        private HashMap<Character, Node> children; // character -> children nodes following that character
        private boolean wordEnd; // classifies this node as the end of a word contained in this trie

        private Node() {
            this.children = new HashMap<Character, Node>();
            this.wordEnd = false;
        }

        /**
         * Adds a new node representing the specified character as a child node, creating a new
         * sequence of characters (therefore making a new word path) within the trie.
         * 
         * @param c is the character to be represented by the new child node.
         * @return true if the new node was created, false otherwise.
         */
        private boolean addChild(Character c) {
            if (children.containsKey(c)) { // child node representing c is already present
                return false;
            }

            children.put(c, new Node());
            return true;
        }

    }

    /**
     * Iterator used to traverse the trie, creating words along its path.
     */
    public class TrieIterator {

        private Node curr; // current node the iterator is on
        private StringBuffer word; // the word that has been made through the taken path

        public TrieIterator() {
            word = new StringBuffer();
            reset();
        }

        /**
         * Similar to Iterator.next(). However because there are multiple possible next nodes,
         * there is a parameter character for what child node should be moved to.
         * 
         * @param c is the representative node to move to.
         * @return true if a node of c exists and is moved to, false otherwise.
         */
        public boolean proceed(Character c) {
            if (!curr.children.containsKey(c)) { // cannot proceed if there is no valid child node
                return false;
            }

            curr = curr.children.get(c); // proceed to next node
            word.append(c); // build onto the word path
            return true;
        }

        /**
         * Reset the iterator back to the root of the trie, also resetting the word path.
         * 
         * @return itself for easy function-chaining.
         */
        public TrieIterator reset() {
            curr = head;
            word.setLength(0);

            return this;
        }

        /**
         * Iterator version of Node.addChild().
         * 
         * @param c is the character to be represented by the new child node.
         * @return true if the new node was created, false otherwise.
         */
        public boolean add(Character c) {
            return curr.addChild(c);
        }

        /**
         * @return true if iterator is on a wordEnd node, false otherwise.
         */
        public boolean isWordEnd() {
            return curr.wordEnd;
        }

        /**
         * @return the word path so far.
         */
        public String getWord() {
            return word.toString();
        }

    }

}