package model;

import java.math.BigDecimal;

public class ItemPedido {
    private Produto produto;
    private int quantidade;
    private BigDecimal precoVenda;

    public ItemPedido(Produto produto, int quantidade, BigDecimal precoVenda) {
        this.produto = produto;
        this.quantidade = quantidade;
        this.precoVenda = precoVenda;
    }

    public Produto getProduto() { return produto; }
    public int getQuantidade() { return quantidade; }
    public BigDecimal getPrecoVenda() { return precoVenda; }

    public void setQuantidade(int quantidade) { this.quantidade = quantidade; }

    public BigDecimal getValorTotal() {
        return precoVenda.multiply(BigDecimal.valueOf(quantidade));
    }
}
    