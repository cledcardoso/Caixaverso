package model;

public class Cliente {
    private static long contador = 1;
    private final long id;
    private String cpf;
    private String nome;
    private String email;

    public Cliente(String cpf, String nome, String email) {
        this.id = contador++;
        this.cpf = cpf;
        this.nome = nome;
        this.email = email;
    }

    public long getId() {
        return id;
    }

    public String getCpf() {
        return cpf;
    }

    public String getNome() {
        return nome;
    }

    public String getEmail() {
        return email;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public static void atualizarContador(long ultimoId) {
        if (ultimoId >= contador) {
            contador = ultimoId + 1;
        }
    }
}
