/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pacssid.view;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.Timer;

/**
 *
 * @author Roberto Carvalho
 */
public class BarraProgresso extends JFrame {

    JProgressBar barraProgresso = new JProgressBar();
    private Timer t;
    private final ActionListener ac;
    int x = 0;
    String valor;
    public void atualiza(int valor) {
        barraProgresso.setValue(valor);
    }

    public BarraProgresso() {
        super("Carregando");
        Container tela = getContentPane();
        Toolkit tk = Toolkit.getDefaultToolkit();
        Dimension d = tk.getScreenSize();
        super.setLocationRelativeTo(null);
        super.setVisible(true);
        super.setLayout(null);
        super.setBounds(d.width / (100) * 40, d.height / (100) * 50, 300, 100);
        super.setResizable(false);
        
        ImageIcon imagemTituloJanela = new ImageIcon("src/pacssid/icons/Sem título.png");
        setIconImage(imagemTituloJanela.getImage());

        barraProgresso = new JProgressBar();
        tela.add(barraProgresso);
        //barraProgresso.setString("Imprimindo");
        barraProgresso.setStringPainted(true);
        barraProgresso.setBounds(10, 10, 270, 20);
        barraProgresso.setVisible(true);
        ac = (ActionEvent e) -> {
            x = x + 1;            
            barraProgresso.setValue(x);
            if (barraProgresso.getValue() == 100) {
                valor = String.valueOf(x);
                barraProgresso.setString(valor);
                dispose();
                JOptionPane.showMessageDialog(null, "Impressão realizada com sucesso.", "Mensagem", JOptionPane.PLAIN_MESSAGE, new ImageIcon("src/pacssid/icons/if_Print_1493286.png"));
                t.stop();
            }
        };
        t = new Timer(7, ac);
        t.start();

    }
}
