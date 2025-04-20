import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

public class SpellCheckerGUI extends JFrame {
    private JTextField searchField;
    private JList<String> suggestionsList;
    private DefaultListModel<String> suggestionsModel;
    private Spell spellChecker;

    public SpellCheckerGUI(Spell spellChecker) {
        this.spellChecker = spellChecker;
        setTitle("Spell Checker");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // Change to DISPOSE_ON_CLOSE
        setLayout(new BorderLayout());

        // Create search field
        searchField = new JTextField();
        searchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                String input = searchField.getText().trim();
                if (input.isEmpty()) {
                    suggestionsModel.clear();
                } else {
                    List<String> suggestions = spellChecker.getSuggestions(input);
                    suggestionsModel.clear();
                    for (String suggestion : suggestions) {
                        suggestionsModel.addElement(suggestion);
                    }
                }
            }
        });

        // Create suggestions list
        suggestionsModel = new DefaultListModel<>();
        suggestionsList = new JList<>(suggestionsModel);
        suggestionsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        suggestionsList.setVisibleRowCount(5);
        suggestionsList.setFixedCellHeight(20);
        suggestionsList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    String selectedSuggestion = suggestionsList.getSelectedValue();
                    if (selectedSuggestion != null) {
                        String correctedWord = selectedSuggestion.split(":")[0]; // Get the word part
                        searchField.setText(correctedWord); // Set the corrected word in the search field
                        suggestionsModel.clear(); // Clear suggestions
                    }
                }
            }
        });

        // Create scroll pane for suggestions
        JScrollPane scrollPane = new JScrollPane(suggestionsList);

        // Add components to the frame
        add(searchField, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        setVisible(true);
    }

    public static void main(String[] args) {
        VocabularyLoader loader = new VocabularyLoader();
        loader.loadVocabulary(new String[]{"C:\\Users\\mitan\\OneDrive\\Desktop\\Mobile_Recommendation\\src\\Vocab.txt"}); // Load vocabulary from file

        Spell spellChecker = new Spell(10007); // Initialize spell checker with a hash table size
        spellChecker.loadVocabulary(loader.getVocabulary()); // Load vocabulary into the hash table

        // Create and run the GUI
        SwingUtilities.invokeLater(() -> new SpellCheckerGUI(spellChecker));
    }
}