package backend;

import com.darkprograms.speech.synthesiser.SynthesiserV2;
import com.darkprograms.speech.translator.GoogleTranslate;
import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.advanced.AdvancedPlayer;

import java.io.IOException;
import java.util.logging.Level;

/**
 * @author goxr3plus, Tommy Luu
 * Text-to-speech using google translate API
 */
public class Text_To_Speech {
    //Create a Synthesizer instance
    SynthesiserV2 synthesizer = new SynthesiserV2("AIzaSyBOti4mM-6x9WDnZIjIeyEU21OpBXqWBgw");
    private static final Log logger = new Log("Log.log");

    /**
     * Calls the MaryTTS to say the given text
     * @param text
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
        thread.start();
    }
}
