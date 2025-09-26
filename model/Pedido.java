package model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Pedido {
    public enum Status {
        ABERTO, FINALIZADO, PAGO, ENTREGUE
    }

    private Long id;
    private Cliente cliente;
    private LocalDateTime dataCriacao;
    private Status status;
    private List<ItemPedido> itens;

    public Pedido(Long id, Cliente cliente) {
        this.id = id;
        this.cliente = cliente;
        this.dataCriacao = LocalDateTime.now();
        this.status = Status.ABERTO;
        this.itens = new ArrayList<>();
    }

    public Long getId() { return id; }
    public Cliente getCliente() { return cliente; }
    public LocalDateTime getDataCriacao() { return dataCriacao; }
    public Status getStatus() { return status; }
    public List<ItemPedido> getItens() { return itens; }

    public void adicionarItem(ItemPedido item) {
        itens.add(item);
    }

    public void removerItem(ItemPedido item) {
        itens.remove(item);
    }

    public BigDecimal getValorTotal() {
        return itens.stream()
                .map(ItemPedido::getValorTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public void finalizar() {
        if (status == Status.ABERTO) {
            status = Status.FINALIZADO;
        }
    }

    public void pagar() {
        if (status == Status.FINALIZADO) {
            status = Status.PAGO;
        }
    }

    public void entregar() {
        if (status == Status.PAGO) {
            status = Status.ENTREGUE;
        }
    }
}
