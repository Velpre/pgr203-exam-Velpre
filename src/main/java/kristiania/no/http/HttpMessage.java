package kristiania.no.http;

import java.io.IOException;
import java.net.Socket;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class HttpMessage {
    public final Map<String, String> headerFields = new HashMap<>();
    public String startLine;
    public String messageBody;
    private String location;

    public HttpMessage(Socket socket) throws IOException {
        startLine = HttpMessage.readLine(socket);
        readHeaders(socket);
        if (headerFields.containsKey("Content-Length")) {
            messageBody = HttpMessage.readBytes(socket, getContentLength());
        }
    }

    public HttpMessage(String startLine, String messageBody) {
        this.startLine = startLine;
        this.messageBody = messageBody;
    }

    public HttpMessage(String startLine, String messageBody, String location) {
        this.startLine = startLine;
        this.messageBody = messageBody;
        this.location = location;
    }

    static String readBytes(Socket socket, int contentLength) throws IOException {
        StringBuilder buffer = new StringBuilder();
        for (int i = 0; i < contentLength; i++) {
            buffer.append((char) socket.getInputStream().read());
        }
        return URLDecoder.decode(buffer.toString(), StandardCharsets.UTF_8);
    }

    public static Map<String, String> parseRequestParameters(String query) {
        Map<String, String> queryMap = new HashMap<>();
        if (query != null) {
            for (String queryParameter : query.split("&")) {
                int equalsPos = queryParameter.indexOf("=");
                String parameterName = queryParameter.substring(0, equalsPos);
                String parameterValue = queryParameter.substring(equalsPos + 1);
                queryMap.put(parameterName, parameterValue);
            }
        }
        return queryMap;
    }

    public static String readLine(Socket socket) throws IOException {
        StringBuilder buffer = new StringBuilder();
        int c;
        while ((c = socket.getInputStream().read()) != -1) {
            if (c == '\r') {
                socket.getInputStream().read();
                break;
            }
            buffer.append((char) c);
        }
        return URLDecoder.decode(buffer.toString(), StandardCharsets.UTF_8);
    }

    public int getContentLength() {
        return Integer.parseInt(headerFields.get("Content-Length"));
    }


    private void readHeaders(Socket socket) throws IOException {
        String headerLine;
        while (!(headerLine = HttpMessage.readLine(socket)).isBlank()) {
            int colonPos = headerLine.indexOf(':');
            String headerField = headerLine.substring(0, colonPos);
            String headerValue = headerLine.substring(colonPos + 1).trim();
            headerFields.put(headerField, headerValue);
        }
    }

    public void write(Socket socket) throws IOException {
        String response = startLine + "\r\n" +
                "Content-Length: " + messageBody.getBytes(StandardCharsets.UTF_8).length + "\r\n" +
                "Connection: close\r\n" +
                "Content-Type: text/html; charset=utf-8\r\n" +
                "Location: " + location + "\r\n" +
                "\r\n" +
                messageBody;
        socket.getOutputStream().write(response.getBytes(StandardCharsets.UTF_8));
    }
}