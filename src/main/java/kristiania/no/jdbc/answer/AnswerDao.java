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
        Answer answer = new Answer();
        answer.setId(rs.getLong("id"));
        answer.setAnswer(rs.getString("answer"));
        answer.setQuestionId(rs.getLong("question_id"));
        answer.setUserId(rs.getLong("user_id"));
        return answer;
    }

// Må også rette opp retrive mothoder med å retunere null hvis det ikke finnes rs.next()


    public void save(Answer answer) throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(
                    "insert into answers (answer, question_id, user_id) values (?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS

            )) {
                statement.setString(1, answer.getAnswer());
                statement.setLong(2, answer.getQuestionId());
                statement.setLong(3, answer.userId());

                statement.executeUpdate();

                try (ResultSet rs = statement.getGeneratedKeys()) {
                    rs.next();
                    answer.setId(rs.getLong("id"));
                }
            }
        }
    }


    //Denne må testes og byttes til Abstract DAO methoden
    public List<Answer> retrieveFromQuestionId(long id) throws SQLException {
        return (List<Answer>) retrieve(id, "select * from answers where question_id = ?");
    }


}
