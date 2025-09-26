package util;

import model.Pedido;

public class NotificacaoPagamento implements Runnable {
    private final Pedido pedido;

    public NotificacaoPagamento(Pedido pedido) {
        this.pedido = pedido;
    }

    @Override
    public void run() {
        System.out.println("[Email] Pedido #" + pedido.getId() + " foi pago. Cliente: " + pedido.getCliente().getEmail());
    }
}
