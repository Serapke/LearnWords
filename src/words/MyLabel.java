/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package words;

import javafx.scene.control.Label;

/**
 *
 * @author Mantas
 */
public class MyLabel extends Label {
    public MyLabel(String name, String cssClass) {
        super(name);
        super.getStyleClass().add(cssClass);
    }
    public void adjustLabel(String name, String cssClass) {
        super.setText(name);
        super.getStyleClass().add(cssClass);
    }
}
