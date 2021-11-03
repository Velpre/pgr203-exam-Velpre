package kristiania.no.jdbc;

public class Answer {
    long id;
    String answer;
    int questionId;
    int userId;

    public Answer(){

    }
    public Answer(String answer, int questionId, int userId) {
        this.answer = answer;
        this.questionId = questionId;
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

    public int getQuestionId() {
        return questionId;
    }

    public void setQuestionId(int questionId) {
        this.questionId = questionId;
    }

    public int userId() {
        return userId;
    }
}
