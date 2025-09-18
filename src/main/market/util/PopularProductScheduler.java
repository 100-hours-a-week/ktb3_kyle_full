package main.market.util;

import main.database.Database;
import main.market.domain.product.Product;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class PopularProductScheduler {
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private final Database database;

    public PopularProductScheduler(Database database) {
        this.database = database;
    }

    public void start() {
        Runnable task = () -> {
            List<Product> mostViewedProducts = database.findMostViewedProducts();

            if (!mostViewedProducts.isEmpty()) System.out.println("\n🔥🔥🔥 현재 가장 많이 조회되고 있는 제품입니다. 🔥🔥🔥");
            for (int i = 0; i < mostViewedProducts.size(); i++) {
                Product product = mostViewedProducts.get(i);
                System.out.println("\n-----------제품 " + (i + 1) + "-----------");
                System.out.println(product.getInformation());
                System.out.println("---------------------------");
            }
        };

        scheduler.scheduleAtFixedRate(task, 20, 10, TimeUnit.SECONDS);
    }

    public void shutdown() {
        scheduler.shutdown();
    }
}
