public class Product {
    int price;
    int viewCount;
    String name;
    String area;

    public Product(int price, int viewCount, String name, String area) {
        this.price = price;
        this.viewCount = viewCount;
        this.name = name;
        this.area = area;
    }

    public String getInformation() {
        return "상품명: " + name +
            "\n가격: " + price +
            "\n거래 희망 지역: " + area +
            "\n조회수: " + viewCount;
    }

    public void increaseViewCount() {
        viewCount++;
    }
}
