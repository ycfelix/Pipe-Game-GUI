package views.panes;

import controllers.LevelManager;
import controllers.SceneManager;
import io.Deserializer;
import javafx.beans.value.ObservableValue;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.ListView;
import javafx.stage.DirectoryChooser;
import models.FXGame;
import views.BigButton;
import views.BigVBox;
import views.SideMenuVBox;

import java.io.File;
import java.io.FileNotFoundException;

public class LevelSelectPane extends GamePane {

    private SideMenuVBox leftContainer = new SideMenuVBox();
    private BigButton returnButton = new BigButton("Return");
    private BigButton playButton = new BigButton("Play");
    private BigButton playRandom = new BigButton("Generate Map and Play");
    private BigButton chooseMapDirButton = new BigButton("Choose map directory");
    private ListView<String> levelsListView = new ListView<>(LevelManager.getInstance().getLevelNames());
    private BigVBox centerContainer = new BigVBox();
    private Canvas levelPreview = new Canvas();

    public LevelSelectPane() {
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
        this.leftContainer.getChildren().addAll(returnButton
                ,chooseMapDirButton,levelsListView,playButton,playRandom);
        this.centerContainer.getChildren().add(this.levelPreview);
        this.setLeft(this.leftContainer);
        this.setCenter(this.centerContainer);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    void styleComponents() {
        // TODO
        this.playButton.setDisable(true);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    void setCallbacks() {
        // TODO
        this.returnButton.setOnAction(e->SceneManager.getInstance().showPane(MainMenuPane.class));
        this.chooseMapDirButton.setOnAction(e->promptUserForMapDirectory());
        this.playButton.setOnAction(e->startGame(false));
        this.playRandom.setOnAction(e->startGame(true));
        this.levelsListView.getSelectionModel().selectedItemProperty().addListener(e->this.connectComponents());
    }

    /**
     * Starts the game.
     *
     * <p>
     * This method should do everything that is required to initialize and start the game, including loading/generating
     * maps, switching scenes, etc.
     * </p>
     *
     * @param generateRandom Whether to use a generated map.
     */
    private void startGame(final boolean generateRandom) {
        // TODO
        GameplayPane p=SceneManager.getInstance().getPane(GameplayPane.class);
        if(generateRandom){
            LevelManager.getInstance().setLevel(null);
            p.startGame(new FXGame());
            SceneManager.getInstance().showPane(GameplayPane.class);
        }
        else{
            try{
                LevelManager.getInstance().setLevel(this.levelsListView.getSelectionModel().getSelectedItem());
                FXGame game= new Deserializer(LevelManager.getInstance().getCurrentLevelPath()).parseFXGame();
                if(game!=null){
                    p.startGame(game);
                    SceneManager.getInstance().showPane(GameplayPane.class);
                }
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    /**
     * Listener method that executes when a map on the list is selected.
     *
     * @param observable Observable value.
     * @param oldValue   Original value.
     * @param newValue   New value.
     */
    private void onMapSelected(ObservableValue<? extends String> observable, String oldValue, String newValue) {
        // TODO
        if(newValue!=null && this.levelsListView.getItems().stream().anyMatch(e->e.equals(newValue))){
            LevelManager.getInstance().setLevel(newValue);
            try{
                FXGame game= new Deserializer(LevelManager.getInstance().getCurrentLevelPath()).parseFXGame();
                if(game!=null){
                    game.renderMap(this.levelPreview);
                }
            }catch (Exception e){
            }
        }
        else{
            this.levelPreview.setWidth(0);
            this.levelPreview.setHeight(0);
        }
    }

    /**
     * Prompts the user for a map directory.
     *
     * <p>
     * Hint:
     * Use {@link DirectoryChooser} to display a folder selection prompt.
     * </p>
     */
    private void promptUserForMapDirectory() {
        // TODO
        DirectoryChooser chooser=new DirectoryChooser();
        File file=chooser.showDialog(null);
        if(file!=null){
            this.commitMapDirectoryChange(file);
        }
    }

    /**
     * Actually changes the current map directory.
     *
     * @param dir New directory to change to.
     */
    private void commitMapDirectoryChange(File dir) {
        // TODO
        this.levelsListView.getSelectionModel().clearSelection();
        LevelManager.getInstance().setMapDirectory(dir.toPath());
    }
}
