/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package words;

/**
 * A class for a Word
 * @author Mantas
 */
public class Word {

    // Things which form a Word
    private String eng;         // Word in English
    private String lith;        // Word in Lithuanian
    private String ex;          // example of Word in a sentence
    private String pos;         // part of speech
    private String def;         // definition in English
                                // pronunciation
                                // Word's icon

    // Main constructor

    /**
     *  Makes a word from 5 strings. If any of them contains a dot then string
     * is splitted into two strings and the first one is taken to make a word
     * @param a - English translation of the word
     * @param b - Lithuanian translation of the word
     * @param c - Example sentence
     * @param d - Part of speech
     * @param e - Word's definition
     */
    public Word(String a, String b, String c, String d, String e) {
        if (a.contains(".")) {
            String[] normalizedEng = a.split("\\.");
            eng = normalizedEng[0];
        }
        else
            eng = a;
        if (b.contains(".")) {
            String[] normalizedLith = b.split("\\.");
            lith = normalizedLith[0];
        }
        else
            lith = b;
        if (c.contains(".")) {
            String[] normalizedEx = c.split("\\.");
            System.out.print(normalizedEx[0]);
            ex = normalizedEx[0];
        }
        else
            ex = c;

        pos = d;

        if (c.contains(".")) {
            String[] normalizedDef = e.split("\\.");
            def = normalizedDef[0];
        }
        else
            def = e;
    }

    /**
     * Sets and gets English translation of the Word
     * @param a
     */
    public void setEnglish(String a) {
        eng = a;
    }
    public String getEnglish() {
        return eng;
    }

    /**
     * Sets and gets Lithuanian translation of the Word
     * @param a
     */
    public void setLithuanian(String a) {
        lith = a;
    }
    public String getLithuanian() {
        return lith;
    }

    /**
     * Sets and gets an example sentence of the Word
     * @param a
     */
    public void setExampleSentence(String a) {
        ex = a;
    }
    public String getExampleSentence() {
        return ex;
    }

    /**
     * Sets and gets a part of speech of the Word
     * @param a
     */
    public void setPartOfSpeech(String a) {
        pos = a;
    }
    public String getPartOfSpeech() {
        return pos;
    }

    /**
     * Sets and gets a definition of the Word
     * @param a
     */
    public void setDefinition(String a){
        def = a;
    }
    public String getDefinition() {
        return def;
    }
}
