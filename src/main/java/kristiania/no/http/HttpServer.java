package kristiania.no.http;

import kristiania.no.http.controllers.HttpController;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.SQLException;
import java.util.HashMap;

public class HttpServer {
    private final HashMap<String, HttpController> controllers = new HashMap<>();
    private final ServerSocket serverSocket;
    private Path rootDirectory;


    public HttpServer(int serverPort) throws IOException {
        serverSocket = new ServerSocket(serverPort);
        new Thread(this::handleClients).start();
    }


    private void handleClients() {
        try {
            while (true) {
                handleClient();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void handleClient() throws IOException, SQLException {
        Socket clientSocket = serverSocket.accept();
        HttpMessage httpMessage = new HttpMessage(clientSocket);
        String[] requestLine = httpMessage.startLine.split(" ");
        String requestTarget = requestLine[1];

        if (requestTarget.equals("/")) requestTarget = "/index.html";

        int questionPos = requestTarget.indexOf('?');
        String fileTarget;
        //Skal vi slette query?
        String query = null;
        if (questionPos != -1) {
            fileTarget = requestTarget.substring(0, questionPos);
            query = requestTarget.substring(questionPos + 1);
        } else {
            fileTarget = requestTarget;
        }

        if (controllers.containsKey(fileTarget)) {
            HttpMessage response = controllers.get(fileTarget).handle(httpMessage);
            response.write(clientSocket);
        } else {
            if (rootDirectory != null && Files.exists(rootDirectory.resolve(requestTarget.substring(1)))) {
                String responseText = Files.readString(rootDirectory.resolve(requestTarget.substring(1)));
                String contentType = "text/plain";
                if (requestTarget.endsWith(".html")) {
                    contentType = "text/html";
                } else if (requestTarget.endsWith(".css")) {
                    contentType = "text/css";
                }

                writeOkResponse(clientSocket, java.net.URLDecoder.decode(responseText, "UTF-8"), contentType);
                return;
            }

            String responseText = "File not found: " + requestTarget;
            String response = "HTTP/1.1 404 Not found\r\n" +
                    "Content-Length: " + responseText.getBytes().length + "\r\n" +
                    "\r\n" +
                    responseText;
            clientSocket.getOutputStream().write(response.getBytes());
        }
    }

    private void writeOkResponse(Socket clientSocket, String responseText, String contentType) throws IOException {
        String response = "HTTP/1.1 200 OK\r\n" +
                "Content-Length: " + responseText.getBytes().length + "\r\n" +
                "Content-Type:" + contentType + "\r\n" +
                "Connection: close\r\n" +
                "\r\n" +
                responseText;
        clientSocket.getOutputStream().write(response.getBytes());
    }


    public int getPort() {
        return serverSocket.getLocalPort();
    }

    public void setRoot(Path path) {
        this.rootDirectory = path;
    }

    public void addController(String path, HttpController controller) {
        controllers.put(path, controller);
    }

}
