package controllers;
//done
import models.exceptions.ResourceNotFoundException;
import org.jetbrains.annotations.NotNull;

import java.io.FileNotFoundException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Helper class for loading resources from the filesystem.
 */
public class ResourceLoader {

    /**
     * Path to the resources directory.
     */
    @NotNull
    private static final Path RES_PATH;

    static{
        // TODO: Initialize RES_PATH
        Path p = Paths.get("","resources");
        if(!p.toFile().exists()){
            throw new RuntimeException("resource folder not exist");
        }
        RES_PATH=p.toAbsolutePath();
    }

    /**
     * Retrieves a resource file from the resource directory.
     *
     * @param relativePath Path to the resource file, relative to the root of the resource directory.
     * @return Absolute path to the resource file.
     * @throws ResourceNotFoundException If the file cannot be found under the resource directory.
     */
    @NotNull
    public static String getResource(@NotNull final String relativePath) throws ResourceNotFoundException{
        // TODO
        if(RES_PATH==null){
            return "";
        }
        Path r=RES_PATH.resolve(relativePath);
        if(r.toFile().exists()) {
            URI resource=r.toAbsolutePath().toFile().toURI();
            return resource.toString();
        }
        throw new ResourceNotFoundException(relativePath + " not found");
    }
}
