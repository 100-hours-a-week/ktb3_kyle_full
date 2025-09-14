package main;

import main.database.Database;
import main.market.Market;
import main.market.domain.user.User;

public class Application {
    public static void main(String[] args) {
        Database database = new Database();
        database.initialize();

        User user = new User();

        Market market = new Market(database, user);
        market.run();
    }
}
