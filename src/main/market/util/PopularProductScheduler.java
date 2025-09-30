package main.market.util;

import main.database.Database;
import main.market.domain.product.Product;
import main.market.io.IOHandler;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class PopularProductScheduler {
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private final Database database;
    private final IOHandler ioHandler;

    public PopularProductScheduler(Database database, IOHandler ioHandler) {
        this.database = database;
        this.ioHandler = ioHandler;
    }

    public void start() {
        Runnable task = () -> {
            List<Product> mostViewedProducts = database.findMostViewedProducts();

            if (!mostViewedProducts.isEmpty()) {
                ioHandler.showMostViewedProducts(mostViewedProducts);
            }
        };

        scheduler.scheduleAtFixedRate(task, 20, 10, TimeUnit.SECONDS);
    }

    public void shutdown() {
        scheduler.shutdown();
    }
}
