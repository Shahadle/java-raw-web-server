import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class HttpResponse{
      private int statusCode;
      private String statusText;
      private final List<Header> headers = new ArrayList<>();
      private byte[] body;

      public HttpResponse(int statusCode, String statusText) {
            this.statusCode = statusCode;
            this.statusText = statusText;
      }

      public static HttpResponse okHtml(String html) {
            return htmlResponse(200, "OK", html);
      }

      public static HttpResponse notFoundHtml(String html) {
            return htmlResponse(404, "Not Found", html);
      }

      public static HttpResponse methodNotAllowedHtml(String html) {
            return htmlResponse(405, "Method Not Allowed", html);
      }

      public void addHeader(String name, String value) {
            headers.add(new Header(name, value));
      }

      private static HttpResponse htmlResponse(int status, String text, String html) {
            HttpResponse res = new HttpResponse(status, text);

            byte[] bodyBytes = html.getBytes(StandardCharsets.UTF_8);
            res.body = bodyBytes;

            res.headers.add(new Header("Content-Type", "text/html; charset=utf-8"));
            res.headers.add(new Header("Content-Length", String.valueOf(bodyBytes.length)));
            res.headers.add(new Header("Connection", "close"));

            return res;
      }

      public void writeTo(OutputStream out) throws IOException {
            StringBuilder sb = new StringBuilder();

            // Status line
            sb.append("HTTP/1.1 ")
                  .append(statusCode)
                  .append(" ")
                  .append(statusText)
                  .append("\r\n");

            // Headers
            for (Header h : headers) {
                  sb.append(h.toHttpString());
            }

            // End headers
            sb.append("\r\n");

            out.write(sb.toString().getBytes(StandardCharsets.UTF_8));
            out.write(body);
      }
}