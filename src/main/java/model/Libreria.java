/* DE NICOLA MATTEO VINCENZO, MATRICOLA 757933, VA
    SIRAGUSA VALERIO, MATRICOLA 756227, VA
    SACCON ALESSANDRO, MATRICOLA 756145, VA
    PIANEZZOLA ANDREA, MATRICOLA 756141, VA
    BROGGINI LUCA, MATRICOLA 756446, VA
*/
package model;

import java.io.*;
import java.sql.*;
import java.util.LinkedList;

/**
 * Rappresenta una libreria personale di un utente.
 * <p>
 * Fornisce metodi per la gestione delle librerie, inclusa registrazione, controllo,
 * inserimento libri e recupero delle librerie o dei libri appartenenti a una libreria.
 * </p>
 * @author Broggini Luca
 * @author De Nicola Matteo
 * @author Pianezzola Andrea
 * @author Saccon Alessandro
 * @author Siragusa Valerio
 * @version 1.0
 */
public class Libreria implements Serializable
{
    private static final long serialVersionUID = 1L;
    
    /**
     * Costruttore principale. Crea una libreria per l'utente specificato.
     *
     * @param userID ID dell'utente proprietario
     * @param nome nome della libreria
     */
    public Libreria(int userID, String nome)
    {
        this.userID = userID;
        this.nome = nome;
    }

    /**
     * Costruttore vuoto.
     */
    public Libreria(){}

    /**
     * <code>userID</code>
     * Identificatore univoco dell'utente associato all'istanza.
     * <p>Valore positivo se definito, altrimenti 0/non inizializzato.</p>
     */
    private int userID;

    /**
     * <code>nome</code>
     * Nome dell'utente associato all'istanza.
     * <p>Non deve essere nullo quando l'oggetto è in stato valido.</p>
     */
    private String nome;

    /**
     * Restituisce l'ID dell'utente proprietario della libreria.
     * @return userID dell'utente
     */
    public int getUserID(){return userID;}

    /**
     * Restituisce il nome della libreria.
     * @return nome della libreria
     */
    public String getNome(){return nome;}

