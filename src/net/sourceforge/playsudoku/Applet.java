package net.sourceforge.playsudoku;

import java.awt.*;
import java.awt.event.*;
//import java.applet.*;
import javax.swing.*;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */


public class Applet extends Frame {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	boolean isStandalone = false;
    BorderLayout borderLayout1 = new BorderLayout();

    //Get a parameter value
    public String getParameter(String key, String def) {
        return System.getProperty(key, def);
    }

    //Construct the applet
//    public Applet() {
//    }

    //Initialize the applet
    public Applet() {
        try {
            jbInit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Component initialization
    private void jbInit() throws Exception {
        this.setSize(new Dimension(400, 300));
        this.setLayout(borderLayout1);

        net.sourceforge.playsudoku.gui.SudokuMainFrame f =
        new net.sourceforge.playsudoku.gui.SudokuMainFrame();
        f.setVisible(true);

    }

    //Get Applet information
    public String getAppletInfo() {
        return "Applet Information";
    }

    //Get parameter info
    public String[][] getParameterInfo() {
        return null;
    }

    //static initializer for setting look & feel
    static {
        try {
            //UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            //UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception e) {
        }
    }

    public static void main (String[] args) {
	   Applet f = new Applet ();
    }
}