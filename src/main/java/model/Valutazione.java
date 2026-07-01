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
 * Classe che rappresenta una valutazione fatta da un utente su un libro.
 * <p>Contiene valutazioni numeriche e note testuali su vari aspetti del libro come
 * stile, contenuto, gradevolezza, originalità, edizione e un voto finale complessivo.</p>
 * <p>Fornisce metodi statici per inserire nuove valutazioni nel database e per 
 * recuperare le valutazioni associate a un libro specifico.</p>
 * @author Broggini Luca
 * @author De Nicola Matteo
 * @author Pianezzola Andrea
 * @author Saccon Alessandro
 * @author Siragusa Valerio
 * @version 1.0
 */
public class Valutazione {

    /**
     * Costruisce una valutazione con tutti i parametri specificati.
     * @param ID ID della valutazione
     * @param userID ID utente che ha fatto la valutazione
     * @param idLibro ID del libro valutato
     * @param stile punteggio stile
     * @param contenuto punteggio contenuto
     * @param gradevolezza punteggio gradevolezza
     * @param notaOriginalità nota testuale sull'originalità
     * @param notaEdizione nota testuale sull'edizione
     * @param notaComplessiva nota complessiva testuale
     * @param originalità punteggio originalità
     * @param edizione punteggio edizione
     * @param votoFinale voto finale complessivo
     * @param notaStile nota testuale sullo stile
     * @param notaContenuto nota testuale sul contenuto
     * @param notaGradevolezza nota testuale sulla gradevolezza
     */
    public Valutazione(int ID, int userID, int idLibro,int stile, int contenuto, int gradevolezza, String notaOriginalità, String notaEdizione, String notaComplessiva,
                       int originalità, int edizione, double votoFinale, String notaStile, String notaContenuto, String notaGradevolezza)
    {
        this.ID = ID;
        this.userID = userID;
        this.idLibro = idLibro;
        this.stile = stile;
        this.contenuto = contenuto;
        this.gradevolezza = gradevolezza;
        this.originalità = originalità;
        this.edizione = edizione;
        this.votoFinale = votoFinale;
        this.notaStile = notaStile;
        this.notaContenuto = notaContenuto;
        this.notaGradevolezza = notaGradevolezza;
        this.notaOriginalità = notaOriginalità;
        this.notaEdizione = notaEdizione;
        this.notaComplessiva = notaComplessiva;
    }

