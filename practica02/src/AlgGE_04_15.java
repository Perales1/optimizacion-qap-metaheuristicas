import java.util.Arrays;

public class AlgGE_04_15 {

    private Instance objeto;


    public AlgGE_04_15(Instance objeto) {
        this.objeto = objeto;
    }

    public int[] solucion() throws Exception {

        int n;
        n = objeto.n;

        // Los departamentos más importantes se calculan de mayor a menor
        Integer[] importancia = calculaMayorImportancia(objeto.F);


        System.out.println("Valores de la importancia");
        for (int i = 0; i < n; i++) {
            System.out.println( importancia[i]);
        }
        // Las localizaciones más céntricas se calculan de menor a mayor
        Integer [] distancias = calculaMenorDistancia(objeto.D);

        System.out.println("Valores de la distancia");
        for (int i = 0; i < n; i++) {
            System.out.println( distancias[i]);
        }

        // En cada posición del vector solución tendremos, en la posición i del vector la el departamento y su valor
        // será la localización correspondiente

        int[] S = new int[n];
        for (int k = 0; k < n; k++) {
            int dept = importancia[k];
            int loc  = distancias[k];
            S[dept] = loc;
        }

        System.out.println("El coste total es: " + calcularCoste(objeto.F,objeto.D,S) );

        return S;
    }

    public static long calcularCoste(int[][] F, int[][] D, int[] S) {
        int n = F.length;
        long coste = 0;

        for (int i = 0; i < n; i++) {
            int locI = S[i]; // localización del depto i
            for (int j = 0; j < n; j++) {
                int locJ = S[j]; // localización del depto j
                coste += (long) F[i][j] * (long) D[locI][locJ];
            }
        }

        return coste;
    }

    private Integer[] calculaMayorImportancia(int [][] flujos) throws Exception {

        if (flujos.length == 0)
            throw new Exception("El tamaño de la matriz de flujos no puede ser nulo");

        long [] resultado = new long[flujos.length];
        int fila = 0;
        int columna = 0;

        for ( int i = 0; i < flujos.length; i++){
            for ( int j = 0; j < flujos[i].length; j++){

                fila += flujos[i][j];
                columna += flujos[j][i];
            }
            resultado[i] = fila + columna;
            fila = columna = 0;
        }

        Integer[] indices = new Integer[resultado.length];
        for (int i = 0; i < indices.length; i++) {
            indices[i] = i; // inicializar con 0,1,2,3
        }

        // ordenar por los valores de importancia, de mayor a menor
        Arrays.sort(indices, (a, b) -> Long.compare(resultado[b], resultado[a]));

        return indices;
    }

    private Integer[] calculaMenorDistancia(int [][] flujos) throws Exception {

        if (flujos.length == 0)
            throw new Exception("El tamaño de la matriz de flujos no puede ser nulo");

        long [] resultado = new long[flujos.length];
        int suma = 0;

        for ( int i = 0; i < flujos.length; i++){
            for ( int j = 0; j < flujos[i].length; j++){

                suma += flujos[i][j];
            }
            resultado[i] = suma;
            suma = 0;
        }

        Integer[] indices = new Integer[resultado.length];
        for (int i = 0; i < indices.length; i++) {
            indices[i] = i;
        }
        Arrays.sort(indices, (a, b) -> Long.compare(resultado[a], resultado[b]));

        return indices;
    }

}
