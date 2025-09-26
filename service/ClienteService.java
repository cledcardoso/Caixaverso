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
            long maiorId = 0;

            while ((linha = br.readLine()) != null) {
                String[] partes = linha.split(";");
                if (partes.length == 4) {
                    long id = Long.parseLong(partes[0]);
                    String cpf = partes[1];
                    String nome = partes[2];
                    String email = partes[3];

                    Cliente cliente = new Cliente(cpf, nome, email);
                    restaurarId(cliente, id);
                    clientes.add(cliente);

                    if (id > maiorId) maiorId = id;
                }
            }

            Cliente.atualizarContador(maiorId);
        } catch (IOException e) {
            System.out.println("Erro ao carregar clientes: " + e.getMessage());
        }
    }

    public void cadastrarCliente(Cliente cliente) {
        Optional<Cliente> existente = buscarPorCpf(cliente.getCpf());
        if (existente.isPresent()) {
            System.out.println("Erro: já existe um cliente com esse CPF.");
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
        clientes.forEach(c -> System.out.println("ID: " + c.getId() + " | CPF: " + c.getCpf() + " | Nome: " + c.getNome() + " | Email: " + c.getEmail()));
    }

    private void salvarClienteNoArquivo(Cliente cliente) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(caminhoArquivo, true))) {
            bw.write(cliente.getId() + ";" + cliente.getCpf() + ";" + cliente.getNome() + ";" + cliente.getEmail());
            bw.newLine();
        } catch (IOException e) {
            System.out.println("Erro ao salvar cliente: " + e.getMessage());
        }
    }

    private void salvarTodosClientes() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(caminhoArquivo))) {
            for (Cliente cliente : clientes) {
                bw.write(cliente.getId() + ";" + cliente.getCpf() + ";" + cliente.getNome() + ";" + cliente.getEmail());
                bw.newLine();
            }
        } catch (IOException e) {
            System.out.println("Erro ao atualizar clientes: " + e.getMessage());
        }
    }

    private void restaurarId(Cliente cliente, long id) {
        try {
            var field = Cliente.class.getDeclaredField("id");
            field.setAccessible(true);
            field.setLong(cliente, id);
        } catch (Exception e) {
            System.out.println("Erro ao restaurar ID do cliente: " + e.getMessage());
        }
    }
}
