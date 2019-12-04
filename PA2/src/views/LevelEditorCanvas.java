package views;

import controllers.Renderer;
import io.Deserializer;
import io.GameProperties;
import io.Serializer;
import javafx.application.Platform;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.FileChooser;
import models.exceptions.InvalidMapException;
import models.map.cells.Cell;
import models.map.cells.FillableCell;
import models.map.cells.TerminationCell;
import models.map.cells.Wall;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import util.Coordinate;
import util.Direction;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Optional;

import static models.Config.TILE_SIZE;

public class LevelEditorCanvas extends Canvas {

    private static final String MSG_MISSING_SOURCE = "Source tile is missing!";
    private static final String MSG_MISSING_SINK = "Sink tile is missing!";
    private static final String MSG_BAD_DIMS = "Map size must be at least 2x2!";
    private static final String MSG_BAD_DELAY = "Delay must be a positive value!";
    private static final String MSG_SOURCE_TO_WALL = "Source tile is blocked by a wall!";
    private static final String MSG_SINK_TO_WALL = "Sink tile is blocked by a wall!";

    private GameProperties gameProp;

    @Nullable
    private TerminationCell sourceCell;
    @Nullable
    private TerminationCell sinkCell;

    public LevelEditorCanvas(int rows, int cols, int delay) {
        super();

        resetMap(rows, cols, delay);
    }

    /**
     * Changes the attributes of this canvas.
     *
     * @param rows  Number of rows.
     * @param cols  Number of columns.
     * @param delay Amount of delay.
     */
    public void changeAttributes(int rows, int cols, int delay) {
        resetMap(rows, cols, delay);
    }

    /**
     * Resets the map with the given attributes.
     *
     * @param rows  Number of rows.
     * @param cols  Number of columns.
     * @param delay Amount of delay.
     */
    private void resetMap(int rows, int cols, int delay) {
        // TODO
        this.gameProp=new GameProperties(rows,cols);
        this.gameProp.delay=delay;

        for(int i=0;i<rows;i++){
            for(int j=0;j<cols;j++){
                Coordinate cor=new Coordinate(i,j);
                if(i!=0&&i!=rows-1&&j!=0&&j!=cols-1){
                    this.gameProp.cells[i][j]=new FillableCell(cor);
                }
                else{
                    this.gameProp.cells[i][j]=new Wall(cor);
                }
            }
        }
        this.renderCanvas();
    }

    /**
     * Renders the canvas.
     */
    private void renderCanvas() {
        Platform.runLater(() -> Renderer.renderMap(this, gameProp.cells));
    }

    /**
     * Sets a tile on the map.
     * <p>
     * Hint:
     * You may need to check/compute some attribute in order to create the new {@link Cell} object.
     *
     * @param sel Selected {@link CellSelection}.
     * @param x   X-coordinate relative to the canvas.
     * @param y   Y-coordinate relative to the canvas.
     */
    public void setTile(@NotNull CellSelection sel, double x, double y) {
        // TODO
        int row=(int)y/32;
        int col=(int)x/32;
        if(row!=0&&row!=this.gameProp.rows-1||col!=0&&col!=this.gameProp.cols-1){
            Coordinate cor=new Coordinate(row,col);
            TerminationCell.Type endCell=row!=0&&row!=this.gameProp.rows-1&&
                    col!=0&&col!=this.gameProp.cols-1?
                    TerminationCell.Type.SOURCE: TerminationCell.Type.SINK;

            Direction dir;
            if(endCell==TerminationCell.Type.SINK){
                if(row==0){
                    dir=Direction.UP;
                }
                else if(row==this.gameProp.rows-1){
                    dir=Direction.DOWN;
                }
                else if(col==0){
                    dir=Direction.LEFT;
                }
                else if(col==this.gameProp.cols-1){
                    dir=Direction.RIGHT;
                }
                else{
                    throw new IllegalStateException("terminate cell not at wall");
                }
            }
            else{
                dir=this.sourceCell!=null?this.sourceCell.pointingTo:Direction.UP;
            }

            Cell cell= switch (sel){
                case WALL->new Wall(cor);
                case CELL->new FillableCell(cor);
                case TERMINATION_CELL->new TerminationCell(cor,dir,endCell);
                default->throw new IllegalArgumentException("wrong cell type");
            };
            this.setTileByMapCoord(cell);
        }
    }

