package service;

import model.*;

import java.io.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class PedidoService {
    private final List<Pedido> pedidos = new ArrayList<>();
    private final String caminhoArquivo;
    private static long contadorPedidos = 1;

    public PedidoService(String caminhoArquivo) {
        this.caminhoArquivo = caminhoArquivo;
    }

    public void carregarPedidosDoArquivo(ClienteService clienteService, ProdutoService produtoService) {
        File arquivo = new File(caminhoArquivo);
        if (!arquivo.exists()) return;

        try (BufferedReader br = new BufferedReader(new FileReader(arquivo))) {
            String linha;
            while ((linha = br.readLine()) != null) {
                String[] partes = linha.split(";");
                if (partes.length >= 4) {
                    long id = Long.parseLong(partes[0]);
                    String cpf = partes[1];
                    Pedido.Status status = Pedido.Status.valueOf(partes[2]);

                    Cliente cliente = clienteService.buscarPorCpf(cpf).orElse(null);
                    if (cliente == null) continue;

                    Pedido pedido = new Pedido(id, cliente);
                    pedido.setStatus(status);

                    if (partes.length == 5) {
                        String[] itens = partes[4].split("\\|");
                        for (String itemStr : itens) {
                            String[] itemPartes = itemStr.split(",");
                            Long idProduto = Long.parseLong(itemPartes[0]);
                            int qtd = Integer.parseInt(itemPartes[1]);
                            BigDecimal preco = new BigDecimal(itemPartes[2]);

                            Produto produto = produtoService.buscarPorId(idProduto).orElse(null);
                            if (produto != null) {
                                pedido.adicionarItem(new ItemPedido(produto, qtd, preco));
                            }
                        }
                    }

                    pedidos.add(pedido);
                    contadorPedidos = Math.max(contadorPedidos, id + 1);
                }
            }
        } catch (IOException e) {
            System.out.println("Erro ao carregar pedidos: " + e.getMessage());
        }
    }

    public Pedido criarPedido(Cliente cliente) {
        Pedido pedido = new Pedido(contadorPedidos++, cliente);
        pedidos.add(pedido);
        return pedido;
    }

    public void adicionarItem(Pedido pedido, Produto produto, int quantidade, BigDecimal precoVenda) {
        if (pedido.getStatus() != Pedido.Status.ABERTO) {
            System.out.println("Pedido não está aberto para alterações.");
            return;
        }
        ItemPedido item = new ItemPedido(produto, quantidade, precoVenda);
        pedido.adicionarItem(item);
        System.out.println("Item adicionado ao pedido.");
    }

    public void removerItem(Pedido pedido, Long idProduto) {
        ItemPedido item = pedido.getItens().stream()
                .filter(i -> i.getProduto().getId().equals(idProduto))
                .findFirst()
                .orElse(null);

        if (item != null) {
            pedido.removerItem(item);
            System.out.println("Item removido do pedido.");
        } else {
            System.out.println("Produto não encontrado no pedido.");
        }
    }

    public void alterarQuantidade(Pedido pedido, Long idProduto, int novaQuantidade) {
        ItemPedido item = pedido.getItens().stream()
                .filter(i -> i.getProduto().getId().equals(idProduto))
                .findFirst()
                .orElse(null);

        if (item != null && pedido.getStatus() == Pedido.Status.ABERTO) {
            item.setQuantidade(novaQuantidade);
            System.out.println("Quantidade atualizada.");
        } else {
            System.out.println("Item não encontrado ou pedido não está aberto.");
        }
    }

    public void pagarPedido(Pedido pedido) {
        pedido.pagar();
        salvarPedidoNoArquivo(pedido);
        System.out.println("Pedido pago.");
    }

    public void entregarPedido(Pedido pedido) {
        pedido.entregar();
        salvarPedidoNoArquivo(pedido);
        System.out.println("Pedido entregue.");
    }

    private void salvarPedidoNoArquivo(Pedido pedido) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(caminhoArquivo, true))) {
            StringBuilder linha = new StringBuilder();
            linha.append(pedido.getId()).append(";")
                 .append(pedido.getCliente().getCpf()).append(";")
                 .append(pedido.getStatus()).append(";")
                 .append(pedido.getDataCriacao()).append(";");

            List<String> itensStr = new ArrayList<>();
            for (ItemPedido item : pedido.getItens()) {
                itensStr.add(item.getProduto().getId() + "," + item.getQuantidade() + "," + item.getPrecoVenda());
            }
            linha.append(String.join("|", itensStr));

            bw.write(linha.toString());
            bw.newLine();
        } catch (IOException e) {
            System.out.println("Erro ao salvar pedido: " + e.getMessage());
        }
    }
}
