/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pacssid.view;

import pacssid.dao.ConectaBanco;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontFormatException;
import static java.awt.Frame.MAXIMIZED_BOTH;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.print.PrinterException;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Scanner;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JOptionPane;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.text.MaskFormatter;

/**
 *
 * @author roberto
 */
public final class Index extends JFrame {

    private JTable tabela;
    private JTableHeader cabecalho;
    private JScrollPane scroll;
    private JPanel painel, rodape, paciente, info_paciente, jplLogo;
    private JTextField textCodigo, textNome, textUnidade, textEstacao, textAcesso;
    private JLabel jlbCodigo, jlbNome, jlbCopy, jlbEstacao, jlbUnidade,
            jlbData, jlbComboUnidades, jlImpressora, jlAcesso, jlVersao;
    private JButton btnAcessar, btnImprimir;
    public String nome, nomes, linhas, codigo, dataexame, datanasc, quantidade,
            acesso, procedimento, impressoras, nomedaImpressora;
    public int linha, linhaTabela, codigoPaciente, indexUnidades;

    public JFormattedTextField jftData1, jftData2;
    public JComboBox<String> comboUnidades, comboImpressoras;
    public int uniSelect, qtd;
    public boolean Sr;
    
    private static final String path = "C:\\Users\\Roberto Carvalho\\Documents\\NetBeansProjects\\PACSSID\\local\\config.txt";
    
    public void criarArquivo() {
        try {
            File diretorio = new File("C:\\Users\\Roberto Carvalho\\Documents\\NetBeansProjects\\PACSSID\\local");
            diretorio.mkdir();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Erro ao criar o diretorio");
        }
    }
    public void getPrinters() {
        PrintService[] impressora = PrintServiceLookup.lookupPrintServices(null, null);
        ArrayList<Impressora> lista = new ArrayList<>();
        for (PrintService ps : impressora) {
            Impressora aux = new Impressora();
            impressoras = ps.getName();
            aux.setNome(impressoras);
            comboImpressoras.addItem(aux.getNome());
        }
    }

    public void CorNaLinha() {
        tabela.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
                    int row, int column) {
                JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, 3);

                Object outrosexames = table.getValueAt(row, 3);
                ImageIcon iconFemale = new ImageIcon("src/pacssid/icons/icon_female.png");
                ImageIcon iconMale = new ImageIcon("src/pacssid/icons/icon_male.png");

