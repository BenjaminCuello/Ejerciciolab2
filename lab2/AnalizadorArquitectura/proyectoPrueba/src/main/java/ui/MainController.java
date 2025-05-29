package ui;

import service.UserService;
import ui.LoginController; // violacion de dependencias cruzadas (misma capa)

public class MainController {
    private final UserService userService = new UserService();

    public void run() {
        userService.registerUser("Alice");
    }
}