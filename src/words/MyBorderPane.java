/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package words;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

/**
 *
 * @author Mantas
 */
public class MyBorderPane extends BorderPane {
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
}
