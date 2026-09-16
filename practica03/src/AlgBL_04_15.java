import java.util.Arrays;

public class AlgBL_04_15 {

    private Instance inst;
    private int[] solucion;
    private int n;
    private int iteraciones;

    public AlgBL_04_15(Instance inst, int[] solucionInicial, int iteraciones) {
        this.inst = inst;
        this.solucion = Arrays.copyOf(solucionInicial, solucionInicial.length);
        this.n = inst.n;
        this.iteraciones = iteraciones;
    }

    /**
     * Busqueda local First Improvement con operador 2-opt y DLB.
     * Log analitico para seguimiento de cada movimiento.
     */
    public int[] ejecutarBusqueda() {
        boolean[] dlb = new boolean[n];
        Arrays.fill(dlb, false);

        int iter = 0;
        boolean mejora;

        System.out.println("===== INICIO BUSQUEDA LOCAL (FIRST IMPROVEMENT + DLB) =====");
        System.out.println("Tamano problema: " + n + " | Iteraciones maximas: " + iteraciones);
        System.out.println("------------------------------------------------------------");

        do {
            mejora = false;
            int mejorasIteracion = 0;

            System.out.println("\n>>> Iteracion global " + (iter + 1));
            System.out.println("Estado DLB inicial: " + Arrays.toString(dlb));

            for (int i = 0; i < n; i++) {
                if (dlb[i]) {
                    System.out.println("  i=" + i + " -> DLB=true, se salta.");
                    continue;
                }

                boolean improve_flag = false;
                System.out.println("  i=" + i + " -> DLB=false, explorando vecinos...");

                for (int j = i + 1; j < n; j++) {
                    long delta = costeDelta2Opt(i, j);
                    System.out.printf("    j=%d -> delta=%d%n", j, delta);

                    if (delta < 0) { // mejora
                        aplicar2Opt(i, j);
                        dlb[i] = dlb[j] = false;
                        improve_flag = true;
                        mejora = true;
                        mejorasIteracion++;

                        System.out.printf("      * Mejora encontrada en (i=%d, j=%d), delta=%d -> se aplica 2-opt%n", i, j, delta);
                        break; // primer mejor
                    }
                }

                if (!improve_flag) {
                    dlb[i] = true;
                    System.out.println("    Ninguna mejora para i=" + i + ", marcando DLB=true");
                }
            }

            System.out.println("Iteracion global " + (iter + 1) + " completada");
            System.out.println("  - Mejoras aplicadas: " + mejorasIteracion);
            System.out.println("  - Mejora global: " + mejora);
            System.out.println("  - Estado DLB final: " + Arrays.toString(dlb));

            iter++;
        } while (mejora && iter < iteraciones);

        System.out.println("\n===== FIN DE LA BUSQUEDA =====");
        System.out.println("Iteraciones totales: " + iter);
        System.out.println("DLB final: " + Arrays.toString(dlb));
        System.out.println("Optimo local alcanzado.");

        return solucion;
    }

    /**
     * Calcula la variacion de coste al intercambiar dos posiciones (2-opt).
     */
    private long costeDelta2Opt(int i, int j) {
        long delta = 0;
        for (int k = 0; k < n; k++) {
            if (k != i && k != j) {
                delta += (long) inst.F[i][k] * (inst.D[solucion[j]][solucion[k]] - inst.D[solucion[i]][solucion[k]]);
                delta += (long) inst.F[j][k] * (inst.D[solucion[i]][solucion[k]] - inst.D[solucion[j]][solucion[k]]);
                delta += (long) inst.F[k][i] * (inst.D[solucion[k]][solucion[j]] - inst.D[solucion[k]][solucion[i]]);
                delta += (long) inst.F[k][j] * (inst.D[solucion[k]][solucion[i]] - inst.D[solucion[k]][solucion[j]]);
            }
        }
        return delta;
    }

    /**
     * Intercambia las posiciones i y j en la solucion actual (2-opt).
     */
    private void aplicar2Opt(int i, int j) {
        int temp = solucion[i];
        solucion[i] = solucion[j];
        solucion[j] = temp;
    }
}
