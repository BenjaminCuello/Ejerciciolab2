package verificador.arquitectura;

// Esta clase representa una regla que permite una dependencia
// desde un tipo de capa origen hacia un tipo de capa destino.
public class ReglaDependenciaPermitida {

    private IdentificadorTipoCapa tipoCapaOrigen;
    private IdentificadorTipoCapa tipoCapaDestino;

    // Constructor para crear una nueva regla de dependencia permitida.
    // Se especifica el tipo de la capa de origen y el tipo de la capa de destino.
    public ReglaDependenciaPermitida(IdentificadorTipoCapa origen, IdentificadorTipoCapa destino) {
        this.tipoCapaOrigen = origen;
        this.tipoCapaDestino = destino;
    }

    // Metodo para obtener el tipo de capa origen de esta regla.
    public IdentificadorTipoCapa getTipoCapaOrigen() {
        return tipoCapaOrigen;
    }

    // Metodo para obtener el tipo de capa destino de esta regla.
    public IdentificadorTipoCapa getTipoCapaDestino() {
        return tipoCapaDestino;
    }

    // Metodo para verificar si esta regla coincide con una dependencia observada
    // entre una capa origen y una capa destino.
    // Compara los nombres de los tipos de capa.
    public boolean coincideCon(IdentificadorTipoCapa origenObservado, IdentificadorTipoCapa destinoObservado) {
        if (origenObservado == null || destinoObservado == null) {
            return false;
        }
        // Comparamos usando los nombres de los tipos para asegurarnos
        // de que estamos comparando el "tipo" efectivo de la capa.
        return this.tipoCapaOrigen.getNombreTipo().equals(origenObservado.getNombreTipo()) &&
                this.tipoCapaDestino.getNombreTipo().equals(destinoObservado.getNombreTipo());
    }
}