import java.util.Arrays;
import java.util.Random;


public class AlgGA_04_15 {

    Instance objeto;
    String parametro;
    int [] semillas;
    int ejecucion;

    public AlgGA_04_15(Instance objeto, String parametro, String[] semillas, int semilla) {
        this.objeto = objeto;
        this.parametro = parametro;
        this.semillas = new int [semillas.length];
        this.ejecucion = semilla;

        for ( int i = 0; i < semillas.length; i++ ) this.semillas[i] = Integer.parseInt(semillas[i]);

        //for ( int i = 0; i < semillas.length; i++){
          //  System.out.println(this.semillas[i]);
        //}
    }

    public int[] solucion() throws Exception{

        int n;
        n = objeto.n;

        // Los departamentos más importantes se calculan de mayor a menor
        Integer[] importancia = calculaMayorImportancia(objeto.F);

        // Las localizaciones más céntricas se calculan de menor a mayor
        Integer [] distancias = calculaMenorDistancia(objeto.D);

        int[] devolver = new int[n];

        devolver = tomaRandom(importancia,distancias);

        return devolver;
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

    public int[] tomaRandom(Integer[] importancias, Integer[] distancias){

        Random []random = new Random[semillas.length];
        for ( int i = 0; i < semillas.length; i++){
            random[i] = new Random(semillas[i]);
        }
        int posicionI,posicionD = 0;
        int dep, loc = 0;

        int [] solucion = new int[importancias.length];
        int tam = importancias.length;

        while (tam != 0){

            posicionI = random[ejecucion].nextInt(Integer.parseInt(parametro));
            posicionD = random[ejecucion].nextInt(Integer.parseInt(parametro));

            if ( posicionI < importancias.length && posicionD < distancias.length){

                dep = importancias[posicionI];
                loc =  distancias[posicionD];

                importancias = nuevoVector(importancias,posicionI);
                distancias = nuevoVector(distancias,posicionD);
                solucion[dep] = loc;
                tam--;
            }
        }

        return solucion;
    }

    public Integer[] nuevoVector(Integer[] vector, int pos){

        Integer[] nuevo = new Integer[vector.length - 1];

        if ( pos != vector.length - 1){ vector[pos] =  vector[vector.length - 1]; }

        for ( int i = 0; i < nuevo.length; i++){ nuevo[i] = vector[i]; }

        return nuevo;
    }
}