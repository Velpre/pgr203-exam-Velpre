package kristiania.no.http.controllers;

import kristiania.no.http.HttpMessage;
import kristiania.no.jdbc.answer.Answer;
import kristiania.no.jdbc.answer.AnswerDao;
import kristiania.no.jdbc.question.Question;
import kristiania.no.jdbc.question.QuestionDao;
import kristiania.no.jdbc.user.UserDao;

import java.awt.*;
import java.sql.SQLException;
import java.util.Map;

import static kristiania.no.http.HttpServer.parseRequestParameters;

public class ListAnswersController implements HttpController {
    private final QuestionDao questionDao;
    private final AnswerDao answerDao;

    private int surveyId;
    private int userId;
    private String allUsers;

    public ListAnswersController(QuestionDao questionDao, AnswerDao answerDao) {
        this.questionDao = questionDao;
        this.answerDao = answerDao;
    }

    @Override
    public HttpMessage handle(HttpMessage request) throws SQLException {
        String responseText ="";
        Map<String, String> queryMap = parseRequestParameters(request.messageBody);

        if (queryMap.size() != 0) {
            surveyId = Integer.parseInt(queryMap.get("survey"));
            userId = Integer.parseInt(queryMap.get("user"));
            allUsers = queryMap.get("allUsers");
        }



        if (allUsers == "on"){
            for (Question question : questionDao.retrieveFromSurveyId(surveyId)) {
                responseText += "<h3>" + question.getTitle() + "</h3>\r\n";
                for (Answer answerByQuestionId : answerDao.retrieveFromQuestionId(question.getId())) {
                    if (answerByQuestionId.getUserId() == userId) {
                        responseText += "<p>" + answerByQuestionId.getAnswer() + "</p>\r\n";
                    }
                }

            }
        }else{
            for (Question question : questionDao.retrieveFromSurveyId(surveyId)) {
                responseText += "<h3>" + question.getTitle() + "</h3>\r\n";
                for (Answer allAnswers : answerDao.retrieveFromQuestionId(question.getId())) {
                    responseText += "<p>" + allAnswers.getAnswer() + "</p>\r\n";
                }
            }
        }




        return new HttpMessage("HTTP/1.1 200", responseText);
    }


    /*
       //Sjekker om det er Admin som er bruker - i så fall printer ut alle answers for valgt survey
            if (userId == 1) {
                for (Answer allAnswers : answerDao.retrieveFromQuestionId(question.getId())) {
                    responseText += "<p>" + allAnswers.getAnswer() + "</p>\r\n";
                }
            }
     */
}
