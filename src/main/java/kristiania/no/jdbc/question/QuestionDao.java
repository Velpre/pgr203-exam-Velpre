package kristiania.no.jdbc.question;

import kristiania.no.jdbc.AbstractDao;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class QuestionDao extends AbstractDao {
    public QuestionDao(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    protected void setStatement(Object generic, PreparedStatement statement) throws SQLException {
        Question question = (Question) generic;
        statement.setString(1, question.getTitle());
        statement.setLong(2, question.getSurveyId());
        statement.executeUpdate();
    }

    @Override
    protected Question mapFromResultSet(ResultSet rs) throws SQLException {
        Question question = new Question();
        question.setId(rs.getLong("id"));
        question.setTitle(rs.getString("title"));
        question.setSurveyId(rs.getLong("survey_id"));
        return question;
    }

    public void save(Question question) throws SQLException {
        question.setId(save(question, "insert into questions (title, survey_id) values (?, ?)"));
    }

    public List<Question> listAll() throws SQLException {
        return listAll("select * from questions");
    }

    public List<Question> retrieveFromSurveyId(long id) throws SQLException {
        return retrieve(id, "select * from questions where survey_id = ?");
    }


    public void delete(long id) throws SQLException {
        delete(id, "delete from questions where id = ?");
    }

    public void update(String name, long id) throws SQLException {
        update(name, id, "update questions set title = ? where id = ? ");
    }


}
