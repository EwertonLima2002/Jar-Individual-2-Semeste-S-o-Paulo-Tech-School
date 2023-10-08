package dados;

import com.github.britooo.looca.api.core.Looca;
import com.github.britooo.looca.api.group.processador.Processador;
import dados.Cliente;
import dados.Conexao;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;
import java.util.Scanner;

public class Dados {
    public static void main(String[] args) {
        Conexao conexao = new Conexao();
        Looca looca = new Looca();
        JdbcTemplate con = conexao.getConexaoDoBanco();
        Scanner leitor = new Scanner(System.in);
        Cliente cliente = new Cliente();
        String email = "";
        String senha = "";
        Integer opcao = 0;

        System.out.println("Bem vindo ao sistema de monitoramento de servidores educacionais alpaca solutions" +
                "\nVocê tem cadastro se sim digite 1: \n" +
                "Se não digite 2 e faça o cadastro");
        opcao = leitor.nextInt();

        leitor.nextLine();
        if (opcao == 1) {
            System.out.println("Digite seu email:");
            email = leitor.nextLine();
            cliente.setEmail(email);

            System.out.println("Digite sua senha agora:");
            senha = leitor.nextLine();
            cliente.setSenha(senha);

            String sql = "SELECT * FROM cadastro WHERE email = ? AND senha = ?";

            try {
                List<Cliente> listaCliente = con.query(sql, new Object[]{cliente.getEmail(), cliente.getSenha()}, new BeanPropertyRowMapper<>(Cliente.class));

                if (listaCliente.isEmpty()) {
                    System.out.println("Não existe cadastro em nossa base de dados, faça o cadastro e tente novamente");
                } else {

                    do {
                        System.out.println("+--------------------------+" +
                                "Verifiquei que você tem cadastro, o que você quer visualizar" +
                                "\n1-)CPU\n" +
                                "2-)Janelas Abertas\n" +
                                "3-)Quantidade de ram\n" +
                                "4-)Tamanho total do Disco\n" +
                                "5-)Memória Disponível\n" +
                                "6-)Porcentagem de Uso do Disco\n" +
                                "7-)Porcentagem de Uso da memória\n" +
                                "8-)Sair" +
                                "+--------------------------+");
                        opcao = leitor.nextInt();

                        switch (opcao) {
                            case 1: {
                                Processador processador = new Processador();
                                System.out.println(processador);
                                break;
                            }
                        }
                    } while (!opcao.equals(8)); // Atualize a variável opcao dentro do loop
                }
            } catch (Exception erro) {
                erro.printStackTrace();
            }

        } else {
            System.out.println("Vamos ao cadastro");

            System.out.println("Me diz qual é o seu nome:");
            String nome = leitor.nextLine();

            System.out.println("Agora me diz o seu email");
            email = leitor.nextLine();

            do {
                System.out.println("E pra finalizar vamos criar uma senha lembrando que é 8 dígitos, ok?");
                senha = leitor.nextLine();
                if (senha.length() < 8) {
                    System.out.println("Senha muito curta, tente novamente");
                }
            } while (senha.length() < 8);

            System.out.println("Fazendo o cadastro ...");
            try {
                con.update("insert into cadastro (nome , email , senha) values (? , ? ,?)", nome, email, senha);

                System.out.println("Cadastro realizado com sucesso");

                do {
                    System.out.println("+--------------------------+" +
                            "Agora que você tem cadastro, o que você quer visualizar" +
                            "\n1-)CPU\n" +
                            "2-)Janelas Abertas\n" +
                            "3-)Quantidade de ram\n" +
                            "4-)Tamanho total do Disco\n" +
                            "5-)Memória Disponível\n" +
                            "6-)Porcentagem de Uso do Disco\n" +
                            "7-)Porcentagem de Uso da memória\n" +
                            "8-)Sair" +
                            "+--------------------------+");
                    opcao = leitor.nextInt();
                } while (!opcao.equals(8)); // Atualize a variável opcao dentro do loop
            } catch (Exception erro) {
                erro.printStackTrace();
            }
        }
    }
}