    /**
     * Registra una nuova libreria per un utente.
     *
     * @param userID identificativo dell'utente proprietario
     * @param nomeLibreria nome della libreria da registrare
     * @return true se la registrazione ha avuto successo, false altrimenti
     */
    public static boolean registraLibreria(int userID, String nomeLibreria) 
    {
        boolean res = false;
        String query = "INSERT INTO libreria (user_id, name) VALUES (?, ?)";
        try (Connection connection = DriverManager.getConnection(SERVER_BOOK.url, SERVER_BOOK.userSQL, SERVER_BOOK.passSQL);
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, userID);
            statement.setString(2, nomeLibreria);
            int rows = statement.executeUpdate();
            res = rows > 0;
        } 
        catch (SQLException e) {
            System.err.println("registraLibreria SQLState=" + e.getSQLState() + " msg=" + e.getMessage());
        }
        return res;
    }   

    /**
     * Controlla se una libreria dell'utente esiste già.
     *
     * @param userID identificativo utente
     * @param nomeLibreria nome della libreria
     * @return true se la libreria esiste già, false altrimenti
     */
    public static boolean controlloLibreria(int userID, String nomeLibreria) 
    {
        boolean res = false;
        String query = "SELECT EXISTS (SELECT 1 FROM libreria WHERE user_id = ? AND name = ?) AS already_exists";
    
        try (Connection connection = DriverManager.getConnection(SERVER_BOOK.url, SERVER_BOOK.userSQL, SERVER_BOOK.passSQL);
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, userID);
            statement.setString(2, nomeLibreria);
            try (ResultSet result = statement.executeQuery()) {
                if (result.next()) 
                    res = result.getBoolean("already_exists");
            }
        } 
        catch (SQLException e) {
            System.err.println("controlloLibreria SQLState=" + e.getSQLState() + " msg=" + e.getMessage());
        }

        return res;
    }

    
    /**
     * Aggiunge un libro ad una libreria dell'utente.
     *
     * @param nomeLibreria nome della libreria
     * @param userID identificativo dell'utente
     * @param bookID identificativo del libro
     * @return true se l'inserimento ha avuto successo, false altrimenti
     */
    public static boolean aggiungiLibro(String nomeLibreria, int userID, int bookID)
    {
        boolean res = false;
        String query = "INSERT INTO libri_libreria (name, user_id, book_id) VALUES (?, ?, ?)";
        try (Connection connection = DriverManager.getConnection(SERVER_BOOK.url, SERVER_BOOK.userSQL, SERVER_BOOK.passSQL);
            PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, nomeLibreria);
            statement.setInt(2, userID);
            statement.setInt(3, bookID);
            int rows = statement.executeUpdate();
            res = rows > 0;
        } 
        catch (SQLException e) {
            System.err.println("aggiungiLibro SQLState=" + e.getSQLState() + " msg=" + e.getMessage());
        }
        return res;
    }


    /**
     * Restituisce le librerie possedute da un utente.
     * @param userID identificativo dell'utente
     * @return lista di librerie associate all'utente
     */
    public static LinkedList<Libreria> getLibrerieUtente(int userID) 
    {
        LinkedList<Libreria> librerieUtente = new LinkedList<>();
        String query = "SELECT user_id, name FROM libreria WHERE user_id = ?";

        try (Connection connection = DriverManager.getConnection(SERVER_BOOK.url, SERVER_BOOK.userSQL, SERVER_BOOK.passSQL);
            PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, userID);
            try (ResultSet result = statement.executeQuery()) {
                while (result.next()) 
                    librerieUtente.add(new Libreria(result.getInt("user_id"), result.getString("name")));
            }
        } 
        catch (SQLException e) {
            System.err.println("getLibrerieUtente SQLState=" + e.getSQLState() + " msg=" + e.getMessage());
        }
        return librerieUtente;
    }


    /**
     * Restituisce l'elenco dei libri presenti in una libreria dell'utente.
     * @param nomeLibreria nome della libreria
     * @param userID identificativo dell'utente
     * @return lista dei libri contenuti nella libreria
     */
    public static LinkedList<Libro> getElencoLibri(String nomeLibreria, int userID)
    {
        LinkedList<Libro> elencoLibri = new LinkedList<>();
        String query =
            "SELECT B.id, B.title, B.authors, B.category, B.publisher, B.publish_date_year " +
            "FROM libri_libreria LB " +
            "JOIN libri B ON B.id = LB.book_id " +
            "WHERE LB.name = ? AND LB.user_id = ?";

        try (Connection connection = DriverManager.getConnection(SERVER_BOOK.url, SERVER_BOOK.userSQL, SERVER_BOOK.passSQL);
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, nomeLibreria);
            statement.setInt(2, userID);
            try (ResultSet result = statement.executeQuery()) {
                while (result.next()) {
                    elencoLibri.add(new Libro(
                        result.getInt("id"),
                        result.getString("title"),
                        result.getString("authors"),
                        result.getString("category"),
                        result.getString("publisher"),
                        result.getString("publish_date_year")
                    ));
                }
            }
        } 
        catch (SQLException e) {
            System.err.println("getElencoLibri SQLState=" + e.getSQLState() + " msg=" + e.getMessage());
        }
        return elencoLibri;
    }

    
    /**
     * Controlla se un libro è presente nella libreria specificata per l'utente dato.
     *
     * @param nomeLibreria nome della libreria
     * @param userID identificativo dell'utente
     * @param bookID identificativo del libro
     * @return true se il libro è già presente, false altrimenti
     */
    public static boolean checkPresente(String nomeLibreria, int userID, int bookID) {
        boolean isMember = false;
        String query =
            "SELECT EXISTS (" +
            "  SELECT 1 FROM libri_libreria WHERE name = ? AND user_id = ? AND book_id = ?" +
            ") AS present";
        try (Connection connection = DriverManager.getConnection(SERVER_BOOK.url, SERVER_BOOK.userSQL, SERVER_BOOK.passSQL);
            PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, nomeLibreria);
            statement.setInt(2, userID);
            statement.setInt(3, bookID);
            try (ResultSet result = statement.executeQuery()) {
                if (result.next()) 
                    isMember = result.getBoolean("present");
            }
        } 
        catch (SQLException e) {
            System.err.println("checkPresente SQLState=" + e.getSQLState() + " msg=" + e.getMessage());
        }
        return isMember;
    }

}