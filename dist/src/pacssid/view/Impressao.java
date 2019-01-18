/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pacssid.view;

/**
 *
 * @author roberto
 */
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author roberto
 */
import pacssid.dao.ConectaBanco;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.print.*;
import static java.awt.print.Printable.NO_SUCH_PAGE;
import static java.awt.print.Printable.PAGE_EXISTS;
import java.io.File;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.print.PrintService;

public final class Impressao implements Printable {

    private PrinterJob pjob = null;
    static String imp = "";
    private PrintService impressora;

    public String a, nome, nomequevemdaclasse, codigoquevemdaclasse,
            dataquevemdaclasse, datanascquevemdaclasse, numeroAcesso, quantidadequevemdaclasse,
            procedimentoquevemdaclasse;
    public int qtd, nomeArquivo, i = 0, uniSelect, novovalor;
    public Boolean Sr;

    public void Imprime(Book bk) throws PrinterException {
        PrinterJob job = PrinterJob.getPrinterJob();

        PrintService[] pservices = PrinterJob.lookupPrintServices();
        if (pservices.length > 0) {
            for (PrintService ps : pservices) {
                if (ps.getName().contains(imp)) {
                    impressora = ps;
                    if (impressora != null) {
                        pjob = PrinterJob.getPrinterJob();
                        pjob.setPrintService(impressora);
                        pjob.setPageable(bk);
                    }
                    BarraProgresso barra = new BarraProgresso();
                    pjob.print();
                    barra.setVisible(true);
                    DeletaArquivos();
                    break;
                }
            }
        }
    }

