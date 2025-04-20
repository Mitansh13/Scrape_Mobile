import java.util.List;
import java.util.stream.Collectors;

public class Filter {
    public static List<SmartphoneRecommendationGUI.Smartphone> filterSmartphones(
            List<SmartphoneRecommendationGUI.Smartphone> smartphones,
            String brand,
            int minPrice,
            int maxPrice,
            List<String> requiredFeatures,
            List<Integer> storageOptions) {

        return smartphones.stream()
                .filter(s -> brand.equals("All") || s.brand.equalsIgnoreCase(brand))
                .filter(s -> s.price >= minPrice && s.price <= maxPrice)
                .filter(s -> requiredFeatures.isEmpty() || s.features.containsAll(requiredFeatures))
                .filter(s -> storageOptions.isEmpty() || storageOptions.contains(s.storage))
                .collect(Collectors.toList());
    }
}