/* DE NICOLA MATTEO VINCENZO, MATRICOLA 757933, VA
    SIRAGUSA VALERIO, MATRICOLA 756227, VA
    SACCON ALESSANDRO, MATRICOLA 756145, VA
    PIANEZZOLA ANDREA, MATRICOLA 756141, VA
    BROGGINI LUCA, MATRICOLA 756446, VA
*/
package view;

import client.ClientService;
import app.Bookapp;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.layout.*;

/**
 * Controller della vista "Visualizza Info" dell’app Book Recommender.
 * <p>
 * Inizializza un menu con azioni per visualizzare informazioni libro,
 * suggerimenti con voto complessivo e media valutazioni, e fornisce il
 * tasto Indietro per tornare al Repository.
 * @author Broggini Luca
 * @author De Nicola Matteo
 * @author Pianezzola Andrea
 * @author Saccon Alessandro
 * @author Siragusa Valerio
 * @version 1.0
 */
public class visualizzaInfoController {

    /**
     * Riferimento all’applicazione principale JavaFX, usato per la navigazione e l’accesso al layout radice.
     */
    private Bookapp mainApp;

    /**
     * Servizio client per la comunicazione con il server (richieste/risposte).
     */
    private ClientService clientService;

    /**
     * Crea il controller della vista "Visualizza Info" inizializzando i riferimenti
     * all'app principale e al servizio client, quindi costruisce l'interfaccia.
     * @param mainApp riferimento all'applicazione principale per la navigazione
     * @param clientService servizio client per eventuali operazioni lato server
     * @throws IllegalArgumentException se mainApp o clientService sono null
     */
    public visualizzaInfoController(Bookapp mainApp, ClientService clientService) {
        if (mainApp == null || clientService == null) {
            throw new IllegalArgumentException("mainApp e clientService non possono essere null");
        }
        this.mainApp = mainApp;
        this.clientService = clientService;
        createView();
    }

    /**
     * Costruisce il layout della vista: crea i pulsanti per informazioni libro,
     * suggerimenti con voto complessivo, media valutazioni e il pulsante Indietro;
     * collega gli handler degli eventi e imposta il contenuto nel BorderPane radice.
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

        Button visualizzaInfolibroBtn = new Button("Informazioni Libro");
        Button visualizzaSuggMedlibroBtn = new Button("Suggerimenti e Voto complessivo");
        Button visualizzaMedlibroBtn = new Button("Media valutazioni");
        Button backButton = new Button("Indietro");

        visualizzaInfolibroBtn.getStyleClass().add("main-btn");
        visualizzaSuggMedlibroBtn.getStyleClass().add("main-btn");
        visualizzaMedlibroBtn.getStyleClass().add("main-btn");
        backButton.getStyleClass().add("back-btn");

        visualizzaInfolibroBtn.setOnAction(e -> showInfoLibro());
        visualizzaSuggMedlibroBtn.setOnAction(e -> showSuggLibro());
        visualizzaMedlibroBtn.setOnAction(e -> showRatingLibro());
        backButton.setOnAction(e -> mainApp.showRepository());

        if (mainApp.rootLayout == null) {
            throw new IllegalStateException("rootLayout non è inizializzato in mainApp");
        }
        card.getChildren().addAll(visualizzaInfolibroBtn, visualizzaSuggMedlibroBtn, visualizzaMedlibroBtn, backButton);
        mainApp.rootLayout.setCenter(card);
    }

    /**
     * Mostra la sezione con le informazioni dettagliate del libro
     * inizializzando il relativo controller della vista.
     * @throws Exception se avvengono errori durante la creazione del controller
     */
    private void showInfoLibro() {
        try {
            viewInfoController controller = new viewInfoController(mainApp, clientService);
        } 
        catch (Exception e) {
            System.err.println("showInfoLibro: errore caricando la vista: " + e.getMessage());
        }
    }

    /**
     * Mostra la sezione con i suggerimenti relativi al libro e il voto complessivo
     * inizializzando il relativo controller della vista.
     * @throws Exception se avvengono errori durante la creazione del controller
     */
    private void showSuggLibro() {
        try {
            viewSuggestionController controller = new viewSuggestionController(mainApp, clientService);
        } 
        catch (Exception e) {
            System.err.println("showSuggLibro: errore caricando la vista: " + e.getMessage());
        }
    }

    /**
     * Mostra la sezione con la media delle valutazioni del libro
     * inizializzando il relativo controller della vista.
     * @throws Exception se avvengono errori durante la creazione del controller
     */
    private void showRatingLibro() {
        try {
            viewRatingsController controller = new viewRatingsController(mainApp, clientService);
        } 
        catch (Exception e) {
            System.err.println("showRatingLibro: errore caricando la vista: " + e.getMessage());
        }
    }

}
