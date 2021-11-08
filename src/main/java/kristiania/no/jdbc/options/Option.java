package kristiania.no.jdbc.options;

public class Option {
    long id;
    String optionName;
    int questionId;

    public Option() {
    }

    public Option(String optionName, int questionId) {
        this.optionName = optionName;
        this.questionId = questionId;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getQuestionId() {
        return questionId;
    }

    public void setQuestionId(int questionId) {
        this.questionId = questionId;
    }

    public String getOptionName() {
        return optionName;
    }

    public void setOptionName(String optionName) {
        this.optionName = optionName;
    }
}