    /**
     * Costruttore di default che assegna automaticamente un nuovo ID.
     */
    public Valutazione()
    {
        this.ID = nextId;
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
     * <code>userID</code>
     * Identificatore dell'utente che ha espresso la valutazione.
     */
    int userID;

    /**
     * <code>idLibro</code>
     * Identificatore del libro a cui si riferisce la valutazione.
     */
    int idLibro;

    /**
     * <code>stile</code>
     * Valutazione numerica dello stile.
     */
    int stile;

    /**
     * <code>notaStile</code>
     * Nota descrittiva a supporto della valutazione di stile.
     */
    String notaStile;

    /**
     * <code>contenuto</code>
     * Valutazione numerica del contenuto (scala definita dall’app, es. 1–10).
     */
    int contenuto;

    /**
     * <code>notaContenuto</code>
     * Nota descrittiva a supporto della valutazione di contenuto.
     */
    String notaContenuto;

    /**
     * <code>gradevolezza</code>
     * Valutazione numerica della gradevolezza/leggibilità (scala definita dall’app).
     */
    int gradevolezza;

    /**
     * <code>notaGradevolezza</code>
     * Nota descrittiva a supporto della valutazione di gradevolezza.
     */
    String notaGradevolezza;

    /**
     * <code>originalità</code>
     * Valutazione numerica dell’originalità (scala definita dall’app).
     */
    int originalità;

    /**
     * <code>notaOriginalità</code>
     * Nota descrittiva a supporto della valutazione di originalità.
     */
    String notaOriginalità;

    /**
     * <code>edizione</code>
     * Valutazione numerica dell’edizione/qualità editoriale (scala definita dall’app).
     */
    int edizione;

    /**
     * <code>notaEdizione</code>
     * Nota descrittiva a supporto della valutazione di edizione.
     */
    String notaEdizione;

    /**
     * <code>votoFinale</code>
     * Voto finale calcolato/assegnato alla recensione (media o punteggio aggregato).
     */
    double votoFinale;

    /**
     * <code>notaComplessiva</code>
     * Nota complessiva libera che sintetizza i giudizi parziali.
     */
    String notaComplessiva;

    /**
     * Restituisce l'ID della valutazione.
     * @return ID della valutazione
     */
    public int getID(){return ID;}

    /**
     * Restituisce l'ID dell'utente che ha effettuato la valutazione.
     * @return userID utente
     */
    public int getUserID(){return userID;}

    /**
     * Restituisce l'ID del libro valutato.
     * @return ID del libro
     */
    public int getIDLibro(){return idLibro;}

    /**
     * Restituisce il punteggio dello stile del libro.
     * @return punteggio stile
     */
    public int getStile(){return stile;}

    /**
     * Restituisce il punteggio sul contenuto del libro.
     * @return punteggio contenuto
     */
    public int getContenuto(){return contenuto;}

    /**
     * Restituisce il punteggio sulla gradevolezza del libro.
     * @return punteggio gradevolezza
     */
    public int getGradevolezza(){return gradevolezza;}

    /**
     * Restituisce il punteggio sull'originalità del libro.
     * @return punteggio originalità
     */
    public int getOriginalità(){return originalità;}

    /**
     * Restituisce il punteggio sull'edizione del libro.
     * @return punteggio edizione
     */
    public int getEdizione(){return edizione;}

    /**
     * Restituisce il voto finale complessivo assegnato.
     * @return voto finale
     */
    public double getVotoFinale(){return votoFinale;}
   
    /**
     * Recupera l'ID massimo attualmente presente nella tabella valutazioni_libri
     * per l'assegnazione corretta dei nuovi ID.
     * @return ID massimo presente, -1 in caso di errore
     */
    static int getCurrentID() {
        int i = -1;
        String query = "SELECT MAX(id) AS id_current FROM valutazioni_libri";

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
     * Inserisce una nuova valutazione di un libro nel database.
     * @param userID ID utente che valuta
     * @param bookID ID libro valutato
     * @param sr punteggio stile
     * @param cr punteggio contenuto
     * @param p punteggio gradevolezza
     * @param on nota sull'originalità
     * @param en nota sull'edizione
     * @param overallnote nota complessiva testuale
     * @param o punteggio originalità
     * @param e punteggio edizione
     * @param fv voto finale complessivo
     * @param sn nota sullo stile
     * @param cn nota sul contenuto
     * @param plean nota sulla gradevolezza
     * @return true se inserimento riuscito, false 
     */
    public static boolean inserisciValutazioneLibro(
            int userID, int bookID, int sr, int cr, int p,
            String on, String en, String overallnote,
            int o, int e, double fv, String sn, String cn, String plean){

        Valutazione valutazione = new Valutazione();
        boolean res = false;
        String query =
            "INSERT INTO valutazioni_libri " +
            "(id, user_id, book_id, style_rating, content_rating, pleasantness, " +
            " originality_note, edition_note, overall_note, originality, edition, final_vote, " +
            " style_note, content_note, pleasantness_note) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection connection = DriverManager.getConnection(SERVER_BOOK.url, SERVER_BOOK.userSQL, SERVER_BOOK.passSQL);
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setInt(1, valutazione.getID());
            statement.setInt(2, userID);
            statement.setInt(3, bookID);
            statement.setInt(4, sr);
            statement.setInt(5, cr);
            statement.setInt(6, p);
            statement.setString(7, on);
            statement.setString(8, en);
            statement.setString(9, overallnote);
            statement.setInt(10, o);
            statement.setInt(11, e);
            statement.setDouble(12, fv);
            statement.setString(13, sn);
            statement.setString(14, cn);
            statement.setString(15, plean);

            int row = statement.executeUpdate();
            if (row > 0) res = true;
        } 
        catch (SQLException exception) {
            System.err.println("inserisciValutazioneLibro SQLState=" + exception.getSQLState() + " msg=" + exception.getMessage());        
        }

        return res;
    }

    /**
     * Recupera tutte le valutazioni associate a un dato libro.
     * @param bookID ID del libro
     * @return una lista di oggetti Valutazione relativi al libro
     */
    public static LinkedList<Valutazione> getValutazioniLibro(int bookID) {
        LinkedList<Valutazione> listaValutazioni = new LinkedList<>();
        String query = "SELECT * FROM valutazioni_libri WHERE book_id = ?";
        try (Connection connection = DriverManager.getConnection(SERVER_BOOK.url, SERVER_BOOK.userSQL, SERVER_BOOK.passSQL);
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setInt(1, bookID);

            try (ResultSet result = statement.executeQuery()) {
                while (result.next()) {
                    listaValutazioni.add(new Valutazione(
                        result.getInt("id"),
                        result.getInt("user_id"),
                        result.getInt("book_id"),
                        result.getInt("style_rating"),
                        result.getInt("content_rating"),
                        result.getInt("pleasantness"),
                        result.getString("originality_note"),
                        result.getString("edition_note"),
                        result.getString("overall_note"),
                        result.getInt("originality"),
                        result.getInt("edition"),
                        result.getDouble("final_vote"),
                        result.getString("style_note"),
                        result.getString("content_note"),
                        result.getString("pleasantness_note")
                    ));
                }
            }
        } 
        catch (SQLException e) {
            System.err.println("getValutazioniLibro SQLState=" + e.getSQLState() + " msg=" + e.getMessage());
        }
        return listaValutazioni;
    }
}