/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package words;

import com.sun.javafx.scene.control.behavior.TextAreaBehavior;
import com.sun.javafx.scene.control.skin.TextAreaSkin;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
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
import javafx.scene.text.Font;
import org.json.JSONObject;
import org.json.JSONException;
/**
 * A class for adding new words to an existing dictionary
 * @author Mantas
 */
public class AddNewWordsScene extends MyBorderPane {

    private final String[] posList = new String[] {"Part Of Speech", "Verb", "Noun", "Adverb", "Adjective"};
    private int posID;
    private ArrayList<Word> wordList;

    private final TextField englishTextField;
    private TextArea lithuanianTextArea;
    private TextArea exampleTextArea;
    private final ChoiceBox partOfSpeech;
    private TextArea definitionTextArea;
    private Button previousWordButton;
    private Button nextWordButton;
    private Button saveWordsButton;

    private String dictionariesDir;
    private File dict;
    private int wordIndex;

    private Tooltip lithuanianTip;
    private final String useCommasTip = "Use commas to separate translations!";

    @SuppressWarnings({"Convert2Lambda", "Convert2Diamond"})
    public AddNewWordsScene(File dictionary) {
        File jarFile = new File(this.getClass().getProtectionDomain().getCodeSource().getLocation().getPath());
        dictionariesDir = jarFile.getParentFile().getParent() + "/Dictionaries/";
        dict = dictionary;
        wordList = new ArrayList<Word>();
        wordIndex = 0;

        MyLabel englishLabel = new MyLabel("English", "formLabel");
        englishTextField = new TextField("");
        englishTextField.setPrefColumnCount(10);
        englishTextField.focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if (!newValue && (englishTextField.getText().length() > 2)) {
                    String suggestion = translateWord();
                    if (!suggestion.isEmpty()) {
                        lithuanianTip.setText(useCommasTip + "\n\nSuggested translation by MyMemory: " + suggestion);
                    };
                }
            }
        });

        MyLabel lithuanianLabel = new MyLabel("Lithuanian", "formLabel");
        lithuanianTextArea = new TextArea();
        lithuanianTip = new Tooltip(useCommasTip);
        lithuanianTip.setFont(Font.font ("Verdana", 16));
        lithuanianTextArea.setTooltip(lithuanianTip);
        lithuanianTextArea.addEventFilter(KeyEvent.KEY_PRESSED, (KeyEvent e) -> {
            if (e.getCode() == KeyCode.TAB) {
                TextAreaSkin skin = (TextAreaSkin) lithuanianTextArea.getSkin();
                if (skin.getBehavior() instanceof TextAreaBehavior) {
                    TextAreaBehavior behavior = (TextAreaBehavior) skin.getBehavior();
                    behavior.callAction("TraverseNext");
                    e.consume();
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
                nextWordButton.setDisable(false);
                if (wordIndex >= wordList.size()-1)
                    saveWordsButton.setDisable(false);
            }

        });

        MyLabel exampleLabel = new MyLabel("Example Sentence", "formLabel");
        exampleTextArea = new TextArea("");
        exampleTextArea.addEventFilter(KeyEvent.KEY_PRESSED, (KeyEvent e) -> {
            if (e.getCode() == KeyCode.TAB) {
                TextAreaSkin skin = (TextAreaSkin) exampleTextArea.getSkin();
                if (skin.getBehavior() instanceof TextAreaBehavior) {
                    TextAreaBehavior behavior = (TextAreaBehavior) skin.getBehavior();
                    behavior.callAction("TraverseNext");
                    e.consume();
                }
            }
        });
        exampleTextArea.setPrefColumnCount(20);
        exampleTextArea.setPrefRowCount(3);
        exampleTextArea.setWrapText(true);

        /**
         * If anything except first choice (Part of Speech) is selected
         * nextWordButton is enabled
         */
        partOfSpeech = new ChoiceBox();
        partOfSpeech.getItems().addAll((Object[]) posList);
        partOfSpeech.getSelectionModel().selectFirst();
        
        partOfSpeech.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
            @SuppressWarnings("override")
            public void changed(ObservableValue ov, Number value, Number new_value)  {
                final int NOT_CHOSEN = 0;
                if (new_value.intValue() > NOT_CHOSEN) {
                    posID = new_value.intValue();
                    if (!lithuanianTextArea.getText().isEmpty()) {
                        nextWordButton.setDisable(false);
                        if (wordIndex >= wordList.size()-1)
                            saveWordsButton.setDisable(false);
                    }
                }
                else if (new_value.intValue() == NOT_CHOSEN) {
                    nextWordButton.setDisable(true);
                    saveWordsButton.setDisable(true);
                }
            }
        });

        MyLabel definitionLabel = new MyLabel("Definition", "formLabel");
        definitionTextArea = new TextArea("");
        definitionTextArea.addEventFilter(KeyEvent.KEY_PRESSED, (KeyEvent e) -> {
            if (e.getCode() == KeyCode.TAB) {
                TextAreaSkin skin = (TextAreaSkin) definitionTextArea.getSkin();
                if (skin.getBehavior() instanceof TextAreaBehavior) {
                    TextAreaBehavior behavior = (TextAreaBehavior) skin.getBehavior();
                    behavior.callAction("TraverseNext");
                    e.consume();
                }
            }
        });
        definitionTextArea.setPrefColumnCount(15);
        definitionTextArea.setPrefRowCount(3);
        definitionTextArea.setWrapText(true);

        previousWordButton = new MyButton("Previous Word", "simpleButton", true, false);
        previousWordButton.setOnAction((ActionEvent event) -> {
            showWord(-1);
        });

        /**
         * Button "Next Word"
         * Disabled until lithuanianTextField is not empty and
         * PartOfSpeech is chosen
         * When pressed, creates a new word and puts it on the
         * words list of the dictionary, then clears all fields and
         * textareas, saveWordsButton set enabled
         */
        nextWordButton = new MyButton("Next Word", "simpleButton", true, true);
        nextWordButton.setOnAction((ActionEvent event) -> {
            makeWord();
        });

        /**
         * Button "Save"
         * Disabled until nextWordButton is not pressed
         * Keyboard Shortcut - CTRL + S
         * When pressed, writes new words to a file of dictionary,
         * clears the list of wordList and sets saveWordsButton
         * disabled
         */
        saveWordsButton = new MyButton("Save", "simpleButton", true, false);
        saveWordsButton.addEventHandler(KeyEvent.KEY_RELEASED, new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if (new KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_DOWN).match(event)) {
                    saveWordsButton.fire();
                }
            }
        });
        saveWordsButton.setOnAction((ActionEvent event) -> {
            saveWords();
            showSavedView();
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
        actionButtons.getChildren().addAll(previousWordButton, nextWordButton, saveWordsButton);
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
        Word currentWord;
        String englishTranslation = englishTextField.getText();
        String lituanianTranslation = lithuanianTextArea.getText();
        String exampleSentence = exampleTextArea.getText();
        if (exampleSentence.isEmpty())   exampleSentence = " ";
        String pos = posList[posID];
        String wordDefinition = definitionTextArea.getText();
        if (wordDefinition.isEmpty())  wordDefinition = " ";
        currentWord = new Word(englishTranslation, lituanianTranslation, exampleSentence, pos, wordDefinition);
        //System.out.print(wordIndex + " " + wordList.size());
        if (wordIndex < wordList.size()) {
            wordList.set(wordIndex, currentWord);
        } else {
            wordList.add(currentWord);
        }
        wordIndex++;
        clearWord();
        if (wordIndex > 0)
            previousWordButton.setDisable(false);
    }

    private void saveWords() {
        makeWord();
        try {
                try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(dictionariesDir + dict, true), "UTF-8"))) {
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
                            System.err.println("Couldn't save the file!");
        		}
            wordList.clear();
    }

    /**
     * Clears all the fields and textareas
     */
    private void clearWord() {
        if (wordIndex < wordList.size()) {
            showWord(1);
        } else {
            englishTextField.setText("");
            lithuanianTextArea.setText("");
            exampleTextArea.setText("");
            definitionTextArea.setText("");
            partOfSpeech.getSelectionModel().selectFirst();
        }
    }

    private void showWord(int direction) {
      Word currentWord;
      if ((direction < 0) && (wordIndex > 0)) {                        // show previous word
        currentWord = wordList.get(--wordIndex);
        saveWordsButton.setDisable(true);
        if (wordIndex == 0)
          previousWordButton.setDisable(true);
      } else {                                                          // show next word
            currentWord = wordList.get(wordIndex);
      }
        englishTextField.setText(currentWord.getEnglish());
        lithuanianTextArea.setText(currentWord.getLithuanian());
        exampleTextArea.setText(currentWord.getExampleSentence());
        definitionTextArea.setText(currentWord.getDefinition());
        partOfSpeech.getSelectionModel().select(currentWord.getPartOfSpeech());
    }
    
    private static String readUrl(String urlString) throws Exception {
        BufferedReader reader = null;
        try {
            URL url = new URL(urlString);
            reader = new BufferedReader(new InputStreamReader(url.openStream()));
            StringBuffer buffer = new StringBuffer();
            int read;
            char[] chars = new char[1024];
            while ((read = reader.read(chars)) != -1)
                buffer.append(chars, 0, read); 
            return buffer.toString();
        } finally {
            if (reader != null)
                reader.close();
        }
    }
    
    private String translateWord() {
        String lithuanianSuggestion = "";
        try {
            JSONObject translationJSON = new JSONObject(readUrl("http://api.mymemory.translated.net/get?q=" + englishTextField.getText() + "!&langpair=en|lt&of=json"));
            JSONObject responseData = translationJSON.getJSONObject("responseData");
            lithuanianSuggestion = (String) responseData.get("translatedText"); 
            return lithuanianSuggestion;
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception ex) {
            Logger.getLogger(AddNewWordsScene.class.getName()).log(Level.SEVERE, null, ex);
        }
        return lithuanianSuggestion;
    }
}
