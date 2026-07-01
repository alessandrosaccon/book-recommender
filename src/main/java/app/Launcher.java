package app;

/**
 * Classe "trampolino" per avviare l'app JavaFX da un fat-jar.
 * Questa classe NON estende Application.
 * 
 * @author Broggini Luca
 * @author De Nicola Matteo
 * @author Pianezzola Andrea
 * @author Saccon Alessandro
 * @author Siragusa Valerio
 * @version 1.0
 */
public class Launcher {

    /**
     * Costruttore vuoto.
     */
    public Launcher(){}

    /**
     * Chiama il vero main dell'applicazione
     * @param args parametro della classe Main
     */
    public static void main(String[] args) {
        Bookapp.main(args);
    }
}