/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pacssid.main;

/**
 *
 * @author roberto
 */
import java.awt.FontFormatException;
import java.io.IOException;
import javax.swing.JFrame;
import pacssid.view.Index;

/**
 *
 * @author roberto
 */
public class Main {

    /**
     * @param args the command line arguments
     * @throws java.awt.FontFormatException
     * @throws java.io.IOException
     */
    public static void main(String[] args) throws FontFormatException, IOException {
       Index tela = new Index();
       tela.setVisible(true);
       tela.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
    
}