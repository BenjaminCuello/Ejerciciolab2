package ui;

import service.UserService;
import dao.ConnectionManager; //violacion de capas no permitidas

public class UserView {
    private final UserService service = new UserService();
    //private final ConnectionManager Manager = new ConnectionManager();

    public void showUser(String name) {
        service.getUserInfo(name);
    }
}