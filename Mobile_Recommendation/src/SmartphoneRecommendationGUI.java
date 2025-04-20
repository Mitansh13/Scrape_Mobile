import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.*;
import java.nio.file.Paths;
import java.util.*;
import java.util.List;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

// Main class for the Smartphone Recommendation GUI
public class SmartphoneRecommendationGUI {

    private static JButton trendingButton; // Declare trending button as a class variable
    private static JButton webCrawlerButton; // Declare web crawler button
    private static JButton statsButton; // Declare stats button
    private static Trie wordTrie; // Trie for word completion
    private static JPopupMenu suggestionMenu; // Single instance of JPopupMenu

    public static void main(String[] args) {
        VocabularyLoader loader = new VocabularyLoader();
        loader.loadVocabulary(new String[]{"C:\\Users\\mitan\\OneDrive\\Desktop\\Mobile_Recommendation\\src\\Vocab.txt"}); // Load vocabulary from file

        Spell spellChecker = new Spell(10007); // Initialize spell checker with a hash table size
        spellChecker.loadVocabulary(loader.getVocabulary()); // Load vocabulary into the hash table

        // Load smartphone data from CSV
        List<Smartphone> smartphones = loadSmartphoneData("C:\\Users\\mitan\\OneDrive\\Desktop\\Mobile_Recommendation\\src\\data.csv");

        // Initialize Trie for word completion
        wordTrie = new Trie();
        for (String word : loader.getVocabulary()) {
            wordTrie.insert(word.toLowerCase()); // Insert vocabulary words into the Trie
        }

        // Create main frame
        JFrame frame = new JFrame("Smartphone Recommendation System");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1200, 700);
        frame.setLayout(new BorderLayout(10, 10));

        // Top Search Panel
        JPanel searchPanel = new JPanel(new BorderLayout(5, 5));
        JTextField searchField = new JTextField();

        JButton searchButton = new JButton("Search");
        JPanel buttonPanel = new JPanel();
        JButton spellCheckerButton = new JButton("Open Spell Checker"); // New Spell Checker button
        buttonPanel.add(spellCheckerButton);

        // Create the web crawler button
        webCrawlerButton = new JButton("Open Web Crawler");
        webCrawlerButton.setAlignmentX(Component.CENTER_ALIGNMENT); // Center align the button
        buttonPanel.add(webCrawlerButton);

        // Create the stats button and add it next to the web crawler button
        statsButton = new JButton("Show Company and Price Stats");
        statsButton.setAlignmentX(Component.CENTER_ALIGNMENT); // Center align the button
        buttonPanel.add(statsButton); // Add the stats button next to the web crawler button

        searchPanel.add(searchField, BorderLayout.CENTER);
        searchPanel.add(searchButton, BorderLayout.EAST);
        searchPanel.add(buttonPanel, BorderLayout.SOUTH);

        frame.add(searchPanel, BorderLayout.NORTH);

