/* DE NICOLA MATTEO VINCENZO, MATRICOLA 757933, VA
    SIRAGUSA VALERIO, MATRICOLA 756227, VA
    SACCON ALESSANDRO, MATRICOLA 756145, VA
    PIANEZZOLA ANDREA, MATRICOLA 756141, VA
    BROGGINI LUCA, MATRICOLA 756446, VA
*/
package client;
 
import model.*;

import java.io.*;
import java.net.*;
import java.util.LinkedList;

/**
 * Classe che gestisce la comunicazione con il server tramite socket.
 * <p>Permette di connettersi, inviare richieste e ricevere risposte
 * per funzioni legate a libri, utenti, librerie, valutazioni e suggerimenti.</p>
 * <p>Invia comandi al server e attende oggetti di ritorno deserializzati.</p>
 * @author Broggini Luca
 * @author De Nicola Matteo
 * @author Pianezzola Andrea
 * @author Saccon Alessandro
 * @author Siragusa Valerio
 * @version 1.0
 */
public class ClientService {

    /**
     * <code>s</code>
     * Socket TCP utilizzato per la connessione attiva con il peer.
     * <p>Deve essere chiuso al termine della sessione per evitare leak di risorse.</p>
     */
    private Socket s;

    /**
     * <code>out</code>
     * Stream di output per inviare oggetti serializzati sul socket.
     * <p>Va istanziato una sola volta per connessione e riutilizzato.</p>
     */
    private ObjectOutputStream out;

    /**
     * <code>in</code>
     * Stream di input per ricevere oggetti serializzati dal socket.
     * <p>Va istanziato una sola volta per connessione e riutilizzato.</p>
     */
    private ObjectInputStream in;

    /**
     * Costruttore vuoto.
     */
    public ClientService(){}


    /**
     * Stabilisce una connessione al server con l'host e la porta specificati.
     * @return true se la connessione ha successo, false in caso di errore
     */
    public boolean exec() {
        try {
            InetAddress addr = InetAddress.getByName(null); 
            s = new Socket(addr, 8080);
            out = new ObjectOutputStream(s.getOutputStream());
            out.flush(); 
            in = new ObjectInputStream(s.getInputStream());
            return true;
        } 
        catch (IOException e) {
            System.err.println("exec I/O error: " + e.getMessage());
            try { if (in != null) in.close(); } catch (IOException ignore) {}
            try { if (out != null) out.close(); } catch (IOException ignore) {}
            try { if (s != null && !s.isClosed()) s.close(); } catch (IOException ignore) {}
            s = null; out = null; in = null;
            return false;
        }
    }

    /**
     * Chiude la connessione con il server.
     */
    public synchronized void close() {
        try {
            if (out != null) {
                out.writeObject("END");
                out.flush();
            }
        } 
        catch (IOException e) {}
        try { if (out != null) out.close(); } catch (IOException ignore) {}
        try { if (in != null) in.close(); } catch (IOException ignore) {}
        try { if (s != null && !s.isClosed()) s.close(); } catch (IOException ignore) {}
        s = null; out = null; in = null;
    }

    /**
     * Esegue una ricerca di libri per titolo inviando la richiesta al server e restituendo la lista risultante.
     * @param titolo nome o stringa da ricercare nel titolo dei libri 
     * @return lista dei libri che corrispondono al titolo specificato 
     * @throws IOException se si verifica un errore di I/O durante l'invio o la ricezione dei dati
     * @throws ClassNotFoundException se la classe degli oggetti ricevuti non è disponibile nel client
     */
    public synchronized LinkedList<Libro> ricercaPerTitolo(String titolo) throws IOException, ClassNotFoundException {
        out.writeObject("RicercaPerTitolo");
        out.writeObject(titolo);
        return (LinkedList<Libro>) in.readObject();
    }

    /**
     * Esegue una ricerca di libri per autore inviando la richiesta al server e restituendo la lista risultante.
     * @param autore nome o stringa da ricercare nell'autore dei libri 
     * @return lista dei libri che corrispondono all'autore specificato 
     * @throws IOException se si verifica un errore di I/O durante l'invio o la ricezione dei dati
     * @throws ClassNotFoundException se la classe degli oggetti ricevuti non è disponibile nel client
     */
    public synchronized LinkedList<Libro> ricercaPerAutore(String autore) throws IOException, ClassNotFoundException {
        out.writeObject("RicercaPerAutore");
        out.writeObject(autore);
        return (LinkedList<Libro>) in.readObject();

    }

