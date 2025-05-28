package verificador.arquitectura.estructura;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.PackageDeclaration;
import verificador.arquitectura.tipos.IdentificadorTipoCapa;
import verificador.arquitectura.tipos.TipoCapaPersistencia;
import verificador.arquitectura.tipos.TipoCapaPresentacion;
import verificador.arquitectura.tipos.TipoCapaServicio;

import java.util.concurrent.atomic.AtomicInteger;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class VerificadorArquitectura {

    public static void main(String[] args) {
        System.out.println("Iniciando el Verificador de Arquitectura...");

        // Define la arquitectura y sus capas
        DefinicionArquitectura arquitecturaDefinida = configurarArquitectura();

        // Pedir al usuario la ruta del proyecto a analizar
        String rutaAlCodigoFuente = solicitarRutaProyecto();

        System.out.println("\nAnalizando el proyecto en: " + rutaAlCodigoFuente);
        File directorioFuente = new File(rutaAlCodigoFuente);

        // Validar que la ruta exista y sea un directorio
        if (!directorioFuente.exists() || !directorioFuente.isDirectory()) {
            System.err.println("Error: La ruta especificada no existe o no es un directorio valido.");
            System.err.println("Asegurate de que la ruta sea a la carpeta que contiene los paquetes (ej. 'src/main/java').");
            return;
        }

        analizarProyecto(rutaAlCodigoFuente, arquitecturaDefinida);
    }

    // Configura la arquitectura con las tres capas y sus paquetes
    public static DefinicionArquitectura configurarArquitectura() {
        DefinicionArquitectura arquitecturaDefinida = new DefinicionArquitectura();

        // Crear los tipos de capa
        IdentificadorTipoCapa tipoPresentacion = new TipoCapaPresentacion();
        IdentificadorTipoCapa tipoServicio = new TipoCapaServicio();
        IdentificadorTipoCapa tipoPersistencia = new TipoCapaPersistencia();

        // Agregar capa de presentacion
        List<String> paquetesPresentacion = new ArrayList<>();
        paquetesPresentacion.add("ui");
        ConfiguracionCapa capaPresentacion = new ConfiguracionCapa(
                "Capa de Presentacion",
                tipoPresentacion,
                paquetesPresentacion
        );
        arquitecturaDefinida.anadirCapa(capaPresentacion);

        // Agregar capa de servicio
        List<String> paquetesServicio = new ArrayList<>();
        paquetesServicio.add("service");
        ConfiguracionCapa capaServicio = new ConfiguracionCapa(
                "Capa de Servicio",
                tipoServicio,
                paquetesServicio
        );
        arquitecturaDefinida.anadirCapa(capaServicio);

        // Agregar capa de persistencia
        List<String> paquetesPersistencia = new ArrayList<>();
        paquetesPersistencia.add("dao");
        ConfiguracionCapa capaPersistencia = new ConfiguracionCapa(
                "Capa de Persistencia",
                tipoPersistencia,
                paquetesPersistencia
        );
        arquitecturaDefinida.anadirCapa(capaPersistencia);

        // Mostrar la arquitectura configurada
        System.out.println("\nArquitectura Configurada:");
        for (ConfiguracionCapa cfgCapa : arquitecturaDefinida.getCapasDefinidas()) {
            System.out.println("  Nombre: " + cfgCapa.getNombreCapa() +
                    ", Tipo: " + cfgCapa.getNombreDelTipoDeCapa() +
                    ", Paquetes: " + cfgCapa.getPaquetesAsociados());
        }

        // Definir las reglas permitidas de dependencia
        System.out.println("\nDefiniendo Reglas de Dependencia Permitidas:");
        arquitecturaDefinida.permitirDependencia(tipoPresentacion, tipoServicio);
        arquitecturaDefinida.permitirDependencia(tipoServicio, tipoPersistencia);

        return arquitecturaDefinida;
    }

    // Solicita al usuario la ruta del proyecto
    public static String solicitarRutaProyecto() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("\nPor favor, ingresa la ruta completa a la carpeta 'src/main/java' del proyecto a analizar:");
        System.out.print("Ruta: ");
        String ruta = scanner.nextLine();
        scanner.close();
        return ruta;
    }

    // Analiza todos los archivos java del proyecto para verificar las dependencias
    public static void analizarProyecto(String rutaBaseCodigo, DefinicionArquitectura arquitectura) {
        File directorioBase = new File(rutaBaseCodigo);
        List<File> archivosJava = listarArchivosJava(directorioBase);

        System.out.println("Archivos Java encontrados: " + archivosJava.size());
        AtomicInteger numeroViolaciones = new AtomicInteger(0);

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

                // Analizar declaraciones import
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

                        boolean mismaCapa = capaClaseActual.getNombreDelTipoDeCapa().equals(capaClaseImportada.getNombreDelTipoDeCapa());
                        boolean paquetesDistintos = !paqueteClaseActual.equals(paqueteImportado);

                        if (mismaCapa) { //si aqui quito el paquete distinto si detecta la cruzada
                            numeroViolaciones.incrementAndGet();
                            System.err.println("      ---------------------------------------------------------");
                            System.err.println("      VIOLACION ARQUITECTONICA DETECTADA:");
                            System.err.println("      TIPO DE ERROR: Dependencia cruzada dentro de la misma capa '" + capaClaseActual.getNombreDelTipoDeCapa() + "'.");
                            System.err.println("      DETALLES:");
                            System.err.println("        - Clase Origen: " + archivoJava.getName());
                            System.err.println("        - Paquete Origen: " + paqueteClaseActual);
                            System.err.println("        - Paquete Destino: " + paqueteImportado);
                            importDeclaracion.getRange().ifPresent(r ->
                                    System.err.println("        - En Línea (aproximada del import): " + r.begin.line)
                            );
                            System.err.println("      ---------------------------------------------------------");
                        } else if (!mismaCapa && !arquitectura.esDependenciaPermitida(capaClaseActual, capaClaseImportada)) {
                            numeroViolaciones.incrementAndGet();
                            System.err.println("      ---------------------------------------------------------");
                            System.err.println("      VIOLACION ARQUITECTONICA DETECTADA:");
                            String tipoError = "Dependencia de la capa '" + capaClaseActual.getNombreDelTipoDeCapa() +
                                    "' hacia la capa '" + capaClaseImportada.getNombreDelTipoDeCapa() + "' no está permitida.";
                            System.err.println("      TIPO DE ERROR: " + tipoError);
                            System.err.println("      DETALLES:");
                            System.err.println("        - Archivo Origen: " + archivoJava.getName());
                            System.err.println("        - Paquete Origen: " + paqueteClaseActual);
                            System.err.println("        - Dependencia Hacia Paquete: " + paqueteImportado);
                            importDeclaracion.getRange().ifPresent(r ->
                                    System.err.println("        - En Línea (aproximada del import): " + r.begin.line)
                            );
                            System.err.println("      ---------------------------------------------------------");
                        }
                    }
                }

                // Analizar instanciaciones con new Clase()
                cu.findAll(com.github.javaparser.ast.expr.ObjectCreationExpr.class).forEach(expr -> {
                    String nombreClaseInstanciada = expr.getType().asString();
                    String paqueteImportado = null;

                    // Buscar en imports exactos
                    for (ImportDeclaration importDecl : cu.getImports()) {
                        String importStr = importDecl.getNameAsString();
                        if (importStr.endsWith("." + nombreClaseInstanciada)) {
                            paqueteImportado = importStr.substring(0, importStr.lastIndexOf('.'));
                            break;
                        }
                    }

                    // Si no esta en imports usar el paquete actual
                    if (paqueteImportado == null) {
                        Optional<PackageDeclaration> pkg = cu.getPackageDeclaration();
                        if (pkg.isPresent()) {
                            paqueteImportado = pkg.get().getNameAsString();
                        }
                    }

                    // Ignorar clases estandar de java
                    if (paqueteImportado == null || paqueteImportado.startsWith("java.") || paqueteImportado.startsWith("javax.")) {
                        return;
                    }

                    ConfiguracionCapa capaImportada = arquitectura.getCapaPorNombrePaquete(paqueteImportado);

                    if (capaImportada != null) {
                        boolean mismaCapa = capaClaseActual.getNombreDelTipoDeCapa().equals(capaImportada.getNombreDelTipoDeCapa());
                        boolean paquetesDistintos = !paqueteClaseActual.equals(paqueteImportado);

                        if (mismaCapa && paquetesDistintos) {
                            numeroViolaciones.incrementAndGet();
                            System.err.println("      ---------------------------------------------------------");
                            System.err.println("      VIOLACION ARQUITECTONICA DETECTADA:");
                            System.err.println("      TIPO DE ERROR: Dependencia cruzada dentro de la misma capa '" + capaClaseActual.getNombreDelTipoDeCapa() + "'.");
                            System.err.println("      DETALLES:");
                            System.err.println("        - Clase Origen: " + archivoJava.getName());
                            System.err.println("        - Paquete Origen: " + paqueteClaseActual);
                            System.err.println("        - Se instancia clase de paquete: " + paqueteImportado);
                            expr.getRange().ifPresent(r -> System.err.println("        - En Línea (aproximada): " + r.begin.line));
                            System.err.println("      ---------------------------------------------------------");
                        } else if (!mismaCapa && !arquitectura.esDependenciaPermitida(capaClaseActual, capaImportada)) {
                            numeroViolaciones.incrementAndGet();
                            System.err.println("      ---------------------------------------------------------");
                            System.err.println("      VIOLACION ARQUITECTONICA DETECTADA:");
                            System.err.println("      TIPO DE ERROR: Instanciación de clase no permitida entre capas.");
                            System.err.println("      DETALLES:");
                            System.err.println("        - Clase Origen: " + archivoJava.getName());
                            System.err.println("        - Capa Origen: " + capaClaseActual.getNombreDelTipoDeCapa());
                            System.err.println("        - Capa Destino: " + capaImportada.getNombreDelTipoDeCapa());
                            expr.getRange().ifPresent(r -> System.err.println("        - En Línea (aproximada): " + r.begin.line));
                            System.err.println("      ---------------------------------------------------------");
                        }
                    }
                });

            } catch (FileNotFoundException e) {
                System.err.println("Error: Archivo no encontrado al parsear - " + archivoJava.getAbsolutePath());
            } catch (Exception e) {
                System.err.println("Error al parsear el archivo " + archivoJava.getName() + ": " + e.getMessage());
            }
        }
        // Mostrar resumen al final del analisis
        if (numeroViolaciones.get() > 0) {
            System.out.println("\n--- Resumen: Se encontraron " + numeroViolaciones + " violaciones arquitectonicas. ---");
        } else {
            System.out.println("\n--- Resumen: ¡No se encontraron violaciones arquitectonicas! ---");
        }
    }

    // Recorre todas las carpetas y devuelve los archivos java encontrados
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

}
