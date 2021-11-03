package kristiania.no.jdbc;

public class Survey {
    String title;
    long id;

    public Survey(String title) {
        this.title = title;
    }
    public Survey(){
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
