/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pacssid.view;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 *
 * @author Roberto Carvalho
 */
public final class Layout extends JFrame {
    public JPanel painel;
    public JButton btnImprimir;
    public int qtd, nomeArquivo, i = 0, uniSelect = 0;
    public JLabel img1, img2, img3, img4, img5, img6;

    public Layout() {
        setTitle(" AgiPrint | Sistema de Impressão por Demanda  |  Versão: 1.2");
        Container tela = getContentPane();
        Toolkit tk = Toolkit.getDefaultToolkit();
        Dimension d = tk.getScreenSize();
        super.setSize(d.width, d.height);
        super.setLocationRelativeTo(null);
        super.setVisible(true);
        super.setLayout(null);
        super.setExtendedState(MAXIMIZED_BOTH);
        super.setResizable(true);
        
        painel = new JPanel();
        tela.add(painel);
        painel.setBounds(0, 0, d.width, d.height);
        painel.setLayout(null);
        painel.setBackground(new Color(227, 232, 245));
        painel.setVisible(true);
        
        btnImprimir = new JButton("Imprimir");
        painel.add(btnImprimir);
        btnImprimir.setBounds(d.width / (100) * 80, d.height/(100)*7,  d.width / (100) * 8, d.height/(100)*4);
        btnImprimir.setVisible(true);
    }
    @Override
    public void paint(Graphics g) {
         Toolkit tk = Toolkit.getDefaultToolkit();
         Dimension d = tk.getScreenSize();
        try {
            BufferedImage bloco1 = ImageIO.read(new File("src/pacssid/temp/1.jpg"));
            g.drawImage(bloco1, d.width / (100) * 31, d.height/(100)*10,  d.width / (100) * 21, d.height/(100)*30, null);
            
            BufferedImage bloco2 = ImageIO.read(new File("src/pacssid/temp/2.jpg"));
            g.drawImage(bloco2, d.width / (100) * 53, d.height/(100)*10, d.width / (100) * 21, d.height/(100)*30, null);
            
            BufferedImage bloco3 = ImageIO.read(new File("src/pacssid/temp/3.jpg"));
            g.drawImage(bloco3, d.width / (100) * 31, d.height/(100)*41, d.width / (100) * 21, d.height/(100)*30, null);
            
            BufferedImage bloco4 = ImageIO.read(new File("src/pacssid/temp/4.jpg"));
            g.drawImage(bloco4, d.width / (100) * 53, d.height/(100)*41,d.width / (100) * 21, d.height/(100)*30, null);
            
            BufferedImage bloco5 = ImageIO.read(new File("src/pacssid/temp/5.jpg"));
            g.drawImage(bloco5, d.width / (100) * 31, d.height/(100)*72, d.width / (100) * 21, d.height/(100)*30, null);
            
            BufferedImage bloco6 = ImageIO.read(new File("src/pacssid/temp/6.jpg"));
            g.drawImage(bloco6, d.width / (100) * 53, d.height/(100)*72, d.width / (100) * 21, d.height/(100)*30, null);

        } catch (IOException ex) {
            Logger.getLogger(Layout.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    public static void main(String args[]) {
        Layout tela = new Layout();
        tela.setVisible(true);
        tela.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

}
