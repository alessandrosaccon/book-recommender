/* DE NICOLA MATTEO VINCENZO, MATRICOLA 757933, VA
    SIRAGUSA VALERIO, MATRICOLA 756227, VA
    SACCON ALESSANDRO, MATRICOLA 756145, VA
    PIANEZZOLA ANDREA, MATRICOLA 756141, VA
    BROGGINI LUCA, MATRICOLA 756446, VA
*/
package model;

import java.io.*;
import java.sql.*;

/**
 * Classe che rappresenta un utente registrato nel sistema.
 * <p>Contiene informazioni base come nome, cognome, codice fiscale (cv), email,
 * userID e password. Gestisce l'accesso e la registrazione degli utenti
 * interfacciandosi con il database.</p>
 * <p>Implementa Serializable per permettere la serializzazione degli oggetti.</p>
 * @author Broggini Luca
 * @author De Nicola Matteo
 * @author Pianezzola Andrea
 * @author Saccon Alessandro
 * @author Siragusa Valerio
 * @version 1.0
 */
public class Utente implements Serializable
{
    static final long serialVersionUID = 1L;

    /**
     * Costruisce un utente con tutti i parametri specificati.
     * @param ID ID univoco utente
     * @param nome nome dell'utente
     * @param cognome cognome dell'utente
     * @param cv codice fiscale dell'utente
     * @param mail email dell'utente
     * @param userID userID utilizzato per il login
     * @param password password dell'utente
     */
    public Utente(int ID, String nome, String cognome, String cv, String mail, String userID, String password) 
    {
        this.ID = ID; 
        this.nome = nome;
        this.cognome = cognome;
        this.cv = cv;
        this.mail = mail;
        this.userID = userID;
        this.password = password;
    }

    /**
     * Costruttore di default, assegna automaticamente un nuovo ID.
     */
    public Utente()
    {
        ID = nextId;
        nextId++;
    }

    /**
     * <code>nextId</code>
     * Contatore statico inizializzato con l'ID corrente, usato per generare nuovi identificativi.
     */
    static int nextId = getCurrentID(); 

    /**
     * <code>ID</code>
     * Identificatore univoco.
     */
    int ID;

    /**
     * <code>nome</code>
     * Nome dell'utente.
     */
    String nome;

    /**
     * <code>cognome</code>
     * Cognome dell'utente.
     */
    String cognome;

    /**
     * <code>cv</code>
     * Codice fiscale dell'utente.
     */
    String cv;

    /**
     * <code>mail</code>
     * Indirizzo email dell'utente; atteso come stringa valida conforme allo standard.
     */
    String mail;

    /**
     * <code>userID</code>
     * Identificatore testuale dell'utente (username) utilizzato per l’accesso.
     */
    String userID;

    /**
     * <code>password</code>
     * Password dell'utente in chiaro nel modello.
     */
    String password;

    /**
     * Restituisce l'ID dell'utente.
     * @return ID univoco dell'utente
     */
    public int getID(){return ID;}

    /**
     * Restituisce il nome dell'utente.
     * @return nome
     */
    public String getNome(){return nome;}

    /**
     * Restituisce il cognome dell'utente.
     * @return cognome
     */
    public String getCognome(){return cognome;}

    /**
     * Restituisce il codice fiscale (cv) dell'utente.
     * @return codice fiscale
     */
    public String getCv(){return cv;}

    /**
     * Restituisce l'email dell'utente.
     * @return email
     */
    public String getMail(){return mail;}

    /**
     * Restituisce il userID dell'utente (usato nel login).
     * @return userID
     */
    public String getUserID(){return userID;}

    /**
     * Restituisce la password associata all'utente.
     * @return password
     */
    public String getPassword(){return password;}

    /**
     * Recupera l'ID massimo attualmente presente nella tabella Utente_Registrati
     * per continuare la numerazione degli ID in modo corretto.
     * @return ID massimo presente nel database, -1 in caso di errore
     * @throws SQLException possibili errori di accesso al database durante l’apertura della connessione, la preparazione/esecuzione della query o la lettura del ResultSet
     */
    private static int getCurrentID() {
        int i = -1;
        String query = "SELECT MAX(id) AS id_current FROM Utente_Registrati";

        try (Connection connection = DriverManager.getConnection(SERVER_BOOK.url, SERVER_BOOK.userSQL, SERVER_BOOK.passSQL);
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet result = statement.executeQuery()) {
            if (result.next()) i = result.getInt("id_current");
        } 
        catch (SQLException exception) {
            System.err.println("getCurrentID SQLState=" + exception.getSQLState() + " msg=" + exception.getMessage());
        }
        return i;
    }

