package service;

import model.Cliente;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ClienteService {
    private static final String FILE_NAME = "clientes.csv";
    private List<Cliente> clientes = new ArrayList<>();

    public ClienteService() {
        carregarClientes();
    }

    public void cadastrarCliente(Cliente cliente) {
        clientes.add(cliente);
        salvarClientes();
    }

    public Optional<Cliente> buscarPorId(Long id) {
        return clientes.stream()
                .filter(c -> c.getId().equals(id))
                .findFirst();
    }

    public void listarClientes() {
        clientes.forEach(c -> System.out.println(c.getId() + " - " + c.getNome()));
    }

    private void salvarClientes() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME))) {
            for (Cliente c : clientes) {
                writer.write(c.getId() + "," + c.getNome() + "," + c.getEmail() + "," + c.getCpf());
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("Erro ao salvar clientes: " + e.getMessage());
        }
    }

    private void carregarClientes() {
        File file = new File(FILE_NAME);
        if (!file.exists()) return;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] dados = line.split(",");
                if (dados.length == 4) {
                    Cliente c = new Cliente(
                            Long.parseLong(dados[0]),
                            dados[1],
                            dados[2],
                            dados[3]
                    );
                    clientes.add(c);
                }
            }
        } catch (IOException e) {
            System.out.println("Erro ao carregar clientes: " + e.getMessage());
        }
    }
}
