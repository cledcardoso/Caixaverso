package service;

import model.Cliente;
import model.ItemPedido;
import model.Pedido;
import model.Produto;
import model.enums.StatusPedido;
import util.DateUtils;

import java.io.*;
import java.math.BigDecimal;
import java.util.*;

public class PedidoService {
    private static final String PEDIDOS_FILE = "pedidos.csv";
    private static final String ITENS_FILE = "itens_pedido.csv";

    private List<Pedido> pedidos = new ArrayList<>();
    private ClienteService clienteService;
    private ProdutoService produtoService;

    public PedidoService(ClienteService clienteService, ProdutoService produtoService) {
        this.clienteService = clienteService;
        this.produtoService = produtoService;
        carregarPedidos();
    }

    public Pedido criarPedido(Cliente cliente) {
        Pedido pedido = new Pedido(cliente);
        pedidos.add(pedido);
        salvarPedidos();
        return pedido;
    }

    public void adicionarItem(Pedido pedido, Produto produto, int quantidade) {
        ItemPedido item = new ItemPedido(produto, quantidade, produto.getPrecoBase());
        pedido.adicionarItem(item);
        salvarPedidos();
    }

    public void finalizarPedido(Pedido pedido) {
        pedido.finalizar();
        salvarPedidos();
    }

    public void pagarPedido(Pedido pedido) {
        pedido.pagar();
        salvarPedidos();
    }

    public void entregarPedido(Pedido pedido) {
        pedido.entregar();
        salvarPedidos();
    }

    public Optional<Pedido> buscarPorId(Long id) {
        return pedidos.stream().filter(p -> p.getId().equals(id.intValue())).findFirst();
    }

    public void listarPedidos() {
        pedidos.forEach(p -> {
            System.out.println("Pedido " + p.getId() + " - Cliente: " + p.getCliente().getNome() +
                    " - Status: " + p.getStatus() + " - Total: R$ " + p.getValorTotal());
        });
    }

    private void salvarPedidos() {
        try (BufferedWriter writerPedidos = new BufferedWriter(new FileWriter(PEDIDOS_FILE));
             BufferedWriter writerItens = new BufferedWriter(new FileWriter(ITENS_FILE))) {

            for (Pedido p : pedidos) {
                writerPedidos.write(p.getId() + "," + p.getCliente().getId() + "," +
                        DateUtils.formatarData(p.getDataCriacao()) + "," +
                        p.getStatus() + "," + p.getValorTotal());
                writerPedidos.newLine();

                for (ItemPedido item : p.getItens()) {
                    writerItens.write(p.getId() + "," + item.getProduto().getId() + "," +
                            item.getQuantidade() + "," + item.getPrecoUnitario());
                    writerItens.newLine();
                }
            }
        } catch (IOException e) {
            System.out.println("Erro ao salvar pedidos: " + e.getMessage());
        }
    }

    private void carregarPedidos() {
        File filePedidos = new File(PEDIDOS_FILE);
        File fileItens = new File(ITENS_FILE);
        if (!filePedidos.exists()) return;

        Map<Integer, Pedido> mapaPedidos = new HashMap<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(filePedidos))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] dados = line.split(",");
                if (dados.length == 5) {
                    Integer idPedido = Integer.parseInt(dados[0]);
                    Long idCliente = Long.parseLong(dados[1]);
                    String status = dados[3];
                    BigDecimal valorTotal = new BigDecimal(dados[4]);

                    Optional<Cliente> optCliente = clienteService.buscarPorId(idCliente);
                    if (optCliente.isEmpty()) continue;

                    Pedido pedido = new Pedido(idPedido, optCliente.get());
                    pedido.setStatus(StatusPedido.valueOf(status));
                    pedido.setValorTotal(valorTotal);

                    pedidos.add(pedido);
                    mapaPedidos.put(idPedido, pedido);
                }
            }

            if (fileItens.exists()) {
                try (BufferedReader readerItens = new BufferedReader(new FileReader(fileItens))) {
                    String lineItem;
                    while ((lineItem = readerItens.readLine()) != null) {
                        String[] dados = lineItem.split(",");
                        if (dados.length == 4) {
                            Integer idPedido = Integer.parseInt(dados[0]);
                            Long idProduto = Long.parseLong(dados[1]);
                            int quantidade = Integer.parseInt(dados[2]);
                            BigDecimal precoUnitario = new BigDecimal(dados[3]);

                            Pedido pedido = mapaPedidos.get(idPedido);
                            Optional<Produto> optProduto = produtoService.buscarPorId(idProduto);

                            if (pedido != null && optProduto.isPresent()) {
                                ItemPedido item = new ItemPedido(optProduto.get(), quantidade, precoUnitario);
                                pedido.getItens().add(item);
                            }
                        }
                    }
                }
            }

        } catch (IOException e) {
            System.out.println("Erro ao carregar pedidos: " + e.getMessage());
        }
    }
}