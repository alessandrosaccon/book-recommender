/* DE NICOLA MATTEO VINCENZO, MATRICOLA 757933, VA
    SIRAGUSA VALERIO, MATRICOLA 756227, VA
    SACCON ALESSANDRO, MATRICOLA 756145, VA
    PIANEZZOLA ANDREA, MATRICOLA 756141, VA
    BROGGINI LUCA, MATRICOLA 756446, VA
*/
package view;

import client.ClientService; 
import model.Libro; 
import model.Utente;
import java.util.LinkedList;
import app.Bookapp;

import javafx.concurrent.Task; 
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

/**
 * Gestisce la vista e la logica per la creazione di una nuova libreria utente.
 * <p>
 * L'utente può specificare un nome per la libreria e aggiungere uno o più libri
 * (cercandoli per titolo) prima di finalizzare la creazione.
 * </p>
 * @author Broggini Luca
 * @author De Nicola Matteo
 * @author Pianezzola Andrea
 * @author Saccon Alessandro
 * @author Siragusa Valerio
 * @version 1.0
 */

public class creaLibreriaController {

    /**
     * <code>mainApp</code>
     * Riferimento all'applicazione principale (Bookapp).
     */
    private Bookapp mainApp;
    /**
     * <code>loginController</code>
     * Controller della vista di login, usato per la navigazione.
     */
    private loginController loginController;
    /**
     * <code>clientService</code>
     * Servizio client per la comunicazione con il server.
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
     * <code>searchLibreria</code>
     * Campo di testo per inserire il nome della nuova libreria.
     */
    private TextField searchLibreria;
    /**
     * <code>titoloField</code>
     * Campo di testo per inserire il titolo del libro da aggiungere.
     */
    private TextField titoloField;   
    /**
     * <code>resultsList</code>
     * ListView per visualizzare i risultati (usata principalmente per clear).
     */
    private ListView<String> resultsList;
    /**
     * <code>libriList</code>
     * ListView per visualizzare i titoli dei libri aggiunti alla lista temporanea.
     */
    private ListView<String> libriList;
    /**
     * <code>libri</code>
     * Lista temporanea degli oggetti Libro da aggiungere alla nuova libreria.
     */
    private LinkedList<Libro> libri;
    
    /**
     * Costruttore del controller.
     * <p>
     * Inizializza i servizi necessari, la lista dei libri e gli alert,
     * e chiama il metodo per costruire l'interfaccia utente.
     * </p>
     * @param mainApp L'istanza principale dell'applicazione.
     * @param loginController Il controller della vista di login (usato per la navigazione).
     * @param clientService Il servizio client per la comunicazione con il server.
     * @throws IllegalArgumentException se i parametri dovessero essere null
     */

    public creaLibreriaController(Bookapp mainApp, loginController loginController, ClientService clientService) {
        if (mainApp == null || clientService == null || loginController == null) {
            throw new IllegalArgumentException("mainApp clientService e loginController non possono essere null");
        }
        this.mainApp = mainApp;
        this.loginController = loginController;
        this.clientService = clientService;
        this.libri = new LinkedList<>();
        this.alertI = new Alert(Alert.AlertType.INFORMATION);
        this.alertE = new Alert(Alert.AlertType.ERROR);
        createView();
    }

