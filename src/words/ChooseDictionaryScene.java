/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package words;

import java.io.File;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/**
 * A class for choosing a dictionary from the list of dictionaries
 * ChooseDictionaryScene has three modes:
 *      Choosing a dictionary to change the words in the dictionary
 *      Choosing a dictionary to add new words to it
 *      Choosing a dictionary to laern words from it
 * @author Mantas
 */
public class ChooseDictionaryScene extends BorderPane {
    private String dictionaryName;
    private int selectedDictionaryID;
    private boolean englishLithuanian;
    
    @SuppressWarnings({"Convert2Lambda", "ConvertToStringSwitch"})
    
    public ChooseDictionaryScene(Dictionaries dicts, String mode, Scene scene) {

        MyLabel chooseDictionaryLabel = new MyLabel("Choose Dictionary", "h2");
        
        MyButton startButton = new MyButton("Load", "simpleButton", true, true);
        
        /**
         * ChoiceBox for choosing a dictionary from the list
         */
        ChoiceBox chooseDictionaryBox = new ChoiceBox();
        chooseDictionaryBox.getItems().addAll(dicts);
        chooseDictionaryBox.getSelectionModel().selectFirst();
        chooseDictionaryBox.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
            @SuppressWarnings("override")
            public void changed(ObservableValue ov, Number value, Number new_value)  {
                if (new_value.intValue() == 0) {
                    startButton.setDisable(true);
                }
                else if ((("change".equals(mode)) ||("add").equals(mode)) && (new_value.intValue() == 1)) {
                    startButton.setDisable(true);
                }
                else if (new_value.intValue() > 0) {
                    selectedDictionaryID = new_value.intValue();
                    startButton.setDisable(false);
                }
            }
        });
        
        /**
         * Button "Load"
         * Disabled when no dictionary or wrong dictionary is selected
         * When pressed,
         *      if mode is "add", then the new instance of 
         *          addNewWordsScene class is created and the program
         *          view is prepared to show it
         *      if mode is "start", then the new instance of 
         *          StartDictionaryScene class is created and the
         *          program view is prepared to show it
         *      if mode is "change", then the new instance of 
         *          ChangeDictionaryScene is created and the program
         *          view is prepared to show it
         */
        startButton.setDefaultButton(true);
        startButton.setDisable(true);
        startButton.setOnAction((ActionEvent event) -> {
            if ("add".equals(mode)) {
                AddNewWordsScene newWords = new AddNewWordsScene(makeFile(dicts));
                ((VBox) scene.getRoot()).getChildren().remove(1);
                ((VBox) scene.getRoot()).getChildren().add(newWords);
            }
            else if ("start".equals(mode)) {
                StartDictionaryScene startDictionary = new StartDictionaryScene(englishLithuanian, makeFile(dicts));
                ((VBox) scene.getRoot()).getChildren().remove(1);
                ((VBox) scene.getRoot()).getChildren().add(startDictionary);
            }
            else if ("change".equals(mode)) {
                ChangeDictionaryScene  changeDictionary = new ChangeDictionaryScene(makeFile(dicts));
                ((VBox) scene.getRoot()).getChildren().remove(1);
                ((VBox) scene.getRoot()).getChildren().add(changeDictionary);
            }
        });
        
        /**
         * The VBox for a Label, ChoiceBox and Button
         */
        VBox chooseDictionarySpace = new VBox();
        chooseDictionarySpace.getChildren().addAll(chooseDictionaryLabel, chooseDictionaryBox,startButton);
        chooseDictionarySpace.setSpacing(10);
        chooseDictionarySpace.setAlignment(Pos.BASELINE_CENTER);
        
        setTop(chooseDictionarySpace);
        setPadding(new Insets(40, 0, 0, 0));
        
        /**
         * If mode is "start" then adds two RadioButtons for the
         * language to learn (LT - ENG or ENG - LT)
         */
        if ("start".equals(mode)) {
            VBox chooseLanguageMenu = new VBox();
            chooseLanguageMenu.setPadding(new Insets(30, 0, 10, 0));
            chooseLanguageMenu.setAlignment(Pos.BASELINE_CENTER);
            
            MyLabel chooseLanguageLabel = new MyLabel("Choose Language", "h2");
            
            /**
             * HBox for the Radiobuttons
             * Css class of RadioButtons - cRadio
             * ENG - LT choice is default
             */
            HBox chooseLanguage = new HBox();
            final ToggleGroup lang = new ToggleGroup();
            RadioButton eng = new RadioButton("ENG - LT");
            eng.setToggleGroup(lang);
            eng.setSelected(true);
            englishLithuanian = true;
            eng.getStyleClass().add("cRadio");
            RadioButton lith = new RadioButton("LT - ENG");
            lith.setToggleGroup(lang);
            lith.getStyleClass().add("cRadio"); 
            lang.selectedToggleProperty().addListener((ObservableValue<? extends Toggle> ov, Toggle old_toggle, Toggle new_toggle) -> {
                englishLithuanian = lang.getSelectedToggle() == eng;
            });
            
            chooseLanguage.getChildren().addAll(eng, lith);
            chooseLanguage.setSpacing(10);
            chooseLanguage.setAlignment(Pos.BASELINE_CENTER);
            chooseLanguageMenu.getChildren().addAll(chooseLanguageLabel, chooseLanguage);
            setCenter(chooseLanguageMenu);
        }
    }
    
    /**
     * Makes the file from the dictionary and returns it
     * @param dicts
     * @return 
     */
    private File makeFile(Dictionaries dicts) {
        dictionaryName = dicts.get(selectedDictionaryID);
        dictionaryName = dictionaryName.toLowerCase();
        File file = new File(dictionaryName + ".txt");
        return file;
    }
}