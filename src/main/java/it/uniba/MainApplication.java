package it.uniba;

import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * Classe principale che permette di recupare le informazioni iniziali per l'avvio del
 * browser web
 */
final
public class MainApplication extends Application {

    private static final String DEFAULT = "http://localhost:8080/app/";

    private Stage mainStage;
    private Stage configStage;

    /**
     * Metodo principale per invocare l'inzio dell'applicazione
     * Lancia il method "launch" di {@link Application} che inizializza la GUI
     * @param args parametri di esecuzione
     */
    public static void main(String... args) {
        launch(args);
    }

    /**
     * Implementazione del metodo astratto ereditato da {@link Application} per poter creare
     * e avviare la main stage.
     * @param primaryStage stage primaria
     * @throws Exception in caso di errori durante la costruzione dello stage
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        BorderPane pane = new BorderPane();
        pane.setPadding(new Insets(10,10,10,10));
        pane.setMinSize(300,150);
        VBox box = new VBox();
        box.setAlignment(Pos.CENTER);
        box.setSpacing(5);
        VBox buttonsBox = new VBox();
        buttonsBox.setAlignment(Pos.CENTER);
        buttonsBox.setSpacing(5);
        pane.setCenter(box);
        Scene scene = new Scene(pane);

        Label label = new Label("Seleziona una opzione");
        Button locale = new Button("Client Server Locale");
        locale.setOnMouseClicked(event -> {
            try {
                DesktopApplication.avvia(DEFAULT);
                mainStage.hide();
            } catch (Exception ex) {
                ex.printStackTrace();
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText("Errore durante l'inizializzazione del server");
                alert.showAndWait();
            }
        });
        Button remoto = new Button("Client Server Remoto");
        remoto.setOnMouseClicked(event -> askRemote());

        Button startServer = new Button("Start Server");
        startServer.setOnMouseClicked(event -> askStartServer());

        buttonsBox.getChildren().addAll(locale, remoto, startServer);
        box.getChildren().addAll(label, buttonsBox);
        primaryStage.setScene(scene);
        this.mainStage = primaryStage;
        primaryStage.show();
    }

    /**
     * Avvia una istanza del server in locale
     */
    private void askStartServer() {
        try {
            new DesktopApplication().run();
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setContentText("Server avviato correttamente sulla porta 8080");
            alert.showAndWait();
            mainStage.hide();
        } catch (Exception ex) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText(ex.getMessage());
            alert.showAndWait();
        }
    }

    /**
     * Avvia un nuovo {@link Stage} con la richiesta dei parametri per il collegamento
     * a un server esterno.
     * All'avvio del nuovo Stage , il vecchio stage principale viene nascosto.
     */
    private void askRemote() {
        HBox boxHost = new HBox();
        boxHost.setSpacing(5);
        boxHost.setAlignment(Pos.CENTER);
        HBox boxPort = new HBox();
        boxPort.setSpacing(5);
        boxPort.setAlignment(Pos.CENTER);
        HBox boxContext = new HBox();
        boxPort.setSpacing(5);
        boxPort.setAlignment(Pos.CENTER);
        TextField context = new TextField();
        TextField host = new TextField();
        TextField port = new TextField();
        port.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d{0,7}")) {
                port.setText(oldValue);
            }
        });

        BooleanBinding hostBinding = Bindings.createBooleanBinding(() -> host.getText().length() > 0, host.textProperty());
        BooleanBinding portBinding = Bindings.createBooleanBinding(() -> port.getText().length() > 0, port.textProperty());
        Button submit = new Button("Submit");
        submit.disableProperty().bind(hostBinding.not().or(portBinding.not()));
        submit.setOnMouseClicked((event) -> closeAndInit(host.getText(),Integer.valueOf(port.getText()), context.getText()));

        boxPort.getChildren().addAll(new Label("Port"), port);
        boxHost.getChildren().addAll(new Label("Host"), host);
        boxContext.getChildren().addAll(new Label("Context"), context);
        VBox mainbox = new VBox();
        mainbox.setPadding(new Insets(10,10,10,10));
        mainbox.setSpacing(5);
        mainbox.setAlignment(Pos.CENTER);
        mainbox.getChildren().addAll(new Label("Inserire i dati del server"), boxHost, boxPort, boxContext, submit);

        Scene scene = new Scene(mainbox);
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.initOwner(mainStage);
        mainStage.hide();
        configStage = stage;
        stage.show();
    }

    /**
     * Chiude la {@link Stage} attualmente in uso e avvisa l'applicazione principale che visualizzare
     * il contenuto recuperato dal server
     * @param text host inserito dall'utente
     * @param port porta inserita dall'utente
     * @param context context dell'applicazione inserita dall'utente
     */
    private void closeAndInit(String text, int port, String context ) {
        try {
            String ip;
            if (text.contains("http") || text.contains("https")) {
                ip = text + ":" + port + "/" + context;
            } else {
                ip = "http://" + text + ":" + port + "/" + context;
            }
            new DesktopApplication().launch(ip);
            configStage.hide();
        } catch (Exception ex) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText(ex.getMessage());
            alert.showAndWait();
        }
    }

    /**
     * Override del metodo stop che effettua un exit del sistema alla chiusura dell'ultima {@link Stage}
     * visibile.
     * @throws Exception in caso di errori
     */
    @Override
    public void stop() throws Exception {
        System.exit(0);
    }
}
