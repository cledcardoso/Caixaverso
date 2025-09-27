package service;

import model.*;

import java.io.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PedidoService {
    private final List<Pedido> pedidos = new ArrayList<>();
    private final String caminhoArquivo;
    private final ClienteService clienteService;
    private final ProdutoService produtoService;

    public PedidoService(ClienteService clienteService, ProdutoService produtoService, String caminhoArquivo) {
    this.clienteService = clienteService;
    this.produtoService = produtoService;
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
                if (partes.length >= 8) {
                    long id = Long.parseLong(partes[0]);
                    String cpf = partes[1];
                    Pedido.Status status = Pedido.Status.valueOf(partes[2]);
                    LocalDateTime dataCriacao = LocalDateTime.parse(partes[3]);

                    Cliente cliente = clienteService.buscarPorCpf(cpf).orElse(null);
                    if (cliente == null) continue;

                    Pedido pedido = new Pedido(cliente);
                    restaurarId(pedido, id);
                    pedido.setStatus(status);
                    pedido.setDataCriacao(dataCriacao);

                    if (!partes[4].isEmpty()) pedido.setDataFinalizacao(LocalDateTime.parse(partes[4]));
                    if (!partes[5].isEmpty()) pedido.setDataPagamento(LocalDateTime.parse(partes[5]));
                    if (!partes[6].isEmpty()) pedido.setDataEntrega(LocalDateTime.parse(partes[6]));

                    if (partes.length >= 8 && !partes[7].isEmpty()) {
                        String[] itens = partes[7].split("\\|");
                        for (String itemStr : itens) {
                            String[] itemPartes = itemStr.split(",");
                            if (itemPartes.length == 3) {
                                long idProduto = Long.parseLong(itemPartes[0]);
                                int qtd = Integer.parseInt(itemPartes[1]);
                                BigDecimal preco = new BigDecimal(itemPartes[2]);

                                Produto produto = produtoService.buscarPorId(idProduto).orElse(null);
                                if (produto != null) {
                                    pedido.adicionarItem(new ItemPedido(produto, qtd, preco));
                                }
                            }
                        }
                    }

                    pedidos.add(pedido);
                    if (id > maiorId) maiorId = id;
                }
            }

            Pedido.atualizarContador(maiorId);
        } catch (IOException | NumberFormatException | DateTimeParseException e) {
            System.out.println("Erro ao carregar pedidos: " + e.getMessage());
        }
    }

    public List<Pedido> carregarPedidosDoArquivoSimples(ClienteService clienteService, ProdutoService produtoService) {
        List<Pedido> pedidosCarregados = new ArrayList<>();
        File arquivo = new File(caminhoArquivo);
        if (!arquivo.exists()) return pedidosCarregados;

        try (BufferedReader br = new BufferedReader(new FileReader(arquivo))) {
            String linha;
            long maiorId = 0;

            while ((linha = br.readLine()) != null) {
                String[] partes = linha.split(";");
                if (partes.length >= 8) {
                    long id = Long.parseLong(partes[0]);
                    String cpf = partes[1];
                    Pedido.Status status = Pedido.Status.valueOf(partes[2]);
                    LocalDateTime dataCriacao = LocalDateTime.parse(partes[3]);

                    Cliente cliente = clienteService.buscarPorCpf(cpf).orElse(null);
                    if (cliente == null) continue;

                    Pedido pedido = new Pedido(cliente);
                    restaurarId(pedido, id);
                    pedido.setStatus(status);
                    pedido.setDataCriacao(dataCriacao);

                    if (!partes[4].isEmpty()) pedido.setDataFinalizacao(LocalDateTime.parse(partes[4]));
                    if (!partes[5].isEmpty()) pedido.setDataPagamento(LocalDateTime.parse(partes[5]));
                    if (!partes[6].isEmpty()) pedido.setDataEntrega(LocalDateTime.parse(partes[6]));

                    if (!partes[7].isEmpty()) {
                        String[] itens = partes[7].split("\\|");
                        for (String itemStr : itens) {
                            String[] itemPartes = itemStr.split(",");
                            if (itemPartes.length == 3) {
                                long idProduto = Long.parseLong(itemPartes[0]);
                                int qtd = Integer.parseInt(itemPartes[1]);
                                BigDecimal preco = new BigDecimal(itemPartes[2]);

                                Produto produto = produtoService.buscarPorId(idProduto).orElse(null);
                                if (produto != null) {
                                    pedido.adicionarItem(new ItemPedido(produto, qtd, preco));
                                }
                            }
                        }
                    }

                    pedidosCarregados.add(pedido);
                    if (id > maiorId) maiorId = id;
                }
            }

            Pedido.atualizarContador(maiorId);
        } catch (IOException | NumberFormatException | DateTimeParseException e) {
            System.out.println("Erro ao carregar pedidos: " + e.getMessage());
        }

        return pedidosCarregados;
    }


    public Pedido criarPedido(Cliente cliente) {
        Pedido pedido = new Pedido(cliente);
        pedidos.add(pedido);
        return pedido;
    }

    public void salvarPedido(Pedido pedido) {
        pedidos.add(pedido); // adiciona à lista interna
        salvarPedidoNoArquivo(pedido, clienteService, produtoService); // persiste no CSV
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

    public boolean finalizarPedido(Pedido pedido) {
        if (pedido.getStatus() != Pedido.Status.ABERTO) {
            System.out.println("Pedido já foi finalizado ou está em outro estado.");
            return false;
        }

        if (pedido.getItens().isEmpty()) {
            System.out.println("Erro: pedido não pode ser finalizado sem itens.");
            return false;
        }

        if (pedido.getValorTotal().compareTo(BigDecimal.ZERO) <= 0) {
            System.out.println("Erro: valor total do pedido deve ser maior que zero.");
            return false;
        }

        pedido.setStatus(Pedido.Status.AGUARDANDO_PAGAMENTO);
        pedido.setDataFinalizacao(LocalDateTime.now()); //
        salvarPedidoNoArquivo(pedido, clienteService, produtoService);
        return true;
    }

    public boolean pagarPedido(Pedido pedido) {
        if (pedido.getStatus() != Pedido.Status.AGUARDANDO_PAGAMENTO) {
            System.out.println("Erro: pagamento só pode ser feito em pedidos aguardando pagamento.");
            return false;
        }

        pedido.setStatus(Pedido.Status.PAGO);
        pedido.setDataPagamento(LocalDateTime.now()); //
        salvarPedidoNoArquivo(pedido, clienteService, produtoService);
        return true;
    }


    public boolean entregarPedido(Pedido pedido) {
        if (pedido.getStatus() != Pedido.Status.PAGO) {
            System.out.println("Erro: pedido só pode ser entregue após pagamento.");
            return false;
        }

        pedido.setStatus(Pedido.Status.FINALIZADO);
        pedido.setDataEntrega(LocalDateTime.now()); // ✅ registra data de entrega
        salvarPedidoNoArquivo(pedido, clienteService, produtoService);
        return true;
    }


    private void salvarPedidoNoArquivo(Pedido pedido, ClienteService clienteService, ProdutoService produtoService) {
        List<Pedido> pedidosExistentes = carregarPedidosDoArquivoSimples(clienteService, produtoService);

        // Remove qualquer pedido com o mesmo ID
        pedidosExistentes.removeIf(p -> p.getId() == pedido.getId());

        // Adiciona o pedido atualizado
        pedidosExistentes.add(pedido);

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(caminhoArquivo))) {
            for (Pedido p : pedidosExistentes) {
                StringBuilder linha = new StringBuilder();
                linha.append(p.getId()).append(";")
                    .append(p.getCliente().getCpf()).append(";")
                    .append(p.getStatus()).append(";")
                    .append(p.getDataCriacao()).append(";")
                    .append(p.getDataFinalizacao() != null ? p.getDataFinalizacao() : "").append(";")
                    .append(p.getDataPagamento() != null ? p.getDataPagamento() : "").append(";")
                    .append(p.getDataEntrega() != null ? p.getDataEntrega() : "").append(";");

                List<String> itensStr = new ArrayList<>();
                for (ItemPedido item : p.getItens()) {
                    itensStr.add(item.getProduto().getId() + "," + item.getQuantidade() + "," + item.getPrecoVenda());
                }
                linha.append(String.join("|", itensStr));

                bw.write(linha.toString());
                bw.newLine();
            }
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
