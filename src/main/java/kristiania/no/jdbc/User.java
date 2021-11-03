package kristiania.no.jdbc;

public class User {
    long id;
    String userName;

    public User(String userName) {
        this.userName = userName;
    }

    public User() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
