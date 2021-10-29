package kristiania.no.http;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class HttpServerTest {
    HttpServer server = new HttpServer(0);

    public HttpServerTest() throws IOException {
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
    void shouldRespondWith200FoKnownRequestTrarget() throws IOException {
        HttpClient client = new HttpClient("localhost", server.getPort(), "/hello");
        assertAll(
                () -> assertEquals(200, client.getStatusCode()),
                () -> assertEquals("text/html; charset=utf-8", client.getHeader("Content-Type")),
                () -> assertEquals("<p>Hello world</p>", client.getMessageBody())
        );
    }
    @Test
    void shouldServeFiles() throws IOException {
        server.setRoot(Paths.get("target/test-classes"));

        String fileContent= "Det funker";
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
        assertEquals(200, new HttpClient("localhost", server.getPort(), "/hello").getStatusCode());
        assertEquals(200, new HttpClient("localhost", server.getPort(), "/hello").getStatusCode());
    }


    @Test
    void shouldEchoMoreThanOneQueryParameter() throws IOException {
        HttpClient client = new HttpClient("localhost", server.getPort(), "/hello?firstName=Veljko&lastName=Premovic");
        assertEquals("<p>Hello Veljko, Premovic</p>", client.getMessageBody());
    }


    @Test
    void shouldReturnQuestionsFromServer() throws IOException {
        Question q1 = new Question("title1", "text1", "1");
        Question q2 = new Question("title2", "text2", "2");


        server.setQuestions(List.of(q1, q2));

        HttpClient client = new HttpClient("localhost", server.getPort(), "/api/questions");
        assertEquals(
                "<p>title1</p><p>title2</p>",
                client.getMessageBody()
        );
    }


    @Test
    void shouldReturnCategoriesFromServer() throws IOException {
        HttpClient client = new HttpClient("localhost", server.getPort(), "/api/categoryOptions");
        assertEquals(
                "<option value=1>1</option>" +
                        "<option value=2>2</option>" +
                        "<option value=3>3</option>"
                ,
                client.getMessageBody()
        );
    }

    @Test
    void shouldAddQuestions() throws IOException {
        HttpPostClient postClient = new HttpPostClient("localhost", server.getPort(),"/api/newQuestion", "title=title1&questionText=text1&category=1");
        assertEquals(200, postClient.getStatusCode());
        Question q = server.getQuestions().get(0);
        assertEquals("title1", q.getTitle());
    }


}
