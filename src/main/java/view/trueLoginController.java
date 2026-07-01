/* DE NICOLA MATTEO VINCENZO, MATRICOLA 757933, VA
    SIRAGUSA VALERIO, MATRICOLA 756227, VA
    SACCON ALESSANDRO, MATRICOLA 756145, VA
    PIANEZZOLA ANDREA, MATRICOLA 756141, VA
    BROGGINI LUCA, MATRICOLA 756446, VA
*/
package view;

import client.ClientService;
import model.Libro; 
import java.util.LinkedList;
import app.Bookapp;

import javafx.concurrent.Task; 
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.application.Platform;

/**
 * Gestisce la vista del menu principale per un utente che ha effettuato l'accesso.
 * Fornisce i pulsanti di navigazione per le funzionalità principali dell'utente loggato,
 * come la creazione di librerie, l'aggiunta di libri, l'inserimento di valutazioni
 * e suggerimenti, e l'uscita dall'applicazione.
 * @author Broggini Luca
 * @author De Nicola Matteo
 * @author Pianezzola Andrea
 * @author Saccon Alessandro
 * @author Siragusa Valerio
 * @version 1.0
 */

public class trueLoginController {

    /**
     * <code>mainApp</code>
     * Riferimento all’applicazione principale JavaFX, usato per navigazione e accesso al layout radice.
     * @see app.Bookapp
     */
    private Bookapp mainApp;

     /**
      * <code>loginController</code>
      * Controller responsabile della vista/flow di login, usato per coordinare autenticazione e UI.
      * @see view.loginController
      */
    private loginController loginController;

     /**
      * <code>clientService</code>
      * Servizio client per richieste/risposte verso il server applicativo.
      * @see client.ClientService
      */
    private ClientService clientService;

     /**
      * <code>searchField1</code>
      * Campo di ricerca testuale principale.
      */
     private TextField searchField1;

     /**
      * <code>searchField2</code>
      * Campo di ricerca testuale secondario.
      */
     private TextField searchField2;

     /**
      * <code>passwordField</code>
      * Campo password per l’inserimento di credenziali in modo offuscato.
      */
    private PasswordField passwordField;

     /**
      * <code>resultsList</code>
      * Lista dei risultati testuali da mostrare all’utente dopo ricerche/azioni.
      */
    private ListView<String> resultsList;
    
    /**
     * Costruttore del controller del menu utente loggato.
     * Inizializza l'applicazione principale, il controller di login, il servizio client
     * e chiama il metodo per creare la vista.
     * @param mainApp L'istanza principale dell'applicazione.
     * @param loginController Il controller della vista di login (usato per la navigazione).
     * @param clientService Il servizio client per la comunicazione con il server.
     * @throws IllegalArgumentException se mainApp o clientService sono null
     */

    public trueLoginController(Bookapp mainApp, loginController loginController, ClientService clientService) {
        if (mainApp == null || clientService == null || loginController == null) {
            throw new IllegalArgumentException("mainApp clientService e loginController non possono essere null");
        }
        this.mainApp = mainApp;
        this.loginController = loginController;
        this.clientService = clientService; 
        createView();
    }

    /**
     * Costruisce l'interfaccia utente (UI) per il menu principale dell'utente.
     * Inizializza i pulsanti per "Crea libreria", "Aggiungere un libro",
     * "Inserisci valutazione", "Inserisci suggerimento" e "Esci",
     * impostando le relative azioni.
     */
    public void createView() {

        if (Bookapp.rootLayout == null) {
            Alert alertE = new Alert(Alert.AlertType.ERROR);
            alertE.setContentText("Layout non inizializzato. Riavvia l'applicazione.");
            alertE.showAndWait();
            return;
        }

        VBox card = new VBox(18);
        card.getStyleClass().add("card");

        Button crealibreriaBtn = new Button("Crea libreria");
        crealibreriaBtn.getStyleClass().add("main-btn");
        Button aggiungiLibroBtn = new Button("Aggiungere un libro");
        aggiungiLibroBtn.getStyleClass().add("main-btn");
        Button inserisciValLibroBtn = new Button("Inserisci valutazione");
        inserisciValLibroBtn.getStyleClass().add("main-btn");
        Button inserisciSugLibroBtn = new Button("Inserisci suggerimento");
        inserisciSugLibroBtn.getStyleClass().add("main-btn");
        Button escButton = new Button("Esci");
        escButton.getStyleClass().add("back-btn");

        crealibreriaBtn.setOnAction(e -> showCreaLibreria());
        aggiungiLibroBtn.setOnAction(e -> showAggiungiLibro());
        inserisciValLibroBtn.setOnAction(e -> showAggiungiValutazione());
        inserisciSugLibroBtn.setOnAction(e -> showAggiungiSuggerimento());
        escButton.setOnAction(e -> {
            try {
                Platform.exit();
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        });

        card.getChildren().addAll(crealibreriaBtn, aggiungiLibroBtn, inserisciValLibroBtn, inserisciSugLibroBtn, escButton);
        Bookapp.rootLayout.setCenter(card);
    }

    /**
     * Mostra la schermata per la creazione di una nuova libreria.
     * Inizializza e visualizza il {@code creaLibreriaController}.
     * @see view.creaLibreriaController
     */
    public void showCreaLibreria() {
        try {
            creaLibreriaController controller = new creaLibreriaController(mainApp, loginController, clientService);
        } 
        catch (Exception e) {
            System.err.println("showCreaLibreria: errore caricando la vista: " + e.getMessage());
        }
    }

    /**
     * Mostra la schermata per aggiungere un libro a una libreria esistente.
     * Inizializza e visualizza il {@code aggiungiLibroController}.
     * @see view.aggiungiLibroController
     */
    public void showAggiungiLibro() {
        try {
            aggiungiLibroController controller = new aggiungiLibroController(mainApp, loginController, clientService);
        }    
        catch (Exception e) {
            System.err.println("showAggiungiLibro: errore caricando la vista: " + e.getMessage());
        }
    }

    /**
     * Mostra la schermata per inserire una valutazione a un libro.
     * Inizializza e visualizza il {@code aggiungiValutazioneController}.
     * @see view.aggiungiValutazioneController
     */
    public void showAggiungiValutazione() {
        try {
            aggiungiValutazioneController controller = new aggiungiValutazioneController(mainApp, loginController, clientService);
        } 
        catch (Exception e) {
            System.err.println("showAggiungiValutazione: errore caricando la vista: " + e.getMessage());
        }
    }

    /**
     * Mostra la schermata per inserire un suggerimento (recensione).
     * Inizializza e visualizza il {@code aggiungiSuggerimentoController}.
     * @see view.aggiungiSuggerimentoController
     */
    public void showAggiungiSuggerimento() {
        try {
            aggiungiSuggerimentoController controller = new aggiungiSuggerimentoController(mainApp, loginController, clientService);
        }    
        catch (Exception e) {
            System.err.println("showAggiungiSuggerimento: errore caricando la vista: " + e.getMessage());
        }
    }

}