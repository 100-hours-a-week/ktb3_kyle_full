package main.market.io.action;

import main.market.domain.product.Product;
import main.market.domain.user.User;
import main.market.io.IOHandler;

public enum UserAction {
    TRADE_REQUEST("거래 요청 보내기") {
        @Override
        public void execute(User user, IOHandler ioHandler, Product product) {
            ioHandler.showTradingRequestSuccess(product.getName());
        }
    },
    REMOVE_WISHLIST("관심 목록에서 삭제하기") {
        @Override
        public void execute(User user, IOHandler ioHandler, Product product) {
            ioHandler.showWishlistRemoveComment(product.getName());
            user.removeWishlist(product);
        }
    },
    ADD_WISHLIST("관심 목록에 추가하기") {
        @Override
        public void execute(User user, IOHandler ioHandler, Product product) {
            ioHandler.showWishlistAddComment(product.getName());
            user.addWishlist(product);
        }
    };

    private final String description;

    public abstract void execute(User user, IOHandler ioHandler, Product product);

    UserAction(String description) {
        this.description = description;
    }

}
