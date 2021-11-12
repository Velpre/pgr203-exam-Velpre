package kristiania.no.http;

import kristiania.no.http.controllers.*;
import kristiania.no.jdbc.answer.AnswerDao;
import kristiania.no.jdbc.options.OptionDao;
import kristiania.no.jdbc.question.QuestionDao;
import kristiania.no.jdbc.survey.SurveyDao;
import kristiania.no.jdbc.user.UserDao;
import org.flywaydb.core.Flyway;
import org.postgresql.ds.PGSimpleDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;


public class SurveyServer {


    private static final Logger logger = LoggerFactory.getLogger(HttpServer.class);




    private static DataSource createDataSource() throws IOException {
        Properties properties = new Properties();
        try (FileReader reader = new FileReader("pgr203.properties")) {
            properties.load(reader);
        }
        PGSimpleDataSource dataSource = new PGSimpleDataSource();
        dataSource.setUrl(properties.getProperty("dataSource.url"));
        dataSource.setUser(properties.getProperty("dataSource.username"));
        dataSource.setPassword(properties.getProperty("dataSource.password"));
        Flyway.configure().dataSource(dataSource).load().migrate();
        return dataSource;
    }

    public static void main(String[] args) throws IOException {
        DataSource dataSource = createDataSource();
        QuestionDao questionDao = new QuestionDao(dataSource);
        SurveyDao surveyDao = new SurveyDao(dataSource);
        AnswerDao answerDao = new AnswerDao(dataSource);
        UserDao userDao = new UserDao(dataSource);

        OptionDao optionDao = new OptionDao(dataSource);

        HttpServer httpServer = new HttpServer(8080);
        httpServer.addController(ListQuestionsController.PATH, new ListQuestionsController(questionDao, optionDao));
        httpServer.addController(AddAndListSurveyController.PATH, new AddAndListSurveyController(surveyDao));
        httpServer.addController(AnswerQuestionsController.PATH, new AnswerQuestionsController(answerDao, userDao));
        httpServer.addController(AddAndListAllQuestionsController.PATH, new AddAndListAllQuestionsController(questionDao, optionDao));
        httpServer.addController(DeleteSurveyController.PATH, new DeleteSurveyController(surveyDao));
        httpServer.addController(ListUsersController.PATH, new ListUsersController(userDao));
        httpServer.addController(ListAnswersController.PATH, new ListAnswersController(questionDao, answerDao));
        httpServer.addController(ChangeQuestionController.PATH, new ChangeQuestionController(questionDao, optionDao));


        logger.info("Server running at http://localhost:" + httpServer.getPort());
    }


}
