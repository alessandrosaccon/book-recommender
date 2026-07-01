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
import javafx.scene.control.*;
import javafx.scene.layout.*;

/**
 * Gestisce la vista del menu "Gestione Libreria" (repository).
 * Fornisce le opzioni di navigazione per le funzionalità di ricerca
 * e visualizzazione delle informazioni sui libri.
 * @author Broggini Luca
 * @author De Nicola Matteo
 * @author Pianezzola Andrea
 * @author Saccon Alessandro
 * @author Siragusa Valerio
 * @version 1.0
 */
public class repositoryController {

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
     * Costruttore del controller del repository.
     * Inizializza l'applicazione principale, il servizio client e
     * chiama il metodo per creare la vista.
     *
     * @param mainApp L'istanza principale dell'applicazione.
     * @param clientService Il servizio client per la comunicazione con il server.
     * @throws IllegalArgumentException se i parametri dovessero essere null
     */
     public repositoryController(Bookapp mainApp, ClientService clientService) {
        if (mainApp == null || clientService == null) {
            throw new IllegalArgumentException("mainApp e clientService non possono essere null");
        }
        this.mainApp = mainApp;
        this.clientService = clientService;
        createView();
    }

    /**
     * Costruisce l'interfaccia utente (UI) per il menu "Gestione Libreria".
     * Inizializza i pulsanti per "Ricerca Informazioni", "Visualizza Informazioni"
     * e "Indietro", impostando le relative azioni di navigazione.
     */
    private void createView() {

        if (Bookapp.rootLayout == null) {
            Alert a = new Alert(Alert.AlertType.ERROR);
            a.setContentText("Layout non inizializzato. Riavvia l'applicazione.");
            a.showAndWait();
            return;
        }

        VBox card = new VBox(18);
        card.getStyleClass().add("card");

        Label titolo = new Label("Gestione Libreria");
        titolo.getStyleClass().add("menu-titolo");

        Button ricercaInfoLibriBtn = new Button("🔍 Ricerca Informazioni");
        Button visualizzaInfoLibroBtn = new Button("📖 Visualizza Informazioni");
        Button backButton = new Button("Indietro");

        ricercaInfoLibriBtn.getStyleClass().add("main-btn");
        visualizzaInfoLibroBtn.getStyleClass().add("main-btn");
        backButton.getStyleClass().add("back-btn");

        ricercaInfoLibriBtn.setOnAction(e -> showCercaLibro());
        visualizzaInfoLibroBtn.setOnAction(e -> showVisualizzaLibro());
        backButton.setOnAction(e -> mainApp.showHomeView());

        card.getChildren().addAll(
            titolo,
            ricercaInfoLibriBtn,
            visualizzaInfoLibroBtn,
            backButton
        );

        Bookapp.rootLayout.setCenter(card);
    }

    /**
     * Mostra la schermata per la ricerca di informazioni sui libri.
     * Inizializza e visualizza il {@code ricercaInfoController}.
     * @throws Exception se avvengono errori durante la creazione del controller
     */
    void showCercaLibro() {
        try {
            ricercaInfoController controller = new ricercaInfoController(mainApp, clientService);
        } 
        catch (Exception e) {
            System.err.println("showCercaLibro: errore caricando la vista: " + e.getMessage());
        }
    }

    /**
     * Mostra la schermata per la visualizzazione delle informazioni di un libro specifico.
     * Inizializza e visualizza il {@code visualizzaInfoController}
     * @throws Exception se avvengono errori durante la creazione del controller
     */
    
    void showVisualizzaLibro() {
        try {
            visualizzaInfoController controller = new visualizzaInfoController(mainApp, clientService);
        }    
        catch (Exception e) {
            System.err.println("showVisualizzaLibro: errore caricando la vista: " + e.getMessage());
        }
    }

}