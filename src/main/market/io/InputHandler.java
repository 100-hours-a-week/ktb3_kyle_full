package main.market.io;

import main.market.Market;
import main.market.exception.MarketException;
import main.market.io.action.MarketStatus;
import main.market.io.action.UserAction;

import java.util.Scanner;

import static main.market.io.action.UserAction.*;

public class InputHandler {
    private final static Scanner SCANNER = new Scanner(System.in);

    public MarketStatus getPageSelecting() {
        String input = SCANNER.nextLine();
        if (input.equals("1")) {
            return MarketStatus.WISHLIST_PAGE;
        }
        if (input.equals("2")) {
            return MarketStatus.PRODUCT_PAGE;
        }
        return MarketStatus.EXIT;
    }

    public String getTradingArea() {
        return SCANNER.nextLine();
    }

    public UserAction selectWishlistPageAction() {
        String input = SCANNER.nextLine();
        if (input.equals("1")) {
            return TRADE_REQUEST;
        }
        if (input.equals("2")) {
            return REMOVE_WISHLIST;
        }
        throw new MarketException("\n🚨 다른 키를 입력하여 페이지 선택으로 돌아갑니다. 🚨");
    }

    public UserAction selectProductPageAction () {
        String input = SCANNER.nextLine();
        if (input.equals("1")) {
            return TRADE_REQUEST;
        }
        if (input.equals("2")) {
            return ADD_WISHLIST;
        }
        throw new MarketException("\n🚨 다른 키를 입력하여 페이지 선택으로 돌아갑니다. 🚨");
    }

    public int getProductNumber() {
        try {
            String input = SCANNER.nextLine();
            return Integer.parseInt(input);
        } catch (NumberFormatException e) {
            throw new MarketException("\n🚨 숫자만 입력해주시기 바랍니다. 페이지 선택으로 돌아갑니다. 🚨");
        }
    }
}
