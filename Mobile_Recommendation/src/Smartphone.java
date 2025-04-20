import java.util.List;

public class Smartphone {
    String name;
    String brand;
    int price;
    List<String> features;

    public Smartphone(String name, String brand, int price, List<String> features) {
        this.name = name;
        this.brand = brand;
        this.price = price;
        this.features = features;
    }

    @Override
    public String toString() {
        return name + " (" + brand + ") - $" + price + ", Features: " + features;
    }
}