        searchField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                if (searchField.getText().equals("Search by phone name, brand, storage, or features")) {
                    searchField.setText(""); // Clear the hint text when focused
                    searchField.setForeground(Color.BLACK); // Change text color to black
                }
            }

            public void focusLost(java.awt.event.FocusEvent evt) {
                if (searchField.getText().isEmpty()) {
                    searchField.setText("Search by phone name, brand, storage, or features"); // Reset hint text
                    searchField.setForeground(Color.GRAY); // Set hint text color back to gray
                }
            }
        });

        // Add key listener for word completion
        searchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                String query = searchField.getText().toLowerCase();
                if (!query.isEmpty()) {
                    List<String> suggestions = wordTrie.prefixWord(query);
                    displaySuggestions(suggestions, searchField);
                } else {
                    suggestionMenu.setVisible(false); // Hide suggestions if the query is empty
                }
            }
        });

        // Main Content Panel
        JPanel contentPanel = new JPanel(new BorderLayout(10, 10));
        frame.add(contentPanel, BorderLayout.CENTER);

        // Filters Panel (Left)
        JPanel filtersPanel = new JPanel();
        filtersPanel.setLayout(new BoxLayout(filtersPanel, BoxLayout.Y_AXIS));
        filtersPanel.setBorder(BorderFactory.createTitledBorder("Filters ")); // Corrected line
        filtersPanel.setPreferredSize(new Dimension(400, 400));
        filtersPanel.add(Box.createVerticalStrut(10));

        // Filter Options
        filtersPanel.add(new JLabel("Brand"));
        filtersPanel.add(Box.createVerticalStrut(10));
        String[] companies = {"All", "Apple", "Samsung", "Google", "OnePlus", "Lava"};
        JComboBox<String> companyDropdown = new JComboBox<>(companies);
        filtersPanel.add(companyDropdown);

        filtersPanel.add(Box.createVerticalStrut(10));

        filtersPanel.add(new JLabel("Price Range"));
        filtersPanel.add(Box.createVerticalStrut(10));
        JPanel priceRangePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JTextField minPriceField = new JTextField(5);
        JTextField maxPriceField = new JTextField(5);
        priceRangePanel.add(new JLabel("Min $"));
        priceRangePanel.add(minPriceField);

        priceRangePanel.add(Box.createHorizontalStrut(20));
        priceRangePanel.add(new JLabel("-"));
        priceRangePanel.add(Box.createHorizontalStrut(20));

        priceRangePanel.add(new JLabel("Max $"));
        priceRangePanel.add(maxPriceField);
        filtersPanel.add(priceRangePanel);

        // Features Section
        filtersPanel.add(new JLabel("Features:"));
        JPanel featuresPanel = new JPanel();
        featuresPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 0)); // Horizontal layout with gap between checkboxes

        // Existing Features
        JCheckBox feature1 = new JCheckBox("5G Support");
        JCheckBox feature2 = new JCheckBox("Wireless Charging");
        JCheckBox feature3 = new JCheckBox("Headphone Jack");
        JCheckBox feature4 = new JCheckBox("High-Resolution Camera");

        // New Features
        JCheckBox feature5 = new JCheckBox(" OLED Display");
        JCheckBox feature6 = new JCheckBox(" A16 Bionic Chip");
        JCheckBox feature7 = new JCheckBox(" A17 Bionic Chip");
        JCheckBox feature8 = new JCheckBox(" MediaTek Dimensity");
        JCheckBox feature9 = new JCheckBox(" Snapdragon 8 Gen 1");
        JCheckBox feature10 = new JCheckBox(" Tensor Chip");
        JCheckBox feature11 = new JCheckBox(" Fluid AMOLED Display");
        JCheckBox feature12 = new JCheckBox(" Water Resistant");
        JCheckBox feature13 = new JCheckBox(" Lightweight Design");

        // Add checkboxes to features panel
        featuresPanel.add(feature1);
        featuresPanel.add(feature2);
        featuresPanel.add(feature3);
        featuresPanel.add(feature4);
        featuresPanel.add(feature5);
        featuresPanel.add(feature6);
        featuresPanel.add(feature7);
        featuresPanel.add(feature8);
        featuresPanel.add(feature9);
        featuresPanel.add(feature10);
        featuresPanel.add(feature11);
        featuresPanel.add(feature12);
        featuresPanel.add(feature13);

        filtersPanel.add(featuresPanel);

        // Add separator line between the two sections
        filtersPanel.add(new JSeparator(JSeparator.VERTICAL));

        // Storage Section
        filtersPanel.add(new JLabel("Storage:"));
        JPanel storagePanel = new JPanel();
        storagePanel.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 0)); // Horizontal layout with gap between checkboxes
        JCheckBox storage128 = new JCheckBox("128GB");
        JCheckBox storage256 = new JCheckBox("256GB");
        JCheckBox storage512 = new JCheckBox("512GB");
        storagePanel.add(storage128);
        storagePanel.add(storage256);
        storagePanel.add(storage512);
        filtersPanel.add(storagePanel);

        JPanel filterButtonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        JButton applyFiltersButton = new JButton("Apply Filters");
        JButton resetFiltersButton = new JButton("Reset Filters");
        filterButtonsPanel.add(applyFiltersButton);
        filterButtonsPanel.add(resetFiltersButton);
        filtersPanel.add(filterButtonsPanel);

        contentPanel.add(new JScrollPane(filtersPanel), BorderLayout.WEST);

        // Product List Panel (Center)
        JPanel productListPanel = new JPanel();
        productListPanel.setLayout(new BoxLayout(productListPanel, BoxLayout.Y_AXIS));
        productListPanel.setBorder(BorderFactory.createTitledBorder("Products"));

        // Display initial product list
        displayProducts(productListPanel, smartphones);
        contentPanel.add(new JScrollPane(productListPanel), BorderLayout.CENTER);

        // Analytics Panel (Right)
        JPanel analyticsPanel = new JPanel();
        analyticsPanel.setLayout(new BoxLayout(analyticsPanel, BoxLayout.Y_AXIS));
        analyticsPanel.setBorder(BorderFactory.createTitledBorder("Analytics"));
        analyticsPanel.setPreferredSize(new Dimension(400, 0));

        // Add a "See Trending Searches" button
        trendingButton = new JButton("See Trending Searches");
        trendingButton.setAlignmentX(Component.CENTER_ALIGNMENT); // Center align the button
        analyticsPanel.add(trendingButton);

