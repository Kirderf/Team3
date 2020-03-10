package backend;

import com.darkprograms.speech.synthesiser.SynthesiserV2;
import com.darkprograms.speech.translator.GoogleTranslate;
import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.advanced.AdvancedPlayer;

import java.io.IOException;
import java.util.logging.Level;

public class Text_To_Speech {
    //Create a Synthesizer instance
    SynthesiserV2 synthesizer = new SynthesiserV2("AIzaSyBOti4mM-6x9WDnZIjIeyEU21OpBXqWBgw");
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(Text_To_Speech.class.getName());

    public Text_To_Speech() throws IOException {
        //Startup message
        speak(GoogleTranslate.translate("ja","Hello how are you?"));
    }

    /**
     * Calls the MaryTTS to say the given text
     *
     * @param text
     */
    public void speak(String text) {
        System.out.println(text);

        //Create a new Thread because JLayer is running on the current Thread and will make the application to lag
        Thread thread = new Thread(() -> {
            try {
                //Create a JLayer instance
                AdvancedPlayer player = new AdvancedPlayer(synthesizer.getMP3Data(text));
                player.play();

                logger.log(Level.WARNING, "Successfully received synthesizer data");

            } catch (IOException | JavaLayerException e) {

                e.printStackTrace(); //Print the exception ( we want to know , not hide below our finger , like many developers do...)

            }
        });

        //We don't want the application to terminate before this Thread terminates
        thread.setDaemon(false);

        //Start the Thread
        thread.start();
    }
}
