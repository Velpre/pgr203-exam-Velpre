package kristiania.no.controllers;

import kristiania.no.http.HttpMessage;
import kristiania.no.http.controllers.ListUsersController;
import kristiania.no.jdbc.TestData;
import kristiania.no.jdbc.user.UserDao;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import static org.assertj.core.api.Assertions.assertThat;


public class ListUsersControllerTest {

    @Test
    void shouldListAllUsers() throws SQLException {
        UserDao userDao = new UserDao(TestData.testDataSource());
        HttpMessage httpMessage = new HttpMessage("GET HTTP/1.1 200", "");
        ListUsersController listUsersController = new ListUsersController(userDao);
        HttpMessage response = listUsersController.handle(httpMessage);
        assertThat(response.messageBody).contains("<option value=1>User1</option>");
    }
}
