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
    void shouldRetrieveSavedAnswersFromQuestionId() throws SQLException, UnsupportedEncodingException {
        Answer answer = new Answer("TestAnswer", 1, 1);
        dao.save(answer);
        assertThat(dao.retrieveFromQuestionId(answer.getQuestionId()))
                .extracting(Answer::getId)
                .contains(answer.getId());
    }

}