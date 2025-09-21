package main.market.io.action;

import main.market.io.IOHandler;

public enum PageStatus {
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

    PageStatus(String description) {
        this.description = description;
    }

    public abstract UserAction provideUserAction(IOHandler ioHandler);

    public boolean isNotExit() {
        return this != EXIT;
    }
}
