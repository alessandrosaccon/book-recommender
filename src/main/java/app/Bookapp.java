/* DE NICOLA MATTEO VINCENZO, MATRICOLA 757933, VA
    SIRAGUSA VALERIO, MATRICOLA 756227, VA
    SACCON ALESSANDRO, MATRICOLA 756145, VA
    PIANEZZOLA ANDREA, MATRICOLA 756141, VA
    BROGGINI LUCA, MATRICOLA 756446, VA
*/
package app;

import client.ClientService;
import model.Utente;
import view.*;

import javafx.application.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.geometry.*;
import javafx.stage.Screen;

/**
 * Applicazione JavaFX di Book Recommender.
 * <p>
 * Gestisce lo Stage principale, il ciclo di vita (start/stop), la connessione al servizio client,
 * lo stato dell’utente autenticato e la navigazione tra Home, Login, Registrazione e Repository.
 * Inizializza il layout radice (BorderPane), applica i CSS dal classpath e mostra le viste.
 * </p>
 * @author Broggini Luca
 * @author De Nicola Matteo
 * @author Pianezzola Andrea
 * @author Saccon Alessandro
 * @author Siragusa Valerio
 * @version 1.0
 */
public class Bookapp extends Application {

    /**
     * <code>primaryStage</code>
     * Stage principale dell'applicazione JavaFX su cui viene impostata la Scene.
     * <p>Fornito dal toolkit in start(Stage) e mantenuto per la navigazione.</p>
     */
    private Stage primaryStage;

    /**
     * <code>clientService</code>
     * Servizio client per le comunicazioni con il server (richieste/risposte).
     * <p>Deve essere inizializzato prima di invocare operazioni di rete.</p>
     */
    private ClientService clientService; 

    /**
     * <code>loggedUser</code>
     * Utente attualmente autenticato nell'applicazione (sessione corrente).
     * <p>Può essere null se non è stato effettuato il login.</p>
     */
    private Utente loggedUser; 

    /**
     * <code>rootLayout</code>
     * Layout radice condiviso (BorderPane) dell'interfaccia.
     * <p>Usato per impostare dinamicamente le viste al centro della scena.</p>
     */
    public static BorderPane rootLayout;

    /**
     * Costruttore vuoto.
     */
    public Bookapp(){}


    /**
     * Imposta l'utente attualmente autenticato nell'applicazione.
     * @param user istanza dell'utente autenticato da memorizzare come sessione corrente
     */
    public void setLoggedUser(Utente user) {
        this.loggedUser = user;
    }

    /**
     * Restituisce l'utente attualmente autenticato, se presente.
     * @return l'utente loggato oppure null se non esiste una sessione attiva
     */
    public Utente getLoggedUser() {
        return this.loggedUser;
    }

    /**
     * Restituisce l'ID dell'utente autenticato, oppure -1 se nessun utente è loggato.
     * @return identificatore numerico dell’utente loggato o -1 se assente
     */
    public int getLoggedUserId() {
        return loggedUser != null ? loggedUser.getID() : -1;
    }

    /**
     * Punto d'ingresso dell'app JavaFX: inizializza lo Stage principale, apre la connessione client e mostra la Home.
     * <p>Non creare Scene o Stage nel costruttore o in init(); usarli in start è il percorso corretto del ciclo di vita JavaFX.</p>
     * @param primaryStage finestra principale fornita dal toolkit JavaFX
     * @throws Exception se avvengono errori durante l'inizializzazione dell'interfaccia o servizi correlati
     */
    @Override
    public void start(Stage primaryStage) throws Exception 
    {
        this.primaryStage = primaryStage;
        this.primaryStage.setMaximized(true);
        this.clientService = new ClientService();
        boolean connected = clientService.exec();

        if (!connected) 
        {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Connessione fallita");
            alert.setHeaderText("Impossibile connettersi al server");
            alert.setContentText("Verifica che il server sia avviato e riprova.");
            alert.showAndWait();
            Platform.exit();
            return;
        }

        initRootLayout();
        showHomeView();
    }

