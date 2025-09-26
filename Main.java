import model.*;
import service.*;
import util.DateUtils;
import util.NotificadorEmail;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.Scanner;

public class Main {
    private static final Scanner sc = new Scanner(System.in);
    private static final ClienteService clienteService = new ClienteService("clientes.csv");
    private static final ProdutoService produtoService = new ProdutoService("produtos.csv");
    private static final PedidoService pedidoService = new PedidoService("pedidos.csv");
    private static Pedido pedidoAtual;

    public static void main(String[] args) {
        clienteService.carregarClientesDoArquivo();
        produtoService.carregarProdutosDoArquivo();
        pedidoService.carregarPedidosDoArquivo(clienteService, produtoService);

        int opcao;
        do {
            exibirMenu();
            opcao = sc.nextInt();
            sc.nextLine(); // Limpa o buffer

            switch (opcao) {
                case 1 -> cadastrarCliente();
                case 2 -> clienteService.listarClientes();
                case 3 -> atualizarCliente();
                case 4 -> cadastrarProduto();
                case 5 -> produtoService.listarProdutos();
                case 6 -> atualizarProduto();
                case 7 -> criarPedido();
                case 8 -> adicionarItem();
                case 9 -> removerItem();
                case 10 -> alterarQuantidade();
                case 11 -> finalizarPedido();
                case 12 -> pagarPedido();
                case 13 -> entregarPedido();
                case 14 -> mostrarResumo();
                case 0 -> System.out.println("Encerrando o sistema...");
                default -> System.out.println("Opção inválida.");
            }
        } while (opcao != 0);
        sc.close();
    }

    private static void exibirMenu() {
        System.out.println("\n==== MENU PRINCIPAL ====");
        System.out.println("1 - Cadastrar Cliente");
        System.out.println("2 - Listar Clientes");
        System.out.println("3 - Atualizar Cliente");
        System.out.println("4 - Cadastrar Produto");
        System.out.println("5 - Listar Produtos");
        System.out.println("6 - Atualizar Produto");
        System.out.println("7 - Criar Pedido");
        System.out.println("8 - Adicionar Item ao Pedido");
        System.out.println("9 - Remover Item do Pedido");
        System.out.println("10 - Alterar Quantidade de Item");
        System.out.println("11 - Finalizar Pedido");
        System.out.println("12 - Pagar Pedido");
        System.out.println("13 - Entregar Pedido");
        System.out.println("14 - Mostrar Resumo do Pedido");
        System.out.println("0 - Sair");
        System.out.print("Escolha uma opção: ");
    }

    private static void cadastrarCliente() {
        System.out.print("CPF: ");
        String cpf = sc.nextLine();
        System.out.print("Nome: ");
        String nome = sc.nextLine();
        System.out.print("Email: ");
        String email = sc.nextLine();
        Cliente cliente = new Cliente(cpf, nome, email);
        clienteService.cadastrarCliente(cliente);
    }

    private static void atualizarCliente() {
        System.out.print("CPF do cliente: ");
        String cpf = sc.nextLine();
        System.out.print("Novo nome: ");
        String nome = sc.nextLine();
        System.out.print("Novo email: ");
        String email = sc.nextLine();
        clienteService.atualizarCliente(cpf, nome, email);
    }

    private static void cadastrarProduto() {
        System.out.print("Nome: ");
        String nome = sc.nextLine();
        System.out.print("Descrição: ");
        String descricao = sc.nextLine();
        System.out.print("Preço: ");
        BigDecimal preco = sc.nextBigDecimal(); sc.nextLine();
        Produto produto = new Produto(nome, descricao, preco);
        produtoService.cadastrarProduto(produto);
    }

    private static void atualizarProduto() {
        System.out.print("ID do produto: ");
        Long id = sc.nextLong(); sc.nextLine();
        System.out.print("Novo nome: ");
        String nome = sc.nextLine();
        System.out.print("Nova descrição: ");
        String descricao = sc.nextLine();
        System.out.print("Novo preço: ");
        BigDecimal preco = sc.nextBigDecimal(); sc.nextLine();
        produtoService.atualizarProduto(id, nome, descricao, preco);
    }

