package backend.util;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;

public final class DirectoryMaker {
    private DirectoryMaker(){
        //hide the implicit public constructor
    }
    /**
     * Used to create a new folder relative to the jar file
     *
     * @param name The name of this new folder
     * @return a string with a path to this new folder, in order to use this path you need to add a File seperator onto the end
     * @throws UnsupportedEncodingException
     */
    public static String folderMaker(String name) throws UnsupportedEncodingException {
        //gets the url of the jar file
        URL url = Log.class.getProtectionDomain().getCodeSource().getLocation();
        //decodes it into a string
        String jarPath = URLDecoder.decode(url.getFile(), "UTF-8");
        //moves up one layer, to the folder the jar file is placed in
        String parentPath = new File(jarPath).getParentFile().getPath();
        //the location of the new directory
        String newDirectory = parentPath + File.separator + name + File.separator;
        File newDir = new File(newDirectory);
        //makes a directory
        newDir.mkdir();
        //returns the path to the new directory
        return newDirectory;
    }
}
