package ui;

import javax.swing.JFrame;
import javax.swing.JPanel;

import java.awt.*;

import util.FrameUtil;

public class AccountSettingsFrame extends JFrame {
    private AccountSettingsFrame(){
        initializeFrame();

    }

    private void initializeFrame(){
        setTitle("Eshopping - Settings");
        setSize(1200, 750);
        setResizable(false);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(new Color(250, 250, 252));
        
        FrameUtil.addCloseConfirmation(this);
    }

    private void initializeComponents(){
        //JPanel mainContainer = new JPanel(new GridBagLayout(250, 250, 252));
    }


}
