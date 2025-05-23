import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.nio.file.Paths;

public class SimpleFileServer {
    public static void main(String[] args) throws IOException {
        int port = 8000;
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext("/", new HttpHandler() {
            @Override
            public void handle(HttpExchange exchange) throws IOException {
                String path = exchange.getRequestURI().getPath();
                if (path.equals("/")) {
                    path = "/library.html";
                }
                File file = new File("." + path).getCanonicalFile();
                if (!file.exists() || !file.isFile()) {
                    String response = "404 (Not Found)\n";
                    exchange.sendResponseHeaders(404, response.length());
                    OutputStream os = exchange.getResponseBody();
                    os.write(response.getBytes());
                    os.close();
                } else {
                    String mime = Files.probeContentType(file.toPath());
                    exchange.getResponseHeaders().set("Content-Type", mime != null ? mime : "application/octet-stream");
                    byte[] bytes = Files.readAllBytes(file.toPath());
                    exchange.sendResponseHeaders(200, bytes.length);
                    OutputStream os = exchange.getResponseBody();
                    os.write(bytes);
                    os.close();
                }
            }
        });
        server.start();
        System.out.println("Server started at http://localhost:" + port);
    }
}
