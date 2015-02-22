/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package words;

import javax.swing.JOptionPane;

/**
 *
 * @author Mantas
 */
public class InfoBoxProvider {
    public InfoBoxProvider(String infoMessage, String titleBar) {
        JOptionPane.showMessageDialog(null, infoMessage, "InfoBox: " + titleBar, JOptionPane.INFORMATION_MESSAGE);
    } 
}
