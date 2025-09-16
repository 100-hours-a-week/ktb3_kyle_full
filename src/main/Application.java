package main;

import main.database.Database;
import main.market.Market;
import main.market.domain.user.User;
import main.market.util.Timer;

public class Application {
    public static void main(String[] args) {
        Database database = new Database();
        database.initialize();

        User user = new User();
        Timer timer = new Timer();
        Market market = new Market(database, user, timer);
        market.run();
    }
}
