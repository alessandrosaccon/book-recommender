/* DE NICOLA MATTEO VINCENZO, MATRICOLA 757933, VA
    SIRAGUSA VALERIO, MATRICOLA 756227, VA
    SACCON ALESSANDRO, MATRICOLA 756145, VA
    PIANEZZOLA ANDREA, MATRICOLA 756141, VA
    BROGGINI LUCA, MATRICOLA 756446, VA
*/
package view;

import client.ClientService;
import app.Bookapp;
import model.Utente;

import javafx.concurrent.Task;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;

/**
 * Gestisce la vista e la logica per il login dell'utente.
 * <p>
 * Costruisce l'interfaccia utente per l'inserimento di username e password
 * e gestisce la verifica delle credenziali tramite il ClientService.
 * Se il login ha successo, reindirizza alla schermata principale dell'utente loggato.
 * </p>
 * @author Broggini Luca
 * @author De Nicola Matteo
 * @author Pianezzola Andrea
 * @author Saccon Alessandro
 * @author Siragusa Valerio
 * @version 1.0
 */

public class loginController {

    /**
     * <code>mainApp</code>
     * Riferimento all'applicazione principale (Bookapp).
     */
    private Bookapp mainApp;
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
     * <code>searchUser</code>
     * Campo di testo per l'inserimento dell'username.
     */
    private TextField searchUser;
    /**
     * <code>searchPassword</code>
     * Campo password per l'inserimento della password.
     */
    private PasswordField searchPassword;

    /**
     * Costruttore del controller di login.
     * <p>
     * Inizializza l'applicazione principale, il servizio client e gli alert,
     * e chiama il metodo per creare la vista.
     * </p>
     * @param mainApp L'istanza principale dell'applicazione.
     * @param clientService Il servizio client per la comunicazione con il server.
     * @throws IllegalArgumentException se i parametri dovessero essere null
     */
    public loginController(Bookapp mainApp, ClientService clientService) {
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
     * Costruisce l'interfaccia utente (UI) per la schermata di login.
     * <p>
     * Inizializza i campi di testo per username e password, i pulsanti
     * "Entra" e "Indietro", e imposta le relative azioni.
     * </p>
     */

    public void createView() {

        if (Bookapp.rootLayout == null) {
            alertE.setContentText("Layout non inizializzato. Riavvia l'applicazione.");
            alertE.showAndWait();
            return;
        }

        VBox card = new VBox(16);
        card.getStyleClass().add("card");

        Label titolo = new Label("Accedi al tuo account");
        titolo.getStyleClass().add("menu-titolo");

        Label labelUser = new Label("Username");
        labelUser.getStyleClass().add("label");
        searchUser = new TextField();
        searchUser.setPromptText("Inserisci il tuo username");
        searchUser.getStyleClass().add("text-field");

        Label labelPassword = new Label("Password");
        labelPassword.getStyleClass().add("label");
        searchPassword = new PasswordField();
        searchPassword.setPromptText("Inserisci la tua password");
        searchPassword.getStyleClass().add("password-field");

        Button enterBtn = new Button("🔑 Entra");
        enterBtn.getStyleClass().add("login-btn-small");
        enterBtn.setOnAction(e -> performSearch());

        Button backBtn = new Button("Indietro");
        backBtn.getStyleClass().add("back-btn-small");
        backBtn.setOnAction(e -> mainApp.showMainMenuScene());

        HBox btnBox = new HBox(14, enterBtn, backBtn);
        btnBox.setAlignment(Pos.CENTER);

        card.getChildren().addAll(
            titolo,
            labelUser, searchUser,
            labelPassword, searchPassword,
            btnBox
        );

        Bookapp.rootLayout.setCenter(card);
    }

    /**
     * Mostra la schermata principale dell'utente dopo un login riuscito.
     * <p>Inizializza e visualizza il <code>trueLoginController</code>.</p>
     */
    public void showTrueLogin(){
        try {
            trueLoginController login = new trueLoginController(mainApp, this, clientService);
        }
        catch(Exception e){
            System.err.println("showTrueLogin: errore caricando la vista: " + e.getMessage());
        }
    }

    /**
     * Gestisce il tentativo di login.
     * <p>
     * Recupera username e password dai campi di testo, valida che non siano vuoti
     * e avvia un Task asincrono per chiamare il metodo <code>clientService.login()</code>.
     * </p><p>
     * Gestisce il risultato del login: in caso di successo, imposta l'utente loggato
     * nell'app principale e mostra la schermata successiva (<code>showTrueLogin()</code>);
     * in caso di fallimento, mostra un alert di errore.
     * </p>
     */
    private void performSearch() {
        String user = searchUser.getText().trim();
        String password = searchPassword.getText().trim();
        if (user.isEmpty() || password.isEmpty()) {
            alertI.setTitle("CAMPO VUOTO");
            alertI.setHeaderText(null);
            alertI.setContentText("COMPILA USERNAME E PASSWORD.");
            alertI.showAndWait();
            return;
        }

        Task<Utente> searchTask = new javafx.concurrent.Task<>() {
            @Override
            protected Utente call() throws Exception {
                return clientService.login(user, password);
            }
        };

        searchTask.setOnSucceeded(e -> {
            Utente utente = searchTask.getValue();
            if (utente == null) {
                alertE.setTitle("LOGIN FALLITO");
                alertE.setHeaderText(null);
                alertE.setContentText("USERNAME O PASSWORD SBAGLIATI!");
                alertE.showAndWait();
            } else {
                mainApp.setLoggedUser(utente);
                showTrueLogin();
            }
        });

        searchTask.setOnFailed(e -> {
            alertE.setContentText("ERRORE DURANTE IL LOGIN.");
            alertE.showAndWait();
            searchTask.getException().printStackTrace();
        });

        new Thread(searchTask).start();
    }

}