    /**
     * Esegue una ricerca di libri filtrando per autore e anno, inviando la richiesta al server e restituendo i risultati. 
     * @param autore nome o stringa autore da filtrare 
     * @param anno anno di pubblicazione da filtrare (formato atteso lato server) 
     * @return lista dei libri che soddisfano i filtri autore e anno 
     * @throws IOException se avviene un errore di I/O sul canale di comunicazione 
     * @throws ClassNotFoundException se la classe degli oggetti risultanti non è risolvibile 
     */
    public synchronized LinkedList<Libro> ricercaPerAutoreAnno(String autore, String anno) throws IOException, ClassNotFoundException {
        out.writeObject("RicercaPerAutoreAnno");
        out.writeObject(autore);
        out.writeObject(anno);
        return (LinkedList<Libro>) in.readObject();

    }

    /**
     * Richiede al server la visualizzazione/recupero di informazioni relative a un libro, identificato dal titolo e da una scelta/azione. 
     * @param titolo titolo del libro da visualizzare 
     * @param scelta codice o opzione che specifica il tipo di visualizzazione richiesta 
     * @return stringa con le informazioni del libro restituite dal server 
     * @throws IOException se si verifica un errore di I/O during la richiesta/risposta 
     * @throws ClassNotFoundException se il tipo dell'oggetto ricevuto non è disponibile 
     */
    public synchronized String VisualizzaLibro(String titolo, int scelta)throws IOException, ClassNotFoundException
    {
        out.writeObject("VisualizzaLibro");
        out.writeObject(titolo);
        out.writeObject(scelta);
        return (String)in.readObject();
    }

    /**
     * Verifica la validità formale del codice fiscale delegando il controllo al server. 
     * @param cf codice fiscale da validare 
     * @return true se il codice fiscale è valido, false altrimenti 
     * @throws IOException in caso di errore di I/O nel canale di comunicazione 
     * @throws ClassNotFoundException se il tipo dell'oggetto booleano ricevuto non è risolvibile 
     */
    public synchronized Boolean validitaCF(String cf) throws IOException, ClassNotFoundException {
        out.writeObject("validitaCF");
        out.writeObject(cf);
        return (Boolean) in.readObject();
    }

    /**
     * Verifica se un codice fiscale esiste già nel sistema interrogando il server. 
     * @param cf codice fiscale da controllare 
     * @return true se esiste un utente con il codice fiscale indicato, false altrimenti 
     * @throws IOException se si verifica un errore di I/O during la comunicazione 
     * @throws ClassNotFoundException se la classe dell'oggetto di risposta non è disponibile 
     */
    public synchronized Boolean esisteCF(String cf) throws IOException, ClassNotFoundException {
        out.writeObject("esisteCF");
        out.writeObject(cf);
        return (Boolean) in.readObject();
    }

    /**
     * Controlla l’esistenza/validità di un utente con l’identificatore fornito, delegando la verifica al server. 
     * @param userID identificatore dell'utente da verificare 
     * @return true se l’utente esiste/risulta valido, false altrimenti 
     * @throws IOException in caso di errore di I/O during la richiesta 
     * @throws ClassNotFoundException se il tipo della risposta non è risolvibile 
     */
    public synchronized Boolean controllaUtente(String userID) throws IOException, ClassNotFoundException {
        out.writeObject("controllaUtente");
        out.writeObject(userID);
        return (Boolean) in.readObject();
    }

    /**
     * Registra un nuovo utente inviando i dati di registrazione al server. 
     * @param nome nome dell'utente 
     * @param cognome cognome dell'utente 
     * @param CF codice fiscale dell'utente 
     * @param mail indirizzo email dell'utente 
     * @param user username richiesto 
     * @param password password scelta 
     * @return true se la registrazione va a buon fine, false altrimenti 
     * @throws IOException se si verifica un errore di I/O nella comunicazione 
     * @throws ClassNotFoundException se il tipo della risposta non è disponibile 
     */
    public synchronized Boolean Registrazione(String nome, String cognome, String CF, String mail, String user, String password)throws IOException, ClassNotFoundException
    {
        out.writeObject("registraUtente");
        out.writeObject(nome);
        out.writeObject(cognome);
        out.writeObject(CF);
        out.writeObject(mail);
        out.writeObject(user);
        out.writeObject(password);
        return (Boolean)in.readObject();
    }

    /**
     * Effettua il login inviando credenziali al server e restituendo l’oggetto utente autenticato in caso di successo. 
     * @param username nome utente 
     * @param password password 
     * @return l'istanza di Utente autenticata oppure null/valore equivalente se le credenziali non sono valide 
     * @throws IOException se si verifica un errore di I/O during la comunicazione 
     * @throws ClassNotFoundException se la classe Utente non è risolvibile in deserializzazione 
     */
    public synchronized Utente login(String username, String password) throws IOException, ClassNotFoundException {
        out.writeObject("login");
        out.writeObject(username);
        out.writeObject(password);
        return (Utente)in.readObject();
    }

