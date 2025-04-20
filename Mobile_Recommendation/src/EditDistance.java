// Class to calculate the edit distance between two words
class EditDistance {
    // Method to calculate the edit distance using dynamic programming
    public static int calculate(String w1, String w2) {
        int[][] dp = new int[w1.length() + 1][w2.length() + 1];

        // Initialize the dynamic programming table
        for (int i = 0; i <= w1.length(); i++) {
            for (int j = 0; j <= w2.length(); j++) {
                if (i == 0) {
                    dp[i][j] = j; // Cost of insertions
                } else if (j == 0) {
                    dp[i][j] = i; // Cost of deletions
                } else if (w1.charAt(i - 1) == w2.charAt(j - 1)) {
                    dp[i][j] = dp[i - 1][j - 1]; // No operation
                } else {
                    dp[i][j] = 1 + Math.min(dp[i - 1][j], Math.min(dp[i][j - 1], dp[i - 1][j - 1])); // Substitution
                }
            }
        }
        return dp[w1.length()][w2.length()];
    }
}