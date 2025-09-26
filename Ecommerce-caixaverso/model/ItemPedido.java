package model;

import java.math.BigDecimal;

public class ItemPedido {
    private Produto produto;
    private int quantidade;
    private BigDecimal precoUnitario;

    public ItemPedido(Produto produto, int quantidade, BigDecimal precoUnitario) {
        this.produto = produto;
        this.quantidade = quantidade;
        this.precoUnitario = precoUnitario;
    }

    public BigDecimal calcularSubtotal() {
        return this.precoUnitario.multiply(BigDecimal.valueOf(this.quantidade));
    }

    public Produto getProduto() { return produto; }
    public int getQuantidade() { return quantidade; }
    public BigDecimal getPrecoUnitario() { return precoUnitario; }
    public void setProduto(Produto produto) { this.produto = produto; }
    public void setQuantidade(int quantidade) { this.quantidade = quantidade; }
    public void setPrecoUnitario(BigDecimal precoUnitario) { this.precoUnitario = precoUnitario; }
}