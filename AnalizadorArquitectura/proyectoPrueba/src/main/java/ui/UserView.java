package ui;

public class UserView {
    public void mostrar() {
        dao.UserRepository repo = new dao.UserRepository(); // ❌ Uso directo sin import, NO LO DETECTA SIN IMPORT
        boolean ok = repo.validar("admin", "1234");
        System.out.println("Acceso: " + (ok ? "Permitido" : "Denegado"));
    }
}