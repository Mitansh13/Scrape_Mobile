import java.util.HashSet;
import java.util.Set;

// Implementation of a Cuckoo Hash Table for efficient word storage and retrieval
class CuckooHashTable {
    private String[] t1; // First hash table
    private String[] t2; // Second hash table
    private int s; // s of the hash tables

    // Constructor initializes the hash tables with the given s
    public CuckooHashTable(int s) {
        this.s = s;
        t1 = new String[s];
        t2 = new String[s];
    }

    // Method to insert a word into the hash table
    public void insert(String word) {
        int hs1 = hs1(word); // Calculate first hash
        int hs2 = hs2(word); // Calculate second hash

        // Attempt to place the word in the hash table
        for (int i = 0; i < s; i++) {
            if (t1[hs1] == null) {
                t1[hs1] = word; // Place in first table if empty
                return;
            } else if (t2[hs2] == null) {
                t2[hs2] = word; // Place in second table if empty
                return;
            }

            // If both positions are occupied, displace the existing word
            String displacedWord = t1[hs1];
            t1[hs1] = word; // Place the new word
            word = displacedWord; // Displaced word becomes the new word to insert

            // Recalculate hashes for the displaced word
            hs1 = hs1(word);
            hs2 = hs2(word);
        }
    }

    // Method to check if a word exists in the hash table
    public boolean contains(String word) {
        return t1[hs1(word)] != null || t2[hs2(word)] != null;
    }

    // Method to retrieve all words stored in the hash table
    public Set<String> getWords() {
        Set<String> words = new HashSet<>();
        for (String word : t1) {
            if (word != null) {
                words.add(word); // Add non-null words from the first table
            }
        }
        for (String word : t2) {
            if (word != null) {
                words.add(word); // Add non-null words from the second table
            }
        }
        return words;
    }

    // Hash function for the first table
    private int hs1(String word) {
        return Math.abs(word.hashCode() % s);
    }

    // Hash function for the second table
    private int hs2(String word) {
        return Math.abs((word.hashCode() / s) % s);
    }
}