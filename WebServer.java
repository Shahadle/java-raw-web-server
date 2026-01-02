import java.net.*;
import java.io.*;
import java.nio.file.*;
import java.nio.charset.StandardCharsets;

/***
 * Raw HTTP web server built using Java sockets.
 * Listens for connections and parses HTTP requests
 * and serves a HTML file (home.html).
 */
public class WebServer {
      public static void main(String[] args) throws IOException {
            // Port the server will listen on
            int port = 4221;
            ServerSocket serverSocket = new ServerSocket(port);
            System.out.println("Server listening on port:" + port);

            while (true) {
                 try (Socket client = serverSocket.accept()) {
                        System.out.println("Client connected.");

                        InputStream in = client.getInputStream();
                        OutputStream out = client.getOutputStream();

                        BufferedReader reader = new BufferedReader(
                              new InputStreamReader(in, StandardCharsets.UTF_8)
                        );

                        // Read the HTTP request line
                        String requestLine = reader.readLine();
                        System.out.println("Request: " + requestLine);

                        String[] parts = requestLine.split(" ");
                        // HTTP Method
                        String method = parts[0];
                        // Requested path
                        String path = parts[1];

                        String httpBody;
                        int status = 200;
                        String statusText = "OK";

                        if(!method.equals("GET")) {
                              status = 405;
                              statusText = "Method Not Allowed";
                              httpBody = "<h1>405 Method Not Allowed</h1>";
                        }else {
                              Path filePath = Path.of("home.html");

                              if (Files.exists(filePath)) {
                                    httpBody = Files.readString(filePath, StandardCharsets.UTF_8);
                              } else {
                                    status = 404;
                                    statusText = "Not Found";
                                    httpBody = "<h1>404 Not Found</h1>";
                              }
                        }

                        byte[] bodyBytes = httpBody.getBytes(StandardCharsets.UTF_8);

                        String headers =
                              "HTTP/1.1 " + status + " " + statusText + "\r\n" +
                              "Content-Type: text/html; charset=utf-8\r\n" +
                              "Content-Length: " + bodyBytes.length + "\r\n" +
                              "Connection: close\r\n" +
                              "\r\n";

                        out.write(headers.getBytes(StandardCharsets.UTF_8));
                        out.write(bodyBytes);
                        out.flush();
                  }
            }
      }
}