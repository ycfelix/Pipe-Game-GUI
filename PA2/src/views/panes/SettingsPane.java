package views.panes;

import controllers.AudioManager;
import controllers.SceneManager;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import models.Config;
import models.FXGame;
import models.FlowTimer;
import org.jetbrains.annotations.NotNull;
import views.BigButton;
import views.BigVBox;
import views.NumberTextField;
import views.SideMenuVBox;

import java.util.Optional;

public class SettingsPane extends GamePane {

    @NotNull
    private static final String MSG_BAD_ROW_NUM = "Row number should be at least 2!";
    @NotNull
    private static final String MSG_BAD_COL_NUM = "Column number should be at least 2!";
    @NotNull
    private static final String MSG_BAD_DELAY_NUM = "Delay value should be a positive integer!";
    @NotNull
    private static final String MSG_BAD_FLOW_NUM = "Flow rate should be a positive integer!";

    @NotNull
    private final VBox leftContainer = new SideMenuVBox();
    @NotNull
    private final Button saveButton = new BigButton("Save");
    @NotNull
    private final Button returnButton = new BigButton("Return");
    @NotNull
    private final Button toggleSoundButton = new BigButton("Sound FX: Enabled");
    /**
     * Text field for modifying the number of rows for generated maps.
     *
     * @see FXGame#getDefaultRows()
     * @see FXGame#setDefaultRows(int)
     */
    @NotNull
    private final NumberTextField rowsField = new NumberTextField(String.valueOf(FXGame.getDefaultRows()));
    @NotNull
    private final BorderPane rowBox = new BorderPane(null, null, rowsField, null, new Label("Default Rows"));
    /**
     * Text field for modifying the number of columns for generated maps.
     *
     * @see FXGame#getDefaultCols()
     * @see FXGame#setDefaultCols(int)
     */
    @NotNull
    private final NumberTextField colsField = new NumberTextField(String.valueOf(FXGame.getDefaultCols()));
    @NotNull
    private final BorderPane colBox = new BorderPane(null, null, colsField, null, new Label("Default Columns"));
    /**
     * Text field for modifying the default flow delay for generated maps.
     *
     * @see FlowTimer#getDefaultDelay()
     * @see FlowTimer#setDefaultDelay(int)
     */
    @NotNull
    private final NumberTextField delayField = new NumberTextField(String.valueOf(FlowTimer.getDefaultDelay()));
    @NotNull
    private final BorderPane delayBox = new BorderPane(null, null, delayField, null, new Label("Default Delay"));
    /**
     * Text field for modifying the default flow rate.
     *
     * @see FlowTimer#getDefaultFlowDuration()
     * @see FlowTimer#setDefaultFlowDuration(int)
     */
    @NotNull
    private final NumberTextField flowField = new NumberTextField(String.valueOf(FlowTimer.getDefaultFlowDuration()));
    @NotNull
    private final BorderPane flowBox = new BorderPane(null, null, flowField, null, new Label("Flow Rate (s)"));
    @NotNull
    private final VBox centerContainer = new BigVBox();
    @NotNull
    private final TextArea infoText = new TextArea(Config.getAboutText());

    public SettingsPane() {
        connectComponents();
        styleComponents();
        setCallbacks();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void connectComponents() {
        // TODO
        this.leftContainer.getChildren().addAll(returnButton,
                saveButton,rowBox,colBox,delayBox,flowBox,toggleSoundButton);
        this.centerContainer.getChildren().add(infoText);
        this.setLeft(this.leftContainer);
        this.setCenter(this.centerContainer);
    }

    /**
     * {@inheritDoc}
     *
     * In particular, the text box should be not editable, text should be wrapped around, "text-area" style should
     * be applied, and preferred height should be {@link Config#HEIGHT}.
     */
    @Override
    void styleComponents() {
        // TODO
        this.infoText.getStyleClass().add("text-area");
        this.infoText.setEditable(false);
        this.infoText.setWrapText(true);
        this.infoText.setPrefHeight(Config.HEIGHT);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    void setCallbacks() {
        // TODO
        this.saveButton.setOnAction(e-> {
            this.validate().ifPresentOrElse(f->{
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("warning");
                alert.setHeaderText("validate error");
                alert.setContentText(f);
                alert.showAndWait();
            },()->this.returnToMainMenu(true));
        });
        this.returnButton.setOnAction(e->this.returnToMainMenu(false));
        this.toggleSoundButton.setOnAction(e->{
            AudioManager manager=AudioManager.getInstance();
            manager.setEnabled(!manager.isEnabled());
            fillValues();
        });
    }

    /**
     * Fill in the default values for all editable fields.
     */
    private void fillValues() {
        // TODO
        this.toggleSoundButton.setText("FX Sound:"+(AudioManager.getInstance().isEnabled()?"enabled":"disabled"));
        this.rowsField.clear();
        this.colsField.clear();
        this.delayField.clear();
        this.flowField.clear();
        this.rowsField.replaceSelection(String.valueOf(FXGame.getDefaultRows()));
        this.colsField.replaceSelection(String.valueOf(FXGame.getDefaultCols()));
        this.delayField.replaceSelection(String.valueOf(FlowTimer.getDefaultDelay()));
        this.flowField.replaceSelection(String.valueOf(FlowTimer.getDefaultFlowDuration()));
    }

    /**
     * Switches back to the {@link MainMenuPane}.
     *
     * @param writeback Whether to save the values present in the text fields to their respective classes.
     */
    private void returnToMainMenu(final boolean writeback) {
        // TODO
        if(writeback){
            FXGame.setDefaultCols(colsField.getValue());
            FXGame.setDefaultRows(rowsField.getValue());
            FlowTimer.setDefaultDelay(delayField.getValue());
            FlowTimer.setDefaultFlowDuration(flowField.getValue());
        }
        fillValues();
        SceneManager.getInstance().showPane(MainMenuPane.class);
    }

    /**
     * Validates on all the input {@link javafx.scene.control.TextField}.
     *
     * <p>
     * There are three things to check in this method:
     * <ul>
     * <li>Whether the value of {@link SettingsPane#delayField} is a positive integer</li>
     * <li>Whether the value of {@link SettingsPane#rowsField} is bigger than 1</li>
     * <li>Whether the value of {@link SettingsPane#colsField} is bigger than 1</li>
     * </ul>
     * </p>
     *
     * @return If validation failed, {@link Optional} containing the reason message; An empty {@link Optional}
     * otherwise.
     */
    @NotNull
    private Optional<String> validate() {
        // TODO
        Optional<String> result=Optional.empty();
        if(this.delayField.getValue()<=0){
            result=Optional.of(MSG_BAD_DELAY_NUM);
            return result;
        }
        else if(this.rowsField.getValue()<=1){
            result=Optional.of(MSG_BAD_ROW_NUM);
            return result;
        }
        else if(this.colsField.getValue()<=1){
            result=Optional.of(MSG_BAD_COL_NUM);
            return result;
        }
        else if(this.flowField.getValue()<=0){
            result=Optional.of(MSG_BAD_FLOW_NUM);
            return result;
        }
        return result;
    }
}
