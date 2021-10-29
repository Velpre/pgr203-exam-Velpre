package kristiania.no.http;

public class Question {
    String title;
    String questionText;
    String category;

    public Question(String title, String questionText, String category) {
        this.title = title;
        this.questionText = questionText;
        this.category = category;

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
}
