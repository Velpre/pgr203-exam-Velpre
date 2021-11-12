package kristiania.no.jdbc.options;

import kristiania.no.jdbc.AbstractDao;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class OptionDao extends AbstractDao {
    public OptionDao(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    protected void setStatement(Object generic, PreparedStatement statement) throws SQLException {
        Option option = (Option) generic;
        statement.setString(1, option.getOptionName());
        statement.setLong(2, option.getQuestionId());
        statement.executeUpdate();
    }


    @Override
    protected Option mapFromResultSet(ResultSet rs) throws SQLException {
        Option options = new Option();
        options.setId(rs.getLong("id"));
        options.setOptionName(rs.getString("option_name"));
        options.setQuestionId(rs.getLong("question_id"));

        return options;
    }


    public void save(Option option) throws SQLException {
        option.setId(save(option, "insert into options (option_name, question_id) values (?,?)"));
    }


    public List<Option> retrieveFromQuestionId(long id) throws SQLException {
        return (List<Option>) retrieve(id, "select * from options where question_id = ?");
    }

    //Denne må testes
    public void delete(long id) throws SQLException {
        delete(id, "delete from options where id = ?");
    }

    //teste denne
    public void update(String name, long id) throws SQLException {
        update(name, id, "update options set option_name = ? where id = ?");
    }

    //Teste denne
    public List<Option> listAll() throws SQLException {
        return listAll("select * from options");
    }
}
