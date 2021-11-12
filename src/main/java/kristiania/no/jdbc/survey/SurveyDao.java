package kristiania.no.jdbc.survey;

import kristiania.no.jdbc.AbstractDao;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class SurveyDao extends AbstractDao {
    public SurveyDao(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    protected void setStatement(Object generic, PreparedStatement statement) throws SQLException {
        Survey survey = (Survey) generic;
        statement.setString(1, survey.getName());
        statement.executeUpdate();
    }

    @Override
    protected Survey mapFromResultSet(ResultSet rs) throws SQLException {
        Survey survey = new Survey();
        survey.setId(rs.getLong("id"));
        survey.setName(rs.getString("survey_name"));
        return survey;
    }


    public void save(Survey survey) throws SQLException {
        survey.setId(save(survey, "insert into survey (survey_name) values (?)"));
    }

    public List<Survey> listAll() throws SQLException {
        return listAll("select * from survey");
    }

    public void delete(long id) throws SQLException {
        delete(id, "delete from survey where id = ?");
    }

}

