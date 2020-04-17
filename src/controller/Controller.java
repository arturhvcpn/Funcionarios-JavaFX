package controller;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import model.ConnectionFactory;
import model.Funcionario;
import model.FuncionarioDAO;

import java.math.BigDecimal;
import java.net.URL;
import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class Controller implements Initializable {

    @FXML
    private DatePicker nascimentoNovoFuncionario;

    @FXML
    private TextField salarioFuncionario;

    @FXML
    private DatePicker nascimentoFuncionario;

    @FXML
    private TextField cargoNovoFuncionario;

    @FXML
    private TextField cargoFuncionario;

    @FXML
    private Tab cadastrar;

    @FXML
    private TextField salarioNovoFuncionario;

    @FXML
    private Tab atualizar;

    @FXML
    private TextField nomeNovoFuncionario;

    @FXML
    private TextField nomeFuncionario;

    @FXML
    private Tab consultar;

    @FXML
    private TableView<Funcionario> funcionarios;

    @FXML
    private TabPane abas;

    @FXML
    private TextField nomeConsultaFuncionario;

    @FXML
    private TableColumn<Funcionario,Integer> idFuncionarios;

    @FXML
    private TableColumn<Funcionario,String>nomeFuncionarios;

    @FXML
    private TableColumn<Funcionario, Date> nascimentoFuncionarios;

    @FXML
    private TableColumn<Funcionario,String>cargoFuncionarios;

    @FXML
    private TableColumn<Funcionario, BigDecimal>salarioFuncionarios;

    private FuncionarioDAO dao;

    private Funcionario funcionarioSelecionado;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        dao = new FuncionarioDAO();

        idFuncionarios.setCellValueFactory(new PropertyValueFactory<>("id"));
        nomeFuncionarios.setCellValueFactory(new PropertyValueFactory<>("nome"));
        nascimentoFuncionarios.setCellValueFactory(new PropertyValueFactory<>("dataNascimento"));
        cargoFuncionarios.setCellValueFactory(new PropertyValueFactory<>("cargo"));
        salarioFuncionarios.setCellValueFactory(new PropertyValueFactory<>("salario"));

    }

    @FXML
    public void gerenciarAbas() {

        if(cadastrar.isSelected() || consultar.isSelected()){
            atualizar.setDisable(true);
            limparCadastroAtualizacaoFuncionario();
        }
    }

    @FXML
    public void limparCadastroNovoFuncionario() {
        nomeNovoFuncionario.clear();
        nascimentoNovoFuncionario.setValue(null);
        cargoNovoFuncionario.clear();
        salarioNovoFuncionario.clear();
    }

    private void limparCadastroAtualizacaoFuncionario(){
        nomeFuncionario.clear();
        nascimentoFuncionario.setValue(null);
        cargoFuncionario.clear();
        salarioFuncionario.clear();
    }

    @FXML
    public void salvarNovoFuncionario() {

        Funcionario funcionario = new Funcionario();

        funcionario.setNome(nomeNovoFuncionario.getText());
        funcionario.setDataNascimento(Date.valueOf(nascimentoNovoFuncionario.getValue()));
        funcionario.setCargo(cargoNovoFuncionario.getText());
        funcionario.setSalario(new BigDecimal(salarioNovoFuncionario.getText()));

        try {

            dao.cadastrar(funcionario);
            exibirDialogoInformacao("Usuário cadastrado com sucesso");
            limparCadastroNovoFuncionario();

        }catch (Exception e){
            exibirDialogoErro("Falha ao cadastrar o funcionário");
            e.printStackTrace();
        }

    }

    @FXML
    public void consultarFuncionarios() {

        try{

            List<Funcionario> resultado = dao.consultar(nomeConsultaFuncionario.getText());

            if(resultado.isEmpty()){
                exibirDialogoInformacao("Nenhum funcionário encontrado!");
            }
            else {

                funcionarios.setItems(FXCollections.observableList(resultado));

            }

        }catch (Exception e){
         exibirDialogoErro("Falha ao realizar a consulta");
         e.printStackTrace();
        }
    }

    @FXML
    public void exibirAbaAtualizacao() {
        funcionarioSelecionado = funcionarios.getSelectionModel().getSelectedItem();

        if(funcionarioSelecionado == null){
            exibirDialogoErro("Não há funcionário selecionado");
        } else{

            atualizar.setDisable(false);

            abas.getSelectionModel().select(atualizar);

            nomeFuncionario.setText(funcionarioSelecionado.getNome());
            nascimentoFuncionario.setValue(funcionarioSelecionado.getDataNascimento().toLocalDate());
            cargoFuncionario.setText(funcionarioSelecionado.getCargo());
            salarioFuncionario.setText(funcionarioSelecionado.getSalario().toString());


        }

    }

    public void deletarFuncionario() {

        Funcionario funcionario = funcionarios.getSelectionModel().getSelectedItem();

        if(funcionario == null){
            exibirDialogoErro("Não há funcionário selecionado");
        } else {
            if (exibirDialogoConfirmacao("Confirma a exclusão do funcionário selecionado?")){

                try{

                    dao.deletar(funcionario.getId());
                    exibirDialogoInformacao("Funcionário deletado com sucesso!");
                    consultarFuncionarios();

                }catch(Exception e){

                    exibirDialogoErro("Falha ao deletar o funcionário");
                    e.printStackTrace();

                }


            }

        }
    }

    public void salvarFuncionario() {

        funcionarioSelecionado.setNome(nomeFuncionario.getText());
        funcionarioSelecionado.setDataNascimento(Date.valueOf(nascimentoFuncionario.getValue()));
        funcionarioSelecionado.setCargo(cargoFuncionario.getText());
        funcionarioSelecionado.setSalario(new BigDecimal(salarioFuncionario.getText()));

        try{
            dao.atualizar(funcionarioSelecionado);
            exibirDialogoInformacao("Funcionário atualizado com sucesso!");
            abas.getSelectionModel().select(consultar);
            consultarFuncionarios();
            //limparCadastroAtualizacaoFuncionario();
        }catch (Exception e){
            exibirDialogoErro("Falha ao atualizar o funcionário");
            e.printStackTrace();
        }


    }

    private void exibirDialogoInformacao(String informacao){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Informação");
        alert.setHeaderText(null);
        alert.setContentText(informacao);

        alert.showAndWait();
    }

    private void exibirDialogoErro(String erro){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erro");
        alert.setHeaderText(null);
        alert.setContentText(erro);

        alert.showAndWait();
    }

    private boolean exibirDialogoConfirmacao(String confirmacao){
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmação");
        alert.setHeaderText(null);
        alert.setContentText(confirmacao);

        Optional<ButtonType> opcao = alert.showAndWait();

        if (opcao.get() == ButtonType.OK){
            return true;
        }
        return false;
    }
}
