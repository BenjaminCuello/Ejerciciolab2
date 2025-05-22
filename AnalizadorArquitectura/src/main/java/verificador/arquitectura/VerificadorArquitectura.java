package verificador.arquitectura;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.PackageDeclaration;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Scanner; // <--- IMPORTANTE: Añadir esta importacion

public class VerificadorArquitectura {

    public static void main(String[] args) {
        System.out.println("Iniciando el Verificador de Arquitectura...");

        DefinicionArquitectura arquitecturaDefinida = new DefinicionArquitectura();

        // --- Configuracion de Capas (sin cambios) ---
        IdentificadorTipoCapa tipoPresentacion = new TipoCapaPresentacion();
        IdentificadorTipoCapa tipoServicio = new TipoCapaServicio();
        IdentificadorTipoCapa tipoPersistencia = new TipoCapaPersistencia();

        List<String> paquetesPresentacion = new ArrayList<>();
        paquetesPresentacion.add("ui");
        ConfiguracionCapa capaPresentacion = new ConfiguracionCapa(
                "Capa de Presentacion",
                tipoPresentacion,
                paquetesPresentacion
        );
        arquitecturaDefinida.anadirCapa(capaPresentacion);

        List<String> paquetesServicio = new ArrayList<>();
        paquetesServicio.add("service");
        ConfiguracionCapa capaServicio = new ConfiguracionCapa(
                "Capa de Servicio",
                tipoServicio,
                paquetesServicio
        );
        arquitecturaDefinida.anadirCapa(capaServicio);

        List<String> paquetesPersistencia = new ArrayList<>();
        paquetesPersistencia.add("dao");
        ConfiguracionCapa capaPersistencia = new ConfiguracionCapa(
                "Capa de Persistencia",
                tipoPersistencia,
                paquetesPersistencia
        );
        arquitecturaDefinida.anadirCapa(capaPersistencia);

        System.out.println("\nArquitectura Configurada:");
        for (ConfiguracionCapa cfgCapa : arquitecturaDefinida.getCapasDefinidas()) {
            System.out.println("  Nombre: " + cfgCapa.getNombreCapa() +
                    ", Tipo: " + cfgCapa.getNombreDelTipoDeCapa() +
                    ", Paquetes: " + cfgCapa.getPaquetesAsociados());
        }

        // --- Definicion de Reglas de Dependencia Permitidas (sin cambios) ---
        System.out.println("\nDefiniendo Reglas de Dependencia Permitidas:");
        arquitecturaDefinida.permitirDependencia(tipoPresentacion, tipoServicio);
        arquitecturaDefinida.permitirDependencia(tipoServicio, tipoPersistencia);

        // --- OBTENER RUTA DEL PROYECTO DEL USUARIO ---
        Scanner scanner = new Scanner(System.in); // Creamos un objeto Scanner para leer la entrada del usuario
        System.out.println("\nPor favor, ingresa la ruta completa a la carpeta 'src/main/java' del proyecto a analizar:");
        System.out.print("Ruta: ");
        String rutaAlCodigoFuente = scanner.nextLine(); // Leemos la linea que el usuario ingrese
        scanner.close(); // Cerramos el scanner cuando ya no lo necesitamos

        System.out.println("\nAnalizando el proyecto en: " + rutaAlCodigoFuente);
        File directorioFuente = new File(rutaAlCodigoFuente);

        if (!directorioFuente.exists() || !directorioFuente.isDirectory()) {
            System.err.println("Error: La ruta especificada no existe o no es un directorio valido.");
            System.err.println("Asegurate de que la ruta sea a la carpeta que contiene los paquetes (ej. 'src/main/java').");
            return; // Salimos del programa si la ruta no es valida
        }

        analizarProyecto(rutaAlCodigoFuente, arquitecturaDefinida);

    } // Fin del metodo main

