import java.util.Iterator;
import java.util.LinkedList;
import java.util.Random;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Dining Philosophers: 4 philosophers, 4 chopsticks.
 *
 *   MONITOR   = decision maker (state, chopsticks, FIFO queue, atomic pair reservation)
 *   SEMAPHORE = waiting mechanism (one private semaphore per philosopher: wait = sleep, signal = wake)
 *
 * The semaphore never decides anything. It only blocks and wakes.
 */
public class DiningPhilosophers {

    static final int N = 4;
    static final int ROUNDS = 5;

    enum State { THINKING, HUNGRY, EATING }

    // ------------------------------------------------------------------
    // Monitor: owns ALL shared data and ALL decisions
    // ------------------------------------------------------------------
    static class Monitor {
        private final ReentrantLock lock = new ReentrantLock(true);

        private final State[] state = new State[N];
        private final boolean[] chopstickBusy = new boolean[N];
        private final LinkedList<Integer> hungryQueue = new LinkedList<>();   // FIFO

        // One private semaphore per philosopher, starting at 0 permits:
        // acquire() sleeps, release() wakes.
        private final Semaphore[] sem = new Semaphore[N];

        Monitor() {
            for (int i = 0; i < N; i++) {
                state[i] = State.THINKING;
                sem[i] = new Semaphore(0);
            }
        }

        // P0 -> C0 + C3, P1 -> C1 + C0, P2 -> C2 + C1, P3 -> C3 + C2
        private static int left(int p)  { return p; }
        private static int right(int p) { return (p + 3) % N; }

        /** Called by a hungry philosopher. Returns only when both chopsticks are reserved for it. */
        void pickUp(int p) throws InterruptedException {
            lock.lock();
            try {
                state[p] = State.HUNGRY;
                hungryQueue.addLast(p);
                log(p, "is HUNGRY   (queue=" + hungryQueue + ")");

                scheduleEligible();        // monitor decides; may grant p right now
            } finally {
                lock.unlock();             // never sleep while holding the monitor
            }

            // WAIT: sleeps if not granted yet. If the monitor already granted p,
            // its permit is waiting and this returns immediately (no lost wakeup).
            sem[p].acquire();
        }

        /** Called when a philosopher finishes eating. */
        void putDown(int p) {
            lock.lock();
            try {
                chopstickBusy[left(p)] = false;
                chopstickBusy[right(p)] = false;
                state[p] = State.THINKING;
                log(p, "released C" + left(p) + " + C" + right(p));

                scheduleEligible();        // wake whoever can now eat
            } finally {
                lock.unlock();
            }
        }

        /**
         * The decision logic. Scans the hungry queue oldest -> newest and grants every
         * philosopher whose two chopsticks are FREE, reserving both atomically.
         *
         * Fairness rule: a younger philosopher may only jump ahead if it shares no chopstick
         * with an older philosopher who is still waiting (prevents starvation).
         * Non-adjacent philosophers share no chopstick, so they can eat concurrently.
         *
         * Must be called with the lock held.
         */
        private void scheduleEligible() {
            boolean[] claimedByOlderWaiter = new boolean[N];

            Iterator<Integer> it = hungryQueue.iterator();
            while (it.hasNext()) {
                int q = it.next();
                int l = left(q), r = right(q);

                boolean pairFree = !chopstickBusy[l] && !chopstickBusy[r];
                boolean blockedByOlder = claimedByOlderWaiter[l] || claimedByOlderWaiter[r];

                if (pairFree && !blockedByOlder) {
                    chopstickBusy[l] = true;       // reserve BOTH chopsticks atomically
                    chopstickBusy[r] = true;
                    state[q] = State.EATING;
                    it.remove();
                    sem[q].release();              // SIGNAL: wake q
                } else {
                    claimedByOlderWaiter[l] = true;
                    claimedByOlderWaiter[r] = true;
                }
            }
        }
    }

    // ------------------------------------------------------------------
    // Philosopher thread
    // ------------------------------------------------------------------
    static class Philosopher extends Thread {
        private final int id;
        private final Monitor monitor;
        private final Random rnd = new Random();

        Philosopher(int id, Monitor monitor) {
            super("P" + id);
            this.id = id;
            this.monitor = monitor;
        }

        @Override
        public void run() {
            try {
                for (int round = 1; round <= ROUNDS; round++) {
                    log(id, "is THINKING");
                    Thread.sleep(100 + rnd.nextInt(300));

                    monitor.pickUp(id);                 // monitor decides, semaphore blocks

                    log(id, "is EATING   (round " + round + ")");   // outside the monitor
                    Thread.sleep(200 + rnd.nextInt(300));

                    monitor.putDown(id);
                }
                log(id, "is DONE");
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    // ------------------------------------------------------------------
    static final long START = System.currentTimeMillis();

    static synchronized void log(int p, String msg) {
        System.out.printf("[%5d ms] P%d %s%n", System.currentTimeMillis() - START, p, msg);
    }

    public static void main(String[] args) throws InterruptedException {
        Monitor monitor = new Monitor();
        Philosopher[] ps = new Philosopher[N];

        for (int i = 0; i < N; i++) {
            ps[i] = new Philosopher(i, monitor);
            ps[i].start();
        }
        for (Philosopher p : ps) {
            p.join();
        }
        System.out.println("All philosophers finished: no deadlock, no starvation.");
    }
}