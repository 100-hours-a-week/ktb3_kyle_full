package main.market.domain.product;

public abstract class Product {
    private int price;
    private int viewCount;
    private String name;
    private String area;

    public Product(int price, int viewCount, String name, String area) {
        this.price = price;
        this.viewCount = viewCount;
        this.name = name;
        this.area = area;
    }

    public String getArea() {
        return area;
    }

    public int getPrice() {
        return price;
    }

    public int getViewCount() {
        return viewCount;
    }

    public String getName() {
        return name;
    }

    public String getInformation() {
        return "상품명: " + name +
            "\n가격: " + price +
            "\n거래 희망 지역: " + area +
            "\n조회수: " + viewCount;
    }

    public abstract String getDetailInformation();

    public void increaseViewCount() {
        viewCount++;
    }
}
