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

        Cliente cliente = null;
        Pedido pedido = null;

        int opcao;
        do {
            System.out.println("\n==== MENU PRINCIPAL ====");
            System.out.println("1 - Cadastrar Cliente");
            System.out.println("2 - Cadastrar Produto");
            System.out.println("3 - Criar Pedido");
            System.out.println("4 - Adicionar Item ao Pedido");
            System.out.println("5 - Finalizar Pedido");
            System.out.println("6 - Pagar Pedido");
            System.out.println("7 - Entregar Pedido");
            System.out.println("8 - Mostrar Resumo do Pedido");
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

                    cliente = new Cliente(nome, email, cpf);
                    clienteService.cadastrarCliente(cliente);
                    System.out.println("Cliente cadastrado com sucesso!");
                    break;

                case 2:
                    System.out.print("ID do produto: ");
                    Long idProduto = sc.nextLong(); sc.nextLine();
                    System.out.print("Nome: ");
                    String nomeProduto = sc.nextLine();
                    System.out.print("Descrição: ");
                    String descricao = sc.nextLine();
                    System.out.print("Preço: ");
                    BigDecimal preco = sc.nextBigDecimal();

                    Produto produto = new Produto(idProduto, nomeProduto, descricao, preco);
                    produtoService.cadastrarProduto(produto);
                    System.out.println("Produto cadastrado com sucesso!");
                    break;

                case 3:
                    System.out.print("CPF do cliente para o pedido: ");
                    String cpfPedido = sc.nextLine();

                    Optional<Cliente> optCliente = clienteService.buscarPorCpf(cpfPedido);

                    if (optCliente.isPresent()) {
                        pedido = pedidoService.criarPedido(optCliente.get());
                        System.out.println("Pedido " + pedido.getId() + " criado com sucesso para o cliente: " + optCliente.get().getNome());
                    } else {
                        System.out.println("Cliente não encontrado!");
                    }
                    break;

                case 4:
                    if (pedido == null) {
                        System.out.println("Crie um pedido primeiro!");
                    } else {
                        System.out.print("ID do produto: ");
                        Long idProdItem = sc.nextLong(); sc.nextLine();

                        Optional<Produto> optProduto = produtoService.buscarPorId(idProdItem);

                        if (optProduto.isPresent()) {
                            Produto prodItem = optProduto.get();
                            System.out.print("Quantidade: ");
                            int qtd = sc.nextInt();
                            pedidoService.adicionarItem(pedido, prodItem, qtd);
                            System.out.println("Item adicionado ao pedido!");
                        } else {
                            System.out.println("Produto não encontrado!");
                        }
                    }
                    break;

                case 5:
                    if (pedido != null) {
                        pedidoService.finalizarPedido(pedido);
                        System.out.println("Pedido finalizado!");
                    } else {
                        System.out.println("Nenhum pedido existente.");
                    }
                    break;

                case 6:
                    if (pedido != null) {
                        pedidoService.pagarPedido(pedido);
                        NotificadorEmail.notificarPagamento(pedido);
                        System.out.println("Pedido pago!");
                    } else {
                        System.out.println("Nenhum pedido existente.");
                    }
                    break;

                case 7:
                    if (pedido != null) {
                        pedidoService.entregarPedido(pedido);
                        NotificadorEmail.notificarEntrega(pedido);
                        System.out.println("Pedido entregue!");
                    } else {
                        System.out.println("Nenhum pedido existente.");
                    }
                    break;

                case 8:
                    if (pedido != null) {
                        System.out.println("\nResumo do Pedido:");
                        System.out.println("Cliente: " + pedido.getCliente().getNome());
                        System.out.println("CPF: " + pedido.getCliente().getCpf());
                        System.out.println("Data: " + DateUtils.formatarData(pedido.getDataCriacao()));
                        System.out.println("Status: " + pedido.getStatus());
                        System.out.println("Valor Total: R$" + pedido.getValorTotal());
                    } else {
                        System.out.println("Nenhum pedido existente.");
                    }
                    break;

                case 0:
                    System.out.println("Saindo do sistema...");
                    break;

                default:
                    System.out.println("Opção inválida!");
            }
        } while (opcao != 0);

        sc.close();
    }
}
