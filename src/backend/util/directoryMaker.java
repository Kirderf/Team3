package backend.util;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;

public abstract class directoryMaker {
    public static String folderMaker(String name) throws UnsupportedEncodingException {
        URL url = Log.class.getProtectionDomain().getCodeSource().getLocation();
        String jarPath = URLDecoder.decode(url.getFile(), "UTF-8");
        String parentPath = new File(jarPath).getParentFile().getPath();
        File newDir = new File(parentPath+ File.separator+ name + File.separator);
        newDir.mkdir();
        return newDir.getAbsolutePath();
    }
}
