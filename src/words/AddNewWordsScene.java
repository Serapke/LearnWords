/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package words;

import com.sun.javafx.scene.control.behavior.TextAreaBehavior;
import com.sun.javafx.scene.control.skin.TextAreaSkin;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;

/**
 * A class for adding new words to an existing dictionary
 * @author Mantas
 */
public class AddNewWordsScene extends BorderPane {
    
    private final String[] posList = new String[] {"Part Of Speech", "Verb", "Noun", "Adverb", "Adjective"};
    private int posID;
    private ArrayList<Word> wordList;
    
    private final TextField englishTextField;
    private TextArea lithuanianTextArea;
    private TextArea exampleTextArea;
    private final ChoiceBox partOfSpeech;
    private TextArea definitionTextArea;
    private Button nextWordButton;
    private Button saveWordsButton;
    
    private Path p;
    
    @SuppressWarnings("Convert2Lambda")
    public AddNewWordsScene(File dictionary) {
        String path = MenuWindow.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        p = Paths.get(URI.create("file:" + path));
        p = p.getParent().getParent();
        
        wordList = new ArrayList<Word>();
        
        /**
         * Label "English"
         * 
         * TextField for english translation
         */
        Label englishLabel = new Label("English");
        
        englishTextField = new TextField("");
        englishTextField.setPrefColumnCount(10);  
        
        /**
         * Tooltip for lithuanianTextArea "Use commas to separate
         * translations"
         * 
         * Label "Lithuanian"
         * 
         * TextArea for lithuanian translation
         * TAB pressed - gets to the next object (exampleTextArea)
         * While not entered, nextWordButton is disabled
         */
        final Tooltip lithuanianToolTip = new Tooltip();
        lithuanianToolTip.setText("Use commas to separate translations");
        
        Label lithuanianLabel = new Label("Lithuanian");
        
        lithuanianTextArea = new TextArea();
        lithuanianTextArea.setTooltip(lithuanianToolTip);
        lithuanianTextArea.setOnKeyPressed(new EventHandler<KeyEvent>() {
            public void handle(KeyEvent k) {
                if (k.getCode() == KeyCode.TAB) {
                    TextAreaSkin skin = (TextAreaSkin) lithuanianTextArea.getSkin();
                    TextAreaBehavior behavior = (TextAreaBehavior) skin.getBehavior();
                    behavior.callAction("TraverseNext");
                    k.consume();
                }
            }
        });
        lithuanianTextArea.setPrefColumnCount(20);
        lithuanianTextArea.setPrefRowCount(2);
        lithuanianTextArea.setWrapText(true);
        lithuanianTextArea.textProperty().addListener((final ObservableValue<? extends String> observable, final String oldValue, final String newValue) -> {
            if (lithuanianTextArea.getText().isEmpty()) {
                nextWordButton.setDisable(true);
            }
            else if ((!lithuanianTextArea.getText().isEmpty()) && (posID > 0)) {
                nextWordButton.setDisable(false);
            }
        });
        
        /**
         * Label "Example Sentence"
         * 
         * TextArea for an example sentence of the word
         * TAB pressed - gets to the next object (definitionTextArea)
         */
        Label exampleLabel = new Label("Example Sentence");
        
        exampleTextArea = new TextArea("");
        exampleTextArea.setOnKeyPressed(new EventHandler<KeyEvent>() {
            public void handle(KeyEvent k) {
                if (k.getCode() == KeyCode.TAB) {
                    TextAreaSkin skin = (TextAreaSkin) exampleTextArea.getSkin();
                    TextAreaBehavior behavior = (TextAreaBehavior) skin.getBehavior();
                    behavior.callAction("TraverseNext");
                    k.consume();
                }
            }
        });
        exampleTextArea.setPrefColumnCount(20);
        exampleTextArea.setPrefRowCount(3);
        exampleTextArea.setWrapText(true);
        
        /**
         * The ChoiceBox for choosing a part of speech of the word
         * The list in the partOfSpeech is posList (the list of parts
         * of speeches)
         * If anything except first choice (Part of Speech) is selected
         * nextWordButton is enabled
         */
        partOfSpeech = new ChoiceBox();
        partOfSpeech.getItems().addAll((Object[]) posList);
        partOfSpeech.getSelectionModel().selectFirst();
        partOfSpeech.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
            @SuppressWarnings("override")
            public void changed(ObservableValue ov, Number value, Number new_value)  {
                if (new_value.intValue() > 0) {
                    posID = new_value.intValue();
                    if (!lithuanianTextArea.getText().isEmpty())
                        nextWordButton.setDisable(false);
                }
                else if (new_value.intValue() == 0) {
                    nextWordButton.setDisable(true);
                }
            }
        });
        
        /**
         * Label "Definition"
         * 
         * TextArea for the definition of the word
         * TAB pressed - gets to the next object (nextWordButton)
         */
        Label definitionLabel = new Label("Definition");
        
        definitionTextArea = new TextArea("");
        definitionTextArea.setOnKeyPressed(new EventHandler<KeyEvent>() {
            public void handle(KeyEvent k) {
                if (k.getCode() == KeyCode.TAB) {
                    TextAreaSkin skin = (TextAreaSkin) definitionTextArea.getSkin();
                    TextAreaBehavior behavior = (TextAreaBehavior) skin.getBehavior();
                    behavior.callAction("TraverseNext");
                    k.consume();
                }
            }
        });
        definitionTextArea.setPrefColumnCount(15);
        definitionTextArea.setPrefRowCount(3);
        definitionTextArea.setWrapText(true);
        
        /**
         * Button "Next Word"
         * Disabled until lithuanianTextField is not empty and 
         * PartOfSpeech is chosen
         * When pressed, creates a new word and puts it on the
         * words list of the dictionary, then clears all fields and
         * textareas, saveWordsButton set enabled
         */
        nextWordButton = new Button("Next Word");
        nextWordButton.setDisable(true);
        nextWordButton.setDefaultButton(true);
        nextWordButton.setPrefSize(120, 20);
        nextWordButton.setOnAction((ActionEvent event) -> {
            makeWord();
            clearWord();
            saveWordsButton.setDisable(false);
        });
        
        /**
         * Button "Save"
         * Disabled until nextWordButton is not pressed
         * Keyboard Shortcut - CTRL + S
         * When pressed, writes new words to a file of dictionary,
         * clears the list of wordList and sets saveWordsButton
         * disabled
         */
        saveWordsButton = new Button("Save");
        saveWordsButton.addEventHandler(KeyEvent.KEY_RELEASED, new EventHandler<KeyEvent>() {
            public void handle(KeyEvent event) {
                if (new KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_DOWN).match(event)) {
                    saveWordsButton.fire();
                }
            }
        });
        saveWordsButton.setDisable(true);
        saveWordsButton.setPrefSize(80, 20);
        saveWordsButton.setOnAction((ActionEvent event) -> {
                try {
                    try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(p + "/Dictionaries/" + dictionary, true), "UTF-8"))) {
                        for(Word w:wordList) {
                            writer.write(w.getEnglish() + ".");
                            writer.write(w.getLithuanian() + ".");
                            writer.write(w.getExampleSentence() + ".");
                            writer.write(w.getPartOfSpeech() + ".");
                            writer.write(w.getDefinition() + ".");
                            writer.newLine();
                        }
                    }
		} catch (IOException ex) {
			InfoBoxProvider error = new InfoBoxProvider("Couldn't save the file!", "Error");
		}
                wordList.clear();
                saveWordsButton.setDisable(true);
        });
        
        /**
         * The places in the grid where object will appear
         */
        GridPane.setConstraints(englishLabel, 0, 0);
        GridPane.setConstraints(englishTextField, 0, 1);
        GridPane.setConstraints(partOfSpeech, 1, 1);
        GridPane.setConstraints(lithuanianLabel, 0, 2);
        GridPane.setConstraints(lithuanianTextArea, 0, 3);
        GridPane.setConstraints(exampleLabel, 0, 5);
        GridPane.setConstraints(definitionLabel, 1, 5);
        GridPane.setConstraints(exampleTextArea, 0, 6);
        GridPane.setConstraints(definitionTextArea, 1, 6);
        
        /**
         * The GridPane for all the fields (textareas, labels and
         * textfields)
         */
        GridPane fields = new GridPane();
        fields.setPadding(new Insets(10, 10, 10, 10));
        fields.setVgap(10);
        fields.setHgap(10);
        fields.getChildren().addAll(englishLabel, englishTextField, partOfSpeech, lithuanianLabel,
                lithuanianTextArea, exampleLabel, exampleTextArea,
                definitionLabel, definitionTextArea);
        fields.setAlignment(Pos.TOP_LEFT);
        
        /**
         * The HBox for buttons
         */
        HBox actionButtons = new HBox();
        actionButtons.getChildren().addAll(nextWordButton, saveWordsButton);
        actionButtons.setPadding(new Insets(15, 12, 15, 12));
        actionButtons.setSpacing(10);
        actionButtons.setAlignment(Pos.BASELINE_RIGHT);
        
        getStyleClass().add("addNewWordsScene");
        setCenter(fields);
        setBottom(actionButtons);        
    }
    
    /**
     * Makes a Word from the fields entered and puts the Word into
 the list of Words (wordList)
     */
    private void makeWord() {
        Word newWord;
        String eng = englishTextField.getText();
        String lt = lithuanianTextArea.getText();
        String ex = exampleTextArea.getText();
        if (ex.isEmpty())   ex = " ";
        String pos = posList[posID];
        String def = definitionTextArea.getText();
        if (def.isEmpty())  def = " ";
        newWord = new Word(eng, lt, ex, pos, def);
        wordList.add(newWord);
    }
    
    /**
     * Clears all the fields and textareas
     */
    private void clearWord() {
        englishTextField.setText("");
        lithuanianTextArea.setText("");
        exampleTextArea.setText("");
        definitionTextArea.setText("");
        partOfSpeech.getSelectionModel().selectFirst();
    }
}