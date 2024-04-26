package models;

public class Auth {
    private final User user;
    private final boolean isAuthenticated;

    public Auth(User user, boolean isAuthenticated) {
        this.user = user;
        this.isAuthenticated = isAuthenticated;
    }

    public User getUser() {
        return user;
    }

    public boolean isAuthenticated() {
        return isAuthenticated;
    }

    @Override
    public String toString() {
        return "Auth{" +
                "user=" + user.toString() +
                ", isAuthenticated=" + isAuthenticated +
                '}';
    }
}