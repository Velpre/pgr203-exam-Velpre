package kristiania.no.http;

import kristiania.no.jdbc.answer.Answer;
import kristiania.no.jdbc.answer.AnswerDao;
import kristiania.no.jdbc.options.Option;
import kristiania.no.jdbc.options.OptionDao;
import kristiania.no.jdbc.question.Question;
import kristiania.no.jdbc.question.QuestionDao;
import kristiania.no.jdbc.survey.Survey;
import kristiania.no.jdbc.survey.SurveyDao;
import kristiania.no.jdbc.user.User;
import kristiania.no.jdbc.user.UserDao;
import org.flywaydb.core.Flyway;
import org.postgresql.ds.PGSimpleDataSource;

import javax.sql.DataSource;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.*;

public class HttpServer {
    private final HashMap<String, HttpController> controllers = new HashMap<>();
    private final ServerSocket serverSocket;
    private Path rootDirectory;

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

    private void handleClient() throws IOException, SQLException {
            Socket clientSocket = serverSocket.accept();
            HttpMessage httpMessage = new HttpMessage(clientSocket);
            String[] requestLine = httpMessage.startLine.split(" ");
            String requestTarget = requestLine[1];

            if(requestTarget.equals("/")) requestTarget = "/index.html";

            int questionPos = requestTarget.indexOf('?');
            String fileTarget;
            String query = null;
            if (questionPos != -1) {
                fileTarget = requestTarget.substring(0, questionPos);
                query = requestTarget.substring(questionPos+1);
            } else {
                fileTarget = requestTarget;
            }

            if (controllers.containsKey(fileTarget)) {
                HttpMessage response = controllers.get(fileTarget).handle(httpMessage);
                response.write(clientSocket);
            } else if (fileTarget.equals("/hello")) {
                String yourName = "world";
                if (query != null){
                    Map<String, String> queryMap = parseRequestParameters(query);
                    yourName = queryMap.get("firstName") + ", " + queryMap.get("lastName");
                }
                String responseText = "<p>Hello " + yourName + "</p>";

                writeOkResponse(clientSocket, java.net.URLDecoder.decode(responseText, "UTF-8"), "text/html; charset=utf-8");
            } else {
                if (rootDirectory != null && Files.exists(rootDirectory.resolve(requestTarget.substring(1)))) {
                    String responseText = Files.readString(rootDirectory.resolve(requestTarget.substring(1)));
                    String contentType = "text/plain";
                    if (requestTarget.endsWith(".html")) {
                        contentType = "text/html";
                    }else if (requestTarget.endsWith(".css")) {
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

    public static Map<String, String> parseRequestParameters(String query) {
        Map<String,String> queryMap = new HashMap<>();
        if (query != null){
            for (String queryParameter : query.split("&")) {
                int equalsPos = queryParameter.indexOf("=");
                String parameterName = queryParameter.substring(0,equalsPos);
                String parameterValue = queryParameter.substring(equalsPos+1);
                queryMap.put(parameterName,parameterValue);
            }
        }
        return queryMap;
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
