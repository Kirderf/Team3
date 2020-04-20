package backend.util;

import java.io.IOException;
import java.net.Socket;

/**
 * The class EchoClient is used to check if the user
 * is connected to the VPN network.
 */
public class EchoClient {

    private Log logger = new Log();
    private EchoClient() {

    }

    /**
     * Gets the instance
     *
     * @return the instance
     */
    public static EchoClient getInstance() {
        return new EchoClient();
    }

    /**
     * This method tries to ping the NTNU SQL server to check if the
     * user is connected to the network or not.
     *
     * @return true if connected to the network, false if not
     */
    public boolean ping() {

        try (Socket pingSocket = new Socket("mysql.stud.ntnu.no", 3306)) {
            logger.logNewInfo("You are connected to NTNUs network");
            return pingSocket.isConnected();
        } catch (IOException e) {
            logger.logNewFatalError("Not connected to NTNUs network");
            return false;
        }

    }
}
