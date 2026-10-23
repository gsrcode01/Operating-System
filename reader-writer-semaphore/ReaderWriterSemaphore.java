import java.util.concurrent.Semaphore;

public class ReaderWriterSemaphore {

    static Semaphore serviceQueue = new Semaphore(1, true);
    static Semaphore resource = new Semaphore(1, true);
    static Semaphore readCountMutex = new Semaphore(1, true);

    static int readCount = 0;
    static int sharedData = 0;

    static void reader(int id) throws InterruptedException {
        // Enter through the fair queue
        serviceQueue.acquire();
        readCountMutex.acquire();

        readCount++;
        if (readCount == 1) {
            resource.acquire(); // First reader blocks writers
        }

        serviceQueue.release();
        readCountMutex.release();

        try {
            System.out.println("Reader " + id
                    + " reads: " + sharedData);
            Thread.sleep(500);
        } finally {
            readCountMutex.acquire();
            try {
                readCount--;
                if (readCount == 0) {
                    resource.release(); // Last reader allows writers
                }
            } finally {
                readCountMutex.release();
            }
        }
    }

    static void writer(int id) throws InterruptedException {
        serviceQueue.acquire();
        try {
            resource.acquire();
        } finally {
            serviceQueue.release();
        }

        try {
            sharedData++;
            System.out.println("Writer " + id
                    + " writes: " + sharedData);
            Thread.sleep(500);
        } finally {
            resource.release();
        }
    }

    public static void main(String[] args) {
        for (int i = 1; i <= 3; i++) {
            final int id = i;

            new Thread(() -> {
                try {
                    reader(id);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }).start();

            new Thread(() -> {
                try {
                    writer(id);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }).start();
        }
    }
}
