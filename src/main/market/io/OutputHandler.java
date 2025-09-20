package main.market.io;

import main.market.domain.product.Product;

import java.util.List;

public class OutputHandler {
    public void askPageSelecting() {
        System.out.println("\n🙇‍♂️ 어서오세요 중고마켓입니다. 이동하시려는 페이지를 선택해주세요.(다른 번호 입력 시 프로그램을 종료합니다.)");
        System.out.println("1. 관심 목록 페이지\n2. 상품 조회 페이지");
        System.out.print(">>> ");
    }

    public void askTradingArea() {
        System.out.print("\n거래 희망 지역을 입력해주세요.(서울, 경기, 인천) \n>>> ");
    }

    public void showProductInformation(List<Product> products) {
        for (int i = 0; i < products.size(); i++) {
            Product product = products.get(i);
            System.out.println("\n-----------제품 " + (i + 1) + "-----------");
            System.out.println(product.getInformation());
            System.out.println("---------------------------");
        }
        System.out.println("\n상세 정보를 조회할 제품의 번호를 입력해주세요(1 ~ " + products.size() + ") 다른 번호 입력 시 페이지 선택으로 돌아갑니다.");
    }

    public void showWishlistComment(boolean isEmptyList) {
        if (isEmptyList) {
            System.out.println("\n❌ 현재 관심 목록에 담긴 제품이 없습니다. ❌");
        }  else {
            System.out.println("\n관심 목록 페이지입니다.");
        }
    }

    public void showDetailInformation(Product product) {
        System.out.println("===========제품 상세 정보===========");
        System.out.println(product.getDetailInformation());
        System.out.println("==================================");
    }

    public void showWishlistPageTask() {
        System.out.println("\n원하시는 작업을 선택해주세요. 다른 키 입력 시 페이지 선택으로 돌아갑니다.");
        System.out.println("1. 거래 요청 보내기\n2. 관심 목록에서 삭제하기");
        System.out.print(">>> ");
    }

    public void showProductPageTask() {
        System.out.println("\n원하시는 작업을 선택해주세요. 다른 키 입력 시 페이지 선택으로 돌아갑니다.");
        System.out.println("1. 거래 요청하기\n2. 관심 목록에 추가하기");
        System.out.print(">>> ");
    }

    public void showTradingRequestSuccess(String productName) {
        System.out.println("\n🎉 [ " + productName + " ] 제품 거래 요청을 성공적으로 전송했습니다. 🎉");
        System.out.println("\n이용해주셔서 감사합니다.");
    }

    public void showWishlistRemoveComment(String productName) {
        System.out.println("\n🗑 관심 목록에서 [ " + productName + " ] 제품이 제거 되었습니다. 🗑");
    }

    public void showWishlistAddComment(String productName) {
        System.out.println("\n🎊 관심 목록에 [ " + productName + " ] 제품이 추가 되었습니다. 🎊");
    }

    public void showSimpleMessage(String message) {
        System.out.println(message);
    }
}
