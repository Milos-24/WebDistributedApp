package model;

public class FactoryUser {

    private String username, password;

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public FactoryUser(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public FactoryUser()
    {

    }

    @Override
    public String toString() {
        return "FactoryUser{" +
                "username='" + username + '\'' +
                ", password='" + password + '\'' +
                '}';
    }

}
