import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

public class Main {
    static Scanner scanner = new Scanner(System.in);
    static HashMap<String, List<Product>> products = new HashMap<>();

    public static void main(String[] args) {
        initialize();

        while (true) {
            String area = selectTradingArea();
            List<Product> areaProducts = getAreaProducts(area);

            while (true) {
                printProductInformation(areaProducts);
                Product selectedProduct = selectProduct(areaProducts);

                if (selectedProduct == null) {
                    System.out.println("\n지역 선택 페이지로 돌아가시겠습니다? (예 : Y, 아니요: 다른 키)");
                    String input = scanner.nextLine();
                    if (input.equals("Y")) {
                        break;
                    }
                    continue;
                }

                printDetailInformation(selectedProduct);
                askTransactionRequest();
            }
        }
    }

    public static String selectTradingArea() {
        String input = "";
        while (true) {
            System.out.print("\n거래 희망 지역을 알려주세요.(서울, 경기, 인천) \n거래 지역 입력 : ");
            input = scanner.nextLine();
            if (input.equals("서울") || input.equals("경기") || input.equals("인천")) break;
            System.out.println("\n거래는 [서울, 경기, 인천] 만 가능합니다. 지역을 다시 입력해주세요.\n\n");
        }
        return input;
    }

    public static List<Product> getAreaProducts(String area) {
        return products.get(area);
    }

    public static void printProductInformation(List<Product> areaProducts) {
        for (int i = 0; i < areaProducts.size(); i++) {
            Product product = areaProducts.get(i);
            System.out.println("-----------제품 "+ (i + 1)+ "-----------");
            System.out.println(product.getInformation());
            System.out.println("---------------------------");
        }
    }

    public static Product selectProduct(List<Product> areaProducts) {
        System.out.println("\n상세 정보를 조회할 제품의 번호를 입력해주세요(1 ~ " + areaProducts.size() + ") 다른 번호 입력 시 제품 목록으로 돌아갑니다.");
        String input = scanner.nextLine();
        int num = Integer.parseInt(input);
        if (num < 1  || num > areaProducts.size()) return null;

        Product product = areaProducts.get(num - 1);
        product.increaseViewCount();
        return product;
    }

    public static void printDetailInformation(Product product) {
        String productInformation = "";
        if (product instanceof Laptop laptop) {
            productInformation = laptop.getDetailInformation();
        } else if (product instanceof Tablet tablet) {
            productInformation = tablet.getDetailInformation();
        }
        System.out.println("===========제품 상세 정보===========");
        System.out.println(productInformation);
        System.out.println("==================================");
    }

    public static void askTransactionRequest() {
        System.out.println("\n거래 요청을 보내시겠습니까? (예 : Y, 아니요: 다른 키) 다른 키 입력 시 상품 선택 페이지로 돌아갑니다.");
        String input = scanner.nextLine();
        if (input.equals("Y")) {
            System.out.println("\n거래 요청을 성공적으로 전송했습니다.");
            System.out.println("\n이용해주셔서 감사합니다.");
            System.exit(0);
        }
        System.out.println("\n거래 요청을 취소하였습니다. 상품 선택 페이지로 돌아갑니다.");
    }

    public static void initialize() {
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
            products.compute(product.area, (k, v) ->
                v == null ? new ArrayList<>() : v
            ).add(product);
        }
    }
}
