import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ReaderWriter {

    static final int READER_COUNT = 4;
    static final int WRITER_COUNT = 2;
    static final int VISITS = 4;

    static long sessionStart;

    static synchronized void note(String text) {
        System.out.printf("[%5d ms] %s%n", System.currentTimeMillis() - sessionStart, text);
    }

    interface LedgerGate {
        String label();
        void beginReading() throws InterruptedException;
        void endReading();
        void beginWriting() throws InterruptedException;
        void endWriting();
    }

    static class SemaphoreGate implements LedgerGate {
        private final Semaphore countProtector = new Semaphore(1);
        private final Semaphore ledgerOwner = new Semaphore(1, true);
        private final Semaphore queueDoor = new Semaphore(1, true);
        private int readersPresent = 0;

        public String label() {
            return "Semaphores";
        }

        public void beginReading() {
            queueDoor.acquireUninterruptibly();
            countProtector.acquireUninterruptibly();
            if (++readersPresent == 1) {
                ledgerOwner.acquireUninterruptibly();
            }
            countProtector.release();
            queueDoor.release();
        }

        public void endReading() {
            countProtector.acquireUninterruptibly();
            if (--readersPresent == 0) {
                ledgerOwner.release();
            }
            countProtector.release();
        }

        public void beginWriting() {
            queueDoor.acquireUninterruptibly();
            ledgerOwner.acquireUninterruptibly();
            queueDoor.release();
        }

        public void endWriting() {
            ledgerOwner.release();
        }
    }

    static class ReadWriteLockGate implements LedgerGate {
        private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock(true);

        public String label() {
            return "ReentrantReadWriteLock";
        }

        public void beginReading() {
            lock.readLock().lock();
        }

        public void endReading() {
            lock.readLock().unlock();
        }

        public void beginWriting() {
            lock.writeLock().lock();
        }

        public void endWriting() {
            lock.writeLock().unlock();
        }
    }

    static class Ledger {
        private final LedgerGate gate;
        private int entries = 0;
        private final AtomicInteger readersInside = new AtomicInteger();
        private final AtomicInteger writersInside = new AtomicInteger();
        final AtomicInteger peakReaders = new AtomicInteger();
        final AtomicInteger brokenRules = new AtomicInteger();

        Ledger(LedgerGate gate) {
            this.gate = gate;
        }

        void consult(int readerId) throws InterruptedException {
            gate.beginReading();
            try {
                int now = readersInside.incrementAndGet();
                peakReaders.accumulateAndGet(now, Math::max);
                if (writersInside.get() != 0) {
                    brokenRules.incrementAndGet();
                }
                note("Reader " + readerId + " sees entries = " + entries + "   (readers inside: " + now + ")");
                Thread.sleep(300);
                readersInside.decrementAndGet();
            } finally {
                gate.endReading();
            }
        }

        void append(int writerId) throws InterruptedException {
            gate.beginWriting();
            try {
                if (writersInside.incrementAndGet() != 1 || readersInside.get() != 0) {
                    brokenRules.incrementAndGet();
                }
                entries++;
                note("Writer " + writerId + " appended, entries = " + entries);
                Thread.sleep(500);
                writersInside.decrementAndGet();
            } finally {
                gate.endWriting();
            }
        }
    }

    static Runnable visitor(boolean isWriter, int id, Ledger ledger) {
        return () -> {
            Random pause = new Random();
            try {
                for (int visit = 0; visit < VISITS; visit++) {
                    Thread.sleep(isWriter ? 100 + pause.nextInt(400) : 50 + pause.nextInt(300));
                    if (isWriter) {
                        ledger.append(id);
                    } else {
                        ledger.consult(id);
                    }
                }
            } catch (InterruptedException interrupted) {
                Thread.currentThread().interrupt();
            }
        };
    }

    static void runSession(LedgerGate gate) throws InterruptedException {
        System.out.println("\n===== " + gate.label() + " =====");
        sessionStart = System.currentTimeMillis();
        Ledger ledger = new Ledger(gate);

        ExecutorService staff = Executors.newFixedThreadPool(READER_COUNT + WRITER_COUNT);
        List<Runnable> jobs = new ArrayList<>();
        for (int id = 1; id <= READER_COUNT; id++) {
            jobs.add(visitor(false, id, ledger));
        }
        for (int id = 1; id <= WRITER_COUNT; id++) {
            jobs.add(visitor(true, id, ledger));
        }
        jobs.forEach(staff::execute);
        staff.shutdown();
        staff.awaitTermination(1, TimeUnit.MINUTES);

        System.out.println("-- peak readers together : " + ledger.peakReaders.get());
        System.out.println("-- rules broken          : " + ledger.brokenRules.get());
    }

    public static void main(String[] args) throws InterruptedException {
        runSession(new SemaphoreGate());
        runSession(new ReadWriteLockGate());
    }
}