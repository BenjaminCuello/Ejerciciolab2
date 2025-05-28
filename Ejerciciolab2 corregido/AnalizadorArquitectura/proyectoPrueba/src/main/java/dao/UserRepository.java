package dao;

public class UserRepository {

    public void save(String name) {
        System.out.println("User " + name + " saved.");
    }

    public String find(String user) {
        return "secret";
    }
}