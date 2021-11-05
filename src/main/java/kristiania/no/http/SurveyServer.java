package kristiania.no.http;

import kristiania.no.http.controllers.*;
import kristiania.no.jdbc.answer.AnswerDao;
import kristiania.no.jdbc.options.OptionDao;
import kristiania.no.jdbc.question.QuestionDao;
import kristiania.no.jdbc.survey.SurveyDao;
import kristiania.no.jdbc.user.UserDao;
import org.flywaydb.core.Flyway;
import org.postgresql.ds.PGSimpleDataSource;

import javax.sql.DataSource;
import java.io.IOException;
import java.nio.file.Paths;

public class SurveyServer {

    private static DataSource createDataSource() {
        PGSimpleDataSource dataSource = new PGSimpleDataSource();
        dataSource.setUrl("jdbc:postgresql://localhost:5432/question_db");
        dataSource.setUser("question_dbuser");
        dataSource.setPassword("sKKA4rPjM6ZQ2eNH8MxQ");
        Flyway.configure().dataSource(dataSource).load().migrate();
        return dataSource;
    }


    public static void main(String[] args) throws IOException {
        DataSource dataSource = createDataSource();
        QuestionDao questionDao =  new QuestionDao(dataSource);
        SurveyDao surveyDao =  new SurveyDao(dataSource);
        AnswerDao answerDao = new AnswerDao(dataSource);
        UserDao userDao = new UserDao(dataSource);
        OptionDao optionDao = new OptionDao(dataSource);

        HttpServer httpServer = new HttpServer(8012);
        httpServer.addController("/api/listQuestions", new ListQuestionsController(questionDao,optionDao));
        httpServer.addController("/api/listSurveyOptions", new ListSurveyOptionsController(surveyDao));
        httpServer.addController("/api/answerQuestions", new AnswerQuestionsController(answerDao,userDao));
        httpServer.addController("/api/newQuestion", new NewQuestionController(questionDao, optionDao));
        httpServer.addController("/api/newSurvey", new NewSurveyController(surveyDao));
        httpServer.addController("/api/deleteSurvey", new DeleteSurveyController(surveyDao));

        httpServer.setRoot(Paths.get("src/main/resources/webfiles"));

        System.out.println("Server running at: http://localhost:"+ httpServer.getPort() + "/");
    }
}
