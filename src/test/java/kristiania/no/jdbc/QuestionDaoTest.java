package kristiania.no.jdbc;

import kristiania.no.jdbc.question.Question;
import kristiania.no.jdbc.question.QuestionDao;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class QuestionDaoTest {
    private QuestionDao dao = new QuestionDao(TestData.testDataSource());

    @Test
    void shouldRetrieveSavedQuestion() throws SQLException {
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
    @Test
    void shouldListAllQuestionsById() throws SQLException {
        Question question = new Question("q1", 1);
        dao.save(question);
        Question question2 = new Question("q2", 2);
        dao.save(question2);

        assertThat(dao.retrieveFromSurveyId(1))
                .extracting(Question::getId)
                .contains(question.getId())
                .doesNotContain(question2.getId());
    }
    @Test
    void shouldAddAndDeleteQuestion() throws SQLException {
        Question question = new Question("Question", 1);
        dao.save(question);
        dao.delete((int) question.id);
        assertThat(dao.listAll()).doesNotContain(question);
    }
}
