import java.util.HashMap;
import java.util.Iterator;

public class Trie {

    private Node head;

    public Trie() {
        head = new Node();
    }

    public boolean addWord(String w) {
        Node curr = head;

        for (int i = 0; i < w.length(); i++) {
            char c = w.charAt(i);

            curr.addChild(c);
            curr = curr.children.get(c);
        }

        if (curr.wordEnd) {
            return false;
        }
        curr.wordEnd = true;
        return true;
    }

    public boolean containsWord(String w) {
        Node curr = head;

        for (int i = 0; i < w.length(); i++) {
            char c = w.charAt(i);

            if (!curr.children.containsKey(c)) {
                return false;
            }
            curr = curr.children.get(c);
        }

        return curr.wordEnd;
    }

    public TrieIterator iterator() {
        return new TrieIterator();
    }

    private class Node {

        private HashMap<Character, Node> children;
        private boolean wordEnd;

        private Node() {
            this.children = new HashMap<Character, Node>();
            this.wordEnd = false;
        }

        private boolean addChild(Character c) {
            if (children.containsKey(c)) {
                return false;
            }

            children.put(c, new Node());
            return true;
        }

    }

    public class TrieIterator {

        private Node curr;
        private StringBuffer word;

        public TrieIterator() {
            word = new StringBuffer();
            reset();
        }

        public boolean proceed(Character c) {
            if (!curr.children.containsKey(c)) {
                return false;
            }

            curr = curr.children.get(c);
            word.append(c);
            return true;
        }

        public TrieIterator reset() {
            curr = head;
            word.setLength(0);

            return this;
        }

        public boolean add(Character c) {
            return curr.addChild(c);
        }

        public boolean isWordEnd() {
            return curr.wordEnd;
        }

        public String getWord() {
            return word.toString();
        }

    }

}