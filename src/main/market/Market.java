package main.market;

import main.database.Database;
import main.market.domain.product.Laptop;
import main.market.domain.product.Product;
import main.market.domain.product.Tablet;
import main.market.domain.user.User;

import java.util.List;
import java.util.Scanner;

public class Market {
    private final static Scanner scanner = new Scanner(System.in);
    private final static String WISHLIST_PAGE = "wishlist";
    private final static String PRODUCT_PAGE = "product";
    private final static String EXIT = "exit";
    private boolean exit = false;
    private final Database database;
    private final User user;

    public Market(Database database, User user) {
        this.database = database;
        this.user = user;
    }

    public void run() {
        while (true) {
            switch (selectPage()) {
                case WISHLIST_PAGE:

                    if (user.isEmptyList()) {
                        System.out.println("\n❌ 현재 관심 목록에 담긴 제품이 없습니다. ❌");
                    } else {
                        System.out.println("\n관심 목록 페이지입니다.");
                        List<Product> userWishlist = user.getWishlist();
                        showProductInformation(userWishlist);
                        Product selectedProduct = selectProduct(userWishlist);

                        if (isSelected(selectedProduct)) {
                            showDetailInformation(selectedProduct);
                            ask(selectedProduct);
                        }
                    }
                    System.out.println("\n페이지 선택으로 돌아갑니다.");
                    break;
                case PRODUCT_PAGE:
                    String selectedArea = selectTradingArea();
                    List<Product> areaProducts = getAreaProducts(selectedArea);
                    showProductInformation(areaProducts);
                    Product selectedProduct = selectProduct(areaProducts);

                    if (isSelected(selectedProduct)) {
                        showDetailInformation(selectedProduct);
                        askTask(selectedProduct);
                    }
                    System.out.println("\n페이지 선택으로 돌아갑니다.");
                    break;
                case EXIT:
                    exit = true;
                    break;
            }

            if (exit) break;
        }
    }

    private String selectPage() {
        System.out.println("🙇‍♂️ 어서오세요 중고마켓입니다. 이동하시려는 페이지를 선택해주세요.(다른 번호 입력 시 프로그램을 종료합니다.)");
        System.out.println("1. 관심 목록 페이지\n2. 상품 조회 페이지");
        System.out.print(">>> ");

        String input = scanner.nextLine();
        if (input.equals("1")) {
            return WISHLIST_PAGE;
        } else if (input.equals("2")) {
            return PRODUCT_PAGE;
        }
        return EXIT;
    }

    private String selectTradingArea() {
        String input = "";
        while (true) {
            System.out.print("\n거래 희망 지역을 알려주세요.(서울, 경기, 인천) \n거래 지역 입력 : ");
            input = scanner.nextLine();
            if (isAvailableArea(input)) break;
            System.out.println("\n거래는 [서울, 경기, 인천] 만 가능합니다. 지역을 다시 입력해주세요.\n\n");
        }
        return input;
    }

    private boolean isAvailableArea(String input) {
        return input.equals("서울") || input.equals("경기") || input.equals("인천");
    }

    private List<Product> getAreaProducts(String area) {
        return database.findByArea(area);
    }

    private void showProductInformation(List<Product> products) {
        for (int i = 0; i < products.size(); i++) {
            Product product = products.get(i);
            System.out.println("\n-----------제품 " + (i + 1) + "-----------");
            System.out.println(product.getInformation());
            System.out.println("---------------------------");
        }
    }

    private Product selectProduct(List<Product> areaProducts) {
        System.out.println("\n상세 정보를 조회할 제품의 번호를 입력해주세요(1 ~ " + areaProducts.size() + ") 다른 번호 입력 시 페이지 선택으로 돌아갑니다.");
        String input = scanner.nextLine();
        int num = Integer.parseInt(input);
        if (num < 1 || num > areaProducts.size()) return null;

        Product product = areaProducts.get(num - 1);
        product.increaseViewCount();
        return product;
    }

    private boolean isSelected(Product selectedProduct) {
        return selectedProduct != null;
    }

    private void showDetailInformation(Product product) {
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

    private void askTask(Product selectedProduct) {
        System.out.println("\n원하시는 작업을 선택해주세요. 다른 키 입력 시 페이지 선택으로 돌아갑니다.");
        System.out.println("1. 거래 요청하기\n2. 관심 목록에 추가하기");
        System.out.print(">>> ");

        String input = scanner.nextLine();
        if (input.equals("1")) {
            System.out.println("\n거래 요청을 성공적으로 전송했습니다.");
            System.out.println("\n이용해주셔서 감사합니다.");
        } else if (input.equals("2")) {
            System.out.println("\n🎊 관심 목록에 [ " + selectedProduct.getName() + " ] 제품이 추가 되었습니다. 🎊");
            user.addWishlist(selectedProduct);
        }
    }

    private void ask(Product selectedProduct) {
        System.out.println("\n원하시는 작업을 선택해주세요. 다른 키 입력 시 페이지 선택으로 돌아갑니다.");
        System.out.println("1. 거래 요청 보내기\n2. 관심 목록에서 삭제하기");
        System.out.print(">>> ");
        String input = scanner.nextLine();

        if (input.equals("1")) {
            System.out.println("\n거래 요청을 성공적으로 전송했습니다.");
            System.out.println("\n이용해주셔서 감사합니다.");
        } else if (input.equals("2")) {
            System.out.println("\n🗑 관심 목록에서 [ " + selectedProduct.getName() + " ] 제품이 제거 되었습니다. 🗑");
            user.removeWishlist(selectedProduct);
        }
    }
}
