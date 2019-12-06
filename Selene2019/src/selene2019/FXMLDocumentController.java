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
import java.net.HttpURLConnection;
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
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import static javafx.util.Duration.seconds;
import javax.ws.rs.core.Response;



/**
 *
 * @author Maxime
 */
public class FXMLDocumentController implements Initializable {
    
    
    String strDate, Calcium, Bicarbonate, Metals, Carbon_dioxide, Dioxygen, Dinitrogen, cholerea, ecoli_water, pneumophila, salmonella, ecoli_food, listeria ;
    
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
    @FXML
    private Label label_Pneumophila;
    @FXML
    private Label label_Cholerea;
    @FXML
    private Label label_Listeria;
    @FXML
    private Label label_Salmonella;
    @FXML
    private Label label_Ecoli_Water;
    
  
    //AnimationTimer permettant de refresh les données toutes les "delay" secondes
    AnimationTimer service = new AnimationTimer() {
        private long timestamp;
        private long time = 0;
        private long fraction = 0;
        int delay = 1;

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
    @FXML
    private Label label_Ecoli_Food;

    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        updateGui();
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
                     label_Pneumophila.setText(pneumophila);
                     label_Cholerea.setText(cholerea);
                     label_Listeria.setText(listeria);
                     label_Salmonella.setText(salmonella);
                     label_Ecoli_Water.setText(ecoli_water);
                     label_Ecoli_Food.setText(ecoli_food);
                     
                 } catch (IOException ex) {
                     Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
                 }
         });
    }
    
    public void consumeRest() throws MalformedURLException, IOException{
        float tabBacteries[] = new float[6];
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
        
        //Get bacteries informations from the JSON and update the GUI
        
        cholerea = data.get(11).getAsJsonObject().get("value").getAsString();
        ecoli_water = data.get(10).getAsJsonObject().get("value").getAsString();
        ecoli_food = data.get(20).getAsJsonObject().get("value").getAsString();
        pneumophila = data.get(18).getAsJsonObject().get("value").getAsString();
        listeria = data.get(21).getAsJsonObject().get("value").getAsString();
        salmonella = data.get(19).getAsJsonObject().get("value").getAsString();
        
        tabBacteries[0] = Float.parseFloat(cholerea);
        tabBacteries[1] = Float.parseFloat(ecoli_water);
        tabBacteries[2] = Float.parseFloat(ecoli_food);
        tabBacteries[3] = Float.parseFloat(pneumophila);   
        tabBacteries[4] = Float.parseFloat(listeria);      
        tabBacteries[5] = Float.parseFloat(salmonella);
        GLaDOS(tabBacteries);
    }
    
    //GLaDOS est une IA extrêmement intelligente et 100% self-aware, qui est en charge de la vérification automatique des valeurs des capteurs, afin de protéger nos astronautes.
    //Please note that GLaDOS is not bound by the Three Laws of Robotics.
    private void GLaDOS(float[] tab){
        
        //Vérification de la quantité de cholerea
        if(tab[0]>0.08){
            label_Cholerea.setStyle("-fx-border-color:black; -fx-background-color: yellow;");
            if(tab[0]>0.09){
                System.out.println("Quantité de Vibrio Cholerea dans l'eau critique");
                label_Cholerea.setStyle("-fx-border-color:black; -fx-background-color: red;");
                if(!toggle_Water.isSelected()){
                    toggle_Water.fire();
                }

            }
        }else{
            label_Cholerea.setStyle("-fx-border-color:white; -fx-background-color: white;");
            if(toggle_Water.isSelected()){
                toggle_Water.fire();
            }
        }
        
        
        //Vérification de la quantité de E-Coli dans l'eau
        if(tab[1]>8.0){
            label_Ecoli_Water.setStyle("-fx-border-color:black; -fx-background-color: yellow;");
            if(tab[1]>9.0){
                label_Ecoli_Water.setStyle("-fx-border-color:black; -fx-background-color: red;");
                System.out.println("Quantité de Escherichia Coli dans l'eau critique");
                if(!toggle_Water.isSelected()){
                    
                    toggle_Water.fire();
                }
            }
        }else{
            label_Ecoli_Water.setStyle("-fx-border-color:white; -fx-background-color: white;");
            if(toggle_Water.isSelected()){
                toggle_Water.fire();
            }
        }
        
        //Vérificaiton de la quantité de E-Coli dans la nourriture
        if(tab[2]>40.0){
            label_Ecoli_Food.setStyle("-fx-border-color:black; -fx-background-color: yellow;");
            if(tab[2]>45.0){
                System.out.println("Quantité de Escherichia Coli dans la nourriture critique");
                label_Ecoli_Food.setStyle("-fx-border-color:black; -fx-background-color: red;");
                if(!toggle_Food.isSelected()){
                    toggle_Food.fire();
                }
            }
        }else{
            label_Ecoli_Food.setStyle("-fx-border-color:white; -fx-background-color: white;");
            if(toggle_Food.isSelected()){
                toggle_Food.fire();
            }
        }
        
        //Vérificaiton de la quantité de Pneumophila dans l'air
        if(tab[3]>70.0){
            label_Pneumophila.setStyle("-fx-border-color:black; -fx-background-color: yellow;");
            if(tab[3]>75.0){
                label_Pneumophila.setStyle("-fx-border-color:black; -fx-background-color: red;");
                System.out.println("Quantité de Pneumophila dans l'air critique");
                if(!toggle_Air.isSelected()){
                    toggle_Air.fire();
                }
            }
        }else{
            label_Pneumophila.setStyle("-fx-border-color:white; -fx-background-color: white;");
            if(toggle_Air.isSelected()){
                toggle_Air.fire();  
            }
        }
        
        //Vérificaiton de la quantité de Listeria dans la nourriture
        if(tab[4]>200.0){
            label_Listeria.setStyle("-fx-border-color:black; -fx-background-color: yellow;");
            if(tab[4]>225.0){
                label_Listeria.setStyle("-fx-border-color:black; -fx-background-color: red;");
                System.out.println("Quantité de Listeria dans la nourriture critique");
                if(!toggle_Food.isSelected()){
                    toggle_Food.fire();
                }
            }
        }else{
            label_Listeria.setStyle("-fx-border-color:white; -fx-background-color: white;");
            if(toggle_Food.isSelected()){
                toggle_Food.fire();
            }
        }
        
        //Vérificaiton de la quantité de Salmonella dans la nourriture
        if(tab[5]>7.0){
            label_Salmonella.setStyle("-fx-border-color:black; -fx-background-color: yellow;");
            if(tab[5]>8.0){
                label_Salmonella.setStyle("-fx-border-color:black; -fx-background-color: red;");
                System.out.println("Quantité de Salmonella dans la nourriture critique");
                if(!toggle_Food.isSelected()){
                    toggle_Food.fire();
                }
            }
        }else{
            label_Salmonella.setStyle("-fx-border-color:white; -fx-background-color: white;");
            if(toggle_Food.isSelected()){
                toggle_Food.fire();
            }
        }
    }

    @FXML
    private void toggleAirCleaning(ActionEvent event) throws MalformedURLException, IOException{
        if(toggle_Air.isSelected()){
            System.out.println("Starting chauffe_marcel");
            URL url = new URL ("http://localhost:8080/chauffe_marcel");
            HttpURLConnection  request = (HttpURLConnection) url.openConnection();
            request.setRequestMethod("GET");
            request.setRequestProperty("Maxime", "a1bcdef"); // set userId its a sample here
            int responseCode = request.getResponseCode();
        }else{
            System.out.println("Stopping chauffe_marcel");
            URL url = new URL ("http://localhost:8080/chauffe_marcel");
            HttpURLConnection  request = (HttpURLConnection) url.openConnection();
            request.setRequestMethod("GET");
            request.setRequestProperty("Maxime", "a1bcdef"); // set userId its a sample here
            int responseCode = request.getResponseCode();
        }
    }

    @FXML
    private void toggleWaterCleaning(ActionEvent event) throws MalformedURLException, IOException {
        if(toggle_Water.isSelected()){
            System.out.println("Starting warterleau");
            URL url = new URL ("http://localhost:8080/waterleau");
            HttpURLConnection  request = (HttpURLConnection) url.openConnection();
            request.setRequestMethod("GET");
            request.setRequestProperty("Maxime", "a1bcdef"); // set userId its a sample here
            int responseCode = request.getResponseCode();
        }else{
            System.out.println("Stopping waterleau");
            URL url = new URL ("http://localhost:8080/waterleau");
            HttpURLConnection  request = (HttpURLConnection) url.openConnection();
            request.setRequestMethod("GET");
            request.setRequestProperty("Maxime", "a1bcdef"); // set userId its a sample here
            int responseCode = request.getResponseCode();
        }
    }

    @FXML
    private void toggleFoodCleaning(ActionEvent event) throws MalformedURLException, IOException {
        if(toggle_Food.isSelected()){
            System.out.println("Starting space_deliveroo");
            URL url = new URL ("http://localhost:8080/space_deliveroo");
            HttpURLConnection  request = (HttpURLConnection) url.openConnection();
            request.setRequestMethod("GET");
            request.setRequestProperty("Maxime", "a1bcdef"); // set userId its a sample here
            int responseCode = request.getResponseCode();
        }else{
            System.out.println("Stopping space_deliveroo");
            URL url = new URL ("http://localhost:8080/space_deliveroo");
            HttpURLConnection  request = (HttpURLConnection) url.openConnection();
            request.setRequestMethod("GET");
            request.setRequestProperty("Maxime", "a1bcdef"); // set userId its a sample here
            int responseCode = request.getResponseCode();
        }  
    }
}
