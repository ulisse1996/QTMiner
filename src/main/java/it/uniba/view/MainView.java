package it.uniba.view;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasOrderedComponents;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import it.uniba.data.Data;
import it.uniba.data.EmptyDatasetException;
import it.uniba.data.SaveFile;
import it.uniba.database.*;
import it.uniba.mining.ClusteringRadiusException;
import it.uniba.mining.QTMiner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * View principale del server che permette di effettuare le possibili interazioni client-server
 * Corrisponde alla root default e utilizza il framework grafico di bootstrap
 */
@Route("")
@StyleSheet("/app/bootstrap.css")
public class MainView extends Div implements HasOrderedComponents {

    private static final Logger LOGGER = LoggerFactory.getLogger(MainView.class);
    private static final String SAVE = "Save";
    private static final String RESULT = "Result";
    private static final String ROW_DB = "ReadFromDB";
    private static final String ROW_FILE = "ReadFromFile";
    private static final String ROW_SAVE = "RowSave";
    private static final String TABLES = "Tables";
    private static final String ITERATION = "Iteration";
    private static final String READ_DB = "ReadDb";
    private static final String FILES = "Files";
    private static final String ROWS = "Rows";
    private static final String READ_FILE = "ReadFile" ;
    private static final String SAVE_NAME = "SaveName";
    private QTMiner qtMiner;

    /**
     * Costruttore della view che viene inizializzato quando si naviga sulla route definita da questo componente
     */
    public MainView() {
        VerticalLayout rows = new VerticalLayout();
        rows.setId(ROWS);
        rows.setAlignItems(FlexComponent.Alignment.CENTER);
        H2 title = new H2("Benvenuto in QTMiner");

        HorizontalLayout fromDb = createReadFromDb();
        fromDb.setId(ROW_DB);
        fromDb.setPadding(true);

        HorizontalLayout fromFile = createReadFromFile();
        fromFile.setId(ROW_FILE);
        fromFile.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        fromFile.setPadding(true);

        rows.add(title, fromDb, fromFile);

        add(rows);
    }

    /**
     * Crea il componente che permette di effettuare la lettura da File
     * @return un {@link HorizontalLayout} che contiene un bottone e una select con i nomi dei file salvati
     */
    private HorizontalLayout createReadFromFile() {
        Select<SaveFile> files = new Select<>();
        files.setId(FILES);
        files.setEmptySelectionCaption("Seleziona un file");
        files.setRequiredIndicatorVisible(true);
        try (DbAccess dbAccess = DbAccess.getInstance()) {
            List<SaveFile> saves = new TableData(dbAccess).getAllSaves();
            if (saves.isEmpty()) {
                files.setEnabled(false);
            } else files.setItems(saves);
        } catch (SQLException | DatabaseConnectionException e) {
            LOGGER.error("Errore durante il collegamento al Db/Set vuoto", e);
            createErrorDialog("Errore durante la connessione al Database");
        }
        files.addValueChangeListener(event -> checkValidFile());

        Button readFromFile = new Button("Leggi da File");
        readFromFile.setId(READ_FILE);
        readFromFile.addClassNames("btn","btn-success","disabled");
        readFromFile.setEnabled(false);
        readFromFile.addClickListener(event -> {
            removeSave();
            try {
                qtMiner = new QTMiner(files.getValue());
                showResult(false, null);
            } catch (IOException ex) {
                LOGGER.error("Errore durante la lettura da File");
            }
        });
        return new HorizontalLayout(files, readFromFile);
    }

    /**
     * Effettua la rimozione del componente che permette il salvataggio se presente
     */
    private void removeSave() {
        findComponentWithId(this, ROW_SAVE).ifPresent(value -> findComponentWithId(this, ROWS)
                .ifPresent(component -> ((VerticalLayout) component).remove(value)));
    }

    /**
     * Callback che controlla l'abilitazione del pulsante che permette di leggere da file.
     * Il pulsante è abilitato solo se ci sono valori nella select
     */
    @SuppressWarnings("unchecked")
    private void checkValidFile() {
        findComponentWithId(this, ROW_FILE).ifPresent(component -> {
            Select<SaveFile> files = (Select<SaveFile>) findComponentWithId((HorizontalLayout) component, FILES).get();
            if (files.getValue() != null) {
                ((Button) findComponentWithId((HorizontalLayout) component, READ_FILE).get()).removeClassName("disabled");
                ((Button) findComponentWithId((HorizontalLayout) component, READ_FILE).get()).setEnabled(true);
            }
        });
    }

    /**
     * Attiva il bottone salvataggio nella view
     */
    private void activateSave() {
        if (!findComponentWithId(this, SAVE).isPresent()) {
            addSaveToButtonList();
        }
    }

