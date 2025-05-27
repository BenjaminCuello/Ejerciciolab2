package dao;
// ✅
public class UserRepository {
    public boolean validarCredenciales(String usuario, String contraseña) {
        // Simulación de acceso a datos (normalmente accedería a una base de datos)
        return "admin".equals(usuario) && "1234".equals(contraseña);
    }
}