package io;
//done
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.nio.file.Path;

/**
 * A serializer for converting {@link GameProperties} into a map file.
 */
public class Serializer {

    /**
     * Path to the map to serialize to.
     */
    @NotNull
    private Path path;

    public Serializer(@NotNull final Path path) {
        this.path = path;
    }

    /**
     * Serializes a {@link GameProperties} object and saves it into a file.
     *
     * @param prop {@link GameProperties} objeect to serialize and save.
     * @throws IOException if an I/O exception has occurred.
     */
    public void serializeGameProp(@NotNull final GameProperties prop) throws IOException {
        // TODO
        File f=this.path.toFile();
        if(f.exists()&&f.delete());
        f.createNewFile();
        try(BufferedWriter writer=new BufferedWriter(new PrintWriter(f))){
            writer.write(String.valueOf(prop.rows));
            writer.newLine();
            writer.write(String.valueOf(prop.cols));
            writer.newLine();
            writer.write(String.valueOf(prop.delay));
            writer.newLine();
            for(int i=0;i<prop.rows;i++){
                for(int j=0;j<prop.cols;j++){
                    writer.write(prop.cells[i][j].toSerializedRep());
                }
                writer.newLine();
            }
        }catch (Exception e){
            throw new IOException("cannot do IO");
        }
    }
}
