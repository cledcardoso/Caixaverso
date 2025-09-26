package model;

import model.enums.StatusPagamento;
import model.enums.StatusPedido;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Pedido {
    public enum Status {
        ABERTO, AGUARDANDO_PAGAMENTO, PAGO, FINALIZADO
    }

    private Integer id;
    private static Integer contador = 1;
    private Cliente cliente;
    private List<ItemPedido> itens;
    StatusPedido statusPedido;
    StatusPagamento statusPagamento;
    private LocalDateTime dataCriacao;
    private BigDecimal valorTotal;

    public Pedido(Cliente cliente) {
        this.id = contador;
        contador++;
        this.cliente = cliente;
        this.itens = new ArrayList<>();
        this.statusPedido = StatusPedido.ABERTO;
        this.dataCriacao = LocalDateTime.now();
        this.valorTotal = BigDecimal.ZERO;
    }

    public Pedido(Integer id, Cliente cliente) {
        this.id = id;
        this.cliente = cliente;
        this.itens = new ArrayList<>();
    }

    public void adicionarItem(ItemPedido item) {
        if (statusPedido != StatusPedido.ABERTO) throw new IllegalStateException("Pedido não está aberto.");
        itens.add(item);
        recalcularTotal();
    }

    public void removerItem(ItemPedido item) {
        if (statusPedido != StatusPedido.ABERTO) throw new IllegalStateException("Pedido não está aberto.");
        itens.remove(item);
        recalcularTotal();
    }

    public void finalizar() {
        if (itens.isEmpty() || valorTotal.compareTo(BigDecimal.ZERO) <= 0)
            throw new IllegalStateException("Pedido inválido para finalização.");
        statusPedido = StatusPedido.AGUARDANDO_PAGAMENTO;
    }

    public void pagar() {
        if (statusPedido != StatusPedido.AGUARDANDO_PAGAMENTO)
            throw new IllegalStateException("Pedido não está aguardando pagamento.");
        statusPedido = StatusPedido.PAGO;
    }

    public void entregar() {
        if (statusPedido != StatusPedido.PAGO)
            throw new IllegalStateException("Pedido não está pago.");
        statusPedido = StatusPedido.FINALIZADO;
    }

    private void recalcularTotal() {
        valorTotal = itens.stream()
                .map(ItemPedido::calcularSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public Integer getId() { return id; }
    public Cliente getCliente() { return cliente; }
    public List<ItemPedido> getItens() { return itens; }
    public StatusPedido getStatus() { return statusPedido; }
    public LocalDateTime getDataCriacao() { return dataCriacao; }
    public BigDecimal getValorTotal() { return valorTotal; }
    public void setStatus(StatusPedido statusPedido) { this.statusPedido = statusPedido; }
    public void setValorTotal(BigDecimal valorTotal) { this.valorTotal = valorTotal; }
}