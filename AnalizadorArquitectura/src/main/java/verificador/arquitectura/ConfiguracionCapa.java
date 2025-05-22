package verificador.arquitectura;

import java.util.ArrayList;
import java.util.List;

// Esta clase representa la configuracion para una capa especifica
// de nuestra arquitectura. Contiene el nombre de la capa, su tipo,
// y los paquetes que le pertenecen.
public class ConfiguracionCapa {

    private String nombreCapa;
    // Cambiamos el tipo de String a nuestra nueva interfaz
    private IdentificadorTipoCapa identificadorTipo;
    private List<String> paquetesAsociados;

    // Se necesitan el nombre de la capa, su tipo y una lista de los paquetes
    // que pertenecen a esta capa.
    public ConfiguracionCapa(String nombreCapa, IdentificadorTipoCapa identificadorTipo, List<String> paquetesAsociados) {
        this.nombreCapa = nombreCapa;
        this.identificadorTipo = identificadorTipo;
        this.paquetesAsociados = new ArrayList<>(paquetesAsociados);
    }

    public String getNombreCapa() {
        return nombreCapa;
    }


    public IdentificadorTipoCapa getIdentificadorTipo() {
        return identificadorTipo;
    }

    // Si necesitamos el String del tipo (por ejemplo, para imprimirlo o compararlo),
    // podemos obtenerlo del identificador.
    public String getNombreDelTipoDeCapa() {
        return identificadorTipo.getNombreTipo();
    }

    public List<String> getPaquetesAsociados() {
        return new ArrayList<>(paquetesAsociados);
    }
    // Metodo para verificar si un nombre de paquete pertenece a esta capa.
    // Por ejemplo, si la capa de presentacion tiene el paquete "ui",
    // y le preguntamos estaCapa.contienePaquete("ui"), deberia devolver true.
    public boolean contienePaquete(String nombrePaquete) {
        return paquetesAsociados.contains(nombrePaquete);
    }
}