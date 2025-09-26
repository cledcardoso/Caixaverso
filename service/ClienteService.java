package service;

import model.Cliente;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ClienteService {
    private List<Cliente> clientes = new ArrayList<>();

    public void cadastrarCliente(Cliente cliente) {
        if (buscarPorCpf(cliente.getCpf()).isPresent()) {
            System.out.println("Cliente com CPF já cadastrado.");
            return;
        }
        clientes.add(cliente);
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
            System.out.println("Cliente atualizado com sucesso!");
        } else {
            System.out.println("Cliente não encontrado.");
        }
    }

    public void listarClientes() {
        System.out.println("\n=== Lista de Clientes ===");
        clientes.forEach(c -> System.out.println("CPF: " + c.getCpf() + " | Nome: " + c.getNome() + " | Email: " + c.getEmail()));
    }
}
