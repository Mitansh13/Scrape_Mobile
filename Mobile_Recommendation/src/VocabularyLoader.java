import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

// Class responsible for loading vocab from specified files
class VocabularyLoader {
    private Set<String> vocab; // A set to store unique words

    // Constructor initializes the vocabulary set
    public VocabularyLoader() {
        vocab = new HashSet<>();
    }

    // Method to load vocabulary from one or more file paths
    public void loadVocabulary(String[] filePaths) {
        for (String filePath : filePaths) {
            try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
                String l; // To read each l from the file
                while ((l = reader.readLine()) != null) {
                    // Split the line into words and add them to the vocabulary set
                    for (String word : l.split("\\s+")) {
                        vocab.add(word.toLowerCase()); // Convert to lowercase for uniformity
                    }
                }
            } catch (IOException e) {
                // Handle any IO exceptions that may occur during file reading
                System.err.println("There is and error reading data from the file: " + e.getMessage());
            }
        }
    }

    // Method to retrieve the loaded vocabulary
    public Set<String> getVocabulary() {
        return vocab;
    }
}