    /**
     * Sets a tile on the map.
     * <p>
     * Hint:
     * You will need to make sure that there is only one source/sink cells in the map.
     *
     * @param cell The {@link Cell} object to set.
     */
    private void setTileByMapCoord(@NotNull Cell cell) {
        // TODO
        Coordinate cor=cell.coord;
        TerminationCell terminationCell;
        if(this.gameProp.cells[cor.row][cor.col] instanceof TerminationCell){
            terminationCell= (TerminationCell) this.gameProp.cells[cor.row][cor.col];
            if(terminationCell.type==TerminationCell.Type.SOURCE){
                this.sourceCell=null;
            }
            else {
                this.sinkCell=null;
            }
        }
        if(cell instanceof TerminationCell){
            terminationCell= (TerminationCell) cell;
            if(terminationCell.type==TerminationCell.Type.SOURCE){
                this.sourceCell=terminationCell;
            }
            else {
                this.sinkCell=terminationCell;
            }
        }
        this.gameProp.cells[cor.row][cor.col]=cell;
        this.renderCanvas();

    }

    /**
     * Toggles the rotation of the source tile clockwise.
     */
    public void toggleSourceTileRotation() {
        // TODO
        if (this.sourceCell != null) {
            Coordinate cor=this.sourceCell.coord;
            Direction dir = this.sourceCell.pointingTo.rotateCW();
            TerminationCell t=new TerminationCell(this.sourceCell.coord,dir,this.sourceCell.type);
            this.gameProp.cells[cor.row][cor.col]=t;
            this.sourceCell = t;
            this.renderCanvas();
        }
    }

    /**
     * Loads a map from a file.
     * <p>
     * Prompts the player if they want to discard the changes, displays the file chooser prompt, and loads the file.
     *
     * @return {@code true} if the file is loaded successfully.
     */
    public boolean loadFromFile() {
        // TODO
        Alert a = new Alert(Alert.AlertType.CONFIRMATION);
        a.setTitle("Confirm");
        a.setHeaderText("Load a map from file?");
        a.setContentText("Current map contents will be lost.");
        a.getButtonTypes().setAll(ButtonType.CANCEL, ButtonType.OK);
        a.showAndWait();
        if ((a.getResult()).equals(ButtonType.CANCEL)) {
            return false;
        }
        File f = this.getTargetLoadFile();
        return f != null ? this.loadFromFile(f.toPath()) : false;
    }

    /**
     * Prompts the user for the file to load.
     * <p>
     * Hint:
     * Use {@link FileChooser} and {@link FileChooser#setSelectedExtensionFilter(FileChooser.ExtensionFilter)}.
     *
     * @return {@link File} to load, or {@code null} if the operation is canceled.
     */
    @Nullable
    private File getTargetLoadFile() {
        // TODO
        FileChooser chooser = new FileChooser();
        chooser.setSelectedExtensionFilter(new FileChooser.ExtensionFilter("Map", Collections.singletonList("*.map")));
        return chooser.showOpenDialog(null);
    }

    /**
     * Loads the file from the given path and replaces the current {@link LevelEditorCanvas#gameProp}.
     * <p>
     * Hint:
     * You should handle any exceptions which arise from loading in this method.
     *
     * @param path Path to load the file from.
     * @return {@code true} if the file is loaded successfully, {@code false} otherwise.
     */
    private boolean loadFromFile(@NotNull Path path) {
        // TODO
        this.sourceCell=null;
        this.sinkCell=null;
        try{
            this.gameProp=new Deserializer(path).parseGameFile();
            for(int i=0;i<this.gameProp.rows;i++){
                for(int j=0;j<this.gameProp.cols;j++){
                    Cell cell=this.gameProp.cells[i][j];
                    Coordinate cor=cell.coord;
                    if(cell instanceof TerminationCell){
                        if(i!=0&&i!=cor.row-1&&j!=0&&j!=cor.col-1){
                            if(this.sourceCell!=null){
                                throw new InvalidMapException("too many source");
                            }
                            this.sourceCell= (TerminationCell) cell;
                        }else{
                            if(this.sinkCell!=null){
                                throw new InvalidMapException("too many sink");
                            }
                            this.sinkCell=(TerminationCell) cell;
                        }
                    }
                }
            }
            this.renderCanvas();
            return true;
        }
        catch (Exception e){
            Alert a = new Alert(Alert.AlertType.ERROR);
            a.setTitle("Error");
            a.setHeaderText("can't load map");
            a.setContentText(e.getMessage());
            a.showAndWait();
            return false;
        }
    }

