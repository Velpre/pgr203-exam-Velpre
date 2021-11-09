package kristiania.no.http;

import java.io.IOException;
import java.net.Socket;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

public class HttpMessage {
    public final Map<String, String> headerFields = new HashMap<>();
    public String startLine;
    public String messageBody;

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


    static String readBytes(Socket socket, int contentLength) throws IOException {
        StringBuilder buffer = new StringBuilder();
        for (int i = 0; i < contentLength; i++) {
            buffer.append((char) socket.getInputStream().read());
        }
        return URLDecoder.decode(buffer.toString(), "UTF-8");
    }
/*
    static String readLine(Socket socket) throws IOException {
        StringBuilder buffer = new StringBuilder();
        int c;
        while ((c = socket.getInputStream().read()) != '\r') {
            buffer.append((char) c);
        }
        int expectedNewline = socket.getInputStream().read();
        assert expectedNewline == '\n';
        return URLDecoder.decode(buffer.toString(), "UTF-8");
    }

 */


    //Tester denne istedenfor. prøver å fikse heap space feilen

    public static String readLine(Socket socket) throws IOException {
        StringBuilder line = new StringBuilder();
        int c;
        while((c = socket.getInputStream().read()) != -1){
            if(c == '\r'){
                socket.getInputStream().read();
                break;
            }
            line.append((char) c);
        }
        return line.toString();
    }

    public int getContentLength() {
        return Integer.parseInt(headerFields.get("Content-Length"));
    }

    public String getHeader(String headerName) {
        return headerFields.get(headerName);
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
                "Content-Length: " + messageBody.length() + "\r\n" +
                "Connection: close\r\n" +
                "Content-Type: text/html; charset=utf-8\r\n" +
                "\r\n" +
                messageBody;
        socket.getOutputStream().write(response.getBytes());
    }
}