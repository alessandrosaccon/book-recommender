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
 * Gestisce la vista e la logica per la registrazione di un nuovo utente.
 * <p>
 * Costruisce l'interfaccia utente per l'inserimento dei dati anagrafici
 * (nome, cognome, CF, email, username, password) e gestisce la validazione
 * e l'invio dei dati al server per la creazione del nuovo account.
 * </p>
 * @author Broggini Luca
 * @author De Nicola Matteo
 * @author Pianezzola Andrea
 * @author Saccon Alessandro
 * @author Siragusa Valerio
 * @version 1.0
 */
public class registrazioneController {

    
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
     * <code>searchNome</code>
     * Campo di testo per l'inserimento del nome.
     */
    private TextField searchNome;
    /**
     * <code>searchCognome</code>
     * Campo di testo per l'inserimento del cognome.
     */
    private TextField searchCognome;
    /**
     * <code>searchCF</code>
     * Campo di testo per l'inserimento del Codice Fiscale.
     */
    private TextField searchCF;
    /**
     * <code>searchMail</code>
     * Campo di testo per l'inserimento dell'email.
     */
    private TextField searchMail;
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
     * Costruttore del controller di registrazione.
     * <p>
     * Inizializza l'applicazione principale, il servizio client e gli alert,
     * e chiama il metodo per creare la vista.
     * </p>
     * @param mainApp L'istanza principale dell'applicazione.
     * @param clientService Il servizio client per la comunicazione con il server.
     * @throws IllegalArgumentException se i parametri dovessero essere null
     */
    public registrazioneController(Bookapp mainApp, ClientService clientService) {
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
     * Costruisce l'interfaccia utente (UI) per la schermata di registrazione.
     * <p>
     * Inizializza i campi di testo per tutti i dati richiesti (Nome, Cognome,
     * CF, Email, Username, Password) e i pulsanti "Registrati" e "Indietro",
     * impostando le relative azioni.
     * </p>
     */
    private void createView() {

        if (Bookapp.rootLayout == null) {
            alertE.setContentText("Layout non inizializzato. Riavvia l'applicazione.");
            alertE.showAndWait();
            return;
        }

        VBox card = new VBox(13);
        card.getStyleClass().add("card");

        Label titolo = new Label("Crea il tuo account");
        titolo.getStyleClass().add("menu-titolo");

        Label labelNome = new Label("Nome");
        labelNome.getStyleClass().add("label");
        searchNome = new TextField();
        searchNome.setPromptText("Inserisci il tuo nome");
        searchNome.getStyleClass().add("text-field");

        Label labelCognome = new Label("Cognome");
        labelCognome.getStyleClass().add("label");
        searchCognome = new TextField();
        searchCognome.setPromptText("Inserisci il tuo cognome");
        searchCognome.getStyleClass().add("text-field");

        Label labelCF = new Label("Codice Fiscale");
        labelCF.getStyleClass().add("label");
        searchCF = new TextField();
        searchCF.setPromptText("Inserisci il tuo codice fiscale");
        searchCF.getStyleClass().add("text-field");

        Label labelMail = new Label("Email");
        labelMail.getStyleClass().add("label");
        searchMail = new TextField();
        searchMail.setPromptText("Inserisci la tua email");
        searchMail.getStyleClass().add("text-field");

        Label labelUser = new Label("Username");
        labelUser.getStyleClass().add("label");
        searchUser = new TextField();
        searchUser.setPromptText("Inserisci il tuo username");
        searchUser.getStyleClass().add("text-field");

        Label labelPassword = new Label("Password");
        labelPassword.getStyleClass().add("label");
        searchPassword = new PasswordField();
        searchPassword.setPromptText("Crea una password");
        searchPassword.getStyleClass().add("password-field");

        Button regBtn = new Button("📝 Registrati");
        regBtn.getStyleClass().add("reg-btn-small");
        regBtn.setOnAction(e -> performRegistration());

        Button backBtn = new Button("Indietro");
        backBtn.getStyleClass().add("back-btn-small");
        backBtn.setOnAction(e -> mainApp.showHomeView());

        HBox btnBox = new HBox(14, regBtn, backBtn);
        btnBox.setAlignment(Pos.CENTER);

        card.getChildren().addAll(
            titolo,
            labelNome, searchNome,
            labelCognome, searchCognome,
            labelCF, searchCF,
            labelMail, searchMail,
            labelUser, searchUser,
            labelPassword, searchPassword,
            btnBox
        );

        mainApp.rootLayout.setCenter(card);
    }

    /**
     * Gestisce il processo di registrazione.
     * <p>
     * 1. Recupera tutti i dati dai campi di input e valida che non siano vuoti.
     * </p><p>
     * 2. Avvia un Task asincrono (<code>controlliPreRegistrazione</code>) per verificare:
     * <ul>
     * <li>La validità del formato del Codice Fiscale.</li>
     * <li>L'unicità del Codice Fiscale.</li>
     * <li>L'unicità dell'Username.</li>
     * </ul>
     * </p><p>
     * 3. Se i controlli preliminari falliscono, mostra un alert di errore specifico.
     * </p><p>
     * 4. Se i controlli preliminari hanno successo, avvia un secondo Task asincrono
     * (<code>regTask</code>) per effettuare la registrazione effettiva tramite il clientService.
     * </p><p>
     * 5. Mostra un alert di successo o fallimento al termine della registrazione.
     * </p>
     */
    private void performRegistration() {
        String nome = searchNome.getText().trim();
        String cognome = searchCognome.getText().trim();
        String CF = searchCF.getText().trim();
        String mail = searchMail.getText().trim();
        String user = searchUser.getText().trim();
        String password = searchPassword.getText().trim();

        if (nome.isEmpty() || cognome.isEmpty() || CF.isEmpty() || mail.isEmpty() || user.isEmpty() || password.isEmpty()) {
            alertI.setTitle("CAMPO VUOTO");
            alertI.setHeaderText(null);
            alertI.setContentText("COMPILA TUTTI I CAMPI!");
            alertI.showAndWait();
            return;
        }

        Task<Integer> controlliPreRegistrazione = new Task<>() { 
            // 0 = ok
            // 1 = CF non valido
            // 2 = CF gia' esistente
            // 3 = username già esistente
            @Override
            protected Integer call() throws Exception {
                if (!clientService.validitaCF(CF)) return 1;

                if (clientService.esisteCF(CF)) return 2;

                if (clientService.controllaUtente(user)) return 3;

                return 0;
            }
        };

        controlliPreRegistrazione.setOnSucceeded(e -> {
            int code = controlliPreRegistrazione.getValue();
            if (code == 1) {
                alertE.setTitle("CODICE FISCALE NON VALIDO");
                alertE.setHeaderText(null);
                alertE.setContentText("IL CODICE FISCALE INSERITO NON RISPETTA IL FORMATO RICHIESTO.");
                alertE.showAndWait();
                return;
            }

            if (code == 2) {
                alertE.setTitle("CODICE FISCALE GIÀ PRESENTE");
                alertE.setHeaderText(null);
                alertE.setContentText("IL CODICE FISCALE INSERITO È GIÀ REGISTRATO.");
                alertE.showAndWait();
                return;
            }

            if (code == 3) {
                alertE.setTitle("USERNAME NON DISPONIBILE");
                alertE.setHeaderText(null);
                alertE.setContentText("LO USERNAME SCELTO È GIÀ IN USO. SCEGLINE UN ALTRO.");
                alertE.showAndWait();
                return;
            }

            Task<Boolean> regTask = new Task<>() {
                @Override
                protected Boolean call() throws Exception {
                    return clientService.Registrazione(nome, cognome, CF, mail, user, password);
                }
            };

            regTask.setOnSucceeded(ev -> {
                Boolean ok = regTask.getValue();
                Alert alert = new Alert(ok ? Alert.AlertType.INFORMATION : Alert.AlertType.ERROR);
                alert.setTitle(ok ? "REGISTRAZIONE RIUSCITA" : "REGISTRAZIONE FALLITA");
                alert.setHeaderText(null);
                alert.setContentText(ok ? "REGISTRAZIONE EFFETTUATA CON SUCCESSO!" : "REGISTRAZIONE FALLITA! RIPROVA.");
                alert.showAndWait();
            });

            regTask.setOnFailed(ev -> {
                alertE.setContentText("ERRORE DURANTE LA REGISTRAZIONE.");
                alertE.showAndWait();
            });

            new Thread(regTask).start();
        });

        controlliPreRegistrazione.setOnFailed(e -> {
            alertE.setContentText("Errore durante la verifica dei dati inseriti.");
            alertE.showAndWait();
        });

        new Thread(controlliPreRegistrazione).start();
    }

}