    /**
     * Verifica l'esistenza di una libreria dell'utente con il nome indicato, demandando la verifica al server.
     * @param userID identificatore dell'utente proprietario 
     * @param nomeLibreria nome della libreria da verificare 
     * @return true se la libreria esiste per l'utente, false altrimenti 
     * @throws IOException in caso di errore di I/O during la richiesta 
     * @throws ClassNotFoundException se la risposta non è risolvibile 
     */
    public synchronized Boolean controlloLibreria(int userID, String nomeLibreria) throws IOException, ClassNotFoundException {
        out.writeObject("controlloLibreria");
        out.writeObject(userID);
        out.writeObject(nomeLibreria);
        return (Boolean)in.readObject();
    }

    /**
     * Recupera i dettagli di un libro identificato dal titolo, richiedendo l'oggetto Libro al server. 
     * @param titolo titolo del libro da recuperare 
     * @return l'istanza Libro corrispondente al titolo, oppure null se non trovato 
     * @throws IOException se si verifica un errore di I/O sul canale 
     * @throws ClassNotFoundException se la classe Libro non è disponibile nel client 
     */
    public synchronized Libro getLibro(String titolo)throws IOException, ClassNotFoundException
    {
        out.writeObject("getLibro");
        out.writeObject(titolo);
        return (Libro)in.readObject();
    }

    /**
     * Verifica se un libro è già presente in una libreria utente sul server. 
     * @param nomeLibreria nome della libreria 
     * @param userID identificatore dell'utente 
     * @param bookID identificatore del libro 
     * @return true se il libro risulta già presente, false altrimenti 
     * @throws IOException se avviene un errore di I/O during la richiesta 
     * @throws ClassNotFoundException se la risposta non è risolvibile 
     */
    public synchronized Boolean checkPresente(String nomeLibreria, int userID, int bookID) throws IOException, ClassNotFoundException {
        out.writeObject("checkPresente");
        out.writeObject(nomeLibreria);
        out.writeObject(userID);
        out.writeObject(bookID);
        return (Boolean)in.readObject();
    }

    /**
     * Registra una nuova libreria per l’utente indicato tramite il server. 
     * @param userID identificatore dell'utente 
     * @param nomeLibreria nome della nuova libreria 
     * @return true se la creazione ha successo, false altrimenti 
     * @throws IOException se si verifica un errore di I/O during la comunicazione 
     * @throws ClassNotFoundException se la risposta non è risolvibile 
     */
    public synchronized Boolean RegistraLibreria(int userID, String nomeLibreria)throws IOException, ClassNotFoundException
    {
        out.writeObject("registraLibreria");
        out.writeObject(userID);
        out.writeObject(nomeLibreria);
        return (Boolean)in.readObject();
    }

    /**
     * Aggiunge un libro alla libreria indicata dell'utente, inviando la richiesta al server. 
     * @param nomeLibreria nome della libreria a cui aggiungere il libro 
     * @param userID identificatore dell'utente 
     * @param bookID identificatore del libro da aggiungere 
     * @return true se l’aggiunta è avvenuta, false altrimenti 
     * @throws IOException in caso di errore di I/O sulla connessione 
     * @throws ClassNotFoundException se la risposta non è risolvibile 
     */
    public synchronized Boolean aggiungiLibro(String nomeLibreria, int userID, int bookID)throws IOException, ClassNotFoundException
    {
        out.writeObject("aggiungiLibro");
        out.writeObject(nomeLibreria);
        out.writeObject(userID);
        out.writeObject(bookID);
        return (Boolean)in.readObject();
    }

    /**
     * Restituisce l'elenco delle librerie appartenenti a un utente richiedendolo al server. 
     * @param userID identificatore dell'utente proprietario 
     * @return lista delle librerie dell'utente (può essere vuota) 
     * @throws IOException se avvengono errori di I/O nella richiesta/risposta 
     * @throws ClassNotFoundException se la classe Libreria non è risolvibile in deserializzazione 
     */
    public synchronized LinkedList<Libreria> getLibrerieUtente(int userID) throws IOException, ClassNotFoundException
    {
        out.writeObject("getLibrerieUtente");
        out.writeObject(userID);
        return (LinkedList<Libreria>)in.readObject();
    }
    
