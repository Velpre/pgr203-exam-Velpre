package kristiania.no.jdbc.answer;

public class Answer {
    long id;
    String answer;
    long questionId;
    long userId;

    public Answer() {
    }

    public Answer(String answer, long questionId, long userId) {
        this.answer = answer;
        this.questionId = questionId;
        this.userId = userId;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public long getQuestionId() {
        return questionId;
    }

    public void setQuestionId(long questionId) {
        this.questionId = questionId;
    }

    public long userId() {
        return userId;
    }
}
