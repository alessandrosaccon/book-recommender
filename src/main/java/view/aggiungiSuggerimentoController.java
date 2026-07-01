/* DE NICOLA MATTEO VINCENZO, MATRICOLA 757933, VA
    SIRAGUSA VALERIO, MATRICOLA 756227, VA
    SACCON ALESSANDRO, MATRICOLA 756145, VA
    PIANEZZOLA ANDREA, MATRICOLA 756141, VA
    BROGGINI LUCA, MATRICOLA 756446, VA
*/
package view;

import model.Libreria;
import model.Libro;
import client.ClientService;
import app.Bookapp;

import java.util.LinkedList;

import javafx.concurrent.*;
import javafx.geometry.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;

/**
 * Controller per la vista che permette agli utenti di aggiungere suggerimenti di libri.
 * <p>Visualizza le librerie dell'utente e i libri contenuti nella libreria selezionata.
 * Consente all'utente di selezionare un libro e inserire un suggerimento per un altro libro
 * tramite il titolo.</p>
 * <p>Gestisce le chiamate asincrone al server per il caricamento dei dati
 * e per l'invio dei suggerimenti, fornendo feedback tramite alert in caso di errori o successi.</p>
 * @author Broggini Luca
 * @author De Nicola Matteo
 * @author Pianezzola Andrea
 * @author Saccon Alessandro
 * @author Siragusa Valerio
 * @version 1.0
 */

public class aggiungiSuggerimentoController 
{
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
     * <code>elencoLibri</code>
     * Lista dei libri (oggetti Libro) presenti nella libreria selezionata.
     */
    private LinkedList<Libro> elencoLibri;
    /**
     * <code>libroSelezionato</code>
     * Memorizza l'oggetto Libro selezionato dalla lista <code>listaLibri</code>.
     */
    private Libro libroSelezionato;

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
     * <code>listaLibrerie</code>
     * ListView per visualizzare i nomi delle librerie dell'utente.
     */
    private ListView<String> listaLibrerie;
    /**
     * <code>listaLibri</code>
     * ListView per visualizzare i titoli dei libri nella libreria selezionata.
     */
    private ListView<String> listaLibri;
    /**
     * <code>titleField</code>
     * Campo di testo per inserire il titolo del libro da suggerire.
     */
    private TextField titleField;

