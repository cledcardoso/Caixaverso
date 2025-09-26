package model;

import java.time.LocalDateTime;

public class Cliente {
    private String cpf;
    private String nome;
    private String email;
    private LocalDateTime dataCadastro;

    public Cliente(String cpf, String nome, String email) {
        if (cpf == null || cpf.isBlank()) {
            throw new IllegalArgumentException("CPF é obrigatório.");
        }
        this.cpf = cpf;
        this.nome = nome;
        this.email = email;
        this.dataCadastro = LocalDateTime.now();
    }

    public String getCpf() { return cpf; }
    public String getNome() { return nome; }
    public String getEmail() { return email; }
    public LocalDateTime getDataCadastro() { return dataCadastro; }

    public void setNome(String nome) { this.nome = nome; }
    public void setEmail(String email) { this.email = email; }
}
