package kristiania.no.jdbc;

import kristiania.no.jdbc.answer.Answer;
import kristiania.no.jdbc.answer.AnswerDao;
import org.junit.jupiter.api.Test;

import java.io.UnsupportedEncodingException;
import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;

public class AnswerDaoTest {
    private final AnswerDao dao = new AnswerDao(TestData.testDataSource());

    @Test
    void shouldRetrieveSavedQuestion() throws SQLException, UnsupportedEncodingException {
        Answer answer = new Answer("TestAnswer", 1, 1);
        dao.save(answer);
        assertThat(dao.retrieve(answer.getId()))
                .hasNoNullFieldsOrProperties()
                .usingRecursiveComparison()
                .isEqualTo(answer);
    }

    @Test
    void shouldListAllQuestions() throws SQLException, UnsupportedEncodingException {
        Answer answer1 = new Answer("TestAnswer", 1, 1);
        dao.save(answer1);
        Answer answer2 = new Answer("TestAnswer2", 1, 1);
        dao.save(answer2);

        assertThat(dao.listAll())
                .extracting(Answer::getId)
                .contains(answer1.getId(), answer2.getId());
    }

    @Test
    void shouldAddAndDeleteQuestion() throws SQLException, UnsupportedEncodingException {
        Answer answer1 = new Answer("TestAnswer", 1, 1);
        dao.save(answer1);
        dao.delete(answer1.getQuestionId());
        assertThat(dao.listAll()).doesNotContain(answer1);
    }
}