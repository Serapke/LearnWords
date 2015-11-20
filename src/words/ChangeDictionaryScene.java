/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package words;

import com.sun.javafx.scene.control.behavior.TextAreaBehavior;
import com.sun.javafx.scene.control.skin.TextAreaSkin;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.StringTokenizer;
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
 * A class for changing the words in the dictionary.
 * Takes a file of the dictionary.
 * @author Mantas
 */
public class ChangeDictionaryScene extends BorderPane {
    
    private final String[] posList = new String[] {"Part Of Speech", "Verb", "Noun", "Adverb", "Adjective"};
    private int posID;
    private ArrayList<Word> wordList;
    
    private final TextField englishTextField;
    private TextArea lithuanianTextArea;
    private TextArea exampleTextArea;
    private final ChoiceBox partOfSpeech;
    private TextArea definitionTextArea;
    private Button deleteWordButton;
    private Button nextWordButton;
    private Button saveWordsButton;
    
    private Word currentWord;
    private int currentWordIndex;
    
    private String dictDir;
    
    @SuppressWarnings("Convert2Lambda")
    public ChangeDictionaryScene(File dictionary) {
        wordList = new ArrayList<Word>();
        File jarFile = new File(this.getClass().getProtectionDomain().getCodeSource().getLocation().getPath());
        dictDir = jarFile.getParent() + "/Dictionaries/";
        
        /**
         * Label "English"
         * Css class - wordsLabel
         */
        Label englishLabel = new Label("English");
        englishLabel.getStyleClass().add("wordsLabel");
        
        /**
         * TextField for the english translation of the current word
         */
        englishTextField = new TextField("");
        englishTextField.setPrefColumnCount(10);
        
        /**
         * Label "Lithuanian"
         * Css class - wordsLabel
         */
        Label lithuanianLabel = new Label("Lithuanian");
        lithuanianLabel.getStyleClass().add("wordsLabel");
        
        /**
         * Tooltip for lithuanianTextArea
         * "Use commas to separate translations"
         */
        final Tooltip lithuanianToolTip = new Tooltip();
        lithuanianToolTip.setText("Use commas to separate translations");
        
        /**
         * TextArea for lithuanian translations of the current word
         * When TAB is pressed, focuses on the next field
         * While lithuanianTextArea is empty, nextWordButton is disabled
         */
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
        if ("".equals(lithuanianTextArea.getText())) {
           // nextWordButton.setDisable(true);
        }
        
        /**
         * ChoiceBox for part of speech of the current word
         * If the part of speech is chosen, enables nextWordButton
         */
        partOfSpeech = new ChoiceBox();
        partOfSpeech.getItems().addAll((Object[]) posList);
        partOfSpeech.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
            @SuppressWarnings("override")
            public void changed(ObservableValue ov, Number value, Number new_value)  {
                if (new_value.intValue() > 0) {
                    posID = new_value.intValue();
                    nextWordButton.setDisable(false);
                }
                else if (new_value.intValue() == 0) {
                    nextWordButton.setDisable(true);
                    saveWordsButton.setDisable(true);
                }
            }
        });
        
        /**
         * Label "Example Sentence"
         * Css class - wordsLabel
         */
        Label exampleLabel = new Label("Example Sentence");
        exampleLabel.getStyleClass().add("wordsLabel");
        
        /**
         * TextArea for example sentence of the current word
         * When TAB is pressed, focuses on the next field
         */
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
         * Label "Definition"
         * Css class - wordsLabel
         */
        Label definitionLabel = new Label("Definition");
        definitionLabel.getStyleClass().add("wordsLabel");
        
        /**
         * TextArea for definition of the current word
         * If TAB is pressed, focuses on the next field
         */
        definitionTextArea = new TextArea("");
        definitionTextArea.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @SuppressWarnings("override")
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
         * Button "Delete Word"
         * When pressed, deletes the current word from the dictionary
         * and if it was not the last word of the list, shows the next
         * word, if it was sets itself disabled
         */
        deleteWordButton = new Button("Delete Word");
        deleteWordButton.setPrefSize(140, 20);
        deleteWordButton.setOnAction((ActionEvent event) -> {
            currentWordIndex--;
            wordList.remove(currentWordIndex);
            if (currentWordIndex < wordList.size())
                showNextWord();
            else 
                deleteWordButton.setDisable(true);
        });
        
        /**
         * Button "Next Word"
         * When "Enter" pressed, fire the button
         * When pressed, change word and show next word on the fields
         */
        nextWordButton = new Button("Next Word");
        nextWordButton.setDefaultButton(true);
        nextWordButton.setPrefSize(120, 20);
        nextWordButton.setOnAction((ActionEvent event) -> {
            changeWord();
            showNextWord();
        });
        
        /**
         * Button "Save"
         * Keyboard Shortcut - CTRL + S
         * Disabled until nextWordButton is pressed at least once
         * When pressed, change last word in the dictionary, save
         * them and disable all fields
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
                changeWord();
                saveWords(dictionary);
                finish();
                saveWordsButton.setDisable(true);
        });
        
        /**
         * The places in the grid where objects will appear
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
         * HBox for buttons
         */
        HBox actionButtons = new HBox();
        actionButtons.getChildren().addAll(deleteWordButton, nextWordButton, saveWordsButton);
        actionButtons.setPadding(new Insets(15, 12, 15, 12));
        actionButtons.setSpacing(10);
        actionButtons.setAlignment(Pos.BASELINE_RIGHT);
        getStyleClass().add("addNewWordsScene");
        setCenter(fields);
        setBottom(actionButtons);
        
        loadDictionary(dictionary);
    }
    
    /**
     * Loads dictionary from the file and saves words into wordList
     * @param dictionary 
     */
    public final void loadDictionary(File dictionary) {
        wordList = new ArrayList<Word>();
        
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(dictDir + dictionary), "UTF-8"));
            String line;
            while ((line = reader.readLine()) != null) {
                makeWord(line);
            }
        }
        catch (UnsupportedEncodingException e) {
            System.err.println(e.getMessage());
	}
        catch(Exception ex) {
            System.err.print("Couldn't read words from the dictionary!" + ex.getMessage());
        }
        
        
        showNextWord();
    }
    
    /**
     * Makes a Word from a String
     * @param lineToParse 
     */
    private void makeWord(String lineToParse) {
        StringTokenizer parser = new StringTokenizer(lineToParse, ".");
        if (parser.hasMoreTokens()) {
           Word nWord = new Word(parser.nextToken(), parser.nextToken(), parser.nextToken(), parser.nextToken(), parser.nextToken());
           wordList.add(nWord);
        }
    }
    
    /**
     * Shows next Word from wordsList in textfields and textareas
     */
    private void showNextWord() {
        if (currentWordIndex == wordList.size()-1) {
            nextWordButton.setDisable(true);
            saveWordsButton.setDisable(false);
            deleteWordButton.setDisable(true);
            System.err.println("No more words to change. If you want to add more words to the dictionary"
                                                        + " simply go to HOME view and select ADD NEW WORDS");
        }
        else {
            currentWord = wordList.get(currentWordIndex);
            currentWordIndex++;
            englishTextField.setText(currentWord.getEnglish());
            lithuanianTextArea.setText(currentWord.getLithuanian());
            partOfSpeech.getSelectionModel().select(currentWord.getPartOfSpeech());
            definitionTextArea.setText(currentWord.getDefinition());
            exampleTextArea.setText(currentWord.getExampleSentence());
        }
    }
    
    /**
     * Makes the ammendments to the current Word
     */
    private void changeWord() {
        currentWord.setEnglish(englishTextField.getText());
        currentWord.setLithuanian(lithuanianTextArea.getText());
        if (exampleTextArea.getText().isEmpty())
           currentWord.setExampleSentence(" ");
        else 
            currentWord.setExampleSentence(exampleTextArea.getText());
        currentWord.setPartOfSpeech(posList[posID]);
        if (definitionTextArea.getText().isEmpty())
           currentWord.setExampleSentence(" ");
        else
            currentWord.setDefinition(definitionTextArea.getText());
    }
    
    /**
     * Saves the changed words in the file of the dictionary
     * @param dictionary 
     */
    private void saveWords(File dictionary) {
        try {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(dictDir + dictionary))) {
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
            System.out.print(ex + " Error in ChangeDictionaryScene!");
        }
    }
    
    /**
     * Disables all textfields and textareas after all the words
     * in the wordList ends
     */
    private void finish() {
        englishTextField.setText("");
        englishTextField.setDisable(true);
        lithuanianTextArea.setText("");
        lithuanianTextArea.setDisable(true);
        exampleTextArea.setText("");
        exampleTextArea.setDisable(true);
        definitionTextArea.setText("");
        definitionTextArea.setDisable(true);
        partOfSpeech.getSelectionModel().selectFirst();
        partOfSpeech.setDisable(true);
    }   
}