    /**
     * Costruisce l'interfaccia utente (UI) per la creazione della libreria.
     * <p>
     * Configura i campi di input per il nome della libreria e l'aggiunta di libri,
     * la lista dei libri da aggiungere e i pulsanti di creazione e annullamento.
     * Imposta le azioni per i pulsanti.
     * </p>
     */
    private void createView() {

        if (Bookapp.rootLayout == null) {
            alertE.setTitle("Errore interfaccia");
            alertE.setHeaderText(null);
            alertE.setContentText("Layout non inizializzato. Riavvia l'applicazione.");
            alertE.showAndWait();
            return;
        }

        VBox card = new VBox(10);
        card.getStyleClass().add("card-big");

        Label labelLibreria = new Label("Nome Libreria:");
        labelLibreria.getStyleClass().add("label-big");
        searchLibreria = new TextField();
        searchLibreria.getStyleClass().add("text-field");

        Label labelTitolo = new Label("Titolo del libro:");
        labelTitolo.getStyleClass().add("label-big");

        VBox vbox = new VBox(8);
        vbox.setAlignment(Pos.CENTER);
        titoloField = new TextField();
        titoloField.setPromptText("Inserisci titolo e premi Aggiungi");
        titoloField.getStyleClass().add("text-field");
        titoloField.setAlignment(Pos.CENTER);

        Button aggiungiBtn = new Button("Aggiungi");
        aggiungiBtn.getStyleClass().add("main-btn-small");
        aggiungiBtn.setOnAction(e -> aggiungiTitolo());
        vbox.getChildren().addAll(titoloField, aggiungiBtn);
        vbox.setMaxWidth(Double.MAX_VALUE);

        Label labelLibri = new Label("Libri da inserire:");
        labelLibri.getStyleClass().add("label-big");

        libriList = new ListView<>();

        resultsList = new ListView<>();

        Button creaBtn = new Button("Crea Libreria");
        creaBtn.getStyleClass().add("main-btn-small");
        Button backBtn = new Button("Indietro");
        backBtn.getStyleClass().add("back-btn-small");


        creaBtn.setOnAction(e -> performSearch());
        backBtn.setOnAction(e -> loginController.showTrueLogin());


        card.getChildren().addAll(labelLibreria, searchLibreria, labelTitolo, vbox, labelLibri, libriList, creaBtn, backBtn);

        mainApp.rootLayout.setCenter(card);
    }

    /**
     * Gestisce il processo di creazione della libreria.
     * <p>
     * 1. Valida il nome della libreria.
     * </p><p>
     * 2. Controlla (in background) se esiste già una libreria con lo stesso nome per l'utente.
     * </p><p>
     * 3. Se non esiste, crea la libreria (in background).
     * </p><p>
     * 4. Se la creazione ha successo, aggiunge tutti i libri dalla lista <code>libri</code>
     * alla nuova libreria (in background).
     * </p><p>
     * 5. Mostra alert informativi o di errore during il processo.
     * </p>
     */
    private void performSearch() {
        String nomeLibreria = searchLibreria.getText().trim();
        resultsList.getItems().clear();
        if (nomeLibreria.isEmpty()) {
            alertI.setTitle("CAMPO MANCANTE");
            alertI.setHeaderText(null);
            alertI.setContentText("INSERISCI IL NOME DELLA LIBRERIA!");
            alertI.showAndWait();
            return;
        }

        Task<Boolean> checkLibraryTask = new Task<>() {
            @Override
            protected Boolean call() throws Exception {
                return clientService.controlloLibreria(mainApp.getLoggedUserId(), nomeLibreria);
            }
        };

        checkLibraryTask.setOnSucceeded(evt -> {
            Boolean exists = checkLibraryTask.getValue();
            if (exists) {
                alertI.setTitle("ATTENZIONE");
                alertI.setHeaderText(null);
                alertI.setContentText("HAI GIA' UNA LIBRERIA CON QUESTO NOME!");
                alertI.showAndWait();                   
                return;
            }

            Task<Boolean> createLibraryTask = new Task<>() {
                @Override
                protected Boolean call() throws Exception {
                    return clientService.RegistraLibreria(mainApp.getLoggedUserId(), nomeLibreria);
                }
            };

            createLibraryTask.setOnSucceeded(e -> {
                Boolean risultati = createLibraryTask.getValue();
                resultsList.getItems().clear();
                if (!risultati) {
                    alertE.setContentText("CREAZIONE DELLA LIBRERIA FALLITA!");
                    alertE.showAndWait();                           } 
                else {
                    Task<Boolean> addBooksTask = new Task<>() {
                        @Override
                        protected Boolean call() throws Exception {
                            for(Libro libro : libri) 
                                if (!clientService.aggiungiLibro(nomeLibreria, mainApp.getLoggedUserId(), libro.getID())) 
                                    return false;           
                            return true;
                        }
                    };

                    addBooksTask.setOnSucceeded(event -> {
                        Boolean addSuccess = addBooksTask.getValue();
                        if (addSuccess) {
                            alertI.setContentText("LIBRERIA CREATA CON SUCCESSO! AGGIUNGI " + libri.size() + " LIBRI.");
                            alertI.showAndWait();       
                            libri.clear();
                            libriList.getItems().clear();
                            searchLibreria.clear();
                        } else {
                            alertE.setContentText("ERRORE DURANTE L'AGGIUNTA DEI LIBRI");
                            alertE.showAndWait();     
                        }
                    });

                    addBooksTask.setOnFailed(event -> {
                            alertE.setContentText("ERRORE DURANTE L'AGGIUNTA DEI LIBRI");
                            alertE.showAndWait();                       
                    });
                    new Thread(addBooksTask).start();
                }
            });

            createLibraryTask.setOnFailed(e -> {
                alertE.setContentText("ERRORE DURANTE LA CREAZIONE DELLA LIBRERIA");
                alertE.showAndWait();     
                createLibraryTask.getException().printStackTrace();
            });

            new Thread(createLibraryTask).start();
        });

        checkLibraryTask.setOnFailed(e -> {
            alertE.setContentText("ERRORE DURANTE IL CONTROLLO DEL NOME DELLA LIBRERIA");
            alertE.showAndWait();     
            checkLibraryTask.getException().printStackTrace();
        });

        new Thread(checkLibraryTask).start();
    }

