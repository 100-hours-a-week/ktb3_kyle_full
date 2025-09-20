package main;

import main.database.Database;
import main.market.Market;
import main.market.domain.user.User;
import main.market.io.IOHandler;

public class Application {
    public static void main(String[] args) {
        Database database = new Database();
        database.initialize();

        User user = new User();
        IOHandler ioHandler = new IOHandler();
        Market market = new Market(database, user, ioHandler);
        market.run();
    }
}
