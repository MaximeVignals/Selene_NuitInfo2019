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
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.text.Font;



/**
 *
 * @author Maxime
 */
public class FXMLDocumentController implements Initializable {

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
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            consumeRest();
        } catch (IOException ex) {
            Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }    
    
    private void consumeRest() throws MalformedURLException, IOException{
        
        
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
        String strDate = month + " " + day + " - " + year;
        labelDate.setText(strDate);
        
        //Get air information from the JSON and update the GUI
        JsonArray data = JSONdata.get("data").getAsJsonArray();
        String Calcium = data.get(0).getAsString();
        
    }
    
}
