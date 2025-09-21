package main.market.io;

import main.market.domain.product.Product;
import main.market.exception.MarketException;
import main.market.io.action.PageStatus;
import main.market.io.action.UserAction;

import java.util.List;

public class IOHandler {
    private final InputHandler inputHandler = new InputHandler();
    private final OutputHandler outputHandler = new OutputHandler();

    public void showSimpleMessage(String message) {
        outputHandler.showSimpleMessage(message);
    }

    public void showProductInformation(List<Product> products) {
        outputHandler.showProductInformation(products);
    }

    public void showDetailInformation(Product product) {
        outputHandler.showDetailInformation(product);
    }

    public void showTradingRequestSuccess(String productName) {
        outputHandler.showTradingRequestSuccess(productName);
    }

    public void showWishlistRemoveComment(String productName) {
        outputHandler.showWishlistRemoveComment(productName);
    }

    public void showWishlistAddComment(String productName) {
        outputHandler.showWishlistAddComment(productName);
    }

    public String getTradingArea() {
        outputHandler.askTradingArea();
        return inputHandler.getTradingArea();
    }

    public Product getProductNumber(List<Product> products) {
        outputHandler.showProductInformation(products);
        int productNumber = inputHandler.getProductNumber();
        if (productNumber > products.size() || productNumber <= 0) {
            throw new MarketException("\n🚨 다른 번호를 입력하여 페이지 선택으로 돌아갑니다. 🚨");
        }
        return products.get(productNumber - 1);
    }

    public PageStatus selectPage() {
        outputHandler.askPageSelecting();
        return inputHandler.getPageSelecting();
    }

    public UserAction selectWishlistPageAction() {
        outputHandler.showWishlistPageTask();
        return inputHandler.selectWishlistPageAction();
    }

    public UserAction selectProductPageAction () {
        outputHandler.showProductPageTask();
        return inputHandler.selectProductPageAction();
    }
}
