package dev.thesarfo;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * The HttpTask class implements the Runnable interface and is used to process an HTTP request.
 * When executed in a thread, this task will handle an HTTP request on a Socket connection, and send a response message
 */
public class HttpTask implements Runnable {
    private Socket socket;

    /**
     * Constructs a new HttpTask to handle the specified Socket connection.
     *
     * @param socket Socket used to handle HTTP requests
     */
    public HttpTask(Socket socket) {
        this.socket = socket;
    }

    /**
     * Implement the run method of the Runnable interface,
     * which is used to process HTTP requests and send response messages
     */
    @Override
    public void run() {
        if (socket == null) {
            throw new IllegalArgumentException("socket can't be null.");
        }

        try {
            OutputStream outputStream = socket.getOutputStream();
            PrintWriter out = new PrintWriter(outputStream);

            HttpMessageParser.Request httpRequest = HttpMessageParser.parse2request(socket.getInputStream());

            try {
                String result = null;
                String httpRes = HttpMessageParser.buildResponse(httpRequest, result);

                out.print(httpRes);
            } catch (Exception e) {
                String httpRes = HttpMessageParser.buildResponse(httpRequest, e.toString());
                out.print(httpRes);
            }

            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
