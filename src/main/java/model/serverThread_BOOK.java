/* DE NICOLA MATTEO VINCENZO, MATRICOLA 757933, VA
    SIRAGUSA VALERIO, MATRICOLA 756227, VA
    SACCON ALESSANDRO, MATRICOLA 756145, VA
    PIANEZZOLA ANDREA, MATRICOLA 756141, VA
    BROGGINI LUCA, MATRICOLA 756446, VA
*/
package model;

import java.io.*;
import java.net.*;

/**
 * Thread che gestisce la comunicazione tra il server e un singolo client
 * nel sistema di raccomandazione libri.
 * <p>All'interno del thread, in base al comando ricevuto dal client,
 * vengono eseguite operazioni su libri, librerie, utenti, suggerimenti
 * e valutazioni interfacciandosi con le classi del modello.</p>
 * <p>Utilizza {@link ObjectInputStream} e {@link ObjectOutputStream} per 
 * leggere e scrivere oggetti tramite socket.</p>
 * @see SERVER_BOOK
 * @author Broggini Luca
 * @author De Nicola Matteo
 * @author Pianezzola Andrea
 * @author Saccon Alessandro
 * @author Siragusa Valerio
 * @version 1.0
 */
public class serverThread_BOOK extends Thread{
    /**
     * <code>socket</code>
     * Socket di comunicazione attivo verso il peer (client o server, a seconda del contesto).
     * <p>Va aperto/assegnato all’inizio della sessione e chiuso al termine.</p>
     */
    Socket socket;

    /**
     * <code>in</code>
     * Stream di input per ricevere oggetti serializzati dal peer tramite il socket.
     * <p>Deve essere creato una sola volta per connessione e riutilizzato.</p>
     */
    ObjectInputStream in;

    /**
     * <code>out</code>
     * Stream di output per inviare oggetti serializzati al peer tramite il socket.
     * <p>Deve essere creato una sola volta per connessione e riutilizzato.</p>
     */
    ObjectOutputStream out;

    /**
     * <code>utente</code>
     * Utente di dominio correntemente in uso/contesto (es. autenticato o oggetto in lavorazione).
     * <p>Può essere null se non impostato o non richiesto dall’operazione.</p>
     */
    Utente utente;

    /**
     * <code>libreria</code>
     * Libreria di dominio attualmente selezionata o in uso per le operazioni.
     * <p>Può essere null se non presente in questo contesto.</p>
     */
    Libreria libreria;

    /**
     * <code>libro</code>
     * Libro di dominio attualmente selezionato o oggetto principale dell’operazione.
     * <p>Può essere null se non impostato o non trovato.</p>
     */
    Libro libro;

    /**
     * <code>suggerimento</code>
     * Suggerimento correntemente gestito o prodotto dal sistema.
     * <p>Può essere null se non pertinente all’operazione corrente.</p>
     */
    Suggerimento suggerimento;

    /**
     * <code>valutazione</code>
     * Valutazione del libro correntemente gestita o in corso di creazione.
     * <p>Può essere null se non presente.</p>
     */
    Valutazione valutazione;

    /**
     * <code>repositoryLibri</code>
     * Repository/servizio di accesso ai dati dei libri utilizzato dal componente.
     * <p>Deve essere inizializzato prima dell’uso.</p>
     */
    RepositoryLibri repositoryLibri;

    /**
     * Costruisce un nuovo thread di gestione client associato a un socket e alle
     * istanze del modello necessario per le operazioni.
     * @param socket socket per la connessione con il client
     * @param utente oggetto per gestire operazioni sugli utenti
     * @param libreria oggetto per gestire operazioni sulle librerie
     * @param libro oggetto per gestire operazioni sui libri
     * @param valutazione oggetto per gestire le valutazioni
     * @param suggerimento oggetto per gestire suggerimenti
     * @param repositoryLibri oggetto per ricerca e gestione libri
     * @throws IOException in caso di errori negli stream di input/output
     */
    public serverThread_BOOK(Socket socket, Utente utente, Libreria libreria, Libro libro,
     Valutazione valutazione, Suggerimento suggerimento, RepositoryLibri repositoryLibri) throws IOException
    {
        this.socket = socket;
        out = new ObjectOutputStream(socket.getOutputStream());
        out.flush();
        in = new ObjectInputStream(socket.getInputStream());
        this.utente = utente;
        this.libreria = libreria;
        this.libro = libro;
        this.valutazione = valutazione;
        this.suggerimento = suggerimento;
        this.repositoryLibri = repositoryLibri;
    }