    /**
     * Inizializza il layout radice (BorderPane), applica il foglio di stile dal classpath e mostra la scena sullo Stage.
     * <p>Il percorso delle risorse dovrebbe essere assoluto a partire dalla root del classpath (es. /view/style.css).
     * getResource() restituisce null se la risorsa non è nel classpath.</p>
     */
    public void initRootLayout() {
        rootLayout = new BorderPane();
        Scene scene = new Scene(rootLayout);
        try 
        {
            String cssPath = getClass().getResource("/view/style.css").toExternalForm();
            scene.getStylesheets().add(cssPath);
        } 
        catch (NullPointerException e) 
        {
            System.err.println("Errore: Impossibile trovare il file /view/style.css. Assicurati che sia in src/main/resources/view/");
            e.printStackTrace();
        }
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * Mostra la vista Home costruendo i nodi principali e i pulsanti di navigazione e posizionandoli nel centro del BorderPane.
     */
    public void showHomeView() {
        VBox card = new VBox(18);
        card.getStyleClass().add("card");
    
        Label titolo = new Label("Book Recommender");
        titolo.getStyleClass().add("menu-titolo");

    
        Button consultaRepositoryBtn = new Button("📚 Consulta libreria");
        Button registerBtn = new Button("📝 Registrazione");
        Button loginBtn = new Button("🔑 Login");

        consultaRepositoryBtn.getStyleClass().add("main-btn");
        registerBtn.getStyleClass().add("reg-btn");
        loginBtn.getStyleClass().add("login-btn");

        consultaRepositoryBtn.setOnAction(e -> showRepository());
        loginBtn.setOnAction(e -> showLogin());
        registerBtn.setOnAction(e -> showRegistrazione());

        VBox btnBox = new VBox(14);
        btnBox.setAlignment(Pos.CENTER);
        btnBox.getChildren().addAll(consultaRepositoryBtn, registerBtn, loginBtn);

        card.getChildren().addAll(titolo, btnBox);

        if (rootLayout != null) 
            rootLayout.setCenter(card);
        else 
            System.err.println("showHomeView: rootLayout è null; chiamare prima initRootLayout().");
    }

    /**
     * Apre la vista di login inizializzando il relativo controller con riferimenti all'app e ai servizi client.
     */
    public void showLogin() {
        try {
            loginController controller = new loginController(this, clientService);
        } 
        catch (Exception e) {
            System.err.println("showLogin: errore caricando la vista: " + e.getMessage());
        }
    }

    /**
     * Apre la vista di registrazione inizializzando il relativo controller con riferimenti all'app e ai servizi client.
     */
    public void showRegistrazione() {
        try {
            registrazioneController controller = new registrazioneController(this, clientService);
        } 
        catch (Exception e) {
            System.err.println("showRegistrazione: errore caricando la vista: " + e.getMessage());
        }
    }

    /**
     * Apre la vista del repository/libreria inizializzando il relativo controller con riferimenti all'app e ai servizi client.
     */
    public void showRepository() {
        try {
            repositoryController controller = new repositoryController(this, clientService);
        } 
        catch (Exception e) {
            System.err.println("showRepository: errore caricando la vista: " + e.getMessage());
        }
    }

    /**
     * Torna al menu principale mostrando nuovamente la Home.
     */
    public void showMainMenuScene() {
        showHomeView();
    }

    /**
     * Metodo di terminazione dell'app JavaFX: chiude risorse e connessioni prima dell'uscita.
     */
    @Override
    public void stop() throws Exception {
        try {
            if (clientService != null) {
                clientService.close();
            }
        } 
        catch (Exception e) {
            System.err.println("stop: errore durante la chiusura delle risorse: " + e.getMessage());
        }
    }

    /**
     * Avvia l'applicazione JavaFX. Può essere omesso poiché le Application possono essere lanciate senza main,
     * ma resta utile per passare argomenti.
     * @param args argomenti da linea di comando passati all'applicazione
     */
    public static void main(String[] args) {
        launch(args);
    }
}