/* DE NICOLA MATTEO VINCENZO, MATRICOLA 757933, VA
    SIRAGUSA VALERIO, MATRICOLA 756227, VA
    SACCON ALESSANDRO, MATRICOLA 756145, VA
    PIANEZZOLA ANDREA, MATRICOLA 756141, VA
    BROGGINI LUCA, MATRICOLA 756446, VA
*/
package model;

import java.io.Serializable;
import java.sql.*;
import java.util.LinkedList;

/**
 * Rappresenta un libro.
 * <p>
 * Contiene informazioni basilari come titolo, autori, categoria, editore e anno,
 * oltre a metodi per accedere alle valutazioni e ai suggerimenti associati.
 * Supporta la creazione di istanze da database.
 * </p>
 * @author Broggini Luca
 * @author De Nicola Matteo
 * @author Pianezzola Andrea
 * @author Saccon Alessandro
 * @author Siragusa Valerio
 * @version 1.0
 */
public class Libro implements Serializable
{
    private static final long serialVersionUID = 1L;
    
    /**
     * Costruisce un nuovo libro con i parametri specificati.
     * @param ID identificatore univoco
     * @param titolo titolo del libro
     * @param autori autori del libro
     * @param categoria categoria del libro
     * @param editore editore del libro
     * @param anno anno di pubblicazione
     */
    public Libro(int ID, String titolo, String autori, String categoria, String editore, String anno){
        this.ID = ID;
        this.titolo = titolo;
        this.autori = autori;
        this.categoria = categoria;
        this.editore = editore;
        this.anno = anno;
    }

    /**
     * Costruttore vuoto.
     */
    public Libro(){}

    /**
     * <code>ID</code>
     * Identificatore numerico univoco del libro.
     * <p>Valore positivo se definito, 0 se non inizializzato.</p>
     */
    private int ID;

    /**
     * <code>titolo</code>
     * Titolo del libro così come riportato nella fonte/catalogo.
     * <p>Non dovrebbe essere nullo in uno stato valido dell'oggetto.</p>
     */
    private String titolo;

    /**
     * <code>autori</code>
     * Autori del libro in forma testuale (es. "Nome Cognome; Altro Autore").
     * <p>Può contenere più autori separati da delimitatori concordati.</p>
     */
    private String autori;

    /**
     * <code>categoria</code>
     * Categoria o genere principale del libro (es. "Narrativa", "Saggistica").
     */
    private String categoria;

    /**
     * <code>editore</code>
     * Nome dell'editore responsabile della pubblicazione.
     */
    private String editore;
    
    /**
     * <code>anno</code>
     * Anno di pubblicazione del libro in formato testuale (es. "2020").
     * <p>Usare un formato coerente con il resto del sistema.</p>
     */
    private String anno;
  
    /**
     * Restituisce l'ID del libro.
     * @return l'identificatore univoco
     */
    public int getID(){return ID;}

    /**
     * Restituisce il titolo del libro.
     * @return il titolo del libro
     */
    public String getTitolo(){return titolo;}

    /**
     * Restituisce gli autori del libro.
     * @return gli autori
     */
    public String getAutori(){return autori;}

    /**
     * Restituisce la categoria del libro.
     * @return la categoria
     */
    public String getCategoria(){return categoria;}

    /**
     * Restituisce l'editore del libro.
     * @return l'editore
     */
    public String getEditore(){return editore;}

    /**
     * Restituisce l'anno di pubblicazione del libro.
     * @return l'anno di pubblicazione
     */
    public String getAnno(){return anno;}

    /**
     * Restituisce una stringa contenente tutte le informazioni principali del libro.
     * @return informazioni aggregate del libro
     */
    public String getInfo(){ return getTitolo() + " " + this.getAutori() + " " + this.getCategoria() + " " + this.getEditore() + " " + this.getAnno();}

    /**
     * Recupera la lista delle valutazioni associate al libro.
     * @return lista di valutazioni del libro
     */
    public LinkedList<Valutazione> getValutazioni() { return Valutazione.getValutazioniLibro(ID); }

    /**
     * Recupera una lista di suggerimenti per il libro.
     * @return lista di suggerimenti associati
     */
    public LinkedList<Integer> getSuggerimenti() { return Suggerimento.getSuggerimentiLibro(ID); }

    /**
     * Ottiene un libro dal database tramite titolo.
     * @param titolo titolo da cercare
     * @return istanza Libro corrispondente o <code>null</code> se non trovato
     */
    public static Libro getLibro(String titolo)
    {
        Libro libro = null;
        String query = "SELECT * FROM libri WHERE title ILIKE ? LIMIT 1";

        try
        {
            Connection connection = DriverManager.getConnection(SERVER_BOOK.url, SERVER_BOOK.userSQL, SERVER_BOOK.passSQL);
                try(PreparedStatement statement = connection.prepareStatement(query);)
                {
                    statement.setString(1, "%" + titolo + "%");
                    ResultSet result = statement.executeQuery();
                    if(result.next())
                        libro = new Libro(result.getInt("id"), result.getString("title"), result.getString("authors"), 
                                result.getString("category"), result.getString("publisher"), result.getString("publish_date_year"));
                }
                catch(SQLException e){System.err.println("ERRORE" + e.getMessage());}
        }catch(SQLException e){System.err.println("ERRORE: ");}

        return libro;
    }

    /**
     * Ottiene un libro dal database tramite ID.
     * @param id identificatore del libro da cercare
     * @return istanza Libro corrispondente o <code>null</code> se non trovato
     */
    public static Libro getLibro(int id)
    {
        Libro libro = null;
        String query = "SELECT * FROM libri WHERE id = ? LIMIT 1";;
        try
        {
            Connection connection = DriverManager.getConnection(SERVER_BOOK.url, SERVER_BOOK.userSQL, SERVER_BOOK.passSQL);
                try(PreparedStatement statement = connection.prepareStatement(query);)
                {
                    statement.setInt(1, id);
                    ResultSet result = statement.executeQuery();
                    if(result.next())
                        libro = new Libro(result.getInt("id"), result.getString("title"), result.getString("authors"), 
                                result.getString("category"), result.getString("publisher"), result.getString("publish_date_year"));

                }
                catch(SQLException e){System.err.println("ERRORE" + e.getMessage());}
        }catch(SQLException e){System.err.println("ERRORE");}
        return libro;
    }

}