package model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class FuncionarioDAO {

    private Connection connection;

    public FuncionarioDAO(){

      connection = new ConnectionFactory().getConnection();
    }

    public void cadastrar(Funcionario funcionario){
        String sql = "insert into funcionario (nome,data_nascimento,cargo,salario) values (?,?,?,?)";
        try {
            PreparedStatement statement = connection.prepareStatement(sql);

            statement.setString(1,funcionario.getNome());
            statement.setDate(2,funcionario.getDataNascimento());
            statement.setString(3,funcionario.getCargo());
            statement.setBigDecimal(4,funcionario.getSalario());

            statement.execute();
            statement.close();

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public void atualizar(Funcionario funcionario){

        String sql = "update funcionario set nome=?, data_nascimento=?, cargo=?, salario=? where id=?";
        try {
            PreparedStatement statement = connection.prepareStatement(sql);

            statement.setString(1,funcionario.getNome());
            statement.setDate(2,funcionario.getDataNascimento());
            statement.setString(3,funcionario.getCargo());
            statement.setBigDecimal(4,funcionario.getSalario());
            statement.setInt(5, funcionario.getId());

            statement.execute();
            statement.close();

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

    }
    //VER SE RECEBE idFuncionario
    public void deletar(Integer idFuncionario){

        String sql = "delete from funcionario where id = ?";
        try {
            PreparedStatement statement = connection.prepareStatement(sql);

            statement.setInt(1,idFuncionario);

            statement.execute();
            statement.close();

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public List<Funcionario> consultar(String nomeFuncionario){

        List<Funcionario> funcionarios = new ArrayList<>();

        String sql = "select * from funcionario where nome like '%" + nomeFuncionario + "%'";
        try {
            PreparedStatement statement = connection.prepareStatement(sql);

            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {

                Funcionario funcionario = new Funcionario();

                funcionario.setId(resultSet.getInt("id"));
                funcionario.setNome(resultSet.getString("nome"));
                funcionario.setDataNascimento(resultSet.getDate("data_nascimento"));
                funcionario.setCargo(resultSet.getString("cargo"));
                funcionario.setSalario(resultSet.getBigDecimal("salario"));

                funcionarios.add(funcionario);
            }

            resultSet.close();
            statement.close();

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        return  funcionarios;
    }

}
