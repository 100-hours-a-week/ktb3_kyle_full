package main.market.domain.product;

public class ElectronicDevice extends Product {
    private String storage;
    private String color;
    private String quality;

    public ElectronicDevice(int price, int viewCount, String name, String address, String storage, String color, String quality) {
        super(price, viewCount, name, address);
        this.storage = storage;
        this.color = color;
        this.quality = quality;
    }

    public String getStorage() {
        return storage;
    }

    public String getColor() {
        return color;
    }

    public String getQuality() {
        return quality;
    }
}
