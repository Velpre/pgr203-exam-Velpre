package kristiania.no.http.controllers;

import kristiania.no.http.HttpMessage;
import kristiania.no.jdbc.answer.Answer;
import kristiania.no.jdbc.answer.AnswerDao;
import kristiania.no.jdbc.question.Question;
import kristiania.no.jdbc.question.QuestionDao;

import java.sql.SQLException;
import java.util.Map;


public class ListAnswersController implements HttpController {
    private final QuestionDao questionDao;
    private final AnswerDao answerDao;

    private int surveyId;
    private int userId;
    private String showAllUsers;
    private HttpMessage httpMessage;

    public ListAnswersController(QuestionDao questionDao, AnswerDao answerDao) {
        this.questionDao = questionDao;
        this.answerDao = answerDao;
    }

    @Override
    public HttpMessage handle(HttpMessage request) throws SQLException {
        String responseText = "";
        if (request.startLine.startsWith("POST")) {
            Map<String, String> queryMap = HttpMessage.parseRequestParameters(request.messageBody);

            surveyId = Integer.parseInt(queryMap.get("survey"));
            userId = Integer.parseInt(queryMap.get("user"));
            showAllUsers = queryMap.get("allUsers");
            responseText = "POST Done";
            httpMessage = new HttpMessage("HTTP/1.1 303", responseText, "../showAnswers.html");
        } else if (request.startLine.startsWith("GET")) {
            if (showAllUsers == null) {
                for (Question question : questionDao.retrieveFromSurveyId(surveyId)) {
                    responseText += "<h3>" + question.getTitle() + "</h3>\r\n";
                    for (Answer answerByQuestionId : answerDao.retrieveFromQuestionId(question.getId())) {
                        if (answerByQuestionId.getUserId() == userId) {
                            responseText += "<p>" + answerByQuestionId.getAnswer() + "</p>\r\n";
                        }
                    }

                }
            } else {
                for (Question question : questionDao.retrieveFromSurveyId(surveyId)) {
                    responseText += "<h3>" + question.getTitle() + "</h3>\r\n";
                    for (Answer allAnswers : answerDao.retrieveFromQuestionId(question.getId())) {
                        responseText += "<p>" + allAnswers.getAnswer() + "</p>\r\n";
                    }
                }
            }
            httpMessage = new HttpMessage("HTTP/1.1 200", responseText);

        }

        return httpMessage;
    }

}
