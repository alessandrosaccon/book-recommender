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
 * Controller della vista "Suggerimenti per libro".
 * <p>
 * Costruisce l'interfaccia con barra di ricerca, pulsanti di azione e lista risultati,
 * gestisce la ricerca asincrona dei suggerimenti per titolo tramite il servizio client
 * e fornisce navigazione al Repository.
 * @author Broggini Luca
 * @author De Nicola Matteo
 * @author Pianezzola Andrea
 * @author Saccon Alessandro
 * @author Siragusa Valerio
 * @version 1.0
 */
public class viewSuggestionController {

    /**
     * <code>mainApp</code>
     * Riferimento all’applicazione principale JavaFX, usato per la navigazione e l’accesso al layout radice.
     */
    private Bookapp mainApp;

    /**
     * <code>clientService</code>
     * Servizio client per la comunicazione con il server (richieste/risposte).
     */
    private ClientService clientService;

    /**
     * <code>alertI</code>
     * Finestra di dialogo informativa per comunicazioni non critiche.
     * <p>
     * <code>alertE</code>
     * Finestra di dialogo per la segnalazione di errori critici.
     */
    private Alert alertI, alertE;

    /**
     * <code>searchLibro</code>
     * Campo di input per digitare il titolo del libro da ricercare.
     */
    private TextField searchLibro;

    /**
     * <code>resultsList</code>
     * Lista dei risultati testuali mostrati all’utente dopo la ricerca.
     */
    private ListView<String> resultsList;
    
    /**
     * Crea il controller, inizializza gli avvisi, memorizza i riferimenti
     * all'app principale e al servizio client e costruisce la vista.
     * @param mainApp riferimento all'applicazione principale per la navigazione
     * @param clientService servizio client usato per le richieste al server
     * @throws IllegalArgumentException se mainApp o clientService sono null
     */
    public viewSuggestionController(Bookapp mainApp, ClientService clientService) {
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
     * Costruisce il layout della vista: crea campo di ricerca, pulsanti di azione
     * (Cerca, Indietro) e lista risultati; registra gli handler degli eventi e
     * imposta il contenuto nel layout radice dell'app.
     */
    private void createView()
    {
        if (Bookapp.rootLayout == null) {
            alertE.setContentText("Layout non inizializzato. Riavvia l'applicazione.");
            alertE.showAndWait();
            return;
        }

        VBox card = new VBox();
        HBox topBar = new HBox(10);
        HBox topBar2 = new HBox(10);
        
        card.getStyleClass().add("card-big");

        Label label = new Label("Suggerimenti per libro:");
        label.getStyleClass().add("label-big");

        searchLibro = new TextField();
        searchLibro.getStyleClass().add("text-field");

        Button searchBtn = new Button("🔍 Cerca");
        searchBtn.getStyleClass().add("main-btn-small");
        Button backBtn = new Button("Indietro");
        backBtn.getStyleClass().add("back-btn-small");

        searchBtn.setOnAction(e -> performSearch()); 
        backBtn.setOnAction(e -> mainApp.showRepository());

        topBar.setAlignment(Pos.CENTER);
        topBar2.setAlignment(Pos.CENTER);
        topBar2.getChildren().addAll(searchBtn, backBtn);
        topBar.getChildren().addAll(label, searchLibro);

        resultsList = new ListView<>();
        card.getChildren().addAll(topBar, topBar2, resultsList);
        mainApp.rootLayout.setCenter(card);
    }
    
    /**
     * Esegue la ricerca dei suggerimenti per il titolo inserito:
     * valida l'input, avvia un Task asincrono che interroga il server,
     * aggiorna la lista risultati al successo o mostra un messaggio di errore al fallimento.
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
                return clientService.VisualizzaLibro(titolo, 2);
            }
        };

        searchTask.setOnSucceeded(e -> {
            String risultati = searchTask.getValue();
            resultsList.getItems().clear();
            if (risultati.isEmpty()) 
                resultsList.getItems().add("NESSUN LIBRO TROVATO PER: " + titolo.toUpperCase());
            else 
                resultsList.getItems().add(risultati);
            
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