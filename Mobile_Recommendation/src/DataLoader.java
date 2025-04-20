import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DataLoader {
    public static List<Smartphone> loadSmartphones(String filePath) {
        List<Smartphone> smartphones = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 4) {
                    String name = parts[0];
                    String brand = parts[1];
                    int price = Integer.parseInt(parts[2].trim());
                    List<String> features = Arrays.asList(parts[3].split("\\|"));
                    smartphones.add(new Smartphone(name, brand, price, features));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return smartphones;
    }
}
