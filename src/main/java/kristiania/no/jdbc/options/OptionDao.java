package kristiania.no.jdbc.options;

import kristiania.no.jdbc.AbstractDao;
import kristiania.no.jdbc.answer.Answer;

import javax.sql.DataSource;
import java.sql.*;
import java.util.List;

public class OptionDao extends AbstractDao {
    public OptionDao(DataSource dataSource) {
        super(dataSource);
    }


    @Override
    protected Option mapFromResultSet(ResultSet rs) throws SQLException {
        Option options = new Option();
        options.setId(rs.getLong("id"));
        options.setOptionName(rs.getString("option_name"));
        options.setQuestionId(rs.getInt("question_id"));
        return options;
    }


    public void save(Option option) throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(
                    "insert into options (option_name, question_id) values (?,?)",
                    Statement.RETURN_GENERATED_KEYS

            )) {
                statement.setString(1, option.getOptionName());
                statement.setInt(2, option.getQuestionId());
                if (option.getOptionName() != "") {
                    statement.executeUpdate();
                    try (ResultSet rs = statement.getGeneratedKeys()) {
                        rs.next();
                        option.setId(rs.getLong("id"));
                    }
                }
            }
        }
    }


    public List<Option> retrieveFromQuestionId(long id) throws SQLException {
        return (List<Option>) retrieve(id, "select * from options where question_id = ?");
    }

    //Denne må testes
    public void delete(long id) throws SQLException {
        delete((int) id, "delete from options where id = ?");
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
