package util;

import model.Pedido;

public class NotificacaoEntrega implements Runnable {
    private final Pedido pedido;

    public NotificacaoEntrega(Pedido pedido) {
        this.pedido = pedido;
    }

    @Override
    public void run() {
        System.out.println("[Email] Pedido #" + pedido.getId() + " foi entregue. Cliente: " + pedido.getCliente().getEmail());
    }
}
