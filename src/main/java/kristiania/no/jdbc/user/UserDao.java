package kristiania.no.jdbc.user;

import kristiania.no.jdbc.AbstractDao;

import javax.sql.DataSource;
import java.sql.*;
import java.util.List;

public class UserDao extends AbstractDao {
    public UserDao(DataSource dataSource) {
        super(dataSource);
    }


    public void save(User user) throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(
                    "insert into users (user_name) values (?)",
                    Statement.RETURN_GENERATED_KEYS
            )) {
                statement.setString(1, user.getUserName());
                statement.executeUpdate();
                try (ResultSet rs = statement.getGeneratedKeys()) {
                    rs.next();
                    user.setId(rs.getLong("id"));
                }
            }
        }
    }

    public User retrieve(long id) throws SQLException {
        List<User> user = retrieve(id, "select * from users where id = ?");
        return user.get(0);
    }

    public List<User> listAll() throws SQLException {
        return listAll("select * from users");
    }

    public void delete(long id) throws SQLException {
        delete(id, "delete from users where id = ?");
    }

    @Override
    protected User mapFromResultSet(ResultSet rs) throws SQLException {
        User user = new User();
        user.setId(rs.getLong("id"));
        user.setUserName(rs.getString("user_name"));
        return user;
    }


}