    /**
     * Callback che attiva il salvataggio nel caso di visualizzazione da Database
     */
    private void addSaveToButtonList() {
        findComponentWithId(this, ROWS)
                .ifPresent(component -> ((VerticalLayout) component).addComponentAtIndex(3,createSave()));
    }

    /**
     * Aggiunge il risultato alla view
     * @param value risultato dell'iterazione con il Database o File
     */
    private void addResult(String value) {
        Optional<Component> result = findComponentWithId(this, RESULT);
        if (result.isPresent()) {
            ((TextArea) result.get()).setValue(value);
        } else {
            findComponentWithId(this, ROWS)
                    .ifPresent(component -> ((VerticalLayout) component).add(createResult(value)));
        }
    }

    /**
     * Crea il componente che permette di visualizzare un risultato attraverso l'interazione con il Database
     * @return un {@link HorizontalLayout} contente il pulsante e la select con le tabelle (filtrate) da cui effettuare un qtmine
     */
    private HorizontalLayout createReadFromDb() {
        Select<String> tables = new Select<>();
        tables.setId(TABLES);
        tables.setEmptySelectionCaption("Seleziona una tabella");
        tables.setRequiredIndicatorVisible(true);
        tables.setItems(getItems());
        tables.addValueChangeListener(event -> checkValidDb());

        NumberField iteration = new NumberField();
        iteration.setId(ITERATION);
        iteration.setRequiredIndicatorVisible(true);
        iteration.setPlaceholder("Iterazioni");
        iteration.setStep(0.1);
        iteration.setMin(0.1);
        iteration.addValueChangeListener(event -> checkValidDb());

        Button readFromDB = new Button("Leggi da DB");
        readFromDB.setId(READ_DB);
        readFromDB.setEnabled(false);
        readFromDB.addClassNames("btn","btn-primary","disabled");
        readFromDB.addClickListener(event -> {
            try {
                Data data = new Data(tables.getValue());
                qtMiner = new QTMiner(iteration.getValue().intValue());
                qtMiner.compute(data);
                activateSave();
                showResult(true, data);
            } catch (SQLException | EmptySetException | DatabaseConnectionException ex) {
                LOGGER.error("Errore durante il collegamento al Db/Set vuoto");
                createErrorDialog("Errore durante la connessione al Database");
            } catch (NoValueException | EmptyDatasetException | ClusteringRadiusException ex) {
                LOGGER.error("Range tuple non valido");
                createErrorDialog("Iterazioni non valide");
            }
        });

        return new HorizontalLayout(tables, iteration, readFromDB);
    }

    private List<String> getItems() {
        try (DbAccess dbAccess = DbAccess.getInstance()) {
            return new TableData(dbAccess).tableNameList()
                    .stream()
                    .filter(s -> !s.equalsIgnoreCase("save"))
                    .collect(Collectors.toList());
        } catch (SQLException | DatabaseConnectionException ex) {
            return new ArrayList<>();
        }
    }

    /**
     * Callback che controlla se il pulsante di ricerca attraverso il Database è disabilitato o no
     */
    @SuppressWarnings("unchecked")
    private void checkValidDb() {
        findComponentWithId(this, ROW_DB).ifPresent(component -> {
            Select<String> tables = (Select<String>) findComponentWithId((HorizontalLayout) component, TABLES).get();
            NumberField iteration = (NumberField) findComponentWithId((HorizontalLayout) component, ITERATION).get();
            if (tables.getValue() != null && iteration.getValue() != null) {
                ((Button) findComponentWithId((HorizontalLayout) component, READ_DB).get()).removeClassName("disabled");
                ((Button) findComponentWithId((HorizontalLayout) component, READ_DB).get()).setEnabled(true);
            }
        });
    }

    /**
     * Crea un dialog in caso di errore bloccante che può essere chiuso attraverso un pulsante
     * @param message contenente l'errore
     */
    private void createErrorDialog(String message) {
        Dialog dialog = new Dialog();
        dialog.setCloseOnEsc(false);
        dialog.setCloseOnOutsideClick(false);

        Button riprova = new Button("Riprova");
        riprova.addClickListener(event -> dialog.close());

        VerticalLayout layout = new VerticalLayout(new Text(message), riprova);
        layout.setAlignItems(FlexComponent.Alignment.CENTER);
        dialog.add(layout);
        dialog.open();
    }

    /**
     * Callback che mostra il risultato di una operazione
     * @param fromDb indica se è stato richiamato attraverso il pulsante che comunica con il Database
     * @param data la classe che contiene gli esempi da visualizzare a schermo o {{@code null}} nel caso di lettura da File
     */
    private void showResult(boolean fromDb, Data data) {
        Optional<Component> component = findComponentWithId(this, "result");
        if (fromDb) {
            if (component.isPresent()) {
                ((Text) component.get()).setText(qtMiner.getClusterSet().toString());
            } else {
                addResult(qtMiner.getClusterSet().toString());
            }
        } else {
            if (component.isPresent()) {
                ((Text) component.get()).setText(qtMiner.getClusterSet().toString());
            } else {
                addResult(qtMiner.getClusterSet().toString());
            }
        }
    }

