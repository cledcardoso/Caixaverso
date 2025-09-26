package service;

import model.ItemPedido;
import model.Pedido;
import model.Produto;

import java.util.ArrayList;
import java.util.List;

public class PedidoService {
    private List<Pedido> pedidos = new ArrayList<>();
    private static long contadorPedidos = 1;

    public Pedido criarPedido(model.Cliente cliente) {
        Pedido pedido = new Pedido(contadorPedidos++, cliente);
        pedidos.add(pedido);
        return pedido;
    }

    public void adicionarItem(Pedido pedido, Produto produto, int quantidade) {
        if (pedido.getStatus() != Pedido.Status.ABERTO) {
            System.out.println("Não é possível adicionar itens. Pedido não está aberto.");
            return;
        }
        ItemPedido item = new ItemPedido(produto, quantidade, produto.getPrecoBase());
        pedido.adicionarItem(item);
    }

    public void removerItem(Pedido pedido, Long idProduto) {
        ItemPedido item = pedido.getItens().stream()
                .filter(i -> i.getProduto().getId().equals(idProduto))
                .findFirst()
                .orElse(null);

        if (item != null) {
            pedido.removerItem(item);
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
        } else {
            System.out.println("Item não encontrado ou pedido não está aberto.");
        }
    }

    public void finalizarPedido(Pedido pedido) {
        pedido.finalizar();
    }

    public void pagarPedido(Pedido pedido) {
        pedido.pagar();
    }

    public void entregarPedido(Pedido pedido) {
        pedido.entregar();
    }
}
