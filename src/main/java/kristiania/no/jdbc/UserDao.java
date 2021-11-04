package kristiania.no.jdbc;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDao {

    private final DataSource dataSource;

    public UserDao(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void save(User user) throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(
                    "insert into users (username) values (?)",
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
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("select * from users where id = ?")) {
                statement.setLong(1, id);

                try (ResultSet rs = statement.executeQuery()) {
                    rs.next();

                    return readFromResultSet(rs);
                }
            }
        }
    }

    private User readFromResultSet(ResultSet rs) throws SQLException {
        User user  = new User();
        user.setId(rs.getLong("id"));
        user.setUserName(rs.getString("username"));
        return user;
    }
    public List<User> listAll() throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("select * from users")) {
                try (ResultSet rs = statement.executeQuery()) {
                    ArrayList<User> result = new ArrayList<>();
                    while (rs.next()) {
                        result.add(readFromResultSet(rs));
                    }
                    return result;
                }
            }
        }
    }
    public void delete(int id) throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(
                    "delete from users where id = ?"
            )) {
                statement.setLong(1, id);

                statement.executeUpdate();
            }
        }
    }
}
