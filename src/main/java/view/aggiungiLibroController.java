/* DE NICOLA MATTEO VINCENZO, MATRICOLA 757933, VA
    SIRAGUSA VALERIO, MATRICOLA 756227, VA
    SACCON ALESSANDRO, MATRICOLA 756145, VA
    PIANEZZOLA ANDREA, MATRICOLA 756141, VA
    BROGGINI LUCA, MATRICOLA 756446, VA
*/
package view;

import client.ClientService;
import model.Libreria;
import model.Libro;
import app.Bookapp;
import java.util.LinkedList;

import javafx.concurrent.Task;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

/**
 * Controller per la vista che permette di aggiungere un libro a una libreria
 * dell'utente loggato.
 * <p>Fornisce la GUI per la ricerca di un libro per titolo, la visualizzazione
 * dei risultati, la selezione di una libreria esistente e l'aggiunta del libro
 * selezionato alla libreria scelta.</p>
 * <p>Gestisce le chiamate asincrone verso il client e mostra messaggi di alert
 * in caso di errori o successi.</p>
 * @author Broggini Luca
 * @author De Nicola Matteo
 * @author Pianezzola Andrea
 * @author Saccon Alessandro
 * @author Siragusa Valerio
 * @version 1.0
 */

public class aggiungiLibroController {

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
     * <code>libroRisultato</code>
     * Memorizza l'oggetto Libro trovato dalla ricerca.
     */
    private Libro libroRisultato;

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
     * <code>searchField</code>
     * Campo di testo per l'inserimento del titolo del libro da cercare.
     */
    private TextField searchField;
    /**
     * <code>resultsList</code>
     * ListView per visualizzare i risultati della ricerca del libro.
     */
    private ListView<String> resultsList;
    /**
     * <code>librerieList</code>
     * ListView per visualizzare le librerie dell'utente.
     */
    private ListView<String> librerieList;

    /**
     * Costruttore che inizializza il controller con riferimenti alle componenti principali.
     * <p>Crea gli alert e costruisce la vista.</p>
     * @param mainApp riferimento all'applicazione principale
     * @param loginController controller della vista di login
     * @param clientService servizio client per comunicazione server
     * @throws IllegalArgumentException se i parametri dovessero essere null
     */

    public aggiungiLibroController(Bookapp mainApp, loginController loginController, ClientService clientService) {
        if (mainApp == null || clientService == null || loginController == null) {
            throw new IllegalArgumentException("mainApp clientService  e loginController non possono essere null");
        }

        this.mainApp = mainApp;
        this.loginController = loginController;
        this.clientService = clientService;

        this.alertI = new Alert(Alert.AlertType.INFORMATION);
        this.alertE = new Alert(Alert.AlertType.ERROR);

        createView();
    }

    /**
     * Costruisce l'interfaccia utente (UI) per l'aggiunta di un libro.
     * <p>
     * Inizializza e dispone la lista delle librerie utente, il campo di ricerca
     * per il titolo del libro, la lista dei risultati di ricerca e i pulsanti
     * "Aggiungi" e "Indietro".
     * </p>
     */
    private void createView() {

        if (Bookapp.rootLayout == null) {
            alertE.setContentText("Layout non inizializzato. Riavvia l'applicazione.");
            alertE.showAndWait();
            return;
        }

        VBox card = new VBox(18);
        card.getStyleClass().add("card-big");

        Label titolo = new Label("Aggiungi libro alla libreria");
        titolo.getStyleClass().add("menu-titolo");

        Label labelLibreria = new Label("Le tue librerie");
        labelLibreria.getStyleClass().add("label-big");
        librerieList = new ListView<>();
        librerieList.setPrefHeight(250);
        librerieList.getStyleClass().add("list-view");
        caricaLibrerie();


        VBox vbox = new VBox(8);
        vbox.setAlignment(Pos.CENTER);
        Label labelLibro = new Label("Aggiungi Libro");
        labelLibro.getStyleClass().add("label-big");
        searchField = new TextField();
        searchField.setPromptText("Inserisci il titolo del libro");
        searchField.getStyleClass().add("text-field");
        searchField.setAlignment(Pos.CENTER);
        Button searchBtn = new Button("🔍 Cerca");
        searchBtn.getStyleClass().add("main-btn-small");
        searchBtn.setOnAction(e -> performSearch());

        vbox.getChildren().addAll(searchField, searchBtn);
        vbox.setMaxWidth(Double.MAX_VALUE);


        Label labelRisultati = new Label("Risultati della ricerca");
        labelRisultati.getStyleClass().add("label-big");
        resultsList = new ListView<>();
        resultsList.setPrefHeight(200);

        Button aggiungiBtn = new Button("➕ Aggiungi libro alla libreria");
        Button backBtn = new Button("Indietro");
        aggiungiBtn.getStyleClass().add("main-btn-small");
        backBtn.getStyleClass().add("back-btn-small");
        
        aggiungiBtn.setOnAction(e -> aggiungiLibroALibreria()); 
        backBtn.setOnAction(e -> loginController.showTrueLogin());

        HBox buttonBox = new HBox(12, aggiungiBtn, backBtn);
        buttonBox.setAlignment(Pos.CENTER);

        card.getChildren().addAll(
            titolo,
            labelLibreria, librerieList,
            labelLibro, vbox,
            labelRisultati, resultsList,
            buttonBox
        );

        mainApp.rootLayout.setCenter(card);
    }

