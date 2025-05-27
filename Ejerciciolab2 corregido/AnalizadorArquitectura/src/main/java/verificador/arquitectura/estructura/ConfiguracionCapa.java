package verificador.arquitectura.estructura;

import verificador.arquitectura.tipos.IdentificadorTipoCapa;

import java.util.ArrayList;
import java.util.List;

// Representa una capa como UI Service o DAO con su nombre tipo y paquetes
public class ConfiguracionCapa {

    private String nombreCapa;
    private IdentificadorTipoCapa identificadorTipo;
    private List<String> paquetesAsociados;

    // Constructor que recibe nombre, tipo de capa y lista de paquetes
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


    // Devuelve el nombre del tipo de capa, como Presentacion, Servicio o Persistencia
    public String getNombreDelTipoDeCapa() {
        return identificadorTipo.getNombreTipo();
    }

    public List<String> getPaquetesAsociados() {
        return new ArrayList<>(paquetesAsociados);
    }
    // Verifica si un paquete pertenece a esta capa comparando si empieza igual
    public boolean contienePaquete(String nombrePaquete) {
        return paquetesAsociados.stream().anyMatch(nombrePaquete::startsWith);
    }
}