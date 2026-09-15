import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/*
 * Matrix multiplication using multiple thread pools.
 *
 * Two 100 x 100 matrices are generated automatically.
 * Each element of the result matrix is calculated by a separate task.
 *
 * Four thread pools are created, with 25 worker threads in each pool.
 * Therefore, a maximum of 100 worker threads can execute the tasks.
 *
 * The main thread waits for all four pools to finish before displaying
 * the matrices and result.
 */

public class MultiplicationThreads {

    private static final int ROWS = 100;
    private static final int COLS = 100;

    public static void main(String[] args) throws InterruptedException {

        int[][] firstMatrix = createMatrix(ROWS, COLS);
        int[][] secondMatrix = createMatrix(ROWS, COLS);
        int[][] resultMatrix = new int[ROWS][COLS];

        /*
         * Four separate pools are created.
         * Each pool contains 25 threads.
         */
        ExecutorService[] threadPools = new ExecutorService[4];

        for (int i = 0; i < threadPools.length; i++) {
            threadPools[i] = Executors.newFixedThreadPool(25);
        }

        int totalTasks = ROWS * COLS;

        long startTime = System.currentTimeMillis();

        /*
         * One task is created for every element of the result matrix.
         * 100 x 100 = 10,000 tasks.
         */
        int poolNumber = 0;

        for (int row = 0; row < ROWS; row++) {

            for (int column = 0; column < COLS; column++) {

                Runnable task = new MatrixElementTask(
                        firstMatrix,
                        secondMatrix,
                        resultMatrix,
                        row,
                        column);

                threadPools[poolNumber].execute(task);

                /*
                 * Distribute tasks among the four pools.
                 */
                poolNumber = (poolNumber + 1) % 4;
            }
        }

        System.out.println(
                "Main thread has submitted " + totalTasks
                        + " matrix calculation tasks.");

        System.out.println(
                "4 thread pools created with 25 threads each.");

        System.out.println(
                "Main thread is now waiting for all worker threads...");

        /*
         * No new tasks are accepted by the pools.
         */
        for (ExecutorService pool : threadPools) {
            pool.shutdown();
        }

        /*
         * Main thread waits here until every task in every pool
         * has completed.
         */
        for (ExecutorService pool : threadPools) {
            pool.awaitTermination(
                    Long.MAX_VALUE,
                    TimeUnit.NANOSECONDS);
        }

        long endTime = System.currentTimeMillis();

        /*
         * Everything below executes only after all worker tasks
         * have completed.
         */
        System.out.println(
                "\nAll 10,000 matrix calculation tasks have finished.");

        System.out.println(
                "Total execution time: "
                        + (endTime - startTime)
                        + " ms");

        displayPart(
                firstMatrix,
                "First matrix (5x5 corner)");

        displayPart(
                secondMatrix,
                "Second matrix (5x5 corner)");

        displayPart(
                resultMatrix,
                "Result matrix (5x5 corner)");

        System.out.println(
                "\nMain thread is the last to finish execution.");
    }

    /*
     * Creates and fills a matrix with random values from 0 to 9.
     */
    private static int[][] createMatrix(int rows, int columns) {

        int[][] matrix = new int[rows][columns];

        Random random = new Random();

        for (int row = 0; row < rows; row++) {

            for (int column = 0; column < columns; column++) {

                matrix[row][column] = random.nextInt(10);
            }
        }

        return matrix;
    }

    /*
     * Displays only the first 5 x 5 portion of a matrix.
     */
    private static void displayPart(
            int[][] matrix,
            String title) {

        System.out.println("\n" + title + ":");

        for (int row = 0; row < 5; row++) {

            for (int column = 0; column < 5; column++) {

                System.out.print(
                        matrix[row][column] + "\t");
            }

            System.out.println();
        }
    }
}

/*
 * This task calculates exactly one element of the result matrix.
 *
 * For result[row][column]:
 *
 * result[row][column] =
 * A[row][0] * B[0][column]
 * + A[row][1] * B[1][column]
 * + ...
 * + A[row][99] * B[99][column]
 */
class MatrixElementTask implements Runnable {

    private final int[][] firstMatrix;
    private final int[][] secondMatrix;
    private final int[][] resultMatrix;

    private final int row;
    private final int column;

    public MatrixElementTask(
            int[][] firstMatrix,
            int[][] secondMatrix,
            int[][] resultMatrix,
            int row,
            int column) {

        this.firstMatrix = firstMatrix;
        this.secondMatrix = secondMatrix;
        this.resultMatrix = resultMatrix;
        this.row = row;
        this.column = column;
    }

    @Override
    public void run() {

        int sum = 0;

        /*
         * Calculate the complete dot product for
         * result[row][column].
         */
        for (int k = 0; k < firstMatrix[0].length; k++) {

            int multiplication = firstMatrix[row][k]
                    * secondMatrix[k][column];

            sum = sum + multiplication;
        }

        resultMatrix[row][column] = sum;
    }
}