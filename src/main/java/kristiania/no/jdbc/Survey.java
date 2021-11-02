package kristiania.no.jdbc;

public class Survey {
    long id;
    private String surveyName;

    public Survey(String surveyName) {
        this.surveyName = surveyName;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return surveyName;
    }

    public void setName(String name) {
        this.surveyName = name;
    }
}
