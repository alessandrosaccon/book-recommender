/* DE NICOLA MATTEO VINCENZO, MATRICOLA 757933, VA
    SIRAGUSA VALERIO, MATRICOLA 756227, VA
    SACCON ALESSANDRO, MATRICOLA 756145, VA
    PIANEZZOLA ANDREA, MATRICOLA 756141, VA
    BROGGINI LUCA, MATRICOLA 756446, VA
*/
package server;

import java.io.*;
import java.sql.*;
import model.*;

/**
 * Classe utility per l'inizializzazione del database PostgreSQL.
 * <p>Verifica se il database "bookrecommender" esiste già nel server locale,
 * in caso negativo lo crea.</p>
 * <p>Utilizza credenziali fisse per la connessione e si connette al database
 * predefinito "postgres" per poter eseguire la creazione del database target.</p>
 * @author Broggini Luca
 * @author De Nicola Matteo
 * @author Pianezzola Andrea
 * @author Saccon Alessandro
 * @author Siragusa Valerio
 * @version 1.0
 */
public class DATABASE {
    /**
     * <code>dbName</code>
     * Nome del database PostgreSQL a cui connettersi.
     * <p>Usato per comporre la JDBC URL.</p>
     */
    static final String dbName = "bookrecommender";

    /**
     * <code>userSQL</code>
     * Username del database utilizzato per l’autenticazione JDBC.
     */
    static final String userSQL = "postgres";

    /**
     * <code>host</code>
     * Host del server PostgreSQL (es. "localhost" o hostname/IP).
     */
    static final String host = "localhost";

    /**
     * <code>PORT</code>
     * Porta TCP del server PostgreSQL; 5432 è il valore predefinito.
     */
    static final int PORT = 5432;

    /**
     * Costruttore vuoto.
     */
    public DATABASE(){}

    /**
     * Inizializza il database "bookrecommender".
     * <p>Si connette al database "postgres" di default e verifica l'esistenza
     * del database target "bookrecommender". Se non esiste, lo crea.</p>
     * <p>Stampa a console lo stato dell'operazione (creazione o esistenza).</p>
     * 
     * @throws RuntimeException errore inizializzazione
     * @param password password inserita dall'utente
     */
    public static void initialize(String password)
    {
        String urlDefaultDB = "jdbc:postgresql://" + host + ":" + PORT + "/postgres";
        String urlTargetDB = "jdbc:postgresql://" + host + ":" + PORT + "/" + dbName;
        boolean res = false;

        try(Connection connection = DriverManager.getConnection(urlDefaultDB, userSQL, password);)
        {
            Statement query = connection.createStatement();
            ResultSet resultSet = query.executeQuery("SELECT 1 FROM pg_database WHERE datname = '" + dbName + "'");
            if(!resultSet.next())
            {   
                query.executeUpdate("CREATE DATABASE\n" + dbName + "\n");
                System.out.println("DATABASE CREATO");
                res = true;
            }
            else
                System.out.println("DATABASE GIA' ESISTENTE");
        }
        catch(SQLException e)
        {   
            System.err.println("Errore inizializzazione DB: " + e.getMessage());
            throw new RuntimeException("Inizializzazione database fallita", e);
        }
        
        if(res)
        {
            try
            (
                Connection connection = DriverManager.getConnection(urlTargetDB, userSQL, password);
            )
            {      
                Statement query = connection.createStatement();
                InputStream input = DATABASE.class.getResourceAsStream("/schema.sql");
                if(input == null)
                    throw new RuntimeException("SCHEMA SQL NON TROVATO");

                BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                StringBuilder stringBuilder = new StringBuilder();
                String line;
                while((line = reader.readLine()) != null)
                {
                    stringBuilder.append(line).append("\n");
                }
                String[] queries = stringBuilder.toString().split(";");
                for(String string : queries)
                {
                    string = string.trim();
                    if(!string.isEmpty())
                        query.execute(string);
                }
                System.out.println("TABELLE CREATE");
            }catch(Exception e){throw new RuntimeException("ERRORE" + e.getLocalizedMessage());}
        }
    }
}