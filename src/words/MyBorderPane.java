/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package words;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.StringTokenizer;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

/**
 *
 * @author Mantas
 */
public class MyBorderPane extends BorderPane {
    File jarFile = new File(this.getClass().getProtectionDomain().getCodeSource().getLocation().getPath());
    private final String dictDir = jarFile.getParent() + "/Dictionaries/";
    public MyBorderPane() {
        
    }
    public void showSavedView() {
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
    @SuppressWarnings({"Convert2Diamond", "ConvertToTryWithResources"})
    public final ArrayList<Word> loadDictionary(File dictionary) {
        ArrayList<Word> wordList = new ArrayList<Word>();                         // # of words in a dictionary
        try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(dictDir+ dictionary), "UTF-8"));
                String line;
                while ((line = reader.readLine()) != null) {
                        makeWord(line, wordList);
                }	
                reader.close();
        } catch(Exception ex) {
            System.out.println(ex);
        }        
        return wordList;
    }
    
    private void makeWord(String lineToParse, ArrayList<Word> wordList) {
        StringTokenizer parser = new StringTokenizer(lineToParse, ".");
        if (parser.hasMoreTokens()) {
           Word nWord = new Word(parser.nextToken(), parser.nextToken(), parser.nextToken(), parser.nextToken(), parser.nextToken());
           wordList.add(nWord);
        }
    }
}
