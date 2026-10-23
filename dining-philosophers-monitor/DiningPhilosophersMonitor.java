import java.util.Iterator;
import java.util.LinkedList;

public class DiningPhilosophersMonitor {

    static final int N = 4;

    static class Table {
        private final boolean[] chopstickBusy = new boolean[N];
        private final boolean[] granted = new boolean[N];
        private final LinkedList<Integer> waiting =
                new LinkedList<>();

        private int left(int p) {
            return p;
        }

        private int right(int p) {
            return (p + N - 1) % N;
        }

        private boolean canEat(int p) {
            return !chopstickBusy[left(p)]
                    && !chopstickBusy[right(p)];
        }

        // All calls happen while holding this monitor
        private void schedule() {
            Iterator<Integer> it = waiting.iterator();

            while (it.hasNext()) {
                int p = it.next();

                // Skip blocked philosophers; check later candidates
                if (canEat(p)) {
                    chopstickBusy[left(p)] = true;
                    chopstickBusy[right(p)] = true;

                    granted[p] = true;
                    it.remove();

                    System.out.println(
                        "Scheduler grants both chopsticks to P" + p
                    );
                }
            }

            notifyAll();
        }

        public synchronized void takeChopsticks(int p)
                throws InterruptedException {
            waiting.addLast(p);
            System.out.println("P" + p + " is HUNGRY");

            schedule();

            try {
                while (!granted[p]) {
                    wait();
                }
            } catch (InterruptedException e) {
                // Clean up if this philosopher is interrupted
                if (granted[p]) {
                    chopstickBusy[left(p)] = false;
                    chopstickBusy[right(p)] = false;
                    granted[p] = false;
                } else {
                    waiting.remove(Integer.valueOf(p));
                }

                schedule();
                throw e;
            }
        }

        public synchronized void putChopsticks(int p) {
            chopstickBusy[left(p)] = false;
            chopstickBusy[right(p)] = false;
            granted[p] = false;

            System.out.println("P" + p + " releases both chopsticks");

            schedule();
        }
    }

    static final Table table = new Table();

    static void philosopher(int p) {
        while (!Thread.currentThread().isInterrupted()) {
            System.out.println("P" + p + " is THINKING");
            pause(500);

            try {
                table.takeChopsticks(p);

                try {
                    System.out.println("P" + p + " is EATING");
                    pause(1000);
                } finally {
                    table.putChopsticks(p);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    static void pause(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public static void main(String[] args) {
        for (int i = 0; i < N; i++) {
            final int p = i;
            new Thread(() -> philosopher(p), "P" + p).start();
        }
    }
}
