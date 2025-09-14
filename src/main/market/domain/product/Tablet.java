package main.market.domain.product;

public class Tablet extends ElectronicDevice {
    private TabletModel model;
    private boolean touchPen;

    public Tablet(int price, int viewCount, String name, String address, String storage, String color, String quality, TabletModel model, boolean touchPen) {
        super(price, viewCount, name, address, storage, color, quality);
        this.model = model;
        this.touchPen = touchPen;
    }

    public String getDetailInformation() {
        return "상품명: " + getName() +
            "\n가격: " + getPrice() +
            "\n거래 희망 지역: " + getArea() +
            "\n용량: " + getStorage() +
            "\n모델: " + model.getModel() +
            "\n터치펜 사용 가능 여부: " + (touchPen ? "O" : "X") +
            "\n색상: " + getColor() +
            "\n품질: " + getQuality() +
            "\n조회수: " + getViewCount();
    }
}
