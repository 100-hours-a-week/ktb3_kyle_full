public class ElectronicDevice extends Product {
    String storage;
    String color;
    String quality;

    public ElectronicDevice(int price, int viewCount, String name, String address, String storage, String color, String quality) {
        super(price, viewCount, name, address);
        this.storage = storage;
        this.color = color;
        this.quality = quality;
    }
}
