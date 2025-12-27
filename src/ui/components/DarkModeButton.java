package ui.components;

import java.awt.Color;
import java.awt.Image;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;

public class DarkModeButton {
    private JButton darkModeBtn;
    private ImageIcon sun, moon;
    private boolean isDark = false;

    public DarkModeButton(JPanel panel){
        darkModeBtn = new JButton();
        toggleMode(panel);

        darkModeBtn.setIcon(sun);
        darkModeBtn.addActionListener(e ->  toggleMode(panel));
        panel.add(darkModeBtn);
    }

    private void loadIcons() {
        ImageIcon sunImg = new ImageIcon("resources/img/sun.png");
        sun = new ImageIcon(sunImg.getImage().getScaledInstance(16, 16, Image.SCALE_SMOOTH));

        ImageIcon moonImg = new ImageIcon("resources/img/moon.png");
        moon = new ImageIcon(moonImg.getImage().getScaledInstance(16, 16, Image.SCALE_SMOOTH));
    }

    private void toggleMode( JPanel panel){
        if(isDark){
            panel.setBackground(Color.WHITE);
            darkModeBtn.setIcon(moon);
        } else{
            panel.setBackground(Color.WHITE);
            darkModeBtn.setIcon(sun);
        }
        isDark = !isDark;
    }
}
