import java.util.*;

public class AlgMEM_04_15 {

    private Instance inst;
    private int[] solucionActual;
    private int[] solucionAnterior;
    private String[] semillas;
    private int posSemilla;

    // Parámetros evolutivos configurables
    private int elitismo;
    private int kBest;
    private int kWorst;
    private String operadorCruce;
    private double probCruce;
    private double probMutacion;
    private int maxEvaluaciones;
    private int tamPoblacion;
    private double probGreedy;

    // Parámetros Tabú
    private int tenenciaTabu;
    private int intervaloTabu;
    private int iteracionesTabu;
    private int siguienteDisparoTabu;

    private Random rand;

    static class Move { int i, j; }

    public AlgMEM_04_15(Instance inst, int[] solucionInicial, String[] semillas, int posSemilla) {
        this.inst = inst;
        this.solucionActual = Arrays.copyOf(solucionInicial, solucionInicial.length);
        this.semillas = semillas;
        this.posSemilla = posSemilla;
    }

    // Configuración de parámetros desde ConfigLoader
    public void setParametros(
            int elitismo,
            int kBest,
            int kWorst,
            String operadorCruce,
            double probCruce,
            double probMutacion,
            int tamPoblacion,
            double probGreedy,
            int maxEvaluaciones,
            int tenenciaTabu,
            int intervaloTabu,
            int iteracionesTabu
    ) {
        this.elitismo = elitismo;
        this.kBest = kBest;
        this.kWorst = kWorst;
        this.operadorCruce = operadorCruce;
        this.probCruce = probCruce;
        this.probMutacion = probMutacion;
        this.tamPoblacion = tamPoblacion;
        this.probGreedy = probGreedy;
        this.maxEvaluaciones = maxEvaluaciones;
        this.tenenciaTabu = tenenciaTabu;
        this.intervaloTabu = intervaloTabu;
        this.iteracionesTabu = iteracionesTabu;

        this.siguienteDisparoTabu = intervaloTabu; // primer disparo
    }

