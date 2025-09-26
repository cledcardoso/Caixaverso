package util;

import model.Pedido;

public class NotificacaoCriacao implements Runnable {
    private final Pedido pedido;

    public NotificacaoCriacao(Pedido pedido) {
        this.pedido = pedido;
    }

    @Override
    public void run() {
        System.out.println("[Email] Pedido #" + pedido.getId() + " criado para " + pedido.getCliente().getEmail());
    }
}
