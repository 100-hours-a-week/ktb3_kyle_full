package main.market.domain.product;

public class Laptop extends ElectronicDevice {
    private int ram;
    private boolean keyboardLight;

    public Laptop(int price, int viewCount, String name, String address, String storage, String color, String quality, int ram, boolean keyboardLight) {
        super(price, viewCount, name, address, storage, color, quality);
        this.ram = ram;
        this.keyboardLight = keyboardLight;
    }

    public String getDetailInformation() {
        return "상품명: " + getName() +
            "\n가격: " + getPrice() +
            "\n거래 희망 지역: " + getArea() +
            "\n용량: " + getStorage() +
            "\n메모리: " + ram + "GB" +
            "\n키보드 백라이트 여부: " + (keyboardLight ? "O" : "X") +
            "\n색상: " + getColor() +
            "\n품질: " + getQuality() +
            "\n조회수: " + getViewCount();
    }
}
