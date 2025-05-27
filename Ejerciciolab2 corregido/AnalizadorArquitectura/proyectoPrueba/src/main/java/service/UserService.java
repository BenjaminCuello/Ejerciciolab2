package service;

import ui.UserView;
// ❌ no debiera importar algo de ui

public class UserService {
    public void iniciarVista() {
        UserView vista = new UserView();
        vista.mostrar();
    }
}