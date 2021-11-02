package kristiania.no.jdbc;

import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;

public class QuestionDaoTest {
    private QuestionDao dao = new QuestionDao(TestData.testDataSource());

    @Test
    void shouldRetrieveSavedPerson() throws SQLException {
        Question question = new Question("q1", 1);
        dao.save(question);
        assertThat(dao.retrieve(question.getId()))
                .hasNoNullFieldsOrProperties()
                .usingRecursiveComparison()
                .isEqualTo(question);
    }
    @Test
    void shouldListAllQuestions() throws SQLException {
        Question question = new Question("q1", 1);
        dao.save(question);
        Question question2 = new Question("q2", 2);
        dao.save(question2);

        assertThat(dao.listAll())
                .extracting(Question::getId)
                .contains(question.getId(), question2.getId());
    }

}
