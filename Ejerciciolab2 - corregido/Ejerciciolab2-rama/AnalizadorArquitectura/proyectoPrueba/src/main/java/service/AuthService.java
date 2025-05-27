package service;

import dao.UserRepository; // ✅ Permitido: Service puede acceder a DAO
import ui.UserView;  // ❌ Service no debe depender de UI

public class AuthService {
    private UserRepository userRepository;

    public AuthService() {
        this.userRepository = new UserRepository();
    }

    public boolean autenticar(String usuario, String contraseña) {
        return userRepository.validarCredenciales(usuario, contraseña);
    }
}