import java.util.Arrays;
import java.util.Random;

public class AlgBT_04_15 {

    private Instance inst;
    private int[] solucionActual;
    private int iteraciones;
    private int[] mejorSolucion;
    private int[] solucionAnterior;

    private int[] semillas;
    private Random rand;
    private int pos = 0;

    static class Move {  int i, j; }

    private int tenencia;
    Move[] listaTabu;
    int puntero = 0;

    private int[][] memoriaLarga;
    int n;
    int estancamiento = 50;

    public AlgBT_04_15(Instance inst, int[] solucion, String iteraciones, String tenencia, String []sem , int semilla) {
        this.inst = inst;
        this.solucionActual = solucion;
        this.iteraciones = Integer.parseInt(iteraciones);
        this.tenencia = Integer.parseInt(tenencia);
        this.listaTabu = new Move[this.tenencia];
        this.n = solucion.length;
        this.memoriaLarga = new int[n][n];
        this.solucionAnterior = solucion;
        this.semillas = new int[sem.length];
        this.pos = semilla;

        for ( int i = 0; i < sem.length; i++ ) this.semillas[i] = Integer.parseInt(sem[i]);

        rand = new Random(semillas[pos]);
    }

    /**
     * Metodo que comprueba si un valor es prohibido o no
     * @param i
     * @param j
     * @return
     */
    public boolean esTabu(int i, int j) {

        if ( i > j ){
            int temp = i;
            i = j;
            j = temp;
        }
        for (Move move : listaTabu) {
            if ( (move != null) && (move.i == i && move.j == j) ) return true;
        }
        return false;
    }

    /**
     * Metodo que introduce un valor en el vector de la memoria a corto plazo
     * @param i
     * @param j
     */
    public void pushTabu(int i, int j) {

        if ( i > j){

            int temp =  i;
            i = j;
            j = temp;
        }
        if (listaTabu[puntero] == null) {

            listaTabu[puntero] = new Move();
        }
        listaTabu[puntero].i = i;
        listaTabu[puntero].j = j;
        puntero = (puntero + 1) % tenencia;
    }

    /**
     * Metodo que actualiza la memoria a largo plazo
     * @param solucion
     */
    void actualizarMemoria(int[] solucion) {

        for (int u = 0; u < solucion.length; u++) {
            int dept = solucion[u];
            memoriaLarga[dept][u]++;
        }
    }

    /**
     * Metodo que busca los valores de las soluciones más repetidas
     * @return
     */
    private int[] intensificar() {
        int[] nuevaSol = new int[n];
        boolean[] ocupada = new boolean[n];
        Arrays.fill(nuevaSol, -1);
        Arrays.fill(ocupada, false);

        for (int dept = 0; dept < n; dept++) {
            int mejorPos = -1;
            int max = -1;

            // buscamos la localización donde más veces ha estado este dept
            for (int loc = 0; loc < n; loc++) {
                if (!ocupada[loc] && memoriaLarga[dept][loc] > max) {
                    max = memoriaLarga[dept][loc];
                    mejorPos = loc;
                }
            }

            if (mejorPos != -1) {
                nuevaSol[mejorPos] = dept;
                ocupada[mejorPos] = true;
            }
        }

        return nuevaSol;
    }

    /**
     * Metodo que busca los valores de las soluciones menos repetidas
     * @return
     */
    private int[] diversificar() {
        int[] nuevaSol = new int[n];
        boolean[] ocupada = new boolean[n];
        Arrays.fill(nuevaSol, -1);
        Arrays.fill(ocupada, false);

        for (int dept = 0; dept < n; dept++) {
            int peorPos = -1;
            int min = Integer.MAX_VALUE;

            for (int loc = 0; loc < n; loc++) {
                if (!ocupada[loc] && memoriaLarga[dept][loc] < min) {
                    min = memoriaLarga[dept][loc];
                    peorPos = loc;
                }
            }

            if (peorPos != -1) {
                nuevaSol[peorPos] = dept;
                ocupada[peorPos] = true;
            }
        }

        return nuevaSol;
    }

