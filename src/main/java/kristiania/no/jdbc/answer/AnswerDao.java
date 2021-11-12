package kristiania.no.jdbc.answer;

import kristiania.no.jdbc.AbstractDao;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class AnswerDao extends AbstractDao {
    public AnswerDao(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    protected void setStatement(Object generic, PreparedStatement statement) throws SQLException {
        Answer answer = (Answer) generic;
        statement.setString(1, answer.getAnswer());
        statement.setLong(2, answer.getQuestionId());
        statement.setLong(3, answer.userId());
        statement.executeUpdate();
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

// Må også rette opp retrive metoder med å retunere null hvis det ikke finnes rs.next()

    public void save(Answer answer) throws SQLException {
        answer.setId(save(answer, "insert into answers (answer, question_id, user_id) values (?, ?, ?)"));
    }

    //Denne må testes og byttes til Abstract DAO methoden
    public List<Answer> retrieveFromQuestionId(long id) throws SQLException {
        return (List<Answer>) retrieve(id, "select * from answers where question_id = ?");
    }


}