    // El metodo analizarProyecto y listarArchivosJava se quedan como estaban
    public static void analizarProyecto(String rutaBaseCodigo, DefinicionArquitectura arquitectura) {
        // ... (sin cambios aqui)
        File directorioBase = new File(rutaBaseCodigo);
        List<File> archivosJava = listarArchivosJava(directorioBase);

        System.out.println("Archivos Java encontrados: " + archivosJava.size());
        int numeroViolaciones = 0;

        for (File archivoJava : archivosJava) {
            System.out.println("\n  Parseando archivo: " + archivoJava.getAbsolutePath());
            try {
                CompilationUnit cu = StaticJavaParser.parse(archivoJava);

                Optional<PackageDeclaration> declaracionPaqueteOpt = cu.getPackageDeclaration();
                if (!declaracionPaqueteOpt.isPresent()) {
                    System.out.println("    ADVERTENCIA: El archivo no tiene declaracion de paquete. Se omite.");
                    continue;
                }

                String paqueteClaseActual = declaracionPaqueteOpt.get().getNameAsString();
                ConfiguracionCapa capaClaseActual = arquitectura.getCapaPorNombrePaquete(paqueteClaseActual);

                if (capaClaseActual == null) {
                    System.out.println("    ADVERTENCIA: El paquete '" + paqueteClaseActual + "' no pertenece a ninguna capa definida. Se omite el archivo.");
                    continue;
                }

                System.out.println("    Paquete: " + paqueteClaseActual + " (Capa: " + capaClaseActual.getNombreDelTipoDeCapa() + ")");

                for (ImportDeclaration importDeclaracion : cu.getImports()) {
                    String nombreClaseOModuloImportado = importDeclaracion.getNameAsString();

                    String paqueteImportado;
                    if (nombreClaseOModuloImportado.endsWith(".*")) {
                        paqueteImportado = nombreClaseOModuloImportado.substring(0, nombreClaseOModuloImportado.lastIndexOf(".*"));
                    } else if (nombreClaseOModuloImportado.contains(".")) {
                        paqueteImportado = nombreClaseOModuloImportado.substring(0, nombreClaseOModuloImportado.lastIndexOf('.'));
                    } else {
                        System.out.println("      ADVERTENCIA: No se pudo determinar el paquete para el import: " + nombreClaseOModuloImportado);
                        continue;
                    }

                    if (paqueteImportado.startsWith("java.") || paqueteImportado.startsWith("javax.")) {
                        continue;
                    }

                    ConfiguracionCapa capaClaseImportada = arquitectura.getCapaPorNombrePaquete(paqueteImportado);

                    if (capaClaseImportada != null) {
                        System.out.println("      Dependencia encontrada: " +
                                capaClaseActual.getNombreDelTipoDeCapa() + " (" + paqueteClaseActual + ") -> " +
                                capaClaseImportada.getNombreDelTipoDeCapa() + " (" + paqueteImportado + ")");

                        if (!arquitectura.esDependenciaPermitida(capaClaseActual, capaClaseImportada)) {
                            numeroViolaciones++;
                            System.err.println("      ---------------------------------------------------------");
                            System.err.println("      VIOLACION ARQUITECTONICA DETECTADA:");

                            // Determinar el tipo de error especifico
                            String tipoError;
                            if (capaClaseActual.getIdentificadorTipo().getNombreTipo().equals(capaClaseImportada.getIdentificadorTipo().getNombreTipo())) {
                                tipoError = "Dependencia intra-capa no permitida en la capa '" + capaClaseActual.getNombreDelTipoDeCapa() + "'.";
                            } else {
                                tipoError = "Dependencia de la capa '" + capaClaseActual.getNombreDelTipoDeCapa() +
                                        "' hacia la capa '" + capaClaseImportada.getNombreDelTipoDeCapa() + "' no esta permitida.";
                            }
                            System.err.println("      TIPO DE ERROR: " + tipoError);
                            System.err.println("      DETALLES:");
                            System.err.println("        - Archivo Origen: " + archivoJava.getName());
                            System.err.println("        - Paquete Origen: " + paqueteClaseActual + " (Capa: " + capaClaseActual.getNombreDelTipoDeCapa() + ")");
                            System.err.println("        - Dependencia Hacia Paquete: " + paqueteImportado + " (Capa: " + capaClaseImportada.getNombreDelTipoDeCapa() + ")");
                            importDeclaracion.getRange().ifPresent(r ->
                                    System.err.println("        - En Linea (aproximada del import): " + r.begin.line)
                            );
                            System.err.println("      ---------------------------------------------------------");
                        }
                    }
                }

            } catch (FileNotFoundException e) {
                System.err.println("Error: Archivo no encontrado al parsear - " + archivoJava.getAbsolutePath());
            } catch (Exception e) {
                System.err.println("Error al parsear el archivo " + archivoJava.getName() + ": " + e.getMessage());
            }
        }

        if (numeroViolaciones > 0) {
            System.out.println("\n--- Resumen: Se encontraron " + numeroViolaciones + " violaciones arquitectonicas. ---");
        } else {
            System.out.println("\n--- Resumen: ¡No se encontraron violaciones arquitectonicas! ---");
        }
    }
    public static List<File> listarArchivosJava(File directorio) {
        List<File> listaArchivos = new ArrayList<>();
        File[] archivos = directorio.listFiles();

        if (archivos != null) {
            for (File archivo : archivos) {
                if (archivo.isDirectory()) {
                    listaArchivos.addAll(listarArchivosJava(archivo));
                } else if (archivo.getName().endsWith(".java")) {
                    listaArchivos.add(archivo);
                }
            }
        }
        return listaArchivos;
    }


} // Fin de la clase VerificadorArquitectura