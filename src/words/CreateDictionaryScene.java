/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package words;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

/**
 * A class for creating a dictionary with no words in it (just the file)
 * Takes a list of dictionaries (dicts) to see if there are no dictionaries
 * called like the new one
 * @author Mantas
 */
public class CreateDictionaryScene extends GridPane {
    private String dictionaryName;
    private File dictionary;
    private String dictDir;
    private MyButton createButton;
    private MyButton clearButton;
    
    public CreateDictionaryScene(Dictionaries dicts, Scene scene) {
        File jarFile = new File(this.getClass().getProtectionDomain().getCodeSource().getLocation().getPath());
        dictDir = jarFile.getParentFile().getParent() + "/Dictionaries/";
        /**
         * Label "Name of the dictionary"
         */
        Label nameDictionaryLabel = new Label("Name of the dictionary");
        
        /**
         * TextField for the name of the dictionary
         */
        final TextField nameDictionaryTextField = new TextField();
        nameDictionaryTextField.textProperty().addListener((final ObservableValue<? extends String> observable, final String oldValue, final String newValue) -> {
            if (nameDictionaryTextField.getText().isEmpty()) {
                createButton.setDisable(true);
                clearButton.setDisable(true);
            }
            else {
                createButton.setDisable(false);
                clearButton.setDisable(false);
            }
        });
        nameDictionaryTextField.setPrefColumnCount(10);
        
        /**
         * Button "Create"
         * On default - disabled
         */
        createButton = new MyButton("Create", "simpleButton", true, true);
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
                System.out.println("Error! Dictionary with such a name already exists.");
            }
        });
        
        /**
         * Button "Clear"
         * If pressed, then clears the field nameDictionaryTextField
         */
        clearButton = new MyButton("Clear", "simple", true, false);
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
        
        try {
            try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(dictDir + dictionary), "UTF-8"))) {
            }
        } catch (IOException ex) {
            System.out.println(ex + " Error in CreateDictionaryScene!");
        }	
    }
}