/* DE NICOLA MATTEO VINCENZO, MATRICOLA 757933, VA
    SIRAGUSA VALERIO, MATRICOLA 756227, VA
    SACCON ALESSANDRO, MATRICOLA 756145, VA
    PIANEZZOLA ANDREA, MATRICOLA 756141, VA
    BROGGINI LUCA, MATRICOLA 756446, VA
*/
package model;

import java.sql.*;
import java.util.LinkedList;

/**
 * Gestore delle operazioni di ricerca e visualizzazione dei libri nel database.
 * <p>
 * Permette di effettuare ricerche per titolo, autore, combinazione autore-anno,
 * e di visualizzare informazioni dettagliate sui libri.
 * </p>
 * @author Broggini Luca
 * @author De Nicola Matteo
 * @author Pianezzola Andrea
 * @author Saccon Alessandro
 * @author Siragusa Valerio
 * @version 1.0
 */
public class RepositoryLibri {
    
    /**
     * Costruttore di default.
     */
    public RepositoryLibri(){}

    /**
     * Cerca libri che corrispondono al titolo specificato.
     * @param titolo titolo o parte del titolo da ricercare
     * @return lista di libri trovati che corrispondono al titolo
     */
    public LinkedList<Libro> ricercaPerTitolo(String titolo) {
        LinkedList<Libro> libriTrovati = new LinkedList<>();
        String query = "SELECT * FROM libri WHERE title ILIKE ?";

        try (Connection connection = DriverManager.getConnection(SERVER_BOOK.url, SERVER_BOOK.userSQL, SERVER_BOOK.passSQL);
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, "%" + titolo + "%");
            try (ResultSet result = statement.executeQuery()) {
                while (result.next()) {
                    libriTrovati.add(new Libro(
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
            System.err.println("ricercaPerTitolo SQLState=" + e.getSQLState() + " msg=" + e.getMessage());
        }
        return libriTrovati;
    }
    
    /**
     * Cerca libri di cui l'autore corrisponde al parametro specificato.
     * @param autore autore o parte dell'autore da ricercare
     * @return lista di libri trovati con quell'autore
     */
    public LinkedList<Libro> ricercaPerAutore(String autore) {
        LinkedList<Libro> libriTrovati = new LinkedList<>();
        String query = "SELECT * FROM libri WHERE authors ILIKE ?";

        try (Connection connection = DriverManager.getConnection(SERVER_BOOK.url, SERVER_BOOK.userSQL, SERVER_BOOK.passSQL);
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, "%" + autore + "%");
            try (ResultSet result = statement.executeQuery()) {
                while (result.next()) {
                    libriTrovati.add(new Libro(
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
            System.err.println("ricercaPerAutore SQLState=" + e.getSQLState() + " msg=" + e.getMessage());
        }
        return libriTrovati;
    }

    /**
     * Cerca libri in base a autore e anno specificati.
     *
     * @param autore autore o parte dell'autore da ricercare
     * @param anno anno di pubblicazione da ricercare
     * @return lista di libri trovati con quell'autore e anno
     */
    public LinkedList<Libro> ricercaPerAutoreAnno(String autore, String anno) {
        LinkedList<Libro> libriTrovati = new LinkedList<>();
        String query = "SELECT * FROM libri WHERE authors ILIKE ? AND publish_date_year ILIKE ?";

        try (Connection connection = DriverManager.getConnection(SERVER_BOOK.url, SERVER_BOOK.userSQL, SERVER_BOOK.passSQL);
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, "%" + autore + "%");
            statement.setString(2, "%" + anno + "%");
            try (ResultSet result = statement.executeQuery()) {
                while (result.next()) {
                    libriTrovati.add(new Libro(
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
            System.err.println("ricercaPerAutoreAnno SQLState=" + e.getSQLState() + " msg=" + e.getMessage());
        }
        return libriTrovati;
    }
   
    /**
     * Visualizza informazioni o statistiche relative a un libro trovato per titolo.
     * <p>
     * L'argomento <code>i</code> stabilisce il tipo di informazione da visualizzare:
     * </p>
     * <ul>
     * <li>1: Informazioni generali sul libro</li>
     * <li>2: Media delle valutazioni e suggerimenti</li>
     * <li>3: Dettaglio sulle medie dei punteggi valutativi</li>
     * </ul>
     *
     * @param titolo titolo del libro da cercare
     * @param i modalità di visualizzazione (1: info, 2: media e suggerimenti, 3: dettaglio medie punteggi)
     * @return stringa con le informazioni richieste oppure messaggio di errore
     */
    public String visualizzaLibro(String titolo, int i) {
        Libro libroTrovato = null;
        String risultato = "";
        String query = "SELECT * FROM libri WHERE title ILIKE ?";

        try (Connection connection = DriverManager.getConnection(SERVER_BOOK.url, SERVER_BOOK.userSQL, SERVER_BOOK.passSQL);
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, "%" + titolo + "%");
            try (ResultSet result = statement.executeQuery()) {
                if (result.next()) {
                    libroTrovato = new Libro(
                        result.getInt("id"),
                        result.getString("title"),
                        result.getString("authors"),
                        result.getString("category"),
                        result.getString("publisher"),
                        result.getString("publish_date_year")
                    );
                }

                if (libroTrovato != null) {
                    switch (i) {
                        case 1:
                            risultato += ("<INFORMAZIONI DEL LIBRO CERCATO>\n");
                            risultato += (libroTrovato.getTitolo() + " " + libroTrovato.getAutori() + " " + libroTrovato.getCategoria() +
                                    libroTrovato.getEditore() + " " + libroTrovato.getAnno());
                            break;

                        case 2:
                            double somma = 0, media;
                            LinkedList<Libro> elencoLibriConsigliati = new LinkedList<>();

                            if (!libroTrovato.getValutazioni().isEmpty()) {
                                for (Valutazione j : libroTrovato.getValutazioni()) {
                                    somma += j.getVotoFinale();
                                }
                                media = somma / libroTrovato.getValutazioni().size();
                            } else {
                                media = 0;
                            }
                            risultato += ("Media delle valutazioni complessive dati dagli utenti ==> " + media + "\n");

                            if (!libroTrovato.getSuggerimenti().isEmpty()) {
                                risultato += ("LIBRI SUGGERITI PER QUESTO LIBRO: \n");
                                for (int j : libroTrovato.getSuggerimenti()) {
                                    Libro l = Libro.getLibro(j);
                                    if (l != null) elencoLibriConsigliati.add(l);
                                }
                                for (Libro j : elencoLibriConsigliati) {
                                    risultato += (j.getTitolo() + "\n");
                                }
                            } else {
                                risultato += ("Non sono presenti suggerimenti per questo libro!");
                            }
                            break;

                        case 3:
                            if (!libroTrovato.getValutazioni().isEmpty()) {
                                int sommaStile = 0, sommaContenuto = 0, sommaGradevolezza = 0, sommaOriginalità = 0, sommaEdizione = 0;
                                int cont = libroTrovato.getValutazioni().size();

                                for (Valutazione j : libroTrovato.getValutazioni()) {
                                    sommaStile += j.getStile();
                                    sommaContenuto += j.getContenuto();
                                    sommaGradevolezza += j.getGradevolezza();
                                    sommaOriginalità += j.getOriginalità();
                                    sommaEdizione += j.getEdizione();
                                }
                                double mediaStile = (cont == 0) ? 0 : (double) sommaStile / cont;
                                double mediaContenuto = (cont == 0) ? 0 : (double) sommaContenuto / cont;
                                double mediaGradevolezza = (cont == 0) ? 0 : (double) sommaGradevolezza / cont;
                                double mediaOriginalità = (cont == 0) ? 0 : (double) sommaOriginalità / cont;
                                double mediaEdizione = (cont == 0) ? 0 : (double) sommaEdizione / cont;

                                risultato += ("Questo libro è stato valutato " + cont + " volte!\n" +
                                        "Elenco media punteggi ==> \nStile: " + mediaStile + "\nContenuto: " + mediaContenuto + "\nGradevolezza: " + mediaGradevolezza +
                                        "\nOriginalità: " + mediaOriginalità + "\nEdizione: " + mediaEdizione +
                                        "\nGli utenti hanno suggerito questo libro ==> " + libroTrovato.getSuggerimenti().size() + " volte!");
                            } else {
                                risultato += ("Non sono presenti valutazioni su questo libro!");
                            }
                            break;

                        default:
                            risultato += ("SCELTA NON ESISTENTE!");
                            break;
                    }
                }
            }
        } 
        catch (SQLException e) {
            System.err.println("visualizzaLibro SQLState=" + e.getSQLState() + " msg=" + e.getMessage());
        }
        return risultato;
    }
    
}