    /**
     * Costruttore che inizializza il controller con riferimenti necessari
     * e costruisce la vista.
     * @param mainApp riferimento all'applicazione principale
     * @param loginController controller per la gestione della login
     * @param clientService servizio client per le chiamate server
     * @throws IllegalArgumentException se i parametri dovessero essere null
     */
    public aggiungiSuggerimentoController(Bookapp mainApp, loginController loginController, ClientService clientService) {
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
     * Costruisce la vista grafica con le liste delle librerie, libri e i controlli per
     * inserire suggerimenti.
     */
    private void createView() {

        if (Bookapp.rootLayout == null) {
            alertE.setContentText("Layout non inizializzato. Riavvia l'applicazione.");
            alertE.showAndWait();
            return;
        }

        VBox card = new VBox(18);
        card.getStyleClass().add("card-big");

        Label labelLibreria = new Label("Le tue librerie:");
        labelLibreria.getStyleClass().add("label");
        listaLibrerie = new ListView<>();
        listaLibrerie.setPrefHeight(120);
        listaLibrerie.getStyleClass().add("list-view");
        caricaLibrerie();

        Label labelLibri = new Label("Libri nella libreria selezionata:");
        labelLibri.getStyleClass().add("label");

        listaLibri = new ListView<>();
        listaLibri.setPrefHeight(140);
        listaLibri.getStyleClass().add("list-view");

        listaLibrerie.getSelectionModel().selectedItemProperty().addListener((obs, oldV, newV) -> {
            if (newV != null) 
                caricaLibriPerLibreria(newV);
        });

        listaLibri.getSelectionModel().selectedItemProperty().addListener((obs, oldV, newV) -> {
            int i = listaLibri.getSelectionModel().getSelectedIndex();
            if (i >= 0 && elencoLibri != null && i < elencoLibri.size()) 
                libroSelezionato = elencoLibri.get(i);
            else
                libroSelezionato = null;
        });

        VBox Vbox_suggest = new VBox(12);
        HBox Hbox_buttons = new HBox(12);
        Hbox_buttons.setAlignment(Pos.CENTER);


        titleField = new TextField();
        titleField.setPromptText("Titolo del libro da suggerire");
        titleField.getStyleClass().add("text-field");
        titleField.setAlignment(Pos.CENTER);

        Button sendBtn = new Button("📤 Invia Suggerimento");
        sendBtn.getStyleClass().add("main-btn-small");
        sendBtn.setOnAction(e -> inviaSuggerimento());

        Button backBtn = new Button("Indietro");
        backBtn.getStyleClass().add("back-btn-small");
        backBtn.setOnAction(e -> loginController.showTrueLogin());

        Hbox_buttons.getChildren().addAll(sendBtn, backBtn);
        Vbox_suggest.getChildren().addAll(titleField, Hbox_buttons);

        card.getChildren().addAll(
            labelLibreria,
            listaLibrerie,
            new Separator(),
            labelLibri,
            listaLibri,
            new Separator(),
            Vbox_suggest
        );

        mainApp.rootLayout.setCenter(card);
    }

    /**
     * Carica le librerie dell'utente loggato in modo asincrono.
     * <p>In caso di assenza libirerie o errore notifica l'utente e lo reindirizza.</p>
     */
    private void caricaLibrerie() 
    {
        Task<LinkedList<Libreria>> task = new Task<>() {
            @Override
            protected LinkedList<Libreria> call() throws Exception {
                return clientService.getLibrerieUtente(mainApp.getLoggedUserId());
            }
        };

        task.setOnSucceeded(e -> {
            LinkedList<Libreria> librerie = task.getValue();
            listaLibrerie.getItems().clear();
            if (librerie.isEmpty()) {
                alertE.setContentText("NON HAI LIBRERIE. DEVI CREARNE UNA PRIMA DI POTER INSERIRE SUGGERIMENTI.");
                alertE.showAndWait();
                loginController.showTrueLogin();
                return;
            }
            for (Libreria l : librerie) {
                listaLibrerie.getItems().add(l.getNome());
            }
        });

        task.setOnFailed(e -> {
            listaLibrerie.getItems().clear();
            alertE.setContentText("ERRORE NEL CARICAMENTO DELLE LIBRERIE.");
            alertE.showAndWait();           
            task.getException().printStackTrace();
        });

        new Thread(task).start();
    }

    /**
     * Carica in modo asincrono i libri appartenenti alla libreria selezionata.
     * <p>Mostra messaggi di errore in caso di problemi.</p>
     * @param nomeLibreria nome della libreria selezionata
     */
    private void caricaLibriPerLibreria(String nomeLibreria) 
    {
        Task<LinkedList<Libro>> task = new Task<>() {
            @Override
            protected LinkedList<Libro> call() throws Exception {
                return clientService.getElencoLibri(nomeLibreria, mainApp.getLoggedUserId());
            }
        };

        task.setOnSucceeded(e -> {
            elencoLibri = task.getValue();
            listaLibri.getItems().clear();
            if (elencoLibri == null || elencoLibri.isEmpty()) {
                listaLibri.getItems().add("NESSUN LIBRO NELLA LIBRERIA CORRENTE");
                return;
            }
            for (Libro libro : elencoLibri) 
                listaLibri.getItems().add(libro.getTitolo());
        });

        task.setOnFailed(e -> {
            listaLibri.getItems().clear();
            alertE.setContentText("ERRORE NEL CARICAMENTO DEI LIBRI.");
            alertE.showAndWait();   
            task.getException().printStackTrace();
        });

        new Thread(task).start();
    }

    /**
     * Invia il suggerimento di un libro associato al libro selezionato.
     * <p>Effettua controlli di validità sui dati inseriti e gestisce risposte di successo o errore.</p>
     */

    private void inviaSuggerimento() {
        if (libroSelezionato == null) {
            alertI.setTitle("CAMPO MANCANTE");
            alertI.setHeaderText(null);
            alertI.setContentText("SELEZIONA PRIMA UN LIBRO DALLA LIBRERIA.");
            alertI.showAndWait();
            return;
        }

        String titoloSuggerito = titleField.getText().trim();
        if (titoloSuggerito.isEmpty()) {
            alertI.setTitle("CAMPO MANCANTE");
            alertI.setHeaderText(null);
            alertI.setContentText("INSERIRE IL TITOLO DEL LIBRO CHE VUOI INSERIRE.");
            alertI.showAndWait();
            return;
        }

        Task<Integer> task = new Task<>() {
            @Override
            protected Integer call() throws Exception {
                int num = clientService.getNumeroConsigli(mainApp.getLoggedUserId(), libroSelezionato.getID());
                if (num >= 3) return 2;

                int suggerimentoID = clientService.inserisciSuggerimento(mainApp.getLoggedUserId(), libroSelezionato.getID());
                if (suggerimentoID <= 0) return 3;

                Libro suggerito = clientService.getLibro(titoloSuggerito);
                if (suggerito == null) return 1;

                boolean ok = clientService.inserisciSuggerimentoLibro(suggerimentoID, suggerito.getID());
                return ok ? 0 : 3;
            }
        };

        task.setOnSucceeded(ev -> {
            int code = task.getValue();
            Alert a;
            switch (code) {
                case 0:
                    alertI.setContentText("SUGGERIMENTO INVIATO CON SUCCESSO.");
                    alertI.showAndWait();
                    titleField.clear();
                    break;
                case 1:
                    alertE.setContentText("LIBRO SUGGERITO NON TROVATO.");
                    alertE.showAndWait();
                    break;
                case 2:
                    alertE.setContentText("NUMERO MASSIMO DI SUGGERIMENTI PER QUESTO LIBRO RAGGIUNTO.");
                    alertE.showAndWait();
                    break;
                default:
                    alertE.setContentText("ERRORE INVIO SUGGERIMENTO.");
                    alertE.showAndWait();
            }
        });

        task.setOnFailed(ev -> {
            task.getException().printStackTrace();
            alertE.setContentText("ERRORE INVIO SUGGERIMENTO.");
            alertE.showAndWait();
        });

        new Thread(task).start();
    }
}