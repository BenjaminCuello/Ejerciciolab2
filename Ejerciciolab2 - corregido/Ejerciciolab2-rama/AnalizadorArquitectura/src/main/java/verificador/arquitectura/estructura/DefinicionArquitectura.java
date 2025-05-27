package verificador.arquitectura.estructura;

import verificador.arquitectura.tipos.IdentificadorTipoCapa;
import verificador.arquitectura.reglas.ReglaDependenciaPermitida;

import java.util.ArrayList;
import java.util.List;

// Define la arquitectura del sistema con sus capas y reglas de dependencia
public class DefinicionArquitectura {

    private List<ConfiguracionCapa> capasDefinidas;
    private List<ReglaDependenciaPermitida> reglasPermitidas;

    // Inicializa listas vacias de capas y reglas
    public DefinicionArquitectura() {
        this.capasDefinidas = new ArrayList<>();
        this.reglasPermitidas = new ArrayList<>();
    }

    // Agrega una capa a la arquitectura
    public void anadirCapa(ConfiguracionCapa capa) {
        if (capa != null) {
            this.capasDefinidas.add(capa);
        }
    }
    // Devuelve una copia de las capas definidas
    public List<ConfiguracionCapa> getCapasDefinidas() {
        return new ArrayList<>(this.capasDefinidas);
    }
    // Busca a que capa pertenece un paquete
    public ConfiguracionCapa getCapaPorNombrePaquete(String nombrePaquete) {
        for (ConfiguracionCapa capa : this.capasDefinidas) {
            if (capa.contienePaquete(nombrePaquete)) {
                return capa;
            }
        }
        return null;
    }
    // Busca una capa por su nombre exacto
    public ConfiguracionCapa getCapaPorNombre(String nombreCapa) {
        for (ConfiguracionCapa capa : this.capasDefinidas) {
            if (capa.getNombreCapa().equals(nombreCapa)) {
                return capa;
            }
        }
        return null;
    }


    // Agrega una regla de dependencia permitida entre dos tipos de capa
    public void permitirDependencia(IdentificadorTipoCapa tipoOrigen, IdentificadorTipoCapa tipoDestino) {
        if (tipoOrigen != null && tipoDestino != null) {
            this.reglasPermitidas.add(new ReglaDependenciaPermitida(tipoOrigen, tipoDestino));
            System.out.println("Regla añadida: " + tipoOrigen.getNombreTipo() + " -> " + tipoDestino.getNombreTipo());
        }
    }

    // Verifica si una dependencia entre dos capas esta permitida
    public boolean esDependenciaPermitida(ConfiguracionCapa capaOrigen, ConfiguracionCapa capaDestino) {
        if (capaOrigen == null || capaDestino == null) {
            return false;
        }

        IdentificadorTipoCapa idTipoOrigen = capaOrigen.getIdentificadorTipo();
        IdentificadorTipoCapa idTipoDestino = capaDestino.getIdentificadorTipo();

        // Regla para evitar dependencias cruzadas dentro de la misma capa
        if (idTipoOrigen.getNombreTipo().equals(idTipoDestino.getNombreTipo())) {
            return false;
        }

        // Recorre las reglas definidas para validar la dependencia
        for (ReglaDependenciaPermitida regla : this.reglasPermitidas) {
            if (regla.coincideCon(idTipoOrigen, idTipoDestino)) {
                return true;
            }
        }
        return false;
    }
}

