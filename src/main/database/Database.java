package main.database;

import main.market.domain.product.Laptop;
import main.market.domain.product.Product;
import main.market.domain.product.Tablet;
import main.market.domain.product.TabletModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class Database {
    private final HashMap<String, List<Product>> products = new HashMap<>();

    public List<Product> findByArea(String area) {
        return products.get(area);
    }

    public List<Product> findMostViewedProducts() {
        int maxViewCount = products.values().stream()
            .flatMap(List::stream)
            .mapToInt(Product::getViewCount)
            .max()
            .orElse(1);

        return products.values().stream()
            .flatMap(List::stream)
            .filter(product -> product.getViewCount() == maxViewCount)
            .toList();
    }

    public void initialize() {
        Laptop laptop1 = new Laptop(
            900000,
            0,
            "MacBook Air 15(M2)",
            "서울",
            "512GB",
            "미드나이트",
            "최상",
            8,
            true
        );

        Laptop laptop2 = new Laptop(
            1600000,
            0,
            "MacBook Pro 14(M3)",
            "경기",
            "512GB",
            "스페이스 블랙",
            "상",
            18,
            true
        );

        Laptop laptop3 = new Laptop(
            600000,
            0,
            "갤럭시북 프로 360",
            "인천",
            "1TB",
            "화이트",
            "중",
            16,
            false
        );

        Tablet tablet1 = new Tablet(
            300000,
            0,
            "아이패드 에어4",
            "서울",
            "64GB",
            "화이트",
            "하",
            TabletModel.WIFI,
            false
        );

        Tablet tablet2 = new Tablet(
            500000,
            0,
            "갤럭시탭 S7+",
            "경기",
            "256GB",
            "블랙",
            "최상",
            TabletModel.CELLULAR,
            true
        );

        Tablet tablet3 = new Tablet(
            550000,
            0,
            "아이패드 프로 11 2세대",
            "서울",
            "64GB",
            "스페이스 그레이",
            "상",
            TabletModel.CELLULAR,
            false
        );

        List<Product> productList = new ArrayList<>();
        productList.add(laptop1);
        productList.add(laptop2);
        productList.add(laptop3);
        productList.add(tablet1);
        productList.add(tablet2);
        productList.add(tablet3);

        for (Product product : productList) {
            products.compute(product.getArea(), (k, v) ->
                v == null ? new ArrayList<>() : v
            ).add(product);
        }
    }
}
