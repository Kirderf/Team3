package backend.util;

import com.darkprograms.speech.synthesiser.SynthesiserV2;
import controller.ControllerPreferences;
import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.advanced.AdvancedPlayer;

import java.io.IOException;

/**
 * The class Text_To_Speech creates objects that lets
 * the application access a synthesizer that reads text
 * to the user.
 */
public class Text_To_Speech {
    private static final Log logger = new Log();
    private static boolean instanceExist = false;
    private static Text_To_Speech instance;

    //Create a Synthesizer instance
    private SynthesiserV2 synthesizer = new SynthesiserV2("AIzaSyBOti4mM-6x9WDnZIjIeyEU21OpBXqWBgw");

    private Text_To_Speech() {
    }

    /**
     * Gets the Text_To_Speech instance.
     *
     * @return the instance
     */
    public static Text_To_Speech getInstance() {
        if (!instanceExist) {
            instance = new Text_To_Speech();
            instanceExist = true;
            return instance;
        }
        return instance;
    }

    /**
     * Calls the MaryTTS to say the given text
     *
     * @param text the text
     */
    public void speak(String text) {
        //Create a new Thread because JLayer is running on the current Thread and will make the application to lag
        Thread thread = new Thread(() -> {
            try {
                //Create a JLayer instance
                AdvancedPlayer player = new AdvancedPlayer(synthesizer.getMP3Data(text));
                player.play();
                logger.logNewInfo("Text_To_Speech : " + "Successfully recieved synthesizer data");
            } catch (IOException | JavaLayerException e) {
                logger.logNewFatalError("Text_To_Speech : " + e.getLocalizedMessage());
            }
        });

        //We don't want the application to terminate before this Thread terminates
        thread.setDaemon(false);

        //Start the Thread
        if (ControllerPreferences.isTtsChecked()) {
            thread.start();
        }
    }

}
