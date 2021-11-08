package kristiania.no.http;

import kristiania.no.http.controllers.HttpController;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class HttpServer {
    private final HashMap<String, HttpController> controllers = new HashMap<>();
    private final ServerSocket serverSocket;
    private Path rootDirectory;
    private QuestionDao questionDao;
    private SurveyDao surveyDao;
    private AnswerDao answerDao;
    private UserDao userDao;
    private OptionDao optionDao;
    private int userId;
    private int surveyId;




    public HttpServer(int serverPort) throws IOException {
        serverSocket = new ServerSocket(serverPort);
        new Thread(this::handleClients).start();
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
            if (query != null) {
                Map<String, String> queryMap = parseRequestParameters(query);
                yourName = queryMap.get("firstName") + ", " + queryMap.get("lastName");
            }
            String responseText = "<p>Hello " + yourName + "</p>";

                writeOkResponse(clientSocket, java.net.URLDecoder.decode(responseText, "UTF-8"), "text/html; charset=utf-8");
            } else if(fileTarget.equals("/api/listQuestions")) {
                String responseText = "";
                Map<String, String> queryMap = parseRequestParameters(httpMessage.messageBody);
                if (queryMap.size() != 0){
                    surveyId = Integer.parseInt(queryMap.get("survey"));
                }
                responseText += "<p>Create New user:</p>";
                responseText += "<input type=\"text\" id=\"userName\" name=\"newUser\" label =\"Username:\"> </input><br>";

                responseText += "<p>Chose one of existing users<p>";
                responseText += "<p><label>Select user <select name=\"existingUsers\" id=\"existingUsers\"></select></label></p>";

                for (Question question : questionDao.retrieveFromSurveyId(surveyId)) {
                    responseText += "<h3>" + question.getTitle() + "</h3>\r\n";
                    for (Option option : optionDao.retrieveFromQuestionId(question.getId())){
                        responseText += "<label class =\"radioLabel\"><input required type=\"radio\" id=\"myRange\" name=\"" + question.getId() + "\" value=\"" + option.getOptionName() +"\">" + option.getOptionName() +"</label>";
                    }
                    //Finne ut om vi skal ha slider hele tiden eller ikke
                    //responseText += "<input name = \"" + question.getId() + "\" type=\"range\" min=\"1\" max=\"5\" value=\"3\" class=\"slider\" id=\"myRange\">";
                }
                responseText += "<br><button>Answer</button>";
                writeOkResponse(clientSocket, java.net.URLDecoder.decode(responseText, "UTF-8"), "text/html; charset=utf-8");
            }else if(fileTarget.equals("/api/listAnswers")){
                String responseText = "";
                Map<String, String> queryMap = parseRequestParameters(httpMessage.messageBody);
                if (queryMap.size() != 0){
                    surveyId = Integer.parseInt(queryMap.get("survey"));
                    userId = Integer.parseInt(queryMap.get("user"));
                }

                for (Question question : questionDao.retrieveFromSurveyId(surveyId)) {
                    responseText += "<h3>" + question.getTitle() + "</h3>\r\n";

                    //Sjekker om det er Admin som er bruker - i så fall printer ut alle answers for valgt survey
                    if(userId == 1){
                        for (Answer allAnswers : answerDao.retrieveFromQuestionId(question.getId())){
                            responseText += "<p>" + allAnswers.getAnswer() + "</p>\r\n";
                        }
                    }else{
                        for (Answer answerByQuestionId : answerDao.retrieveFromQuestionId(question.getId())){
                            if(answerByQuestionId.getUserId() == userId ){
                                responseText += "<p>" + answerByQuestionId.getAnswer() + "</p>\r\n";
                            }
                        }
                    }
                }
                writeOkResponse(clientSocket, java.net.URLDecoder.decode(responseText, "UTF-8"), "text/html; charset=utf-8");
            } else if (fileTarget.equals("/api/changeQuestion")){
                Map<String, String> queryMap = parseRequestParameters(httpMessage.messageBody);

                questionDao.updateQuestion(Long.parseLong(queryMap.get("question")), queryMap.get("title"));
                optionDao.updateOption(queryMap.get("option1"), Integer.parseInt(queryMap.get("question")));
                optionDao.updateOption(queryMap.get("option2"), Integer.parseInt(queryMap.get("question")));
                optionDao.updateOption(queryMap.get("option3"), Integer.parseInt(queryMap.get("question")));
                String responseText = "Done";
                writeOkResponse(clientSocket, java.net.URLDecoder.decode(responseText, "UTF-8"), "text/html; charset=utf-8");
            }
            else if(fileTarget.equals("/api/answerQuestions")){
                String responseText = "You have answered: ";

                Map<String, String> queryMap = parseRequestParameters(httpMessage.messageBody);
                //Finner ut av om bruker lager ny user eller velger eksisterende
                User newUser;
                User existingUser;
                if (queryMap.get("newUser") == ""){
                  existingUser = userDao.retrieve(Long.parseLong(queryMap.get("existingUsers")));
                    queryMap.remove("newUser");
                    queryMap.remove("existingUsers");
                    Object[] keySet = queryMap.keySet().toArray();

                    for (int i = 0; i < keySet.length; i++) {
                        Answer a = new Answer(queryMap.get(keySet[i]), Integer.parseInt((String) keySet[i]), (int) existingUser.getId());
                        answerDao.save(a);

                        responseText += " " + a.getAnswer();
                    }

                    responseText += " with user " + existingUser.getUserName();

                }else {
                    newUser = new User(queryMap.get("newUser"));
                    userDao.save(newUser);
                    queryMap.remove("newUser");
                    queryMap.remove("existingUsers");

                    Object[] keySet = queryMap.keySet().toArray();

                    for (int i = 0; i < keySet.length; i++) {
                        Answer a = new Answer(queryMap.get(keySet[i]), Integer.parseInt((String) keySet[i]), (int) newUser.getId());
                        answerDao.save(a);

                        responseText += " " + a.getAnswer();
                    }

                    responseText += " with user " + newUser.getUserName();
                }

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
            }
            else if(fileTarget.equals("/api/listUsers")){
                String responseText = "";

                for (User user : userDao.listAll()) {
                    responseText += "<option value=" + user.getId() + ">" + user.getUserName() + "</option>";
                }


                /*
                if(requestTarget.equals("/takeSurvey.html")){

                    for (User user : userDao.listAll()) {

                        if (user.getId() == 1) {
                            System.out.println("test");
                            responseText += "";

                        } else {
                            String responseText1 = "";
                            for (User user1 : userDao.listAll()) {

                                responseText1 += "<option value=" + user1.getId() + ">" + user1.getUserName() + "</option>";
                            }

                        }
                    }
                }else {
                    /*
                    for (User user : userDao.listAll()) {
                        responseText += "<option value=" + user.getId() + ">" + user.getUserName() + "</option>";
                    }

                     */

                writeOkResponse(clientSocket, java.net.URLDecoder.decode(responseText, "UTF-8"), "text/html; charset=utf-8");

            } else if(fileTarget.equals("/api/listAllQuestions")){
                String responseText = "";
                for (Question question : questionDao.listAll()) {
                    responseText += "<option value=" + question.getId() + ">" + question.getTitle() + "</option>";
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
