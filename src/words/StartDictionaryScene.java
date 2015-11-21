/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package words;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.StringTokenizer;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.StrokeType;
import javafx.scene.text.Text;
import javafx.scene.text.TextBoundsType;

/**
 * A class for learning the words from dictionary (file).
 *      If mode (translationMode) is True then ENG-LT
 *      If mode (translationMode) is False then LT-ENG
 */
public class StartDictionaryScene extends BorderPane {
    private final File dictionary;
    private String dictionaryName;
    private final boolean translationMode;
    private ArrayList<Word> wordList;
    private ArrayList<Word> wrongWordList;
    private Word currentWord;
    private int currentWordIndex;
    
    private int wordsCount;
    private int errCount;
    
    private final Label wordLabel;
    private final TextField translationTextField;
    private final Label posLabel;
    private final Label correctAnswerLabel;
    private final TextArea definition;
    private final TextArea example;
    private final VBox buttonArea;
    private MyButton submitButton;
    
    private boolean isShowAnswer;
    private String correctAnswer;
    private boolean foundCorrectAnswer;
    private String oneOfPossibleAnswers;
    private String userAnswer;
    private final HBox wordStatus;
    private final ImageView wordStatusImage;
    private Image correctWordImage;
    private Image wrongWordImage;
    private final Label wordStatusLabel;
    
    private boolean showStatistics;
    
    private String dictDir;
    private java.net.URL imgURL;
    
    
    public StartDictionaryScene(boolean mode, File file, Scene scene) {
        File jarFile = new File(this.getClass().getProtectionDomain().getCodeSource().getLocation().getPath());
        dictDir = jarFile.getParent() + "/Dictionaries/";
        imgURL = MenuWindow.class.getResource("Images");
        
        
        dictionary = file;
        translationMode = mode;
        
        /**
         * Label "Word"
         */
        wordLabel = new Label("Word");
        
        /**
         * A TextField for the translation of the word
         */
        translationTextField = new TextField();
        translationTextField.setPrefColumnCount(20);  
        
        /**
         * Part of Speech label
         * Changes depending on the word
         */
        posLabel = new Label("Part of Speech");
        
        /**
         * Correct answer label
         * Shows correct answers after submitting user's choice
         */
        correctAnswerLabel = new Label("Correct answer");
        
        /**
         * The status of answer's correctness
         * Shows different image 
         */
        wordStatus = new HBox();
        wordStatusImage = new ImageView();
        wordStatusLabel = new Label(); 
        wordStatus.setSpacing(5);
        wordStatus.getChildren().addAll(wordStatusImage, wordStatusLabel);
        
        /**
         * VBox for definition and example TextAreas
         */
        VBox hintExampleSpace = new VBox();
        definition = new TextArea();
        definition.setWrapText(true);
        definition.setPrefColumnCount(30);
        definition.setPrefRowCount(2);
        definition.setEditable(false);
        
        example = new TextArea();
        example.setWrapText(true);
        example.setPrefColumnCount(30);
        example.setPrefRowCount(2);
        example.setEditable(false);
        
        hintExampleSpace.getChildren().addAll(definition, example);
        hintExampleSpace.setPadding(new Insets(10, 0, 10, 0));
        hintExampleSpace.setSpacing(10);
        hintExampleSpace.setAlignment(Pos.TOP_LEFT);
        hintExampleSpace.getStyleClass().add("sidebar");
        
        /**
         * VBox for buttons
         * Submit button
         *      When pressed, determines if user's answer is
         *      correct and shows correct answers. If there is no
         *      words left, show statistics. Else, next word. 
         */
        buttonArea = new VBox();
        submitButton = new MyButton("Submit", "simpleButton", false, true);
        submitButton.setPrefWidth(120);
        submitButton.setOnAction((ActionEvent event) -> {
            if (isShowAnswer) {                                                             // show if user was correct OR show next Word
                showAnswer();
            }
            else if (showStatistics) {
                showStatisticsGrid();
            }
            /**
             * Show next word
             */
            else {
                if (currentWordIndex < wordList.size()) {
                    showNextWord();
                }
            }
        });
        buttonArea.setAlignment(Pos.CENTER);
        buttonArea.getChildren().add(submitButton);
        
        /**
         * The places in the grid where objects will appear
         */
        GridPane.setConstraints(wordLabel, 0, 0);
        GridPane.setConstraints(posLabel, 1, 0);
        GridPane.setConstraints(translationTextField, 0, 2, 2, 1);
        GridPane.setConstraints(wordStatus, 2, 2);
        GridPane.setConstraints(correctAnswerLabel, 0, 3, 3, 1);
        GridPane.setConstraints(hintExampleSpace, 0, 4, 3, 1);
        
        /**
         * The GridPane for all the fields (textareas, labels and
         * textfields)
         */
        GridPane fields = new GridPane();
        fields.getChildren().addAll(wordLabel, posLabel, translationTextField, wordStatus, correctAnswerLabel, hintExampleSpace);
        fields.getStyleClass().add("startDictionaryScene");
        fields.setPadding(new Insets(10, 10, 10, 10));
        fields.setVgap(8);
        fields.setHgap(15);
        fields.setPrefWidth(450);
        fields.setAlignment(Pos.TOP_LEFT);
        
        setLeft(fields);
        setBottom(buttonArea);
        loadDictionary(dictionary);
    }
    
