package main;

import main.database.Database;
import main.market.Market;
import main.market.domain.user.User;
import main.market.io.IOHandler;
import main.market.util.PopularProductScheduler;
import main.market.util.Timer;

public class Application {
    public static void main(String[] args) {
        User user = new User();
        Timer timer = new Timer();
        IOHandler ioHandler = new IOHandler();

        Database database = new Database();
        database.initialize();

        PopularProductScheduler scheduler = new PopularProductScheduler(database, ioHandler);
        scheduler.start();

        Market market = new Market(database, user, ioHandler, timer);
        market.run();
        scheduler.shutdown();
    }
}
