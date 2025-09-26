package service;

import model.Produto;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ProdutoService {
    private List<Produto> produtos = new ArrayList<>();

    public void cadastrarProduto(Produto produto) {
        if (buscarPorId(produto.getId()).isPresent()) {
            System.out.println("Produto com ID já cadastrado.");
            return;
        }
        produtos.add(produto);
        System.out.println("Produto cadastrado com sucesso!");
    }

    public Optional<Produto> buscarPorId(Long id) {
        return produtos.stream()
                .filter(p -> p.getId().equals(id))
                .findFirst();
    }

    public void atualizarProduto(Long id, String novoNome, String novaDescricao, BigDecimal novoPreco) {
        Optional<Produto> produtoOpt = buscarPorId(id);
        if (produtoOpt.isPresent()) {
            Produto produto = produtoOpt.get();
            produto.setNome(novoNome);
            produto.setDescricao(novaDescricao);
            produto.setPrecoBase(novoPreco);
            System.out.println("Produto atualizado com sucesso!");
        } else {
            System.out.println("Produto não encontrado.");
        }
    }

    public void listarProdutos() {
        System.out.println("\n=== Lista de Produtos ===");
        produtos.forEach(p -> System.out.println("ID: " + p.getId() + " | Nome: " + p.getNome() + " | Preço: R$" + p.getPrecoBase()));
    }
}
