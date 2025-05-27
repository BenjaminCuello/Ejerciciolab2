package verificador.arquitectura.reglas;

import verificador.arquitectura.tipos.IdentificadorTipoCapa;

// Representa una regla que permite una dependencia de una capa a otra
public class ReglaDependenciaPermitida {

    private IdentificadorTipoCapa tipoCapaOrigen;
    private IdentificadorTipoCapa tipoCapaDestino;

    // Crea una nueva regla indicando la capa origen y la capa destino
    public ReglaDependenciaPermitida(IdentificadorTipoCapa origen, IdentificadorTipoCapa destino) {
        this.tipoCapaOrigen = origen;
        this.tipoCapaDestino = destino;
    }

    // Devuelve el tipo de capa origen
    public IdentificadorTipoCapa getTipoCapaOrigen() {
        return tipoCapaOrigen;
    }

    // Devuelve el tipo de capa destino
    public IdentificadorTipoCapa getTipoCapaDestino() {
        return tipoCapaDestino;
    }

    // Verifica si esta regla coincide con una dependencia observada
    public boolean coincideCon(IdentificadorTipoCapa origenObservado, IdentificadorTipoCapa destinoObservado) {
        if (origenObservado == null || destinoObservado == null) {
            return false;
        }
        // Compara por nombre los tipos de capa
        return this.tipoCapaOrigen.getNombreTipo().equals(origenObservado.getNombreTipo()) &&
                this.tipoCapaDestino.getNombreTipo().equals(destinoObservado.getNombreTipo());
    }
}