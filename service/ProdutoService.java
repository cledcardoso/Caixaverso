package service;

import model.Produto;

import java.io.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ProdutoService {
    private static final String FILE_NAME = "produtos.csv";
    private List<Produto> produtos = new ArrayList<>();

    public ProdutoService() {
        carregarProdutos();
    }

    public void cadastrarProduto(Produto produto) {
        produtos.add(produto);
        salvarProdutos();
    }

    public Optional<Produto> buscarPorId(Long id) {
        return produtos.stream()
                .filter(p -> p.getId().equals(id))
                .findFirst();
    }

    public void listarProdutos() {
        produtos.forEach(p ->
                System.out.println(p.getId() + " - " + p.getNome() + " | R$ " + p.getPrecoBase())
        );
    }

    private void salvarProdutos() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME))) {
            for (Produto p : produtos) {
                writer.write(p.getId() + "," + p.getNome() + "," + p.getDescricao() + "," + p.getPrecoBase());
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("Erro ao salvar produtos: " + e.getMessage());
        }
    }

    private void carregarProdutos() {
        File file = new File(FILE_NAME);
        if (!file.exists()) return;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] dados = line.split(",");
                if (dados.length == 4) {
                    Produto p = new Produto(
                            Long.parseLong(dados[0]),
                            dados[1],
                            dados[2],
                            new BigDecimal(dados[3])
                    );
                    produtos.add(p);
                }
            }
        } catch (IOException e) {
            System.out.println("Erro ao carregar produtos: " + e.getMessage());
        }
    }
}
