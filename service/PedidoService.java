package service;

import model.Cliente;
import model.ItemPedido;
import model.Pedido;
import model.Produto;
import java.math.BigDecimal;

import java.util.ArrayList;
import java.util.List;

public class PedidoService {
    private List<Pedido> pedidos = new ArrayList<>();
    private static long contadorPedidos = 1;

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
        System.out.println("Pedido pago.");
    }

    public void entregarPedido(Pedido pedido) {
        pedido.entregar();
        System.out.println("Pedido entregue.");
    }

    public void listarPedidos() {
        System.out.println("\n=== Lista de Pedidos ===");
        pedidos.forEach(p -> System.out.println("Pedido #" + p.getId() + " | Cliente: " + p.getCliente().getNome() + " | Status: " + p.getStatus()));
    }
}
