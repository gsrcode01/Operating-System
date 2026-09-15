import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ArrayBlockingQueue;

class Producer implements Runnable {

    private final BlockingQueue<Integer> buffer;

    Producer(BlockingQueue<Integer> buffer) {
        this.buffer = buffer;
    }

    @Override
    public void run() {

        try {
            for (int value = 1; value <= 10; value++) {

                buffer.put(value);

                System.out.println(
                        Thread.currentThread().getName()
                                + " produced : " + value);

                Thread.sleep(200);
            }

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}

class Consumer implements Runnable {

    private final BlockingQueue<Integer> buffer;

    Consumer(BlockingQueue<Integer> buffer) {
        this.buffer = buffer;
    }

    @Override
    public void run() {

        try {
            for (int count = 0; count < 10; count++) {

                int value = buffer.take();

                System.out.println(
                        Thread.currentThread().getName()
                                + " consumed : " + value);

                Thread.sleep(400);
            }

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}

public class ProducerConsumerThreadPool {

    public static void main(String[] args)
            throws InterruptedException {

        BlockingQueue<Integer> buffer = new ArrayBlockingQueue<>(5);

        ExecutorService pool = Executors.newFixedThreadPool(4);

        pool.execute(new Producer(buffer));
        pool.execute(new Producer(buffer));

        pool.execute(new Consumer(buffer));
        pool.execute(new Consumer(buffer));

        pool.shutdown();

        while (!pool.awaitTermination(1,
                java.util.concurrent.TimeUnit.SECONDS)) {

            System.out.println("Main thread waiting...");
        }

        System.out.println("All producer and consumer tasks completed.");
    }
}