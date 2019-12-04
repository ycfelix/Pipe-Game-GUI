package views.panes;

import controllers.AudioManager;
import controllers.LevelManager;
import controllers.SceneManager;
import io.Deserializer;
import javafx.application.Platform;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import models.FXGame;
import org.jetbrains.annotations.NotNull;
import views.BigButton;
import views.BigVBox;
import views.GameplayInfoPane;
import javafx.scene.input.KeyCode;
import java.io.FileNotFoundException;
import java.util.Optional;

import static models.Config.TILE_SIZE;

/**
 * Pane for displaying the actual gameplay.
 */
public class GameplayPane extends GamePane {

    private HBox topBar = new HBox(20);
    private VBox canvasContainer = new BigVBox();
    private Canvas gameplayCanvas = new Canvas();
    private HBox bottomBar = new HBox(20);
    private Canvas queueCanvas = new Canvas();
    private Button quitToMenuButton = new BigButton("Quit to menu");

    private FXGame game;

    private final IntegerProperty ticksElapsed = new SimpleIntegerProperty();
    private GameplayInfoPane infoPane = null;

    public GameplayPane() {
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
        this.canvasContainer.getChildren().addAll(this.gameplayCanvas);
        this.bottomBar.getChildren().addAll(this.queueCanvas,this.quitToMenuButton);
        this.setCenter(this.canvasContainer);
        this.setTop(this.topBar);
        this.setBottom(bottomBar);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    void styleComponents() {
        // TODO
        this.bottomBar.getStyleClass().add("bottom-menu");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    void setCallbacks() {
        // TODO
        this.gameplayCanvas.setOnMouseClicked(this::onCanvasClicked);
        this.quitToMenuButton.setOnAction(event -> GameplayPane.this.doQuitToMenuAction());
        this.setOnKeyPressed(this::onKeyPressed);
    }

    /**
     * Handles events when somewhere on the {@link GameplayPane#gameplayCanvas} is clicked.
     *
     * @param event Event to handle.
     */
    private void onCanvasClicked(MouseEvent event) {
        // TODO
        boolean playing=!this.game.hasWon()&&!this.game.hasLost();
        if(playing){
            this.game.placePipe((int)event.getY()/TILE_SIZE,(int)event.getX()/TILE_SIZE);
            if(this.game.hasWon()){

                AudioManager.getInstance().playSound(AudioManager.SoundRes.WIN);
                Platform.runLater(this::createWinPopup);
            }
            else{
                AudioManager.getInstance().playSound(AudioManager.SoundRes.MOVE);
            }
            this.game.renderMap(this.gameplayCanvas);
            this.game.renderQueue(this.queueCanvas);
        }
    }

    /**
     * Handles events when a key is pressed.
     *
     * @param event Event to handle.
     */
    private void onKeyPressed(KeyEvent event) {
        // TODO
        boolean playing=!this.game.hasWon()&&!this.game.hasLost();
        if(playing){
            if(event.getCode()==KeyCode.U){
                this.game.undoStep();
                this.game.renderMap(this.gameplayCanvas);
                this.game.renderQueue(this.queueCanvas);
            }
            else if(event.getCode()==KeyCode.S){
                this.game.skipPipe();
                this.game.renderQueue(this.queueCanvas);
            }
        }
    }

    /**
     * Creates a popup which tells the player they have completed the map.
     */
    private void createWinPopup() {
        // TODO
        Alert a=new Alert(Alert.AlertType.CONFIRMATION);
        a.setTitle("level clear popup");
        a.getButtonTypes().addAll(new ButtonType("Next Map"),new ButtonType("Return"));
        Optional<ButtonType> c=a.showAndWait();
        String cmd=c.orElseThrow().getText();
        if(cmd.equals("Next Map")){
            this.loadNextMap();
        }
        if(cmd.equals("Return")){
            this.doQuitToMenu();
        }

    }

    /**
     * Loads the next map in the series, or generate a new map if one is not available.
     */
    private void loadNextMap() {
        // TODO
        String s=LevelManager.getInstance().getAndSetNextLevel();
        FXGame g=null;
        System.out.println(s);
        if(s!=null){
            try{
                g=(new Deserializer(LevelManager.getInstance().getCurrentLevelPath())).parseFXGame();
            }
            catch (Exception e){
                e.printStackTrace();
                g=null;
            }
        }
        if(g==null){
            g=new FXGame();
        }
        this.startGame(g);
    }

    /**
     * Creates a popup which tells the player they have lost the map.
     */
    private void createLosePopup() {
        // TODO
        Alert a = new Alert(Alert.AlertType.CONFIRMATION);
        a.setTitle("Lose popup");
        a.setHeaderText("you lose");
        ButtonType lose = new ButtonType("Return");
        a.getButtonTypes().setAll(lose);
        Optional<ButtonType> c=a.showAndWait();
        String cmd=c.orElseThrow().getText();
        if(cmd.equals("Return")){
            this.doQuitToMenu();
        }
    }

    /**
     * Creates a popup which prompts the player whether they want to quit.
     */
    private void doQuitToMenuAction() {
        // TODO
        Alert a = new Alert(Alert.AlertType.CONFIRMATION);
        a.setTitle("quit to menu popup");
        a.setHeaderText("Quit to menu?");
        a.setContentText("current progress may not be saved");
        a.getButtonTypes().setAll(ButtonType.CANCEL, ButtonType.OK);
        a.showAndWait();
        ButtonType cmd=a.getResult();
        if(cmd.equals(ButtonType.OK)) {
            this.doQuitToMenu();
        }
    }

    /**
     * Go back to the Level Select scene.
     */
    private void doQuitToMenu() {
        // TODO
        this.endGame();
        SceneManager.getInstance().showPane(LevelSelectPane.class);
    }

    /**
     * Starts a new game with the given name.
     *
     * @param game New game to start.
     */
    void startGame(@NotNull FXGame game) {
        // TODO
        if(this.game!=null){
            this.endGame();
        }
        this.game=game;
        this.infoPane=new GameplayInfoPane(
                LevelManager.getInstance().getCurrentLevelProperty(),
                this.ticksElapsed,
                game.getNumOfSteps(),
                game.getNumOfUndo()
        );
        this.topBar.getChildren().add(this.infoPane);
        HBox.setHgrow(this.infoPane,Priority.ALWAYS);
        game.addOnTickHandler(()->{
            Platform.runLater(()->{
                GameplayPane.this.ticksElapsed.set(GameplayPane.this.ticksElapsed.get()+1);
            });
        });
        game.addOnFlowHandler(()->{
            game.updateState();
            game.renderMap(GameplayPane.this.gameplayCanvas);
            if(game.hasLost()){
                AudioManager.getInstance().playSound(AudioManager.SoundRes.LOSE);
                Platform.runLater(this::createLosePopup);
            }
        });
        game.renderMap(this.gameplayCanvas);
        game.renderQueue(this.queueCanvas);
        game.startCountdown();
    }

    /**
     * Cleans up the currently bound game.
     */
    private void endGame() {
        // TODO
        this.game.stopCountdown();
        this.gameplayCanvas.setWidth(0);
        this.gameplayCanvas.setHeight(0);
        this.queueCanvas.setWidth(0);
        this.queueCanvas.setHeight(0);
        this.ticksElapsed.set(0);
        this.topBar.getChildren().remove(this.infoPane);
        this.infoPane=null;
        this.game=null;
    }
}