    /**
     * Metodo de la busqueda local modificado para actualizar las memorias buscando un mejor valor
     * @param solucion
     * @param dlb
     * @return
     */
    public boolean ejecutarBusqueda(int [] solucion, boolean [] dlb) {

        boolean mejora;

        System.out.println("===== INICIO BUSQUEDA LOCAL (FIRST IMPROVEMENT + DLB) =====");
        System.out.println("------------------------------------------------------------");

        do {
            mejora = false;
            int mejorasIteracion = 0;

            System.out.println("Estado DLB inicial: " + Arrays.toString(dlb));

            for (int i = 0; i < n; i++) {
                if (dlb[i]) {
                    System.out.println(" -> Posición i = " + i + " del vector -> DLB=true, se salta.");
                    continue;
                }

                boolean improve_flag = false;
                System.out.println(" -> Posición i = " + i + " del vector -> DLB=false, explorando vecinos...");
                System.out.println("  - Comparando con la posición: ");

                for (int j = i + 1; j < n; j++) {

                    if ( esTabu(i, j) ) {
                        System.out.println("    Movimiento " + i + " y " + j + " es tabu y ha sido rechazado. ");
                        continue;
                    }
                    long delta = costeDelta2Opt(solucion,i, j);
                    System.out.printf("    j=%d -> delta=%d%n", j, delta);

                    if (delta < 0) { // mejora

                        solucionAnterior = Arrays.copyOf(solucion, solucion.length); // Segunda mejor solución

                        aplicar2Opt(solucion,i, j);  // Actualizamos memorias
                        pushTabu(i, j);
                        actualizarMemoria(solucion);

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
            System.out.println("  - Mejoras aplicadas: " + mejorasIteracion);
            System.out.println("  - Estado DLB final: " + Arrays.toString(dlb));

        } while (mejora);

        System.out.println("\n===== FIN DE LA BUSQUEDA LOCAL =====");
        System.out.println("DLB final: " + Arrays.toString(dlb));
        System.out.println("Optimo local alcanzado.");

        return mejora;
    }

    /**
     * Calcula la variacion de coste al intercambiar dos posiciones (2-opt).
     */
    private long costeDelta2Opt(int [] vector, int i, int j) {
        long delta = 0;
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

    /**
     * Intercambia las posiciones i y j en el vector que estemos utilizando
     */
    private void aplicar2Opt (int [] vector, int i, int j){
        int temp = vector[i];
        vector[i] = vector[j];
        vector[j] = temp;
    }


    /**
     * Metodo que ejecuta el algoritmo Tabú llamando asi a la búsqueda local, intensificación o diversificación
     * @return
     */
    public int[] ejecucionTabu(){

        int sinMejoraGlobal = 0;
        long mejorCosteGlobal = AlgGE_04_15.calcularCoste(inst.F,inst.D, solucionActual); // empieza siendo el coste global
        mejorSolucion = Arrays.copyOf(solucionActual, solucionActual.length);
        int ite = 0;
        boolean [] dlb = new boolean[n];
        Arrays.fill(dlb, false);

        while ( ite < iteraciones ) {

            boolean hubomejora = ejecutarBusqueda(solucionActual,dlb);

            long costeActual = AlgGE_04_15.calcularCoste(inst.F,inst.D, solucionActual);
            if (costeActual < mejorCosteGlobal) {
                mejorCosteGlobal = costeActual;
                mejorSolucion = Arrays.copyOf(solucionActual, solucionActual.length);
                sinMejoraGlobal = 0;
            } else {
                sinMejoraGlobal++;
            }

            if ( !hubomejora ){
                System.out.println("    La solución actual empeora, toma la anterior y vuelve a buscar");
                System.out.println("    Iteraciones seguidas sin mejora en la solución: " + sinMejoraGlobal);
                solucionActual = Arrays.copyOf(solucionAnterior, solucionAnterior.length);
            }

            System.out.println("Coste de la solución actual: "+ costeActual);

            // ---- (4) Oscilación estratégica ----
            if (sinMejoraGlobal >= estancamiento) {

                if (rand.nextDouble() < 0.5) {
                    solucionActual = intensificar();  // intensificar
                    System.out.println("Tras 50 iteraciones sin mejorar, buscamos intensificar..");
                } else {
                    solucionActual = diversificar(); // diversificar
                    System.out.println("Tras 50 iteraciones sin mejorar, buscamos intensificar..");
                }

                Arrays.fill(dlb, false);
                Arrays.fill(listaTabu, null);
                puntero = 0;
                sinMejoraGlobal = 0;
            }
            ite++;
        }

        return mejorSolucion;
    }
}
