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
 * Gestisce la vista e la logica per la ricerca di libri in base all'autore e all'anno di pubblicazione.
 * Costruisce l'interfaccia utente con campi di testo per l'autore e l'anno,
 * e una lista per visualizzare i risultati della ricerca.
 * @author Broggini Luca
 * @author De Nicola Matteo
 * @author Pianezzola Andrea
 * @author Saccon Alessandro
 * @author Siragusa Valerio
 * @version 1.0
 */

public class ricercaAutoreAnnoController {

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
     * <code>searchAnno</code>
     * Campo di testo per l'inserimento dell'anno di pubblicazione.
     */
    private TextField searchAnno;
    
    /**
     * <code>searchAutore</code>
     * Campo di testo per l'inserimento dell'autore.
     */
    private TextField searchAutore;
    
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

    public ricercaAutoreAnnoController(Bookapp mainApp, ClientService clientService) {
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
     * Costruisce l'interfaccia utente (UI) per la ricerca per autore e anno.
     * Configura le etichette, i campi di testo per 'Autore' e 'Anno',
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
        HBox topBar3 = new HBox(10);

        card.getStyleClass().add("card-big");

        Label labelAutore = new Label("Inserisci Autore:");
        labelAutore.getStyleClass().add("label-big");

        searchAutore = new TextField();
        searchAutore.getStyleClass().add("text-field");

        Label labelAnno = new Label("Inserisci Anno:");
        labelAnno.getStyleClass().add("label-big");

        searchAnno = new TextField();
        searchAnno.getStyleClass().add("text-field");


        Button searchButton = new Button("Cerca");
        searchButton.getStyleClass().add("main-btn-small");
        Button backBtn = new Button("Indietro");
        backBtn.getStyleClass().add("back-btn-small");

        searchButton.setOnAction(e -> performSearch()); 
        backBtn.setOnAction(e -> mainApp.showRepository());

        topBar.setAlignment(Pos.CENTER);
        topBar2.setAlignment(Pos.CENTER);
        topBar3.setAlignment(Pos.CENTER);

        topBar3.getChildren().addAll(searchButton, backBtn);
        topBar.getChildren().addAll(labelAutore, searchAutore);
        topBar2.getChildren().addAll(labelAnno, searchAnno);

        resultsList = new ListView<>();
        card.getChildren().addAll(topBar, topBar2, topBar3, resultsList);
        mainApp.rootLayout.setCenter(card);

    }

    /**
     * Esegue la ricerca dei libri per autore e anno.
     * Recupera il testo dai campi <code>searchAutore</code> e <code>searchAnno</code>, valida che
     * entrambi non siano vuoti e avvia un Task asincrono per chiamare il
     * <code>clientService.ricercaPerAutoreAnno()</code>.
     * <p>Popola la <code>resultsList</code> con le informazioni dei libri trovati o mostra
     * un messaggio in caso di nessun risultato o errore.
     * @see client.ClientService#ricercaPerAutoreAnno(String, String)
     */
    private void performSearch() {
        String autore = searchAutore.getText().trim();
        if (autore.isEmpty()) {
            resultsList.getItems().clear();
            alertI.setTitle("CAMPO MANCANTE");
            alertI.setHeaderText(null);
            alertI.setContentText("INSERIRE L'AUTORE.");
            alertI.showAndWait();   
            return;
        }

        String anno = searchAnno.getText().trim();
        if (autore.isEmpty()) {
            resultsList.getItems().clear();
            alertI.setTitle("CAMPO MANCANTE");
            alertI.setHeaderText(null);
            alertI.setContentText("INSERIRE L'ANNO.");
            alertI.showAndWait();   
            return;
        }

        Task<LinkedList<Libro>> searchTask = new Task<>() {
            @Override
            protected LinkedList<Libro> call() throws Exception {
                return clientService.ricercaPerAutoreAnno(autore, anno);
            }
        };

        searchTask.setOnSucceeded(e -> {
            LinkedList<Libro> risultati = searchTask.getValue();
            resultsList.getItems().clear();
            if (risultati.isEmpty())
                resultsList.getItems().add("NESSUN LIBRO TROVATO PER: " + autore.toUpperCase() + " " + anno.toUpperCase());
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