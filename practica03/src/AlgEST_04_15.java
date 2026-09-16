import java.util.*;

public class AlgEST_04_15 {

    private Instance inst;
    private String[] semillas;
    private int posSemilla;

    private int kBest;
    private int kWorst;
    private String operadorCruce;
    private double probMutacion;
    private int maxEvaluaciones;
    private int tamPoblacion;
    private double probGreedy;

    private Random rand;

    public AlgEST_04_15(
            Instance inst,
            int[] solucionInicial,
            String[] semillas,
            int posSemilla
    ) {
        this.inst = inst;
        this.semillas = semillas;
        this.posSemilla = posSemilla;
    }

    public void setParametrosEvolutivos(int kBest, int kWorst, String operadorCruce,
                                        double probMutacion, int maxEvaluaciones, int tamPoblacion, double probGreedy) {
        this.kBest = kBest;
        this.kWorst = kWorst;
        this.operadorCruce = operadorCruce;
        this.probMutacion = probMutacion;
        this.maxEvaluaciones = maxEvaluaciones;
        this.tamPoblacion = tamPoblacion;
        this.probGreedy = probGreedy;
    }

    public int[] ejecutar() throws Exception {

        rand = new Random(Long.parseLong(semillas[posSemilla]));

        System.out.println("\n==============================");
        System.out.println("   GENERACIÓN 0 (INICIAL)");
        System.out.println("==============================");

        int[][] poblacion = new int[tamPoblacion][];
        double[] costes = new double[tamPoblacion];
        int evaluaciones = 0;

        int nGreedy = (int) (tamPoblacion * probGreedy);

        for (int i = 0; i < tamPoblacion; i++) {

            System.out.println("\n-- Creando individuo inicial " + i);

            if (i < nGreedy) {
                System.out.println("   Método = GREEDY");
                AlgGA_04_15 gr = new AlgGA_04_15(inst, "5", semillas, posSemilla);
                poblacion[i] = gr.solucion();
            } else {
                System.out.println("   Método = ALEATORIO");
                poblacion[i] = generarAleatorio();
            }

            costes[i] = AlgGE_04_15.calcularCoste(inst.F, inst.D, poblacion[i]);
            evaluaciones++;

            System.out.println("   Individuo = " + Arrays.toString(poblacion[i]));
            System.out.println("   Coste = " + costes[i]);
        }

        int[] mejor = Arrays.copyOf(poblacion[0], poblacion[0].length);
        double mejorCoste = costes[0];

        for (int i = 1; i < tamPoblacion; i++) {
            if (costes[i] < mejorCoste) {
                mejorCoste = costes[i];
                mejor = Arrays.copyOf(poblacion[i], poblacion[i].length);
            }
        }

        System.out.println("\n>> Mejor inicial = " + Arrays.toString(mejor) + " | coste = " + mejorCoste);

        while (evaluaciones < maxEvaluaciones) {

            System.out.println("\n======================================");
            System.out.println("   NUEVA GENERACIÓN (ESTACIONARIO)");
            System.out.println("======================================");

            System.out.println("-- Seleccionando padres via torneo");

            int[] p1 = torneo(poblacion, costes, kBest);
            int[] p2 = torneo(poblacion, costes, kBest);

            System.out.println("   Padre1 = " + Arrays.toString(p1));
            System.out.println("   Padre2 = " + Arrays.toString(p2));

            int[] hijo1 = Arrays.copyOf(p1, p1.length);
            int[] hijo2 = Arrays.copyOf(p2, p2.length);

            System.out.println("\n-- CRUCE aplicado (" + operadorCruce + ")");

            if (operadorCruce.equalsIgnoreCase("OX2")) {
                System.out.println("   >> OX2 antes:");
                System.out.println("      H1 = " + Arrays.toString(hijo1));
                System.out.println("      H2 = " + Arrays.toString(hijo2));
                ox2(hijo1, hijo2);
                System.out.println("   >> OX2 después:");
                System.out.println("      H1 = " + Arrays.toString(hijo1));
            } else {
                System.out.println("   >> MOC antes:");
                System.out.println("      H1 = " + Arrays.toString(hijo1));
                System.out.println("      H2 = " + Arrays.toString(hijo2));
                moc(hijo1, hijo2);
                System.out.println("   >> MOC después:");
                System.out.println("      H1 = " + Arrays.toString(hijo1));
            }

            System.out.println("\n-- MUTACIONES");

            if (rand.nextDouble() < probMutacion) {
                System.out.println("   Mutación en hijo1");
                System.out.println("      Antes = " + Arrays.toString(hijo1));
                mutacion2Opt(hijo1);
                System.out.println("      Después = " + Arrays.toString(hijo1));
            }

            if (rand.nextDouble() < probMutacion) {
                System.out.println("   Mutación en hijo2");
                System.out.println("      Antes = " + Arrays.toString(hijo2));
                mutacion2Opt(hijo2);
                System.out.println("      Después = " + Arrays.toString(hijo2));
            }

            double costeHijo1 = AlgGE_04_15.calcularCoste(inst.F, inst.D, hijo1);
            double costeHijo2 = AlgGE_04_15.calcularCoste(inst.F, inst.D, hijo2);
            evaluaciones += 2;

            System.out.println("\n-- Evaluación hijos");
            System.out.println("   Hijo1 = " + Arrays.toString(hijo1) + " | coste = " + costeHijo1);
            System.out.println("   Hijo2 = " + Arrays.toString(hijo2) + " | coste = " + costeHijo2);

            System.out.println("\n-- Reemplazo (torneo de perdedores)");

            int idx1 = torneoPerdedores(poblacion, costes, kWorst);
            System.out.println("   Reemplazando individuo en pos " + idx1 + " con hijo1");
            System.out.println("   Antes = " + Arrays.toString(poblacion[idx1]));
            poblacion[idx1] = Arrays.copyOf(hijo1, hijo1.length);
            costes[idx1] = costeHijo1;
            System.out.println("   Después = " + Arrays.toString(poblacion[idx1]));

            int idx2 = torneoPerdedores(poblacion, costes, kWorst);
            System.out.println("   Reemplazando individuo en pos " + idx2 + " con hijo2");
            System.out.println("   Antes = " + Arrays.toString(poblacion[idx2]));
            poblacion[idx2] = Arrays.copyOf(hijo2, hijo2.length);
            costes[idx2] = costeHijo2;
            System.out.println("   Después = " + Arrays.toString(poblacion[idx2]));

            if (costeHijo1 < mejorCoste) {
                mejor = Arrays.copyOf(hijo1, hijo1.length);
                mejorCoste = costeHijo1;
            }
            if (costeHijo2 < mejorCoste) {
                mejor = Arrays.copyOf(hijo2, hijo2.length);
                mejorCoste = costeHijo2;
            }

            System.out.println("\n>> Mejor actual = " + Arrays.toString(mejor) + " | coste = " + mejorCoste);
        }

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
        return Arrays.copyOf(poblacion[bestIdx], poblacion[bestIdx].length);
    }

