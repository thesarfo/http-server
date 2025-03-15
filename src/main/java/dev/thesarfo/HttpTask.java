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
            System.out.println("=== HTTP REQUEST ===");
            System.out.println("Method: " + httpRequest.getMethod());
            System.out.println("URI: " + httpRequest.getUri());
            System.out.println("Version: " + httpRequest.getVersion());
            System.out.println("Headers: " + httpRequest.getHeaders());
            System.out.println("Message: " + httpRequest.getMessage());
            System.out.println("====================================");


            try {
                String result = "default response";
                String httpRes = HttpMessageParser.buildResponse(httpRequest, result);

                System.out.println("=== HTTP RESPONSE ===");
                System.out.println("Status: " + httpRequest.getVersion() + " 200 ok");
                System.out.println("Headers: Content-Type: application/json, Content-Length: " + result.getBytes().length);
                System.out.println("Body: " + result);
                System.out.println("=====================");

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
