package model;

import java.math.BigDecimal;

public class Produto {
    private static long contador = 1;
    private final long id;
    private String nome;
    private String descricao;
    private BigDecimal precoBase;

    public Produto(String nome, String descricao, BigDecimal precoBase) {
        this.id = contador++;
        this.nome = nome;
        this.descricao = descricao;
        this.precoBase = precoBase;
    }

    public long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public BigDecimal getPrecoBase() {
        return precoBase;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public void setPrecoBase(BigDecimal precoBase) {
        this.precoBase = precoBase;
    }

    public static void atualizarContador(long ultimoId) {
        if (ultimoId >= contador) {
            contador = ultimoId + 1;
        }
    }
}
