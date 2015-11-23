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
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/**
 * A class for changing the words in the dictionary.
 * Takes a file of the dictionary.
 * @author Mantas
 */
public class ChangeDictionaryScene extends MyBorderPane {
    
    private final String[] posList = new String[] {"Part Of Speech", "Verb", "Noun", "Adverb", "Adjective"};
    private int posID;
    private ArrayList<Word> wordList;
    
    private final TextField englishTextField;
    private TextArea lithuanianTextArea;
    private TextArea exampleTextArea;
    private final ChoiceBox partOfSpeech;
    private TextArea definitionTextArea;
    private MyButton previousWordButton;
    private MyButton deleteWordButton;
    private MyButton nextWordButton;
    private MyButton saveWordsButton;
    
    private Word currentWord;
    private int wordIndex;
    
    private String dictDir;
    
    @SuppressWarnings("Convert2Lambda")
    public ChangeDictionaryScene(File dictionary) {
        wordList = new ArrayList<Word>();
        File jarFile = new File(this.getClass().getProtectionDomain().getCodeSource().getLocation().getPath());
        dictDir = jarFile.getParentFile().getParent() + "/Dictionaries/";
        wordIndex = 0;

        MyLabel englishLabel = new MyLabel("English", "formLabel");
        
        /**
         * TextField for the english translation of the current word
         */
        englishTextField = new TextField("");
        englishTextField.setPrefColumnCount(10);
        
        MyLabel lithuanianLabel = new MyLabel("Lithuanian", "formLabel");
        
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
        lithuanianTextArea.textProperty().addListener((final ObservableValue<? extends String> observable, final String oldValue, final String newValue) -> {
            if (lithuanianTextArea.getText().isEmpty()) {
                nextWordButton.setDisable(true);
                saveWordsButton.setDisable(true);
            }
            // if textarea not empty and pos is chosen
            else if ((!lithuanianTextArea.getText().isEmpty()) && (posID > 0)) {
                if (wordIndex >= wordList.size()-1)
                    saveWordsButton.setDisable(false);
                else
                    nextWordButton.setDisable(false);
            }

        });
        
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
                    if (wordIndex != wordList.size()-1)
                        nextWordButton.setDisable(false);
                   saveWordsButton.setDisable(false);
                }
                else if (new_value.intValue() == 0) {
                    nextWordButton.setDisable(true);
                    saveWordsButton.setDisable(true);
                }
            }
        });
        
        MyLabel exampleLabel = new MyLabel("Example Sentence", "formLabel");
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
        
        MyLabel definitionLabel = new MyLabel("Definition", "formLabel");
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
        
        previousWordButton = new MyButton("Previous", "simpleButton", true, false);
        previousWordButton.setOnAction((ActionEvent event) -> {
            showWord(-1);
        });
        /**
         * Button "Delete Word"
         * When pressed, deletes the current word from the dictionary
         * and if it was not the last word of the list, shows the next
         * word, if it was sets itself disabled
         */
        deleteWordButton = new MyButton("Delete", "simpleButton", false, false);
        deleteWordButton.setOnAction((ActionEvent event) -> {
            //System.out.println("wordIndex " + wordIndex + ", wordList.size() " + wordList.size());
            deleteWord();
        });
        
        /**
         * Button "Next Word"
         * When "Enter" pressed, fire the button
         * When pressed, change word and show next word on the fields
         */
        nextWordButton = new MyButton("Next", "simpleButton", false, true);
        nextWordButton.setOnAction((ActionEvent event) -> {
            changeWord();
            showWord(1);
        });
        
        /**
         * Button "Save"
         * Keyboard Shortcut - CTRL + S
         * Disabled until nextWordButton is pressed at least once
         * When pressed, change last word in the dictionary, save
         * them and disable all fields
         */
        saveWordsButton = new MyButton("Save", "simpleButton", false, false);
        saveWordsButton.addEventHandler(KeyEvent.KEY_RELEASED, new EventHandler<KeyEvent>() {
            public void handle(KeyEvent event) {
                if (new KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_DOWN).match(event)) {
                    saveWordsButton.fire();
                }
            }
        });
        saveWordsButton.setOnAction((ActionEvent event) -> {
                changeWord();
                saveWords(dictionary);
                showSavedView();
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
        actionButtons.getChildren().addAll(previousWordButton, deleteWordButton, nextWordButton, saveWordsButton);
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
        
        
        showWord(1);
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
    
    private void deleteWord() {
        wordList.remove(wordIndex);
        if (wordIndex < wordList.size())
            showWord(1);
        else {
            showWord(-1);
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
        wordList.set(wordIndex++, currentWord);
        if (wordIndex > 0)
            previousWordButton.setDisable(false);
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
        wordList.clear();
    }
    
    private void showWord(int direction) {
      if ((direction < 0) && (wordIndex > 0)) {                        // show previous word
        currentWord = wordList.get(--wordIndex);
        if (wordIndex == 0)
          previousWordButton.setDisable(true);
      } else {                                                          // show next word
        currentWord = wordList.get(wordIndex);
        //System.out.println(wordIndex + " - " + wordList.size());
        if (wordIndex == wordList.size()-1)
            nextWordButton.setDisable(true);
      }
        if (1 == wordList.size()) 
            deleteWordButton.setDisable(true);
        englishTextField.setText(currentWord.getEnglish());
        lithuanianTextArea.setText(currentWord.getLithuanian());
        exampleTextArea.setText(currentWord.getExampleSentence());
        definitionTextArea.setText(currentWord.getDefinition());
        partOfSpeech.getSelectionModel().select(currentWord.getPartOfSpeech());
    }
    
    /**
     * Disables all textfields and textareas after all the words
     * in the wordList ends
     */
    private void finish() {
        MyLabel finish = new MyLabel("Your changes have been saved!", "h1");
        MyLabel goHome = new MyLabel("You can go to Home display by:\n 1) Selecting: File > Home \n 2) Using keyboard shortcut: CTRL + H", "suggestion");
        VBox finishVBox = new VBox();
        finishVBox.getChildren().addAll(finish, goHome);
        finishVBox.setAlignment(Pos.CENTER);
        finishVBox.setSpacing(20);
        finishVBox.setPadding(new Insets(20, 0, 0, 0));
        setCenter(finishVBox);
        setBottom(null);
    }   
}