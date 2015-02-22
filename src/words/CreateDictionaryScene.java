/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package words;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javax.swing.JOptionPane;

/**
 * A class for creating a dictionary with no words in it (just the file)
 * Takes a list of dictionaries (dicts) to see if there are no dictionaries
 * called like the new one
 * @author Mantas
 */
public class CreateDictionaryScene extends GridPane {
    private String dictionaryName;
    private File dictionary;
    
    public CreateDictionaryScene(Dictionaries dicts, Scene scene) {
        
        /**
         * Label "Name of the dictionary"
         */
        Label nameDictionaryLabel = new Label("Name of the dictionary");
        
        /**
         * TextField for the name of the dictionary
         */
        final TextField nameDictionaryTextField = new TextField();
        nameDictionaryTextField.setPrefColumnCount(10);
        
        /**
         * Button "Create"
         * On default - disabled
         */
        Button createButton = new Button("Create");
        createButton.setDefaultButton(true);
        createButton.setOnAction((ActionEvent event) -> {
            dictionaryName = nameDictionaryTextField.getText();
            /**
             * Checks if the name of the new dictionary is already used,
             * if it's not dictionary is added to the list of dictionaries
             * and refreshes the list, then creates the instance of
             * addNewWordsScene and prepares the view, else returns error
             * message
             */
            if (!dicts.contains(dictionaryName)) {
                createDictionaryFile(dicts);
                AddNewWordsScene newWords = new AddNewWordsScene(dictionary);
                ((VBox) scene.getRoot()).getChildren().remove(1);
                ((VBox) scene.getRoot()).getChildren().add(newWords);
            }
            else {
                InfoBoxProvider error = new InfoBoxProvider("Dictionary with such a name already exists!", "Error");
            }
        });
        
        /**
         * Button "Clear"
         * If pressed, then clears the field nameDictionaryTextField
         */
        Button clearButton = new Button("Clear");
        clearButton.setOnAction((ActionEvent event) -> {
            nameDictionaryTextField.clear(); 
        });
        
        /**
         * The places in the grid where objects will appear
         */
        GridPane.setConstraints(nameDictionaryLabel, 0, 0, 2, 1);
        GridPane.setConstraints(nameDictionaryTextField, 0, 1, 2, 1);
        GridPane.setConstraints(createButton, 0, 2);
        GridPane.setConstraints(clearButton, 1, 2);
        
        setPadding(new Insets(10, 10, 10, 10));
        setVgap(10);
        setHgap(20);
        getChildren().addAll(nameDictionaryLabel, nameDictionaryTextField, createButton, clearButton);
        getStyleClass().add("createDictionaryScene");
        setAlignment(Pos.BOTTOM_CENTER);
    }
    
    /**
     * Creates dictionary file from the dictionaryName
     */
    private void createDictionaryFile(Dictionaries dicts) {
        dicts.add(dictionaryName);
        dicts.refreshDicts();
        dictionaryName = dictionaryName.toLowerCase() + ".txt";
        dictionary = new File(dictionaryName);
        String path = MenuWindow.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        Path p = Paths.get(URI.create("file:" + path));
        p = p.getParent().getParent();
        
        try {
            try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(p + "/Dictionaries/" + dictionary), "UTF-8"))) {
            }
        } catch (IOException ex) {
            InfoBoxProvider error = new InfoBoxProvider("Dictionary Creation Failed!", "Error");
        }	
    }
}