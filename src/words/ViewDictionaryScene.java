/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package words;

import java.io.File;
import java.util.ArrayList;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

/**
 *
 * @author Mantas
 */
public class ViewDictionaryScene extends MyBorderPane {
    
    private ArrayList<Word> wordList;
    private ListView<String> list = new ListView<String>();
    
    public ViewDictionaryScene(File dictionary) {
        String dictionaryName = dictionary.toString();
        dictionaryName = dictionaryName.substring(0, dictionaryName.length()-4).toUpperCase();
        MyLabel dictionaryTitle = new MyLabel(dictionaryName, "h2");
        
        setAlignment(dictionaryTitle, Pos.CENTER);
        
        wordList = loadDictionary(dictionary);
        
        MyLabel english = new MyLabel("", "h3");
        english.getStyleClass().add("bold");
        english.minHeight(20);
        MyLabel pos = new MyLabel("", "h4");
        pos.getStyleClass().add("red");
        pos.minHeight(20);
        
        HBox englishPos = new HBox();
        englishPos.getChildren().addAll(english, pos);
        englishPos.setPrefWidth(220);
        englishPos.setSpacing(20);
        
        MyLabel lithuanian = new MyLabel("", "h4");
        lithuanian.setPadding(new Insets(0, 0, 0, 8));
        lithuanian.setWrapText(true);
        MyLabel exampleSentence = new MyLabel("", "quote");
        exampleSentence.setPrefWidth(220);
        exampleSentence.setMinHeight(40);
        exampleSentence.setPadding(new Insets(0, 0, 0, 12));
        exampleSentence.setWrapText(true);
        MyLabel definition = new MyLabel("", "quote");
        definition.setWrapText(true);
        definition.setPrefWidth(220);
        definition.setMinHeight(40);
        definition.setPadding(new Insets(0, 0, 0, 12));
        
        VBox wordView = new VBox();
        wordView.getStyleClass().add("wordView");
        wordView.getChildren().addAll(englishPos, lithuanian, exampleSentence, definition);
        wordView.setAlignment(Pos.TOP_LEFT);
        wordView.setSpacing(7);
        wordView.setPrefWidth(240);
        wordView.setPadding(new Insets(10, 10, 10, 10));
        setAlignment(wordView, Pos.CENTER_LEFT);
        
        list.setItems(getEnglishTranslations());
        list.setPrefWidth(180);
        list.getSelectionModel().selectedItemProperty().addListener(
            new ChangeListener<String>() {
                public void changed(ObservableValue<? extends String> ov, 
                    String old_val, String new_val) {
                        Word selectedWord = getWordByEnglish(new_val);
                        english.setText(new_val);
                        lithuanian.setText(selectedWord.getLithuanian());
                        pos.setText(selectedWord.getPartOfSpeech());
                        exampleSentence.setText(selectedWord.getExampleSentence());
                        if (exampleSentence.getText().equals(" ")) {
                            exampleSentence.setText("No example sentence was provided.");
                        }
                        definition.setText(selectedWord.getDefinition());
                        if (definition.getText().equals(" ")) {
                            definition.setText("No example sentence was provided.");
                        } 
                    }
            });
        
        
        
        setTop(dictionaryTitle);
        setLeft(list);
        setRight(wordView);
        setPadding(new Insets(10, 10, 10, 10));
        
      
    }
    
    private ObservableList<String> getEnglishTranslations() {
        ObservableList<String> englishTranslations = FXCollections.observableArrayList();
        System.out.println(wordList.size());
        for (Word word : wordList)
            englishTranslations.add(word.getEnglish());
        
        return englishTranslations;
    }
    
    private Word getWordByEnglish(String english) {
        Word word = null;
        for (Word candidateWord : wordList)
            if (candidateWord.getEnglish().equals(english))
                word = candidateWord;
        return word;
    }
}
