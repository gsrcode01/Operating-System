# Operating System Concurrency Implementations & Visualizations

Practical implementations and interactive web visualizations of classic operating-system synchronization and concurrency problems.

---

## 📂 Repository Structure

```text
os/
├── dining-philosophers-semaphore/
│   ├── DiningPhilosophersSemaphore.java    # Semaphore + FIFO queue scheduler
│   └── index.html                         # Interactive 4-philosopher round-table simulation
│
├── dining-philosophers-monitor/
│   ├── DiningPhilosophersMonitor.java      # Java Monitor (synchronized, wait, notifyAll)
│   └── index.html                         # Interactive Table Monitor visualization
│
├── reader-writer-semaphore/
│   ├── ReaderWriterSemaphore.java          # Fair serviceQueue + resource + mutex semaphores
│   └── index.html                         # Interactive Fair Turnstile Reader-Writer simulator
│
├── reader-writer-monitor/
│   ├── ReaderWriterMonitor.java            # Java Monitor with Writer-Preference condition sync
│   └── index.html                         # Interactive Writer-Preference Monitor simulator
│
├── MultiplicationThreads.java              # Matrix multiplication with ExecutorService thread pools
├── ProducerConsumerThreadPool.java         # Producer-Consumer with thread pools & bounded buffer
├── index.html                              # Matrix Thread Pool Visualizer & navigation hub
├── MCQ.docx                                # Multiple-choice questions and revision notes
├── ch1.pdf, ch2_modified.pdf, ch3_modified.pdf
└── README.md
```

---

## 🚀 Projects & Visualizations

### 1. Dining Philosophers (Semaphore)
- **Folder:** [`dining-philosophers-semaphore/`](dining-philosophers-semaphore/)
- **Source Code:** [DiningPhilosophersSemaphore.java](dining-philosophers-semaphore/DiningPhilosophersSemaphore.java)
- **Visual Simulation:** [dining-philosophers-semaphore/index.html](dining-philosophers-semaphore/index.html)
- **Key Concepts:**
  - $N=4$ philosophers sharing 4 chopsticks in a circle.
  - A centralized `schedule()` method protected by a `mutex` semaphore ensures deadlock freedom.
  - Philosophers block on private `wakeup[p]` semaphores until both chopsticks are allocated simultaneously.
  - FIFO waiting queue ensures fairness and prevents philosopher starvation.

### 2. Dining Philosophers (Monitor)
- **Folder:** [`dining-philosophers-monitor/`](dining-philosophers-monitor/)
- **Source Code:** [DiningPhilosophersMonitor.java](dining-philosophers-monitor/DiningPhilosophersMonitor.java)
- **Visual Simulation:** [dining-philosophers-monitor/index.html](dining-philosophers-monitor/index.html)
- **Key Concepts:**
  - Encapsulated synchronization inside a `Table` monitor using Java's intrinsic locks (`synchronized`).
  - Condition synchronization with `wait()` and `notifyAll()`.
  - Hungry philosophers join a `waiting` list and wait in the monitor's condition wait set until the scheduler grants both chopsticks.

### 3. Reader–Writer Problem (Semaphore)
- **Folder:** [`reader-writer-semaphore/`](reader-writer-semaphore/)
- **Source Code:** [ReaderWriterSemaphore.java](reader-writer-semaphore/ReaderWriterSemaphore.java)
- **Visual Simulation:** [reader-writer-semaphore/index.html](reader-writer-semaphore/index.html)
- **Key Concepts:**
  - **Fair Turnstile:** `serviceQueue` semaphore ensures incoming requests are served in order.
  - **Starvation Freedom:** Writers do not starve behind continuous readers.
  - `readCountMutex` protects the reader counter, and the first reader acquires the `resource` semaphore while the last reader releases it.

### 4. Reader–Writer Problem (Monitor)
- **Folder:** [`reader-writer-monitor/`](reader-writer-monitor/)
- **Source Code:** [ReaderWriterMonitor.java](reader-writer-monitor/ReaderWriterMonitor.java)
- **Visual Simulation:** [reader-writer-monitor/index.html](reader-writer-monitor/index.html)
- **Key Concepts:**
  - **Writer Preference:** Readers wait if `writerActive || waitingWriters > 0`.
  - Immediate prioritization of pending writers eliminates writer starvation.
  - Coordinated via `wait()` and `notifyAll()` inside Java `synchronized` methods.
