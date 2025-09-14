package main.market.domain.user;

import main.market.domain.product.Product;

import java.util.ArrayList;
import java.util.List;

public class User {
    private final List<Product> wishlist = new ArrayList<>();

    public List<Product> getWishlist() {
        return new ArrayList<>(wishlist);
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
