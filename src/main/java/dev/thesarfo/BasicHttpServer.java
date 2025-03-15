package dev.thesarfo;


import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * simple HTTP server that handles incoming HTTP requests using a thread pool.
 */
public class BasicHttpServer {
    //A single-threaded ExecutorService used to perform the HTTP server startup task.
    private static ExecutorService bootstrapExecutor = Executors.newSingleThreadExecutor();

    /*
    * A thread pool used to process HTTP requests from clients.
    * The size of the thread pool is equal to the number of available cores of the processor,
    * the queue size is 100, and the DiscardPolicy discard policy is used.
     */
    private static ExecutorService taskExecutor;
    private static int PORT = 8999;

    /**
     * Starts the HTTP server and initializes the thread pool for handling requests.
     */
    public static void startHttpServer() {
        int nThreads = Runtime.getRuntime().availableProcessors();
        taskExecutor =
                new ThreadPoolExecutor(nThreads, nThreads, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>(100),
                        new ThreadPoolExecutor.DiscardPolicy());

        while (true) {
            try {
                ServerSocket serverSocket = new ServerSocket(PORT);
                bootstrapExecutor.submit(new ServerThread(serverSocket));
                break;
            } catch (Exception e) {
                try {
                    TimeUnit.SECONDS.sleep(10);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                }
            }
        }

        bootstrapExecutor.shutdown();
    }

    /**
     * ServerThread is a Runnable that handles incoming client connections.
     */
    private static class ServerThread implements Runnable {
        private ServerSocket serverSocket;


        /**
         * Constructs a ServerThread with the specified ServerSocket.
         *
         * @param s the ServerSocket to accept client connections
         * @throws IOException if an I/O error occurs when opening the socket
         */
        public ServerThread(ServerSocket s) throws IOException {
            this.serverSocket = s;
        }

        @Override
        public void run() {
            while (true) {
                try {
                    Socket socket = this.serverSocket.accept();
                    HttpTask eventTask = new HttpTask(socket);
                    taskExecutor.submit(eventTask);
                } catch (Exception e) {
                    e.printStackTrace();
                    try {
                        TimeUnit.SECONDS.sleep(1);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                    }
                }
            }
        }
    }
}