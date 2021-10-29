package kristiania.no.http;

import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class HttpClientTest {
    @Test
    void shouldReturnStatusCode() throws IOException {
        assertEquals(200, new HttpClient("httpbin.org", 80, "/html").getStatusCode());
    }

    @Test
    void shouldGet404ResponseCode() throws IOException {
        HttpClient client1= new HttpClient("httpbin.org", 80, "/bla-bla");
        assertEquals(404, client1.getStatusCode());
    }

    @Test
    void shouldReturnHeaders() throws IOException {
        HttpClient client = new HttpClient("httpbin.org", 80, "/html");
        assertEquals("text/html; charset=utf-8", client.getHeader("Content-Type"));
    }

    @Test
    void shouldReadContentLength() throws IOException {
        HttpClient client = new HttpClient("httpbin.org", 80, "/html");
        assertEquals(3741, client.getContentLength());
    }
    @Test
    void shouldReadResponseHeaders() throws IOException {
        HttpClient client = new HttpClient("httpbin.org", 80, "/html");
        assertEquals("text/html; charset=utf-8", client.getHeader("Content-Type"));
    }

    @Test
    void shouldReadBody() throws IOException {
        HttpClient client = new HttpClient("httpbin.org", 80, "/html");
        assertTrue(client.getMessageBody().startsWith("<!DOCTYPE html>"));
    }
}
