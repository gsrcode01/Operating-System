public class ReaderWriterMonitor {

    static class Monitor {
        private int readers = 0;
        private int sharedData = 0;
        private int waitingWriters = 0;
        private boolean writerActive = false;

        public synchronized void startRead()
                throws InterruptedException {
            while (writerActive || waitingWriters > 0) {
                wait();
            }
            readers++;
        }

        public synchronized void endRead() {
            readers--;

            if (readers == 0) {
                notifyAll();
            }
        }

        public synchronized void startWrite()
                throws InterruptedException {
            waitingWriters++;

            try {
                while (writerActive || readers > 0) {
                    wait();
                }
                writerActive = true;
            } finally {
                waitingWriters--;
            }
        }

        public synchronized void endWrite() {
            writerActive = false;
            notifyAll();
        }

        public synchronized int readData() {
            return sharedData;
        }

        public synchronized void writeData(int value) {
            sharedData = value;
        }
    }

    static Monitor monitor = new Monitor();

    static void reader(int id) throws InterruptedException {
        monitor.startRead();
        try {
            int value = monitor.readData();
            System.out.println("Reader " + id + " reads: " + value);
            Thread.sleep(500);
        } finally {
            monitor.endRead();
        }
    }

    static void writer(int id) throws InterruptedException {
        monitor.startWrite();
        try {
            int value = monitor.readData() + 1;
            monitor.writeData(value);
            System.out.println("Writer " + id + " writes: " + value);
            Thread.sleep(500);
        } finally {
            monitor.endWrite();
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
