package main.market.domain.user;

import main.market.domain.product.Product;
import main.market.exception.MarketException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class User {
    private final List<Product> wishlist = new ArrayList<>();

    public List<Product> getWishlist() {
        if (wishlist.isEmpty()) {
            throw new MarketException("\n❌ 현재 관심 목록에 담긴 제품이 없습니다. ❌");
        }
        return Collections.unmodifiableList(wishlist);
    }

    public boolean isEmptyList() {
        return wishlist.isEmpty();
    }

    public void addWishlist(Product product) {
        wishlist.add(product);
    }

    public void removeWishlist(Product product) {
        wishlist.remove(product);
    }
}
