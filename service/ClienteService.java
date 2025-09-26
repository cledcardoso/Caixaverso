package service;

import model.Cliente;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ClienteService {
    private final List<Cliente> clientes = new ArrayList<>();
    private final String caminhoArquivo;

    public ClienteService(String caminhoArquivo) {
        this.caminhoArquivo = caminhoArquivo;
    }

    public void carregarClientesDoArquivo() {
        File arquivo = new File(caminhoArquivo);
        if (!arquivo.exists()) return;

        try (BufferedReader br = new BufferedReader(new FileReader(arquivo))) {
            String linha;
            while ((linha = br.readLine()) != null) {
                String[] partes = linha.split(";");
                if (partes.length == 3) {
                    String cpf = partes[0];
                    String nome = partes[1];
                    String email = partes[2];
                    clientes.add(new Cliente(cpf, nome, email));
                }
            }
        } catch (IOException e) {
            System.out.println("Erro ao carregar clientes: " + e.getMessage());
        }
    }

    public void cadastrarCliente(Cliente cliente) {
        if (buscarPorCpf(cliente.getCpf()).isPresent()) {
            System.out.println("Cliente com CPF já cadastrado.");
            return;
        }
        clientes.add(cliente);
        salvarClienteNoArquivo(cliente);
        System.out.println("Cliente cadastrado com sucesso!");
    }

    public Optional<Cliente> buscarPorCpf(String cpf) {
        return clientes.stream()
                .filter(c -> c.getCpf().equals(cpf))
                .findFirst();
    }

    public void atualizarCliente(String cpf, String novoNome, String novoEmail) {
        Optional<Cliente> clienteOpt = buscarPorCpf(cpf);
        if (clienteOpt.isPresent()) {
            Cliente cliente = clienteOpt.get();
            cliente.setNome(novoNome);
            cliente.setEmail(novoEmail);
            salvarTodosClientes();
            System.out.println("Cliente atualizado com sucesso!");
        } else {
            System.out.println("Cliente não encontrado.");
        }
    }

    public void listarClientes() {
        System.out.println("\n=== Lista de Clientes ===");
        clientes.forEach(c -> System.out.println("CPF: " + c.getCpf() + " | Nome: " + c.getNome() + " | Email: " + c.getEmail()));
    }

    private void salvarClienteNoArquivo(Cliente cliente) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(caminhoArquivo, true))) {
            bw.write(cliente.getCpf() + ";" + cliente.getNome() + ";" + cliente.getEmail());
            bw.newLine();
        } catch (IOException e) {
            System.out.println("Erro ao salvar cliente: " + e.getMessage());
        }
    }

    private void salvarTodosClientes() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(caminhoArquivo))) {
            for (Cliente cliente : clientes) {
                bw.write(cliente.getCpf() + ";" + cliente.getNome() + ";" + cliente.getEmail());
                bw.newLine();
            }
        } catch (IOException e) {
            System.out.println("Erro ao atualizar clientes: " + e.getMessage());
        }
    }
}
