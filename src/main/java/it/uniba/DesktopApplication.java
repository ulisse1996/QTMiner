package it.uniba;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringBinding;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import org.eclipse.jetty.annotations.AnnotationConfiguration;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.util.resource.Resource;
import org.eclipse.jetty.webapp.*;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.URL;

/**
 * Classe che crea l'applicazione principale dove verranno mostre le informazioni recuperate dal
 * server locale o remoto
 */
final
class DesktopApplication {

    /**
     * Metodo statico che instanzia un nuovo oggetto di questa classe e avvia il metodo run che inizializza
     * il server locale e la GUI principale
     * @param ip da cui invocare le richieste al server
     * @throws Exception in caso di errore durante la creazione del server locale
     */
    static void avvia(String ip) throws Exception {
        new DesktopApplication().run(ip);
    }

    /**
     * Avvia il server locale e invoca l'inizializzazione della GUI principale
     * @param ip da cui invocale le richieste al server
     * @throws Exception in caso di errori durante la creazione del server locale
     */
    private void run(String ip) throws Exception {
        avviaServer();
        launch(ip);
    }

    /**
     * Avvia il server locale
     * @throws Exception in caso di errori durante la creazione del server locale
     */
    void run() throws Exception {
        avviaServer();
        Stage primaryStage = new Stage();
        Button button = new Button("Spegni Server");
        button.setOnMouseClicked(event -> primaryStage.hide());
        VBox vBox = new VBox(button);
        vBox.setAlignment(Pos.CENTER);
        vBox.setMinSize(50,100);
        Scene scene = new Scene(vBox);
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    private void avviaServer() throws Exception {
        URL webRootLocation = DesktopApplication.class.getResource("/META-INF");

        WebAppContext context = new WebAppContext();
        context.setBaseResource(Resource.newResource(webRootLocation));
        context.setContextPath("/app");
        context.setAttribute("org.eclipse.jetty.server.webapp.ContainerIncludeJarPattern", ".*");
        context.setConfigurationDiscovered(true);
        context.setConfigurations(new Configuration[]{
                new AnnotationConfiguration(),
                new WebInfConfiguration(),
                new WebXmlConfiguration(),
                new MetaInfConfiguration()
        });
        context.getServletContext().setExtendedListenerTypes(true);

        Server server = new Server(8080);
        server.setHandler(context);
        server.start();
    }

    /**
     * Lancia la {@link WebView} che mostrerà le informazioni recuperate dal server
     * locale o remoto
     * @param ip verso cui effettuare le richieste al server
     */
    void launch(String ip) {
        Stage primaryStage = new Stage();
        primaryStage.setTitle("QTMiner");
        WebView view = new WebView();


        view.getEngine().load(ip);
        view.setContextMenuEnabled(false);

        VBox vBox = new VBox();
        vBox.getChildren().add(view);
        Scene scene = new Scene(vBox);
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }
}
