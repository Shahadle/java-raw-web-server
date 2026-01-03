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
            try (ServerSocket serverSocket = new ServerSocket(port)){
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

                              if (requestLine == null || requestLine.isBlank()) {
                                    continue;
                              }

                              String[] parts = requestLine.split(" ");
                              // HTTP Method
                              String method = parts[0];
                              // Requested path
                              String path = parts[1];

                              HttpResponse response;

                              if(!method.equals("GET")) {
                                    response = HttpResponse.methodNotAllowedHtml("<h1>405 Method Not Allowed</h1>");
                              }else {
                                    if (!path.equals("/")) {
                                          response = HttpResponse.notFoundHtml("<h1>404 Not Found</h1>");
                                    }else {
                                          Path filePath = Path.of("home.html");
                                          if (Files.exists(filePath)) {
                                                String htmlPage = Files.readString(filePath, StandardCharsets.UTF_8);
                                                response = HttpResponse.okHtml(htmlPage);
                                          } else {
                                                response = HttpResponse.notFoundHtml("<h1>404 Not Found</h1>");
                                          }
                                    }
                              }

                              try {
                                    response.writeTo(out);
                                    out.flush();
                              } catch (java.net.SocketException e) {
                                    System.out.println("Client aborted connection: " + e.getMessage());
                              }
                        }
                  }
            }
      }
}