    /**
     * Loads dictionary from the file and saves words into wordList
     * @param dictionary 
     */
    @SuppressWarnings({"Convert2Diamond", "ConvertToTryWithResources"})
    public final void loadDictionary(File dictionary) {
        
        wordList = new ArrayList<Word>();
        wrongWordList = new ArrayList<Word>();
        wordsCount = 0;                             // # of words in a dictionary
        try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(dictDir+ dictionary), "UTF-8"));
                String line;
                while ((line = reader.readLine()) != null) {
                        wordsCount++;
                        makeWord(line);
                }	
                reader.close();
        } catch(Exception ex) {
            System.out.println(ex);
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
        currentWord = wordList.get(currentWordIndex);
        currentWordIndex++;
        posLabel.setText("(" + currentWord.getPartOfSpeech() + ")");
        translationTextField.setText("");
        definition.setText("Definition");
        example.setText("Example sentence");
        wordStatusLabel.setText("");
        wordStatusImage.setImage(null);
        correctAnswerLabel.setText("");
        isShowAnswer = true;
        submitButton.setText("Submit");
        if (translationMode) {
            wordLabel.setText(currentWord.getEnglish());  
        }
        else {
            wordLabel.setText(currentWord.getLithuanian()); 
        }
    }
    
    /**
     * Shows the correct answer and determines either the user's
     * answer is correct or wrong
     */
    private void showAnswer() {
        /**
         * If ENG-LT, then...
         */
        if (translationMode) {
            foundCorrectAnswer = false;
            correctAnswer = currentWord.getLithuanian();                            // gets all correct answers
            StringTokenizer parser = new StringTokenizer(correctAnswer, ",");
            userAnswer = translationTextField.getText();                            // takes user's answer
            while (parser.hasMoreTokens()) {
                oneOfPossibleAnswers = parser.nextToken();                          // takes one of the correct answers
                oneOfPossibleAnswers = oneOfPossibleAnswers.replaceAll("\\s","");   // ?
                if (!userAnswer.isEmpty())
                    userAnswer = userAnswer.replaceAll("\\s","");                   // ?
                /**
                 * If user's answer is correct
                 */
                if (oneOfPossibleAnswers.equals(userAnswer)) {                          // if one of the correct answers is a user's answer
                    foundCorrectAnswer = true;                                          // correct answer found
                    correctWordImage = new Image(imgURL + "/checkmark.png");                 // gets an image "checkmark"
                    wordStatusLabel.setText("Correct!");                                // shows "Correct!"
                    wordStatusLabel.setId("green");                                     // paints "Correct!" green
                    wordStatusImage.setImage(correctWordImage);                         // set the image
                    break;                                                              // stop the cycle
                }
            }
            /**
             * If user's answer is incorrect
             */
            if (!foundCorrectAnswer) {
                errCount++;                                                             // one more error found
                wrongWordList.add(currentWord);
                wrongWordImage = new Image(imgURL + "/x-mark.png");                          // gets an image "x-mark"
                wordStatusLabel.setText("Wrong!");                                      // shows "Wrong!"
                wordStatusLabel.setId("red");                             // paints "Wrong!" red
                wordStatusImage.setImage(wrongWordImage);                               // set the image
            }
            /**
             * Shows correct answers
             */
            correctAnswerLabel.setText("Correct answer is: " + currentWord.getLithuanian());
            /**
             * Shows the definition and the example of the current word
             */
            if (currentWord.getDefinition().length() > 1)
                definition.setText(currentWord.getDefinition());
            else
                definition.setText("No definition provided");
            if (currentWord.getExampleSentence().length() > 1)
                example.setText(currentWord.getExampleSentence());
            else
                example.setText("No example sentence provided");
            isShowAnswer = false;
            endOfWordsCatcher();
        }
        /**
         * If LT-ENG, then...
         */
        else {
            correctAnswer = currentWord.getEnglish();
            userAnswer = translationTextField.getText();
            
            /**
             * If user's answer is correct...
             */
            if (userAnswer.equals(correctAnswer)) {
                correctWordImage = new Image(imgURL + "/checkmark.png");                 // gets an image "checkmark"
                wordStatusLabel.setText("Correct!");                                // shows "Correct!"
                wordStatusLabel.setId("green");                      // paints "Correct!" green
                wordStatusImage.setImage(correctWordImage);                         // set the image
            }
            /**
             * If not, add one wrong word to the count
             */
            else {
                errCount++;                                                             // one more error found
                wrongWordList.add(currentWord);
                wrongWordImage = new Image(imgURL + "/x-mark.png");                          // gets an image "x-mark"
                wordStatusLabel.setText("Wrong!");                                      // shows "Wrong!"
                wordStatusLabel.setId("red");                             // paints "Wrong!" red
                wordStatusImage.setImage(wrongWordImage);                               // set the image
            }
            
            /**
             * Shows correct answer
             */
            correctAnswerLabel.setText("Correct answer is: " + correctAnswer);
            /**
             * Shows the definition and the example sentence of the current word
             */
            if (currentWord.getDefinition().length() > 1)
                definition.setText(currentWord.getDefinition());
            else
                definition.setText("No definition provided");
            if (currentWord.getExampleSentence().length() > 1)
                example.setText(currentWord.getExampleSentence());
            else
                example.setText("No example sentence provided");
            isShowAnswer = false;
            endOfWordsCatcher();
        }
    }
    
    /**
     * Checks if it was the last Word, if it was then the next time
 Submit button is pressed, the statistics will be shown. Else
 prepare Submit button for showing the next Word.
     */
    private void endOfWordsCatcher() {
        if (currentWordIndex == wordList.size()) {
            submitButton.setText("Finish");
            showStatistics = true;
        }
        else {
            submitButton.setText("Next Word");
        }
    }
    
    /**
     * Rewrites "Revision.txt" by adding new words from wrongWordList
     */
    private void refreshRevisionDictionary() {
        boolean isRevision = !dictionaryName.equalsIgnoreCase("revision");
        try {
            try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(dictDir + "revision.txt", isRevision), "UTF-8"))) {
                for(Word w:wrongWordList) {
                    writer.write(w.getEnglish() + ".");
                    writer.write(w.getLithuanian() + ".");
                    writer.write(w.getExampleSentence() + ".");
                    writer.write(w.getPartOfSpeech() + ".");
                    writer.write(w.getDefinition() + ".");
                    writer.newLine();
                }
            }
        } catch (IOException ex) {
            System.out.println(ex);
        }
    }
    
    /**
     * Shows statistics of the dictionary
     */
    private void showStatisticsGrid() {
        
        /**
         * The name of the dictionary label
         */
        dictionaryName = dictionary.toString();
        dictionaryName = dictionaryName.substring(0, dictionaryName.length()-4);
        dictionaryName = dictionaryName.toUpperCase();
        Label dictionaryNameLabel = new Label(dictionaryName);
        dictionaryNameLabel.getStyleClass().add("dictionaryName");
        
        /**
         * Words, correct and wrong answers count
         */
        Label wordsCountLabel = new Label("Words count:");
        Text wordsCountText = new Text(String.valueOf(wordsCount));
        Label correctAnswersLabel = new Label("Correct answers:");
        Text correctAnswersCountText = new Text(String.valueOf(wordsCount-errCount));
        Label wrongAnswersLabel = new Label("Wrong answers:");
        Text wrongAnswersCountText = new Text(String.valueOf(errCount));
        
        /**
         * The view for a grade of the dictionary
         */
        Circle markCircle = new Circle(40, Color.web("#fff"));
        markCircle.setStrokeType(StrokeType.OUTSIDE);
        markCircle.setStroke(Color.web("#1B9A91"));
        markCircle.setStrokeWidth(4);
        Text markText = new Text(String.valueOf((wordsCount-errCount) * 100 / wordsCount) + "%");
        markText.getStyleClass().add("mark");
        markText.setBoundsType(TextBoundsType.VISUAL);
        StackPane mark = new StackPane();
        mark.getChildren().addAll(markCircle, markText);
        
        /**
         * The places in the grid where objects will appear
         */
        GridPane.setConstraints(dictionaryNameLabel, 0, 0);
        GridPane.setConstraints(wordsCountLabel, 0, 1);
        GridPane.setConstraints(wordsCountText, 1, 1);
        GridPane.setConstraints(correctAnswersLabel, 0, 2);
        GridPane.setConstraints(correctAnswersCountText, 1, 2);
        GridPane.setConstraints(wrongAnswersLabel, 0, 3);
        GridPane.setConstraints(wrongAnswersCountText, 1, 3);
        GridPane.setConstraints(mark, 2, 4);
        
        /**
         * The GridPane for all the fields (textareas, labels and
         * textfields)
         */
        GridPane statistics = new GridPane();
        statistics.getStyleClass().add("statistics");
        statistics.setPadding(new Insets(50, 10, 50, 10));
        statistics.setVgap(8);
        statistics.setHgap(20);
        statistics.setPrefWidth(450);
        statistics.setAlignment(Pos.CENTER);
        statistics.getChildren().addAll(dictionaryNameLabel, wordsCountLabel,
                                wordsCountText, correctAnswersLabel, 
                                correctAnswersCountText, wrongAnswersLabel,
                                wrongAnswersCountText, mark);
        
        setLeft(null);
        setCenter(statistics);
        setBottom(null);
        refreshRevisionDictionary();  
    }
}