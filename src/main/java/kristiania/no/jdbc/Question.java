package kristiania.no.jdbc;

public class Question {
    long id;
    String title;
    String questionText;
    int surveyId;

    public Question() {
    }

    public Question(String title, String questionText, int surveyId) {
        this.title = title;
        this.questionText = questionText;
        this.surveyId = surveyId;
    }



    public int getSurveyId() {
        return surveyId;
    }

    public void setSurveyId(int surveyId) {
        this.surveyId = surveyId;
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
    public long getId() {
        return id;
    }
}
