package words;

import javafx.scene.control.Button;



class MyButton extends Button {
    public MyButton(String name, String cssClass, boolean setDisableValue, boolean setDefaultButton) {
            super(name);
            super.getStyleClass().add(cssClass);
            super.setDisable(setDisableValue);
            super.setDefaultButton(setDefaultButton);
    }
}
