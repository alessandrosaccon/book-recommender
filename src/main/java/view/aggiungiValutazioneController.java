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
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

/**
 * Gestisce la vista per l'aggiunta di una valutazione a un libro.
 * <p>
 * Questa classe costruisce l'interfaccia utente, carica le librerie e i libri
 * dell'utente e gestisce l'invio della valutazione al server.
 * </p>
 * @author Broggini Luca
 * @author De Nicola Matteo
 * @author Pianezzola Andrea
 * @author Saccon Alessandro
 * @author Siragusa Valerio
 * @version 1.0
 */

public class aggiungiValutazioneController {

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
     * <code>libroSelezionato</code>
     * Memorizza l'oggetto Libro selezionato dall'utente per la valutazione.
     */
    private Libro libroSelezionato;
    /**
     * <code>libriInLibreria</code>
     * Lista dei libri (oggetti Libro) presenti nella libreria selezionata.
     */
    private LinkedList<Libro> libriInLibreria;

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
     * <code>libriList</code>
     * ListView per visualizzare i titoli dei libri nella libreria selezionata.
     */
    private ListView<String> libriList;
    /**
     * <code>librerieList</code>
     * ListView per visualizzare i nomi delle librerie dell'utente.
     */
    private ListView<String> librerieList;


    // Campi valutazione
    /**
     * <code>tfStile</code>
     * Campo di testo per il voto (1-5) relativo allo Stile.
     */
    private TextField tfStile;
    /**
     * <code>tfNotaStile</code>
     * Campo di testo per la nota opzionale (max 256 caratteri) relativa allo Stile.
     */
    private TextField tfNotaStile;
    /**
     * <code>tfContenuto</code>
     * Campo di testo per il voto (1-5) relativo al Contenuto.
     */
    private TextField tfContenuto;
    /**
     * <code>tfNotaContenuto</code>
     * Campo di testo per la nota opzionale (max 256 caratteri) relativa al Contenuto.
     */
    private TextField tfNotaContenuto;
    /**
     * <code>tfGradevolezza</code>
     * Campo di testo per il voto (1-5) relativo alla Gradevolezza.
     */
    private TextField tfGradevolezza;
    /**
     * <code>tfNotaGradevolezza</code>
     * Campo di testo per la nota opzionale (max 256 caratteri) relativa alla Gradevolezza.
     */
    private TextField tfNotaGradevolezza;
    /**
     * <code>tfOriginalita</code>
     * Campo di testo per il voto (1-5) relativo all'Originalità.
     */
    private TextField tfOriginalita;
    /**
     * <code>tfNotaOriginalita</code>
     * Campo di testo per la nota opzionale (max 256 caratteri) relativa all'Originalità.
     */
    private TextField tfNotaOriginalita;
    /**
     * <code>tfEdizione</code>
     * Campo di testo per il voto (1-5) relativo all'Edizione.
     */
    private TextField tfEdizione;
    /**
     * <code>tfNotaEdizione</code>
     * Campo di testo per la nota opzionale (max 256 caratteri) relativa all'Edizione.
     */
    private TextField tfNotaEdizione;
    /**
     * <code>tfComplessiva</code>
     * Campo di testo per la nota opzionale complessiva (max 256 caratteri).
     */
    private TextField tfComplessiva;

