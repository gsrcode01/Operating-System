import java.util.Iterator;
import java.util.LinkedList;
import java.util.concurrent.Semaphore;

public class DiningPhilosophersSemaphore {

    static final int N = 4;

    // Protects shared scheduling state
    static final Semaphore mutex = new Semaphore(1, true);

    // Each philosopher has a private waiting semaphore
    static final Semaphore[] wakeup = new Semaphore[N];

    static final boolean[] chopstickBusy = new boolean[N];
    static final boolean[] granted = new boolean[N];

    static final LinkedList<Integer> waiting = new LinkedList<>();

    static {
        for (int i = 0; i < N; i++) {
            wakeup[i] = new Semaphore(0, true);
        }
    }

    static int left(int p) {
        return p;
    }

    static int right(int p) {
        return (p + N - 1) % N;
    }

    static boolean canEat(int p) {
        return !chopstickBusy[left(p)]
                && !chopstickBusy[right(p)];
    }

    // Called only while holding mutex
    static void schedule() {
        Iterator<Integer> it = waiting.iterator();

        while (it.hasNext()) {
            int p = it.next();

            // Skip blocked philosophers and check others
            if (canEat(p)) {
                chopstickBusy[left(p)] = true;
                chopstickBusy[right(p)] = true;

                granted[p] = true;
                it.remove();

                System.out.println(
                    "Scheduler grants both chopsticks to P" + p
                );

                wakeup[p].release();
            }
        }
    }

    static void takeChopsticks(int p) {
        mutex.acquireUninterruptibly();

        waiting.addLast(p);
        System.out.println("P" + p + " is HUNGRY");

        schedule();

        mutex.release();

        // Blocks until the scheduler grants both chopsticks
        wakeup[p].acquireUninterruptibly();
    }

    static void putChopsticks(int p) {
        mutex.acquireUninterruptibly();

        chopstickBusy[left(p)] = false;
        chopstickBusy[right(p)] = false;
        granted[p] = false;

        System.out.println("P" + p + " releases both chopsticks");

        // Give available resources to eligible hungry philosophers
        schedule();

        mutex.release();
    }

    static void philosopher(int p) {
        while (!Thread.currentThread().isInterrupted()) {
            System.out.println("P" + p + " is THINKING");
            pause(500);

            takeChopsticks(p);

            try {
                System.out.println("P" + p + " is EATING");
                pause(1000);
            } finally {
                putChopsticks(p);
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
