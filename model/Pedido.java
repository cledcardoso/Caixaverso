package model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Pedido {
    public enum Status {
        ABERTO, AGUARDANDO_PAGAMENTO, PAGO, FINALIZADO
    }

    private static long contador = 1;
    private long id;
    private Cliente cliente;
    private LocalDateTime dataCriacao;
    private Status status;
    private List<ItemPedido> itens = new ArrayList<>();

    public Pedido(Cliente cliente) {
        this.id = contador++;
        this.cliente = cliente;
        this.dataCriacao = LocalDateTime.now();
        this.status = Status.ABERTO;
    }

    public static void atualizarContador(long maiorId) {
        contador = maiorId + 1;
    }

    public void adicionarItem(ItemPedido item) {
        if (status != Status.ABERTO) {
            System.out.println("Pedido não está aberto para alterações.");
            return;
        }
        if (item.getQuantidade() <= 0 || item.getPrecoVenda().compareTo(BigDecimal.ZERO) <= 0) {
            System.out.println("Quantidade e preço devem ser maiores que zero.");
            return;
        }
        itens.add(item);
    }

    public void removerItem(ItemPedido item) {
        if (status != Status.ABERTO) {
            System.out.println("Pedido não está aberto para alterações.");
            return;
        }
        itens.remove(item);
    }

    public void finalizar() {
        if (status != Status.ABERTO) {
            System.out.println("Pedido já foi finalizado ou está em outro estado.");
            return;
        }
        if (itens.isEmpty()) {
            System.out.println("Erro: pedido não pode ser finalizado sem itens.");
            return;
        }
        if (getValorTotal().compareTo(BigDecimal.ZERO) <= 0) {
            System.out.println("Erro: valor total do pedido deve ser maior que zero.");
            return;
        }
        this.status = Status.AGUARDANDO_PAGAMENTO;
    }

    public void pagar() {
        if (status != Status.AGUARDANDO_PAGAMENTO) {
            System.out.println("Erro: pagamento só pode ser feito em pedidos aguardando pagamento.");
            return;
        }
        this.status = Status.PAGO;
    }

    public void entregar() {
        if (status != Status.PAGO) {
            System.out.println("Erro: pedido só pode ser entregue após pagamento.");
            return;
        }
        this.status = Status.FINALIZADO;
    }

    public BigDecimal getValorTotal() {
        return itens.stream()
                .map(item -> item.getPrecoVenda().multiply(BigDecimal.valueOf(item.getQuantidade())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    // Getters
    public long getId() { return id; }
    public Cliente getCliente() { return cliente; }
    public LocalDateTime getDataCriacao() { return dataCriacao; }
    public Status getStatus() { return status; }
    public List<ItemPedido> getItens() { return itens; }
}
