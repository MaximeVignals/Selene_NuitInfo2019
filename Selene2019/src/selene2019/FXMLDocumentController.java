/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package selene2019;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.jfoenix.controls.JFXToggleButton;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.time.Duration;
import java.util.ResourceBundle;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.animation.AnimationTimer;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.text.Font;
import static javafx.util.Duration.seconds;



/**
 *
 * @author Maxime
 */
public class FXMLDocumentController implements Initializable {

    Thread Service;
    
    String strDate, Calcium, Bicarbonate, Metals, Carbon_dioxide, Dioxygen, Dinitrogen;
    
    @FXML
    private Label labelDate;
    @FXML
    private Font x2;
    @FXML
    private Font x1;
    @FXML
    private Label label_CO2;
    @FXML
    private Label label_N2;
    @FXML
    private Label label_O2;
    @FXML
    private Label label_Ca;
    @FXML
    private Label label_Metals;
    @FXML
    private Label label_HCO3;
    @FXML
    private JFXToggleButton toggle_Air;
    @FXML
    private JFXToggleButton toggle_Water;
    @FXML
    private JFXToggleButton toggle_Food;
    
    
  
    //AnimationTimer permettant de refresh les données toutes les "delay" secondes
    AnimationTimer service = new AnimationTimer() {
        private long timestamp;
        private long time = 0;
        private long fraction = 0;
        int delay = 3;

        @Override
        public void start() {
            // current time adjusted by remaining time from last run
            timestamp = System.currentTimeMillis() - fraction;
            super.start();
        }

        @Override
        public void stop() {
            super.stop();
            // save leftover time not handled with the last update
            fraction = System.currentTimeMillis() - timestamp;
        }

        @Override
        public void handle(long now) {
            long newTime = System.currentTimeMillis();
            if (timestamp + (delay *1000) <= newTime) {
                long deltaT = (newTime - timestamp) / 1000;
                time += deltaT;
                timestamp += 1000 * deltaT;
                updateGui();
            }
        }
    };
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        service.start();
    }
    
    public void updateGui(){
         Platform.runLater(() -> {
                 try {
                     consumeRest();
                     labelDate.setText(strDate);
                     label_Ca.setText(Calcium);
                     label_HCO3.setText(Bicarbonate);
                     label_Metals.setText(Metals);
                     label_CO2.setText(Carbon_dioxide);
                     label_O2.setText(Dioxygen);
                     label_N2.setText(Dinitrogen);
                 } catch (IOException ex) {
                     Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
                 }
         });
    }
    
    public void consumeRest() throws MalformedURLException, IOException{
        URL url = new URL ("http://localhost:8080/data");
        URLConnection request = url.openConnection();
        request.connect();

        //Get JSON from the Selene API and convert it to a JsonObject for usage
        JsonParser jp = new JsonParser(); //from gson
        JsonElement root = jp.parse(new InputStreamReader((InputStream) request.getContent())); //Convert the input stream to a json element
        JsonObject JSONdata = root.getAsJsonObject();

        //Get date information from the JSON and update the GUI
        JsonObject date = JSONdata.get("date").getAsJsonObject();
        String month = date.get("month").getAsString();
        String day = date.get("dayOfMonth").getAsString();
        String year = date.get("year").getAsString();
        strDate = month + " " + day + " - " + year;

        //Get the data array from the JSON
        JsonArray data = JSONdata.get("data").getAsJsonArray();

        //Get water information from the JSON and update the GUI
        Calcium = data.get(0).getAsJsonObject().get("value").getAsString().substring(0, 6);
        Bicarbonate = data.get(3).getAsJsonObject().get("value").getAsString().substring(0, 6);
        Metals = data.get(9).getAsJsonObject().get("value").getAsString().substring(0, 6);

        //Get air information from the JSON and update the GUI
        Carbon_dioxide = data.get(14).getAsJsonObject().get("value").getAsString().substring(0, 6);
        Dioxygen = data.get(13).getAsJsonObject().get("value").getAsString().substring(0, 6);
        Dinitrogen = data.get(12).getAsJsonObject().get("value").getAsString().substring(0, 6);
       
    }
    
}
