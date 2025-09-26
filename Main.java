import model.*;
import service.*;
import util.DateUtils;
import util.NotificadorEmail;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        ClienteService clienteService = new ClienteService();
        ProdutoService produtoService = new ProdutoService();
        PedidoService pedidoService = new PedidoService();

        Pedido pedido = null;

        int opcao;
        do {
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
            opcao = sc.nextInt();
            sc.nextLine();

            switch (opcao) {
                case 1:
                    System.out.print("CPF: ");
                    String cpf = sc.nextLine();
                    System.out.print("Nome: ");
                    String nome = sc.nextLine();
                    System.out.print("Email: ");
                    String email = sc.nextLine();
                    Cliente cliente = new Cliente(nome, email, cpf);
                    clienteService.cadastrarCliente(cliente);
                    break;

                case 2:
                    clienteService.listarClientes();
                    break;

                case 3:
                    System.out.print("CPF do cliente a atualizar: ");
                    String cpfAtualizar = sc.nextLine();
                    System.out.print("Novo nome: ");
                    String novoNome = sc.nextLine();
                    System.out.print("Novo email: ");
                    String novoEmail = sc.nextLine();
                    clienteService.atualizarCliente(cpfAtualizar, novoNome, novoEmail);
                    break;

                case 4:
                    System.out.print("ID do produto: ");
                    Long idProduto = sc.nextLong(); sc.nextLine();
                    System.out.print("Nome: ");
                    String nomeProduto = sc.nextLine();
                    System.out.print("Descrição: ");
                    String descricao = sc.nextLine();
                    System.out.print("Preço: ");
                    BigDecimal preco = sc.nextBigDecimal(); sc.nextLine();
                    Produto produto = new Produto(idProduto, nomeProduto, descricao, preco);
                    produtoService.cadastrarProduto(produto);
                    break;

                case 5:
                    produtoService.listarProdutos();
                    break;

                case 6:
                    System.out.print("ID do produto a atualizar: ");
                    Long idAtualizar = sc.nextLong(); sc.nextLine();
                    System.out.print("Novo nome: ");
                    String novoNomeProd = sc.nextLine();
                    System.out.print("Nova descrição: ");
                    String novaDesc = sc.nextLine();
                    System.out.print("Novo preço: ");
                    BigDecimal novoPreco = sc.nextBigDecimal(); sc.nextLine();
                    produtoService.atualizarProduto(idAtualizar, novoNomeProd, novaDesc, novoPreco);
                    break;

                case 7:
                    System.out.print("CPF do cliente para o pedido: ");
                    String cpfPedido = sc.nextLine();
                    Optional<Cliente> optCliente = clienteService.buscarPorCpf(cpfPedido);
                    if (optCliente.isPresent()) {
                        pedido = pedidoService.criarPedido(optCliente.get());
                        System.out.println("Pedido #" + pedido.getId() + " criado para " + optCliente.get().getNome());
                    } else {
                        System.out.println("Cliente não encontrado.");
                    }
                    break;

                case 8:
                    if (pedido != null) {
                        System.out.print("ID do produto: ");
                        Long idProdItem = sc.nextLong(); sc.nextLine();
                        Optional<Produto> optProduto = produtoService.buscarPorId(idProdItem);
                        if (optProduto.isPresent()) {
                            Produto prodItem = optProduto.get();
                            System.out.print("Quantidade: ");
                            int qtd = sc.nextInt();
                            System.out.print("Preço de venda: ");
                            BigDecimal precoVenda = sc.nextBigDecimal(); sc.nextLine();
                            pedidoService.adicionarItem(pedido, prodItem, qtd, precoVenda);

                        } else {
                            System.out.println("Produto não encontrado.");
                        }
                    } else {
                        System.out.println("Crie um pedido primeiro.");
                    }
                    break;

                case 9:
                    if (pedido != null) {
                        System.out.print("ID do produto a remover: ");
                        Long idRemover = sc.nextLong(); sc.nextLine();
                        pedidoService.removerItem(pedido, idRemover);
                    } else {
                        System.out.println("Crie um pedido primeiro.");
                    }
                    break;

                case 10:
                    if (pedido != null) {
                        System.out.print("ID do produto a alterar: ");
                        Long idAlterar = sc.nextLong(); sc.nextLine();
                        System.out.print("Nova quantidade: ");
                        int novaQtd = sc.nextInt(); sc.nextLine();
                        pedidoService.alterarQuantidade(pedido, idAlterar, novaQtd);
                    } else {
                        System.out.println("Crie um pedido primeiro.");
                    }
                    break;

                case 11:
                    if (pedido != null) {
                        pedido.finalizar();
                        System.out.println("Pedido finalizado.");
                    } else {
                        System.out.println("Nenhum pedido existente.");
                    }
                    break;

                case 12:
                    if (pedido != null) {
                        pedidoService.pagarPedido(pedido);
                        NotificadorEmail.notificarPagamento(pedido);
                    } else {
                        System.out.println("Nenhum pedido existente.");
                    }
                    break;

                case 13:
                    if (pedido != null) {
                        pedidoService.entregarPedido(pedido);
                        NotificadorEmail.notificarEntrega(pedido);
                    } else {
                        System.out.println("Nenhum pedido existente.");
                    }
                    break;

                case 14:
                    if (pedido != null) {
                        System.out.println("\n=== Resumo do Pedido ===");
                        System.out.println("Pedido: #" + pedido.getId());
                        System.out.println("Cliente: " + pedido.getCliente().getNome() + " | CPF: " + pedido.getCliente().getCpf());
                        System.out.println("Data: " + DateUtils.formatarData(pedido.getDataCriacao()));
                        System.out.println("Status: " + pedido.getStatus());
                        System.out.println("Itens:");
                        pedido.getItens().forEach(item -> System.out.println("- " + item.getProduto().getNome() + " | Qtd: " + item.getQuantidade() + " | Preço: R$" + item.getPrecoVenda()));
                        System.out.println("Valor Total: R$" + pedido.getValorTotal());
                    } else {
                        System.out.println("Nenhum pedido existente.");
                    }
                    break;

                case 0:
                    System.out.println("Saindo do sistema...");
                    break;

                default:
                    System.out.println("Opção inválida.");
            }
        } while (opcao != 0);

        sc.close();
    }
}
