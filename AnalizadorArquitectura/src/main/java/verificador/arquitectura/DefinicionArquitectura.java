package verificador.arquitectura;

import java.util.ArrayList;
import java.util.List;

// Esta clase define la arquitectura completa del sistema,
// incluyendo todas las capas. Mas adelante, tambien incluira
// las reglas de dependencia entre ellas.
public class DefinicionArquitectura {

    private List<ConfiguracionCapa> capasDefinidas;
    private List<ReglaDependenciaPermitida> reglasPermitidas;

    // Constructor que inicializa la lista de capas.
    public DefinicionArquitectura() {
        this.capasDefinidas = new ArrayList<>();
        this.reglasPermitidas = new ArrayList<>();
    }

    // Metodo para añadir una nueva capa a nuestra definicion de arquitectura.
    public void anadirCapa(ConfiguracionCapa capa) {
        if (capa != null) {
            this.capasDefinidas.add(capa);
        }
    }
    // Metodo para obtener todas las capas definidas en la arquitectura.
    public List<ConfiguracionCapa> getCapasDefinidas() {
        // Devolvemos una nueva copia para proteger la lista interna.
        return new ArrayList<>(this.capasDefinidas);
    }
    // Metodo para encontrar una ConfiguracionCapa dado el nombre de un paquete.
    // Esto sera util para saber a que capa pertenece una clase especifica
    // basandonos en su paquete.
    public ConfiguracionCapa getCapaPorNombrePaquete(String nombrePaquete) {
        for (ConfiguracionCapa capa : this.capasDefinidas) {
            if (capa.contienePaquete(nombrePaquete)) {
                return capa;
            }
        }
        return null;
    }
    // Metodo para encontrar una ConfiguracionCapa dado el nombre de la capa.
    public ConfiguracionCapa getCapaPorNombre(String nombreCapa) {
        for (ConfiguracionCapa capa : this.capasDefinidas) {
            if (capa.getNombreCapa().equals(nombreCapa)) {
                return capa;
            }
        }
        return null;
    }


    // Metodo para añadir una regla que permite una dependencia entre tipos de capa.
    // Por ejemplo, permitir que Presentacion dependa de Servicio.
    public void permitirDependencia(IdentificadorTipoCapa tipoOrigen, IdentificadorTipoCapa tipoDestino) {
        if (tipoOrigen != null && tipoDestino != null) {
            this.reglasPermitidas.add(new ReglaDependenciaPermitida(tipoOrigen, tipoDestino));
            System.out.println("Regla añadida: " + tipoOrigen.getNombreTipo() + " -> " + tipoDestino.getNombreTipo());
        }
    }

    // Metodo para verificar si una dependencia entre dos capas configuradas es permitida.
    public boolean esDependenciaPermitida(ConfiguracionCapa capaOrigen, ConfiguracionCapa capaDestino) {
        if (capaOrigen == null || capaDestino == null) {
            return false; // No se puede determinar si las capas no existen
        }

        IdentificadorTipoCapa idTipoOrigen = capaOrigen.getIdentificadorTipo();
        IdentificadorTipoCapa idTipoDestino = capaDestino.getIdentificadorTipo();

        // --- INICIO DE LA MODIFICACION PARA REGLA INTRA-CAPA ---
        // Regla 4: No se permiten dependencias cruzadas entre clases dentro de una misma capa.
        // Comparamos los nombres de los tipos de capa. Si son iguales, es una dependencia intra-capa.
        if (idTipoOrigen.getNombreTipo().equals(idTipoDestino.getNombreTipo())) {
            // Comentamos la siguiente linea para que se vea la violacion
            // System.out.println("DEBUG: Detectada dependencia intra-capa: " + idTipoOrigen.getNombreTipo() + " -> " + idTipoDestino.getNombreTipo());
            return false; // Las dependencias dentro de la misma capa no estan permitidas por defecto.
        }
        // --- FIN DE LA MODIFICACION PARA REGLA INTRA-CAPA ---

        // Si no es una dependencia intra-capa, verificamos las reglas explicitas.
        // Recorremos todas las reglas permitidas que hemos definido.
        for (ReglaDependenciaPermitida regla : this.reglasPermitidas) {
            if (regla.coincideCon(idTipoOrigen, idTipoDestino)) {
                return true; // Si alguna regla coincide, la dependencia es permitida.
            }
        }
        return false; // Si ninguna regla coincide (y no era intra-capa), la dependencia no es permitida.
    }
}