    /**
     * Restituisce l'elenco dei libri contenuti in una libreria utente, ottenendolo dal server. 
     * @param nomeLibreria nome della libreria 
     * @param userID identificatore dell'utente proprietario 
     * @return lista dei libri della libreria (può essere vuota) 
     * @throws IOException se si verifica un errore di I/O during la comunicazione 
     * @throws ClassNotFoundException se la classe Libro non è risolvibile 
     */
    public synchronized LinkedList<Libro> getElencoLibri(String nomeLibreria, int userID) throws IOException, ClassNotFoundException
    {
        out.writeObject("getElencoLibri");
        out.writeObject(nomeLibreria);
        out.writeObject(userID);
        return (LinkedList<Libro>)in.readObject();
    }

    /**
     * Inserisce una valutazione di un libro per un utente, inviando al server tutte le componenti della valutazione. 
     * @param userID identificatore dell'utente che valuta 
     * @param bookID identificatore del libro valutato 
     * @param sr parametro di valutazione (es. story rating) 
     * @param cr parametro di valutazione (es. characters rating) 
     * @param on nota/valutazione componente "on" 
     * @param p parametro di valutazione "p" 
     * @param en parametro di valutazione "en" 
     * @param overallNote nota complessiva testuale 
     * @param o parametro di valutazione "o" 
     * @param e parametro di valutazione "e" 
     * @param fv valore numerico "fv" 
     * @param sn testo "sn" 
     * @param cn testo "cn" 
     * @param plean testo "plean" 
     * @return true se l’inserimento è andato a buon fine, false altrimenti 
     * @throws IOException se avviene un errore di I/O nel canale di comunicazione 
     * @throws ClassNotFoundException se la risposta non è risolvibile 
     */
    public synchronized Boolean inserisciValutazioneLibro(int userID, int bookID, int sr, int cr, String on, int p, String en, String overallNote, int o, int e, double fv, String sn, String cn, String plean) throws IOException, ClassNotFoundException
    {
        out.writeObject("inserisciValutazioneLibro");
        out.writeObject(userID);
        out.writeObject(bookID);
        out.writeObject(sr);
        out.writeObject(cr);
        out.writeObject(on);
        out.writeObject(p);
        out.writeObject(en);
        out.writeObject(overallNote);
        out.writeObject(o);
        out.writeObject(e);
        out.writeObject(fv);
        out.writeObject(sn);
        out.writeObject(cn);
        out.writeObject(plean);
        return (Boolean)in.readObject();
    }

    /**
     * Restituisce il numero di consigli registrati per una coppia utente–libro ottenendolo dal server. 
     * @param userID identificatore dell'utente 
     * @param bookID identificatore del libro 
     * @return numero di consigli per la coppia utente–libro indicata 
     * @throws IOException se si verifica un errore di I/O during la comunicazione 
     * @throws ClassNotFoundException se il tipo della risposta non è risolvibile 
     */
    public synchronized int getNumeroConsigli(int userID, int bookID) throws IOException, ClassNotFoundException {
        out.writeObject("getNumeroConsigli");
        out.writeObject(userID);
        out.writeObject(bookID);
        return (int) in.readObject();
    }

    /**
     * Crea un nuovo suggerimento per una coppia utente–libro richiedendone l'inserimento al server e restituendo l'ID.
     * @param userID identificatore dell'utente 
     * @param bookID identificatore del libro selezionato 
     * @return l'ID del suggerimento creato, o un valore sentinella se la creazione fallisce 
     * @throws IOException se avviene un errore di I/O during la chiamata 
     * @throws ClassNotFoundException se il tipo della risposta non è risolvibile 
     */
    public synchronized int inserisciSuggerimento(int userID, int bookID) throws IOException, ClassNotFoundException {
        out.writeObject("inserisciSuggerimento");
        out.writeObject(userID);
        out.writeObject(bookID);
        return (int) in.readObject();
    }

    /**
     * Collega un libro a un suggerimento esistente sul server.
     * @param suggerimentoID identificatore del suggerimento 
     * @param bookID identificatore del libro da collegare 
     * @return true se l'associazione è stata registrata, false altrimenti 
     * @throws IOException se si verifica un errore di I/O during la comunicazione 
     * @throws ClassNotFoundException se il tipo della risposta non è risolvibile 
     */
    public synchronized Boolean inserisciSuggerimentoLibro(int suggerimentoID, int bookID) throws IOException, ClassNotFoundException {
        out.writeObject("inserisciSuggerimentoLibro");
        out.writeObject(suggerimentoID);
        out.writeObject(bookID);
        return (Boolean) in.readObject();
    }
}