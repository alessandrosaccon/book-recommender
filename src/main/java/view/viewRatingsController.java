/* DE NICOLA MATTEO VINCENZO, MATRICOLA 757933, VA
    SIRAGUSA VALERIO, MATRICOLA 756227, VA
    SACCON ALESSANDRO, MATRICOLA 756145, VA
    PIANEZZOLA ANDREA, MATRICOLA 756141, VA
    BROGGINI LUCA, MATRICOLA 756446, VA
*/
package view;

import client.ClientService;
import app.Bookapp;

import javafx.concurrent.Task; 
import javafx.geometry.Pos;

import javafx.scene.control.*;
import javafx.scene.layout.*;

/**
 * Controller della vista "Valutazioni del libro".
 * <p>
 * Costruisce l'interfaccia con barra di ricerca e lista risultati,
 * esegue la ricerca asincrona delle valutazioni per titolo tramite il servizio client
 * e consente il ritorno al Repository.
 * @author Broggini Luca
 * @author De Nicola Matteo
 * @author Pianezzola Andrea
 * @author Saccon Alessandro
 * @author Siragusa Valerio
 * @version 1.0
 */
public class viewRatingsController {

    /**
     * Riferimento all’applicazione principale JavaFX, usato per la navigazione e l’accesso al layout radice.
     */
    private Bookapp mainApp;

    /**
     * Servizio client per la comunicazione con il server (richieste/risposte).
     */
    private ClientService clientService;

    /**
     * Finestra di dialogo informativa per comunicazioni non critiche.
     */
    private Alert alertI;

    /**
     * Finestra di dialogo per la segnalazione di errori critici.
     */
    private Alert alertE;

    /**
     * Campo di input per digitare il titolo del libro da ricercare.
     */
    private TextField searchLibro;

    /**
     * Lista dei risultati testuali mostrati all’utente dopo la ricerca.
     */
    private ListView<String> resultsList;
    
    /**
     * Crea il controller, inizializza gli avvisi, memorizza i riferimenti
     * all'app principale e al servizio client e costruisce la vista.
     * @param mainApp riferimento all’applicazione principale per la navigazione
     * @param clientService servizio client usato per le richieste al server
     * @throws IllegalArgumentException se mainApp o clientService sono null
     */
    public viewRatingsController(Bookapp mainApp, ClientService clientService) {
        if (mainApp == null || clientService == null) {
            throw new IllegalArgumentException("mainApp e clientService non possono essere null");
        }
        this.mainApp = mainApp;
        this.clientService = clientService; 
        alertI = new Alert(Alert.AlertType.INFORMATION);
        alertE = new Alert(Alert.AlertType.ERROR);
        createView();
    }

    /**
     * Costruisce il layout della vista:
     * <p>
     * - crea label e campo di ricerca,
     * - crea i pulsanti (Cerca, Indietro) e collega gli handler,
     * - impagina i nodi con due barre orizzontali e un contenitore principale,
     * - imposta la lista dei risultati e monta il tutto nel layout radice.
     */
    private void createView() {

        if (Bookapp.rootLayout == null) {
            alertE.setContentText("Layout non inizializzato. Riavvia l'applicazione.");
            alertE.showAndWait();
            return;
        }

        VBox card = new VBox();
        HBox topBar = new HBox(10);
        HBox topBar2 = new HBox(10);
        
        card.getStyleClass().add("card-big");

        Label label = new Label("Valutazioni del libro:");
        label.getStyleClass().add("label-big");

        searchLibro = new TextField();
        searchLibro.getStyleClass().add("text-field");

        Button searchBtn = new Button("🔍 Cerca");
        searchBtn.getStyleClass().add("main-btn-small");
        Button backBtn = new Button("Indietro");
        backBtn.getStyleClass().add("back-btn-small");

        searchBtn.setOnAction(e -> performSearch());
        backBtn.setOnAction(e -> {
            try {
                mainApp.showRepository();
            } catch (Exception ex) {
                System.err.println("Errore durante il ritorno al Repository: " + ex.getMessage());
            }
        });

        topBar.setAlignment(Pos.CENTER);
        topBar2.setAlignment(Pos.CENTER);
        topBar.getChildren().addAll(label, searchLibro);
        topBar2.getChildren().addAll(searchBtn, backBtn);

        resultsList = new ListView<>();
        card.getChildren().addAll(topBar, topBar2, resultsList);

        if (mainApp.rootLayout == null) {
            throw new IllegalStateException("rootLayout non è inizializzato in mainApp");
        }
        mainApp.rootLayout.setCenter(card);
    }
    
    /**
     * Esegue la ricerca delle valutazioni per il titolo inserito:
     * <p>
     * - valida l'input e mostra un messaggio se vuoto,
     * - lancia un Task asincrono che interroga il server (azione 3),
     * - aggiorna la lista risultati al successo o mostra un alert in caso di errore.
     */
    private void performSearch() {
        String titolo = searchLibro.getText().trim();
        resultsList.getItems().clear();
        if (titolo.isEmpty()) {
            alertI.setTitle("CAMPO MANCANTE");
            alertI.setHeaderText(null);
            alertI.setContentText("INSERIRE IL TITOLO.");
            alertI.showAndWait();       
            return;
        }

        Task<String> searchTask = new Task<>() {
            @Override
            protected String call() throws Exception {
                return clientService.VisualizzaLibro(titolo, 3);
            }
        };

        searchTask.setOnSucceeded(e -> {
            String risultati = searchTask.getValue();
            resultsList.getItems().clear();
            if (risultati.isEmpty()) {
                resultsList.getItems().add("NESSUN LIBRO TROVATO PER: " + titolo.toUpperCase());
            } else {
                resultsList.getItems().add(risultati); 
            }
        });

        searchTask.setOnFailed(e -> {
            resultsList.getItems().clear();
            alertE.setContentText("ERRORE DURANTE LA RICERCA.");
            alertE.showAndWait();            
            searchTask.getException().printStackTrace(); 
        });

        new Thread(searchTask).start();
    }

}
