package kristiania.no.jdbc;

public class Question {
    long id;
    String title;
    String questionText;
    String category;

    public Question() {
    }

    public Question(String title, String questionText, String category) {
        this.title = title;
        this.questionText = questionText;
        this.category = category;
    }

    public long getId() {
        return id;
    }

    public String getCategory() {
        return category;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getQuestionText() {
        return questionText;
    }

    public void setQuestionText(String questionText) {
        this.questionText = questionText;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
