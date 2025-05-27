package verificador.arquitectura.tipos;

// Representa el tipo de capa de persistencia como DAO
public class TipoCapaPersistencia implements IdentificadorTipoCapa {
    @Override
    public String getNombreTipo() {
        return "Persistencia";
    }
}