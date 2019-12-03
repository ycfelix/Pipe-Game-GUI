package views.panes;

import controllers.SceneManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import models.Config;
import models.FXGame;
import models.FlowTimer;
import views.*;

import java.util.Arrays;

/**
 * Pane for the Level Editor.
 */
public class LevelEditorPane extends GamePane {

    private final LevelEditorCanvas levelEditor = new LevelEditorCanvas(FXGame.getDefaultRows(), FXGame.getDefaultCols(), FlowTimer.getDefaultDelay());
    private final VBox leftContainer = new SideMenuVBox();

    private final Button returnButton = new BigButton("Return");

    private Label rowText = new Label("Rows");
    private NumberTextField rowField = new NumberTextField(String.valueOf(levelEditor.getNumOfRows()));
    private BorderPane rowBox = new BorderPane(null, null, rowField, null, rowText);

    private Label colText = new Label("Columns");
    private NumberTextField colField = new NumberTextField(String.valueOf(levelEditor.getNumOfCols()));
    private BorderPane colBox = new BorderPane(null, null, colField, null, colText);

    private Button newGridButton = new BigButton("New Grid");

    private Label delayText = new Label("Delay");
    private NumberTextField delayField = new NumberTextField(String.valueOf(levelEditor.getAmountOfDelay()));
    private BorderPane delayBox = new BorderPane(null, null, delayField, null, delayText);

    private ObservableList<LevelEditorCanvas.CellSelection> cellList = FXCollections.observableList(Arrays.asList(LevelEditorCanvas.CellSelection.values()));
    private ListView<LevelEditorCanvas.CellSelection> selectedCell = new ListView<>();

    private Button toggleRotationButton = new BigButton("Toggle Source Rotation");
    private Button loadButton = new BigButton("Load");
    private Button saveButton = new BigButton("Save As");

    private VBox centerContainer = new BigVBox();

    public LevelEditorPane() {
        connectComponents();
        styleComponents();
        setCallbacks();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    void connectComponents() {
        // TODO
        this.leftContainer.getChildren().addAll(returnButton,rowBox
                ,colBox,newGridButton,delayBox,selectedCell,
                toggleRotationButton,loadButton,saveButton);
        this.centerContainer.getChildren().add(this.levelEditor);
        this.selectedCell.setItems(this.cellList);
        this.selectedCell.getSelectionModel().select(0);
        this.setLeft(this.returnButton);
        this.setCenter(this.centerContainer);
    }

    /**
     * {@inheritDoc}
     *
     * {@link LevelEditorPane#selectedCell} should have cell heights of {@link Config#LIST_CELL_HEIGHT}.
     */
    @Override
    void styleComponents() {
        // TODO
        this.selectedCell.setFixedCellSize(Config.LIST_CELL_HEIGHT);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    void setCallbacks() {
        // TODO
        this.returnButton.setOnAction(e->
                SceneManager.getInstance().showPane(MainMenuPane.class)
        );
        this.newGridButton.setOnAction(e->
                this.levelEditor.changeAttributes(
                        rowField.getValue(),colField.getValue(),delayField.getValue())
        );
        this.delayField.setOnAction(e->this.levelEditor.setAmountOfDelay(delayField.getValue()));

        this.toggleRotationButton.setOnAction(e->this.levelEditor.toggleSourceTileRotation());
        this.loadButton.setOnAction(e->
        {
            if(levelEditor.loadFromFile()){
                this.rowField.setText(String.valueOf(this.levelEditor.getNumOfRows()));
                this.colField.setText(String.valueOf(this.levelEditor.getNumOfCols()));
                this.delayField.setText(String.valueOf(this.levelEditor.getAmountOfDelay()));
            }
        });
        this.saveButton.setOnAction(e->
                this.levelEditor.saveToFile()
        );
        this.levelEditor.setOnMouseClicked(e->{
            this.levelEditor.setTile(
                    this.selectedCell.getSelectionModel().getSelectedItem(),e.getX(),e.getY());
        });
    }
}
