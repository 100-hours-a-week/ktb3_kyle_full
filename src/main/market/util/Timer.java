package main.market.util;

public class Timer extends Thread {
    private int count = 0;

    @Override
    public void run() {
        while (true) {
            try {
                Thread.sleep(1000);
                count++;
            } catch (InterruptedException e) {
                break;
            }
        }
    }

    public int getCount() {
        return count;
    }
}
