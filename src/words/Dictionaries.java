package words;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

/**
 * Dictionaries - List of Names of the Dictionaries without (.txt)
 * Saved in "dicts.txt" file
 */
public class Dictionaries extends ArrayList<String> {
    /**
     * When Dictionaries instance is created a list of dictionaries
     * are read from "dicts.txt" file
     */
    
    private String dictDir;
    
    public Dictionaries() {
        File jarFile = new File(this.getClass().getProtectionDomain().getCodeSource().getLocation().getPath());
        dictDir = jarFile.getParent() + "/Dictionaries/";
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
        Path path = Paths.get(dictDir + fileName + ".txt");
        try {
            Files.delete(path);
            this.remove(i);
            refreshDicts();
        } catch (NoSuchFileException x) {
            System.err.format("%s: no such" + " file or directory%n", path);
        } catch (DirectoryNotEmptyException x) {
            System.err.format("%s not empty%n", path);
        } catch (IOException x) {
            // File permission problems are caught here.
            System.err.println(x);
        }
    }
    /**
     * Read dictionaries from "dicts.txt" file and save to a new list
     */
    private void readDicts() {
        try {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(dictDir + "dicts.txt"), "UTF-8"))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    this.add(line);
                }
            } catch (FileNotFoundException ex) {
                System.out.println(ex.getMessage());
            }
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
            // Create new dicts.txt and revision.txt files 
        }
    }
    
    /**
     * Refreshes the dictionary list by writing the new list over
     * the old one in "dicts.txt" file
     */
    public void refreshDicts() {
        try {
            try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(dictDir  + "dicts.txt"), "UTF-8"))) {
                 for(String d:this) {
                            writer.write(d);
                            writer.newLine();
                        }
            }
        } catch (IOException ex) {
            System.out.println(ex);
        }
    }
    
    /**
     * Renames the name of the dictionary which index is i by a 
     * new name newName
     * @param i
     * @param newName
     */
    public void renameDict(int i, String newName) {
        String fileName;
        i++;                    // because customDicts doesn't include "Revision"
        fileName = this.get(i).toLowerCase();
        String fileDelete = dictDir + fileName + ".txt";
        File fileToDelete = new File(fileDelete);
        String fileNew = dictDir + newName.toLowerCase() + ".txt";
        File newFile = new File(fileNew);
        Path path = fileToDelete.toPath();
        copyDictionaryFile(newFile, fileToDelete);
        try {
            Files.delete(path);
            this.remove(i);
            this.add(newName);
            refreshDicts();
        } catch (NoSuchFileException x) {
            System.err.format("%s: no such" + " file or directory%n", path);
        } catch (DirectoryNotEmptyException x) {
            System.err.format("%s not empty%n", path);
        } catch (IOException x) {
            // File permission problems are caught here.
            System.err.println(x);
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
                System.out.println(ex);
        }	
    }
}