    private int torneoPerdedores(int[][] poblacion, double[] costes, int k) {
        int worstIdx = rand.nextInt(poblacion.length);
        for (int i = 1; i < k; i++) {
            int idx = rand.nextInt(poblacion.length);
            if (costes[idx] > costes[worstIdx]) worstIdx = idx;
        }
        return worstIdx;
    }

    private void ox2(int[] hijo1, int[] hijo2) {

        System.out.println("      >> OX2");

        int n = hijo1.length;
        int i1 = rand.nextInt(n);
        int i2 = rand.nextInt(n);
        if (i1 > i2) {
            int t = i1;
            i1 = i2;
            i2 = t;
        }

        System.out.println("         Segmento [" + i1 + "," + i2 + "]");

        Set<Integer> sub1 = new HashSet<>();
        for (int i = i1; i <= i2; i++) sub1.add(hijo1[i]);

        int[] nuevo = new int[n];
        Arrays.fill(nuevo, -1);
        int idx = 0;

        for (int val : hijo2) {
            if (!sub1.contains(val)) {
                while (idx >= i1 && idx <= i2) idx++;
                if (idx >= n) break;
                nuevo[idx++] = val;
            }
        }

        for (int i = i1; i <= i2; i++) nuevo[i] = hijo1[i];

        System.out.println("         OX2 -> " + Arrays.toString(nuevo));

        System.arraycopy(nuevo, 0, hijo1, 0, n);
    }

    private void moc(int[] h1, int[] h2) {

        System.out.println("      >> MOC");

        int n = h1.length;
        int punto = rand.nextInt(n - 1) + 1;

        System.out.println("         Punto de corte = " + punto);

        Set<Integer> izq1 = new HashSet<>();
        for (int i = 0; i < punto; i++) izq1.add(h1[i]);

        int[] nuevo = new int[n];
        Arrays.fill(nuevo, -1);

        for (int i = 0; i < punto; i++) nuevo[i] = h1[i];

        int idx = punto;
        for (int val : h2) {
            if (!izq1.contains(val)) nuevo[idx++] = val;
        }

        System.out.println("         MOC -> " + Arrays.toString(nuevo));

        System.arraycopy(nuevo, 0, h1, 0, n);
    }

    private void mutacion2Opt(int[] ind) {
        int n = ind.length;
        int i = rand.nextInt(n);
        int j = rand.nextInt(n);

        System.out.println("      >> MUTACIÓN 2-OPT swap(" + i + "," + j + ")");
        System.out.println("         Antes = " + Arrays.toString(ind));

        int tmp = ind[i];
        ind[i] = ind[j];
        ind[j] = tmp;

        System.out.println("         Después = " + Arrays.toString(ind));
    }
}
