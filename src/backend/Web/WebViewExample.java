package backend.Web;

import com.codebrig.journey.JourneyBrowserView;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;

public class WebViewExample extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    public void start(Stage primaryStage) {
        JourneyBrowserView browser = new JourneyBrowserView("https://google.com");
        JFrame frame = new JFrame();
        frame.getContentPane().add(browser, BorderLayout.CENTER);

        frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                browser.getCefApp().dispose();
                frame.dispose();
            }
        });

        frame.setTitle("Journey");
        frame.setSize(1000, 600);
        frame.show();
    }

        /*
        primaryStage.setTitle("JavaFX WebView Example");

        WebView webView = new WebView();

        WebEngine webEngine = webView.getEngine();
        webEngine.setJavaScriptEnabled(true);
        //webEngine.load("backend/Web/test/demo1.html");
        webEngine.load(getClass().getResource("/backend/Web/test/demo1.html").toString());
        VBox vBox = new VBox(webView);
        Scene scene = new Scene(vBox, 960, 600);

        primaryStage.setScene(scene);
        primaryStage.show();
        */



}
