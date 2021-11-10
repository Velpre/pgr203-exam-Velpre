package kristiania.no.http;

import kristiania.no.http.controllers.*;
import kristiania.no.jdbc.TestData;
import kristiania.no.jdbc.answer.AnswerDao;
import kristiania.no.jdbc.options.OptionDao;
import kristiania.no.jdbc.question.Question;
import kristiania.no.jdbc.question.QuestionDao;
import kristiania.no.jdbc.survey.SurveyDao;
import kristiania.no.jdbc.user.UserDao;
import org.junit.jupiter.api.Test;

import javax.sql.DataSource;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class HttpServerTest {
    HttpServer server = new HttpServer(0);

    public HttpServerTest() throws IOException {
        DataSource dataSource = TestData.testDataSource();
        QuestionDao questionDao = new QuestionDao(dataSource);
        SurveyDao surveyDao = new SurveyDao(dataSource);
        AnswerDao answerDao = new AnswerDao(dataSource);
        UserDao userDao = new UserDao(dataSource);
        OptionDao optionDao = new OptionDao(dataSource);

        server.addController("/api/listQuestions", new ListQuestionsController(questionDao, optionDao));
        server.addController("/api/listSurveyOptions", new ListSurveyOptionsController(surveyDao));
        server.addController("/api/answerQuestions", new AnswerQuestionsController(answerDao, userDao));
        server.addController("/api/newQuestion", new NewQuestionController(questionDao, optionDao));
        server.addController("/api/newSurvey", new NewSurveyController(surveyDao));
        server.addController("/api/deleteSurvey", new DeleteSurveyController(surveyDao));
        server.addController("/api/listUsers", new ListUsersController(userDao));
        server.addController("/api/listAnswers", new ListAnswersController(questionDao, answerDao));
        server.addController("/api/changeQuestion", new ChangeQuestionController(questionDao, optionDao));
        server.addController("/api/listAllQuestions", new ListAllQuestionsController(questionDao));

    }

    @Test
    void shouldReturn404ForUnknowRequestTarget() throws IOException {
        HttpClient client = new HttpClient("localhost", server.getPort(), "/non-existing");
        assertEquals(404, client.getStatusCode());
    }

    @Test
    void shouldRespondWithRequestTargetIn404() throws IOException {
        HttpClient client = new HttpClient("localhost", server.getPort(), "/non-existing");
        assertEquals("File not found: /non-existing", client.getMessageBody());
    }

    @Test
    void shouldRespondWith200ForKnownRequestTarget() throws IOException {
        HttpPostClient postClient = new HttpPostClient("localhost", server.getPort(), "/api/answerQuestions", "newUser=test");
        HttpClient client = new HttpClient("localhost", server.getPort(), "/api/answerQuestions?newUser=test");
        assertAll(
                () -> assertEquals(200, client.getStatusCode()),
                () -> assertEquals("text/html; charset=utf-8", client.getHeader("Content-Type")),
                () -> assertEquals("You have answered:  Questions are answered with user test", client.getMessageBody())
        );
    }

    @Test
    void shouldServeFiles() throws IOException {
        server.setRoot(Paths.get("target/test-classes"));

        String fileContent = "Det funker";
        Files.write(Paths.get("target/test-classes/test.txt"), fileContent.getBytes());

        HttpClient client = new HttpClient("localhost", server.getPort(), "/test.txt");
        assertEquals(fileContent, client.getMessageBody());
    }

    //Sjekk om nødvendig
    @Test
    void shouldUseFileExtensionForContentType() throws IOException {
        server.setRoot(Paths.get("target/test-classes"));
        String fileContent = "<p>Hello</p>";
        Files.write(Paths.get("target/test-classes/example-file.html"), fileContent.getBytes());

        HttpClient client = new HttpClient("localhost", server.getPort(), "/example-file.html");
        assertEquals("text/html", client.getHeader("Content-Type"));
    }

    @Test
    void shouldHandelMoreThanOneRequest() throws IOException {
        assertEquals(200, new HttpClient("localhost", server.getPort(), "/api/listAllQuestions").getStatusCode());
        assertEquals(200, new HttpClient("localhost", server.getPort(), "/api/listAllQuestions").getStatusCode());
    }


    @Test
    void shouldEchoMoreThanOneQueryParameter() throws IOException {
        HttpPostClient postClient = new HttpPostClient("localhost", server.getPort(), "/api/newQuestion",
                "title=test&survey=1&option1=test1&option2=test1&option3=test1&option4=test1&option5=test1");
        HttpClient client = new HttpClient("localhost", server.getPort(), "/api/newQuestion");

        assertEquals("You have added: Question: test Survey: 1 Options:test1 test1 test1 test1 test1.", client.getMessageBody());
    }



    //Testen tester helt feil ting denne må rettes
    @Test
    void shouldReturnQuestionsFromServer() throws IOException, SQLException {
        QuestionDao questionDao = new QuestionDao(TestData.testDataSource());
        OptionDao optionDao = new OptionDao(TestData.testDataSource());
        server.addController("/api/listQuestions", new ListQuestionsController(questionDao, optionDao));


        //question1 & question2 objekt blir lagt til i DB gjennom V006 migrering
        HttpClient client = new HttpClient("localhost", server.getPort(), "/api/listQuestions");
        assertEquals(
                "<p>Create New user:</p><input type=\"text\" id=\"userName\" name=\"newUser\" label =\"Username:\"> </input><br><p>Chose one of existing users<p><p><label>Select user <select name=\"existingUsers\" id=\"existingUsers\"></select></label></p><br><button>Answer</button>"
                ,
                client.getMessageBody()
        );
    }


    @Test
    void shouldReturnOptionsFromServer() throws IOException, SQLException {
        SurveyDao surveyDao = new SurveyDao(TestData.testDataSource());
        server.addController("/api/listSurveyOptions", new ListSurveyOptionsController(surveyDao));
        //survey1 & survey2 objekt blir lagt til i DB gjennom V004 migrering
        HttpClient client = new HttpClient("localhost", server.getPort(), "/api/listSurveyOptions");
        assertEquals(
                "<option value=1>Client Questionnaire</option>" +
                        "<option value=2>Test Questionnaire</option>"
                ,

                client.getMessageBody()
        );
    }


    @Test
    void shouldAddQuestions() throws IOException, SQLException {
        DataSource dataSource = TestData.testDataSource();
        QuestionDao questionDao = new QuestionDao(dataSource);
        OptionDao optionDao = new OptionDao(dataSource);

        server.addController("/api/newQuestion", new NewQuestionController(questionDao, optionDao));

        HttpPostClient postClient = new HttpPostClient("localhost", server.getPort(),
                "/api/newQuestion",
                "title=title1&questionText=text1&survey" +
                        "=1&option1=o1&option2=o2&option3=o3&option4=o4&option5=o5");
        assertEquals(200, postClient.getStatusCode());
        List<Question> questionList = questionDao.listAll();

        assertThat(questionList)
                .extracting(Question::getId)
                .contains(1L);

    }

}
