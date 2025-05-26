package ui.menu;
// ❌
import ui.LoginController;  // violacion cruzada en misma capa (Presentación)

public class MainController {
    private LoginController loginController;

    public void iniciar() {
        loginController = new LoginController();
        loginController.iniciarSesion();
    }
}