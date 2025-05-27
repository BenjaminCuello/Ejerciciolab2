package verificador.arquitectura.tipos;

// Representa el tipo de capa de servicio como logica de negocio
public class TipoCapaServicio implements IdentificadorTipoCapa {
    @Override
    public String getNombreTipo() {
        return "Servicio";
    }
}