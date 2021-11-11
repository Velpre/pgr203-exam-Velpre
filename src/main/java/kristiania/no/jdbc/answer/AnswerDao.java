package kristiania.no.jdbc.answer;

import kristiania.no.jdbc.AbstractDao;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
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
        answer.setQuestionId(rs.getInt("question_id"));
        answer.setUserId(rs.getInt("user_id"));
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
        List<Answer> answer = retrieve(id, "select * from answers where id = ?");
        return answer.get(0);
    }



    public List<Answer> listAll() throws SQLException {
        return listAll("select * from answers");
    }



    //Denne må testes og byttes til Abstract DAO methoden

    public List<Answer> retrieveFromQuestionId(long id) throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("select * from answers where question_id = ?")) {
                statement.setLong(1, id);

                try (ResultSet rs = statement.executeQuery()) {
                    ArrayList<Answer> result = new ArrayList<>();
                    while (rs.next()) {
                        result.add(mapFromResultSet(rs));
                    }
                    return result;
                }
            }
        }
    }
}
