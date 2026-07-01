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
import javafx.scene.control.*;
import javafx.scene.layout.*;

/**
 * Gestisce la vista del menu "Ricerca Informazioni".
 * Fornisce all'utente le opzioni per scegliere il tipo di ricerca da effettuare
 * (per titolo, per autore, o per autore e anno), reindirizzando ai
 * controller specifici.
 * @author Broggini Luca
 * @author De Nicola Matteo
 * @author Pianezzola Andrea
 * @author Saccon Alessandro
 * @author Siragusa Valerio
 * @version 1.0
 */
public class ricercaInfoController {

    /**
     * <code>mainApp</code>
     * Riferimento all'applicazione principale (Bookapp).
     * @see app.Bookapp
     */
    private Bookapp mainApp;
    
    /**
     * <code>clientService</code>
     * Servizio client per la comunicazione con il server.
     * @see client.ClientService
     */
    private ClientService clientService; 
    
    /**
     * Costruttore del controller di ricerca informazioni.
     * Inizializza l'applicazione principale, il servizio client e
     * chiama il metodo per creare la vista.
     * @param mainApp L'istanza principale dell'applicazione.
     * @param clientService Il servizio client per la comunicazione con il server.
     * @throws IllegalArgumentException se mainApp o clientService sono null
     */
    public ricercaInfoController(Bookapp mainApp, ClientService clientService) {
        if (mainApp == null || clientService == null) {
            throw new IllegalArgumentException("mainApp e clientService non possono essere null");
        }
        this.mainApp = mainApp;
        this.clientService = clientService; 
        createView();
    }

    /**
     * Costruisce l'interfaccia utente (UI) per il menu di selezione della ricerca.
     * Inizializza i pulsanti per "Ricerca per titolo", "Ricerca per autore",
     * "Ricerca per autore e anno" e "Indietro", impostando le relative azioni
     * di navigazione.
     */
    private void createView() {

        if (Bookapp.rootLayout == null) {
            Alert alertE = new Alert(Alert.AlertType.ERROR);
            alertE.setContentText("Layout non inizializzato. Riavvia l'applicazione.");
            alertE.showAndWait();
            return;
        }

        VBox card = new VBox(10);
        card.getStyleClass().add("card");

        Button ricercaInfo1libroBtn = new Button("Ricerca per titolo");
        Button ricercaInfo2libroBtn = new Button("Ricerca per autore");
        Button ricercaInfo3libroBtn = new Button("Ricerca per autore e anno");
        Button backBtn = new Button("Indietro");

        ricercaInfo1libroBtn.getStyleClass().add("main-btn");
        ricercaInfo2libroBtn.getStyleClass().add("main-btn");
        ricercaInfo3libroBtn.getStyleClass().add("main-btn");
        backBtn.getStyleClass().add("back-btn");

        ricercaInfo1libroBtn.setOnAction(e -> showCercaTitolo()); 
        ricercaInfo2libroBtn.setOnAction(e -> showCercaAutore());
        ricercaInfo3libroBtn.setOnAction(e -> showCercaAutoreAnno());
        backBtn.setOnAction(e -> mainApp.showRepository());

        card.getChildren().addAll(ricercaInfo1libroBtn, ricercaInfo2libroBtn, ricercaInfo3libroBtn, backBtn);
        mainApp.rootLayout.setCenter(card);

    }

    /**
     * Mostra la schermata per la ricerca per titolo.
     * Inizializza e visualizza il {@code ricercaTitoloController}.
     * @see view.ricercaTitoloController
     * @throws Exception se avvengono errori durante la creazione del controller
     */
    private void showCercaTitolo() {
        try {
            ricercaTitoloController controller = new ricercaTitoloController(mainApp, clientService);
        } 
        catch (Exception e) {
            System.err.println("showCercaTitolo: errore caricando la vista: " + e.getMessage());
        }
    }

    /**
     * Mostra la schermata per la ricerca per autore.
     * Inizializza e visualizza il {@code ricercaAutoreController}.
     * @see view.ricercaAutoreController
     * @throws Exception se avvengono errori durante la creazione del controller
     */
    private void showCercaAutore() {
        try {
            ricercaAutoreController controller = new ricercaAutoreController(mainApp, clientService);
        } 
        catch (Exception e) {
            System.err.println("showCercaAutore: errore caricando la vista: " + e.getMessage());
        }
    }

    /**
     * Mostra la schermata per la ricerca per autore e anno.
     * Inizializza e visualizza il {@code ricercaAutoreAnnoController}.
     * @see view.ricercaAutoreAnnoController
     * @throws Exception se avvengono errori durante la creazione del controller
     */
    private void showCercaAutoreAnno() {
        try {
            ricercaAutoreAnnoController controller = new ricercaAutoreAnnoController(mainApp, clientService);
        } 
        catch (Exception e) {
            System.err.println("showCercaAutoreAnno: errore caricando la vista: " + e.getMessage());
        }
    }

}