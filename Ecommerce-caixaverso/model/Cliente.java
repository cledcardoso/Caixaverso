package model;

import java.time.LocalDateTime;

public class Cliente {
    private String nome;
    private String email;
    private String cpf;
    private LocalDateTime dataCadastro;

    public Cliente(String nome, String email, String cpf) {
        if (cpf == null || cpf.isBlank()) {
            throw new IllegalArgumentException("CPF é obrigatório.");
        }
        this.nome = nome;
        this.email = email;
        this.cpf = cpf;
        this.dataCadastro = LocalDateTime.now();
    }

    public String getNome() { return nome; }
    public String getEmail() { return email; }
    public String getCpf() { return cpf; }
    public LocalDateTime getDataCadastro() { return dataCadastro; }

    public void setNome(String nome) { this.nome = nome; }
    public void setEmail(String email) { this.email = email; }
}
