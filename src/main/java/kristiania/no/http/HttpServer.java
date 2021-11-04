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
    private final ServerSocket serverSocket;
    private Path rootDirectory;
    private QuestionDao questionDao;
    private SurveyDao surveyDao;
    private AnswerDao answerDao;
    private UserDao userDao;
    private OptionDao optionDao;
    private int savedQuery;



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
            } else if(fileTarget.equals("/api/listQuestions")) {
                String responseText = "";
                Map<String, String> queryMap = parseRequestParameters(httpMessage.messageBody);
                if (queryMap.size() != 0){
                    savedQuery = Integer.parseInt(queryMap.get("survey"));
                }
                responseText += "<p>Write username:</p>";
                responseText += "<input required type=\"text\" id=\"userName\" name=\"userName\" label =\"Username:\"> </input><br>";

                for (Question question : questionDao.retrieveFromSurveyId(savedQuery)) {
                    responseText += "<h3>" + question.getTitle() + "</h3>\r\n";
                    for (Option option : optionDao.retrieveFromQuestionId(question.getId())){
                       // responseText += "<label>" + option.getOptionName() + "</label>";
                        responseText += "<label class =\"radioLabel\"><input type=\"radio\" id=\"myRange\" name=\"" + question.getId() + "\" value=\"" + option.getOptionName() +"\">" + option.getOptionName() +"</label>";
                    }

                    //responseText += "<input name = \"" + question.getId() + "\" type=\"range\" min=\"1\" max=\"5\" value=\"3\" class=\"slider\" id=\"myRange\">";
                }
                responseText += "<br><button>Answer</button>";
                writeOkResponse(clientSocket, java.net.URLDecoder.decode(responseText, "UTF-8"), "text/html; charset=utf-8");
            }else if(fileTarget.equals("/api/answerQuestions")){
                String responseText = "You have added: ";

                Map<String, String> queryMap = parseRequestParameters(httpMessage.messageBody);
                User user = new User(queryMap.get("userName"));
                userDao.save(user);
                queryMap.remove("userName");

                Object[] keySet = queryMap.keySet().toArray();

                for (int i = 0; i < keySet.length; i++) {
                    Answer a = new Answer(queryMap.get(keySet[i]), Integer.parseInt((String) keySet[i]), (int) user.getId());
                    answerDao.save(a);
                    responseText += " " + a.getAnswer();
                }

                responseText += " with user" + user.getUserName();

                writeOkResponse(clientSocket, java.net.URLDecoder.decode(responseText, "UTF-8"), "text/html; charset=utf-8");

            } else if (fileTarget.equals("/api/newQuestion")) {
                Map<String, String> queryMap = parseRequestParameters(httpMessage.messageBody);
                Question q = new Question(queryMap.get("title"), Integer.parseInt(queryMap.get("survey")));
                questionDao.save(q);
                Option o = new Option(queryMap.get("option1"), (int) q.getId());
                Option o1 = new Option(queryMap.get("option2"), (int) q.getId());
                Option o2 = new Option(queryMap.get("option3"), (int) q.getId());
                optionDao.save(o);
                optionDao.save(o1);
                optionDao.save(o2);
                String responseText = "You have added: Question: " + q.getTitle()  + " Survey: " + q.getSurveyId() + " Options:" + o.getOptionName() + " " + o1.getOptionName() + " " + o2.getOptionName() + ".";
                writeOkResponse(clientSocket, java.net.URLDecoder.decode(responseText, "UTF-8"), "text/html; charset=utf-8");

            }else if (fileTarget.equals("/api/newSurvey")) {
                Map<String, String> queryMap = parseRequestParameters(httpMessage.messageBody);
                Survey s = new Survey(queryMap.get("title"));
                surveyDao.save(s);
                String responseText = "You have added: Title: " + s.getName() + ".";
                writeOkResponse(clientSocket, java.net.URLDecoder.decode(responseText, "UTF-8"), "text/html; charset=utf-8");
            }
            else if (fileTarget.equals("/api/deleteSurvey")) {
                String responseText = "";
                if (httpMessage.messageBody != ""){
                    Map<String, String> queryMap = parseRequestParameters(httpMessage.messageBody);
                    surveyDao.delete(Integer.parseInt(queryMap.get("survey")));
                    responseText = "You have removed survey with id: " + queryMap.get("survey") + ".";
                }
                writeOkResponse(clientSocket, java.net.URLDecoder.decode(responseText, "UTF-8"), "text/html; charset=utf-8");
            }
            else if (fileTarget.equals("/api/listSurveyOptions")) {
                String responseText = "";

                for (Survey survey : surveyDao.listAll()) {
                    responseText += "<option value=" + survey.getId() + ">" + survey.getName() + "</option>";
                }
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

    private Map<String, String> parseRequestParameters(String query) {
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

    public void setOptionDao(OptionDao optionDao) {
        this.optionDao = optionDao;
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
        HttpServer httpServer = new HttpServer(8070);
        System.out.println("Server running at: http://localhost:"+ httpServer.getPort() + "/");

        DataSource dataSource = createDataSource();
        httpServer.questionDao =  new QuestionDao(dataSource);
        httpServer.surveyDao =  new SurveyDao(dataSource);
        httpServer.answerDao = new AnswerDao(dataSource);
        httpServer.userDao = new UserDao(dataSource);
        httpServer.optionDao = new OptionDao(dataSource);
        httpServer.setRoot(Paths.get("src/main/resources/webfiles"));
    }

}
