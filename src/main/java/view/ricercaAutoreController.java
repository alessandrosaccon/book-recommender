/* DE NICOLA MATTEO VINCENZO, MATRICOLA 757933, VA
    SIRAGUSA VALERIO, MATRICOLA 756227, VA
    SACCON ALESSANDRO, MATRICOLA 756145, VA
    PIANEZZOLA ANDREA, MATRICOLA 756141, VA
    BROGGINI LUCA, MATRICOLA 756446, VA
*/
package view;

import client.ClientService;
import app.Bookapp;
import model.Libro;
import java.util.LinkedList;

import javafx.concurrent.Task;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

/**
 * Gestisce la vista e la logica per la ricerca di libri in base all'autore.
 * Costruisce l'interfaccia utente con un campo di testo per l'autore e una lista
 * per visualizzare i risultati della ricerca.
 * <p>Nota: Il campo <code>searchLibro</code> e la variabile <code>titolo</code> sono usati per l'autore,
 * nonostante il nome.
 * @author Broggini Luca
 * @author De Nicola Matteo
 * @author Pianezzola Andrea
 * @author Saccon Alessandro
 * @author Siragusa Valerio
 * @version 1.0
 */

public class ricercaAutoreController {

    /**
     * <code>mainApp</code>
     * Riferimento all'applicazione principale (Bookapp).
     * @see app.Bookapp
     */
    private app.Bookapp mainApp;
    
    /**
     * <code>clientService</code>
     * Servizio client per la comunicazione con il server.
     * @see client.ClientService
     */
    private ClientService clientService;

    /**
     * <code>alertI</code>
     * Alert per messaggi informativi.
     */
    private Alert alertI;
    
    /**
     * <code>alertE</code>
     * Alert per messaggi di errore.
     */
    private Alert alertE;
    
    /**
     * <code>searchLibro</code>
     * Campo di testo per l'inserimento dell'autore (nonostante il nome).
     */
    private TextField searchLibro;
    
    /**
     * <code>resultsList</code>
     * ListView per visualizzare i risultati della ricerca.
     */
    private ListView<String> resultsList;

    /**
     * Costruttore del controller.
     * Inizializza i servizi necessari, gli alert e chiama il metodo per
     * costruire l'interfaccia utente.
     * @param mainApp L'istanza principale dell'applicazione.
     * @param clientService Il servizio client per la comunicazione con il server.
     * @throws IllegalArgumentException se i parametri dovessero essere null
     */
    public ricercaAutoreController(Bookapp mainApp, ClientService clientService) {
        if (mainApp == null || clientService == null) {
            throw new IllegalArgumentException("mainApp e clientService non possono essere null");
        }
        this.mainApp = mainApp;
        this.clientService = clientService;
        this.alertI = new Alert(Alert.AlertType.INFORMATION);
        this.alertE = new Alert(Alert.AlertType.ERROR);
        createView();
    }

    /**
     * Costruisce l'interfaccia utente (UI) per la ricerca per autore.
     * Configura l'etichetta, il campo di testo per l'autore,
     * i pulsanti "Cerca" e "Indietro", e la lista dei risultati.
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

        VBox vBar = new VBox(6);
        Label label = new Label("Inserisci autore da ricercare:");
        label.getStyleClass().add("label-big");

        searchLibro = new TextField();
        searchLibro.getStyleClass().add("text-field");
        vBar.getChildren().addAll(label, searchLibro);


        Button searchBtn = new Button("Cerca");
        searchBtn.getStyleClass().add("main-btn-small");
        Button backBtn = new Button("Indietro");
        backBtn.getStyleClass().add("back-btn-small");

        searchBtn.setOnAction(e -> performSearch()); 
        backBtn.setOnAction(e -> mainApp.showRepository());

        topBar.setAlignment(Pos.CENTER);
        topBar2.setAlignment(Pos.CENTER);
        topBar2.getChildren().addAll(searchBtn, backBtn);
        topBar.getChildren().addAll(vBar);

        resultsList = new ListView<>();
        card.getChildren().addAll(topBar, topBar2, resultsList);
        mainApp.rootLayout.setCenter(card);

    }
    
    /**
     * Esegue la ricerca dei libri per autore.
     * Recupera il testo (autore) dal campo <code>searchLibro</code>, valida che non sia vuoto
     * e avvia un Task asincrono per chiamare il <code>clientService.ricercaPerAutore()</code>.
     * <p>Popola la <code>resultsList</code> con le informazioni dei libri trovati o mostra un messaggio
     * in caso di nessun risultato o errore.
     * @see client.ClientService#ricercaPerAutore(String)
     */
    private void performSearch() {
        String titolo = searchLibro.getText().trim();
        if (titolo.isEmpty()) {
            resultsList.getItems().clear();
            alertI.setTitle("CAMPO MANCANTE");
            alertI.setHeaderText(null);
            alertI.setContentText("INSERIRE IL TITOLO."); 
            alertI.showAndWait();   
            return;
        }

        Task<LinkedList<Libro>> searchTask = new Task<>() {
            @Override
            protected LinkedList<Libro> call() throws Exception {
                return clientService.ricercaPerAutore(titolo);
            }
        };

        searchTask.setOnSucceeded(e -> {
            LinkedList<Libro> risultati = searchTask.getValue();
            resultsList.getItems().clear();
            if (risultati.isEmpty()) 
                resultsList.getItems().add("NESSUN LIBRO TROVATO PER: " + titolo.toUpperCase());
            else
                for (Libro libro : risultati) 
                    resultsList.getItems().add(libro.getInfo()); 
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