package kristiania.no.jdbc.question;

public class Question {
    long id;
    String title;
    long surveyId;

    public Question() {
    }

    public Question(String title, long surveyId) {
        this.title = title;
        this.surveyId = surveyId;
    }


    public long getSurveyId() {
        return surveyId;
    }

    public void setSurveyId(long surveyId) {
        this.surveyId = surveyId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

}
