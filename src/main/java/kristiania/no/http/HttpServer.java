package kristiania.no.http;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HttpServer {
    private final ServerSocket serverSocket;
    private Path rootDirectory;
    private List<Question> questions= new ArrayList<>();

    public HttpServer(int serverPort) throws IOException {
        serverSocket = new ServerSocket(serverPort);
        new Thread(this::handleClients).start();
    }

    private void handleClients() {
        try{
            while (true){
                handleClient();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handleClient() throws IOException {
            Socket clientSocket = serverSocket.accept();
            HttpMessage httpMessage = new HttpMessage(clientSocket);
            String[] requestLine = httpMessage.startLine.split(" ");
            String requestTarget = requestLine[1];

            if(requestTarget.equals("/")) requestTarget ="/index.html";

            int questionPos = requestTarget.indexOf('?');
            String fileTarget;
            String query = null;
            if (questionPos != -1) {
                fileTarget = requestTarget.substring(0, questionPos);
                query = requestTarget.substring(questionPos+1);
            } else {
                fileTarget = requestTarget;
            }

            // Prøve å kvitte seg med /hello etterhvert
            if (fileTarget.equals("/hello")) {
                String yourName = "world";
                if (query != null){
                    Map<String, String> queryMap = parseRequestParameters(query);
                    yourName = queryMap.get("firstName") + ", " + queryMap.get("lastName");
                }
                String responseText = "<p>Hello " + yourName + "</p>";

                writeOkResponse(clientSocket, responseText, "text/html");
            } else if(fileTarget.equals("/api/questions")) {

                String responseText = "";

                for (Question questions : questions) {
                    responseText += "<p>" + questions.getTitle() + "</p>";
                }

                writeOkResponse(clientSocket, responseText, "text/html");
            }else {
                if (rootDirectory != null && Files.exists(rootDirectory.resolve(requestTarget.substring(1)))) {
                    String responseText = Files.readString(rootDirectory.resolve(requestTarget.substring(1)));
                    String contentType = "text/plain";
                    if (requestTarget.endsWith(".html")) {
                        contentType = "text/html";
                    }else if (requestTarget.endsWith(".css")) {
                        contentType = "text/css";
                    }

                    writeOkResponse(clientSocket, responseText, contentType);
                    return;
                }

                String responseText = "File not found: " + requestTarget;
                String response = "HTTP/1.1 404 Not found\r\n" +
                        "Content-Length: " + responseText.length() + "\r\n" +
                        "\r\n" +
                        responseText;
                clientSocket.getOutputStream().write(response.getBytes());
            }
        }

    private void writeOkResponse(Socket clientSocket, String responseText, String contentType) throws IOException {
        String response = "HTTP/1.1 200 OK\r\n" +
                "Content-Length: " + responseText.length() + "\r\n" +
                "Content-Type:" + contentType + "\r\n" +
                "Connection: close\r\n" +
                "\r\n" +
                responseText;
        clientSocket.getOutputStream().write(response.getBytes());
    }

    private Map<String, String> parseRequestParameters(String query) {
        Map<String,String> queryMap = new HashMap<>();
        for (String queryParameter : query.split("&")) {
            int equalsPos = queryParameter.indexOf("=");
            String parameterName = queryParameter.substring(0,equalsPos);
            String parameterValue = queryParameter.substring(equalsPos+1);
            queryMap.put(parameterName,parameterValue);
        }
        return queryMap;
    }

    public int getPort() {
        return serverSocket.getLocalPort();
    }

    public void setRoot(Path path) {
        this.rootDirectory = path;
    }

    public void setQuestions(List<Question> q) {
        this.questions = q;
    }


    public static void main(String[] args) throws IOException {
        HttpServer httpServer = new HttpServer(8080);
        Question q = new Question();
        q.setTitle("title1");
        Question q2 = new Question();
        q2.setTitle("title2");
        httpServer.setQuestions(List.of(q, q2));
        httpServer.setRoot(Paths.get("src/main/resources"));


    }
}