    private static void criarPedido() {
        System.out.print("CPF do cliente: ");
        String cpf = sc.nextLine();
        Optional<Cliente> clienteOpt = clienteService.buscarPorCpf(cpf);
        if (clienteOpt.isPresent()) {
            pedidoAtual = pedidoService.criarPedido(clienteOpt.get());
            System.out.println("Pedido #" + pedidoAtual.getId() + " criado para " + clienteOpt.get().getNome());
        } else {
            System.out.println("Cliente não encontrado.");
        }
    }

    private static void adicionarItem() {
        if (pedidoAtual == null) {
            System.out.println("Crie um pedido primeiro.");
            return;
        }
        System.out.print("ID do produto: ");
        Long id = sc.nextLong(); sc.nextLine();
        Optional<Produto> produtoOpt = produtoService.buscarPorId(id);
        if (produtoOpt.isPresent()) {
            System.out.print("Quantidade: ");
            int qtd = sc.nextInt();
            System.out.print("Preço de venda: ");
            BigDecimal precoVenda = sc.nextBigDecimal(); sc.nextLine();
            pedidoService.adicionarItem(pedidoAtual, produtoOpt.get(), qtd, precoVenda);
        } else {
            System.out.println("Produto não encontrado.");
        }
    }

    private static void removerItem() {
        if (pedidoAtual == null) {
            System.out.println("Crie um pedido primeiro.");
            return;
        }
        System.out.print("ID do produto a remover: ");
        Long id = sc.nextLong(); sc.nextLine();
        pedidoService.removerItem(pedidoAtual, id);
    }

    private static void alterarQuantidade() {
        if (pedidoAtual == null) {
            System.out.println("Crie um pedido primeiro.");
            return;
        }
        System.out.print("ID do produto a alterar: ");
        Long id = sc.nextLong(); sc.nextLine();
        System.out.print("Nova quantidade: ");
        int qtd = sc.nextInt(); sc.nextLine();
        pedidoService.alterarQuantidade(pedidoAtual, id, qtd);
    }

    private static void finalizarPedido() {
        if (pedidoAtual != null) {
            pedidoAtual.finalizar();
            System.out.println("Pedido finalizado.");
        } else {
            System.out.println("Nenhum pedido existente.");
        }
    }

    private static void pagarPedido() {
        if (pedidoAtual != null) {
            pedidoService.pagarPedido(pedidoAtual);
            NotificadorEmail.notificarPagamento(pedidoAtual);
        } else {
            System.out.println("Nenhum pedido existente.");
        }
    }

    private static void entregarPedido() {
        if (pedidoAtual != null) {
            pedidoService.entregarPedido(pedidoAtual);
            NotificadorEmail.notificarEntrega(pedidoAtual);
        } else {
            System.out.println("Nenhum pedido existente.");
        }
    }

    private static void mostrarResumo() {
        if (pedidoAtual != null) {
            System.out.println("\n=== Resumo do Pedido ===");
            System.out.println("Pedido: #" + pedidoAtual.getId());
            System.out.println("Cliente: " + pedidoAtual.getCliente().getNome() + " | CPF: " + pedidoAtual.getCliente().getCpf());
            System.out.println("Data: " + DateUtils.formatarData(pedidoAtual.getDataCriacao()));
            System.out.println("Status: " + pedidoAtual.getStatus());
            System.out.println("Itens:");
            pedidoAtual.getItens().forEach(item ->
                System.out.println("- " + item.getProduto().getNome() +
                        " | Qtd: " + item.getQuantidade() +
                        " | Preço: R$" + item.getPrecoVenda()));
            System.out.println("Valor Total: R$" + pedidoAtual.getValorTotal());
        } else {
            System.out.println("Nenhum pedido existente.");
        }
    }
}
