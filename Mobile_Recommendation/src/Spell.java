import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

// Main class for the spell checker
public class Spell {
    private CuckooHashTable hT; // Hash table to store vocabulary

    // Constructor initializes the hash table with a specified size
    public Spell(int tableSize) {
        hT = new CuckooHashTable(tableSize);
    }

    // Method to load vocabulary into the hash table
    public void loadVocabulary(Set<String> vocabulary) {
        for (String word : vocabulary) {
            hT.insert(word); // Insert each word into the hash table
        }
    }

    // Method to retrieve suggestions for a misspelled word
    public List<String> getSuggestions(String word) {
        List<String> suggestions = new ArrayList<>();
        for (String vocabWord : hT.getWords()) {
            int distance = EditDistance.calculate(word, vocabWord); // Calculate edit distance
            if (distance <= 2) {
                suggestions.add(vocabWord + ":" + distance); // Store as "word:distance"
            }
        }
        mergeSort(suggestions, 0, suggestions.size() - 1); // Sort suggestions by edit distance
        return suggestions;
    }

    // Method to check if a word exists in the vocabulary
    public boolean contains(String word) {
        return hT.contains(word);
    }

    // Merge sort algorithm to sort suggestions by edit distance
    private void mergeSort(List<String> suggestions, int left, int right) {
        if (left < right) {
            int mid = (left + right) / 2;
            mergeSort(suggestions, left, mid);
            mergeSort(suggestions, mid + 1, right);
            merge(suggestions, left, mid, right);
        }
    }

    // Merge function for merge sort
    private void merge(List<String> suggestions, int left, int mid, int right) {
        List<String> ll = new ArrayList<>(suggestions.subList(left, mid + 1));
        List<String> rl = new ArrayList<>(suggestions.subList(mid + 1, right + 1));

        int i = 0, j = 0, k = left;
        while (i < ll.size() && j < rl.size()) {
            if (extractDistance(ll.get(i)) <= extractDistance(rl.get(j))) {
                suggestions.set(k++, ll.get(i++));
            } else {
                suggestions.set(k++, rl.get(j++));
            }
        }
        while (i < ll.size()) suggestions.set(k++, ll.get(i++));
        while (j < rl.size()) suggestions.set(k++, rl.get(j++));
    }

    // Helper function to extract the edit distance from a suggestion string
    private int extractDistance(String suggestion) {
        String[] pts = suggestion.split(":");
        return Integer.parseInt(pts[1]); // Only get the distance
    }

    // Main method to test the spell checker
    public static void main(String[] args) {
        VocabularyLoader loader = new VocabularyLoader();
        loader.loadVocabulary(new String[] {"C:\\Users\\Krutarth\\OneDrive\\Desktop\\Uwin\\ACC\\Assignments\\Final\\Mobile_Recommendation\\src\\Vocab.txt"}); // Load vocabulary from file

        Spell spellChecker = new Spell(10007); // Initialize spell checker with a hash table size
        spellChecker.loadVocabulary(loader.getVocabulary()); // Load vocabulary into the hash table

        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.print("Enter a word (type 'exit' to quit): ");
            String word = scanner.nextLine().trim();

            if (word.equalsIgnoreCase("exit")) {
                System.out.println("Exiting spell checker.");
                break;
            }

            if (spellChecker.contains(word)) {
                System.out.println("The word you searched which is '" + word + "' is spelled correctly.");
            } else {
                System.out.println("The word you searched which is'" + word + "' is misspelled.");
                System.out.println("Suggestions:");
                List<String> suggestions = spellChecker.getSuggestions(word);
                if (suggestions.isEmpty()) {
                    System.out .println("No close match found Try again!!!");
                } else {
                    for (String suggestion : suggestions) {
                        System.out.println(suggestion);
                    }
                }
            }
        }
        scanner.close();
    }
}