    /**
     * Crea il componente che contiene il {@link Button} di salvataggio e il {@link TextField} in cui immettere il nome del file da salvare
     * @return un {@link HorizontalLayout} con i componenti creati
     */
    private HorizontalLayout createSave() {
        TextField saveName = new TextField();
        saveName.setId(SAVE_NAME);
        saveName.setRequired(true);
        saveName.setPlaceholder("Nome del salvataggio");
        saveName.setMaxLength(30);
        saveName.setMinLength(1);
        saveName.setRequiredIndicatorVisible(true);
        saveName.addValueChangeListener(event -> checkSave());

        Button save = new Button("Salva risultato");
        save.setId(SAVE);
        save.addClassNames("btn","btn-danger","disabled");
        save.addClickListener(event -> {
            SaveFile saveFile = new SaveFile();
            saveFile.setFileName(saveName.getValue());
            try {
                this.qtMiner.salva(saveFile);
                replaceFiles();
                showAlert();
            } catch (IOException e) {
                createErrorDialog("Errore durante il salvataggio");
            } catch (SQLException | DatabaseConnectionException e) {
                createErrorDialog("Salvataggio con lo stesso nome presente");
            }
        });

        HorizontalLayout saveFile = new HorizontalLayout(saveName, save);
        saveFile.setId(ROW_SAVE);
        saveFile.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        saveFile.setPadding(true);
        return saveFile;
    }

    /**
     * Mostra un alert in alto a sinistra in caso di salvataggio avvenuto correttamente
     */
    private void showAlert() {
        Notification notification = new Notification(
                "Salvataggio Effettuato", 3000,
                Notification.Position.TOP_START);
        notification.open();
    }

    /**
     * Callback che ricarica i file presenti in caso di salvataggio di un nuovo file
     */
    @SuppressWarnings("unchecked")
    private void replaceFiles() {
        findComponentWithId(this, FILES).ifPresent(component -> {
            try (DbAccess dbAccess = DbAccess.getInstance()){
                List<SaveFile> saves = new TableData(dbAccess).getAllSaves();
                ((Select<SaveFile>) component).setItems(saves);
                ((Select<SaveFile>) component).setEnabled(true); // Only use is for no saves in db
            } catch (SQLException | DatabaseConnectionException e) {
                LOGGER.error("Errore durante il collegamento al Db/Set vuoto", e);
                createErrorDialog("Errore durante la connessione al Database");
            }
        });
    }

    /**
     * Callback che controlla se il {@link Button} di salvataggio è disabilitato o no
     */
    private void checkSave() {
        findComponentWithId(this, ROW_SAVE).ifPresent(component -> {
            TextField name = (TextField) findComponentWithId((HorizontalLayout) component, SAVE_NAME).get();
            if (name.getValue() != null) {
                ((Button) findComponentWithId((HorizontalLayout) component, SAVE).get()).removeClassName("disabled");
                ((Button) findComponentWithId((HorizontalLayout) component, SAVE).get()).setEnabled(true);
            }
        });
    }

    /**
     * Crea il componente che contiene il risultato da mostrare
     * @param value il valore contenuto nel componente
     * @return una {@link TextArea} con all'interno il risultato
     */
    private Component createResult(String value) {
        TextArea textArea = new TextArea();
        textArea.setId(RESULT);
        textArea.setValue(value);
        textArea.setLabel("Risultato");
        textArea.setReadOnly(true);
        textArea.setSizeFull();
        return textArea;
    }

    /**
     * Metodo che permette di recuperare un componente attraverso un id
     * @param root il componente di partenza da cui effettuare la ricerca
     * @param id che identifica il componente
     * @param <T> il componente iniziale deve estendere {@link HasOrderedComponents}
     * @return un {@link Optional} che potrebbe o no contenere il risultato
     */
    private <T extends HasOrderedComponents> Optional<Component> findComponentWithId(T root, String id) {
        for (int i = 0; i < root.getComponentCount(); i++) {
            Component child = root.getComponentAt(i);
            if (child.getId().orElse("").equals(id)) {
                return Optional.of(child);
            } else if (child instanceof HasOrderedComponents) {
                Optional<Component> optionalComponent = findComponentWithId((HasOrderedComponents) child, id);
                if (optionalComponent.isPresent()) {
                    return optionalComponent;
                }
            }
        }

        return Optional.empty();
    }
}