    public int[] ejecutar() throws Exception {
        rand = new Random(Long.parseLong(semillas[posSemilla]));

        int[][] poblacion = new int[tamPoblacion][];
        double[] costes = new double[tamPoblacion];
        int evaluaciones = 0;

        System.out.println("************************************************************");
        System.out.println("* ALGORITMO MEMÉTICO GENERACIONAL (MGEN)                  *");
        System.out.println("************************************************************");
        System.out.println(" - Población: " + tamPoblacion);
        System.out.println(" - Max Evaluaciones: " + maxEvaluaciones);
        System.out.println(" - Prob. Cruce: " + probCruce);
        System.out.println(" - Prob. Mutación: " + probMutacion);
        System.out.println(" - Elitismo: " + elitismo);
        System.out.println(" - kBest: " + kBest);
        System.out.println("************************************************************\n");

        int nGreedy = (int) (tamPoblacion * probGreedy);
        System.out.println(">>> INICIALIZACIÓN DE POBLACIÓN");
        System.out.println("    -> Individuos Greedy: " + nGreedy);
        System.out.println("    -> Individuos Aleatorios: " + (tamPoblacion - nGreedy));

        for (int i = 0; i < tamPoblacion; i++) {
            if (i < nGreedy) {
                AlgGA_04_15 gr = new AlgGA_04_15(inst, "5", semillas, posSemilla);
                poblacion[i] = gr.solucion();
            } else {
                poblacion[i] = generarAleatorio();
            }
            costes[i] = AlgGE_04_15.calcularCoste(inst.F, inst.D, poblacion[i]);
            evaluaciones++;
        }

        int[] mejor = Arrays.copyOf(poblacion[0], inst.n);
        double mejorCoste = costes[0];

        // Mejor de la población inicial
        for (int i = 1; i < tamPoblacion; i++) {
            if (costes[i] < mejorCoste) {
                mejorCoste = costes[i];
                mejor = Arrays.copyOf(poblacion[i], inst.n);
            }
        }

        System.out.println(">>> MEJOR INICIAL (Gen 0): " + String.format("%.0f", mejorCoste));
        System.out.println("------------------------------------------------------------");

        int generacion = 1;
        solucionActual = Arrays.copyOf(mejor, inst.n);

        while (evaluaciones < maxEvaluaciones) {

            // === SISTEMA DE DISPARADOR TABÚ ===
            if (evaluaciones >= siguienteDisparoTabu) {
                System.out.println("\n############################################################");
                System.out.println("### DISPARADOR MEMÉTICO ACTIVADO: Evaluación " + siguienteDisparoTabu);
                System.out.println("### Aplicando Búsqueda Tabú sobre el ÉLITE actual");
                System.out.println("### Iteraciones Tabú: " + iteracionesTabu);
                System.out.println("### Coste Élite antes de Tabú: " + String.format("%.0f", mejorCoste));

                boolean mejorado = busquedaTabuModificada(solucionActual, iteracionesTabu);

                if (!mejorado) {
                    solucionActual = Arrays.copyOf(solucionAnterior, solucionAnterior.length);
                    busquedaTabuModificada(solucionActual, iteracionesTabu);
                }

                double nuevoCoste = AlgGE_04_15.calcularCoste(inst.F, inst.D, solucionActual);

                if (nuevoCoste < mejorCoste) {
                    System.out.println(">>> ¡ÉXITO! El individuo ha mejorado.");
                    System.out.println("    Coste Anterior: " + String.format("%.0f", mejorCoste) +
                            " -> Nuevo Coste: " + String.format("%.0f", nuevoCoste));
                    mejor = Arrays.copyOf(solucionActual, inst.n);
                    mejorCoste = nuevoCoste;
                } else {
                    System.out.println(">>> El individuo NO ha mejorado tras la búsqueda local.");
                    System.out.println("    Se mantiene el coste: " + String.format("%.0f", mejorCoste));
                }

                System.out.println("############################################################\n");
                siguienteDisparoTabu += intervaloTabu;
            }

            // === Evolutivo: cruces y mutaciones ===
            int[][] nuevaPoblacion = new int[tamPoblacion][];
            double[] nuevosCostes = new double[tamPoblacion];

            for (int i = 0; i < tamPoblacion; i++) {
                int[] p1 = torneo(poblacion, costes, kBest);
                int[] p2 = torneo(poblacion, costes, kBest);

                int[] hijo = Arrays.copyOf(p1, inst.n);

                if (rand.nextDouble() < probCruce) {
                    if (operadorCruce.equalsIgnoreCase("OX2")) {
                        ox2(hijo, p2);
                    }
                    // si quieres otros operadores, se pueden añadir aquí
                }

                if (rand.nextDouble() < probMutacion) {
                    mutacion2Opt(hijo);
                }

                nuevaPoblacion[i] = hijo;
                nuevosCostes[i] = AlgGE_04_15.calcularCoste(inst.F, inst.D, hijo);
                evaluaciones++;
                if (evaluaciones >= maxEvaluaciones) break;
            }

            // Reemplazar el peor con el mejor global (elitismo)
            int peorIdx = 0;
            double peorCoste = nuevosCostes[0];
            for (int k = 1; k < tamPoblacion; k++) {
                if (nuevaPoblacion[k] == null) break;
                if (nuevosCostes[k] > peorCoste) {
                    peorCoste = nuevosCostes[k];
                    peorIdx = k;
                }
            }
            nuevaPoblacion[peorIdx] = Arrays.copyOf(mejor, inst.n);
            nuevosCostes[peorIdx] = mejorCoste;

            poblacion = nuevaPoblacion;
            costes = nuevosCostes;

            // Actualizar mejor global
            for (int i = 0; i < tamPoblacion; i++) {
                if (poblacion[i] != null && costes[i] < mejorCoste) {
                    mejorCoste = costes[i];
                    mejor = Arrays.copyOf(poblacion[i], inst.n);
                }
            }

            System.out.println("Gen: " + String.format("%-4d", generacion) +
                    " | Evals: " + String.format("%-5d", evaluaciones) +
                    " | Mejor Global: " + String.format("%.0f", mejorCoste));

            generacion++;
        }

        System.out.println("\n************************************************************");
        System.out.println("* FIN DE EJECUCIÓN (MGEN)");
        System.out.println("* Evaluaciones Totales: " + evaluaciones);
        System.out.println("* Coste Final: " + String.format("%.0f", mejorCoste));
        System.out.println("************************************************************");

        return mejor;
    }

    private int[] generarAleatorio() {
        int n = inst.n;
        int[] perm = new int[n];
        for (int i = 0; i < n; i++) perm[i] = i;
        for (int i = n - 1; i > 0; i--) {
            int j = rand.nextInt(i + 1);
            int tmp = perm[i];
            perm[i] = perm[j];
            perm[j] = tmp;
        }
        return perm;
    }

    private int[] torneo(int[][] poblacion, double[] costes, int k) {
        int bestIdx = rand.nextInt(poblacion.length);
        for (int i = 1; i < k; i++) {
            int idx = rand.nextInt(poblacion.length);
            if (costes[idx] < costes[bestIdx]) bestIdx = idx;
        }
        return Arrays.copyOf(poblacion[bestIdx], inst.n);
    }

