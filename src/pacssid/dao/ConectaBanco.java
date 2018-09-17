/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pacssid.dao;

/**
 *
 * @author roberto
 */
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.swing.JOptionPane;

/**
 *
 * @author Roberto Carvalho
 */
public class ConectaBanco {
    public String servidor = "ODIN";// Endereço do banco de dados
    public String banco = "pacsdb";// Nome do banco de dados criado
    public String porta = "5432";// Porta do Banco
    public Statement stm;//responsável por preparar e ralizar pesquisas no banco de dados
    public ResultSet rs;//responsável por armazenar o resultado de uma pesquisa passada para o statement
    private final String driver = "org.postgresql.Driver";//responsável por identificar o servço de banco de dados   
    private final String usuario= "postgres";// utilizados no banco
    private final String senha= "roberto";
    public Connection conn;// responsável por realizar a conexão com o banco de dados
    private final String caminho = "jdbc:postgresql://"+servidor+":"+porta+"/"+banco+"";//responsável por setar o local do banco de dados

    public void conexao(){//este metodo será responsavel por realizar a conexão com o banco bados
        try {
              System.setProperty("jdbc.Drivers", driver);
             conn = DriverManager.getConnection(caminho, usuario, senha);
             conn.setAutoCommit(true);
        } catch (SQLException ex) {
           JOptionPane.showMessageDialog(null, "\n Sem Conexão: " +ex.getMessage(),"ERROR",JOptionPane.ERROR_MESSAGE);
        }
    }

        public void desconecta(){// Método para fechar a conexão com o banco de dados
        try {
            conn.close();// fecha a conexão
        } catch (SQLException ex) {
             JOptionPane.showMessageDialog(null, "Erro ao Fechar conexão !\n "+ ex.getMessage());
        }
    }

}
