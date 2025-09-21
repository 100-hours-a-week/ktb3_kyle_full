package main.market;

import main.database.Database;
import main.market.domain.product.Product;
import main.market.domain.user.User;
import main.market.exception.MarketException;
import main.market.io.IOHandler;
import main.market.io.action.PageStatus;
import main.market.io.action.UserAction;

import java.util.*;

import static main.market.io.action.PageStatus.*;

public class Market {
    private final static Set<String> TRADING_AREA = Set.of("서울", "경기", "인천");
    private final IOHandler ioHandler;
    private final Database database;
    private final User user;
    private PageStatus pageStatus = WISHLIST_PAGE;

    public Market(Database database, User user, IOHandler ioHandler) {
        this.database = database;
        this.user = user;
        this.ioHandler = ioHandler;
    }

    public void run() {
        do {
            try {
                pageStatus = ioHandler.selectPage();
                List<Product> products = findProductsByMarketStatus();
                Product selectedProduct = selectProduct(products);
                ioHandler.showDetailInformation(selectedProduct);
                executeUserAction(selectedProduct);
            } catch (MarketException e) {
                ioHandler.showSimpleMessage(e.getMessage());
            } catch (Exception e) {
                ioHandler.showSimpleMessage("알 수 없는 오류가 발생했습니다.");
            }
        } while (pageStatus.isNotExit());
    }

    private List<Product> findProductsByMarketStatus() {
        if (pageStatus == WISHLIST_PAGE) {
            return user.getWishlist();
        }
        if (pageStatus == PRODUCT_PAGE) {
            String tradingArea = ioHandler.getTradingArea();
            if (isInvalidTradingArea(tradingArea)) {
                throw new MarketException("\n🚨 거래 불가능 지역입니다. 거래는 [서울, 경기, 인천] 만 가능합니다. 🚨");
            }
            return database.findByArea(tradingArea);
        }
        throw new MarketException("\n🙇‍♂️ 다른 번호를 입력하여 프로그램을 종료합니다. 이용해주셔서 감사합니다. 🙇‍♂️");
    }

    private boolean isInvalidTradingArea(String input) {
        return !TRADING_AREA.contains(input);
    }

    private Product selectProduct(List<Product> products) {
        Product product = ioHandler.getProductNumber(products);
        product.increaseViewCount();
        return product;
    }

    private void executeUserAction(Product selectedProduct) {
        UserAction userAction = pageStatus.provideUserAction(ioHandler);
        userAction.execute(user, ioHandler, selectedProduct);
    }
}