//        // Create the stats button
//        statsButton = new JButton("Show Company and Price Stats");
//        statsButton.setAlignmentX(Component.CENTER_ALIGNMENT); // Center align the button
//        analyticsPanel.add(statsButton); // Add the button to the analytics panel

        // Create the stats panel (initially hidden)
        JPanel statsPanel = new JPanel();
        statsPanel.setLayout(new BoxLayout(statsPanel, BoxLayout.Y_AXIS));
        statsPanel.setBorder(BorderFactory.createTitledBorder("Company and Price Statistics"));

        // Add the stats panel to the content panel but keep it hidden initially
        contentPanel.add(statsPanel, BorderLayout.SOUTH);
        statsPanel.setVisible(false); // Hide the stats panel initially

        // Add the analytics panel to the content panel
        contentPanel.add(new JScrollPane(analyticsPanel), BorderLayout.EAST);

        // Action Listeners for Filters
        applyFiltersButton.addActionListener(e -> {
            try {
                // Get selected filters
                String selectedBrand = (String) companyDropdown.getSelectedItem();
                int minPrice = minPriceField.getText().isEmpty() ? 0 : Integer.parseInt(minPriceField.getText());
                int maxPrice = maxPriceField.getText().isEmpty() ? Integer.MAX_VALUE : Integer.parseInt(maxPriceField.getText());

                List<String> selectedFeatures = new ArrayList<>();
                if (feature1.isSelected()) selectedFeatures.add("5G Support");
                if (feature2.isSelected()) selectedFeatures.add("Wireless Charging");
                if (feature3.isSelected()) selectedFeatures.add("Headphone Jack");
                if (feature4.isSelected()) selectedFeatures.add("High-Resolution Camera");
                if (feature5.isSelected()) selectedFeatures.add(" OLED Display");
                if (feature6.isSelected()) selectedFeatures.add(" A16 Bionic Chip");
                if (feature7.isSelected()) selectedFeatures.add(" A17 Bionic Chip");
                if (feature8.isSelected()) selectedFeatures.add(" MediaTek Dimensity");
                if (feature9.isSelected()) selectedFeatures.add(" Snapdragon 8 Gen 1");
                if (feature10.isSelected()) selectedFeatures.add(" Tensor Chip");
                if (feature11.isSelected()) selectedFeatures.add(" Fluid AMOLED Display");
                if (feature12.isSelected()) selectedFeatures.add(" Water Resistant");
                if (feature13.isSelected()) selectedFeatures.add(" Lightweight Design");

                List<Integer> selectedStorage = new ArrayList<>();
                if (storage128.isSelected()) selectedStorage.add(128);
                if (storage256.isSelected()) selectedStorage.add(256);
                if (storage512.isSelected()) selectedStorage.add(512);

                // Filter smartphones
                List<Smartphone> filteredSmartphones = Filter.filterSmartphones(
                        smartphones, selectedBrand, minPrice, maxPrice, selectedFeatures, selectedStorage
                );

                // Update product list panel
                productListPanel.removeAll();
                if (filteredSmartphones.isEmpty()) {
                    productListPanel.add(new JLabel("No results found.", JLabel.CENTER));
                } else {
                    displayProducts(productListPanel, filteredSmartphones);
                }
                productListPanel.revalidate();
                productListPanel.repaint();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, "Error applying filters: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        resetFiltersButton.addActionListener(e -> {
            // Reset filters
            companyDropdown.setSelectedIndex(0);
            minPriceField.setText("");
            maxPriceField.setText("");
            feature1.setSelected(false);
            feature2.setSelected(false);
            feature3.setSelected(false);
            feature4.setSelected(false);
            feature5.setSelected(false);
            feature6.setSelected(false);
            feature7.setSelected(false);
            feature8.setSelected(false);
            feature9.setSelected(false);
            feature10.setSelected(false);
            feature11.setSelected(false);
            feature12.setSelected(false);
            feature13.setSelected(false);
            storage128.setSelected(false);
            storage256.setSelected(false);
            storage512.setSelected(false);

            // Reset product list
            productListPanel.removeAll();
            displayProducts(productListPanel, smartphones);
            productListPanel.revalidate();
            productListPanel.repaint();
        });

        // Action Listener for Search Button
        searchButton.addActionListener(e -> {
            // Get the search query from the search bar
            String query = searchField.getText().toLowerCase();

            // Log the search query to CSV
            logSearchQuery(query);

            // Call the search function to filter the products based on the query
            searchSmartphones(query, smartphones, productListPanel);
        });

        // Action Listener for Spell Checker Button
        spellCheckerButton.addActionListener(e -> {
            // Open the Spell Checker GUI
            new SpellCheckerGUI(spellChecker);
        });

        // Add ActionListener for the "See Trending Searches" button
        trendingButton.addActionListener(e -> {
            displayTrendingSearches(analyticsPanel);
        });

        // Add ActionListener for the "Show Company and Price Stats" button
        statsButton.addActionListener(e -> {
            JFrame statsFrame = new JFrame("Company and Price Statistics");
            statsFrame.setSize(400, 300);
            statsFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            statsFrame.setLayout(new BorderLayout());

            JPanel statsPanel1 = new JPanel();
            statsPanel1.setLayout(new BoxLayout(statsPanel1, BoxLayout.Y_AXIS));
            statsPanel1.setBorder(BorderFactory.createTitledBorder("Statistics"));

            Map<String, List<Integer>> companyPrices = new HashMap<>();
            for (Smartphone smartphone : smartphones) {
                companyPrices.computeIfAbsent(smartphone.brand, k -> new ArrayList<>()).add(smartphone.price);
            }

            for (Map.Entry<String, List<Integer>> entry : companyPrices.entrySet()) {
                String company = entry.getKey();
                List<Integer> prices = entry.getValue();
                double averagePrice = prices.stream().mapToInt(Integer::intValue).average().orElse(0);
                int minPrice = prices.stream().mapToInt(Integer::intValue).min().orElse(0);
                int maxPrice = prices.stream().mapToInt(Integer::intValue).max().orElse(0);

                statsPanel1.add(new JLabel("Company: " + company));
                statsPanel1.add(new JLabel("Average Price: $" + String.format("%.2f", averagePrice)));
                statsPanel1.add(new JLabel("Min Price: $" + minPrice));
                statsPanel1.add(new JLabel("Max Price: $" + maxPrice));
                statsPanel1.add(Box.createVerticalStrut(10)); // Add some space between companies
            }

            statsFrame.add(new JScrollPane(statsPanel1), BorderLayout.CENTER);
            statsFrame.setVisible(true); // Show the stats frame
        });

        // Add ActionListener for the "Open Web Crawler" button
        webCrawlerButton.addActionListener(e -> {
            WebCrawlerGUI webCrawlerGUI = new WebCrawlerGUI();
            webCrawlerGUI.setVisible(true);
        });

        // Create the suggestion menu
        suggestionMenu = new JPopupMenu();

        // Show the frame
        frame.setVisible(true);
    }

    private static void logSearchQuery(String query) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("C:\\Users\\mitan\\OneDrive\\Desktop\\Mobile_Recommendation\\src\\search_log.csv", true))) {
            writer.write(query);
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void searchSmartphones(String query, List<Smartphone> smartphones, JPanel productListPanel) {
        List<Smartphone> filteredSmartphones = new ArrayList<>();
        String normalizedQuery = query.toLowerCase().trim(); // Normalize the query

        for (Smartphone smartphone : smartphones) {
            boolean matchesQuery = false;

            // Normalize smartphone attributes for comparison
            String normalizedName = smartphone.name.toLowerCase();
            String normalizedBrand = smartphone.brand.toLowerCase();

            // Check if the smartphone name or brand contains the query
            if (normalizedName.contains(normalizedQuery) || normalizedBrand.contains(normalizedQuery)) {
                matchesQuery = true;
            }

            // Try to parse the query as storage and check if it matches
            try {
                int queryStorage = Integer.parseInt(normalizedQuery);
                if (smartphone.storage == queryStorage) {
                    matchesQuery = true;
                }
            } catch (NumberFormatException ex) {
                // Ignore if query is not a valid number
            }

            // Check if any of the features match the query
            for (String feature : smartphone.features) {
                if (feature.toLowerCase().contains(normalizedQuery)) {
                    matchesQuery = true;
                    break;
                }
            }

            // Add the smartphone to the filtered list if it matches the query
            if (matchesQuery) {
                filteredSmartphones.add(smartphone);
            }
        }

        // Update the product list panel with the results
        productListPanel.removeAll();
        if (filteredSmartphones.isEmpty()) {
            productListPanel.add(new JLabel("No results found.", JLabel.CENTER));
        } else {
            displayProducts(productListPanel, filteredSmartphones);
        }
        productListPanel.revalidate();
        productListPanel.repaint();
    }

    private static void displayProducts(JPanel productListPanel, List<Smartphone> smartphones) {
        for (Smartphone smartphone : smartphones) {
            JPanel productCard = new JPanel(new GridLayout(1, 2, 10, 10));
            productCard.setBorder(BorderFactory.createLineBorder(Color.GRAY));

            JPanel productDetails = new JPanel(new GridLayout(5, 1));
            JLabel nameLabel = new JLabel(smartphone.name);
            nameLabel.setFont(new Font("Arial", Font.BOLD, 18));
            nameLabel.setForeground(Color.BLUE);
            productDetails.add(nameLabel);

            JLabel brandLabel = new JLabel(smartphone.brand);
            brandLabel.setFont(new Font("Arial", Font.ITALIC, 18));
            brandLabel.setForeground(Color.RED);
            productDetails.add(brandLabel);

            JLabel priceLabel = new JLabel(String.valueOf("$ " + smartphone.price));
            priceLabel.setFont(new Font("Arial", Font.BOLD, 18));
            priceLabel.setForeground(Color.BLACK);
            productDetails.add(priceLabel);

            productDetails.add(new JLabel(smartphone.storage + " GB"));
            productDetails.add(new JLabel(String.join(", ", smartphone.features)));

            productCard.add(productDetails);
            productListPanel.add(productCard);
        }
    }

    private static void displayTrendingSearches(JPanel analyticsPanel) {
        Map<String, Integer> searchFrequency = new HashMap<>();
        try (BufferedReader br = new BufferedReader(new FileReader("C:\\Users\\mitan\\OneDrive\\Desktop\\Mobile_Recommendation\\src\\search_log.csv"))) {
            String line;
            while ((line = br.readLine()) != null) {
                searchFrequency.put(line, searchFrequency.getOrDefault(line, 0) + 1);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        List<Map.Entry<String, Integer>> sortedEntries = new ArrayList<>(searchFrequency.entrySet());
        sortedEntries.sort((a, b) -> b.getValue().compareTo(a.getValue()));

        analyticsPanel.removeAll();
        analyticsPanel.add(new JLabel("Trending Searches:", JLabel.CENTER));
        for (Map.Entry<String, Integer> entry : sortedEntries) {
            analyticsPanel.add(new JLabel(entry.getKey() + " - " + entry.getValue() + " times"));
        }

        JButton backButton = new JButton("Back");
        backButton.addActionListener(e -> {
            // Clear the analytics panel and reset to the main view
            analyticsPanel.removeAll();
            analyticsPanel.add(trendingButton); // Re-add the trending button
            analyticsPanel.revalidate();
            analyticsPanel.repaint();
        });
        analyticsPanel.add(backButton);

        analyticsPanel.revalidate();
        analyticsPanel.repaint();
    }

    private static List<Smartphone> loadSmartphoneData(String csvFilePath) {
        List<Smartphone> smartphones = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(csvFilePath))) {
            String line;
            boolean isFirstLine = true; // Flag to skip the header
            while ((line = br.readLine()) != null) {
                if (isFirstLine) {
                    isFirstLine = false; // Skip the first line
                    continue;
                }
                String[] parts = line.split(",");
                if (parts.length >= 5) { // Expect at least 5 fields now
                    String name = parts[0].trim();
                    String brand = parts[1].trim();
                    int price = Integer.parseInt(parts[2].trim());
                    List<String> features = Arrays.asList(parts[3].trim().split(";"));
                    int storage = Integer.parseInt(parts[4].trim()); // Parse storage
                    smartphones.add(new Smartphone(name, brand, price, features, storage));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error reading data from file: " + e.getMessage(), "File Error", JOptionPane.ERROR_MESSAGE);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Invalid data format in CSV file: " + e.getMessage(), "Data Error", JOptionPane.ERROR_MESSAGE);
        }
        return smartphones;
    }

    // Inner class for Smartphone
    static class Smartphone {
        String name, brand;
        int price;
        List<String> features;
        int storage;

        public Smartphone(String name, String brand, int price, List<String> features, int storage) {
            this.name = name;
            this.brand = brand;
            this.price = price;
            this.features = features;
            this.storage = storage;
        }
    }

    // Trie class for word completion
    static class TrieNode {
        Map<Character, TrieNode> children = new HashMap<>();
        boolean isEndOfWord = false;
    }

    static class Trie {
        private final TrieNode root;

        public Trie() {
            root = new TrieNode();
        }

        public void insert(String word) {
            TrieNode current = root;
            for (char c : word.toCharArray()) {
                current = current.children.computeIfAbsent(c, k -> new TrieNode());
            }
            current.isEndOfWord = true;
        }

        public List<String> prefixWord(String prefix) {
            List<String> results = new ArrayList<>();
            TrieNode current = root;
            for (char c : prefix.toCharArray()) {
                current = current.children.get(c);
                if (current == null) {
                    return results; // No suggestions
                }
            }
            findAllWords(current, results, prefix);
            return results;
        }

        private void findAllWords(TrieNode node, List<String> results, String prefix) {
            if (node.isEndOfWord) {
                results.add(prefix);
            }
            for (Map.Entry<Character, TrieNode> entry : node.children.entrySet()) {
                findAllWords(entry.getValue(), results, prefix + entry.getKey());
            }
        }
    }

    private static void displaySuggestions(List<String> suggestions, JTextField searchField) {
        suggestionMenu.removeAll(); // Clear previous suggestions

        for (String suggestion : suggestions) {
            JMenuItem item = new JMenuItem(suggestion);
            item.addActionListener(e -> {
                searchField.setText(suggestion); // Set the selected suggestion in the search field
                suggestionMenu.setVisible(false); // Hide the suggestion menu
                searchField.requestFocus(); // Refocus on the search field
            });
            suggestionMenu.add(item);
        }

        // Show the suggestion menu below the search field only if there are suggestions
        if (!suggestions.isEmpty()) {
            suggestionMenu.show(searchField, 0, searchField.getHeight());
        } else {
            suggestionMenu.setVisible(false); // Hide the menu if no suggestions
        }
    }
}

// Web Crawler GUI Class
class WebCrawlerGUI extends JFrame {
    private JTextField urlField;
    private JTextArea resultArea;

    public WebCrawlerGUI() {
        setTitle("Web Crawler");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // URL input panel
        JPanel inputPanel = new JPanel();
        urlField = new JTextField(30);
        JButton crawlButton = new JButton("Crawl");
        inputPanel.add(new JLabel("Enter URL:"));
        inputPanel.add(urlField);
        inputPanel.add(crawlButton);
        add(inputPanel, BorderLayout.NORTH);

        // Result area
        resultArea = new JTextArea();
        resultArea.setEditable(false);
        add(new JScrollPane(resultArea), BorderLayout.CENTER);

        // Crawl button action
        crawlButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String url = urlField.getText();
                crawlWebsite(url);
            }
        });
    }

    private void crawlWebsite(String url) {
        new Thread(() -> {
            try {
                UWCrawler crawler = new UWCrawler();
                List<String> visitedUrls = crawler.crawl(url);

                // Clear previous results
                resultArea.setText("Crawling completed. Visited URLs:\n");
                for (String visitedUrl : visitedUrls) {
                    resultArea.append(visitedUrl + "\n"); // Append each visited URL to the JTextArea
                }
            } catch (Exception e) {
                resultArea.setText("Error: " + e.getMessage());
            }
        }).start();
    }
}