    /**
     * Esegue il ciclo di lettura dei comandi dal client e gestisce le richieste 
     * chiamando le opportune funzioni del modello.
     * <p>Gestisce comandi di ricerca libri, controllo utenti, registrazione,
     * login, gestione librerie, inserimento valutazioni e suggerimenti.</p>
     * <p>Quando termina la comunicazione chiude il socket client.</p>
     */
    public void run()
    {
        String comando = "";
        
        try{          
            while(true)
            {
                comando = (String)in.readObject();
                
                if(comando.equals("END")) {
                    break;
				}

                if(comando.equals("RicercaPerTitolo")) {
                    String t = (String)in.readObject();
                    out.writeObject(repositoryLibri.ricercaPerTitolo(t));
				}
                
                if(comando.equals("RicercaPerAutore")) {
					String a = (String)in.readObject();
                    out.writeObject(repositoryLibri.ricercaPerAutore(a));
				}

                if(comando.equals("RicercaPerAutoreAnno")) {
					String autore = (String)in.readObject();
                    String anno = (String)in.readObject();
                    out.writeObject(repositoryLibri.ricercaPerAutoreAnno(autore, anno));
				}

                if(comando.equals("VisualizzaLibro")){
                    String titolo = (String)in.readObject();
                    int scelta = (int)in.readObject();
                    out.writeObject(repositoryLibri.visualizzaLibro(titolo, scelta));
                }

                if(comando.equals("validitaCF")){
                    String cf = (String)in.readObject();
                    out.writeObject(Utente.controlloCF(cf));
                }

                if(comando.equals(("esisteCF"))){
                    String cf = (String)in.readObject();
                    out.writeObject(Utente.esisteCF(cf));
                }

                if(comando.equals(("controllaUtente"))){
                    String user = (String)in.readObject();
                    out.writeObject(Utente.controllaUtente(user));
                }

                if(comando.equals(("registraUtente"))){   
                    String nome = (String)in.readObject();
                    String cognome = (String)in.readObject();
                    String cf = (String)in.readObject();
                    String mail = (String)in.readObject();
                    String user = (String)in.readObject();
                    String pass = (String)in.readObject();
                    
                    out.writeObject(Utente.registrazione(nome, cognome, cf, mail, user, pass));
                }

                if(comando.equals(("login"))){
                    String user = (String)in.readObject();
                    String password = (String)in.readObject();
                    out.writeObject(Utente.Login(user, password));
                }

                if(comando.equals(("controlloLibreria"))){
                    int userID = (int)in.readObject();
                    String nomeLibreria = (String)in.readObject();
                    out.writeObject(Libreria.controlloLibreria(userID, nomeLibreria));
                }

                if(comando.equals(("getLibro"))){
                    String titolo = (String)in.readObject();
                    out.writeObject(Libro.getLibro(titolo));
                }

                if(comando.equals(("checkPresente"))){
                    String nomeLibreria = (String)in.readObject();
                    int userID = (int)in.readObject();
                    int bookID = (int)in.readObject();
                    out.writeObject(Libreria.checkPresente(nomeLibreria, userID, bookID));
                }

                if(comando.equals("registraLibreria")){
                    int userID = (int)in.readObject();
                    String nomeLibreria = (String)in.readObject();
                    out.writeObject(Libreria.registraLibreria(userID, nomeLibreria));
                }

                if(comando.equals("aggiungiLibro")) {
                    String nomeLibreria = (String)in.readObject();
					int userID = (int)in.readObject();
                    int bookID = (int)in.readObject();
					out.writeObject(Libreria.aggiungiLibro(nomeLibreria, userID, bookID));
				}

                if(comando.equals("getLibrerieUtente")) {
					int userID = (int)in.readObject();
					out.writeObject(Libreria.getLibrerieUtente(userID));
				}

                if(comando.equals("getElencoLibri")) {
                    String nomeLibreria = (String)in.readObject();
					int userID = (int)in.readObject();
					out.writeObject(Libreria.getElencoLibri(nomeLibreria, userID));
				}

                if(comando.equals("inserisciValutazioneLibro")) {
					int userID = (int)in.readObject();
                    int bookID = (int)in.readObject();
                    int sr = (int)in.readObject();
                    int cr = (int)in.readObject();
                    String on = (String)in.readObject();
                    int p = (int)in.readObject();
                    String en = (String)in.readObject();
                    String overallNote = (String)in.readObject();
                    int o = (int)in.readObject();
                    int e = (int)in.readObject();
                    double fv = (double)in.readObject();
                    String sn = (String)in.readObject();
                    String cn = (String)in.readObject();
                    String plean = (String)in.readObject();
					out.writeObject(Valutazione.inserisciValutazioneLibro(userID, bookID, sr, cr, p, on, en, overallNote, o, e, fv, sn, cn, plean));
				}

                if(comando.equals("getNumeroConsigli")) {
					int userID = (int)in.readObject();
                    int bookID = (int)in.readObject(); 
					out.writeObject(Suggerimento.getNumeroConsigli(userID, bookID));
				}

                if(comando.equals("inserisciSuggerimento")) {
					int userID = (int)in.readObject();
                    int bookID = (int)in.readObject(); 
					out.writeObject(Suggerimento.inserisciSuggerimento(userID, bookID));
				}

                if(comando.equals("inserisciSuggerimentoLibro")) {
					int suggerimentoID = (int)in.readObject();
                    int bookID = (int)in.readObject(); 
					out.writeObject(Suggerimento.inserisciSuggerimentoLibro(suggerimentoID, bookID));
				}
            }
        }
        catch (EOFException | SocketException e) {
            System.err.println("Connessione chiusa dal client: " + e.getMessage());
        } 
        catch (ClassNotFoundException e) {
            System.err.println("Tipo oggetto non riconosciuto dal server: " + e.getMessage());
        }
        catch (IOException e) {
            System.err.println("Errore I/O su socket/stream: " + e.getMessage());
        }
        finally{
            try {
                if (out != null) out.close();
            } 
            catch (IOException e) {}
            try {
                if (in != null) in.close();
            } 
            catch (IOException e) {}
            try {
                if (socket != null && !socket.isClosed()) socket.close();
            } 
            catch (IOException e) {
                System.err.println("Errore in chiusura socket: " + e.getLocalizedMessage());
            }
        }
    }
}