package kristiania.no.jdbc.options;

public class Option {
    long id;
    String optionName;
    long questionId;

    public Option() {
    }

    public Option(String optionName, long questionId) {
        this.optionName = optionName;
        this.questionId = questionId;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getQuestionId() {
        return questionId;
    }

    public void setQuestionId(long questionId) {
        this.questionId = questionId;
    }

    public String getOptionName() {
        return optionName;
    }

    public void setOptionName(String optionName) {
        this.optionName = optionName;
    }
}
