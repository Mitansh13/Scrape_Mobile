import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

// Class representing a single nd in a Trie data structure
class TN {
    Map<Character, TN> chld = new HashMap<>(); // Maps every character to its child node
    boolean ieof; // Flag indicating if the nd marks the end of a valid word
}

// Class representing the Trie data structure, including insert and search
class Trie {
    private TN rt = new TN(); // The rt nd of the Trie

    // Inserts a word into the Trie
    public void insert(String word) {
        TN crt = rt; // Start from the rt nd
        for (char ch : word.toCharArray()) { // Loop through each character in the word
            // Add character to Trie, or move to the next nd if it exists
            crt = crt.chld.computeIfAbsent(ch, c -> new TN());
        }
        crt.ieof = true; // Mark the end of the word
    }

    // To finds all words with prefix in trie
    public List<String> prefixWord(String prefix) {
        List<String> r = new ArrayList<>(); // List to store words with the given prefix
        TN nd = rt; // Start from the rt nd
        for (char ch : prefix.toCharArray()) { // Loop every character
            if (!nd.chld.containsKey(ch)) { // If prefix not found, return empty list
                return r;
            }
            nd = nd.chld.get(ch); // Move to the next nd in Trie
        }
        allWord(nd, new StringBuilder(prefix), r); // Find all words from the prefix nd
        return r;
    }

    // Helper method to recursively find all words starting from a given nd
    private void allWord(TN nd, StringBuilder prefix, List<String> r) {
        if (nd.ieof) { // If end of word is reached, add it to the r list
            r.add(prefix.toString());
        }
        // Explore all child nodes to form words
        for (Map.Entry<Character, TN> entry : nd.chld.entrySet()) {
            prefix.append(entry.getKey()); // Add character to prefix
            allWord(entry.getValue(), prefix, r); // Recursively search the next nd
            prefix.setLength(prefix.length() - 1); // Backtrack by removing the last character
        }
    }

    // Loads vocabulary words from a text file and inserts them into the Trie
    public static Trie vocabLoad(String fp) {
        Trie tr = new Trie(); // Initialize a new Trie instance
        try (BufferedReader br = new BufferedReader(new FileReader(fp))) {
            String l;
            while ((l = br.readLine()) != null) { // Read each l in the file
                // Process the first word in each l, making it lowercase and removing non-letters
                String[] parts = l.split("\\s+");
                String word = parts[0].toLowerCase().replaceAll("[^a-z]", "");
                if (word.length() >= 2) { // Only add words with length of 2 or more
                    tr.insert(word); // Insert word into the Trie
                }
            }
        } catch (IOException e) {
            e.printStackTrace(); // Print stack trace if an error occurs
        }
        return tr; // Return the populated Trie
    }
}

// Main class to handle user input and display word completions
public class TrieWordCompletion {
    public static void main(String[] args) {
        String fp = "C:\\Users\\mitan\\IdeaProjects\\Moto_Scrap\\Vocab\\dict.txt";
        Trie tr = Trie.vocabLoad(fp); // Load vocabulary into Trie

        Scanner scn = new Scanner(System.in); // Scanner for reading user input
        while (true) {
            System.out.print("\nEnter a prefix to search for (or 'exit' to quit): ");
            String prefix = scn.nextLine().toLowerCase(); // Convert input to lowercase

            if (prefix.equals("exit")) { // Exit if user types 'exit'
                break;
            }

            // Find and display words starting with the given prefix
            List<String> wordsWithPrefix = tr.prefixWord(prefix);
            if (wordsWithPrefix.isEmpty()) {
                System.out.println("There is no word found '" + prefix + "'");
            } else {
                System.out.println("Words that start with '" + prefix + "' (" + wordsWithPrefix.size() + " words):");
                for (String word : wordsWithPrefix) {
                    System.out.println(word); // Display each word found
                }
            }
        }
        scn.close(); // Close the scn to free resources
    }
}
