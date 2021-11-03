package kristiania.no.jdbc;

public class Question {
    long id;
    String title;
    int surveyId;

    public Question() {
    }

    public Question(String title, int surveyId) {
        this.title = title;
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


    public void setId(long id) {
        this.id = id;
    }
    public long getId() {
        return id;
    }

}
