package kristiania.no.jdbc.options;

public class Option {
    long id;
    String optionName;
    int questionId;

    public Option() {
    }

    public Option(long id, String optionName, int questionId) {
        this.id = id;
        this.optionName = optionName;
        this.questionId = questionId;
    }

    public long getId() {
        return id;
    }

    public int getQuestionId() {
        return questionId;
    }

    public void setQuestionId(int questionId) {
        this.questionId = questionId;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getOptionName() {
        return optionName;
    }

    public void setOptionName(String optionName) {
        this.optionName = optionName;
    }
}
