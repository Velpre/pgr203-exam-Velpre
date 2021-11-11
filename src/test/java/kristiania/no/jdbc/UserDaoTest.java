package kristiania.no.jdbc;

import kristiania.no.jdbc.user.User;
import kristiania.no.jdbc.user.UserDao;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;

public class UserDaoTest {
    private final UserDao dao = new UserDao(TestData.testDataSource());

    @Test
    void shouldRetrieveSavedUser() throws SQLException {
        User user = new User("testUser");
        dao.save(user);
        assertThat(dao.retrieve(user.getId()))
                .hasNoNullFieldsOrProperties()
                .usingRecursiveComparison()
                .isEqualTo(user);
    }

    @Test
    void shouldListSavedUser() throws SQLException {
        User user = new User("testUser");
        User user1 = new User("testUser1");
        dao.save(user);
        dao.save(user1);

        assertThat(dao.listAll())
                .extracting(User::getUserName)
                .contains("testUser", "testUser1");
    }

    @Test
    void shouldAddAndDeleteUser() throws SQLException {
        User user = new User("testUser");
        dao.save(user);
        dao.delete((int) user.getId());
        assertThat(dao.listAll()).doesNotContain(user);
    }
}
