# Operating System

This repository contains practical implementations and interactive visualizations of operating-system concepts, with a focus on multithreading, synchronization, and concurrent execution.

## Implementations

### 1. Dining Philosophers Problem
**Files:** `DiningPhilosophers.java`, `diningphilosopher.html`

The Java implementation models four philosophers sharing four chopsticks. A monitor coordinates access to the chopsticks, while semaphores put waiting philosophers to sleep and wake them when they can proceed. A FIFO queue and fairness checks help prevent starvation, and both chopsticks are reserved together so two philosophers cannot claim the same chopstick simultaneously.

The HTML simulator presents the philosophers' states—thinking, hungry, and eating—along with the shared chopsticks, waiting queue, and synchronization activity. Users can interact with the simulation and observe how the coordination rules work.

**Concepts:** threads, monitors, semaphores, mutual exclusion, resource allocation, deadlock prevention, and starvation prevention.

### 2. Reader–Writer Problem
**Files:** `ReaderWriter.java`, `readerwriter.html`

The Java program demonstrates coordination between readers and writers that access shared data. Multiple readers can read concurrently, but a writer must have exclusive access so that no reader or other writer accesses the shared resource while writing.

The HTML simulator visualizes readers and writers waiting for and accessing a shared ledger. It lets users compare semaphore-based synchronization with Java's `ReentrantReadWriteLock`, explore fair and reader-preference behavior, and observe how a continuous stream of readers can cause writer starvation when fairness is disabled.

**Concepts:** read–write synchronization, semaphores, locks, mutual exclusion, fairness, and starvation.

### 3. Matrix Multiplication with Thread Pools
**File:** `MultiplicationThreads.java`

This program performs matrix multiplication using concurrent tasks managed by Java executor services. The work is split across thread pools so multiple matrix-result calculations can execute concurrently. It demonstrates how independent computations can be distributed among worker threads and how the program waits for tasks to complete.

**Concepts:** multithreading, executor services, fixed thread pools, parallel computation, and task coordination.

### 4. Producer–Consumer Problem with a Thread Pool
**File:** `ProducerConsumerThreadPool.java`

This program models producers adding items to a shared bounded buffer and consumers removing them. Producers and consumers run as tasks in a thread pool. The bounded blocking queue coordinates access when the buffer is full or empty, demonstrating how concurrent tasks can safely exchange data.

**Concepts:** producer–consumer synchronization, bounded buffers, blocking queues, thread pools, and concurrency.

### 5. Matrix Thread-Pool Visualization
**File:** `index.html`

This browser-based visualization illustrates the matrix multiplication and thread-pool workflow. It provides a visual way to follow worker activity and the progress of parallel calculations, making the relationship between matrix operations and concurrent execution easier to understand.

**Concepts:** visualizing parallel execution, worker threads, thread pools, and matrix computation.

## Technologies

- **Java** — concurrent program implementations.
- **Java Concurrency API** — threads, executor services, semaphores, locks, and blocking queues.
- **HTML, CSS, and JavaScript** — interactive visualizations for synchronization and thread-pool concepts.
