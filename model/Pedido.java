package model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Pedido {
    public enum Status { ABERTO, FINALIZADO, PAGO, ENTREGUE }

    private static long contador = 1;
    private final long id;
    private final Cliente cliente;
    private final LocalDateTime dataCriacao;
    private Status status;
    private final List<ItemPedido> itens = new ArrayList<>();

    public Pedido(Cliente cliente) {
        this.id = contador++;
        this.cliente = cliente;
        this.dataCriacao = LocalDateTime.now();
        this.status = Status.ABERTO;
    }

    public Pedido(long id, Cliente cliente) {
        this.id = id;
        this.cliente = cliente;
        this.dataCriacao = LocalDateTime.now();
        this.status = Status.ABERTO;
    }

    public long getId() {
        return id;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public LocalDateTime getDataCriacao() {
        return dataCriacao;
    }

    public Status getStatus() {
        return status;
    }

    public List<ItemPedido> getItens() {
        return itens;
    }

    public void adicionarItem(ItemPedido item) {
        itens.add(item);
    }

    public void removerItem(ItemPedido item) {
        itens.remove(item);
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public void finalizar() {
        this.status = Status.FINALIZADO;
    }

    public void pagar() {
        this.status = Status.PAGO;
    }

    public void entregar() {
        this.status = Status.ENTREGUE;
    }

    public double getValorTotal() {
        return itens.stream()
                .mapToDouble(i -> i.getPrecoVenda().doubleValue() * i.getQuantidade())
                .sum();
    }

    public static void atualizarContador(long ultimoId) {
        if (ultimoId >= contador) {
            contador = ultimoId + 1;
        }
    }
}
