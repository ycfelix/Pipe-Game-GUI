package controllers;
//done
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Singleton class responsible for loading and managing levels from the filesystem.
 */
public class LevelManager {

    /**
     * Singleton instance.
     */
    @NotNull
    private static final LevelManager INSTANCE = new LevelManager();

    /**
     * List of all loaded level names.
     */
    @NotNull
    private final ObservableList<String> levelNames = FXCollections.observableArrayList();
    /**
     * The JavaFX property housing the current level name.
     * <p>
     * The {@link String} backing the property may be {@code null} if no level is loaded from the filesystem.
     */
    @NotNull
    private final StringProperty curLevelNameProperty = new SimpleStringProperty();

    /**
     * Path to the map directory. Defaults to the current working directory.
     */
    @NotNull
    private Path mapDirectory = Paths.get("");

    private LevelManager() {
        setMapDirectory(mapDirectory);
    }

    /**
     * @return Singleton instance of this class.
     */
    @NotNull
    public static LevelManager getInstance() {
        return INSTANCE;
    }

    /**
     * Sets the current map directory, and loads all maps from the newly set directory.
     *
     * @param mapDirectory New map directory to load maps from.
     */
    public void setMapDirectory(@NotNull Path mapDirectory) {
        // TODO
        this.mapDirectory=mapDirectory;
        this.loadLevelNamesFromDisk();
    }

    /**
     * Loads all level names from the currently set {@link LevelManager#mapDirectory}.
     *
     * <p>
     * Hint:
     * <ul>
     * <li>Use try-with-resources to automatically manage the lifetime of {@link Stream}.</li>
     * <li>Use {@link Files#walk(Path, int, FileVisitOption...)} to retrieve all files inside a given directory. Check
     * the documentation to see what to put for the second parameter!</li>
     * <li>Use {@link Stream} to filter, map and sort the loaded filenames.</li>
     * </ul>
     * </p>
     */
    private void loadLevelNamesFromDisk() {
        // TODO
        try(Stream<Path> f=Files.walk(this.mapDirectory,1)){
            Stream<Path> files=f.filter(e-> e.toFile().isFile());
            if(files!=null){
                List<String> maps=files.map(e->e.getFileName().toString()).
                        filter(e->e.endsWith(".map")).
                        sorted(String::compareTo).
                        collect(Collectors.toList());
                this.levelNames.clear();
                this.levelNames.addAll(maps);
            }
        }
        catch (Exception e){
            Alert a=new Alert(Alert.AlertType.WARNING);
            a.setTitle("Warning");
            a.setHeaderText("cannot load level names from disk");
            a.showAndWait();
        }
    }

    @NotNull
    public ObservableList<String> getLevelNames() {
        return levelNames;
    }

    /**
     * @return Full path to the currently selected level.
     */
    @NotNull
    public Path getCurrentLevelPath() {
        // TODO
        Path p=this.mapDirectory.resolve(curLevelNameProperty.get());
        if(p!=null){
            return p.normalize();
        }
        return null;
    }

    /**
     * Sets the currently selected level.
     *
     * @param levelName Name of the newly selected level, or {@code null} if a level is not loaded.
     * @throws IllegalArgumentException When the level name is blank.
     */
    public void setLevel(@Nullable String levelName) {
        // TODO
        if(levelName!=null){
            if(levelName.isBlank()){
                throw new IllegalArgumentException("empty level name!");
            }
        }
        this.curLevelNameProperty.set(levelName);

    }

    /**
     * Retrieves and sets the next level.
     * <p>
     * If there is a "next map" in the directory, set the level to that map. Otherwise, set the level to {@code null}.
     * </p>
     * <p>
     * Hint:
     * <ul>
     * <li>Remember to check whether the string in {@code curLevelNameProperty} is empty or not.</li>
     * </ul>
     * </p>
     *
     * @return Name of the next map if present, {@code null} otherwise.
     */
    @Nullable
    public String getAndSetNextLevel() {
        // TODO
        if(this.curLevelNameProperty.get()==null){
            return null;
        }
        int idx=this.levelNames.indexOf(this.curLevelNameProperty.get());
        if(idx!=-1&&idx<this.levelNames.size()-1){
            String s=this.levelNames.get(idx+1);
            this.setLevel(s);
            return this.curLevelNameProperty.get();
        }
        return null;
    }

    @NotNull
    public StringProperty getCurrentLevelProperty() {
        return curLevelNameProperty;
    }
}
