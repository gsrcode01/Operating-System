# Operating-System
# Operating System Assignment

This repository contains Java implementations demonstrating **multithreading, thread pools, producer-consumer synchronization, and parallel matrix multiplication**.

## 📁 Files

- `ProducerConsumerThreadPool.java` – Producer-Consumer problem using `BlockingQueue` and a fixed thread pool.
- `MultiplicationThreads.java` – 100×100 matrix multiplication using 4 thread pools with 25 threads each.
- `index.html` – Interactive visualization of the matrix multiplication thread-pool process.

## 🔹 Producer-Consumer

The Producer-Consumer program uses:

- `BlockingQueue`
- `ArrayBlockingQueue`
- `ExecutorService`
- Fixed Thread Pool
- 2 Producers
- 2 Consumers
- Buffer capacity of 5
- 4 worker threads

Producers generate values and place them into the shared buffer, while consumers retrieve and process them.

## 🔹 Matrix Multiplication

The matrix multiplication program uses:

- Two `100 × 100` matrices
- 4 thread pools
- 25 threads per pool
- 100 total worker threads
- 10,000 tasks
- One task for each result matrix element

The matrices are generated automatically, and the program measures the execution time after all tasks are completed.

## 🔹 Visualization

`index.html` provides an interactive visualization showing:

- 4 thread pools
- 25 workers per pool
- Task distribution
- Matrix multiplication
- Task completion
- Start, Pause, Step, and Reset controls

## 🛠️ Technologies

- Java
- Java Concurrency API
- ExecutorService
- BlockingQueue
- Threads
- HTML
- CSS
- JavaScript

## ▶️ Run

### Producer-Consumer

```bash
javac ProducerConsumerThreadPool.java
java ProducerConsumerThreadPool
