package words;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

/**
 * Dictionaries - List of Names of the Dictionaries without (.txt)
 * Saved in "dicts.txt" file
 */
public class Dictionaries extends ArrayList<String> {
    private Path p;
    
    /**
     * When Dictionaries instance is created a list of dictionaries
     * are read from "dicts.txt" file
     */
    public Dictionaries() {
        String path = MenuWindow.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        p = Paths.get(URI.create("file:" + path));
        p = p.getParent().getParent();
        
        readDicts();
    }
    
    /**
     * A method which returns a list of Dictionaries without
     * the Revision dictionary
     * @return 
     */
    public ArrayList<String> getCustomDicts() {
        ArrayList<String> customDicts = new ArrayList<String>();
        this.stream().filter((d) -> (!d.equals("Revision"))).forEach((d) -> {
            customDicts.add(d);
        });
        return customDicts;
    }
    
    /**
     * The extension of contains method of ArrayList class which
     * returns True if there is a dictionary named like Object e
     * and False otherwise
     * @param e
     * @return 
     */
    public boolean contains(Object e)
    {
        String name = e.toString().toUpperCase();
        name = name.substring(0, 1) + name.substring(1).toLowerCase();
        return indexOf(name) != -1;
    }
    
    /**
     * Deletes the dictionary with index i from list of dictionaries 
     * @param i
     */
    public void deleteDict(int i) {
        String fileName;
        i++;                    // because customDicts doesn't include "Revision"
        fileName = this.get(i).toLowerCase();
        File fileToDelete = new File(p + "/Dictionaries/" + fileName + ".txt");
        if (fileToDelete.delete()) {
            this.remove(i);
        }
        else {
            InfoBoxProvider error = new InfoBoxProvider("File cannot be deleted!", "Error");
        }
        refreshDicts();
    }
    
    /**
     * Read dictionaries from "dicts.txt" file and save to a new list
     */
    private void readDicts() {
        //System.out.println("---" + p);
        try {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(p + "/Dictionaries/dicts.txt"), "UTF-8"))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    this.add(line);
                }
            }
        } catch (IOException ex) {
            InfoBoxProvider error = new InfoBoxProvider("Couldn't read the dictionaries!", "Error");
        }
    }
    
    /**
     * Refreshes the dictionary list by writing the new list over
     * the old one in "dicts.txt" file
     */
    public void refreshDicts() {
        try {
            try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(p + "/Dictionaries/dicts.txt"), "UTF-8"))) {
                 for(String d:this) {
                            writer.write(d);
                            writer.newLine();
                        }
            }
        } catch (IOException ex) {
            InfoBoxProvider error = new InfoBoxProvider("Dictionary Creation Failed!", "Error");
        }
    }
    
    /**
     * Renames the name of the dicitonary which index is i by a 
     * new name newName
     * @param i
     * @param newName
     */
    public void renameDict(int i, String newName) {
        String fileName;
        i++;                    // because customDicts doesn't include "Revision"
        fileName = this.get(i).toLowerCase();
        File fileToDelete = new File(p + "/Dictionaries/" + fileName + ".txt");
        File newFile = new File(p + "/Dictionaries/" + newName.toLowerCase() + ".txt");
        copyDictionaryFile(newFile, fileToDelete);
        if (fileToDelete.delete()) {
            this.add(i, newName);
            this.remove(i+1);   
        }
        else {
            InfoBoxProvider error = new InfoBoxProvider("File cannot be renamed!", "Error");
        }
        refreshDicts();
    }
    
    /**
     * Copies dictionary file file1 in a place of file2
     * @param file1
     * @param file2
     */
    public void copyDictionaryFile(File file1, File file2) {
       try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file2), "UTF-8"));
                try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file1), "UTF-8"))) {
                        
                    String line;
                    while ((line = reader.readLine()) != null) {
                            writer.write(line);
                            writer.newLine();
                    }
                    reader.close();
                }
        } catch(Exception ex) {
                InfoBoxProvider error = new InfoBoxProvider("File cannot be copied!", "Error");
        }	
    }
}