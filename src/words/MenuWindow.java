/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package words;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Popup;
import javafx.stage.Stage;

/**
 *
 * @author Mantas
 */
public class MenuWindow extends Application {

    private Scene programView;          // Program view
    private VBox mainMenu;              // Main Menu view

    private MenuBar menu;
    private MenuItem home;

    private final Dictionaries dicts = new Dictionaries();  // the list of dictonaries
    private int selectedDictionaryID;                       // the ID of the dictionary selected (used in renaming and deleting of dictionary)

    java.net.URL imgURL = MenuWindow.class.getResource("Images");
    @Override
    public void start(Stage primaryStage) {

        //String path = MenuWindow.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        //p = Paths.get(URI.create("file:" + path));
       
        programView = new Scene(new VBox(), 450, 340);

        /**
         * MenuBar
         *      File
         *          Home
         *          Change Dictionary
         *          Rename Dictionary
         *          Delete Dictionary
         *          Exit
         *      Help
         *          Online Support
         *          Keyboard Shortcuts
         *          About
         */
        MenuBar menu = new MenuBar();
            Menu file = new Menu("File");
            Menu help = new Menu ("Help");
                /**
                 * Home
                 *      In Main Menu (or Home) view - disabled
                 *      On Action: opens Main Menu
                 */
                //home = new MenuItem("Home", new ImageView(new Image(imgURL + "/home.png")));
                home = createMenuItem("Home", "home.png", "CTRL+H");
                home.setDisable(true);
                home.setOnAction((ActionEvent e) -> {
                    PrepareView();
                    ((VBox) programView.getRoot()).getChildren().add(mainMenu);
                });
                /**
                 * Change Dictionary
                 *      On Action:  creates an instance of a class ChooseDictionaryScene with option keyword "change",
                 *                  gives the instance the list of dictionaries and Program view
                 */
                MenuItem changeDictionary = createMenuItem("Change Dictionary", "change.png", "CTRL+ALT+C");
                changeDictionary.setOnAction((ActionEvent e) -> {
                    ChooseDictionaryScene chooseDicScene = new ChooseDictionaryScene(dicts, "change", programView);
                    PrepareView();
                    ((VBox) programView.getRoot()).getChildren().add(chooseDicScene);
                });
                /**
                 * Rename Dictionary
                 *      On Action:  Creates a Popup with
                 *                      Label "Choose Dictionary to be Renamed"
                 *                      ChoiceBox with the list of Dictionaries
                 *                      Label "New Dictionary Name"
                 *                      TextField to enter a new dictionary name
                 *                      Submit and Cancel buttons
                 *
                 */
                MenuItem renameDictionary = createMenuItem("Rename Dictionary", "rename.png", "CTRL+ALT+R");
                renameDictionary.setOnAction((ActionEvent e) -> {
                    final Popup popup = new Popup();
                    VBox pop = new VBox();
                    pop.getStyleClass().add("pop");

                    Button submitButton = new Button("Submit");
                    submitButton.setDisable(true);          // Submit button is disabled until no dictionary is chosen and new name is not entered

                    Label newDictionaryNameLabel = new Label("New Dictionary Name");
                    newDictionaryNameLabel.getStyleClass().add("popupLabel");

                    TextField newNameTextField = new TextField();
                    newNameTextField.textProperty().addListener((final ObservableValue<? extends String> observable, final String oldValue, final String newValue) -> {
                        if (newNameTextField.getText().isEmpty()) {
                            submitButton.setDisable(true);
                        }
                        else if ((!newNameTextField.getText().isEmpty()) && (selectedDictionaryID > 0)) {
                            submitButton.setDisable(false);
                        }
                    });

                    /**
                     * The box of two buttons (Submit and Cancel)
                     * Displayed horizontally
                     */
                    submitButton.setDefaultButton(true);                // fires when Enter is pressed
                    submitButton.setOnAction((ActionEvent f) -> {       // if Submit pressed - rename the chosen dictionary and hide Popup
                        if (!dicts.contains(newNameTextField.getText())) {
                            dicts.renameDict(selectedDictionaryID, newNameTextField.getText());
                            popup.hide();
                        } else {
                            System.out.println("Error! Dictionary with such a name already exists!");
                        }
                    });

                    Button cancelButton = new Button("Cancel");
                    cancelButton.setCancelButton(true);                 // fires when ESC is pressed
                    cancelButton.setOnAction((ActionEvent f) -> {       // if Cancel pressed - hide Popup
                       popup.hide();
                    });

                    HBox buttons = new HBox();
                    buttons.setAlignment(Pos.BASELINE_CENTER);
                    buttons.setSpacing(10);
                    buttons.getChildren().addAll(submitButton, cancelButton);

                    Label deleteDictionaryLabel = new Label("Choose Dictionary to be Renamed");
                    deleteDictionaryLabel.getStyleClass().add("popupLabel");

                    ChoiceBox chooseDictionaryBox = new ChoiceBox();
                    chooseDictionaryBox.getItems().addAll(dicts.getCustomDicts());
                    chooseDictionaryBox.getSelectionModel().selectFirst();
                    chooseDictionaryBox.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
                        @SuppressWarnings("override")
                        public void changed(ObservableValue ov, Number value, Number new_value)  {
                            if (new_value.intValue() > 0) {
                                selectedDictionaryID = new_value.intValue();
                                if (!newNameTextField.getText().isEmpty())
                                    submitButton.setDisable(false);
                            }
                            else if (new_value.intValue() == 0) {
                                submitButton.setDisable(true);
                            }
                        }
                    });

                    pop.getChildren().addAll(deleteDictionaryLabel, chooseDictionaryBox, newDictionaryNameLabel, newNameTextField, buttons);
                    pop.setSpacing(10);
                    pop.setAlignment(Pos.BASELINE_CENTER);
                    popup.getContent().add(pop);
                    popup.show(primaryStage);
                });
                /**
                 * Delete Dictionary
                 *      On Action:  Creates a Popup with
                 *                      Label "Choose Dictionary to be Deleted"
                 *                      ChoiceBox with the list of Dictionaries
                 *                      Submit and Cancel buttons
                 *
                 */
                MenuItem deleteDictionary = createMenuItem("Delete Dictionary", "delete.png", "Ctrl+Alt+D");
                deleteDictionary.setOnAction((ActionEvent e) -> {
                    final Popup popup = new Popup();
                    VBox pop = new VBox();
                    pop.getStyleClass().add("pop");

                    HBox buttons = new HBox();

                    Button submitButton = new Button("Submit");
                    submitButton.setDisable(true);  // Submit button is disabled until no dictionary is chosen
                    submitButton.setDefaultButton(true);
                    submitButton.setOnAction((ActionEvent f) -> {   // if Submit pressed - deletes the chosen dictionary and hide Popup
                       dicts.deleteDict(selectedDictionaryID);
                       popup.hide();
                    });

                    Button cancelButton = new Button("Cancel");
                    cancelButton.setCancelButton(true);
                    cancelButton.setOnAction((ActionEvent f) -> {
                       popup.hide();
                    });
                    buttons.setAlignment(Pos.BASELINE_CENTER);
                    buttons.setSpacing(10);
                    buttons.getChildren().addAll(submitButton, cancelButton);

                    Label deleteDictionaryLabel = new Label("Choose Dictionary to be Deleted");
                    deleteDictionaryLabel.getStyleClass().add("popupLabel");

                    ChoiceBox chooseDictionaryBox = new ChoiceBox();
                    chooseDictionaryBox.getItems().addAll(dicts.getCustomDicts());
                    chooseDictionaryBox.getSelectionModel().selectFirst();
                    chooseDictionaryBox.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
                        @SuppressWarnings("override")
                        public void changed(ObservableValue ov, Number value, Number new_value)  {
                            if (new_value.intValue() > 0) {
                                selectedDictionaryID = new_value.intValue();
                                submitButton.setDisable(false);
                            }
                            else if (new_value.intValue() == 0) {
                                submitButton.setDisable(true);
                            }
                        }
                    });
                    pop.getChildren().addAll(deleteDictionaryLabel, chooseDictionaryBox, buttons);
                    pop.setSpacing(10);
                    pop.setAlignment(Pos.BASELINE_CENTER);
                    popup.getContent().add(pop);
                    popup.show(primaryStage);
                });
                /**
                 * Exit
                 *      On Action: closes the program
                 */
                MenuItem exit = createMenuItem("Exit", "log-out.png", "Ctrl+Q");
                    exit.setOnAction((ActionEvent e) -> {
                        System.exit(0);
                    });
                /**
                * Online Support
                *       On Action: goes to LearnWords blog
                */
                MenuItem onlineSupport = createMenuItem("Online Support", "online.png", "Ctrl+O");
                onlineSupport.setOnAction((ActionEvent e) -> {
                    Hyperlink link = new Hyperlink();
                    link.setOnAction(new EventHandler<ActionEvent>() {
                        final WebView browser = new WebView();
                        final WebEngine webEngine = browser.getEngine();
                        public void handle(ActionEvent e) {
                            //try {
                             //   System.out.println("WEB");
                                //Desktop.getDesktop().browse(new URI("file:///C:/Users/Mantas/OneDrive/IT/WEB/projektai/LearnWords/blog.html"));
                            //} catch (IOException | URISyntaxException ex) {
                              //  Logger.getLogger(MenuWindow.class.getName()).log(Level.SEVERE, null, ex);
                            //}
                        }
                    });
                    link.fire();
                });
                /**
                 * Keyboard Shortcuts
                 *      On Action: shows a Popup with keyboard shortcuts used in program
                 */
                MenuItem keyboardShortcuts = createMenuItem("Keyboard Shortcuts", "shortcut.png", "");
                keyboardShortcuts.setOnAction((ActionEvent e) -> {

                    final Popup popup = new Popup();
                    GridPane pop = new GridPane();
                    pop.getStyleClass().add("pop");

                    Text homeShortcut = new Text("Home");
                    Text homeShortcutKey = new Text("Ctrl + H");

                    Text quitShortcut = new Text("Quit");
                    Text quitShortcutKey = new Text("Ctrl + Q");

                    Text renameShortcut = new Text("Rename Dictionary");
                    Text renameShortcutKey = new Text("Ctrl + Alt + R");

                    Text changeShortcut = new Text("Change Dictionary");
                    Text changeShortcutKey = new Text("Ctrl + Alt + C");

                    Text deleteShortcut = new Text("Delete Dictionary");
                    Text deleteShortcutKey = new Text("Ctrl + Alt + D");

                    GridPane.setConstraints(homeShortcut, 0, 0);
                    GridPane.setConstraints(homeShortcutKey, 1, 0);
                    GridPane.setConstraints(quitShortcut, 0, 1);
                    GridPane.setConstraints(quitShortcutKey, 1, 1);
                    GridPane.setConstraints(renameShortcut, 0, 2);
                    GridPane.setConstraints(renameShortcutKey, 1, 2);
                    GridPane.setConstraints(changeShortcut, 0, 3);
                    GridPane.setConstraints(changeShortcutKey, 1, 3);
                    GridPane.setConstraints(deleteShortcut, 0, 4);
                    GridPane.setConstraints(deleteShortcutKey, 1, 4);


                    pop.getChildren().addAll(homeShortcut, homeShortcutKey,
                                             quitShortcut, quitShortcutKey,
                                             renameShortcut, renameShortcutKey,
                                             changeShortcut, changeShortcutKey,
                                             deleteShortcut, deleteShortcutKey);
                    pop.setHgap(70);
                    pop.setVgap(10);
                    pop.setAlignment(Pos.BASELINE_CENTER);
                    popup.getContent().add(pop);
                    popup.show(primaryStage);
                });
                // MenuItem checkForUpdates = new MenuItem("Check for Updates", new ImageView(new Image("file:check-updates.png")));
                /**
                 * About
                 *      On Action: goes to LearnWords main page
                 */
                MenuItem about = createMenuItem("About", "about.png", "");
                about.setOnAction((ActionEvent e) -> {
                    Hyperlink link = new Hyperlink();
                    link.setOnAction(new EventHandler<ActionEvent>() {
                        final WebView browser = new WebView();
                        final WebEngine webEngine = browser.getEngine();
                        public void handle(ActionEvent e) {
                           // try {
                            //    Desktop.getDesktop().browse(new URI("file:///C:/Users/Mantas/OneDrive/IT/WEB/projektai/LearnWords/index.html"));
                            //} catch (IOException | URISyntaxException ex) {
                            //    Logger.getLogger(MenuWindow.class.getName()).log(Level.SEVERE, null, ex);
                            //}
                        }
                    });
                    link.fire();
                });

        menu.getMenus().addAll(file, help);
        file.getItems().addAll(home, changeDictionary, renameDictionary, deleteDictionary, new SeparatorMenuItem(), exit);
        help.getItems().addAll(onlineSupport, keyboardShortcuts, about);


        /**
         * Main Menu (mainMenu) components
         */

        Label programViewTitle = new Label();
        programViewTitle.setText("Learn Your Words!");
        programViewTitle.setId("title");

        /**
         * Start Learning Button
         *      Css class: menuButtons
         *      When pressed:   creates an instance of a class ChooseDictionaryScene with option keyword "start",
         *                      gives the instance the list of dictionaries and Program view
         */
        Button sDictionaryButton = new Button();
        sDictionaryButton.setText("Start Learning");
        sDictionaryButton.getStyleClass().add("menuButtons");
        sDictionaryButton.setOnAction((ActionEvent event) -> {
            ChooseDictionaryScene chooseDicScene = new ChooseDictionaryScene(dicts, "start", programView);
            PrepareView();
            ((VBox) programView.getRoot()).getChildren().add(chooseDicScene);
        });

        /**
         * Create New Dictionary Button
         *      Css class: menuButtons
         *      When pressed:   creates an instance of a class CreateDictionaryScene,
         *                      gives the instance the list of dictionaries and Program view
         */
        Button cDictionaryButton = new Button();
        cDictionaryButton.setText("Create New Dictionary");
        cDictionaryButton.getStyleClass().add("menuButtons");
        cDictionaryButton.setOnAction((ActionEvent event) -> {
            CreateDictionaryScene createDicScene = new CreateDictionaryScene(dicts, programView);
            PrepareView();
            ((VBox) programView.getRoot()).getChildren().add(createDicScene);
        });

        /**
         * Add New Words Button
         *      Css class: menuButtons
         *      When pressed:   creates an instance of a class ChooseDictionaryScene with option keyword "add",
         *                      gives the instance the list of dictionaries and Program view
         */
        Button aDictionaryButton = new Button();
        aDictionaryButton.setText("Add New Words");
        aDictionaryButton.getStyleClass().add("menuButtons");
        aDictionaryButton.setOnAction((ActionEvent event) -> {
            ChooseDictionaryScene chooseDicScene = new ChooseDictionaryScene(dicts, "add", programView);
            PrepareView();
            ((VBox) programView.getRoot()).getChildren().add(chooseDicScene);
        });

        /**
         * The Main Menu (mainMenu) has:
         *      Program Title
         *      Start Learning Button
         *      Create New Dictionary Button
         *      Add New Words Button
         *
         * The Program has:
         *      Menu
         *      Main Menu (mainMenu)
         */
        mainMenu = new VBox();
        mainMenu.setAlignment(Pos.BASELINE_CENTER);
        mainMenu.setSpacing(10);
        mainMenu.getChildren().addAll(programViewTitle,sDictionaryButton, cDictionaryButton, aDictionaryButton);
        ((VBox) programView.getRoot()).getChildren().addAll(menu, mainMenu);

        /**
         * Title: LearnWords
         * Scene: new VBox(), 450, 340
         * Css link: MainWindow.css
         * Position: Center on Screen
         * Resizable: Not
         */
        primaryStage.setTitle("LearnWords");
        primaryStage.setScene(programView);
        programView.getStylesheets().add(MenuWindow.class.getResource("MainWindow.css").toExternalForm());
        primaryStage.getIcons().add(new Image(imgURL + "/favicon.png"));
        primaryStage.centerOnScreen();
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    /**
     * sets link to Home enabled
     * removes Main Menu view and adds ChooseDictionaryScene instance's view
     */
    private void PrepareView() {
        home.setDisable(false);
        ((VBox) programView.getRoot()).getChildren().remove(1);
    }

    private MenuItem createMenuItem(String name, String img, String keyComb) {
      MenuItem menuItem = new MenuItem(name, new ImageView(new Image(imgURL + "/" + img)));
      if (!keyComb.isEmpty())
        menuItem.setAccelerator(KeyCombination.keyCombination(keyComb));
      return menuItem;
    }
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
}
