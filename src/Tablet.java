public class Tablet extends ElectronicDevice {
    TabletModel model;
    boolean touchPen;

    public Tablet(int price, int viewCount, String name, String address, String storage, String color, String quality, TabletModel model, boolean touchPen) {
        super(price, viewCount, name, address, storage, color, quality);
        this.model = model;
        this.touchPen = touchPen;
    }

    public String getDetailInformation() {
        return "상품명: " + name +
            "\n가격: " + price +
            "\n거래 희망 지역: " + area +
            "\n용량: " + storage +
            "\n모델: " + model.getModel() +
            "\n터치펜 사용 가능 여부: " + (touchPen ? "O" : "X") +
            "\n색상: " + color +
            "\n품질: " + quality +
            "\n조회수: " + viewCount;
    }
}
