/* DE NICOLA MATTEO VINCENZO, MATRICOLA 757933, VA
    SIRAGUSA VALERIO, MATRICOLA 756227, VA
    SACCON ALESSANDRO, MATRICOLA 756145, VA
    PIANEZZOLA ANDREA, MATRICOLA 756141, VA
    BROGGINI LUCA, MATRICOLA 756446, VA
*/
package model;


import java.io.IOException;
import java.sql.*;
import java.util.LinkedList;

/**
 * Classe che rappresenta un suggerimento di libri fatto da un utente.
 * <p>Un suggerimento è associato a un utente, a un libro selezionato e
 * a una lista di libri suggeriti correlati.</p>
 * <p>Fornisce metodi per inserire nuovi suggerimenti nel database,
 * associare libri ai suggerimenti e recuperare suggerimenti esistenti.</p>
 * @author Broggini Luca
 * @author De Nicola Matteo
 * @author Pianezzola Andrea
 * @author Saccon Alessandro
 * @author Siragusa Valerio
 * @version 1.0
 */
public class Suggerimento {

    /**
     * Costruisce un suggerimento con ID, userID e selectedbookID specificati.
     * @param ID ID del suggerimento
     * @param userID ID dell'utente che ha effettuato il suggerimento
     * @param selectedbookID ID del libro selezionato
     */
    public Suggerimento(int ID, int userID, int selectedbookID)
    {
        this.ID = ID;
        this.userID = userID;
        this.selectedbookID = selectedbookID;
    }

    /**
     * Costruttore di default che assegna automaticamente un nuovo ID.
     */
    public Suggerimento()
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
     * Identificatore univoco dell'istanza (assegnato usando nextId o dal database).
     */
    int ID;

    /**
     * <code>userID</code>
     * Identificatore dell’utente associato a questa istanza.
     */
    int userID;

    /**
     * <code>selectedbookID</code>
     * Identificatore del libro selezionato associato a questa istanza.
     */
    int selectedbookID;

    /**
     * Restituisce l'ID del suggerimento.
     * @return ID del suggerimento
     */
    public int getID(){return ID;}

    /**
     * Restituisce l'identificatore dell'utente associato.
     * @return l'ID dell'utente
     */
    public int getUserID(){return userID;}

    /**
     * Restituisce l'identificatore del libro selezionato.
     * @return l'ID del libro selezionato
     */
    public int getSelectedBookID(){return selectedbookID;}

    /**
     * Recupera l'ID massimo attualmente presente nella tabella valutazioni_libri
     * per l'assegnazione corretta dei nuovi ID.
     * @return ID massimo presente, -1 in caso di errore
     */
    static int getCurrentID() {
        int i = -1;
        String query = "SELECT MAX(id) AS id_current FROM suggerimento";

        try (Connection connection = DriverManager.getConnection(SERVER_BOOK.url, SERVER_BOOK.userSQL, SERVER_BOOK.passSQL);
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet result = statement.executeQuery()) {
            if (result.next()) i = result.getInt("id_current");
        } 
        catch (SQLException e) {
            System.err.println("getCurrentID SQLState=" + e.getSQLState() + " msg=" + e.getMessage());
        }
        return i;
    }

