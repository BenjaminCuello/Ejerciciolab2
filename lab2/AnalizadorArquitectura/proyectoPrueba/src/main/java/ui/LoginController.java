package ui;

import service.AuthService;
//import ui.MainController;

public class LoginController {
    private final AuthService authService = new AuthService();

    public boolean login(String username, String password) {
        return authService.authenticate(username, password);
    }
}