import java.io.*;
import java.util.*;

public class Instance {
    int n;          // número de departamentos/localizaciones
    int[][] F;      // matriz de flujos
    int[][] D;      // matriz de distancias


    // Constructor: recibe ruta y carga todo

    public Instance(String filePath) throws IOException {

        List<String> contenido = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                String t = line.trim();
                if (!t.isEmpty()) contenido.add(t);
            }
        }
        if (contenido.isEmpty())
            throw new IllegalArgumentException("El archivo está vacío o mal formateado.");

        int idx = 0;
        n = Integer.parseInt(contenido.get(idx++));
        F = new int[n][n];
        D = new int[n][n];

        // Leer F
        for (int i = 0; i < n; i++) {
            int[] fila = parseRow(contenido.get(idx++), n);
            System.arraycopy(fila, 0, F[i], 0, n);
        }
        // Leer D
        for (int i = 0; i < n; i++) {
            int[] fila = parseRow(contenido.get(idx++), n);
            System.arraycopy(fila, 0, D[i], 0, n);
        }
    }

    private int[] parseRow(String line, int n) {
        String[] partes = line.trim().split("\\s+");
        if (partes.length != n)
            throw new IllegalArgumentException("Fila con " + partes.length + " columnas; se esperaban " + n);
        int[] filas = new int[n];
        for (int i = 0; i < n; i++) filas[i] = Integer.parseInt(partes[i]);
        return filas;
    }

    // Método para mostrar matrices
    public void print() {
        System.out.println("n = " + n);
        System.out.println("\nMatriz F:");
        printMatrix(F);
        System.out.println("\nMatriz D:");
        printMatrix(D);
    }

    private void printMatrix(int[][] M) {
        for (int[] filas : M) {
            for (int val : filas) System.out.print(val + " ");
            System.out.println();
        }
    }
}
