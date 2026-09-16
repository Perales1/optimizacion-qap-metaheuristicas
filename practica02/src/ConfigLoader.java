import java.io.*;
import java.util.*;

public class ConfigLoader {

    private String archivo;
    private String algoritmo;
    private String parametro;
    private String[] semillas;
    private String iteraciones;
    private String tenencia;
    private int posSemilla;
    private int elitismo;
    private int kBest;
    private int kWorst;
    private String operadorCruce;
    private double probCruce;
    private double probMutacion;
    private int evaluaciones;
    private int tamPoblacion;
    private double probGreedy;

    public ConfigLoader(String configPath) throws IOException {
        Properties props = new Properties();

        try (BufferedReader br = new BufferedReader(new FileReader(configPath))) {

            // Ignorar líneas que empiezan por "#" (comentarios)
            StringBuilder sb = new StringBuilder();
            String line;

            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) continue;
                sb.append(line).append("\n");
            }

            // Este metodo separa los valores que se encuentran en el archivo separados por un '=' o incluso por un ':'
            // Los de la izquierda son la clave y los de la derecha el valor

            props.load(new StringReader(sb.toString()));
        }

        // Sacamos los valores de la clave
        archivo = props.getProperty("archivo");
        algoritmo = props.getProperty("algoritmo");
        parametro = props.getProperty("k");
        semillas = props.getProperty("semillas").split(",");
        iteraciones = props.getProperty("iteraciones");
        tenencia = props.getProperty("tenencia");
        posSemilla = Integer.parseInt(props.getProperty("posSemilla"));
        elitismo = Integer.parseInt(props.getProperty("elitismo", "1"));
        kBest = Integer.parseInt(props.getProperty("kBest", "2"));
        kWorst = Integer.parseInt(props.getProperty("kWorst", "3"));
        operadorCruce = props.getProperty("operadorCruce", "OX2");
        probCruce = Double.parseDouble(props.getProperty("probCruce", "0.7"));
        probMutacion = Double.parseDouble(props.getProperty("probMutacion", "0.1"));
        evaluaciones = Integer.parseInt(props.getProperty("evaluaciones", "50000"));
        tamPoblacion = Integer.parseInt(props.getProperty("tamPoblacion"));
        probGreedy = Double.parseDouble(props.getProperty("probGreedy", "0.5"));

        if (archivo == null || algoritmo == null || parametro == null || semillas == null || iteraciones == null) {
            throw new IllegalArgumentException("El archivo de configuración debe tener 'archivo' y 'algoritmo'");
        }
    }

    public String getArchivo() {
        return archivo;
    }

    public String getAlgoritmo() {
        return algoritmo;
    }

    public String getParametro() {
        return parametro;
    }

    public String[] getSemillas() {
        return semillas;
    }

    public String getIteraciones() {
        return iteraciones;
    }

    public String getTenencia() {
        return tenencia;
    }

    public int getposSemilla() {
        return posSemilla;
    }

    public int getElitismo() {
        return elitismo;
    }

    public int getKBest() {
        return kBest;
    }

    public int getKWorst() {
        return kWorst;
    }

    public String getOperadorCruce() {
        return operadorCruce;
    }

    public double getProbCruce() {
        return probCruce;
    }

    public double getProbMutacion() {
        return probMutacion;
    }

    public int getEvaluaciones() {
        return evaluaciones;
    }

    public int getTamPoblacion() {
        return tamPoblacion;
    }

    public double getProbGreedy() {
        return probGreedy;
    }

    public void printConfig() {
        System.out.println("Archivo de datos: " + archivo);
        System.out.println("Algoritmo: " + algoritmo);
        System.out.println("k (parametro): " + parametro);
        System.out.println("Semillas: " + Arrays.toString(semillas));
        System.out.println("Iteraciones: " + iteraciones);
        System.out.println("Tenencia: " + tenencia);
        System.out.println("Elitismo: " + elitismo);
        System.out.println("kBest: " + kBest + " | kWorst: " + kWorst);
        System.out.println("OperadorCruce: " + operadorCruce);
        System.out.println("ProbCruce: " + probCruce + " | ProbMutación: " + probMutacion);
        System.out.println("Evaluaciones: " + evaluaciones);
        System.out.println("tamPoblacion: " + tamPoblacion);
        System.out.println("probGreedy: " + probGreedy);
    }
}