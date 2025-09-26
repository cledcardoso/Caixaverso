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

        int opcao = -1;
        do {
            System.out.println("\n==== MENU PRINCIPAL ====");
            System.out.println("1 - Clientes");
            System.out.println("2 - Produtos");
            System.out.println("3 - Pedidos");
            System.out.println("0 - Sair");
            System.out.print("Escolha uma opção: ");
            String input = sc.nextLine().trim();
            if (!input.matches("\\d+")) {
                System.out.println("Entrada inválida. Digite apenas números.");
                continue;
            }
            opcao = Integer.parseInt(input);

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
        int opcao = -1;
        do {
            System.out.println("\n--- Menu Clientes ---");
            System.out.println("1 - Cadastrar Cliente");
            System.out.println("2 - Listar Clientes");
            System.out.println("3 - Atualizar Cliente");
            System.out.println("0 - Voltar");
            System.out.print("Escolha uma opção: ");
            String input = sc.nextLine().trim();
            if (!input.matches("\\d+")) {
                System.out.println("Entrada inválida. Digite apenas números.");
                continue;
            }
            opcao = Integer.parseInt(input);

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
        int opcao = -1;
        do {
            System.out.println("\n--- Menu Produtos ---");
            System.out.println("1 - Cadastrar Produto");
            System.out.println("2 - Listar Produtos");
            System.out.println("3 - Atualizar Produto");
            System.out.println("0 - Voltar");
            System.out.print("Escolha uma opção: ");
            String input = sc.nextLine().trim();
            if (!input.matches("\\d+")) {
                System.out.println("Entrada inválida. Digite apenas números.");
                continue;
            }
            opcao = Integer.parseInt(input);

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
        int opcao = -1;
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
            String input = sc.nextLine().trim();
            if (!input.matches("\\d+")) {
                System.out.println("Entrada inválida. Digite apenas números.");
                continue;
            }
            opcao = Integer.parseInt(input);

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
        String cpf;
        do {
            System.out.print("CPF (somente números): ");
            cpf = sc.nextLine().trim();
            if (!cpf.matches("\\d+")) {
                System.out.println("CPF inválido. Digite apenas números.");
                continue;
            }
            break;
        } while (true);

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
        System.out.println("Cliente cadastrado com sucesso.");
    }

    private static void atualizarCliente() {
        String cpf;
        do {
            System.out.print("CPF do cliente (somente números): ");
            cpf = sc.nextLine().trim();

            if (!cpf.matches("\\d+")) {
                System.out.println("CPF inválido. Digite apenas números.");
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
            System.out.println("Cliente atualizado com sucesso.");
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
        System.out.println("Produto cadastrado com sucesso.");
    }

    private static void atualizarProduto() {
        Long id = null;
        do {
            System.out.print("ID do produto (somente números): ");
            String input = sc.nextLine().trim();
            if (!input.matches("\\d+")) {
                System.out.println("ID inválido. Digite apenas números.");
                continue;
            }
            id = Long.parseLong(input);
            break;
        } while (true);

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
        System.out.println("Produto atualizado com sucesso.");
    }

    private static void criarPedido() {
        String cpf;
        do {
            System.out.print("CPF do cliente (somente números): ");
            cpf = sc.nextLine().trim();

            if (cpf.isEmpty()) {
                System.out.println("CPF é obrigatório.");
                continue;
            }

            if (!cpf.matches("\\d+")) {
                System.out.println("CPF inválido. Digite apenas números.");
                continue;
            }

            Optional<Cliente> clienteOpt = clienteService.buscarPorCpf(cpf);
            if (clienteOpt.isEmpty()) {
                System.out.println("Cliente não encontrado.");
                return;
            }

            pedidoAtual = pedidoService.criarPedido(clienteOpt.get());
            System.out.println("Pedido #" + pedidoAtual.getId() + " criado para " + clienteOpt.get().getNome());
            break;
        } while (true);

        // Adição de itens
        boolean adicionouItem = false;
        while (true) {
            System.out.print("Deseja adicionar um item ao pedido? (s/n): ");
            String resposta = sc.nextLine().trim().toLowerCase();
            if (resposta.equals("n")) break;
            if (!resposta.equals("s")) {
                System.out.println("Resposta inválida. Digite 's' ou 'n'.");
                continue;
            }

            Long idProduto = null;
            do {
                System.out.print("ID do produto (somente números): ");
                String input = sc.nextLine().trim();
                if (!input.matches("\\d+")) {
                    System.out.println("ID inválido. Digite apenas números.");
                    continue;
                }
                idProduto = Long.parseLong(input);
                break;
            } while (true);

            Optional<Produto> produtoOpt = produtoService.buscarPorId(idProduto);
            if (produtoOpt.isEmpty()) {
                System.out.println("Produto não encontrado.");
                continue;
            }

            System.out.print("Quantidade: ");
            int qtd = sc.nextInt(); sc.nextLine();
            if (qtd <= 0) {
                System.out.println("Quantidade deve ser maior que zero.");
                continue;
            }

            System.out.print("Preço de venda: ");
            BigDecimal precoVenda = sc.nextBigDecimal(); sc.nextLine();
            if (precoVenda.compareTo(BigDecimal.ZERO) <= 0) {
                System.out.println("Preço de venda deve ser maior que zero.");
                continue;
            }

            pedidoService.adicionarItem(pedidoAtual, produtoOpt.get(), qtd, precoVenda);
            adicionouItem = true;
            System.out.println("Item adicionado com sucesso.");
        }

        if (!adicionouItem) {
            System.out.println("Pedido não pode ser finalizado sem itens. Cancelando criação.");
            pedidoAtual = null;
        }
    }

    private static Pedido selecionarPedido() {
        pedidoService.listarPedidos();
        System.out.print("Digite o ID do pedido desejado: ");
        String input = sc.nextLine().trim();
        if (!input.matches("\\d+")) {
            System.out.println("ID inválido.");
            return null;
        }
        long id = Long.parseLong(input);
        return pedidoService.buscarPorId(id).orElse(null);
    }

    private static void adicionarItem() {
        if (pedidoService.listarPedidosVazios()) {
            System.out.println("Nenhum pedido cadastrado.");
            return;
        }

        Pedido pedido = selecionarPedido();
        if (pedido == null) {
            System.out.println("Pedido não encontrado.");
            return;
        }

        Long id = null;
        do {
            System.out.print("ID do produto (somente números): ");
            String input = sc.nextLine().trim();
            if (!input.matches("\\d+")) {
                System.out.println("ID inválido. Digite apenas números.");
                continue;
            }
            id = Long.parseLong(input);
            break;
        } while (true);

        Optional<Produto> produtoOpt = produtoService.buscarPorId(id);
        if (produtoOpt.isPresent()) {
            System.out.print("Quantidade: ");
            int qtd = sc.nextInt(); sc.nextLine();
            if (qtd <= 0) {
                System.out.println("Quantidade deve ser maior que zero.");
                return;
            }

            System.out.print("Preço de venda: ");
            BigDecimal precoVenda = sc.nextBigDecimal(); sc.nextLine();
            if (precoVenda.compareTo(BigDecimal.ZERO) <= 0) {
                System.out.println("Preço de venda deve ser maior que zero.");
                return;
            }

            pedidoService.adicionarItem(pedido, produtoOpt.get(), qtd, precoVenda);
            System.out.println("Item adicionado ao pedido.");
        } else {
            System.out.println("Produto não encontrado.");
        }
    }

    private static void removerItem() {
        if (pedidoService.listarPedidosVazios()) {
            System.out.println("Nenhum pedido cadastrado.");
            return;
        }

        Pedido pedido = selecionarPedido();
        if (pedido == null) {
            System.out.println("Pedido não encontrado.");
            return;
        }

        Long id = null;
        do {
            System.out.print("ID do produto a remover (somente números): ");
            String input = sc.nextLine().trim();
            if (!input.matches("\\d+")) {
                System.out.println("ID inválido. Digite apenas números.");
                continue;
            }
            id = Long.parseLong(input);
            break;
        } while (true);

        pedidoService.removerItem(pedido, id);
        System.out.println("Item removido do pedido.");
    }

    private static void alterarQuantidade() {
        if (pedidoService.listarPedidosVazios()) {
            System.out.println("Nenhum pedido cadastrado.");
            return;
        }

        Pedido pedido = selecionarPedido();
        if (pedido == null) {
            System.out.println("Pedido não encontrado.");
            return;
        }

        Long id = null;
        do {
            System.out.print("ID do produto a alterar (somente números): ");
            String input = sc.nextLine().trim();
            if (!input.matches("\\d+")) {
                System.out.println("ID inválido. Digite apenas números.");
                continue;
            }
            id = Long.parseLong(input);
            break;
        } while (true);

        System.out.print("Nova quantidade: ");
        int qtd = sc.nextInt(); sc.nextLine();
        if (qtd <= 0) {
            System.out.println("Quantidade deve ser maior que zero.");
            return;
        }

        pedidoService.alterarQuantidade(pedido, id, qtd);
        System.out.println("Quantidade alterada com sucesso.");
    }

    private static void finalizarPedido() {
        if (pedidoService.listarPedidosVazios()) {
            System.out.println("Nenhum pedido cadastrado.");
            return;
        }

        Pedido pedido = selecionarPedido();
        if (pedido == null) {
            System.out.println("Pedido não encontrado.");
            return;
        }

        if (pedido.getItens().isEmpty()) {
            System.out.println("Pedido não pode ser finalizado sem itens.");
            return;
        }

        pedidoService.finalizarPedido(pedido);
        System.out.println("Pedido finalizado.");
        new Thread(new NotificacaoCriacao(pedido)).start();
    }

    private static void pagarPedido() {
        if (pedidoService.listarPedidosVazios()) {
            System.out.println("Nenhum pedido cadastrado.");
            return;
        }

        Pedido pedido = selecionarPedido();
        if (pedido == null) {
            System.out.println("Pedido não encontrado.");
            return;
        }

        pedidoService.pagarPedido(pedido);
        System.out.println("Pedido pago.");
        new Thread(new NotificacaoPagamento(pedido)).start();
    }

    private static void entregarPedido() {
        if (pedidoService.listarPedidosVazios()) {
            System.out.println("Nenhum pedido cadastrado.");
            return;
        }

        Pedido pedido = selecionarPedido();
        if (pedido == null) {
            System.out.println("Pedido não encontrado.");
            return;
        }

        pedidoService.entregarPedido(pedido);
        System.out.println("Pedido entregue.");
        new Thread(new NotificacaoEntrega(pedido)).start();
    }

    private static void mostrarResumo() {
        if (pedidoService.listarPedidosVazios()) {
            System.out.println("Nenhum pedido cadastrado.");
            return;
        }

        Pedido pedido = selecionarPedido();
        if (pedido != null) {
            System.out.println("\n=== Resumo do Pedido ===");
            System.out.println("Pedido: #" + pedido.getId());
            System.out.println("Cliente: " + pedido.getCliente().getNome() + " | CPF: " + pedido.getCliente().getCpf());
            System.out.println("Data: " + DateUtils.formatarData(pedido.getDataCriacao()));
            System.out.println("Status: " + pedido.getStatus());
            System.out.println("Itens:");
            pedido.getItens().forEach(item ->
                System.out.println("- " + item.getProduto().getNome() +
                        " | Qtd: " + item.getQuantidade() +
                        " | Preço: R$" + item.getPrecoVenda()));
            System.out.printf("Valor Total: R$%.2f%n", pedido.getValorTotal());
        } else {
            System.out.println("Pedido não encontrado.");
        }
    }
}