// Crawler Class
class UWCrawler {
    private static final String SAVE_DIRECTORY = "saved_pages";
    private static final int MAX_PAGES = 100;  // Limit to avoid overloading the server

    private Set<String> visitedPages = new HashSet<>();
    private Queue<String> pagesToVisit = new LinkedList<>();

    public List<String> crawl(String startUrl) {
        List<String> visitedUrls = new ArrayList<>();
        pagesToVisit.add(startUrl);
        while (!pagesToVisit.isEmpty() && visitedPages.size() < MAX_PAGES) {
            String url = pagesToVisit.poll();
            if (url != null && !visitedPages.contains(url)) {
                visitedUrls.add(url);
                visitPage(url);
            }
        }
        return visitedUrls;
    }

    private void visitPage(String url) {
        try {
            Document doc = Jsoup.connect(url).get();
            System.out.println("Visiting and Crawling: " + url);
            visitedPages.add(url);

            // Save the HTML content of the page to a file
            saveHtmlToFile(doc, url);

            // Extract and add links to other pages on the site
            Elements links = doc.select("a[href]");
            for (Element link : links) {
                String linkHref = link.absUrl("href");
                if (!visitedPages.contains(linkHref)) {
                    pagesToVisit.add(linkHref);
                }
            }
        } catch (IOException e) {
            System.err.println("Error accessing: " + url + " - " + e.getMessage());
        }
    }

    private void saveHtmlToFile(Document doc, String url) {
        try {
            // Create the directory if it doesn't exist
            File directory = new File(SAVE_DIRECTORY);
            if (!directory.exists()) {
                directory.mkdir();
            }

            // Generate a safe filename using the URL
            String safeFileName = Paths.get(url.replace("https://", "").replaceAll("[^a-zA-Z0-9.-]", "_")).getFileName().toString();
            File file = new File(SAVE_DIRECTORY + File.separator + safeFileName + ".html");

            // Save HTML content to a file
            FileWriter writer = new FileWriter(file);
            writer.write(doc.html());
            writer.close();

//            System.out.println("Saved page to: " + file.getAbsolutePath());
        } catch (IOException e) {
            System.err.println("Error saving file for URL: " + url + " - " + e.getMessage());
        }
    }
}