    /**
     * Registra un nuovo utente nel database.
     * @param nome nome dell'utente
     * @param cognome cognome dell'utente
     * @param cf codice fiscale (cv) dell'utente
     * @param mail email dell'utente
     * @param userID userID scelto dall'utente per il login
     * @param password password associata all'utente
     * @return true se la registrazione ha avuto successo, false altrimenti
     */
    public static boolean registrazione(String nome, String cognome, String cf, String mail, String userID, String password) {
        Utente utente = new Utente();
        boolean res = false;
        String query = "INSERT INTO Utente_Registrati (id, first_name, last_name, cv, email, user_id, password) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection connection = DriverManager.getConnection(SERVER_BOOK.url, SERVER_BOOK.userSQL, SERVER_BOOK.passSQL);
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, utente.getID());
            statement.setString(2, nome);
            statement.setString(3, cognome);
            statement.setString(4, cf);
            statement.setString(5, mail);
            statement.setString(6, userID);
            statement.setString(7, password);
            int rows = statement.executeUpdate();
            if (rows > 0) res = true;
        } 
        catch (SQLException e) {
            System.err.println("registrazione SQLState=" + e.getSQLState() + " msg=" + e.getMessage());
        }
        return res;
    }

    /**
     * Esegue il login cercando un utente con il dato userID e verifica la password.
     * @param userID userID inserito dall'utente
     * @param password password inserita dall'utente
     * @return oggetto Utente se login riuscito, null altrimenti
     */
    public static Utente Login(String userID, String password) {
        Utente user = null;
        String query = "SELECT * FROM Utente_Registrati WHERE user_id = ?";

        try (Connection connection = DriverManager.getConnection(SERVER_BOOK.url, SERVER_BOOK.userSQL, SERVER_BOOK.passSQL);
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, userID);
            try (ResultSet result = statement.executeQuery()) {
                if (result.next()) {
                    if (password.equals(result.getString("password"))) {
                        user = new Utente(
                            result.getInt("id"),
                            result.getString("first_name"),
                            result.getString("last_name"),
                            result.getString("cv"),
                            result.getString("email"),
                            result.getString("user_id"),
                            result.getString("password")
                        );
                    }
                }
            }
        } 
        catch (SQLException e) {
            System.err.println("Login SQLState=" + e.getSQLState() + " msg=" + e.getMessage());
        }
        return user;
    }

    /**
     * Verifica la validità formale del codice fiscale (cf).
     * @param cf codice fiscale da controllare
     * @return true se il formato è valido, false altrimenti
     */
    public static boolean controlloCF(String cf) 
    {
        char c;

        //INIZIALMENTE VEDIAMO LA STRINGA INSERITA COME CORRETTA, SE DOVESSI RIVELARSI SBAGLIATA VERRA' SETTATO A FALSE
        boolean temp = true;

        //VIENE CONTROLLATA LA LUNGHEZZA DELLA STRINGA INSERITA(16 CARATTERI)
        if(cf.length() != 16)
            temp = false;        
        else
            for (int i = 0; i < cf.length(); i++)
            {
                //ALL'INTERNO DI C SCORRIAMO I SINGOLI CARATTERI DELLA STRINGA UTILIZZANDO IL CHAR AT
                c = cf.charAt(i);

                //SE I CARATTERI INDICATI NELLE CONDIZIONI NON SONO LETTERE/NUMERI IL CODICE FISCALE E' ERRATO
                if (i<6 || i==8 || i==11 || i==15)
                    if(!Character.isLetter(c))
                        temp = false;

                if (i>=6 && i<8 || i>=9 && i<11 || i>=12 && i<15)
                    if(!Character.isDigit(c))
                        temp = false;
            }

        return temp;
    }

    /**
     * Verifica se un codice fiscale esiste già nel database.
     * @param cf codice fiscale da verificare
     * @return true se esiste già, false altrimenti
     */
    public static boolean esisteCF(String cf) {
        String query = "SELECT 1 FROM Utente_Registrati WHERE cv = ? LIMIT 1";
        boolean res = false;

        try (Connection connection = DriverManager.getConnection(SERVER_BOOK.url, SERVER_BOOK.userSQL, SERVER_BOOK.passSQL);
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, cf);
            try (ResultSet result = statement.executeQuery()) {
                if (result.next()) res = true;
            }
        } 
        catch (SQLException e) {
            System.err.println("esisteCF SQLState=" + e.getSQLState() + " msg=" + e.getMessage());
        }
        return res;
    }

    /**
     * Verifica se un userID esiste già nel database.
     * @param userID userID da verificare
     * @return true se esiste già, false altrimenti
     */
    public static boolean controllaUtente(String userID) {
        boolean res = false;
        String query = "SELECT 1 FROM Utente_Registrati WHERE user_id = ? LIMIT 1";

        try (Connection connection = DriverManager.getConnection(SERVER_BOOK.url, SERVER_BOOK.userSQL, SERVER_BOOK.passSQL);
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, userID);
            try (ResultSet result = statement.executeQuery()) {
                if (result.next()) res = true;
            }
        } 
        catch (SQLException e) {
            System.err.println("controllaUtente SQLState=" + e.getSQLState() + " msg=" + e.getMessage());
        }
        return res;
    }

}