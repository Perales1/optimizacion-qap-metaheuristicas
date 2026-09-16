import java.util.Arrays;
import java.io.PrintStream;
import java.io.File;
import java.io.FileOutputStream;

public class Main {
    public static void main(String[] args) throws Exception {

        // Lee siempre el configurador.txt
        ConfigLoader config = new ConfigLoader("archivos/configurador.txt");

        // Crear carpeta logs si no existe
        File logDir = new File("archivos/logs");
        if (!logDir.exists()) {
            logDir.mkdirs();
        }

        // Nombre base del archivo (sin extensión)
        String nombreArchivoBase = new File(config.getArchivo()).getName().replaceAll("\\..+$", "");
        String algoritmo = config.getAlgoritmo().toUpperCase();
        String logFileName;

        // Semilla del vector de semillas a utilizar
        int posSemilla = config.getposSemilla();
        String semilla = config.getSemillas()[posSemilla];
        String operador = config.getOperadorCruce();
        String elitismo = String.valueOf(config.getElitismo());
        String kbest = String.valueOf(config.getKBest());

        // Generación del nombre del log según algoritmo
        if (algoritmo.equals("GREEDY")) {
            logFileName = "archivos/logs/" + nombreArchivoBase + "_" + algoritmo + ".txt";
        } else if (algoritmo.equals("GEN") || algoritmo.equals("EST")) {
            logFileName = "archivos/logs/" + nombreArchivoBase + "_" + algoritmo + "_"
                    + operador + "_E:" + elitismo + "_kBest:" + kbest + "_" + semilla + ".txt";
        } else if (algoritmo.equals("MEM")) {
            // Para MEM añadimos intervaloTabu e iteracionesTabu
            logFileName = "archivos/logs/" + nombreArchivoBase + "_" + algoritmo + "_"
                    + semilla + "_ITab_" + config.getIntervaloTabu()
                    + "_IterTab_" + config.getIteracionesTabu() + ".txt";
        } else {
            // Resto de algoritmos con semilla
            logFileName = "archivos/logs/" + nombreArchivoBase + "_" + algoritmo + "_" + semilla + ".txt";
        }

        // Redirigir la salida a archivo (append=true)
        PrintStream ps = new PrintStream(new FileOutputStream(logFileName, true));
        System.setOut(ps);

        System.out.println("===== NUEVA EJECUCIÓN =====");
        config.printConfig();

        // Cargar instancia del QAP
        Instance inst = new Instance(config.getArchivo());
        long tiempoinicio = System.currentTimeMillis();

        // Elección del algoritmo
        switch (algoritmo) {
            case "GREEDY":
                AlgGE_04_15 solver = new AlgGE_04_15(inst);
                int[] S = solver.solucion();
                System.out.println("Solución Greedy: " + Arrays.toString(S));
                break;

            case "GREEDYRANDOM":
                AlgGA_04_15 resolver = new AlgGA_04_15(inst, config.getParametro(), config.getSemillas(), posSemilla);
                int[] solucionGR = resolver.solucion();
                System.out.println("Solución GreedyRandom: " + Arrays.toString(solucionGR));
                System.out.println("Coste inicial: " + AlgGE_04_15.calcularCoste(inst.F, inst.D, solucionGR));
                break;

            case "BL":
                AlgGA_04_15 gr = new AlgGA_04_15(inst, config.getParametro(), config.getSemillas(), posSemilla);
                int[] solucionInicial = gr.solucion();
                AlgBL_04_15 busquedaLocal = new AlgBL_04_15(inst, solucionInicial, Integer.parseInt(config.getIteraciones()));
                int[] solucionBL = busquedaLocal.ejecutarBusqueda();
                System.out.println("Solución final tras Búsqueda Local: " + Arrays.toString(solucionBL));
                System.out.println("Coste final tras Búsqueda Local: " + AlgGE_04_15.calcularCoste(inst.F, inst.D, solucionBL));
                break;

            case "TABU":
                AlgGA_04_15 tabu = new AlgGA_04_15(inst, config.getParametro(), config.getSemillas(), posSemilla);
                int[] solucionTabuInicial = tabu.solucion();
                AlgBT_04_15 busquedaTabu = new AlgBT_04_15(inst, solucionTabuInicial, config.getIteraciones(), config.getTenencia(), config.getSemillas(), posSemilla);
                int[] mejorSolucion = busquedaTabu.ejecucionTabu();
                System.out.println("Solución final tras Tabu: " + Arrays.toString(mejorSolucion));
                System.out.println("Coste final tras Tabu: " + AlgGE_04_15.calcularCoste(inst.F, inst.D, mejorSolucion));
                break;

            case "GEN":
                AlgGA_04_15 gen = new AlgGA_04_15(inst, config.getParametro(), config.getSemillas(), posSemilla);
                int[] solucionGen = gen.solucion();
                AlgGEN_04_15 genetico = new AlgGEN_04_15(inst, solucionGen, config.getSemillas(), config.getposSemilla());
                genetico.setParametrosEvolutivos(
                        config.getElitismo(),
                        config.getKBest(),
                        config.getKWorst(),
                        config.getOperadorCruce(),
                        config.getProbCruce(),
                        config.getProbMutacion(),
                        config.getEvaluaciones(),
                        config.getTamPoblacion(),
                        config.getProbGreedy()
                );
                int[] solucionesGEN = genetico.ejecutar();
                System.out.println("Solución final GEN: " + Arrays.toString(solucionesGEN));
                System.out.println("Coste final GEN: " + AlgGE_04_15.calcularCoste(inst.F, inst.D, solucionesGEN));
                break;

            case "EST":
                AlgGA_04_15 est = new AlgGA_04_15(inst, config.getParametro(), config.getSemillas(), posSemilla);
                int[] solucionEst = est.solucion();
                AlgEST_04_15 estacionario = new AlgEST_04_15(inst, solucionEst, config.getSemillas(), config.getposSemilla());
                estacionario.setParametrosEvolutivos(
                        config.getKBest(),
                        config.getKWorst(),
                        config.getOperadorCruce(),
                        config.getProbMutacion(),
                        config.getEvaluaciones(),
                        config.getTamPoblacion(),
                        config.getProbGreedy()
                );
                int[] solucionesEST = estacionario.ejecutar();
                System.out.println("Solución final GEN: " + Arrays.toString(solucionesEST));
                System.out.println("Coste final GEN: " + AlgGE_04_15.calcularCoste(inst.F, inst.D, solucionesEST));
                break;

            case "MEM":
                // Generación de solución inicial con GA
                AlgGA_04_15 genMem = new AlgGA_04_15(inst, config.getParametro(), config.getSemillas(), config.getposSemilla());
                int[] solucionMem = genMem.solucion();

                // Inicialización del memético
                AlgMEM_04_15 memetico = new AlgMEM_04_15(inst, solucionMem, config.getSemillas(), config.getposSemilla());
                memetico.setParametros(
                        config.getElitismo(),
                        config.getKBest(),
                        config.getKWorst(),
                        config.getOperadorCruce(),
                        config.getProbCruce(),
                        config.getProbMutacion(),
                        config.getTamPoblacion(),
                        config.getProbGreedy(),
                        config.getEvaluaciones(),
                        config.getTenencia(),
                        config.getIntervaloTabu(),
                        config.getIteracionesTabu()
                );

                int[] solucionesMEM = memetico.ejecutar();
                System.out.println("Solución final MGEN: " + Arrays.toString(solucionesMEM));
                System.out.println("Coste final MGEN: " + AlgGE_04_15.calcularCoste(inst.F, inst.D, solucionesMEM));
                break;

            default:
                throw new IllegalArgumentException("Algoritmo no reconocido: " + algoritmo);
        }

        long tiempofinal = System.currentTimeMillis();
        long duracion = tiempofinal - tiempoinicio;

        System.out.println("\nTiempo total de ejecución (ms): " + duracion);
        System.out.println("===== FIN DE LA EJECUCIÓN =====\n");

        ps.close();
    }
}
