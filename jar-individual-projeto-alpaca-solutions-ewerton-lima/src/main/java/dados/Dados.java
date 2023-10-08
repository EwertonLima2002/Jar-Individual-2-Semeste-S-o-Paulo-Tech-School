package dados;

import com.github.britooo.looca.api.core.Looca;
import com.github.britooo.looca.api.group.discos.Disco;
import com.github.britooo.looca.api.group.memoria.Memoria;
import com.github.britooo.looca.api.group.processador.Processador;
import com.github.britooo.looca.api.group.rede.Rede;
import dados.Cliente;
import dados.Conexao;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;

import java.math.BigDecimal;
import java.math.RoundingMode;
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
        double tamanhoTotalGiB = 0;
        Double tot_disco = 0.0;
        Integer total_disco = 0;
        Double tamanho_disco = 0.0;

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
                                "\n1-)Porcentagem de Uso do Disco\n" +
                                "2-)Porcentagem de uso da memória\n" +
                                "3-)Quantidade de ram\n" +
                                "4-)Tamanho total do disco:\n" +
                                "5-)Memória Disponível\n" +
                                "6-)Porcentagem de Uso da CPU:\n" +
                                "7-)Porcentagem de uso do disco: %s\n" +
                                "8-)Verificar os ultimos registos e ter uma metrica:\n" +
                                "9-)Sair" +
                                "+--------------------------+");
                        opcao = leitor.nextInt();

                        switch (opcao) {
                            case 1: {
                                Processador processador = new Processador();
                                System.out.println(processador);
                                Double total_pro = processador.getUso();
                                BigDecimal porcentagem_uso_disco = BigDecimal.valueOf(total_pro).setScale(2, RoundingMode.HALF_UP);
                                con.update("insert into servidor (porcentagem_uso_disco) values (?)", porcentagem_uso_disco);
                                break;
                            }
                            case 2: {
                                Memoria memoria = new Memoria();
                                Double percentual_de_uso_mem = (double) memoria.getEmUso() / memoria.getTotal() * 100;
                                BigDecimal porcentagem_uso_memoria = BigDecimal.valueOf(percentual_de_uso_mem).setScale(2 , RoundingMode.HALF_UP);
                                System.out.println(memoria);
                                System.out.println(porcentagem_uso_memoria);
                                con.update("insert into servidor (porcentagem_uso_memoria) values (?)", porcentagem_uso_memoria);
                                break;
                            }
                            case 3:{
                                Memoria memoria = new Memoria();
                                Double ramDisponivel = (double) memoria.getDisponivel() / (1024 * 1024 * 1024);
                                System.out.println(
                                        String.format("""
                                                Memória Ram disponível: %.2f
                                                
                                                """, ramDisponivel));
                                con.update("insert into servidor (quantidade_de_ram) values (?)", ramDisponivel);
                                break;
                            }
                            case 4:{

                                for (Disco disco : looca.getGrupoDeDiscos().getDiscos()) {
                                    tamanhoTotalGiB = (double) disco.getTamanho() / (1024 * 1024 * 1024);
                                    tot_disco = Math.round(tamanhoTotalGiB * 100.0) / 100.0;
                                    tamanho_disco = Double.valueOf(disco.getTamanho());
                                    total_disco = (int) Math.round(tamanhoTotalGiB);
                                }
                                System.out.println(
                                        String.format("""
                                                A quantidade total de disco é de %d
                                                """, total_disco)
                                );

                                con.update("insert into servidor (tamanho_total_disco) values (?)" , tot_disco);
                            break;
                            }
                            case 5:{
                                Memoria memoria = new Memoria();
                                Double memoria_disponivel = memoria.getDisponivel() / (1024 * 1024 * 1024.0);
                                memoria_disponivel = Math.round(memoria_disponivel * 100.0) / 100.0;
                                System.out.println(
                                        String.format("Memória atualmente disponível: %.2f" , memoria_disponivel)
                                );
                                con.update("insert into servidor (memoria_disponivel) values (?)", memoria_disponivel);
                                break;
                            }
                            case 6:{
                                Processador processador = new Processador();
                                Double porcentagem_uso_cpu = processador.getUso() / processador.getNumeroCpusFisicas();
                                BigDecimal porcentagem_de_uso_da_cpu = BigDecimal.valueOf(porcentagem_uso_cpu).setScale(2 , RoundingMode.HALF_UP);
                                System.out.println(
                                        String.format("Porcentagem de uso da cpu %.2f", porcentagem_de_uso_da_cpu));

                                con.update("insert into servidor (porcentagem_uso_cpu) values (?)", porcentagem_uso_cpu);
                             break;
                            }
                            case 7:{

                                for(Disco disco : looca.getGrupoDeDiscos().getDiscos()){
                                    tamanhoTotalGiB = (double) disco.getTamanho() / (1024 * 1024 * 1024);
                                    tot_disco = Math.round(tamanhoTotalGiB * 100.0) / 100.0;
                                    tamanho_disco = Double.valueOf(disco.getTamanho());
                                    total_disco = (int) Math.round(tamanhoTotalGiB);


                                }
                                BigDecimal tamanho_disponivel_do_disco = new BigDecimal(tamanho_disco)
                                        .setScale(2, RoundingMode.HALF_UP)
                                        .divide(new BigDecimal(1024 * 1024 * 1024), 2, RoundingMode.HALF_UP);

                                System.out.println(
                                        String.format("Tamanho total do disco : %.2f", tamanho_disponivel_do_disco));

                                con.update("insert into servidor (tamanho_total_disco) values (?)", tamanho_disponivel_do_disco);
                                break;
                            }

                            case 8:{
                                List<Dados> ultimosRegistros = con.query("SELECT * FROM servidor ORDER BY idservidor DESC LIMIT 10", new BeanPropertyRowMapper<>(Dados.class));

                               if(ultimosRegistros.isEmpty()){
                                   System.out.println("Não existe registros em nossa base de dados");
                               }
                               else {

                                   for(int i = 0; i <ultimosRegistros.size();i++){
                                       System.out.println(ultimosRegistros);
                                   }
                               }
                            }
                        }
                    } while (!opcao.equals(8));
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

            System.out.println("E pra finalizar vamos criar uma senha lembrando que é 8 dígitos, ok?");
            do {
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
                                    "\n1-)Porcentagem de Uso do Disco\n" +
                                    "2-)Porcentagem de uso da memória\n" +
                                    "3-)Quantidade de ram\n" +
                                    "4-)Tamanho total do disco:\n" +
                                    "5-)Memória Disponível\n" +
                                    "6-)Porcentagem de Uso da CPU:\n" +
                                    "7-)Porcentagem de uso do disco: %s\n" +
                                    "8-)Verificar os ultimos registos e ter uma metrica:\n" +
                                    "9-)Sair" +
                            "+--------------------------+");
                    opcao = leitor.nextInt();




                    switch (opcao) {
                        case 1: {
                            Processador processador = new Processador();
                            System.out.println(processador);
                            Double total_pro = processador.getUso();
                            BigDecimal porcentagem_uso_disco = BigDecimal.valueOf(total_pro).setScale(2, RoundingMode.HALF_UP);
                            con.update("insert into servidor (porcentagem_uso_disco) values (?)", porcentagem_uso_disco);
                            break;
                        }
                        case 2: {
                            Memoria memoria = new Memoria();
                            Double percentual_de_uso_mem = (double) memoria.getEmUso() / memoria.getTotal() * 100;
                            BigDecimal porcentagem_uso_memoria = BigDecimal.valueOf(percentual_de_uso_mem).setScale(2 , RoundingMode.HALF_UP);
                            System.out.println(memoria);
                            System.out.println(porcentagem_uso_memoria);
                            con.update("insert into servidor (porcentagem_uso_memoria) values (?)", porcentagem_uso_memoria);
                            break;
                        }
                        case 3:{
                            Memoria memoria = new Memoria();
                            Double ramDisponivel = (double) memoria.getDisponivel() / (1024 * 1024 * 1024);
                            System.out.println(
                                    String.format("""
                                                Memória Ram disponível: %.2f
                                                
                                                """, ramDisponivel));
                            con.update("insert into servidor (quantidade_de_ram) values (?)", ramDisponivel);
                            break;
                        }
                        case 4:{

                            for (Disco disco : looca.getGrupoDeDiscos().getDiscos()) {
                                tamanhoTotalGiB = (double) disco.getTamanho() / (1024 * 1024 * 1024);
                                tot_disco = Math.round(tamanhoTotalGiB * 100.0) / 100.0;
                                tamanho_disco = Double.valueOf(disco.getTamanho());
                                total_disco = (int) Math.round(tamanhoTotalGiB);
                            }
                            System.out.println(
                                    String.format("""
                                                A quantidade total de disco é de %d
                                                """, total_disco)
                            );

                            con.update("insert into servidor (tamanho_total_disco) values (?)" , tot_disco);
                            break;
                        }
                        case 5:{
                            Memoria memoria = new Memoria();
                            Double memoria_disponivel = memoria.getDisponivel() / (1024 * 1024 * 1024.0);
                            memoria_disponivel = Math.round(memoria_disponivel * 100.0) / 100.0;
                            System.out.println(
                                    String.format("Memória atualmente disponível: %.2f" , memoria_disponivel)
                            );
                            con.update("insert into servidor (memoria_disponivel) values (?)", memoria_disponivel);
                            break;
                        }
                        case 6:{
                            Processador processador = new Processador();
                            Double porcentagem_uso_cpu = processador.getUso() / processador.getNumeroCpusFisicas();
                            BigDecimal porcentagem_de_uso_da_cpu = BigDecimal.valueOf(porcentagem_uso_cpu).setScale(2 , RoundingMode.HALF_UP);
                            System.out.println(
                                    String.format("Porcentagem de uso da cpu %.2f", porcentagem_de_uso_da_cpu));

                            con.update("insert into servidor (porcentagem_uso_cpu) values (?)", porcentagem_uso_cpu);
                            break;
                        }
                        case 7:{

                            for(Disco disco : looca.getGrupoDeDiscos().getDiscos()){
                                tamanhoTotalGiB = (double) disco.getTamanho() / (1024 * 1024 * 1024);
                                tot_disco = Math.round(tamanhoTotalGiB * 100.0) / 100.0;
                                tamanho_disco = Double.valueOf(disco.getTamanho());
                                total_disco = (int) Math.round(tamanhoTotalGiB);


                            }
                            BigDecimal tamanho_disponivel_do_disco = new BigDecimal(tamanho_disco)
                                    .setScale(2, RoundingMode.HALF_UP)
                                    .divide(new BigDecimal(1024 * 1024 * 1024), 2, RoundingMode.HALF_UP);

                            System.out.println(
                                    String.format("Tamanho total do disco : %.2f", tamanho_disponivel_do_disco));

                            con.update("insert into servidor (tamanho_total_disco) values (?)", tamanho_disponivel_do_disco);
                            break;
                        }
                    }
                } while (!opcao.equals(8));
            } catch (Exception erro) {
                erro.printStackTrace();
            }
        }
    }
}