    private void ox2(int[] hijo1, int[] hijo2) {
        int n = hijo1.length;
        int i1 = rand.nextInt(n);
        int i2 = rand.nextInt(n);
        if (i1 > i2) { int t = i1; i1 = i2; i2 = t; }

        Set<Integer> sub1 = new HashSet<>();
        for (int i = i1; i <= i2; i++) sub1.add(hijo1[i]);

        int[] nuevo = new int[n];
        Arrays.fill(nuevo, -1);
        int idx = 0;

        for (int val : hijo2) {
            if (!sub1.contains(val)) {
                while (idx >= i1 && idx <= i2) idx++;
                if (idx < n) nuevo[idx++] = val;
            }
        }
        for (int i = i1; i <= i2; i++) nuevo[i] = hijo1[i];
        System.arraycopy(nuevo, 0, hijo1, 0, n);
    }

    private void mutacion2Opt(int[] ind) {
        int n = ind.length;
        int i = rand.nextInt(n);
        int j = rand.nextInt(n);
        int tmp = ind[i];
        ind[i] = ind[j];
        ind[j] = tmp;
    }

    private boolean busquedaTabuModificada(int[] solucion, int iteracionesTabu) {
        int n = inst.n;
        int[] mejorSolucionLocal = Arrays.copyOf(solucion, n);
        double mejorCosteLocal = AlgGE_04_15.calcularCoste(inst.F, inst.D, mejorSolucionLocal);
        boolean mejora = false;

        Move[] listaTabu = new Move[tenenciaTabu];
        int punteroTabu = 0;

        for (int iter = 0; iter < iteracionesTabu; iter++) {

            long mejorDelta = Long.MAX_VALUE;
            int bestI = -1;
            int bestJ = -1;
            boolean movimientoEncontrado = false;

            for (int i = 0; i < n; i++) {
                for (int j = i + 1; j < n; j++) {

                    long delta = costeDelta2Opt(solucionActual, i, j);
                    boolean esTabu = esTabu(listaTabu, i, j);

                    double costeVecinoEstimado = AlgGE_04_15.calcularCoste(inst.F, inst.D, solucionActual) + delta;

                    if (!esTabu || (costeVecinoEstimado < mejorCosteLocal)) {
                        if (delta < mejorDelta) {
                            mejorDelta = delta;
                            bestI = i;
                            bestJ = j;
                            movimientoEncontrado = true;
                        }
                    }
                }
            }

            if (movimientoEncontrado) {
                solucionAnterior = Arrays.copyOf(solucionActual, solucionActual.length);
                mejora = true;
                aplicar2Opt(solucionActual, bestI, bestJ);

                if (listaTabu[punteroTabu] == null) listaTabu[punteroTabu] = new Move();
                listaTabu[punteroTabu].i = bestI;
                listaTabu[punteroTabu].j = bestJ;
                punteroTabu = (punteroTabu + 1) % tenenciaTabu;

                double costeActual = AlgGE_04_15.calcularCoste(inst.F, inst.D, solucionActual);
                if (costeActual < mejorCosteLocal) { mejorCosteLocal = costeActual; }
            } else { break; }
        }
        System.out.println("    [Tabú] Búsqueda finalizada. Mejor coste local encontrado: " + String.format("%.0f", mejorCosteLocal));
        return mejora;
    }

    private boolean esTabu(Move[] listaTabu, int i, int j) {
        if (i > j) { int t = i; i = j; j = t; }
        for (Move m : listaTabu) {
            if (m != null && m.i == i && m.j == j) return true;
        }
        return false;
    }

    private void aplicar2Opt(int[] vector, int i, int j) {
        int temp = vector[i];
        vector[i] = vector[j];
        vector[j] = temp;
    }

    private long costeDelta2Opt(int[] vector, int i, int j) {
        long delta = 0;
        int n = vector.length;
        for (int k = 0; k < n; k++) {
            if (k != i && k != j) {
                delta += (long) inst.F[i][k] * (inst.D[vector[j]][vector[k]] - inst.D[vector[i]][vector[k]]);
                delta += (long) inst.F[j][k] * (inst.D[vector[i]][vector[k]] - inst.D[vector[j]][vector[k]]);
                delta += (long) inst.F[k][i] * (inst.D[vector[k]][vector[j]] - inst.D[vector[k]][vector[i]]);
                delta += (long) inst.F[k][j] * (inst.D[vector[k]][vector[i]] - inst.D[vector[k]][vector[j]]);
            }
        }
        return delta;
    }
}