                if (column == 3) {
                    if (outrosexames.equals("F")) {

                    } else if (outrosexames.equals("M")) {

                    } else {

                    }
                }
                return label;
            }
        });
    }

    public void PreencherTabela() {
        String unidades;
        unidades = String.valueOf(comboUnidades.getSelectedItem());
        indexUnidades = comboUnidades.getSelectedIndex();
        switch (indexUnidades) {
            case 0:
                unidades = "MATRIZ";
                break;
            case 1:
                unidades = "H";
                break;
            case 2:
                unidades = "COM";
                break;
            default:
                unidades = "GA";
                break;
        }
        if (textCodigo.getText().equals("") && textNome.getText().equals("")) {
            String id, ano, dia, mes, nomePaciente, sexo, modalidade, datanascimento, datatime, descricao, numeroimagens, numeroacesso;
            ConectaBanco banco = new ConectaBanco();
            banco.conexao();
            String sql = "select p.pat_id,p.pat_name,pat_birthdate,p.pat_sex, sr.modality,s.accession_no ,s.study_desc,to_char(s.study_datetime,'DD/MM/YYYY'),s.num_instances from patient p, study s,series sr where\n"
                    + "s.patient_fk = p.pk and sr.study_fk = s.pk and sr.institution like '%" + unidades + "%' order by s.study_datetime desc limit 100";

            try {
                Statement stm = banco.conn.createStatement();
                ResultSet resultado = stm.executeQuery(sql);
                while (resultado.next()) {

                    id = resultado.getString("pat_id");
                    nomePaciente = resultado.getString("pat_name");
                    String nomeDividido = nomePaciente;
                    sexo = resultado.getString("pat_sex");
                    modalidade = resultado.getString("modality");
                    numeroimagens = resultado.getString("num_instances");
                    datatime = resultado.getString("to_char");
                    descricao = resultado.getString("study_desc");
                    datanascimento = resultado.getString("pat_birthdate");
                    numeroacesso = resultado.getString("accession_no");
                    if (numeroacesso == null) {
                        numeroacesso = "null";
                    }
                    if (datanascimento == null) {
                        datanascimento = "00/00/0000";
                    } else {
                        ano = datanascimento.substring(0, 4);
                        dia = datanascimento.substring(datanascimento.length() - 2);
                        mes = datanascimento.substring(4, 6);
                        datanascimento = dia + "/" + mes + "/" + ano;
                    }
                    String[] result = nomeDividido.split("\\^");
                    DefaultTableModel val = (DefaultTableModel) tabela.getModel();
                    val.addRow(new String[]{id, result[0], datanascimento, sexo, modalidade, descricao, numeroacesso, datatime, numeroimagens});

                }
                banco.desconecta();
            } catch (SQLException ex) {
                Logger.getLogger(Index.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else if (!"".equals(textCodigo.getText()) && textNome.getText().equals("")) {
            String id, ano, dia, mes, nomePaciente, sexo, modalidade, datanascimento, datatime, descricao, numeroimagens, numeroacesso;

            unidades = String.valueOf(comboUnidades.getSelectedItem());
            indexUnidades = comboUnidades.getSelectedIndex();
            switch (indexUnidades) {
                case 0:
                    unidades = "MATRIZ";
                    break;
                case 1:
                    unidades = "H";
                    break;
                case 2:
                    unidades = "COM";
                    break;
                default:
            }
            ConectaBanco banco = new ConectaBanco();
            banco.conexao();
            String sql = "select p.pat_id,p.pat_name,pat_birthdate,p.pat_sex, sr.modality ,s.accession_no,s.study_desc,to_char(s.study_datetime,'DD/MM/YYYY'),s.num_instances from patient p, study s,series sr where"
                    + " s.patient_fk = p.pk and sr.study_fk = s.pk and p.pat_id='" + textCodigo.getText() + "' and sr.institution like '%" + unidades + "%' order by s.study_datetime desc limit 100";

            try {
                Statement stm = banco.conn.createStatement();
                ResultSet resultado = stm.executeQuery(sql);
                while (resultado.next()) {
                    id = resultado.getString("pat_id");
                    nomePaciente = resultado.getString("pat_name");
                    String nomeDividido = nomePaciente;
                    sexo = resultado.getString("pat_sex");
                    modalidade = resultado.getString("modality");
                    datatime = resultado.getString("to_char");
                    descricao = resultado.getString("study_desc");
                    numeroimagens = resultado.getString("num_instances");
                    datanascimento = resultado.getString("pat_birthdate");
                    numeroacesso = resultado.getString("accession_no");
                    if (numeroacesso == null) {
                        numeroacesso = "null";
                    }
                    if (datanascimento == null) {
                        datanascimento = "00/00/0000";
                    } else {
                        ano = datanascimento.substring(0, 4);
                        dia = datanascimento.substring(datanascimento.length() - 2);
                        mes = datanascimento.substring(4, 6);
                        datanascimento = dia + "/" + mes + "/" + ano;
                    }
                    String[] result = nomeDividido.split("\\^");
                    DefaultTableModel val = (DefaultTableModel) tabela.getModel();
                    val.addRow(new String[]{id, result[0], datanascimento, sexo, modalidade, descricao, numeroacesso, datatime, numeroimagens});

                }

                banco.desconecta();
            } catch (SQLException ex) {
                Logger.getLogger(Index.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            String id, ano, dia, mes, nomePaciente, sexo, modalidade, datanascimento, datatime, descricao, numeroimagens, numeroacesso;
            unidades = String.valueOf(comboUnidades.getSelectedItem());
            indexUnidades = comboUnidades.getSelectedIndex();
            switch (indexUnidades) {
                case 0:
                    unidades = "MATRIZ";
                    break;
                case 1:
                    unidades = "H";
                    break;
                case 2:
                    unidades = "COM";
                    break;
                default:
            }
            ConectaBanco banco = new ConectaBanco();
            banco.conexao();
            String sql = "select p.pat_id,p.pat_name,pat_birthdate,p.pat_sex, sr.modality ,s.accession_no,s.study_desc,to_char(s.study_datetime,'DD/MM/YYYY'),s.num_instances from patient p, study s,series sr where"
                    + " s.patient_fk = p.pk and sr.study_fk = s.pk and p.pat_name like'" + textNome.getText() + "%' and sr.institution like '%" + unidades + "%' order by s.study_datetime desc limit 100";

            try {
                Statement stm = banco.conn.createStatement();
                ResultSet resultado = stm.executeQuery(sql);
                while (resultado.next()) {
                    id = resultado.getString("pat_id");
                    nomePaciente = resultado.getString("pat_name");
                    String nomeDividido = nomePaciente;
                    sexo = resultado.getString("pat_sex");
                    modalidade = resultado.getString("modality");
                    datatime = resultado.getString("to_char");
                    descricao = resultado.getString("study_desc");
                    numeroimagens = resultado.getString("num_instances");
                    datanascimento = resultado.getString("pat_birthdate");
                    numeroacesso = resultado.getString("accession_no");
                    if (numeroacesso == null) {
                        numeroacesso = "null";
                    }
                    if (datanascimento == null) {
                        datanascimento = "00/00/0000";
                    } else {
                        ano = datanascimento.substring(0, 4);
                        dia = datanascimento.substring(datanascimento.length() - 2);
                        mes = datanascimento.substring(4, 6);
                        datanascimento = dia + "/" + mes + "/" + ano;
                    }
                    String[] result = nomeDividido.split("\\^");
                    DefaultTableModel val = (DefaultTableModel) tabela.getModel();
                    val.addRow(new String[]{id, result[0], datanascimento, sexo, modalidade, descricao, numeroacesso, datatime, numeroimagens});
                }
                banco.desconecta();
            } catch (SQLException ex) {
                Logger.getLogger(Index.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        tabela.changeSelection(0, 9, false, false);

        CorNaLinha();
    }

    public Index() throws FontFormatException, IOException {
        super(" AGIPRINT | Sistema de Impressão por Demanda  |  Versão: 2.0");
        // exec();
        Container tela = getContentPane();
        Toolkit tk = Toolkit.getDefaultToolkit();
        Dimension d = tk.getScreenSize();
        super.setSize(d.width, d.height - 30);
        super.setLocationRelativeTo(null);
        super.setVisible(true);
        super.setLayout(null);
        super.setExtendedState(MAXIMIZED_BOTH);
        super.setResizable(true);

        ImageIcon imagemTituloJanela = new ImageIcon("src/pacssid/icons/Sem título.png");
        setIconImage(imagemTituloJanela.getImage());
        ImageIcon iconBtnPesquisa = new ImageIcon("src/pacssid/icons/if_system-search_118797.png");
        ImageIcon iconPrint = new ImageIcon("src/pacssid/icons/print.png");
        ImageIcon iconLogo = new ImageIcon("src/pacssid/icons/logo.png");

        painel = new JPanel();
        tela.add(painel);
        painel.setBounds(0, 0, d.width, d.height);
        painel.setBackground(new Color(227, 232, 245));//227, 232, 245
        painel.setLayout(null);
        painel.setVisible(true);

        jplLogo = new JPanel();
        painel.add(jplLogo);
        jplLogo.setBounds(d.width / (100) * 77, d.height / (100) * 7, d.width / (100) * 9, d.height / (100) * 7);
        jplLogo.setBackground(new Color(39, 41, 38));
        jplLogo.setLayout(null);
        jplLogo.setVisible(true);

        paciente = new JPanel();
        painel.add(paciente);
        paciente.setBounds(d.width / (100) * 2, d.height / (100) * 6, d.width / (100) * 75, d.height / (100) * 24);
        paciente.setBackground(new Color(227, 232, 245)); //0,49,
        paciente.setBorder(BorderFactory.createTitledBorder(null, "INFORMAÇÃO DE PESQUISA", TitledBorder.LEFT, TitledBorder.TOP, new Font("Arial", Font.BOLD, 12), new Color(0, 0, 0)));
        paciente.setLayout(null);
        paciente.setVisible(true);

        info_paciente = new JPanel();
        painel.add(info_paciente);
        info_paciente.setBounds(d.width / (100) * 78, d.height / (100) * 40, d.width / (100) * 25, d.height / (100) * 24);
        info_paciente.setBackground(new Color(227, 232, 245));
        info_paciente.setBorder(BorderFactory.createTitledBorder(null, "LOCALIZAÇÃO", TitledBorder.CENTER, TitledBorder.TOP, new Font("Arial", Font.BOLD, 12), new Color(0, 0, 0)));
        info_paciente.setLayout(null);
        info_paciente.setVisible(true);

        jlVersao = new JLabel(iconLogo);
        jplLogo.add(jlVersao);
        jlVersao.setBounds(d.width / (100) * 1, 1, 96, 56);
        jlVersao.setVisible(true);

        rodape = new JPanel();
        painel.add(rodape);
        rodape.setBounds(0, d.height / (100) * 98, d.width, d.height / (100) * 3);
        rodape.setBackground(new Color(227, 232, 245));//141, 186, 47
        rodape.setLayout(null);
        rodape.setVisible(true);

        jlbCopy = new JLabel("© 2018, design e deselvolvimento por Roberto Carvalho da Agion Tecnologia.");
        rodape.add(jlbCopy);
        jlbCopy.setBounds(d.width / (100) * 40, -2, d.width / (100) * 36, 22);
        jlbCopy.setFont(new Font("Arial", Font.BOLD, 11));
        jlbCopy.setForeground(new Color(0, 0, 0));
        jlbCopy.setVisible(true);

        jlbCodigo = new JLabel("CÓDIGO:");
        paciente.add(jlbCodigo);
        jlbCodigo.setBounds(d.width / (100) * 2, d.height / (100) * 4, 100, 22);
        jlbCodigo.setFont(new Font("Arial", Font.BOLD, 14));
        jlbCodigo.setForeground(new Color(0, 0, 0));
        jlbCodigo.setVisible(true);

        textCodigo = new JTextField();
        paciente.add(textCodigo);
        textCodigo.setBounds(d.width / (100) * 2, d.height / (100) * 7, 100, 22);
        textCodigo.setFont(new Font("Arial", Font.PLAIN, 16));
        textCodigo.setForeground(new Color(0, 49, 77));
        textCodigo.setVisible(true);

        jlbData = new JLabel("DATA:");
        paciente.add(jlbData);
        jlbData.setBounds(d.width / (100) * 20, d.height / (100) * 4, 100, 22);
        jlbData.setFont(new Font("Arial", Font.BOLD, 14));
        jlbData.setForeground(new Color(0, 0, 0));
        jlbData.setVisible(false);

        jftData1 = new JFormattedTextField();
        paciente.add(jftData1);
        MaskFormatter formater = new MaskFormatter();
        jftData1.setColumns(10);
        try {
            formater.setMask("##/##/####");
            formater.install(jftData1);
        } catch (ParseException ex) {
            JOptionPane.showMessageDialog(null, ex);
        }

        jftData1.setBounds(d.width / (100) * 20, d.height / (100) * 7, 80, 22);
        jftData1.setFont(new Font("Arial", Font.PLAIN, 14));
        jftData1.setVisible(false);

        jlbNome = new JLabel("NOME:");
        paciente.add(jlbNome);
        jlbNome.setBounds(d.width / (100) * 2, d.height / (100) * 12, 100, 22);
        jlbNome.setFont(new Font("Arial", Font.BOLD, 14));
        jlbNome.setForeground(new Color(0, 0, 0));
        jlbNome.setVisible(true);

        textNome = new JTextField();
        paciente.add(textNome);
        textNome.setBounds(d.width / (100) * 2, d.height / (100) * 15, d.width / (100) * 24, 22);
        textNome.setFont(new Font("Arial", Font.PLAIN, 16));
        textNome.setForeground(new Color(0, 49, 77));
        textNome.setDocument(new Documento());
        textNome.setVisible(true);

        btnAcessar = new JButton(iconBtnPesquisa);
        paciente.add(btnAcessar);
        btnAcessar.setBounds(d.width / (100) * 27, d.height / (100) * 15, 25, 22);
        btnAcessar.setFont(new Font("Arial", Font.PLAIN, 12));
        btnAcessar.setToolTipText("Pesquisa.");
        btnAcessar.setBorder(javax.swing.BorderFactory.createLineBorder(new Color(0, 0, 102), 1));
        btnAcessar.setVisible(true);
        btnAcessar.setMnemonic(KeyEvent.VK_ENTER);
        btnAcessar.getRootPane().setDefaultButton(btnAcessar);
        btnAcessar.addActionListener((ActionEvent e) -> {
            limparTabela();
            PreencherTabela();
        });
        btnAcessar.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btnAcessar.setBackground(new Color(192, 192, 192));
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btnAcessar.setBackground(UIManager.getColor("control"));
            }
        });

        jlImpressora = new JLabel("IMPRESSORA:");
        paciente.add(jlImpressora);
        jlImpressora.setBounds(d.width / (100) * 30, d.height / (100) * 4, 100, 22);
        jlImpressora.setFont(new Font("Arial", Font.BOLD, 14));
        jlImpressora.setForeground(new Color(0, 0, 0));
        jlImpressora.setVisible(true);

        comboImpressoras = new JComboBox();
        paciente.add(comboImpressoras);
        comboImpressoras.setBounds(d.width / (100) * 30, d.height / (100) * 7, 240, 22);
        comboImpressoras.setForeground(new Color(0, 49, 77));
        comboImpressoras.addItem("");
        getPrinters();
        comboImpressoras.setVisible(true);
        comboImpressoras.setEnabled(true);

        jlbComboUnidades = new JLabel("UNIDADES:");
        paciente.add(jlbComboUnidades);
        jlbComboUnidades.setBounds(d.width / (100) * 15, d.height / (100) * 4, 100, 22);
        jlbComboUnidades.setFont(new Font("Arial", Font.BOLD, 14));
        jlbComboUnidades.setForeground(new Color(0, 0, 0));
        jlbComboUnidades.setVisible(true);

        comboUnidades = new JComboBox();
        paciente.add(comboUnidades);
        comboUnidades.setBounds(d.width / (100) * 15, d.height / (100) * 7, 170, 22);
        comboUnidades.setForeground(new Color(0, 49, 77));
        comboUnidades.addItem("MATRIZ");
        comboUnidades.addItem("HOSPITAL JD CUIABA");
        comboUnidades.addItem("COM. COSTA");
        comboUnidades.addItem("GASTRO MT");
        comboUnidades.setVisible(true);
        comboUnidades.setSelectedIndex(2);
        comboUnidades.setEnabled(true);

        btnImprimir = new JButton("Imprimir", iconPrint);
        painel.add(btnImprimir);
        btnImprimir.setBounds(d.width / (100) * 78, d.height / (100) * 67, 100, 40);
        btnImprimir.setFont(new Font("Arial", Font.PLAIN, 14));
        btnImprimir.setToolTipText("Realizar impressão do exame selecionado.");
        btnImprimir.setBorder(javax.swing.BorderFactory.createLineBorder(new Color(0, 0, 0), 1));
        btnImprimir.setRequestFocusEnabled(true);
        btnImprimir.setRolloverEnabled(true);
        btnImprimir.setVisible(true);
        btnImprimir.addActionListener((ActionEvent e) -> {
            if (comboImpressoras.getSelectedIndex() == 0) {
                JOptionPane.showMessageDialog(null, "Nenhuma Impressora foi Selecionada.", "Aviso", JOptionPane.WARNING_MESSAGE, new ImageIcon("src/pacssid/icons/error.png"));
            } else {
                linha = tabela.getSelectedRow();
                if (linha < 0) {
                    JOptionPane.showMessageDialog(null, "Nenhum Exame foi Selecionado.", "Aviso", JOptionPane.WARNING_MESSAGE, new ImageIcon("src/pacssid/icons/error.png"));
                } else {
                    nome = String.valueOf(tabela.getValueAt(linha, 1));
                    codigo = String.valueOf(tabela.getValueAt(linha, 0));
                    dataexame = String.valueOf(tabela.getValueAt(linha, 7));
                    datanasc = String.valueOf(tabela.getValueAt(linha, 2));
                    quantidade = String.valueOf(tabela.getValueAt(linha, 8));
                    acesso = String.valueOf(tabela.getValueAt(linha, 6));
                    procedimento = String.valueOf(tabela.getValueAt(linha, 5));
                    uniSelect = comboUnidades.getSelectedIndex();
                    nomedaImpressora = String.valueOf(comboImpressoras.getSelectedItem());
                    Impressao impressao = new Impressao(nome, acesso, codigo, dataexame, datanasc, quantidade, uniSelect, procedimento, nomedaImpressora);
                    if (acesso.equals("null")) {
                        impressao.VerCaminhoCodigo(codigo);
                        try {
                            impressao.Imprimir();                          
                        } catch (PrinterException ex) {
                            Logger.getLogger(Index.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    } else {
                        impressao.VerCaminhoaAcesso(acesso);
                        try {
                            impressao.Imprimir();
                        } catch (PrinterException ex) {
                            Logger.getLogger(Index.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }
            }
        });
        btnImprimir.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btnImprimir.setBackground(new Color(192, 192, 192));
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btnImprimir.setBackground(UIManager.getColor("control"));
            }
        });

        jlbUnidade = new JLabel("UNIDADE:");
        info_paciente.add(jlbUnidade);
        jlbUnidade.setBounds(d.width / (100) * 1, d.height / (100) * 3, 100, 22);
        jlbUnidade.setForeground(new Color(0, 0, 0));
        jlbUnidade.setFont(new Font("Arial", Font.PLAIN, 12));
        jlbUnidade.setVisible(true);

        textUnidade = new JTextField();
        info_paciente.add(textUnidade);
        textUnidade.setBounds(d.width / (100) * 2, d.height / (100) * 6, 280, 22);
        textUnidade.setFont(new Font("Arial", Font.BOLD, 16));
        textUnidade.setBorder(null);
        textUnidade.setForeground(new Color(0, 0, 0));
        textUnidade.setBackground(null);
        textUnidade.setEditable(false);
        textUnidade.setVisible(true);

        jlbEstacao = new JLabel("ESTAÇÃO:");
        info_paciente.add(jlbEstacao);
        jlbEstacao.setBounds(d.width / (100) * 1, d.height / (100) * 10, 150, 22);
        jlbEstacao.setForeground(new Color(0, 0, 0));
        jlbEstacao.setFont(new Font("Arial", Font.PLAIN, 12));
        jlbEstacao.setVisible(true);

        textEstacao = new JTextField();
        info_paciente.add(textEstacao);
        textEstacao.setBounds(d.width / (100) * 2, d.height / (100) * 13, 150, 22);
        textEstacao.setFont(new Font("Arial", Font.BOLD, 16));
        textEstacao.setBorder(null);
        textEstacao.setForeground(new Color(0, 0, 0));
        textEstacao.setEditable(false);
        textEstacao.setBackground(null);
        textEstacao.setVisible(true);

        jlAcesso = new JLabel("ACESSO:");
        info_paciente.add(jlAcesso);
        jlAcesso.setBounds(d.width / (100) * 1, d.height / (100) * 17, 150, 22);
        jlAcesso.setForeground(new Color(0, 0, 0));
        jlAcesso.setFont(new Font("Arial", Font.PLAIN, 12));
        jlAcesso.setVisible(true);

        textAcesso = new JTextField();
        info_paciente.add(textAcesso);
        textAcesso.setBounds(d.width / (100) * 2, d.height / (100) * 20, 280, 22);
        textAcesso.setFont(new Font("Arial", Font.BOLD, 16));
        textAcesso.setBorder(null);
        textAcesso.setForeground(new Color(0, 0, 0));
        textAcesso.setBackground(null);
        textAcesso.setEditable(false);
        textAcesso.setVisible(true);

        tabela = new JTable();
        scroll = new JScrollPane();
        cabecalho = tabela.getTableHeader();
        cabecalho.setForeground(Color.white);
        cabecalho.setBackground(new Color(39, 41, 38));//12, 97, 114
        tabela.getTableHeader().setPreferredSize(new Dimension(tabela.getTableHeader().getWidth(), 27));
        cabecalho.setFont(new Font("Arial", Font.BOLD, 12));
        scroll.setBounds(d.width / (100) * 2, d.height / (100) * 31, d.width / (100) * 75, d.height / (100) * 58);
        scroll.setVisible(true);
        tabela.setEnabled(false);
        painel.add(scroll);
        tabela.setModel(new DefaultTableModel(
                new Object[][]{},
                new String[]{
                    "ID", "NOME", "DATA NASC", "SEXO", "MOD", "PROCEDIMENTO", "ACESSO", "DATA", "QTD"
                }
        ) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        });

        scroll.setViewportView(tabela);
        tabela.setSelectionForeground(Color.black);
        tabela.setForeground(new Color(0, 41, 89));
        tabela.setBackground(Color.white);
        tabela.setSelectionBackground(new Color(205, 197, 191)); // 12, 97, 114
        tabela.setSelectionForeground(new Color(0, 49, 77));
        tabela.setFont(new Font("Times", Font.PLAIN, 11));
        tabela.setEnabled(true);
        tabela.setRowHeight(25);

        tabela.getColumnModel().getColumn(0).setPreferredWidth(d.width / (100) * 3);  // CODIGO
        tabela.getColumnModel().getColumn(1).setPreferredWidth(d.width / (100) * 20); // NOME     
        tabela.getColumnModel().getColumn(2).setPreferredWidth(d.width / (100) * 6);  // DATA NASC
        tabela.getColumnModel().getColumn(3).setPreferredWidth(d.width / (100) * 3);  //SEXO
        tabela.getColumnModel().getColumn(4).setPreferredWidth(d.width / (100) * 3); // MOD   
        tabela.getColumnModel().getColumn(5).setPreferredWidth(d.width / (100) * 18);  // PROCEDIMENTO 
        tabela.getColumnModel().getColumn(6).setPreferredWidth(d.width / (100) * 5);  // ACESSO 
        tabela.getColumnModel().getColumn(7).setPreferredWidth(d.width / (100) * 5);  // DATA
        tabela.getColumnModel().getColumn(8).setPreferredWidth(d.width / (100) * 3);  // QTD 

        tabela.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                String codigodeAcesso, codigo;
                if (e.getClickCount() == 1) {
                    int linha;
                    linha = tabela.getSelectedRow();
                    if (linha == -1) {

                    } else {
                        linha = tabela.getSelectedRow();
                        codigodeAcesso = (String) tabela.getValueAt(linha, 6);
                        if (codigodeAcesso == null) {
                            codigo = (String) tabela.getValueAt(linha, 0);
                            VerInformacoesCodigo(codigo);
                        } else {
                            VerInformacoes(codigodeAcesso);
                        }

                    }
                }
            }

            public void VerInformacoesCodigo(String codigo) {
                ConectaBanco banco = new ConectaBanco();
                banco.conexao();
                String sql = "SELECT S.INSTITUTION AS ESTACAO,S.STATION_NAME AS UNIDADE,ST.ACCESSION_NO AS ACESSSO FROM SERIES S,STUDY ST, "
                        + "PATIENT P WHERE ST.PATIENT_FK = P.PK AND S.STUDY_FK = ST.PK AND P.PAT_ID ='" + codigo + "'";
                try {
                    Statement stm = banco.conn.createStatement();
                    ResultSet resultado = stm.executeQuery(sql);
                    if (resultado.next()) {
                        indexUnidades = comboUnidades.getSelectedIndex();
                        switch (indexUnidades) {
                            case 0:
                                textUnidade.setText("IMAGENS MATRIZ");
                                break;
                            case 1:
                                textUnidade.setText("HOSPITAL JADRIM CUIABÁ");
                                break;
                            case 2:
                                textUnidade.setText("COMANDANTE COSTA");
                                break;
                            case 3:
                                textUnidade.setText("GASTRO MT");
                                break;
                            default:
                        }
                        textEstacao.setText(resultado.getString("UNIDADE"));
                        textAcesso.setText(resultado.getString("ACESSSO"));
                    }
                    banco.desconecta();
                } catch (SQLException ex) {
                    Logger.getLogger(Index.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

            public void VerInformacoes(String codigodeAcesso) {
                ConectaBanco banco = new ConectaBanco();
                banco.conexao();
                String sql = "SELECT S.INSTITUTION AS ESTACAO,S.STATION_NAME AS UNIDADE,ST.ACCESSION_NO AS ACESSSO FROM SERIES S,STUDY ST, "
                        + "PATIENT P WHERE ST.PATIENT_FK = P.PK AND S.STUDY_FK = ST.PK AND ST.ACCESSION_NO ='" + codigodeAcesso + "'";
                try {
                    Statement stm = banco.conn.createStatement();
                    ResultSet resultado = stm.executeQuery(sql);
                    if (resultado.next()) {
                        indexUnidades = comboUnidades.getSelectedIndex();
                        switch (indexUnidades) {
                            case 0:
                                textUnidade.setText("IMAGENS MATRIZ");
                                break;
                            case 1:
                                textUnidade.setText("HOSPITAL JARDIM CUIABÁ");
                                break;
                            case 2:
                                textUnidade.setText("COMANDANTE COSTA");
                                break;
                            case 3:
                                textUnidade.setText("GASTRO MT");
                                break;
                            default:
                        }
                        textEstacao.setText(resultado.getString("UNIDADE"));
                        textAcesso.setText(resultado.getString("ACESSSO"));
                    }
                    banco.desconecta();
                } catch (SQLException ex) {
                    Logger.getLogger(Index.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });

        TrocarFundo();
//        exec();
    }

    public void exec() {
        try {
            Process process = Runtime.getRuntime().exec("git pull https://github.com/robertocf/PACSSID.git");
            Scanner leitor = new Scanner(process.getInputStream());
            while (leitor.hasNextLine()) {
                System.out.println(leitor.nextLine());
            }
        } catch (IOException e) {

        }
    }

    public void TrocarFundo() {
        textCodigo.requestFocus();
        textCodigo.setBackground(new Color(240, 230, 140));
        textCodigo.setForeground(new Color(47, 79, 79));
        jftData1.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent fe) {
                jftData1.setBackground(new Color(240, 230, 140));
                jftData1.setForeground(new Color(47, 79, 79));

            }

            @Override
            public void focusLost(FocusEvent fe) {
                jftData1.setBackground(Color.WHITE);
            }
        });
        textNome.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent fe) {
                textNome.setBackground(new Color(240, 230, 140));
                textNome.setForeground(new Color(47, 79, 79));
            }

            @Override
            public void focusLost(FocusEvent fe) {
                textNome.setBackground(Color.WHITE);
            }
        });
        textCodigo.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent fe) {
                textCodigo.setBackground(new Color(240, 230, 140));
                textCodigo.setForeground(new Color(47, 79, 79));
            }

            @Override
            public void focusLost(FocusEvent fe) {
                textCodigo.setBackground(Color.WHITE);
            }
        });
    }

    class Documento extends PlainDocument {

        @Override
        public void insertString(int offs, String str, AttributeSet a) throws BadLocationException {
            int tamanho = (this.getLength() + str.length());
            super.insertString(offs, str.toUpperCase(), a);
        }
    }

    private void limparTabela() {
        while (tabela.getRowCount() > 0) {
            DefaultTableModel dm = (DefaultTableModel) tabela.getModel();
            dm.getDataVector().removeAllElements();
        }
    }

}
