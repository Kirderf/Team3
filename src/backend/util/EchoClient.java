package backend.util;

import java.io.IOException;
import java.net.Socket;

public class EchoClient {

    Log logger = new Log();

    private EchoClient() {

    }

    public static EchoClient getInstance() {
        return new EchoClient();
    }

    public boolean ping() throws IOException {
        Socket pingSocket = null;

        try {
            pingSocket = new Socket("mysql.stud.ntnu.no", 3306);
            logger.logNewInfo("You are connected to NTNUs network");
            return pingSocket.isConnected();
        } catch (IOException e) {
            logger.logNewFatalError("Not connected to NTNUs network");
            return false;
        } finally {
            if (pingSocket != null) {
                pingSocket.close();
            }
        }

    }
}
