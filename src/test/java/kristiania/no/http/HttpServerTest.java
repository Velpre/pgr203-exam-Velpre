package kristiania.no.http;

import kristiania.no.http.controllers.*;
import kristiania.no.jdbc.TestData;
import kristiania.no.jdbc.answer.AnswerDao;
import kristiania.no.jdbc.options.Option;
import kristiania.no.jdbc.options.OptionDao;
import kristiania.no.jdbc.question.Question;
import kristiania.no.jdbc.question.QuestionDao;
import kristiania.no.jdbc.survey.Survey;
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
    DataSource dataSource = TestData.testDataSource();
    QuestionDao questionDao = new QuestionDao(dataSource);
    AnswerDao answerDao = new AnswerDao(dataSource);
    UserDao userDao = new UserDao(dataSource);
    OptionDao optionDao = new OptionDao(dataSource);
    SurveyDao surveyDao = new SurveyDao(dataSource);


    public HttpServerTest() throws IOException {
        server.addController("/api/listAllQuestions", new ListAllQuestionsController(questionDao));
        server.addController("/api/answerQuestions", new AnswerQuestionsController(answerDao, userDao));
        server.addController("/api/newQuestion", new NewQuestionController(questionDao, optionDao));
        server.addController("/api/listQuestions", new ListQuestionsController(questionDao, optionDao));
        server.addController("/api/listSurveyOptions", new ListSurveyOptionsController(surveyDao));


    }

    @Test
    void shouldReturn404ForUnknownRequestTarget() throws IOException {
        HttpClient client = new HttpClient("localhost", server.getPort(), "/non-existing");
        assertEquals(404, client.getStatusCode());
    }

    @Test
    void shouldRespondWithRequestTargetIn404() throws IOException {
        HttpClient client = new HttpClient("localhost", server.getPort(), "/non-existing");
        assertEquals("File not found: /non-existing", client.getMessageBody());
    }

    @Test
    void shouldServeFiles() throws IOException {
        server.setRoot(Paths.get("target/test-classes"));

        String fileContent = "Det funker";
        Files.write(Paths.get("target/test-classes/test.txt"), fileContent.getBytes());

        HttpClient client = new HttpClient("localhost", server.getPort(), "/test.txt");
        assertEquals(fileContent, client.getMessageBody());
    }

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

/*
   @Test
    void shouldEchoMoreThanOneQueryParameter() throws IOException {
        HttpPostClient postClient = new HttpPostClient("localhost", server.getPort(), "/api/newQuestion",
                "title=test&survey=1&option1=test1&option2=test1&option3=test1&option4=test1&option5=test1");
        HttpClient client = new HttpClient("localhost", server.getPort(), "/api/newQuestion");

        assertEquals("You have added: Question: test Survey: 1 Options:test1 test1 test1 test1 test1.", client.getMessageBody());
    }

 */




    @Test
    void shouldRespondWith200ForKnownRequestTarget() throws IOException {
        new HttpPostClient("localhost", server.getPort(), "/api/answerQuestions", "newUser=test");
        HttpClient client = new HttpClient("localhost", server.getPort(), "/api/answerQuestions");
        assertAll(
                () -> assertEquals(200, client.getStatusCode()),
                () -> assertEquals("text/html; charset=utf-8", client.getHeader("Content-Type")),
                () -> assertEquals("Questions answered", client.getMessageBody())
        );
    }



    @Test
    void shouldReturnSurveyFromServer() throws IOException, SQLException {
        Survey survey1 = new Survey("test1");
        Survey survey2 = new Survey("test2");
        surveyDao.save(survey1);
        surveyDao.save(survey2);
        server.addController("/api/listSurveyOptions", new ListSurveyOptionsController(surveyDao));
        //Andre survey objekter blir også lagt til i DB gjennom V006 migrering
        HttpClient client = new HttpClient("localhost", server.getPort(), "/api/listSurveyOptions");
        assertThat(client.getMessageBody()).contains("<option value=" + survey1.getId() + ">test1</option><option value=" + survey2.getId() + ">test2</option>");
    }

  //Trenger vi denne under? har lagt den til

    @Test
    void shouldListAllQuestionsFromServer() throws IOException, SQLException {
        Question question1 = new Question("test1",1);
        Question question2 = new Question("test2",2);
        questionDao.save(question1);
        questionDao.save(question2);
        HttpClient client = new HttpClient("localhost", server.getPort(), "/api/listAllQuestions");
        assertThat(client.getMessageBody()).contains("<option value=" + question1.getId() + ">test1</option><option value=" + question2.getId() + ">test2</option>");
    }


    @Test
    void shouldAddQuestions() throws IOException, SQLException {
        HttpPostClient postClient = new HttpPostClient("localhost", server.getPort(),
                "/api/newQuestion",
                "title=title1&questionText=text1&survey=1&option1=o1&option2=o2&option3=o3&option4=o4&option5=o5");
        assertEquals(303, postClient.getStatusCode());
        List<Question> questionList = questionDao.listAll();

        assertThat(questionList)
                .extracting(Question::getId)
                .contains(1L);

    }

}
