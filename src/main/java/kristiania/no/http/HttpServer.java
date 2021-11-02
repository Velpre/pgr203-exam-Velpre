package kristiania.no.http;

import kristiania.no.jdbc.*;
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
    private final ServerSocket serverSocket;
    private Path rootDirectory;
    private QuestionDao questionDao;
    private SurveyDao surveyDao;
    private AnswerDao answerDao;



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

            // Prøve å kvitte seg med /hello etterhvert
            if (fileTarget.equals("/hello")) {
                String yourName = "world";
                if (query != null){
                    Map<String, String> queryMap = parseRequestParameters(query);
                    yourName = queryMap.get("firstName") + ", " + queryMap.get("lastName");
                }
                String responseText = "<p>Hello " + yourName + "</p>";

                writeOkResponse(clientSocket, java.net.URLDecoder.decode(responseText, "UTF-8"), "text/html; charset=utf-8");
            } else if(fileTarget.equals("/api/questions")) {

                String responseText = "";

                for (Question questions : questionDao.listAll()) {
                    responseText += "<p>" + questions.getTitle() + "</p>" +
                            "<p><label>Answer question: <input type=text name=answer></label></p>";
                }

                writeOkResponse(clientSocket, java.net.URLDecoder.decode(responseText, "UTF-8"), "text/html; charset=utf-8");
            }else if(fileTarget.equals("/api/addAnswers")){
                //Her må det jobbes mere med

                Map<String, String> queryMap = parseRequestParameters(httpMessage.messageBody);
                Answer a = new Answer(queryMap.get("answer"), 1);
                answerDao.save(a);
                String responseText = "You have added answers.";
                writeOkResponse(clientSocket, java.net.URLDecoder.decode(responseText, "UTF-8"), "text/html; charset=utf-8");
            }
            else if (fileTarget.equals("/api/newQuestion")) {
                Map<String, String> queryMap = parseRequestParameters(httpMessage.messageBody);
                Question q = new Question(queryMap.get("title"), Integer.parseInt(queryMap.get("survey")));
                questionDao.save(q);
                String responseText = "You have added: Question: " + q.getTitle()  + " Survey: " + q.getSurveyId() + ".";
                writeOkResponse(clientSocket, java.net.URLDecoder.decode(responseText, "UTF-8"), "text/html; charset=utf-8");

            }else if (fileTarget.equals("/api/surveyOptions")) {
                String responseText = "";

                int i = 1;
                for (Survey survey : surveyDao.listAll()) {
                    responseText += "<option value=" + i++ + ">" + survey.getName() + "</option>";
                }
                writeOkResponse(clientSocket, java.net.URLDecoder.decode(responseText, "UTF-8"), "text/html; charset=utf-8");
            }

            else {
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

    /*
    Finne ut om vi trenger den
    public void addQuestions(Question q) {
        questions.add(q);
    }
 */
    public List<Question> getQuestions() throws SQLException {
        return questionDao.listAll();
    }


//Settere for Dao klasser
    public void setQuestionDao(QuestionDao questionDao) {
        this.questionDao = questionDao;
    }
    public void setSurveyDao(SurveyDao surveyDao) {
        this.surveyDao = surveyDao;
    }

    public void setAnswerDao(AnswerDao answerDao) {
        this.answerDao = answerDao;
    }

    private static DataSource createDataSource() {
        PGSimpleDataSource dataSource = new PGSimpleDataSource();
        dataSource.setUrl("jdbc:postgresql://localhost:5432/question_db");
        dataSource.setUser("question_dbuser");
        dataSource.setPassword("P545v#C@ZZ");
        Flyway.configure().dataSource(dataSource).load().migrate();
        return dataSource;
    }


    public static void main(String[] args) throws IOException {
        HttpServer httpServer = new HttpServer(8081);
        httpServer.questionDao =  new QuestionDao(createDataSource());
        httpServer.surveyDao =  new SurveyDao(createDataSource());
        httpServer.answerDao = new AnswerDao(createDataSource());
        httpServer.setRoot(Paths.get("src/main/resources/webfiles"));
    }

}
