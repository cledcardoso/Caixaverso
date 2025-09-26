package util;

import model.Pedido;

public class NotificadorEmail {
    public static void notificarPagamento(Pedido pedido) {
        System.out.println("[Email] Pagamento confirmado para o pedido #" + pedido.getId());
    }

    public static void notificarEntrega(Pedido pedido) {
        System.out.println("[Email] Pedido #" + pedido.getId() + " foi entregue com sucesso.");
    }
}
