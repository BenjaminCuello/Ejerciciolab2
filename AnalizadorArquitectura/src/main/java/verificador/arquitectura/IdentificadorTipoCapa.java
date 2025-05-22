package verificador.arquitectura;

// Esta interfaz sirve para marcar las clases que representan
// un tipo especifico de capa en nuestra arquitectura.
// Ademas, define un metodo para obtener un String que identifique el tipo.
public interface IdentificadorTipoCapa {
    // Metodo que cada tipo de capa concreto debera implementar
    // para devolver un String unico que lo identifique.
    String getNombreTipo();
}