    public void TemSR(String codigo) {
        ConectaBanco banco = new ConectaBanco();
        banco.conexao();
        String sql = "select p.pat_id,p.pat_name,pat_birthdate,p.pat_sex, sr.modality ,s.study_desc,to_char(s.study_datetime,'DD/MM/YYYY'),s.num_instances from patient p, study s,series sr where\n"
                + "s.patient_fk = p.pk and sr.study_fk = s.pk and sr.modality = 'SR' and p.pat_id = '" + codigo + "' order by s.study_datetime desc;";
        try {
            Statement stm = banco.conn.createStatement();
            ResultSet resultado = stm.executeQuery(sql);

            if (resultado.next()) {
                Sr = true;
                qtd = qtd - 1;
            } else {
                qtd = qtd;
            }
            banco.desconecta();
        } catch (SQLException ex) {
            Logger.getLogger(Impressao.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void TemSRAcesso(String acesso) {
        ConectaBanco banco = new ConectaBanco();
        banco.conexao();
        String sql = "select p.pat_id,p.pat_name,pat_birthdate,p.pat_sex, sr.modality ,s.study_desc,to_char(s.study_datetime,'DD/MM/YYYY'),s.num_instances from patient p, study s,series sr where\n"
                + "s.patient_fk = p.pk and sr.study_fk = s.pk and sr.modality = 'SR' and s.accession_no = '" + acesso + "' order by s.study_datetime desc;";
        try {
            Statement stm = banco.conn.createStatement();
            ResultSet resultado = stm.executeQuery(sql);

            if (resultado.next()) {
                Sr = true;
                qtd = qtd - 1;
            }
            banco.desconecta();
        } catch (SQLException ex) {
            Logger.getLogger(Impressao.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void VerCaminhoaAcesso(String acesso) {
        ConectaBanco banco = new ConectaBanco();
        banco.conexao();
        DicomParaJpeg dicom = new DicomParaJpeg();
        String caminho;
        String sql = "SELECT  FL.FILEPATH AS CAMINHO FROM FILES FL, PATIENT PT, STUDY ST, SERIES SR, INSTANCE INS \n"
                + "                WHERE   ST.PATIENT_FK = PT.PK AND\n"
                + "			 SR.STUDY_FK = ST.PK AND\n"
                + "			 FL.INSTANCE_FK = INS.PK AND\n"
                + "			 INS.SERIES_FK = SR.PK AND\n"
                + "                        SR.MODALITY = 'US' AND \n"
                + "                        ST.ACCESSION_NO='" + acesso + "'";

        try {
            Statement stm = banco.conn.createStatement();
            ResultSet resultado = stm.executeQuery(sql);
            while (resultado.next()) {
                i++;
                caminho = resultado.getString("caminho");
                dicom.Converte(caminho, i);
            }
            banco.desconecta();
        } catch (SQLException ex) {
            Logger.getLogger(Impressao.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void VerCaminhoCodigo(String codigo) {
        ConectaBanco banco = new ConectaBanco();
        banco.conexao();
        DicomParaJpeg dicom = new DicomParaJpeg();
        String caminho;
        String sql = "SELECT  FL.FILEPATH AS CAMINHO FROM FILES FL, PATIENT PT, STUDY ST, SERIES SR, INSTANCE INS \n"
                + "                WHERE ST.PATIENT_FK = PT.PK AND\n"
                + "			 SR.STUDY_FK = ST.PK AND\n"
                + "			 FL.INSTANCE_FK = INS.PK AND\n"
                + "			 INS.SERIES_FK = SR.PK AND\n"
                + "                      SR.MODALITY = 'US' AND \n"
                + "                      PT.PAT_ID ='" + codigo + "'";

        try {
            Statement stm = banco.conn.createStatement();
            ResultSet resultado = stm.executeQuery(sql);
            while (resultado.next()) {
                i++;
                caminho = resultado.getString("caminho");
                dicom.Converte(caminho, i);
            }
            banco.desconecta();
        } catch (SQLException ex) {
            Logger.getLogger(Impressao.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    public static double fatorConverMMPt = 2.834646D;

    public Impressao(String nome, String acesso, String codigo, String dataexame, String datanasc, String quantidade,
            int indice, String procedimento, String nomedaImpressora) {

        nomequevemdaclasse = nome;
        codigoquevemdaclasse = codigo;
        dataquevemdaclasse = dataexame;
        datanascquevemdaclasse = datanasc;
        quantidadequevemdaclasse = quantidade;
        procedimentoquevemdaclasse = procedimento;
        imp = nomedaImpressora;
        numeroAcesso = acesso;

        qtd = Integer.parseInt(quantidadequevemdaclasse);
        uniSelect = indice;
        //TemSR(codigo);
        TemSRAcesso(acesso);
    }

    @Override
    public int print(Graphics g, PageFormat pf, int pageIndice) throws PrinterException {

        Graphics2D g2d = (Graphics2D) g;
        g2d.translate(pf.getImageableX(), pf.getImageableY());
        PrinterJob job = PrinterJob.getPrinterJob();
        Cabecalho(g, pf, pageIndice);

        Paper paper = new Paper();
        // Papel A4
        // 8,5 pol.
        double width = 215.9 * fatorConverMMPt;
        // 13 pol.
        double height = 300.2 * fatorConverMMPt;
        paper.setSize(width, height);
        paper.setImageableArea(0.0, 0.0, width, height);
        pf.setPaper(paper);
        Book bk = new Book();
        bk.append(new paginaPrincipal(), pf);
        if (qtd <= 6) {
            MenorQueSete(g, pf, pageIndice);
            Imprime(bk);
        } else if (qtd > 6 && qtd <= 12) {
            bk.append(new paginaPrincipal(), job.defaultPage(), 0);
            switch (qtd) {
                case 7:
                    bk.append(new pagina7(), pf, 1);
                    break;
                case 8:
                    bk.append(new pagina8(), pf, 1);
                    break;
                case 9:
                    bk.append(new pagina9(), pf, 1);
                    break;
                case 10:
                    bk.append(new pagina10(), pf, 1);
                    break;
                case 11:
                    bk.append(new pagina11(), pf, 1);
                    break;
                case 12:
                    bk.append(new pagina12(), pf, 1);
                    break;
            }
            Imprime(bk);
        } else if (qtd > 12 && qtd <= 18) {
            bk.append(new paginaPrincipal(), job.defaultPage(), 0);
            bk.append(new pagina12(), pf, 1);
            switch (qtd) {
                case 13:
                    bk.append(new pagina13(), pf, 1);
                    break;
                case 14:
                    bk.append(new pagina14(), pf, 1);
                    break;
                case 15:
                    bk.append(new pagina15(), pf, 1);
                    break;
                case 16:
                    bk.append(new pagina16(), pf, 1);
                    break;
                case 17:
                    bk.append(new pagina17(), pf, 1);
                    break;
                case 18:
                    bk.append(new pagina18(), pf, 1);
                    break;
            }
            Imprime(bk);
        } else if (qtd > 18 && qtd <= 24) {
            bk.append(new paginaPrincipal(), job.defaultPage(), 0);
            bk.append(new pagina12(), pf, 1);
            bk.append(new pagina18(), pf, 1);
            switch (qtd) {
                case 19:
                    bk.append(new pagina19(), pf, 1);
                    break;
                case 20:
                    bk.append(new pagina20(), pf, 1);
                    break;
                case 21:
                    bk.append(new pagina21(), pf, 1);
                    break;
                case 22:
                    bk.append(new pagina22(), pf, 1);
                    break;
                case 23:
                    bk.append(new pagina23(), pf, 1);
                    break;
                case 24:
                    bk.append(new pagina24(), pf, 1);
                    break;
            }
            Imprime(bk);
        } else if (qtd > 24 && qtd <= 30) {
            bk.append(new paginaPrincipal(), job.defaultPage(), 0);
            bk.append(new pagina12(), pf, 1);
            bk.append(new pagina18(), pf, 1);
            bk.append(new pagina24(), pf, 1);
            switch (qtd) {
                case 25:
                    bk.append(new pagina25(), pf, 1);
                    break;
                case 26:
                    bk.append(new pagina26(), pf, 1);
                    break;
                case 27:
                    bk.append(new pagina27(), pf, 1);
                    break;
                case 28:
                    bk.append(new pagina28(), pf, 1);
                    break;
                case 29:
                    bk.append(new pagina29(), pf, 1);
                    break;
                case 30:
                    bk.append(new pagina30(), pf, 1);
                    break;
            }
            Imprime(bk);
        } else if (qtd > 30 && qtd <= 36) {
            bk.append(new paginaPrincipal(), job.defaultPage(), 0);
            bk.append(new pagina12(), pf, 1);
            bk.append(new pagina18(), pf, 1);
            bk.append(new pagina24(), pf, 1);
            bk.append(new pagina30(), pf, 1);
            switch (qtd) {
                case 31:
                    bk.append(new pagina31(), pf, 1);
                    break;
                case 32:
                    bk.append(new pagina32(), pf, 1);
                    break;
                case 33:
                    bk.append(new pagina33(), pf, 1);
                    break;
                case 34:
                    bk.append(new pagina34(), pf, 1);
                    break;
                case 35:
                    bk.append(new pagina35(), pf, 1);
                    break;
                case 36:
                    bk.append(new pagina36(), pf, 1);
                    break;
            }
            Imprime(bk);
        } else if (qtd > 36 && qtd <= 42) {
            bk.append(new paginaPrincipal(), job.defaultPage(), 0); // # LAYOUT 1 Á 6 
            bk.append(new pagina12(), pf, 1);                    // # LAYOUT 7 Á 12 
            bk.append(new pagina18(), pf, 1);                    // # LAYOUT 13 Á 18 
            bk.append(new pagina24(), pf, 1);                    // # LAYOUT 19 Á 24 
            bk.append(new pagina30(), pf, 1);                    // # LAYOUT 25 Á 32 
            bk.append(new pagina36(), pf, 1);                    // # LAYOUT 33 Á 42 
            switch (qtd) {
                case 37:
                    bk.append(new pagina37(), pf, 1);
                    break;
                case 38:
                    bk.append(new pagina38(), pf, 1);
                    break;
                case 39:
                    bk.append(new pagina39(), pf, 1);
                    break;
                case 40:
                    bk.append(new pagina40(), pf, 1);
                    break;
                case 41:
                    bk.append(new pagina41(), pf, 1);
                    break;
                case 42:
                    bk.append(new pagina42(), pf, 1);
                    break;
            }
            Imprime(bk);
        } else if (qtd > 42 && qtd <= 48) {
            bk.append(new paginaPrincipal(), job.defaultPage(), 0); // # LAYOUT 1 Á 6 
            bk.append(new pagina12(), pf, 1);                    // # LAYOUT 7 Á 12 
            bk.append(new pagina18(), pf, 1);                    // # LAYOUT 13 Á 18 
            bk.append(new pagina24(), pf, 1);                    // # LAYOUT 19 Á 24 
            bk.append(new pagina30(), pf, 1);                    // # LAYOUT 25 Á 32 
            bk.append(new pagina36(), pf, 1);
            bk.append(new pagina42(), pf, 1);
            switch (qtd) {
                case 43:
                    bk.append(new pagina43(), pf, 1);
                    break;
                case 44:
                    bk.append(new pagina44(), pf, 1);
                    break;
                case 45:
                    bk.append(new pagina45(), pf, 1);
                    break;
                case 46:
                    bk.append(new pagina46(), pf, 1);
                    break;
                case 47:
                    bk.append(new pagina47(), pf, 1);
                    break;
                case 48:
                    bk.append(new pagina48(), pf, 1);
                    break;
            }
            Imprime(bk);
        } else if (qtd > 48 && qtd <= 54) {
            bk.append(new paginaPrincipal(), job.defaultPage(), 0); // # LAYOUT 1 Á 6 
            bk.append(new pagina12(), pf, 1);                    // # LAYOUT 7 Á 12 
            bk.append(new pagina18(), pf, 1);                    // # LAYOUT 13 Á 18 
            bk.append(new pagina24(), pf, 1);                    // # LAYOUT 19 Á 24 
            bk.append(new pagina30(), pf, 1);                    // # LAYOUT 25 Á 32 
            bk.append(new pagina36(), pf, 1);
            bk.append(new pagina42(), pf, 1);
            bk.append(new pagina48(), pf, 1);
            switch (qtd) {
                case 49:
                    bk.append(new pagina49(), pf, 1);
                    break;
                case 50:
                    bk.append(new pagina50(), pf, 1);
                    break;
                case 51:
                    bk.append(new pagina51(), pf, 1);
                    break;
                case 52:
                    bk.append(new pagina52(), pf, 1);
                    break;
                case 53:
                    bk.append(new pagina53(), pf, 1);
                    break;
                case 54:
                    bk.append(new pagina54(), pf, 1);
                    break;
            }
            Imprime(bk);

        } else if (qtd > 54 && qtd <= 60) {
            bk.append(new paginaPrincipal(), job.defaultPage(), 0); // # LAYOUT 1 Á 6 
            bk.append(new pagina12(), pf, 1);                     // # LAYOUT 7 Á 12 
            bk.append(new pagina18(), pf, 1);                    // # LAYOUT 13 Á 18 
            bk.append(new pagina24(), pf, 1);                    // # LAYOUT 19 Á 24 
            bk.append(new pagina30(), pf, 1);                    // # LAYOUT 25 Á 32 
            bk.append(new pagina36(), pf, 1);                    // # LAYOUT 32 Á 38 
            bk.append(new pagina42(), pf, 1);                   // # LAYOUT 38 Á 44            
            bk.append(new pagina48(), pf, 1);
            bk.append(new pagina54(), pf, 1);
            switch (qtd) {
                case 55:
                    bk.append(new pagina55(), pf, 1);
                    break;
                case 56:
                    bk.append(new pagina56(), pf, 1);
                    break;

            }

            Imprime(bk);
        }

        return NO_SUCH_PAGE;
    }

    public int Cabecalho(Graphics g, PageFormat pf, int pageIndice) throws PrinterException {
        try {
            switch (uniSelect) {
                case 0: {
                    // # Logo
                    BufferedImage image = ImageIO.read(new File("src/pacssid/icons/logo_imagens.png"));
                    g.drawImage(image, 10, 0, 160, 90, null);
                    // # Fim Logo
                    g.setColor(new Color(169, 81, 138));
                    g.drawLine(165, 18, 165, 87);
                    g.setFont(new Font("SansSerif", Font.PLAIN, 12));
                    g.setColor(Color.black);
                    g.drawString("Código :", 180, 24);
                    g.drawString(codigoquevemdaclasse, 230, 24);
                    g.drawString("Data Exame :", 180, 40);
                    g.drawString(dataquevemdaclasse, 260, 40);
                    g.drawString("Data Nasc :", 180, 58);
                    g.drawString(datanascquevemdaclasse, 250, 58);
                    g.drawString("Nome:", 180, 76);
                    g.drawString(nomequevemdaclasse, 220, 76);
                    g.drawString("Descrição:", 180, 94);
                    if (procedimentoquevemdaclasse.equals("null")) {
                        procedimentoquevemdaclasse = " ";                        
                    }else{
                       g.drawString(procedimentoquevemdaclasse, 250, 94);
                    }
                    g.setFont(new Font("SansSerif", Font.BOLD, 10));
                    g.drawString("Av. das Flores, 553 - Jd. Cuiabá - Cuiabá-MT - Fone: 4009-8001", 156, 820);
                    // Fim Rodapé
                    break;
                }
                case 1: {
                    // # Logo
                    BufferedImage image = ImageIO.read(new File("src/pacssid/icons/logo_imagens.png"));
                    g.drawImage(image, 10, 0, 160, 90, null);
                    // # Fim Logo
                    g.setColor(new Color(169, 81, 138));
                    g.drawLine(165, 18, 165, 77);
                    g.setFont(new Font("SansSerif", Font.PLAIN, 12));
                    g.setColor(Color.black);
                    g.drawString("Código :", 180, 24);
                    g.drawString(codigoquevemdaclasse, 230, 24);
                    g.drawString("Data Exame :", 180, 40);
                    g.drawString(dataquevemdaclasse, 260, 40);
                    g.drawString("Data Nasc :", 180, 58);
                    g.drawString(datanascquevemdaclasse, 250, 58);
                    g.drawString("Nome:", 180, 76);
                    g.drawString(nomequevemdaclasse, 220, 76);
                    g.drawString("Descrição:", 180, 94);
                     if (procedimentoquevemdaclasse.equals("null")) {
                        procedimentoquevemdaclasse = " ";                        
                    }else{
                       g.drawString(procedimentoquevemdaclasse, 250, 94);
                    }
                    g.setFont(new Font("SansSerif", Font.BOLD, 10));
                    g.drawString("Av. das Flores, 843, Térreo - Jd. Cuiabá - Cuiabá-MT - Fone: 4009-8001", 156, 820);
                    // Fim Rodapé
                    break;
                }
                case 2: {
                    // # Logo
                    BufferedImage image = ImageIO.read(new File("src/pacssid/icons/logo_imagens.png"));
                    g.drawImage(image, 10, 0, 160, 90, null);
                    // # Fim Logo
                    g.setColor(new Color(169, 81, 138));
                    g.drawLine(165, 18, 165, 77);
                    g.setFont(new Font("SansSerif", Font.PLAIN, 12));
                    g.setColor(Color.black);
                    g.drawString("Código :", 180, 24);
                    g.drawString(codigoquevemdaclasse, 230, 24);
                    g.drawString("Data Exame :", 180, 40);
                    g.drawString(dataquevemdaclasse, 260, 40);
                    g.drawString("Data Nasc :", 180, 58);
                    g.drawString(datanascquevemdaclasse, 250, 58);
                    g.drawString("Nome:", 180, 76);
                    g.drawString(nomequevemdaclasse, 220, 76);
                    g.drawString("Descrição:", 180, 94);
                     if (procedimentoquevemdaclasse.equals("null")) {
                        procedimentoquevemdaclasse = " ";                        
                    }else{
                       g.drawString(procedimentoquevemdaclasse, 250, 94);
                    }
                    g.setFont(new Font("SansSerif", Font.BOLD, 10));
                    g.drawString("R. Cmte. Costa, 1494 - Centro Sul - Cuiabá-MT - Fone: 2136-5154", 156, 820);
                    // Fim Rodapé
                    break;
                }
                case 3:
                    // # Logo
                    BufferedImage image = ImageIO.read(new File("src/pacssid/icons/logo_imagens.png"));
                    g.drawImage(image, 10, 0, 160, 90, null);
                    // # Fim Logo
                    g.setColor(new Color(169, 81, 138));
                    g.drawLine(165, 18, 165, 77);
                    g.setFont(new Font("SansSerif", Font.PLAIN, 12));
                    g.setColor(Color.black);
                    g.drawString("Código :", 180, 24);
                    g.drawString(codigoquevemdaclasse, 230, 24);
                    g.drawString("Data Exame :", 180, 40);
                    g.drawString(dataquevemdaclasse, 260, 40);
                    g.drawString("Data Nasc :", 180, 58);
                    g.drawString(datanascquevemdaclasse, 250, 58);
                    g.drawString("Nome:", 180, 76);
                    g.drawString(nomequevemdaclasse, 220, 76);
                    g.drawString("Descrição:", 180, 94);
                     if (procedimentoquevemdaclasse.equals("null")) {
                        procedimentoquevemdaclasse = " ";                        
                    }else{
                       g.drawString(procedimentoquevemdaclasse, 250, 94);
                    }
                    g.setFont(new Font("SansSerif", Font.BOLD, 10));
                    g.drawString("R. das Begônias, 615 - Jd. Cuiabá - Cuiabá-MT - Fone: 2127-4412", 156, 820);
                    // Fim Rodapé
                    break;
                default:
                    // # Logo
                    BufferedImage image1 = ImageIO.read(new File("src/pacssid/icons/logo_imagens.png"));
                    g.drawImage(image1, 10, 0, 160, 90, null);
                    // # Fim Logo
                    g.setColor(new Color(169, 81, 138));
                    g.drawLine(165, 18, 165, 77);
                    g.setFont(new Font("SansSerif", Font.PLAIN, 12));
                    g.setColor(Color.black);
                    g.drawString("Código :", 180, 24);
                    g.drawString(codigoquevemdaclasse, 230, 24);
                    g.drawString("Data Exame :", 180, 40);
                    g.drawString(dataquevemdaclasse, 260, 40);
                    g.drawString("Data Nasc :", 180, 58);
                    g.drawString(datanascquevemdaclasse, 250, 58);
                    g.drawString("Nome:", 180, 76);
                    g.drawString(nomequevemdaclasse, 220, 76);
                    g.drawString("Descrição:", 180, 94);
                     if (procedimentoquevemdaclasse.equals("null")) {
                        procedimentoquevemdaclasse = " ";                        
                    }else{
                       g.drawString(procedimentoquevemdaclasse, 250, 94);
                    }
                    g.setFont(new Font("SansSerif", Font.BOLD, 10));
                    g.drawString("Av. das Flores, 553 - Jd. Cuiabá - Cuiabá-MT - Fone: 4009-8001", 156, 820);
                    // Fim Rodapé    
                    break;

            }

        } catch (IOException ex) {
            Logger.getLogger(Impressao.class.getName()).log(Level.SEVERE, null, ex);
        }
        /* Now we perform our rendering */
 /* tell the caller that this page is part of the printed document */
        return PAGE_EXISTS;
    }

    public int MenorQueSete(Graphics g, PageFormat pf, int pageIndice) throws PrinterException {
        int arredondar = (int) Math.round(((double) qtd / 6) + 0.4d);
        switch (qtd) {
            case 1:
                try {
                    Cabecalho(g, pf, pageIndice);
                    BufferedImage bloco1 = ImageIO.read(new File("src/pacssid/temp/1.jpg"));
                    g.drawImage(bloco1, 15, 100, 280, 220, null);
                    g.setFont(new Font("TimesRoman", Font.PLAIN, 9));
                    g.drawString("Página 1 de " + arredondar, 260, 800);
                } catch (IOException ex) {
                    Logger.getLogger(Impressao.class.getName()).log(Level.SEVERE, null, ex);
                }
                break;
            case 2:
                try {
                    Cabecalho(g, pf, pageIndice);
                    BufferedImage bloco1 = ImageIO.read(new File("src/pacssid/temp/1.jpg"));
                    BufferedImage bloco2 = ImageIO.read(new File("src/pacssid/temp/2.jpg"));
                    g.drawImage(bloco1, 15, 100, 280, 220, null);
                    g.drawImage(bloco2, 300, 100, 280, 220, null);
                    g.setFont(new Font("TimesRoman", Font.PLAIN, 9));
                    g.drawString("Página 1 de " + arredondar, 260, 800);
                } catch (IOException ex) {
                    Logger.getLogger(Impressao.class.getName()).log(Level.SEVERE, null, ex);
                }
                break;
            case 3:
                try {
                    Cabecalho(g, pf, pageIndice);
                    BufferedImage bloco1 = ImageIO.read(new File("src/pacssid/temp/1.jpg"));
                    BufferedImage bloco2 = ImageIO.read(new File("src/pacssid/temp/2.jpg"));
                    BufferedImage bloco3 = ImageIO.read(new File("src/pacssid/temp/3.jpg"));
                    g.drawImage(bloco1, 15, 100, 280, 220, null);
                    g.drawImage(bloco2, 300, 100, 280, 220, null);
                    g.drawImage(bloco3, 15, 330, 280, 220, null);
                    g.setFont(new Font("TimesRoman", Font.PLAIN, 9));
                    g.drawString("Página 1 de " + arredondar, 260, 800);
                } catch (IOException ex) {
                    Logger.getLogger(Impressao.class.getName()).log(Level.SEVERE, null, ex);
                }
                break;
            case 4:
                try {
                    Cabecalho(g, pf, pageIndice);
                    BufferedImage bloco1 = ImageIO.read(new File("src/pacssid/temp/1.jpg"));
                    BufferedImage bloco2 = ImageIO.read(new File("src/pacssid/temp/2.jpg"));
                    BufferedImage bloco3 = ImageIO.read(new File("src/pacssid/temp/3.jpg"));
                    BufferedImage bloco4 = ImageIO.read(new File("src/pacssid/temp/4.jpg"));
                    g.drawImage(bloco1, 15, 100, 280, 220, null);
                    g.drawImage(bloco2, 300, 100, 280, 220, null);
                    g.drawImage(bloco3, 15, 330, 280, 220, null);
                    g.drawImage(bloco4, 300, 330, 280, 220, null);
                    g.setFont(new Font("TimesRoman", Font.PLAIN, 9));
                    g.drawString("Página 1 de " + arredondar, 260, 800);
                } catch (IOException ex) {
                    Logger.getLogger(Impressao.class.getName()).log(Level.SEVERE, null, ex);
                }
                break;
            case 5:
                try {
                    Cabecalho(g, pf, pageIndice);
                    BufferedImage bloco1 = ImageIO.read(new File("src/pacssid/temp/1.jpg"));
                    BufferedImage bloco2 = ImageIO.read(new File("src/pacssid/temp/2.jpg"));
                    BufferedImage bloco3 = ImageIO.read(new File("src/pacssid/temp/3.jpg"));
                    BufferedImage bloco4 = ImageIO.read(new File("src/pacssid/temp/4.jpg"));
                    BufferedImage bloco5 = ImageIO.read(new File("src/pacssid/temp/5.jpg"));
                    g.drawImage(bloco1, 15, 100, 280, 220, null);
                    g.drawImage(bloco2, 300, 100, 280, 220, null);
                    g.drawImage(bloco3, 15, 330, 280, 220, null);
                    g.drawImage(bloco4, 300, 330, 280, 220, null);
                    g.drawImage(bloco5, 15, 560, 280, 220, null);
                    g.setFont(new Font("TimesRoman", Font.PLAIN, 9));
                    g.drawString("Página 1 de " + arredondar, 260, 800);
                } catch (IOException ex) {
                    Logger.getLogger(Impressao.class.getName()).log(Level.SEVERE, null, ex);
                }
                break;
            case 6:
                try {
                    Cabecalho(g, pf, pageIndice);
                    BufferedImage bloco1 = ImageIO.read(new File("src/pacssid/temp/1.jpg"));
                    BufferedImage bloco2 = ImageIO.read(new File("src/pacssid/temp/2.jpg"));
                    BufferedImage bloco3 = ImageIO.read(new File("src/pacssid/temp/3.jpg"));
                    BufferedImage bloco4 = ImageIO.read(new File("src/pacssid/temp/4.jpg"));
                    BufferedImage bloco5 = ImageIO.read(new File("src/pacssid/temp/5.jpg"));
                    BufferedImage bloco6 = ImageIO.read(new File("src/pacssid/temp/6.jpg"));
                    g.drawImage(bloco1, 15, 100, 280, 220, null);
                    g.drawImage(bloco2, 300, 100, 280, 220, null);
                    g.drawImage(bloco3, 15, 330, 280, 220, null);
                    g.drawImage(bloco4, 300, 330, 280, 220, null);
                    g.drawImage(bloco5, 15, 560, 280, 220, null);
                    g.drawImage(bloco6, 300, 560, 280, 220, null);
                    g.setFont(new Font("TimesRoman", Font.PLAIN, 9));
                    g.drawString("Página 1 de " + arredondar, 260, 800);
                } catch (IOException ex) {
                    Logger.getLogger(Impressao.class.getName()).log(Level.SEVERE, null, ex);
                }
                break;
        }
        /* Now we perform our rendering */
 /* tell the caller that this page is part of the printed document */
        return PAGE_EXISTS;
    }

    public void DeletaArquivos() {
        File pasta = new File("src/pacssid/temp/");
        if (pasta.isDirectory()) {
            File[] tudo = pasta.listFiles();
            for (File toDelete : tudo) {
                toDelete.delete();
            }
        }
    }

    public void Imprimir() throws PrinterException {
        PrinterJob job = PrinterJob.getPrinterJob();
        job.setPrintable(this);
        job.print();

    }

    class paginaPrincipal implements Printable {
        @Override
        public int print(Graphics g, PageFormat pf, int pageIndex) throws PrinterException {
            Graphics2D g2d = (Graphics2D) g;
            g2d.translate(pf.getImageableX(), pf.getImageableY());
            int arredondar = (int) Math.round(((double) qtd / 6) + 0.4d);
            if (qtd <= 6) {
                MenorQueSete(g, pf, pageIndex);
            } else {

                try {
                    Cabecalho(g, pf, pageIndex);
                    BufferedImage bloco1 = ImageIO.read(new File("src/pacssid/temp/1.jpg"));
                    BufferedImage bloco2 = ImageIO.read(new File("src/pacssid/temp/2.jpg"));
                    BufferedImage bloco3 = ImageIO.read(new File("src/pacssid/temp/3.jpg"));
                    BufferedImage bloco4 = ImageIO.read(new File("src/pacssid/temp/4.jpg"));
                    BufferedImage bloco5 = ImageIO.read(new File("src/pacssid/temp/5.jpg"));
                    BufferedImage bloco6 = ImageIO.read(new File("src/pacssid/temp/6.jpg"));
                    g.drawImage(bloco1, 15, 100, 280, 220, null);
                    g.drawImage(bloco2, 300, 100, 280, 220, null);
                    g.drawImage(bloco3, 15, 330, 280, 220, null);
                    g.drawImage(bloco4, 300, 330, 280, 220, null);
                    g.drawImage(bloco5, 15, 560, 280, 220, null);
                    g.drawImage(bloco6, 300, 560, 280, 220, null);
                    g.setFont(new Font("TimesRoman", Font.PLAIN, 9));
                    g.drawString("Página 1 de " + arredondar, 260, 800);

                } catch (IOException ex) {
                    Logger.getLogger(Impressao.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            return Printable.PAGE_EXISTS;
        }
    }

    class pagina2 implements Printable {

        @Override
        public int print(Graphics g, PageFormat pf, int pageIndex) throws PrinterException {
            Graphics2D g2d = (Graphics2D) g;
            g2d.translate(pf.getImageableX(), pf.getImageableY());
            int arredondar = (int) Math.round(((double) qtd / 6) + 0.4d);
            try {
                Cabecalho(g, pf, pageIndex);
                BufferedImage bloco7 = ImageIO.read(new File("src/pacssid/temp/7.jpg"));
                BufferedImage bloco8 = ImageIO.read(new File("src/pacssid/temp/8.jpg"));
                BufferedImage bloco9 = ImageIO.read(new File("src/pacssid/temp/9.jpg"));
                BufferedImage bloco10 = ImageIO.read(new File("src/pacssid/temp/10.jpg"));
                BufferedImage bloco11 = ImageIO.read(new File("src/pacssid/temp/11.jpg"));
                BufferedImage bloco12 = ImageIO.read(new File("src/pacssid/temp/12.jpg"));
                g.drawImage(bloco7, 15, 100, 280, 220, null);
                g.drawImage(bloco8, 300, 100, 280, 220, null);
                g.drawImage(bloco9, 15, 330, 280, 220, null);
                g.drawImage(bloco10, 300, 330, 280, 220, null);
                g.drawImage(bloco11, 15, 560, 280, 220, null);
                g.drawImage(bloco12, 300, 560, 280, 220, null);
                g.setFont(new Font("TimesRoman", Font.PLAIN, 9));
                g.drawString("Página 2 de " + arredondar, 260, 800);
            } catch (IOException ex) {
                Logger.getLogger(Impressao.class.getName()).log(Level.SEVERE, null, ex);
            }
            return Printable.PAGE_EXISTS;
        }
    }

    class pagina1 implements Printable {

        @Override
        public int print(Graphics g, PageFormat pf, int pageIndex) throws PrinterException {
            Graphics2D g2d = (Graphics2D) g;
            g2d.translate(pf.getImageableX(), pf.getImageableY());
            int arredondar = (int) Math.round(((double) qtd / 6) + 0.4d);
            try {
                Cabecalho(g, pf, pageIndex);
                BufferedImage bloco7 = ImageIO.read(new File("src/pacssid/temp/1.jpg"));
                g.drawImage(bloco7, 15, 100, 280, 220, null);
                g.setFont(new Font("TimesRoman", Font.PLAIN, 9));
                g.drawString("Página 1 de " + arredondar, 260, 800);
            } catch (IOException ex) {
                Logger.getLogger(Impressao.class.getName()).log(Level.SEVERE, null, ex);
            }
            return Printable.PAGE_EXISTS;
        }
    }

    class paginacom2 implements Printable {

        @Override
        public int print(Graphics g, PageFormat pf, int pageIndex) throws PrinterException {
            Graphics2D g2d = (Graphics2D) g;
            g2d.translate(pf.getImageableX(), pf.getImageableY());
            int arredondar = (int) Math.round(((double) qtd / 6) + 0.4d);
            try {
                Cabecalho(g, pf, pageIndex);
                BufferedImage bloco7 = ImageIO.read(new File("src/pacssid/temp/1.jpg"));
                BufferedImage bloco8 = ImageIO.read(new File("src/pacssid/temp/2.jpg"));
                g.drawImage(bloco7, 15, 100, 280, 220, null);
                g.drawImage(bloco8, 300, 100, 280, 220, null);
                g.setFont(new Font("TimesRoman", Font.PLAIN, 9));
                g.drawString("Página 1 de " + arredondar, 260, 800);
            } catch (IOException ex) {
                Logger.getLogger(Impressao.class.getName()).log(Level.SEVERE, null, ex);
            }
            return Printable.PAGE_EXISTS;
        }
    }

    class pagina3 implements Printable {

        @Override
        public int print(Graphics g, PageFormat pf, int pageIndex) throws PrinterException {
            Graphics2D g2d = (Graphics2D) g;
            g2d.translate(pf.getImageableX(), pf.getImageableY());
            int arredondar = (int) Math.round(((double) qtd / 6) + 0.4d);
            try {
                Cabecalho(g, pf, pageIndex);
                BufferedImage bloco7 = ImageIO.read(new File("src/pacssid/temp/1.jpg"));
                BufferedImage bloco8 = ImageIO.read(new File("src/pacssid/temp/2.jpg"));
                BufferedImage bloco9 = ImageIO.read(new File("src/pacssid/temp/3.jpg"));
                g.drawImage(bloco7, 15, 100, 280, 220, null);
                g.drawImage(bloco8, 300, 100, 280, 220, null);
                g.drawImage(bloco9, 15, 330, 280, 220, null);
                g.setFont(new Font("TimesRoman", Font.PLAIN, 9));
                g.drawString("Página 1 de " + arredondar, 260, 800);
            } catch (IOException ex) {
                Logger.getLogger(Impressao.class.getName()).log(Level.SEVERE, null, ex);
            }
            return Printable.PAGE_EXISTS;
        }
    }

    class pagina4 implements Printable {

        @Override
        public int print(Graphics g, PageFormat pf, int pageIndex) throws PrinterException {
            Graphics2D g2d = (Graphics2D) g;
            g2d.translate(pf.getImageableX(), pf.getImageableY());
            int arredondar = (int) Math.round(((double) qtd / 6) + 0.4d);
            try {
                Cabecalho(g, pf, pageIndex);
                BufferedImage bloco7 = ImageIO.read(new File("src/pacssid/temp/1.jpg"));
                BufferedImage bloco8 = ImageIO.read(new File("src/pacssid/temp/2.jpg"));
                BufferedImage bloco9 = ImageIO.read(new File("src/pacssid/temp/3.jpg"));
                BufferedImage bloco10 = ImageIO.read(new File("src/pacssid/temp/4.jpg"));
                g.drawImage(bloco7, 15, 100, 280, 220, null);
                g.drawImage(bloco8, 300, 100, 280, 220, null);
                g.drawImage(bloco9, 15, 330, 280, 220, null);
                g.drawImage(bloco10, 300, 330, 280, 220, null);
                g.setFont(new Font("TimesRoman", Font.PLAIN, 9));
                g.drawString("Página 1 de " + arredondar, 260, 800);
            } catch (IOException ex) {
                Logger.getLogger(Impressao.class.getName()).log(Level.SEVERE, null, ex);
            }
            return Printable.PAGE_EXISTS;
        }
    }

    class pagina5 implements Printable {

        @Override
        public int print(Graphics g, PageFormat pf, int pageIndex) throws PrinterException {
            Graphics2D g2d = (Graphics2D) g;
            g2d.translate(pf.getImageableX(), pf.getImageableY());
            int arredondar = (int) Math.round(((double) qtd / 6) + 0.4d);
            try {
                Cabecalho(g, pf, pageIndex);
                BufferedImage bloco7 = ImageIO.read(new File("src/pacssid/temp/1.jpg"));
                BufferedImage bloco8 = ImageIO.read(new File("src/pacssid/temp/2.jpg"));
                BufferedImage bloco9 = ImageIO.read(new File("src/pacssid/temp/3.jpg"));
                BufferedImage bloco10 = ImageIO.read(new File("src/pacssid/temp/4.jpg"));
                BufferedImage bloco11 = ImageIO.read(new File("src/pacssid/temp/5.jpg"));
                g.drawImage(bloco7, 15, 100, 280, 220, null);
                g.drawImage(bloco8, 300, 100, 280, 220, null);
                g.drawImage(bloco9, 15, 330, 280, 220, null);
                g.drawImage(bloco10, 300, 330, 280, 220, null);
                g.drawImage(bloco11, 15, 560, 280, 220, null);
                g.setFont(new Font("TimesRoman", Font.PLAIN, 9));
                g.drawString("Página 1 de " + arredondar, 260, 800);
            } catch (IOException ex) {
                Logger.getLogger(Impressao.class.getName()).log(Level.SEVERE, null, ex);
            }
            return Printable.PAGE_EXISTS;
        }
    }

    class pagina6 implements Printable {

        @Override
        public int print(Graphics g, PageFormat pf, int pageIndex) throws PrinterException {
            Graphics2D g2d = (Graphics2D) g;
            g2d.translate(pf.getImageableX(), pf.getImageableY());
            int arredondar = (int) Math.round(((double) qtd / 6) + 0.4d);
            try {
                Cabecalho(g, pf, pageIndex);
                BufferedImage bloco1 = ImageIO.read(new File("src/pacssid/temp/1.jpg"));
                BufferedImage bloco2 = ImageIO.read(new File("src/pacssid/temp/2.jpg"));
                BufferedImage bloco3 = ImageIO.read(new File("src/pacssid/temp/3.jpg"));
                BufferedImage bloco4 = ImageIO.read(new File("src/pacssid/temp/4.jpg"));
                BufferedImage bloco5 = ImageIO.read(new File("src/pacssid/temp/5.jpg"));
                BufferedImage bloco6 = ImageIO.read(new File("src/pacssid/temp/6.jpg"));
                g.drawImage(bloco1, 15, 100, 280, 220, null);
                g.drawImage(bloco2, 300, 100, 280, 220, null);
                g.drawImage(bloco3, 15, 330, 280, 220, null);
                g.drawImage(bloco4, 300, 330, 280, 220, null);
                g.drawImage(bloco5, 15, 560, 280, 220, null);
                g.drawImage(bloco6, 300, 560, 280, 220, null);
                g.setFont(new Font("TimesRoman", Font.PLAIN, 9));
                g.drawString("Página 1 de " + arredondar, 260, 800);
            } catch (IOException ex) {
                Logger.getLogger(Impressao.class.getName()).log(Level.SEVERE, null, ex);
            }
            return Printable.PAGE_EXISTS;
        }
    }

    class pagina7 implements Printable {

        @Override
        public int print(Graphics g, PageFormat pf, int pageIndex) throws PrinterException {
            Graphics2D g2d = (Graphics2D) g;
            g2d.translate(pf.getImageableX(), pf.getImageableY());
            int arredondar = (int) Math.round(((double) qtd / 6) + 0.4d);
            try {
                Cabecalho(g, pf, pageIndex);
                BufferedImage bloco7 = ImageIO.read(new File("src/pacssid/temp/7.jpg"));
                g.drawImage(bloco7, 15, 100, 280, 220, null);
                g.setFont(new Font("TimesRoman", Font.PLAIN, 9));
                g.drawString("Página 2 de " + arredondar, 260, 800);
            } catch (IOException ex) {
                Logger.getLogger(Impressao.class.getName()).log(Level.SEVERE, null, ex);
            }
            return Printable.PAGE_EXISTS;
        }
    }

    class pagina8 implements Printable {

        @Override
        public int print(Graphics g, PageFormat pf, int pageIndex) throws PrinterException {
            Graphics2D g2d = (Graphics2D) g;
            g2d.translate(pf.getImageableX(), pf.getImageableY());
            int arredondar = (int) Math.round(((double) qtd / 6) + 0.4d);
            try {
                Cabecalho(g, pf, pageIndex);
                BufferedImage bloco7 = ImageIO.read(new File("src/pacssid/temp/7.jpg"));
                BufferedImage bloco8 = ImageIO.read(new File("src/pacssid/temp/8.jpg"));
                g.drawImage(bloco7, 15, 100, 280, 220, null);
                g.drawImage(bloco8, 300, 100, 280, 220, null);
                g.setFont(new Font("TimesRoman", Font.PLAIN, 9));
                g.drawString("Página 2 de " + arredondar, 260, 800);

            } catch (IOException ex) {
                Logger.getLogger(Impressao.class.getName()).log(Level.SEVERE, null, ex);
            }
            return Printable.PAGE_EXISTS;
        }
    }

    class pagina9 implements Printable {

        @Override
        public int print(Graphics g, PageFormat pf, int pageIndex) throws PrinterException {
            Graphics2D g2d = (Graphics2D) g;
            g2d.translate(pf.getImageableX(), pf.getImageableY());
            int arredondar = (int) Math.round(((double) qtd / 6) + 0.4d);
            try {
                Cabecalho(g, pf, pageIndex);
                BufferedImage bloco7 = ImageIO.read(new File("src/pacssid/temp/7.jpg"));
                BufferedImage bloco8 = ImageIO.read(new File("src/pacssid/temp/8.jpg"));
                BufferedImage bloco9 = ImageIO.read(new File("src/pacssid/temp/9.jpg"));
                g.drawImage(bloco7, 15, 100, 280, 220, null);
                g.drawImage(bloco8, 300, 100, 280, 220, null);
                g.drawImage(bloco9, 15, 330, 280, 220, null);
                g.setFont(new Font("TimesRoman", Font.PLAIN, 9));
                g.drawString("Página 2 de " + arredondar, 260, 800);
            } catch (IOException ex) {
                Logger.getLogger(Impressao.class.getName()).log(Level.SEVERE, null, ex);
            }
            return Printable.PAGE_EXISTS;
        }
    }

    class pagina10 implements Printable {

        @Override
        public int print(Graphics g, PageFormat pf, int pageIndex) throws PrinterException {
            Graphics2D g2d = (Graphics2D) g;
            g2d.translate(pf.getImageableX(), pf.getImageableY());
            int arredondar = (int) Math.round(((double) qtd / 6) + 0.4d);
            try {
                Cabecalho(g, pf, pageIndex);
                BufferedImage bloco7 = ImageIO.read(new File("src/pacssid/temp/7.jpg"));
                BufferedImage bloco8 = ImageIO.read(new File("src/pacssid/temp/8.jpg"));
                BufferedImage bloco9 = ImageIO.read(new File("src/pacssid/temp/9.jpg"));
                BufferedImage bloco10 = ImageIO.read(new File("src/pacssid/temp/10.jpg"));
                g.drawImage(bloco7, 15, 100, 280, 220, null);
                g.drawImage(bloco8, 300, 100, 280, 220, null);
                g.drawImage(bloco9, 15, 330, 280, 220, null);
                g.drawImage(bloco10, 300, 330, 280, 220, null);
                g.setFont(new Font("TimesRoman", Font.PLAIN, 9));
                g.drawString("Página 2 de " + arredondar, 260, 800);
            } catch (IOException ex) {
                Logger.getLogger(Impressao.class.getName()).log(Level.SEVERE, null, ex);
            }
            return Printable.PAGE_EXISTS;
        }
    }

    class pagina11 implements Printable {

        @Override
        public int print(Graphics g, PageFormat pf, int pageIndex) throws PrinterException {
            Graphics2D g2d = (Graphics2D) g;
            g2d.translate(pf.getImageableX(), pf.getImageableY());
            int arredondar = (int) Math.round(((double) qtd / 6) + 0.4d);
            try {
                Cabecalho(g, pf, pageIndex);
                BufferedImage bloco7 = ImageIO.read(new File("src/pacssid/temp/7.jpg"));
                BufferedImage bloco8 = ImageIO.read(new File("src/pacssid/temp/8.jpg"));
                BufferedImage bloco9 = ImageIO.read(new File("src/pacssid/temp/9.jpg"));
                BufferedImage bloco10 = ImageIO.read(new File("src/pacssid/temp/10.jpg"));
                BufferedImage bloco11 = ImageIO.read(new File("src/pacssid/temp/11.jpg"));
                g.drawImage(bloco7, 15, 100, 280, 220, null);
                g.drawImage(bloco8, 300, 100, 280, 220, null);
                g.drawImage(bloco9, 15, 330, 280, 220, null);
                g.drawImage(bloco10, 300, 330, 280, 220, null);
                g.drawImage(bloco11, 15, 560, 280, 220, null);
                g.setFont(new Font("TimesRoman", Font.PLAIN, 9));
                g.drawString("Página 2 de " + arredondar, 260, 800);
            } catch (IOException ex) {
                Logger.getLogger(Impressao.class.getName()).log(Level.SEVERE, null, ex);
            }
            return Printable.PAGE_EXISTS;
        }
    }

    class pagina12 implements Printable {

        @Override
        public int print(Graphics g, PageFormat pf, int pageIndex) throws PrinterException {
            Graphics2D g2d = (Graphics2D) g;
            g2d.translate(pf.getImageableX(), pf.getImageableY());
            int arredondar = (int) Math.round(((double) qtd / 6) + 0.4d);
            try {
                Cabecalho(g, pf, pageIndex);
                BufferedImage bloco7 = ImageIO.read(new File("src/pacssid/temp/7.jpg"));
                BufferedImage bloco8 = ImageIO.read(new File("src/pacssid/temp/8.jpg"));
                BufferedImage bloco9 = ImageIO.read(new File("src/pacssid/temp/9.jpg"));
                BufferedImage bloco10 = ImageIO.read(new File("src/pacssid/temp/10.jpg"));
                BufferedImage bloco11 = ImageIO.read(new File("src/pacssid/temp/11.jpg"));
                BufferedImage bloco12 = ImageIO.read(new File("src/pacssid/temp/12.jpg"));
                g.drawImage(bloco7, 15, 100, 280, 220, null);
                g.drawImage(bloco8, 300, 100, 280, 220, null);
                g.drawImage(bloco9, 15, 330, 280, 220, null);
                g.drawImage(bloco10, 300, 330, 280, 220, null);
                g.drawImage(bloco11, 15, 560, 280, 220, null);
                g.drawImage(bloco12, 300, 560, 280, 220, null);
                g.setFont(new Font("TimesRoman", Font.PLAIN, 9));
                g.drawString("Página 2 de " + arredondar, 260, 800);
            } catch (IOException ex) {
                Logger.getLogger(Impressao.class.getName()).log(Level.SEVERE, null, ex);
            }
            return Printable.PAGE_EXISTS;
        }
    }

    class pagina13 implements Printable {

        @Override
        public int print(Graphics g, PageFormat pf, int pageIndex) throws PrinterException {
            Graphics2D g2d = (Graphics2D) g;
            g2d.translate(pf.getImageableX(), pf.getImageableY());
            int arredondar = (int) Math.round(((double) qtd / 6) + 0.4d);
            try {
                Cabecalho(g, pf, pageIndex);
                BufferedImage bloco7 = ImageIO.read(new File("src/pacssid/temp/13.jpg"));
                g.drawImage(bloco7, 15, 100, 280, 220, null);
                g.setFont(new Font("TimesRoman", Font.PLAIN, 9));
                g.drawString("Página 3 de " + arredondar, 260, 800);
            } catch (IOException ex) {
                Logger.getLogger(Impressao.class.getName()).log(Level.SEVERE, null, ex);
            }
            return Printable.PAGE_EXISTS;
        }
    }

    class pagina14 implements Printable {

        @Override
        public int print(Graphics g, PageFormat pf, int pageIndex) throws PrinterException {
            Graphics2D g2d = (Graphics2D) g;
            g2d.translate(pf.getImageableX(), pf.getImageableY());
            int arredondar = (int) Math.round(((double) qtd / 6) + 0.4d);
            try {
                Cabecalho(g, pf, pageIndex);
                BufferedImage bloco7 = ImageIO.read(new File("src/pacssid/temp/13.jpg"));
                BufferedImage bloco8 = ImageIO.read(new File("src/pacssid/temp/14.jpg"));
                g.drawImage(bloco7, 15, 100, 280, 220, null);
                g.drawImage(bloco8, 300, 100, 280, 220, null);
                g.setFont(new Font("TimesRoman", Font.PLAIN, 9));
                g.drawString("Página 3 de " + arredondar, 260, 800);
            } catch (IOException ex) {
                Logger.getLogger(Impressao.class.getName()).log(Level.SEVERE, null, ex);
            }
            return Printable.PAGE_EXISTS;
        }
    }

    class pagina15 implements Printable {

        @Override
        public int print(Graphics g, PageFormat pf, int pageIndex) throws PrinterException {
            Graphics2D g2d = (Graphics2D) g;
            g2d.translate(pf.getImageableX(), pf.getImageableY());
            int arredondar = (int) Math.round(((double) qtd / 6) + 0.4d);
            try {
                Cabecalho(g, pf, pageIndex);
                BufferedImage bloco7 = ImageIO.read(new File("src/pacssid/temp/13.jpg"));
                BufferedImage bloco8 = ImageIO.read(new File("src/pacssid/temp/14.jpg"));
                BufferedImage bloco9 = ImageIO.read(new File("src/pacssid/temp/15.jpg"));
                g.drawImage(bloco7, 15, 100, 280, 220, null);
                g.drawImage(bloco8, 300, 100, 280, 220, null);
                g.drawImage(bloco9, 15, 330, 280, 220, null);
                g.setFont(new Font("TimesRoman", Font.PLAIN, 9));
                g.drawString("Página 3 de " + arredondar, 260, 800);
            } catch (IOException ex) {
                Logger.getLogger(Impressao.class.getName()).log(Level.SEVERE, null, ex);
            }
            return Printable.PAGE_EXISTS;
        }
    }

    class pagina16 implements Printable {

        @Override
        public int print(Graphics g, PageFormat pf, int pageIndex) throws PrinterException {
            Graphics2D g2d = (Graphics2D) g;
            g2d.translate(pf.getImageableX(), pf.getImageableY());
            int arredondar = (int) Math.round(((double) qtd / 6) + 0.4d);
            try {
                Cabecalho(g, pf, pageIndex);
                BufferedImage bloco7 = ImageIO.read(new File("src/pacssid/temp/13.jpg"));
                BufferedImage bloco8 = ImageIO.read(new File("src/pacssid/temp/14.jpg"));
                BufferedImage bloco9 = ImageIO.read(new File("src/pacssid/temp/15.jpg"));
                g.drawImage(bloco7, 15, 100, 280, 220, null);
                g.drawImage(bloco8, 300, 100, 280, 220, null);
                g.drawImage(bloco9, 15, 330, 280, 220, null);
                g.setFont(new Font("TimesRoman", Font.PLAIN, 9));
                g.drawString("Página 3 de " + arredondar, 260, 800);
            } catch (IOException ex) {
                Logger.getLogger(Impressao.class.getName()).log(Level.SEVERE, null, ex);
            }
            return Printable.PAGE_EXISTS;
        }
    }

    class pagina17 implements Printable {

        @Override
        public int print(Graphics g, PageFormat pf, int pageIndex) throws PrinterException {
            Graphics2D g2d = (Graphics2D) g;
            g2d.translate(pf.getImageableX(), pf.getImageableY());
            int arredondar = (int) Math.round(((double) qtd / 6) + 0.4d);
            try {
                Cabecalho(g, pf, pageIndex);
                BufferedImage bloco7 = ImageIO.read(new File("src/pacssid/temp/13.jpg"));
                BufferedImage bloco8 = ImageIO.read(new File("src/pacssid/temp/14.jpg"));
                BufferedImage bloco9 = ImageIO.read(new File("src/pacssid/temp/15.jpg"));
                BufferedImage bloco10 = ImageIO.read(new File("src/pacssid/temp/16.jpg"));
                BufferedImage bloco11 = ImageIO.read(new File("src/pacssid/temp/17.jpg"));
                g.drawImage(bloco7, 15, 100, 280, 220, null);
                g.drawImage(bloco8, 300, 100, 280, 220, null);
                g.drawImage(bloco9, 15, 330, 280, 220, null);
                g.drawImage(bloco10, 300, 330, 280, 220, null);
                g.drawImage(bloco11, 15, 560, 280, 220, null);
                g.setFont(new Font("TimesRoman", Font.PLAIN, 9));
                g.drawString("Página 3 de " + arredondar, 260, 800);
            } catch (IOException ex) {
                Logger.getLogger(Impressao.class.getName()).log(Level.SEVERE, null, ex);
            }
            return Printable.PAGE_EXISTS;
        }
    }

    class pagina18 implements Printable {

        @Override
        public int print(Graphics g, PageFormat pf, int pageIndex) throws PrinterException {
            Graphics2D g2d = (Graphics2D) g;
            g2d.translate(pf.getImageableX(), pf.getImageableY());
            int arredondar = (int) Math.round(((double) qtd / 6) + 0.4d);
            try {
                Cabecalho(g, pf, pageIndex);
                BufferedImage bloco7 = ImageIO.read(new File("src/pacssid/temp/13.jpg"));
                BufferedImage bloco8 = ImageIO.read(new File("src/pacssid/temp/14.jpg"));
                BufferedImage bloco9 = ImageIO.read(new File("src/pacssid/temp/15.jpg"));
                BufferedImage bloco10 = ImageIO.read(new File("src/pacssid/temp/16.jpg"));
                BufferedImage bloco11 = ImageIO.read(new File("src/pacssid/temp/17.jpg"));
                BufferedImage bloco12 = ImageIO.read(new File("src/pacssid/temp/18.jpg"));
                g.drawImage(bloco7, 15, 100, 280, 220, null);
                g.drawImage(bloco8, 300, 100, 280, 220, null);
                g.drawImage(bloco9, 15, 330, 280, 220, null);
                g.drawImage(bloco10, 300, 330, 280, 220, null);
                g.drawImage(bloco11, 15, 560, 280, 220, null);
                g.drawImage(bloco12, 300, 560, 280, 220, null);
                g.setFont(new Font("TimesRoman", Font.PLAIN, 9));
                g.drawString("Página 3 de " + arredondar, 260, 800);
            } catch (IOException ex) {
                Logger.getLogger(Impressao.class.getName()).log(Level.SEVERE, null, ex);
            }
            return Printable.PAGE_EXISTS;
        }
    }

    class pagina19 implements Printable {

        @Override
        public int print(Graphics g, PageFormat pf, int pageIndex) throws PrinterException {
            Graphics2D g2d = (Graphics2D) g;
            g2d.translate(pf.getImageableX(), pf.getImageableY());
            int arredondar = (int) Math.round(((double) qtd / 6) + 0.4d);
            try {
                Cabecalho(g, pf, pageIndex);
                BufferedImage bloco7 = ImageIO.read(new File("src/pacssid/temp/19.jpg"));
                g.drawImage(bloco7, 15, 100, 280, 220, null);
                g.setFont(new Font("TimesRoman", Font.PLAIN, 9));
                g.drawString("Página 4 de " + arredondar, 260, 800);
            } catch (IOException ex) {
                Logger.getLogger(Impressao.class.getName()).log(Level.SEVERE, null, ex);
            }
            return Printable.PAGE_EXISTS;
        }
    }

    class pagina20 implements Printable {

        @Override
        public int print(Graphics g, PageFormat pf, int pageIndex) throws PrinterException {
            Graphics2D g2d = (Graphics2D) g;
            g2d.translate(pf.getImageableX(), pf.getImageableY());
            int arredondar = (int) Math.round(((double) qtd / 6) + 0.4d);
            try {
                Cabecalho(g, pf, pageIndex);
                BufferedImage bloco7 = ImageIO.read(new File("src/pacssid/temp/19.jpg"));
                BufferedImage bloco8 = ImageIO.read(new File("src/pacssid/temp/20.jpg"));
                g.drawImage(bloco7, 15, 100, 280, 220, null);
                g.drawImage(bloco8, 300, 100, 280, 220, null);
                g.setFont(new Font("TimesRoman", Font.PLAIN, 9));
                g.drawString("Página 4 de " + arredondar, 260, 800);
            } catch (IOException ex) {
                Logger.getLogger(Impressao.class.getName()).log(Level.SEVERE, null, ex);
            }
            return Printable.PAGE_EXISTS;
        }
    }

    class pagina21 implements Printable {

        @Override
        public int print(Graphics g, PageFormat pf, int pageIndex) throws PrinterException {
            Graphics2D g2d = (Graphics2D) g;
            g2d.translate(pf.getImageableX(), pf.getImageableY());
            int arredondar = (int) Math.round(((double) qtd / 6) + 0.4d);
            try {
                Cabecalho(g, pf, pageIndex);
                BufferedImage bloco7 = ImageIO.read(new File("src/pacssid/temp/19.jpg"));
                BufferedImage bloco8 = ImageIO.read(new File("src/pacssid/temp/20.jpg"));
                BufferedImage bloco9 = ImageIO.read(new File("src/pacssid/temp/21.jpg"));
                g.drawImage(bloco7, 15, 100, 280, 220, null);
                g.drawImage(bloco8, 300, 100, 280, 220, null);
                g.drawImage(bloco9, 15, 330, 280, 220, null);
                g.setFont(new Font("TimesRoman", Font.PLAIN, 9));
                g.drawString("Página 4 de " + arredondar, 260, 800);
            } catch (IOException ex) {
                Logger.getLogger(Impressao.class.getName()).log(Level.SEVERE, null, ex);
            }
            return Printable.PAGE_EXISTS;
        }
    }

    class pagina22 implements Printable {

        @Override
        public int print(Graphics g, PageFormat pf, int pageIndex) throws PrinterException {
            Graphics2D g2d = (Graphics2D) g;
            g2d.translate(pf.getImageableX(), pf.getImageableY());
            int arredondar = (int) Math.round(((double) qtd / 6) + 0.4d);
            try {
                Cabecalho(g, pf, pageIndex);
                BufferedImage bloco7 = ImageIO.read(new File("src/pacssid/temp/19.jpg"));
                BufferedImage bloco8 = ImageIO.read(new File("src/pacssid/temp/20.jpg"));
                BufferedImage bloco9 = ImageIO.read(new File("src/pacssid/temp/21.jpg"));
                BufferedImage bloco10 = ImageIO.read(new File("src/pacssid/temp/22.jpg"));
                g.drawImage(bloco7, 15, 100, 280, 220, null);
                g.drawImage(bloco8, 300, 100, 280, 220, null);
                g.drawImage(bloco9, 15, 330, 280, 220, null);
                g.drawImage(bloco10, 300, 330, 280, 220, null);
                g.setFont(new Font("TimesRoman", Font.PLAIN, 9));
                g.drawString("Página 4 de " + arredondar, 260, 800);
            } catch (IOException ex) {
                Logger.getLogger(Impressao.class.getName()).log(Level.SEVERE, null, ex);
            }
            return Printable.PAGE_EXISTS;
        }
    }

    class pagina23 implements Printable {

        @Override
        public int print(Graphics g, PageFormat pf, int pageIndex) throws PrinterException {
            Graphics2D g2d = (Graphics2D) g;
            g2d.translate(pf.getImageableX(), pf.getImageableY());
            int arredondar = (int) Math.round(((double) qtd / 6) + 0.4d);
            try {
                Cabecalho(g, pf, pageIndex);
                BufferedImage bloco7 = ImageIO.read(new File("src/pacssid/temp/19.jpg"));
                BufferedImage bloco8 = ImageIO.read(new File("src/pacssid/temp/20.jpg"));
                BufferedImage bloco9 = ImageIO.read(new File("src/pacssid/temp/21.jpg"));
                BufferedImage bloco10 = ImageIO.read(new File("src/pacssid/temp/22.jpg"));
                BufferedImage bloco11 = ImageIO.read(new File("src/pacssid/temp/23.jpg"));
                g.drawImage(bloco7, 15, 100, 280, 220, null);
                g.drawImage(bloco8, 300, 100, 280, 220, null);
                g.drawImage(bloco9, 15, 330, 280, 220, null);
                g.drawImage(bloco10, 300, 330, 280, 220, null);
                g.drawImage(bloco11, 15, 560, 280, 220, null);
                g.setFont(new Font("TimesRoman", Font.PLAIN, 9));
                g.drawString("Página 4 de " + arredondar, 260, 800);
            } catch (IOException ex) {
                Logger.getLogger(Impressao.class.getName()).log(Level.SEVERE, null, ex);
            }
            return Printable.PAGE_EXISTS;
        }
    }

    class pagina24 implements Printable {

        @Override
        public int print(Graphics g, PageFormat pf, int pageIndex) throws PrinterException {
            Graphics2D g2d = (Graphics2D) g;
            g2d.translate(pf.getImageableX(), pf.getImageableY());
            int arredondar = (int) Math.round(((double) qtd / 6) + 0.4d);
            try {
                Cabecalho(g, pf, pageIndex);
                BufferedImage bloco7 = ImageIO.read(new File("src/pacssid/temp/19.jpg"));
                BufferedImage bloco8 = ImageIO.read(new File("src/pacssid/temp/20.jpg"));
                BufferedImage bloco9 = ImageIO.read(new File("src/pacssid/temp/21.jpg"));
                BufferedImage bloco10 = ImageIO.read(new File("src/pacssid/temp/22.jpg"));
                BufferedImage bloco11 = ImageIO.read(new File("src/pacssid/temp/23.jpg"));
                BufferedImage bloco12 = ImageIO.read(new File("src/pacssid/temp/24.jpg"));
                g.drawImage(bloco7, 15, 100, 280, 220, null);
                g.drawImage(bloco8, 300, 100, 280, 220, null);
                g.drawImage(bloco9, 15, 330, 280, 220, null);
                g.drawImage(bloco10, 300, 330, 280, 220, null);
                g.drawImage(bloco11, 15, 560, 280, 220, null);
                g.drawImage(bloco12, 300, 560, 280, 220, null);
                g.setFont(new Font("TimesRoman", Font.PLAIN, 9));
                g.drawString("Página 4 de " + arredondar, 260, 800);
            } catch (IOException ex) {
                Logger.getLogger(Impressao.class.getName()).log(Level.SEVERE, null, ex);
            }
            return Printable.PAGE_EXISTS;
        }
    }

    class pagina25 implements Printable {

        @Override
        public int print(Graphics g, PageFormat pf, int pageIndex) throws PrinterException {
            Graphics2D g2d = (Graphics2D) g;
            g2d.translate(pf.getImageableX(), pf.getImageableY());
            int arredondar = (int) Math.round(((double) qtd / 6) + 0.4d);
            try {
                Cabecalho(g, pf, pageIndex);
                BufferedImage bloco7 = ImageIO.read(new File("src/pacssid/temp/25.jpg"));
                g.drawImage(bloco7, 15, 100, 280, 220, null);
                g.setFont(new Font("TimesRoman", Font.PLAIN, 9));
                g.drawString("Página 5 de " + arredondar, 260, 800);
            } catch (IOException ex) {
                Logger.getLogger(Impressao.class.getName()).log(Level.SEVERE, null, ex);
            }
            return Printable.PAGE_EXISTS;
        }
    }

    class pagina26 implements Printable {

        @Override
        public int print(Graphics g, PageFormat pf, int pageIndex) throws PrinterException {
            Graphics2D g2d = (Graphics2D) g;
            g2d.translate(pf.getImageableX(), pf.getImageableY());
            int arredondar = (int) Math.round(((double) qtd / 6) + 0.4d);
            try {
                Cabecalho(g, pf, pageIndex);
                BufferedImage bloco7 = ImageIO.read(new File("src/pacssid/temp/25.jpg"));
                BufferedImage bloco8 = ImageIO.read(new File("src/pacssid/temp/26.jpg"));
                g.drawImage(bloco7, 15, 100, 280, 220, null);
                g.drawImage(bloco8, 300, 100, 280, 220, null);
                g.setFont(new Font("TimesRoman", Font.PLAIN, 9));
                g.drawString("Página 5 de " + arredondar, 260, 800);
            } catch (IOException ex) {
                Logger.getLogger(Impressao.class.getName()).log(Level.SEVERE, null, ex);
            }
            return Printable.PAGE_EXISTS;
        }
    }

    class pagina27 implements Printable {

        @Override
        public int print(Graphics g, PageFormat pf, int pageIndex) throws PrinterException {
            Graphics2D g2d = (Graphics2D) g;
            g2d.translate(pf.getImageableX(), pf.getImageableY());
            int arredondar = (int) Math.round(((double) qtd / 6) + 0.4d);
            try {
                Cabecalho(g, pf, pageIndex);
                BufferedImage bloco7 = ImageIO.read(new File("src/pacssid/temp/25.jpg"));
                BufferedImage bloco8 = ImageIO.read(new File("src/pacssid/temp/26.jpg"));
                BufferedImage bloco9 = ImageIO.read(new File("src/pacssid/temp/27.jpg"));
                g.drawImage(bloco7, 15, 100, 280, 220, null);
                g.drawImage(bloco8, 300, 100, 280, 220, null);
                g.drawImage(bloco9, 15, 330, 280, 220, null);
                g.setFont(new Font("TimesRoman", Font.PLAIN, 9));
                g.drawString("Página 5 de " + arredondar, 260, 800);
            } catch (IOException ex) {
                Logger.getLogger(Impressao.class.getName()).log(Level.SEVERE, null, ex);
            }
            return Printable.PAGE_EXISTS;
        }
    }

    class pagina28 implements Printable {

        @Override
        public int print(Graphics g, PageFormat pf, int pageIndex) throws PrinterException {
            Graphics2D g2d = (Graphics2D) g;
            g2d.translate(pf.getImageableX(), pf.getImageableY());
            int arredondar = (int) Math.round(((double) qtd / 6) + 0.4d);
            try {
                Cabecalho(g, pf, pageIndex);
                BufferedImage bloco7 = ImageIO.read(new File("src/pacssid/temp/25.jpg"));
                BufferedImage bloco8 = ImageIO.read(new File("src/pacssid/temp/26.jpg"));
                BufferedImage bloco9 = ImageIO.read(new File("src/pacssid/temp/27.jpg"));
                BufferedImage bloco10 = ImageIO.read(new File("src/pacssid/temp/28.jpg"));
                g.drawImage(bloco7, 15, 100, 280, 220, null);
                g.drawImage(bloco8, 300, 100, 280, 220, null);
                g.drawImage(bloco9, 15, 330, 280, 220, null);
                g.drawImage(bloco10, 300, 330, 280, 220, null);
                g.setFont(new Font("TimesRoman", Font.PLAIN, 9));
                g.drawString("Página 5 de " + arredondar, 260, 800);
            } catch (IOException ex) {
                Logger.getLogger(Impressao.class.getName()).log(Level.SEVERE, null, ex);
            }
            return Printable.PAGE_EXISTS;
        }
    }

    class pagina29 implements Printable {

        @Override
        public int print(Graphics g, PageFormat pf, int pageIndex) throws PrinterException {
            Graphics2D g2d = (Graphics2D) g;
            g2d.translate(pf.getImageableX(), pf.getImageableY());
            int arredondar = (int) Math.round(((double) qtd / 6) + 0.4d);
            try {
                Cabecalho(g, pf, pageIndex);
                BufferedImage bloco7 = ImageIO.read(new File("src/pacssid/temp/25.jpg"));
                BufferedImage bloco8 = ImageIO.read(new File("src/pacssid/temp/26.jpg"));
                BufferedImage bloco9 = ImageIO.read(new File("src/pacssid/temp/27.jpg"));
                BufferedImage bloco10 = ImageIO.read(new File("src/pacssid/temp/28.jpg"));
                BufferedImage bloco11 = ImageIO.read(new File("src/pacssid/temp/29.jpg"));
                g.drawImage(bloco7, 15, 100, 280, 220, null);
                g.drawImage(bloco8, 300, 100, 280, 220, null);
                g.drawImage(bloco9, 15, 330, 280, 220, null);
                g.drawImage(bloco10, 300, 330, 280, 220, null);
                g.drawImage(bloco11, 15, 560, 280, 220, null);
                g.setFont(new Font("TimesRoman", Font.PLAIN, 9));
                g.drawString("Página 5 de " + arredondar, 260, 800);
            } catch (IOException ex) {
                Logger.getLogger(Impressao.class.getName()).log(Level.SEVERE, null, ex);
            }
            return Printable.PAGE_EXISTS;
        }
    }

    class pagina30 implements Printable {

        @Override
        public int print(Graphics g, PageFormat pf, int pageIndex) throws PrinterException {
            Graphics2D g2d = (Graphics2D) g;
            g2d.translate(pf.getImageableX(), pf.getImageableY());
            int arredondar = (int) Math.round(((double) qtd / 6) + 0.4d);
            try {
                Cabecalho(g, pf, pageIndex);
                BufferedImage bloco7 = ImageIO.read(new File("src/pacssid/temp/25.jpg"));
                BufferedImage bloco8 = ImageIO.read(new File("src/pacssid/temp/26.jpg"));
                BufferedImage bloco9 = ImageIO.read(new File("src/pacssid/temp/27.jpg"));
                BufferedImage bloco10 = ImageIO.read(new File("src/pacssid/temp/28.jpg"));
                BufferedImage bloco11 = ImageIO.read(new File("src/pacssid/temp/29.jpg"));
                BufferedImage bloco12 = ImageIO.read(new File("src/pacssid/temp/30.jpg"));
                g.drawImage(bloco7, 15, 100, 280, 220, null);
                g.drawImage(bloco8, 300, 100, 280, 220, null);
                g.drawImage(bloco9, 15, 330, 280, 220, null);
                g.drawImage(bloco10, 300, 330, 280, 220, null);
                g.drawImage(bloco11, 15, 560, 280, 220, null);
                g.drawImage(bloco12, 300, 560, 280, 220, null);
                g.setFont(new Font("TimesRoman", Font.PLAIN, 9));
                g.drawString("Página 5 de " + arredondar, 260, 800);
            } catch (IOException ex) {
                Logger.getLogger(Impressao.class.getName()).log(Level.SEVERE, null, ex);
            }
            return Printable.PAGE_EXISTS;
        }
    }

    class pagina31 implements Printable {

        @Override
        public int print(Graphics g, PageFormat pf, int pageIndex) throws PrinterException {
            Graphics2D g2d = (Graphics2D) g;
            g2d.translate(pf.getImageableX(), pf.getImageableY());
            int arredondar = (int) Math.round(((double) qtd / 6) + 0.4d);
            try {
                Cabecalho(g, pf, pageIndex);
                BufferedImage bloco7 = ImageIO.read(new File("src/pacssid/temp/31.jpg"));
                g.drawImage(bloco7, 15, 100, 280, 220, null);
                g.setFont(new Font("TimesRoman", Font.PLAIN, 9));
                g.drawString("Página 5 de " + arredondar, 260, 800);
            } catch (IOException ex) {
                Logger.getLogger(Impressao.class.getName()).log(Level.SEVERE, null, ex);
            }
            return Printable.PAGE_EXISTS;
        }
    }

    class pagina32 implements Printable {

        @Override
        public int print(Graphics g, PageFormat pf, int pageIndex) throws PrinterException {
            Graphics2D g2d = (Graphics2D) g;
            g2d.translate(pf.getImageableX(), pf.getImageableY());
            int arredondar = (int) Math.round(((double) qtd / 6) + 0.4d);
            try {
                Cabecalho(g, pf, pageIndex);
                BufferedImage bloco7 = ImageIO.read(new File("src/pacssid/temp/31.jpg"));
                BufferedImage bloco8 = ImageIO.read(new File("src/pacssid/temp/32.jpg"));
                g.drawImage(bloco7, 15, 100, 280, 220, null);
                g.drawImage(bloco8, 300, 100, 280, 220, null);
                g.setFont(new Font("TimesRoman", Font.PLAIN, 9));
                g.drawString("Página 6 de " + arredondar, 260, 800);
            } catch (IOException ex) {
                Logger.getLogger(Impressao.class.getName()).log(Level.SEVERE, null, ex);
            }
            return Printable.PAGE_EXISTS;
        }
    }

    class pagina33 implements Printable {

        @Override
        public int print(Graphics g, PageFormat pf, int pageIndex) throws PrinterException {
            Graphics2D g2d = (Graphics2D) g;
            g2d.translate(pf.getImageableX(), pf.getImageableY());
            int arredondar = (int) Math.round(((double) qtd / 6) + 0.4d);
            try {
                Cabecalho(g, pf, pageIndex);
                BufferedImage bloco7 = ImageIO.read(new File("src/pacssid/temp/31.jpg"));
                BufferedImage bloco8 = ImageIO.read(new File("src/pacssid/temp/32.jpg"));
                BufferedImage bloco9 = ImageIO.read(new File("src/pacssid/temp/33.jpg"));
                g.drawImage(bloco7, 15, 100, 280, 220, null);
                g.drawImage(bloco8, 300, 100, 280, 220, null);
                g.drawImage(bloco9, 15, 330, 280, 220, null);
                g.setFont(new Font("TimesRoman", Font.PLAIN, 9));
                g.drawString("Página 6 de " + arredondar, 260, 800);
            } catch (IOException ex) {
                Logger.getLogger(Impressao.class.getName()).log(Level.SEVERE, null, ex);
            }
            return Printable.PAGE_EXISTS;
        }
    }

    class pagina34 implements Printable {

        @Override
        public int print(Graphics g, PageFormat pf, int pageIndex) throws PrinterException {
            Graphics2D g2d = (Graphics2D) g;
            g2d.translate(pf.getImageableX(), pf.getImageableY());
            int arredondar = (int) Math.round(((double) qtd / 6) + 0.4d);
            try {
                Cabecalho(g, pf, pageIndex);
                BufferedImage bloco7 = ImageIO.read(new File("src/pacssid/temp/31.jpg"));
                BufferedImage bloco8 = ImageIO.read(new File("src/pacssid/temp/32.jpg"));
                BufferedImage bloco9 = ImageIO.read(new File("src/pacssid/temp/33.jpg"));
                BufferedImage bloco10 = ImageIO.read(new File("src/pacssid/temp/34.jpg"));
                g.drawImage(bloco7, 15, 100, 280, 220, null);
                g.drawImage(bloco8, 300, 100, 280, 220, null);
                g.drawImage(bloco9, 15, 330, 280, 220, null);
                g.drawImage(bloco10, 300, 330, 280, 220, null);
                g.setFont(new Font("TimesRoman", Font.PLAIN, 9));
                g.drawString("Página 6 de " + arredondar, 260, 800);
            } catch (IOException ex) {
                Logger.getLogger(Impressao.class.getName()).log(Level.SEVERE, null, ex);
            }
            return Printable.PAGE_EXISTS;
        }
    }

    class pagina35 implements Printable {

        @Override
        public int print(Graphics g, PageFormat pf, int pageIndex) throws PrinterException {
            Graphics2D g2d = (Graphics2D) g;
            g2d.translate(pf.getImageableX(), pf.getImageableY());
            int arredondar = (int) Math.round(((double) qtd / 6) + 0.4d);
            try {
                Cabecalho(g, pf, pageIndex);
                BufferedImage bloco7 = ImageIO.read(new File("src/pacssid/temp/31.jpg"));
                BufferedImage bloco8 = ImageIO.read(new File("src/pacssid/temp/32.jpg"));
                BufferedImage bloco9 = ImageIO.read(new File("src/pacssid/temp/33.jpg"));
                BufferedImage bloco10 = ImageIO.read(new File("src/pacssid/temp/34.jpg"));
                BufferedImage bloco11 = ImageIO.read(new File("src/pacssid/temp/35.jpg"));
                g.drawImage(bloco7, 15, 100, 280, 220, null);
                g.drawImage(bloco8, 300, 100, 280, 220, null);
                g.drawImage(bloco9, 15, 330, 280, 220, null);
                g.drawImage(bloco10, 300, 330, 280, 220, null);
                g.drawImage(bloco11, 15, 560, 280, 220, null);
                g.setFont(new Font("TimesRoman", Font.PLAIN, 9));
                g.drawString("Página 6 de " + arredondar, 260, 800);
            } catch (IOException ex) {
                Logger.getLogger(Impressao.class.getName()).log(Level.SEVERE, null, ex);
            }
            return Printable.PAGE_EXISTS;
        }
    }

    class pagina36 implements Printable {

        @Override
        public int print(Graphics g, PageFormat pf, int pageIndex) throws PrinterException {
            Graphics2D g2d = (Graphics2D) g;
            g2d.translate(pf.getImageableX(), pf.getImageableY());
            int arredondar = (int) Math.round(((double) qtd / 6) + 0.4d);
            try {
                Cabecalho(g, pf, pageIndex);
                BufferedImage bloco7 = ImageIO.read(new File("src/pacssid/temp/31.jpg"));
                BufferedImage bloco8 = ImageIO.read(new File("src/pacssid/temp/32.jpg"));
                BufferedImage bloco9 = ImageIO.read(new File("src/pacssid/temp/33.jpg"));
                BufferedImage bloco10 = ImageIO.read(new File("src/pacssid/temp/34.jpg"));
                BufferedImage bloco11 = ImageIO.read(new File("src/pacssid/temp/35.jpg"));
                BufferedImage bloco12 = ImageIO.read(new File("src/pacssid/temp/36.jpg"));
                g.drawImage(bloco7, 15, 100, 280, 220, null);
                g.drawImage(bloco8, 300, 100, 280, 220, null);
                g.drawImage(bloco9, 15, 330, 280, 220, null);
                g.drawImage(bloco10, 300, 330, 280, 220, null);
                g.drawImage(bloco11, 15, 560, 280, 220, null);
                g.drawImage(bloco12, 300, 560, 280, 220, null);
                g.setFont(new Font("TimesRoman", Font.PLAIN, 9));
                g.drawString("Página 6 de " + arredondar, 260, 800);
            } catch (IOException ex) {
                Logger.getLogger(Impressao.class.getName()).log(Level.SEVERE, null, ex);
            }
            return Printable.PAGE_EXISTS;
        }
    }

    class pagina37 implements Printable {

        @Override
        public int print(Graphics g, PageFormat pf, int pageIndex) throws PrinterException {
            Graphics2D g2d = (Graphics2D) g;
            g2d.translate(pf.getImageableX(), pf.getImageableY());
            int arredondar = (int) Math.round(((double) qtd / 6) + 0.4d);
            try {
                Cabecalho(g, pf, pageIndex);
                BufferedImage bloco7 = ImageIO.read(new File("src/pacssid/temp/37.jpg"));
                g.drawImage(bloco7, 15, 100, 280, 220, null);
                g.setFont(new Font("TimesRoman", Font.PLAIN, 9));
                g.drawString("Página 7 de " + arredondar, 260, 800);
            } catch (IOException ex) {
                Logger.getLogger(Impressao.class.getName()).log(Level.SEVERE, null, ex);
            }
            return Printable.PAGE_EXISTS;
        }
    }

    class pagina38 implements Printable {

        @Override
        public int print(Graphics g, PageFormat pf, int pageIndex) throws PrinterException {
            Graphics2D g2d = (Graphics2D) g;
            g2d.translate(pf.getImageableX(), pf.getImageableY());
            int arredondar = (int) Math.round(((double) qtd / 6) + 0.4d);
            try {
                Cabecalho(g, pf, pageIndex);
                BufferedImage bloco7 = ImageIO.read(new File("src/pacssid/temp/37.jpg"));
                BufferedImage bloco8 = ImageIO.read(new File("src/pacssid/temp/38.jpg"));
                g.drawImage(bloco7, 15, 100, 280, 220, null);
                g.drawImage(bloco8, 300, 100, 280, 220, null);
                g.setFont(new Font("TimesRoman", Font.PLAIN, 9));
                g.drawString("Página 7 de " + arredondar, 260, 800);
            } catch (IOException ex) {
                Logger.getLogger(Impressao.class.getName()).log(Level.SEVERE, null, ex);
            }
            return Printable.PAGE_EXISTS;
        }
    }

    class pagina39 implements Printable {

        @Override
        public int print(Graphics g, PageFormat pf, int pageIndex) throws PrinterException {
            Graphics2D g2d = (Graphics2D) g;
            g2d.translate(pf.getImageableX(), pf.getImageableY());
            int arredondar = (int) Math.round(((double) qtd / 6) + 0.4d);
            try {
                Cabecalho(g, pf, pageIndex);
                BufferedImage bloco7 = ImageIO.read(new File("src/pacssid/temp/37.jpg"));
                BufferedImage bloco8 = ImageIO.read(new File("src/pacssid/temp/38.jpg"));
                BufferedImage bloco9 = ImageIO.read(new File("src/pacssid/temp/39.jpg"));
                g.drawImage(bloco7, 15, 100, 280, 220, null);
                g.drawImage(bloco8, 300, 100, 280, 220, null);
                g.drawImage(bloco9, 15, 330, 280, 220, null);
                g.setFont(new Font("TimesRoman", Font.PLAIN, 9));
                g.drawString("Página 7 de " + arredondar, 260, 800);
            } catch (IOException ex) {
                Logger.getLogger(Impressao.class.getName()).log(Level.SEVERE, null, ex);
            }
            return Printable.PAGE_EXISTS;
        }
    }

    class pagina40 implements Printable {

        @Override
        public int print(Graphics g, PageFormat pf, int pageIndex) throws PrinterException {
            Graphics2D g2d = (Graphics2D) g;
            g2d.translate(pf.getImageableX(), pf.getImageableY());
            int arredondar = (int) Math.round(((double) qtd / 6) + 0.4d);
            try {
                Cabecalho(g, pf, pageIndex);
                BufferedImage bloco7 = ImageIO.read(new File("src/pacssid/temp/37.jpg"));
                BufferedImage bloco8 = ImageIO.read(new File("src/pacssid/temp/38.jpg"));
                BufferedImage bloco9 = ImageIO.read(new File("src/pacssid/temp/39.jpg"));
                BufferedImage bloco10 = ImageIO.read(new File("src/pacssid/temp/40.jpg"));
                g.drawImage(bloco7, 15, 100, 280, 220, null);
                g.drawImage(bloco8, 300, 100, 280, 220, null);
                g.drawImage(bloco9, 15, 330, 280, 220, null);
                g.drawImage(bloco10, 300, 330, 280, 220, null);
                g.setFont(new Font("TimesRoman", Font.PLAIN, 9));
                g.drawString("Página 7 de " + arredondar, 260, 800);
            } catch (IOException ex) {
                Logger.getLogger(Impressao.class.getName()).log(Level.SEVERE, null, ex);
            }
            return Printable.PAGE_EXISTS;
        }
    }

    class pagina41 implements Printable {

        @Override
        public int print(Graphics g, PageFormat pf, int pageIndex) throws PrinterException {
            Graphics2D g2d = (Graphics2D) g;
            g2d.translate(pf.getImageableX(), pf.getImageableY());
            int arredondar = (int) Math.round(((double) qtd / 6) + 0.4d);
            try {
                Cabecalho(g, pf, pageIndex);
                BufferedImage bloco7 = ImageIO.read(new File("src/pacssid/temp/37.jpg"));
                BufferedImage bloco8 = ImageIO.read(new File("src/pacssid/temp/38.jpg"));
                BufferedImage bloco9 = ImageIO.read(new File("src/pacssid/temp/39.jpg"));
                BufferedImage bloco10 = ImageIO.read(new File("src/pacssid/temp/40.jpg"));
                BufferedImage bloco11 = ImageIO.read(new File("src/pacssid/temp/41.jpg"));
                g.drawImage(bloco7, 15, 100, 280, 220, null);
                g.drawImage(bloco8, 300, 100, 280, 220, null);
                g.drawImage(bloco9, 15, 330, 280, 220, null);
                g.drawImage(bloco10, 300, 330, 280, 220, null);
                g.drawImage(bloco11, 15, 560, 280, 220, null);
                g.setFont(new Font("TimesRoman", Font.PLAIN, 9));
                g.drawString("Página 7 de " + arredondar, 260, 800);
            } catch (IOException ex) {
                Logger.getLogger(Impressao.class.getName()).log(Level.SEVERE, null, ex);
            }
            return Printable.PAGE_EXISTS;
        }
    }

    class pagina42 implements Printable {

        @Override
        public int print(Graphics g, PageFormat pf, int pageIndex) throws PrinterException {
            Graphics2D g2d = (Graphics2D) g;
            g2d.translate(pf.getImageableX(), pf.getImageableY());
            int arredondar = (int) Math.round(((double) qtd / 6) + 0.4d);
            try {
                Cabecalho(g, pf, pageIndex);
                BufferedImage bloco7 = ImageIO.read(new File("src/pacssid/temp/37.jpg"));
                BufferedImage bloco8 = ImageIO.read(new File("src/pacssid/temp/38.jpg"));
                BufferedImage bloco9 = ImageIO.read(new File("src/pacssid/temp/39.jpg"));
                BufferedImage bloco10 = ImageIO.read(new File("src/pacssid/temp/40.jpg"));
                BufferedImage bloco11 = ImageIO.read(new File("src/pacssid/temp/41.jpg"));
                BufferedImage bloco12 = ImageIO.read(new File("src/pacssid/temp/42.jpg"));
                g.drawImage(bloco7, 15, 100, 280, 220, null);
                g.drawImage(bloco8, 300, 100, 280, 220, null);
                g.drawImage(bloco9, 15, 330, 280, 220, null);
                g.drawImage(bloco10, 300, 330, 280, 220, null);
                g.drawImage(bloco11, 15, 560, 280, 220, null);
                g.drawImage(bloco12, 300, 560, 280, 220, null);
                g.setFont(new Font("TimesRoman", Font.PLAIN, 9));
                g.drawString("Página 7 de " + arredondar, 260, 800);
            } catch (IOException ex) {
                Logger.getLogger(Impressao.class.getName()).log(Level.SEVERE, null, ex);
            }
            return Printable.PAGE_EXISTS;
        }
    }

    class pagina43 implements Printable {

        @Override
        public int print(Graphics g, PageFormat pf, int pageIndex) throws PrinterException {
            Graphics2D g2d = (Graphics2D) g;
            g2d.translate(pf.getImageableX(), pf.getImageableY());
            int arredondar = (int) Math.round(((double) qtd / 6) + 0.4d);
            try {
                Cabecalho(g, pf, pageIndex);
                BufferedImage bloco7 = ImageIO.read(new File("src/pacssid/temp/43.jpg"));
                g.drawImage(bloco7, 15, 100, 280, 220, null);

                g.setFont(new Font("TimesRoman", Font.PLAIN, 9));
                g.drawString("Página 8 de " + arredondar, 260, 800);
            } catch (IOException ex) {
                Logger.getLogger(Impressao.class.getName()).log(Level.SEVERE, null, ex);
            }
            return Printable.PAGE_EXISTS;
        }
    }

    class pagina44 implements Printable {

        @Override
        public int print(Graphics g, PageFormat pf, int pageIndex) throws PrinterException {
            Graphics2D g2d = (Graphics2D) g;
            g2d.translate(pf.getImageableX(), pf.getImageableY());
            int arredondar = (int) Math.round(((double) qtd / 6) + 0.4d);
            try {
                Cabecalho(g, pf, pageIndex);
                BufferedImage bloco7 = ImageIO.read(new File("src/pacssid/temp/43.jpg"));
                BufferedImage bloco8 = ImageIO.read(new File("src/pacssid/temp/44.jpg"));
                g.drawImage(bloco7, 15, 100, 280, 220, null);
                g.drawImage(bloco8, 300, 100, 280, 220, null);
                g.setFont(new Font("TimesRoman", Font.PLAIN, 9));
                g.drawString("Página 8 de " + arredondar, 260, 800);
            } catch (IOException ex) {
                Logger.getLogger(Impressao.class.getName()).log(Level.SEVERE, null, ex);
            }
            return Printable.PAGE_EXISTS;
        }
    }

    class pagina45 implements Printable {

        @Override
        public int print(Graphics g, PageFormat pf, int pageIndex) throws PrinterException {
            Graphics2D g2d = (Graphics2D) g;
            g2d.translate(pf.getImageableX(), pf.getImageableY());
            int arredondar = (int) Math.round(((double) qtd / 6) + 0.4d);
            try {
                Cabecalho(g, pf, pageIndex);
                BufferedImage bloco7 = ImageIO.read(new File("src/pacssid/temp/43.jpg"));
                BufferedImage bloco8 = ImageIO.read(new File("src/pacssid/temp/44.jpg"));
                BufferedImage bloco9 = ImageIO.read(new File("src/pacssid/temp/45.jpg"));
                g.drawImage(bloco7, 15, 100, 280, 220, null);
                g.drawImage(bloco8, 300, 100, 280, 220, null);
                g.drawImage(bloco9, 15, 330, 280, 220, null);
                g.setFont(new Font("TimesRoman", Font.PLAIN, 9));
                g.drawString("Página 8 de " + arredondar, 260, 800);
            } catch (IOException ex) {
                Logger.getLogger(Impressao.class.getName()).log(Level.SEVERE, null, ex);
            }
            return Printable.PAGE_EXISTS;
        }
    }

    class pagina46 implements Printable {

        @Override
        public int print(Graphics g, PageFormat pf, int pageIndex) throws PrinterException {
            Graphics2D g2d = (Graphics2D) g;
            g2d.translate(pf.getImageableX(), pf.getImageableY());
            int arredondar = (int) Math.round(((double) qtd / 6) + 0.4d);
            try {
                Cabecalho(g, pf, pageIndex);
                BufferedImage bloco7 = ImageIO.read(new File("src/pacssid/temp/43.jpg"));
                BufferedImage bloco8 = ImageIO.read(new File("src/pacssid/temp/44.jpg"));
                BufferedImage bloco9 = ImageIO.read(new File("src/pacssid/temp/45.jpg"));
                BufferedImage bloco10 = ImageIO.read(new File("src/pacssid/temp/46.jpg"));
                g.drawImage(bloco7, 15, 100, 280, 220, null);
                g.drawImage(bloco8, 300, 100, 280, 220, null);
                g.drawImage(bloco9, 15, 330, 280, 220, null);
                g.drawImage(bloco10, 300, 330, 280, 220, null);
                g.setFont(new Font("TimesRoman", Font.PLAIN, 9));
                g.drawString("Página 8 de " + arredondar, 260, 800);
            } catch (IOException ex) {
                Logger.getLogger(Impressao.class.getName()).log(Level.SEVERE, null, ex);
            }
            return Printable.PAGE_EXISTS;
        }
    }

    class pagina47 implements Printable {

        @Override
        public int print(Graphics g, PageFormat pf, int pageIndex) throws PrinterException {
            Graphics2D g2d = (Graphics2D) g;
            g2d.translate(pf.getImageableX(), pf.getImageableY());
            int arredondar = (int) Math.round(((double) qtd / 6) + 0.4d);
            try {
                Cabecalho(g, pf, pageIndex);
                BufferedImage bloco7 = ImageIO.read(new File("src/pacssid/temp/43.jpg"));
                BufferedImage bloco8 = ImageIO.read(new File("src/pacssid/temp/44.jpg"));
                BufferedImage bloco9 = ImageIO.read(new File("src/pacssid/temp/45.jpg"));
                BufferedImage bloco10 = ImageIO.read(new File("src/pacssid/temp/46.jpg"));
                BufferedImage bloco11 = ImageIO.read(new File("src/pacssid/temp/47.jpg"));
                g.drawImage(bloco7, 15, 100, 280, 220, null);
                g.drawImage(bloco8, 300, 100, 280, 220, null);
                g.drawImage(bloco9, 15, 330, 280, 220, null);
                g.drawImage(bloco10, 300, 330, 280, 220, null);
                g.drawImage(bloco11, 15, 560, 280, 220, null);
                g.setFont(new Font("TimesRoman", Font.PLAIN, 9));
                g.drawString("Página 8 de " + arredondar, 260, 800);
            } catch (IOException ex) {
                Logger.getLogger(Impressao.class.getName()).log(Level.SEVERE, null, ex);
            }
            return Printable.PAGE_EXISTS;
        }
    }

    class pagina48 implements Printable {

        @Override
        public int print(Graphics g, PageFormat pf, int pageIndex) throws PrinterException {
            Graphics2D g2d = (Graphics2D) g;
            g2d.translate(pf.getImageableX(), pf.getImageableY());
            int arredondar = (int) Math.round(((double) qtd / 6) + 0.4d);
            try {
                Cabecalho(g, pf, pageIndex);
                BufferedImage bloco7 = ImageIO.read(new File("src/pacssid/temp/43.jpg"));
                BufferedImage bloco8 = ImageIO.read(new File("src/pacssid/temp/44.jpg"));
                BufferedImage bloco9 = ImageIO.read(new File("src/pacssid/temp/45.jpg"));
                BufferedImage bloco10 = ImageIO.read(new File("src/pacssid/temp/46.jpg"));
                BufferedImage bloco11 = ImageIO.read(new File("src/pacssid/temp/47.jpg"));
                BufferedImage bloco12 = ImageIO.read(new File("src/pacssid/temp/48.jpg"));
                g.drawImage(bloco7, 15, 100, 280, 220, null);
                g.drawImage(bloco8, 300, 100, 280, 220, null);
                g.drawImage(bloco9, 15, 330, 280, 220, null);
                g.drawImage(bloco10, 300, 330, 280, 220, null);
                g.drawImage(bloco11, 15, 560, 280, 220, null);
                g.drawImage(bloco12, 300, 560, 280, 220, null);
                g.setFont(new Font("TimesRoman", Font.PLAIN, 9));
                g.drawString("Página 8 de " + arredondar, 260, 800);
            } catch (IOException ex) {
                Logger.getLogger(Impressao.class.getName()).log(Level.SEVERE, null, ex);
            }
            return Printable.PAGE_EXISTS;
        }
    }

    class pagina49 implements Printable {

        @Override
        public int print(Graphics g, PageFormat pf, int pageIndex) throws PrinterException {
            Graphics2D g2d = (Graphics2D) g;
            g2d.translate(pf.getImageableX(), pf.getImageableY());
            int arredondar = (int) Math.round(((double) qtd / 6) + 0.4d);
            try {
                Cabecalho(g, pf, pageIndex);
                BufferedImage bloco7 = ImageIO.read(new File("src/pacssid/temp/49.jpg"));
                g.drawImage(bloco7, 15, 100, 280, 220, null);
                g.setFont(new Font("TimesRoman", Font.PLAIN, 9));
                g.drawString("Página 9 de " + arredondar, 260, 800);
            } catch (IOException ex) {
                Logger.getLogger(Impressao.class.getName()).log(Level.SEVERE, null, ex);
            }
            return Printable.PAGE_EXISTS;
        }
    }

    class pagina50 implements Printable {

        @Override
        public int print(Graphics g, PageFormat pf, int pageIndex) throws PrinterException {
            Graphics2D g2d = (Graphics2D) g;
            g2d.translate(pf.getImageableX(), pf.getImageableY());
            int arredondar = (int) Math.round(((double) qtd / 6) + 0.4d);
            try {
                Cabecalho(g, pf, pageIndex);
                BufferedImage bloco7 = ImageIO.read(new File("src/pacssid/temp/49.jpg"));
                BufferedImage bloco8 = ImageIO.read(new File("src/pacssid/temp/50.jpg"));
                g.drawImage(bloco7, 15, 100, 280, 220, null);
                g.drawImage(bloco8, 300, 100, 280, 220, null);
                g.setFont(new Font("TimesRoman", Font.PLAIN, 9));
                g.drawString("Página 9 de " + arredondar, 260, 800);
            } catch (IOException ex) {
                Logger.getLogger(Impressao.class.getName()).log(Level.SEVERE, null, ex);
            }
            return Printable.PAGE_EXISTS;
        }
    }

    class pagina51 implements Printable {

        @Override
        public int print(Graphics g, PageFormat pf, int pageIndex) throws PrinterException {
            Graphics2D g2d = (Graphics2D) g;
            g2d.translate(pf.getImageableX(), pf.getImageableY());
            int arredondar = (int) Math.round(((double) qtd / 6) + 0.4d);
            try {
                Cabecalho(g, pf, pageIndex);
                BufferedImage bloco7 = ImageIO.read(new File("src/pacssid/temp/49.jpg"));
                BufferedImage bloco8 = ImageIO.read(new File("src/pacssid/temp/50.jpg"));
                BufferedImage bloco9 = ImageIO.read(new File("src/pacssid/temp/51.jpg"));
                g.drawImage(bloco7, 15, 100, 280, 220, null);
                g.drawImage(bloco8, 300, 100, 280, 220, null);
                g.drawImage(bloco9, 15, 330, 280, 220, null);
                g.setFont(new Font("TimesRoman", Font.PLAIN, 9));
                g.drawString("Página 9 de " + arredondar, 260, 800);
            } catch (IOException ex) {
                Logger.getLogger(Impressao.class.getName()).log(Level.SEVERE, null, ex);
            }
            return Printable.PAGE_EXISTS;
        }
    }

    class pagina52 implements Printable {

        @Override
        public int print(Graphics g, PageFormat pf, int pageIndex) throws PrinterException {
            Graphics2D g2d = (Graphics2D) g;
            g2d.translate(pf.getImageableX(), pf.getImageableY());
            int arredondar = (int) Math.round(((double) qtd / 6) + 0.4d);
            try {
                Cabecalho(g, pf, pageIndex);
                BufferedImage bloco7 = ImageIO.read(new File("src/pacssid/temp/49.jpg"));
                BufferedImage bloco8 = ImageIO.read(new File("src/pacssid/temp/50.jpg"));
                BufferedImage bloco9 = ImageIO.read(new File("src/pacssid/temp/51.jpg"));
                BufferedImage bloco10 = ImageIO.read(new File("src/pacssid/temp/52.jpg"));
                g.drawImage(bloco7, 15, 100, 280, 220, null);
                g.drawImage(bloco8, 300, 100, 280, 220, null);
                g.drawImage(bloco9, 15, 330, 280, 220, null);
                g.drawImage(bloco10, 300, 330, 280, 220, null);
                g.setFont(new Font("TimesRoman", Font.PLAIN, 9));
                g.drawString("Página 9 de " + arredondar, 260, 800);
            } catch (IOException ex) {
                Logger.getLogger(Impressao.class.getName()).log(Level.SEVERE, null, ex);
            }
            return Printable.PAGE_EXISTS;
        }
    }

    class pagina53 implements Printable {

        @Override
        public int print(Graphics g, PageFormat pf, int pageIndex) throws PrinterException {
            Graphics2D g2d = (Graphics2D) g;
            g2d.translate(pf.getImageableX(), pf.getImageableY());
            int arredondar = (int) Math.round(((double) qtd / 6) + 0.4d);
            try {
                Cabecalho(g, pf, pageIndex);
                BufferedImage bloco7 = ImageIO.read(new File("src/pacssid/temp/49.jpg"));
                BufferedImage bloco8 = ImageIO.read(new File("src/pacssid/temp/50.jpg"));
                BufferedImage bloco9 = ImageIO.read(new File("src/pacssid/temp/51.jpg"));
                BufferedImage bloco10 = ImageIO.read(new File("src/pacssid/temp/52.jpg"));
                BufferedImage bloco11 = ImageIO.read(new File("src/pacssid/temp/53.jpg"));
                g.drawImage(bloco7, 15, 100, 280, 220, null);
                g.drawImage(bloco8, 300, 100, 280, 220, null);
                g.drawImage(bloco9, 15, 330, 280, 220, null);
                g.drawImage(bloco10, 300, 330, 280, 220, null);
                g.drawImage(bloco11, 15, 560, 280, 220, null);
                g.setFont(new Font("TimesRoman", Font.PLAIN, 9));
                g.drawString("Página 9 de " + arredondar, 260, 800);
            } catch (IOException ex) {
                Logger.getLogger(Impressao.class.getName()).log(Level.SEVERE, null, ex);
            }
            return Printable.PAGE_EXISTS;
        }
    }

    class pagina54 implements Printable {

        @Override
        public int print(Graphics g, PageFormat pf, int pageIndex) throws PrinterException {
            Graphics2D g2d = (Graphics2D) g;
            g2d.translate(pf.getImageableX(), pf.getImageableY());
            int arredondar = (int) Math.round(((double) qtd / 6) + 0.4d);
            try {
                Cabecalho(g, pf, pageIndex);
                BufferedImage bloco7 = ImageIO.read(new File("src/pacssid/temp/49.jpg"));
                BufferedImage bloco8 = ImageIO.read(new File("src/pacssid/temp/50.jpg"));
                BufferedImage bloco9 = ImageIO.read(new File("src/pacssid/temp/51.jpg"));
                BufferedImage bloco10 = ImageIO.read(new File("src/pacssid/temp/52.jpg"));
                BufferedImage bloco11 = ImageIO.read(new File("src/pacssid/temp/53.jpg"));
                BufferedImage bloco12 = ImageIO.read(new File("src/pacssid/temp/54.jpg"));
                g.drawImage(bloco7, 15, 100, 280, 220, null);
                g.drawImage(bloco8, 300, 100, 280, 220, null);
                g.drawImage(bloco9, 15, 330, 280, 220, null);
                g.drawImage(bloco10, 300, 330, 280, 220, null);
                g.drawImage(bloco11, 15, 560, 280, 220, null);
                g.drawImage(bloco12, 300, 560, 280, 220, null);
                g.setFont(new Font("TimesRoman", Font.PLAIN, 9));
                g.drawString("Página 9 de " + arredondar, 260, 800);
            } catch (IOException ex) {
                Logger.getLogger(Impressao.class.getName()).log(Level.SEVERE, null, ex);
            }
            return Printable.PAGE_EXISTS;
        }
    }

    class pagina55 implements Printable {

        @Override
        public int print(Graphics g, PageFormat pf, int pageIndex) throws PrinterException {
            Graphics2D g2d = (Graphics2D) g;
            g2d.translate(pf.getImageableX(), pf.getImageableY());
            int arredondar = (int) Math.round(((double) qtd / 6) + 0.4d);
            try {
                Cabecalho(g, pf, pageIndex);
                BufferedImage bloco7 = ImageIO.read(new File("src/pacssid/temp/55.jpg"));
                g.drawImage(bloco7, 15, 100, 280, 220, null);
                g.setFont(new Font("TimesRoman", Font.PLAIN, 9));
                g.drawString("Página 10 de " + arredondar, 260, 800);
            } catch (IOException ex) {
                Logger.getLogger(Impressao.class.getName()).log(Level.SEVERE, null, ex);
            }
            return Printable.PAGE_EXISTS;
        }
    }

    class pagina56 implements Printable {

        @Override
        public int print(Graphics g, PageFormat pf, int pageIndex) throws PrinterException {
            Graphics2D g2d = (Graphics2D) g;
            g2d.translate(pf.getImageableX(), pf.getImageableY());
            int arredondar = (int) Math.round(((double) qtd / 6) + 0.4d);
            try {
                Cabecalho(g, pf, pageIndex);
                BufferedImage bloco7 = ImageIO.read(new File("src/pacssid/temp/55.jpg"));
                BufferedImage bloco8 = ImageIO.read(new File("src/pacssid/temp/56.jpg"));
                g.drawImage(bloco7, 15, 100, 280, 220, null);
                g.drawImage(bloco8, 300, 100, 280, 220, null);
                g.setFont(new Font("TimesRoman", Font.PLAIN, 9));
                g.drawString("Página 10 de " + arredondar, 260, 800);
            } catch (IOException ex) {
                Logger.getLogger(Impressao.class.getName()).log(Level.SEVERE, null, ex);
            }
            return Printable.PAGE_EXISTS;
        }
    }
}
