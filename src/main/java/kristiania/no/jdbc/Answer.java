package kristiania.no.jdbc;

public class Answer {
    long id;
    String answer;
    int questionId;

    public Answer(){

    }
    public Answer(String answer, int questionId) {
        this.answer = answer;
        this.questionId = questionId;
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
}
