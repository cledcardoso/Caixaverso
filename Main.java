import model.*;
import service.*;
import util.DateUtils;
import util.NotificacaoCriacao;
import util.NotificacaoPagamento;
import util.NotificacaoEntrega;

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
            System.out.println("\n==== MENU PRINCIPAL ====");
            System.out.println("1 - Clientes");
            System.out.println("2 - Produtos");
            System.out.println("3 - Pedidos");
            System.out.println("0 - Sair");
            System.out.print("Escolha uma opção: ");
            opcao = sc.nextInt(); sc.nextLine();

            switch (opcao) {
                case 1 -> menuClientes();
                case 2 -> menuProdutos();
                case 3 -> menuPedidos();
                case 0 -> System.out.println("Encerrando o sistema...");
                default -> System.out.println("Opção inválida.");
            }
        } while (opcao != 0);
        sc.close();
    }

    private static void menuClientes() {
        int opcao;
        do {
            System.out.println("\n--- Menu Clientes ---");
            System.out.println("1 - Cadastrar Cliente");
            System.out.println("2 - Listar Clientes");
            System.out.println("3 - Atualizar Cliente");
            System.out.println("0 - Voltar");
            System.out.print("Escolha uma opção: ");
            opcao = sc.nextInt(); sc.nextLine();

            switch (opcao) {
                case 1 -> cadastrarCliente();
                case 2 -> clienteService.listarClientes();
                case 3 -> atualizarCliente();
                case 0 -> System.out.println("Voltando ao menu principal...");
                default -> System.out.println("Opção inválida.");
            }
        } while (opcao != 0);
    }

    private static void menuProdutos() {
        int opcao;
        do {
            System.out.println("\n--- Menu Produtos ---");
            System.out.println("1 - Cadastrar Produto");
            System.out.println("2 - Listar Produtos");
            System.out.println("3 - Atualizar Produto");
            System.out.println("0 - Voltar");
            System.out.print("Escolha uma opção: ");
            opcao = sc.nextInt(); sc.nextLine();

            switch (opcao) {
                case 1 -> cadastrarProduto();
                case 2 -> produtoService.listarProdutos();
                case 3 -> atualizarProduto();
                case 0 -> System.out.println("Voltando ao menu principal...");
                default -> System.out.println("Opção inválida.");
            }
        } while (opcao != 0);
    }

    private static void menuPedidos() {
        int opcao;
        do {
            System.out.println("\n--- Menu Pedidos ---");
            System.out.println("1 - Criar Pedido");
            System.out.println("2 - Adicionar Item");
            System.out.println("3 - Remover Item");
            System.out.println("4 - Alterar Quantidade");
            System.out.println("5 - Finalizar Pedido");
            System.out.println("6 - Pagar Pedido");
            System.out.println("7 - Entregar Pedido");
            System.out.println("8 - Mostrar Resumo");
            System.out.println("0 - Voltar");
            System.out.print("Escolha uma opção: ");
            opcao = sc.nextInt(); sc.nextLine();

            switch (opcao) {
                case 1 -> criarPedido();
                case 2 -> adicionarItem();
                case 3 -> removerItem();
                case 4 -> alterarQuantidade();
                case 5 -> finalizarPedido();
                case 6 -> pagarPedido();
                case 7 -> entregarPedido();
                case 8 -> mostrarResumo();
                case 0 -> System.out.println("Voltando ao menu principal...");
                default -> System.out.println("Opção inválida.");
            }
        } while (opcao != 0);
    }

    private static void cadastrarCliente() {
    System.out.print("CPF: ");
    String cpf = sc.nextLine();

    Optional<Cliente> existente = clienteService.buscarPorCpf(cpf);
        if (existente.isPresent()) {
            System.out.println("Já existe um cliente cadastrado com esse CPF");
            return;
        }

        System.out.print("Nome: ");
        String nome = sc.nextLine();
        System.out.print("Email: ");
        String email = sc.nextLine();
        Cliente cliente = new Cliente(cpf, nome, email);
        clienteService.cadastrarCliente(cliente);
    }

    private static void atualizarCliente() {
        String cpf;
        do {
            System.out.print("CPF do cliente: ");
            cpf = sc.nextLine().trim();

            if (cpf.isEmpty()) {
                System.out.println("CPF é obrigatório. Tente novamente.");
                continue;
            }

            Optional<Cliente> clienteOpt = clienteService.buscarPorCpf(cpf);
            if (clienteOpt.isEmpty()) {
                System.out.println("Cliente com CPF " + cpf + " não encontrado. Atualização cancelada.");
                return;
            }

            Cliente cliente = clienteOpt.get();
            System.out.println("Cliente atual: " + cliente.getNome() + " | Email: " + cliente.getEmail());

            System.out.print("Novo nome: ");
            String nome = sc.nextLine();
            System.out.print("Novo email: ");
            String email = sc.nextLine();

            clienteService.atualizarCliente(cpf, nome, email);
            break;

        } while (true);
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

        Optional<Produto> produtoOpt = produtoService.buscarPorId(id);
        if (produtoOpt.isEmpty()) {
            System.out.println("Produto com ID " + id + " não encontrado. Atualização cancelada.");
            return;
        }

        Produto produto = produtoOpt.get();
        System.out.println("Produto atual: " + produto.getNome() + " | Preço: R$" + produto.getPrecoBase());

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
            System.out.println("Pedido concluído.");
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
            System.out.printf("Valor Total: R$%.2f%n", pedidoAtual.getValorTotal());
        } else {
            System.out.println("Nenhum pedido existente.");
        }
    }
}