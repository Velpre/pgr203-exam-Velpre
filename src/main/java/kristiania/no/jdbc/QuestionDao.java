package kristiania.no.jdbc;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class QuestionDao {
    private final DataSource dataSource;
    public QuestionDao(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void save(Question question) throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(
                    "insert into questions (question, survey_id) values (?, ?)",
                    Statement.RETURN_GENERATED_KEYS

            )) {
                statement.setString(1, question.getTitle());
                statement.setInt(2, question.getSurveyId());

                statement.executeUpdate();

                try (ResultSet rs = statement.getGeneratedKeys()) {
                    rs.next();
                    question.setId(rs.getLong("id"));
                }
            }
        }
    }

    //fjerne denne?
    public Question retrieve(long id) throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("select * from questions where id = ?")) {
                statement.setLong(1, id);

                try (ResultSet rs = statement.executeQuery()) {
                    rs.next();

                    return readFromResultSet(rs);
                }
            }
        }
    }

    //teste denne
    public List<Question> retrieveFromSurveyId(long id) throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("select * from questions where survey_id = ?")) {
                statement.setLong(1, id);

                try (ResultSet rs = statement.executeQuery()) {
                    ArrayList<Question> result = new ArrayList<>();
                    while (rs.next()) {
                        result.add(readFromResultSet(rs));
                    }
                    return result;
                }
            }
        }
    }

    private Question readFromResultSet(ResultSet rs) throws SQLException {
        Question question = new Question();
        question.setId(rs.getLong("id"));
        question.setTitle(rs.getString("question"));
        question.setSurveyId(rs.getInt("survey_id"));
        return question;
    }

    public List<Question> listAll() throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("select * from questions")) {
                try (ResultSet rs = statement.executeQuery()) {
                    ArrayList<Question> result = new ArrayList<>();
                    while (rs.next()) {
                        result.add(readFromResultSet(rs));
                    }
                    return result;
                }
            }
        }
    }
    //Teste denne
    public void delete(int id) throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(
                    "delete from questions where id = ?"
            )) {
                statement.setLong(1, id);

                statement.executeUpdate();
            }
        }
    }
}