    /**
     * Inserisce un nuovo suggerimento nella tabella suggerimento per l'utente e il libro indicati.
     * <p>Il valore restituito indica l'ID del suggerimento se l'inserimento va a buon fine, altrimenti -1.</p>
     * @param userID identificatore dell'utente autore del suggerimento 
     * @param bookID identificatore del libro selezionato da associare al suggerimento
     * @return l'ID del suggerimento inserito, oppure -1 se l'inserimento non ha avuto effetto
     */
    public static int inserisciSuggerimento(int userID, int bookID) {
        Suggerimento suggerimento = new Suggerimento();
        int res = -1;
        String query = "INSERT INTO suggerimento (id, user_id, selected_book_id) VALUES (?, ?, ?)";

        try (Connection connection = DriverManager.getConnection(SERVER_BOOK.url, SERVER_BOOK.userSQL, SERVER_BOOK.passSQL);
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, suggerimento.getID());
            statement.setInt(2, userID);
            statement.setInt(3, bookID);
            int row = statement.executeUpdate();
            if (row > 0) res = suggerimento.getID();
        } 
        catch (SQLException e) {
            System.err.println("inserisciSuggerimento SQLState=" + e.getSQLState() + " msg=" + e.getMessage());
        }
        return res;
    }

    /**
     * Inserisce l'associazione tra un suggerimento esistente e un libro consigliato nella tabella libri_suggerimento. [web:1]
     *
     * <p>Restituisce true se almeno una riga è stata inserita, altrimenti false.</p>
     *
     * @param suggID identificatore del suggerimento esistente (chiave esterna suggestion_id) 
     * @param bookID identificatore del libro consigliato da collegare (chiave esterna book_id) 
     * @return true se l'inserimento ha interessato almeno una riga, false altrimenti 
     */
    public static boolean inserisciSuggerimentoLibro(int suggID, int bookID) {
        boolean res = false;
        String query = "INSERT INTO libri_suggerimento (suggestion_id, book_id) VALUES (?, ?)";

        try (Connection connection = DriverManager.getConnection(SERVER_BOOK.url, SERVER_BOOK.userSQL, SERVER_BOOK.passSQL);
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, suggID);
            statement.setInt(2, bookID);
            int row = statement.executeUpdate();
            if (row > 0) res = true;
        } 
        catch (SQLException e) {
            System.err.println("inserisciSuggerimentoLibro SQLState=" + e.getSQLState() + " msg=" + e.getMessage());
        }
        return res;
    }

    /**
     * Recupera la lista degli ID dei libri consigliati per il libro indicato, basandosi sulle righe in libri_suggerimento collegate
     * ai suggerimenti che hanno selected_book_id uguale al bookID fornito. 
     * @param bookID identificatore del libro per cui ottenere i libri suggeriti associati 
     * @return lista di ID dei libri suggeriti (può essere vuota se non ci sono corrispondenze) 
     */
    public static LinkedList<Integer> getSuggerimentiLibro(int bookID) {
        LinkedList<Integer> listaLibri = new LinkedList<>();
        String query =
            "SELECT ls.book_id AS suggested_book_id " +
            "FROM libri_suggerimento ls " +
            "JOIN suggerimento s ON s.id = ls.suggestion_id " +
            "WHERE s.selected_book_id = ?";

        try (Connection connection = DriverManager.getConnection(SERVER_BOOK.url, SERVER_BOOK.userSQL, SERVER_BOOK.passSQL);
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, bookID);
            try (ResultSet result = statement.executeQuery()) {
                while (result.next())
                    listaLibri.add(result.getInt("suggested_book_id"));
            }
        } 
        catch (SQLException e) {
            System.err.println("getSuggerimentiLibro SQLState=" + e.getSQLState() + " msg=" + e.getMessage());
        }
        return listaLibri;
    }

    /**
     * Restituisce il numero di consigli registrati per una coppia utente–libro, contando le righe in libri_suggerimento
     * collegate a suggerimenti con user_id e selected_book_id corrispondenti. 
     * @param userID identificatore dell'utente da filtrare nella conta dei consigli 
     * @param bookID identificatore del libro selezionato da filtrare nella conta dei consigli 
     * @return numero totale di consigli trovati per la coppia utente–libro indicata 
     */
    public static int getNumeroConsigli(int userID, int bookID) {
        int row = 0;
        final String query =
            "SELECT COUNT(*) AS num_consigli " +
            "FROM libri_suggerimento ls " +
            "JOIN suggerimento s ON s.id = ls.suggestion_id " +
            "WHERE s.user_id = ? AND s.selected_book_id = ?";

        try (Connection connection = DriverManager.getConnection(SERVER_BOOK.url, SERVER_BOOK.userSQL, SERVER_BOOK.passSQL);
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, userID);
            statement.setInt(2, bookID);
            try (ResultSet result = statement.executeQuery()) {
                while (result.next()) 
                    row = result.getInt("num_consigli");

            }
        } 
        catch (SQLException e) {
            System.err.println("getNumeroConsigli SQLState=" + e.getSQLState() + " msg=" + e.getMessage());
        }
        return row;
    }
}