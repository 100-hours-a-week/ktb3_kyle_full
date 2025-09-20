package main.market.io.action;

import main.market.io.IOHandler;

public enum MarketStatus {
    HOME("페이지 선택 홈") {
        @Override
        public UserAction provideUserAction(IOHandler ioHandler) {
            return null;
        }
    },
    WISHLIST_PAGE("관심 목록 페이지") {
        @Override
        public UserAction provideUserAction(IOHandler ioHandler) {
            return ioHandler.selectWishlistPageAction();
        }
    },
    PRODUCT_PAGE("상품 조회 페이지") {
        @Override
        public UserAction provideUserAction(IOHandler ioHandler) {
            return ioHandler.selectProductPageAction();
        }
    },
    EXIT("종료") {
        @Override
        public UserAction provideUserAction(IOHandler ioHandler) {
            return null;
        }
    };

    private final String description;

    MarketStatus(String description) {
        this.description = description;
    }

    public abstract UserAction provideUserAction(IOHandler ioHandler);

    public boolean isNotExit() {
        return this != EXIT;
    }
}