    /**
     * Aggiunge un libro alla lista temporanea dei libri da inserire nella libreria.
     * <p>
     * 1. Valida il titolo inserito.
     * </p><p>
     * 2. Controlla che il libro non sia già stato aggiunto alla lista locale.
     * </p><p>
     * 3. Cerca il libro sul server tramite il titolo (in background).
     * </p><p>
     * 4. Se trovato, lo aggiunge alla lista <code>libri</code> (oggetti Libro) e alla <code>libriList</code> (ListView).
     * </p><p>
     * 5. Mostra alert in caso di titolo mancante, duplicato, libro non trovato o errore.
     * </p>
     */
    
    private void aggiungiTitolo() {
        String titolo = titoloField.getText().trim();
        
        if (titolo.isEmpty()) {
            alertI.setTitle("CAMPO MANCANTE");
            alertI.setHeaderText(null);
            alertI.setContentText("INSERISCI IL TITOLO DEL LIBRO");
            alertI.showAndWait();     
            return;
        }

        for (Libro libroEsistente : libri) {
            if (libroEsistente.getTitolo().equalsIgnoreCase(titolo)) {
                alertE.setContentText("LIBRO GIA' AGGIUNTO ALLA LISTA");
                alertE.showAndWait();     
                return;
            }
        }

        Task<Libro> searchTask = new Task<>() {
            @Override
            protected Libro call() throws Exception {
                return clientService.getLibro(titolo);
            }
        };

        searchTask.setOnSucceeded(e -> {
            Libro risultati = searchTask.getValue();
            resultsList.getItems().clear();
            
            if (risultati == null) {
                alertI.setTitle("ATTENZIONE");
                alertI.setHeaderText(null);
                alertI.setContentText("LIBRO NON TROVATO!");
                alertI.showAndWait();
                libriList.getItems().remove(titolo); 
                return;
            }

            libri.add(risultati);
            libriList.getItems().add(titolo);
            titoloField.clear();
        });

        searchTask.setOnFailed(e -> {
            alertE.setContentText("ERRORE DURANTE LA RICERCA DEL LIBRO");
            alertE.showAndWait();
            libriList.getItems().remove(titolo);
        });

        new Thread(searchTask).start();
    }
}