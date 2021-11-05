package kristiania.no.jdbc.answer;

import kristiania.no.jdbc.AbstractDao;

import javax.sql.DataSource;
import java.sql.*;
import java.util.List;

public class AnswerDao extends AbstractDao {
    public AnswerDao(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    protected Answer mapFromResultSet(ResultSet rs) throws SQLException {
        Answer answer  = new Answer();
        answer.setId(rs.getLong("id"));
        answer.setAnswer(rs.getString("answer"));
        answer.setQuestionId(rs.getInt("question_id"));
        answer.setUserId(rs.getInt("user_id"));
        return answer;
    }

    public void save(Answer answer) throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(
                    "insert into answers (answer, question_id, user_id) values (?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS

            )) {
                statement.setString(1, answer.getAnswer());
                statement.setInt(2, answer.getQuestionId());
                statement.setInt(3, answer.userId());

                statement.executeUpdate();

                try (ResultSet rs = statement.getGeneratedKeys()) {
                    rs.next();
                    answer.setId(rs.getLong("id"));
                }
            }
        }
    }

    public Answer retrieve(long id) throws SQLException {
       return (Answer) retrieve(id, "select * from answers where id = ?");
    }


    public List<Answer> listAll() throws SQLException {
        return listAll("select * from answers");
    }

    public void delete(int id) throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(
                    "delete from answers where id = ?"
            )) {
                statement.setLong(1, id);

                statement.executeUpdate();
            }
        }
    }

}
