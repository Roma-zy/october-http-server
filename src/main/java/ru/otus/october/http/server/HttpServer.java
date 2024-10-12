package ru.otus.october.http.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HttpServer {
    private int port;
    private Dispatcher dispatcher;
    private ExecutorService threadPool;

    public HttpServer(int port, int threadPoolSize) {
        this.port = port;
        this.dispatcher = new Dispatcher();
        this.threadPool = Executors.newFixedThreadPool(threadPoolSize);
    }

    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Сервер запущен на порту: " + port);
            while (true) {
                Socket socket = serverSocket.accept();
                threadPool.execute(() -> handleRequest(socket));
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            threadPool.shutdown();
        }
    }

    private void handleRequest(Socket socket) {
        try {
            byte[] buffer = new byte[8192];
            int n = socket.getInputStream().read(buffer);
            String rawRequest = new String(buffer, 0, n);
            HttpRequest request = new HttpRequest(rawRequest);
            request.info(true);
            dispatcher.execute(request, socket.getOutputStream());
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}