    /**
     * Costruttore del controller.
     *
     * @param mainApp L'istanza principale dell'applicazione.
     * @param loginController Il controller della vista di login.
     * @param clientService Il servizio client per la comunicazione con il server.
     * @throws IllegalArgumentException se i parametri dovessero essere null
     */
    public aggiungiValutazioneController(Bookapp mainApp, loginController loginController, ClientService clientService) {
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
     * Crea e restituisce un TextFormatter che limita l'input a 256 caratteri.
     * @return Un TextFormatter<String> con limite di lunghezza.
     */
    private TextFormatter<String> createLimitedFormatter() {
        return new TextFormatter<>(change -> change.getControlNewText().length() <= 256 ? change : null);
    }

    /**
     * Inizializza e costruisce l'interfaccia utente (UI) per l'aggiunta di valutazioni.
     * <p>
     * Configura i layout, i campi di testo, le liste e i pulsanti, e imposta i listener
     * per la selezione delle librerie, dei libri e per l'invio e l'annullamento.
     * </p>
     */

    private void createView() {

        if (Bookapp.rootLayout == null) {
            alertE.setContentText("Layout non inizializzato. Riavvia l'applicazione.");
            alertE.showAndWait();
            return;
        }

        VBox card = new VBox(18);
        card.getStyleClass().add("card-large");

        Label labelLibreria = new Label("Le tue librerie:");
        labelLibreria.getStyleClass().add("label");
        librerieList = new ListView<>();
        librerieList.getStyleClass().add("list-view");
        caricaLibrerie();

        Label labelLibri = new Label("Libri nella libreria selezionata:");
        labelLibri.getStyleClass().add("label");
        libriList = new ListView<>();
        libriList.getStyleClass().add("list-view");

        librerieList.getSelectionModel().selectedItemProperty().addListener((obs, oldV, newV) -> {
            if (newV != null) {
                caricaLibriPerLibreria(newV);
            }
        });

        libriList.getSelectionModel().selectedItemProperty().addListener((obs, oldV, newV) -> {
            int idx = libriList.getSelectionModel().getSelectedIndex();
            if (idx >= 0 && libriInLibreria != null && idx < libriInLibreria.size()) {
                libroSelezionato = libriInLibreria.get(idx);
            } else {
                libroSelezionato = null;
            }
        });

        VBox form = new VBox(6);
        HBox columnsTextFields = new HBox(10);
        VBox col1 = new VBox(6);
        VBox col2 = new VBox(6);
        columnsTextFields.getChildren().addAll(col1, col2);
        columnsTextFields.setAlignment(Pos.CENTER);
        HBox.setHgrow(col1, javafx.scene.layout.Priority.ALWAYS);
        HBox.setHgrow(col2, javafx.scene.layout.Priority.ALWAYS);
        col1.setMaxWidth(Double.MAX_VALUE);
        col2.setMaxWidth(Double.MAX_VALUE);


        form.setPadding(new Insets(6));

        tfStile = new TextField(); 
        tfStile.setPromptText("Stile (1-5)");
        tfStile.getStyleClass().add("text-field");


        tfNotaStile = new TextField(); 
        tfNotaStile.setPromptText("Nota sullo stile (opzionale) - max 256 caratteri");
        tfNotaStile.getStyleClass().add("text-field");

        tfNotaStile.setTextFormatter(new TextFormatter<String>(change -> 
            change.getControlNewText().length() <= 256 ? change : null));

        tfContenuto = new TextField(); 
        tfContenuto.setPromptText("Contenuto (1-5)");
        tfContenuto.getStyleClass().add("text-field");


        tfNotaContenuto = new TextField(); 
        tfNotaContenuto.setPromptText("Nota sul contenuto (opzionale) - max 256 caratteri");
        tfNotaContenuto.getStyleClass().add("text-field");

        tfNotaContenuto.setTextFormatter(new TextFormatter<String>(change -> 
            change.getControlNewText().length() <= 256 ? change : null));

        tfGradevolezza = new TextField(); 
        tfGradevolezza.setPromptText("Gradevolezza (1-5)");
        tfGradevolezza.getStyleClass().add("text-field");


        tfNotaGradevolezza = new TextField(); 
        tfNotaGradevolezza.setPromptText("Nota sulla gradevolezza (opzionale) - max 256 caratteri");
        tfNotaGradevolezza.getStyleClass().add("text-field");

        tfNotaGradevolezza.setTextFormatter(new TextFormatter<String>(change -> 
            change.getControlNewText().length() <= 256 ? change : null));

        tfOriginalita = new TextField(); 
        tfOriginalita.setPromptText("Originalità (1-5)");
        tfOriginalita.getStyleClass().add("text-field");


        tfNotaOriginalita = new TextField(); 
        tfNotaOriginalita.setPromptText("Nota originalità (opzionale) - max 256 caratteri");
        tfNotaOriginalita.getStyleClass().add("text-field");

        tfNotaOriginalita.setTextFormatter(new TextFormatter<String>(change -> 
            change.getControlNewText().length() <= 256 ? change : null));

        tfEdizione = new TextField(); 
        tfEdizione.setPromptText("Edizione (1-5)");
        tfEdizione.getStyleClass().add("text-field");

        
        tfNotaEdizione = new TextField(); 
        tfNotaEdizione.setPromptText("Nota edizione (opzionale) - max 256 caratteri");
        tfNotaEdizione.getStyleClass().add("text-field");

        tfNotaEdizione.setTextFormatter(new TextFormatter<String>(change -> 
            change.getControlNewText().length() <= 256 ? change : null));

        tfComplessiva = new TextField(); 
        tfComplessiva.setPromptText("Nota complessiva (opzionale) - max 256 caratteri");
        tfComplessiva.getStyleClass().add("text-field");

        tfComplessiva.setTextFormatter(new TextFormatter<String>(change -> 
            change.getControlNewText().length() <= 256 ? change : null));

        Button inviaBtn = new Button("Invia valutazione");
        inviaBtn.getStyleClass().add("main-btn-small");
        Button backBtn = new Button("Indietro");
        backBtn.getStyleClass().add("back-btn-small");

        backBtn.setOnAction(e -> loginController.showTrueLogin());
        inviaBtn.setOnAction(e -> inviaValutazione());

        col1.getChildren().addAll(tfStile, tfNotaStile, tfContenuto, tfNotaContenuto, tfGradevolezza, tfNotaGradevolezza);
        col2.getChildren().addAll(tfOriginalita, tfNotaOriginalita, tfEdizione, tfNotaEdizione, tfNotaGradevolezza);


        HBox buttonsBox = new HBox(8, inviaBtn, backBtn);
        buttonsBox.setPadding(new Insets(20, 0, 0, 0));
        buttonsBox.setAlignment(Pos.CENTER);
        form.getChildren().addAll(new Label("Valuta il libro selezionato:"), columnsTextFields, tfComplessiva, buttonsBox);
        form.setAlignment(Pos.CENTER);

        card.getChildren().addAll(labelLibreria, librerieList, new Separator(), labelLibri, libriList, new Separator(), form);
        mainApp.rootLayout.setCenter(card);
    }

    /**
     * Carica in modo asincrono le librerie dell'utente loggato.
     * <p>
     * Utilizza un Task per eseguire la chiamata al <code>clientService</code>.
     * Popola la <code>librerieList</code> con i nomi delle librerie.
     * Mostra un alert e torna al login se non ci sono librerie o in caso di errore.
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
            librerieList.getItems().clear();
            if (librerie == null || librerie.isEmpty()) {
                alertE.setContentText("NON HAI ANCORA CREATO LIBRERIE. VERRAI REINDIRIZZATO ALLA SCHERMATA PRECEDENTE.");
                alertE.showAndWait();
                loginController.showTrueLogin();
                return;
            }
            for (Libreria l : librerie) 
                librerieList.getItems().add(l.getNome());
        });

        task.setOnFailed(e -> {
            librerieList.getItems().clear();
            alertE.setContentText("ERRORE NEL CARICAMENTO DELLE LIBRERIE.");
            alertE.showAndWait();
            task.getException().printStackTrace();
        });

        new Thread(task).start();
    }

    /**
     * Carica in modo asincrono i libri contenuti in una specifica libreria dell'utente.
     * <p>
     * Utilizza un Task per eseguire la chiamata al <code>clientService</code>.
     * Popola la <code>libriList</code> con i titoli dei libri.
     * </p>
     * @param nomeLibreria Il nome della libreria selezionata.
     */
    private void caricaLibriPerLibreria(String nomeLibreria) {
        Task<LinkedList<Libro>> task = new Task<>() {
            @Override
            protected LinkedList<Libro> call() throws Exception {
                return clientService.getElencoLibri(nomeLibreria, mainApp.getLoggedUserId());
            }
        };

        task.setOnSucceeded(e -> {
            libriInLibreria = task.getValue();
            libriList.getItems().clear();
            if (libriInLibreria == null || libriInLibreria.isEmpty()) {
                libriList.getItems().add("NESSUN LIBRO IN QUESTA LIBRERIA");
                return;
            }
            for (Libro b : libriInLibreria) {
                libriList.getItems().add(b.getTitolo());
            }
        });

        task.setOnFailed(e -> {
            libriList.getItems().clear();
            alertE.setContentText("ERRORE NEL CARICAMENTO DEI LIBRI.");
            alertE.showAndWait();
            task.getException().printStackTrace();
        });

        new Thread(task).start();
    }

    /**
     * Gestisce l'invio della valutazione.
     * <p>
     * Esegue la validazione dei campi di input (controllo libro selezionato e voti
     * numerici obbligatori tra 1 e 5).
     * Calcola il voto finale come media dei voti parziali.
     * Invia i dati al server tramite <code>clientService</code> in un task asincrono.
     * Mostra un alert di successo o errore e pulisce i campi in caso di successo.
     * </p>
     */
    private void inviaValutazione() {

        if (libroSelezionato == null) {
            alertI.setTitle("CAMPO MANCANTE");
            alertI.setHeaderText(null);
            alertI.setContentText("SELEZIONA UN LIBRO DALLA LISTA!");
            alertI.showAndWait();
            return;
        }

        int s, c, p, o, eVal;
        try {
            s = Integer.parseInt(tfStile.getText().trim());
            c = Integer.parseInt(tfContenuto.getText().trim());
            p = Integer.parseInt(tfGradevolezza.getText().trim());
            o = Integer.parseInt(tfOriginalita.getText().trim());
            eVal = Integer.parseInt(tfEdizione.getText().trim());
            if (s < 1 || s > 5 || c < 1 || c > 5 || p < 1 || p > 5 || o < 1 || o > 5 || eVal < 1 || eVal > 5)
                throw new NumberFormatException();
        } catch (NumberFormatException ex) {
            alertI.setContentText("I CAMPI DI VALUTAZIONE DEVONO ESSERE NUMERI INTERI TRA 1 E 5.");
            alertI.showAndWait();
            return;
        }

        String notaOriginalita = tfNotaOriginalita.getText().trim();
        String notaEdizione = tfNotaEdizione.getText().trim();
        String notaComplessiva = tfComplessiva.getText().trim();
        String notaStile = tfNotaStile.getText().trim();
        String notaContenuto = tfNotaContenuto.getText().trim();
        String notaGradevolezza = tfNotaGradevolezza.getText().trim();

        double votoFinale = (s + c + p + o + eVal) / 5.0;

        Task<Boolean> task = new Task<>() {
            @Override
            protected Boolean call() throws Exception {
                return clientService.inserisciValutazioneLibro(mainApp.getLoggedUserId(), libroSelezionato.getID(), s, c, notaOriginalita, p, notaEdizione, notaComplessiva, o, eVal, votoFinale, notaStile, notaContenuto, notaGradevolezza);
            }
        };

        task.setOnSucceeded(ev -> {
            Boolean res = task.getValue();
            Alert a = new Alert(res ? Alert.AlertType.INFORMATION : Alert.AlertType.ERROR);
            a.setContentText(res ? "VALUTAZIONE INVIATA CON SUCCESSO." : "ERRORE NELL'INVIO DELLA VALUTAZIONE.");
            a.showAndWait();
            if (res) {
                tfStile.clear(); tfContenuto.clear(); tfGradevolezza.clear(); tfOriginalita.clear(); tfEdizione.clear();
                tfNotaStile.clear(); tfNotaContenuto.clear(); tfNotaGradevolezza.clear(); tfNotaOriginalita.clear(); tfNotaEdizione.clear(); tfComplessiva.clear();
            }
        });

        task.setOnFailed(ev -> {
            task.getException().printStackTrace();
            alertE.setContentText("ERRORE DURANTE L'INVIO DELLA VALUTAZIONE.");
            alertE.showAndWait();
        });

        new Thread(task).start();
    }

}