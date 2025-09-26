package service;

import model.*;

import util.NotificacaoCriacao;
import util.NotificacaoPagamento;
import util.NotificacaoEntrega;

import java.io.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PedidoService {
    private final List<Pedido> pedidos = new ArrayList<>();
    private final String caminhoArquivo;

    public PedidoService(String caminhoArquivo) {
        this.caminhoArquivo = caminhoArquivo;
    }

    public void carregarPedidosDoArquivo(ClienteService clienteService, ProdutoService produtoService) {
        File arquivo = new File(caminhoArquivo);
        if (!arquivo.exists()) return;

        try (BufferedReader br = new BufferedReader(new FileReader(arquivo))) {
            String linha;
            long maiorId = 0;

            while ((linha = br.readLine()) != null) {
                String[] partes = linha.split(";");
                if (partes.length >= 4) {
                    long id = Long.parseLong(partes[0]);
                    String cpf = partes[1];
                    Pedido.Status status = Pedido.Status.valueOf(partes[2]);

                    Cliente cliente = clienteService.buscarPorCpf(cpf).orElse(null);
                    if (cliente == null) continue;

                    Pedido pedido = new Pedido(cliente);
                    restaurarId(pedido, id);
                    pedido.setStatus(status);

                    if (partes.length == 5) {
                        String[] itens = partes[4].split("\\|");
                        for (String itemStr : itens) {
                            String[] itemPartes = itemStr.split(",");
                            long idProduto = Long.parseLong(itemPartes[0]);
                            int qtd = Integer.parseInt(itemPartes[1]);
                            BigDecimal preco = new BigDecimal(itemPartes[2]);

                            Produto produto = produtoService.buscarPorId(idProduto).orElse(null);
                            if (produto != null) {
                                pedido.adicionarItem(new ItemPedido(produto, qtd, preco));
                            }
                        }
                    }

                    pedidos.add(pedido);
                    if (id > maiorId) maiorId = id;
                }
            }

            Pedido.atualizarContador(maiorId);
        } catch (IOException e) {
            System.out.println("Erro ao carregar pedidos: " + e.getMessage());
        }
    }

    public Pedido criarPedido(Cliente cliente) {
        Pedido pedido = new Pedido(cliente);
        pedidos.add(pedido);
        return pedido;
    }

    public Optional<Pedido> buscarPorId(long id) {
        return pedidos.stream().filter(p -> p.getId() == id).findFirst();
    }

    public void adicionarItem(Pedido pedido, Produto produto, int quantidade, BigDecimal precoVenda) {
        if (pedido.getStatus() != Pedido.Status.ABERTO) {
            System.out.println("Pedido não está aberto para alterações.");
            return;
        }
        if (quantidade <= 0 || precoVenda.compareTo(BigDecimal.ZERO) <= 0) {
            System.out.println("Quantidade e preço devem ser maiores que zero.");
            return;
        }
        ItemPedido item = new ItemPedido(produto, quantidade, precoVenda);
        pedido.adicionarItem(item);
    }

    public void removerItem(Pedido pedido, long idProduto) {
        if (pedido.getStatus() != Pedido.Status.ABERTO) {
            System.out.println("Pedido não está aberto para alterações.");
            return;
        }

        ItemPedido item = pedido.getItens().stream()
                .filter(i -> i.getProduto().getId() == idProduto)
                .findFirst()
                .orElse(null);

        if (item != null) {
            pedido.removerItem(item);
            System.out.println("Item removido do pedido.");
        } else {
            System.out.println("Produto não encontrado no pedido.");
        }
    }

    public void alterarQuantidade(Pedido pedido, long idProduto, int novaQuantidade) {
        if (pedido.getStatus() != Pedido.Status.ABERTO) {
            System.out.println("Pedido não está aberto para alterações.");
            return;
        }

        ItemPedido item = pedido.getItens().stream()
                .filter(i -> i.getProduto().getId() == idProduto)
                .findFirst()
                .orElse(null);

        if (item != null) {
            item.setQuantidade(novaQuantidade);
            System.out.println("Quantidade atualizada.");
        } else {
            System.out.println("Item não encontrado no pedido.");
        }
    }

    public void listarPedidos() {
        if (pedidos.isEmpty()) {
            System.out.println("Nenhum pedido cadastrado.");
            return;
        }
        for (Pedido p : pedidos) {
            System.out.println("ID: " + p.getId() + " | Cliente: " + p.getCliente().getNome() + " | Status: " + p.getStatus());
        }
    }

    public void finalizarPedido(Pedido pedido) {
        if (pedido.getStatus() != Pedido.Status.ABERTO) {
            System.out.println("Pedido já foi finalizado ou está em outro estado.");
            return;
        }

        if (pedido.getItens().isEmpty()) {
            System.out.println("Erro: pedido não pode ser finalizado sem itens.");
            return;
        }

        if (pedido.getValorTotal().compareTo(BigDecimal.ZERO) <= 0) {
            System.out.println("Erro: valor total do pedido deve ser maior que zero.");
            return;
        }

        pedido.setStatus(Pedido.Status.AGUARDANDO_PAGAMENTO);
        salvarPedidoNoArquivo(pedido);
        new Thread(new NotificacaoCriacao(pedido)).start();
        System.out.println("Pedido finalizado e aguardando pagamento.");
    }

    public void pagarPedido(Pedido pedido) {
        if (pedido.getStatus() != Pedido.Status.AGUARDANDO_PAGAMENTO) {
            System.out.println("Erro: pagamento só pode ser feito em pedidos aguardando pagamento.");
            return;
        }

        pedido.setStatus(Pedido.Status.PAGO);
        salvarPedidoNoArquivo(pedido);
        new Thread(new NotificacaoPagamento(pedido)).start();
        System.out.println("Pedido pago.");
    }

    public void entregarPedido(Pedido pedido) {
        if (pedido.getStatus() != Pedido.Status.PAGO) {
            System.out.println("Erro: pedido só pode ser entregue após pagamento.");
            return;
        }

        pedido.setStatus(Pedido.Status.FINALIZADO);
        salvarPedidoNoArquivo(pedido);
        new Thread(new NotificacaoEntrega(pedido)).start();
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

    private void restaurarId(Pedido pedido, long id) {
        try {
            var field = Pedido.class.getDeclaredField("id");
            field.setAccessible(true);
            field.setLong(pedido, id);
        } catch (Exception e) {
            System.out.println("Erro ao restaurar ID do pedido: " + e.getMessage());
        }
    }

    public boolean listarPedidosVazios() {
        return pedidos.isEmpty();
    }

}
