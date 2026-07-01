/* DE NICOLA MATTEO VINCENZO, MATRICOLA 757933, VA
    SIRAGUSA VALERIO, MATRICOLA 756227, VA
    SACCON ALESSANDRO, MATRICOLA 756145, VA
    PIANEZZOLA ANDREA, MATRICOLA 756141, VA
    BROGGINI LUCA, MATRICOLA 756446, VA
*/
package model;

import java.util.Scanner;
import java.io.IOException;
import java.net.*;
import java.sql.*;

import server.*;

/**
 * La classe SERVER_BOOK rappresenta il server principale dell'applicazione
 * di gestione e raccomandazione di libri. Si occupa di accettare connessioni
 * dai client, inizializzare il database e avviare i thread che gestiscono
 * le richieste.
 * <p>Il server utilizza PostgreSQL come database e ascolta il traffico in ingresso
 * sulla porta 8080 per impostazione predefinita.</p>
 * <p>Quando viene avviato, il server crea istanze delle principali classi del modello
 * come Libreria, Libro, Utente, Suggerimento, Valutazione e RepositoryLibri.</p>
 * @author Broggini Luca
 * @author De Nicola Matteo
 * @author Pianezzola Andrea
 * @author Saccon Alessandro
 * @author Siragusa Valerio
 * @version 1.0
 */
public class SERVER_BOOK {
    
    /**
     * <code>PORT</code>
     * Porta TCP del server applicativo per le connessioni in ingresso.
     * <p>Usa 8080 come valore predefinito; considera renderlo final se è una costante.</p>
     */
    private static int PORT = 8080;

    /**
     * <code>server</code>
     * Socket del server, utilizzato per accettare connessioni client.
     * <p>Deve essere aperto all'avvio del servizio e chiuso in fase di shutdown.</p>
     */
    //private ServerSocket server;

    /**
     * <code>socket</code>
     * Socket della connessione con il client attualmente gestito.
     * <p>Può variare ad ogni accettazione; chiudere dopo l’uso.</p>
     */
    private Socket socket;

    /**
     * <code>url</code>
     * URL JDBC della connessione al database PostgreSQL dell’applicazione.
     * <p>Formato atteso: jdbc:postgresql://host:porta/database.</p>
     */
    public static String url = "jdbc:postgresql://localhost:5432/bookrecommender";

    /**
     * <code>userSQL</code>
     * Username del database utilizzato per l’autenticazione JDBC.
     */
    public static String userSQL = "postgres";

    /**
     * <code>passSQL</code>
     * Password del database utilizzata per l’autenticazione JDBC.
     */
    public static String passSQL = "";

    /**
     * Costruttore vuoto
     */
    public SERVER_BOOK(){}

    /**
    * Avvia il server per il sistema di raccomandazione libri (Book Recommender).
    * 
    * <p>Il metodo esegue le seguenti operazioni:
    * <ul>
    * <li>Richiede la password del database tramite input da console</li>
    * <li>Inizializza il database con la password fornita</li>
    * <li>Crea un ServerSocket sulla porta 8080</li>
    * <li>Inizializza gli oggetti principali del sistema (Libreria, Libro, Utente, etc.)</li>
    * <li>Accetta connessioni client in un ciclo infinito, creando un thread dedicato per ogni client</li>
    * </ul>
    *
    * <p><strong>Gestione errori:</strong></p>
    * <ul>
    * <li>Eccezioni fatali durante l'avvio vengono stampate con stack trace</li>
    * <li>Eccezioni durante accept() o creazione thread vengono loggate ma non interrompono il server</li>
    * <li>Il ServerSocket viene sempre chiuso nel blocco finally</li>
    * </ul>
    */
    public void exec() {

        Scanner in = new Scanner(System.in);
        System.out.println("PASSWORD DATABASE ==> ");
        passSQL = in.nextLine();

        ServerSocket server = null;

    try {
        DATABASE.initialize(passSQL);
        
        server = new ServerSocket(8080);

        Libreria libreria = new Libreria();
        Libro libro = new Libro();
        Utente utente = new Utente();
        Suggerimento suggerimento = new Suggerimento();
        Valutazione valutazione = new Valutazione();
        RepositoryLibri repositoryLibri = new RepositoryLibri();

        System.out.println("SERVER PRONTO");

        while (true) {
            try {
                Socket clientSocket = server.accept();
                System.out.println("NUOVA CONNESSIONE ACCETTATA: " + clientSocket.getInetAddress());

                serverThread_BOOK slave = new serverThread_BOOK(clientSocket, utente, libreria, libro, valutazione, suggerimento, repositoryLibri);
                slave.start();

            } catch (IOException e) {
                System.err.println("Errore durante l'accept o la creazione del thread: " + e.getMessage());
            }
        }
    } catch (Exception e) {
        System.err.println("ERRORE FATALE ALL'AVVIO DEL SERVER:");
        e.printStackTrace(); 
    } finally {
        try {
            if (server != null) server.close();
        } catch (IOException e) { e.printStackTrace(); }
    }
}

    /**
     * Metodo main che avvia l'esecuzione del server.
     * @param args argomenti della linea di comando (non utilizzati)
     * @throws SQLException se si verifica un errore during l'esecuzione
     */
    public static void main(String[] args) throws SQLException{
        new SERVER_BOOK().exec();
    }
}