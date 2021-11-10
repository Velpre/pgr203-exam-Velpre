package kristiania.no.jdbc.question;

import kristiania.no.jdbc.AbstractDao;

import javax.sql.DataSource;
import java.sql.*;
import java.util.List;

public class QuestionDao extends AbstractDao {
    public QuestionDao(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    protected Question mapFromResultSet(ResultSet rs) throws SQLException {
        Question question = new Question();
        question.setId(rs.getLong("id"));
        question.setTitle(rs.getString("title"));
        question.setSurveyId(rs.getInt("survey_id"));
        return question;
    }

    public void save(Question question) throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(
                    "insert into questions (title, survey_id) values (?, ?)",
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

    public List<Question> listAll() throws SQLException {
        return listAll("select * from questions");
    }

    public Question retrieve(long id) throws SQLException {
        return (Question) retrieveById(id, "select * from questions where id = ?");
    }

    public void delete(int id) throws SQLException {
        delete(id, "delete from questions where id = ?");
    }

    public List<Question> retriveFromParentId(long id) throws SQLException {
        return retrieveFromParentId(id, "select * from questions where survey_id = ?");
    }


    public void updateQuestion(String name, long id) throws SQLException {
        updateQuestion(name, id, "update questions set title = ? where id = ? ");
    }


}
