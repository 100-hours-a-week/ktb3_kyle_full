public class Laptop extends ElectronicDevice {
    int ram;
    boolean keyboardLight;

    public Laptop(int price, int viewCount, String name, String address, String storage, String color, String quality, int ram, boolean keyboardLight) {
        super(price, viewCount, name, address, storage, color, quality);
        this.ram = ram;
        this.keyboardLight = keyboardLight;
    }

    public String getDetailInformation() {
        return "상품명: " + name +
            "\n가격: " + price +
            "\n거래 희망 지역: " + area +
            "\n용량: " + storage +
            "\n메모리: " + ram + "GB" +
            "\n키보드 백라이트 여부: " + (keyboardLight ? "O" : "X") +
            "\n색상: " + color +
            "\n품질: " + quality +
            "\n조회수: " + viewCount;
    }
}