    /**
     * Esegue la ricerca di un libro in base al titolo inserito.
     * <p>
     * Recupera il testo dal campo di ricerca, valida che non sia vuoto
     * e avvia un Task asincrono per chiamare il <code>clientService.getLibro()</code>.
     * </p><p>
     * Mostra il risultato (se trovato) nella <code>resultsList</code> e lo memorizza
     * in <code>libroRisultato</code>.
     * </p>
     */
    private void performSearch() {
        String titolo = searchField.getText().trim();
        resultsList.getItems().clear();
        if (titolo.isEmpty()) {
            alertI.setTitle("CAMPO MANCANTE");
            alertI.setHeaderText(null);
            alertI.setContentText("INSERISCI IL TITOLO DEL LIBRO!");
            alertI.showAndWait();
            return;
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
                alertI.setContentText("LIBRO NON TROVATO!");
                alertI.showAndWait();
            } 
            else {
                resultsList.getItems().add(risultati.getInfo());
                libroRisultato = risultati;
            }
        });

        searchTask.setOnFailed(e -> {
            resultsList.getItems().clear();
            alertE.setContentText("ERRORE DURANTE LA RICERCA!");
            alertE.showAndWait();
            searchTask.getException().printStackTrace();
        });

        new Thread(searchTask).start();
    }

    /**
     * Carica in modo asincrono le librerie dell'utente loggato.
     * <p>
     * Utilizza un Task per chiamare <code>clientService.getLibrerieUtente()</code>.
     * Popola la <code>librerieList</code> o mostra un errore e torna alla schermata
     * precedente se l'utente non ha librerie.
     * </p>
     */
    private void caricaLibrerie() {
        Task<LinkedList<Libreria>> task = new Task<>() {
            @Override
            protected LinkedList<Libreria> call() throws Exception {
                return clientService.getLibrerieUtente(mainApp.getLoggedUserId());
            }
        };

        task.setOnSucceeded(e -> {
            LinkedList<Libreria> librerie = task.getValue();
            if (librerie.isEmpty()) {
                alertE.setContentText("NON HAI ANCORA CREATO LIBRERIE. VERRAI REINDIRIZZATO ALLA SCHERMATA PRECEDENTE.");
                alertE.showAndWait();
                loginController.showTrueLogin();
            } else {
                librerieList.getItems().clear();
                librerie.forEach(i -> librerieList.getItems().add(i.getNome()));
            }
        });

        task.setOnFailed(e -> {
            librerieList.getItems().clear();
            task.getException().printStackTrace();
            alertE.setContentText("ERRORE NEL CARICAMENTO DELLE LIBRERIE.");
            alertE.showAndWait();
            loginController.showTrueLogin();
        });

        new Thread(task).start();
    }

    /**
     * Gestisce l'aggiunta del libro trovato alla libreria selezionata.
     * <p>
     * 1. Valida che una libreria sia selezionata e che un libro sia stato trovato.
     * </p><p>
     * 2. Avvia un Task asincrono per controllare se il libro è già presente
     * nella libreria (<code>clientService.checkPresente()</code>).
     * </p><p>
     * 3. Se non è presente, avvia un secondo Task asincrono per aggiungerlo
     * (<code>clientService.aggiungiLibro()</code>).
     * </p><p>
     * 4. Mostra alert di successo o errore in base all'esito delle operazioni.
     * </p>
     */
    private void aggiungiLibroALibreria() {
        String libreriaSelezionata = librerieList.getSelectionModel().getSelectedItem();

        if (libreriaSelezionata == null) {
            resultsList.getItems().clear();
            alertI.setTitle("CAMPO MANCANTE");
            alertI.setHeaderText(null);
            alertI.setContentText("SELEZIONA UNA LIBRERIA!");
            alertI.showAndWait();
            return;
        }

        if(libroRisultato == null){
            resultsList.getItems().clear();
            alertI.setTitle("CAMPO MANCANTE");
            alertI.setHeaderText(null);
            alertI.setContentText("SELEZIONA UN LIBRO!");
            alertI.showAndWait();
            return;
        }

        Task<Boolean> checkTask = new Task<>() {
            @Override
            protected Boolean call() throws Exception {
                return clientService.checkPresente(libreriaSelezionata, mainApp.getLoggedUserId(), libroRisultato.getID());
            }
        };

        checkTask.setOnSucceeded(e -> {
            Boolean isPresente = checkTask.getValue();
            if (isPresente) {
                resultsList.getItems().clear();
                alertE.setContentText("LIBRO GIA' PRESENTE NELLA LIBRERIA!");
                alertE.showAndWait();
                return;
            }

            Task<Boolean> addTask = new Task<>() {
                @Override
                protected Boolean call() throws Exception {
                    return clientService.aggiungiLibro(libreriaSelezionata, mainApp.getLoggedUserId(), libroRisultato.getID());
                }
            };

            addTask.setOnSucceeded(evt -> {
                Boolean result = addTask.getValue();
                resultsList.getItems().clear();
                if (result) {
                    alertI.setTitle("OPERAZIONE COMPLETATA");
                    alertI.setHeaderText(null);
                    alertI.setContentText("LIBRO AGGIUNTO CON SUCCESSO!");
                    alertI.showAndWait();               
                } 
                else {
                    alertE.setContentText("ERRORE DURANTE L'AGGIUNTA DEL LIBRO!");
                    alertE.showAndWait();                       
                }
            });

            addTask.setOnFailed(evt -> {
                resultsList.getItems().clear();
                alertE.setContentText("ERRORE DURANTE L'AGGIUNTA DEL LIBRO!");
                alertE.showAndWait();                       
                addTask.getException().printStackTrace();
            });

            new Thread(addTask).start();
        });

        checkTask.setOnFailed(e -> {
            resultsList.getItems().clear();
            alertE.setContentText("ERRORE DURANTE LA VERIFICA DEL LIBRO!");
            alertE.showAndWait();           
            checkTask.getException().printStackTrace();
        });

        new Thread(checkTask).start();
    }

}