    /**
     * Checks the validity of the map, prompts the player for the target save directory, and saves the file.
     */
    public void saveToFile() {
        // TODO
        Optional valid = this.checkValidity();
        if (valid != null&&valid.isPresent()) {
            Alert a = new Alert(Alert.AlertType.ERROR);
            a.setTitle("Error");
            a.setHeaderText("fail checking validity");
            a.setContentText((String)valid.get());
            a.showAndWait();
        }
        else {
            File f = this.getTargetSaveDirectory();
            if (f != null) {
                this.exportToFile(f.toPath());
            }
        }
    }

    /**
     * Prompts the user for the directory and filename to save as.
     * <p>
     * Hint:
     * Use {@link FileChooser} and {@link FileChooser#setSelectedExtensionFilter(FileChooser.ExtensionFilter)}.
     *
     * @return {@link File} to save to, or {@code null} if the operation is canceled.
     */
    @Nullable
    private File getTargetSaveDirectory() {
        // TODO
        FileChooser chooser = new FileChooser();
        chooser.setSelectedExtensionFilter(new FileChooser.ExtensionFilter("Map", Collections.singletonList("*.map")));
        return chooser.showSaveDialog(null);
    }

    /**
     * Exports the current map to a file.
     * <p>
     * Hint:
     * You should handle any exceptions which arise from saving in this method.
     *
     * @param p Path to export to.
     */
    private void exportToFile(@NotNull Path p) {
        // TODO
        Serializer serializer=new Serializer(p);
        try{
            serializer.serializeGameProp(this.gameProp);
        }catch (Exception e){
            Alert a = new Alert(Alert.AlertType.ERROR);
            a.setTitle("Error");
            a.setHeaderText("fail save to file");
            a.setContentText(e.getMessage());
            a.showAndWait();
        }
    }

    /**
     * Checks whether the current map and its properties are valid.
     * <p>
     * Hint:
     * You should check for the following conditions:
     * <ul>
     * <li>Source cell is present</li>
     * <li>Sink cell is present</li>
     * <li>Minimum map size is 2x2</li>
     * <li>Flow delay is at least 1</li>
     * <li>Source/Sink tiles are not blocked by walls</li>
     * </ul>
     *
     * @return {@link Optional} containing the error message, or an empty {@link Optional} if the map is valid.
     */
    private Optional<String> checkValidity() {
        // TODO
        if(this.sourceCell==null){
            return Optional.of(MSG_MISSING_SOURCE);
        }
        if(this.sinkCell==null){
            return Optional.of(MSG_MISSING_SINK);
        }
        if(this.gameProp.rows>=2 && this.gameProp.cols>=2){
            if(this.gameProp.delay<=0){
                return Optional.of(MSG_BAD_DELAY);
            }
            else{
                Coordinate source=this.sourceCell.coord.add(this.sourceCell.pointingTo.getOffset());
                Coordinate sink=this.sinkCell.coord.add(this.sinkCell.pointingTo.getOpposite().getOffset());
                if(this.gameProp.cells[source.row][source.col] instanceof Wall){
                    return Optional.of(MSG_SOURCE_TO_WALL);
                }
                if(this.gameProp.cells[sink.row][sink.col] instanceof Wall){
                    return Optional.of(MSG_SOURCE_TO_WALL);
                }
                return Optional.empty();
            }
        }
        return Optional.of(MSG_BAD_DIMS);
    }

    public int getNumOfRows() {
        return gameProp.rows;
    }

    public int getNumOfCols() {
        return gameProp.cols;
    }

    public int getAmountOfDelay() {
        return gameProp.delay;
    }

    public void setAmountOfDelay(int delay) {
        gameProp.delay = delay;
    }

    public enum CellSelection {
        WALL("Wall"),
        CELL("Cell"),
        TERMINATION_CELL("Source/Sink");

        private String text;

        CellSelection(@NotNull String text) {
            this.text = text;
        }

        @Override
        public String toString() {
            return text;
        }
    }
}
