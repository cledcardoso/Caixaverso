package service;

import model.Produto;

import java.io.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ProdutoService {
    private final List<Produto> produtos = new ArrayList<>();
    private final String caminhoArquivo;

    public ProdutoService(String caminhoArquivo) {
        this.caminhoArquivo = caminhoArquivo;
    }

    public void carregarProdutosDoArquivo() {
        File arquivo = new File(caminhoArquivo);
        if (!arquivo.exists()) return;

        try (BufferedReader br = new BufferedReader(new FileReader(arquivo))) {
            String linha;
            while ((linha = br.readLine()) != null) {
                String[] partes = linha.split(";");
                if (partes.length == 4) {
                    String nome = partes[1];
                    String descricao = partes[2];
                    BigDecimal preco = new BigDecimal(partes[3]);
                    produtos.add(new Produto(nome, descricao, preco));
                }
            }
        } catch (IOException e) {
            System.out.println("Erro ao carregar produtos: " + e.getMessage());
        }
    }

    public void cadastrarProduto(Produto produto) {
        if (buscarPorId(produto.getId()).isPresent()) {
            System.out.println("Produto com ID já cadastrado.");
            return;
        }
        produtos.add(produto);
        salvarProdutoNoArquivo(produto);
        System.out.println("Produto cadastrado com sucesso!");
    }

    public Optional<Produto> buscarPorId(Long id) {
        return produtos.stream()
                .filter(p -> p.getId() == id)
                .findFirst();
    }

    public void atualizarProduto(Long id, String novoNome, String novaDescricao, BigDecimal novoPreco) {
        Optional<Produto> produtoOpt = buscarPorId(id);
        if (produtoOpt.isPresent()) {
            Produto produto = produtoOpt.get();
            produto.setNome(novoNome);
            produto.setDescricao(novaDescricao);
            produto.setPrecoBase(novoPreco);
            salvarTodosProdutos();
            System.out.println("Produto atualizado com sucesso!");
        } else {
            System.out.println("Produto não encontrado.");
        }
    }

    public void listarProdutos() {
        System.out.println("\n=== Lista de Produtos ===");
        produtos.forEach(p -> System.out.println("ID: " + p.getId() + " | Nome: " + p.getNome() + " | Preço: R$" + p.getPrecoBase()));
    }

    private void salvarProdutoNoArquivo(Produto produto) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(caminhoArquivo, true))) {
            bw.write(produto.getId() + ";" + produto.getNome() + ";" + produto.getDescricao() + ";" + produto.getPrecoBase());
            bw.newLine();
        } catch (IOException e) {
            System.out.println("Erro ao salvar produto: " + e.getMessage());
        }
    }

    private void salvarTodosProdutos() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(caminhoArquivo))) {
            for (Produto produto : produtos) {
                bw.write(produto.getId() + ";" + produto.getNome() + ";" + produto.getDescricao() + ";" + produto.getPrecoBase());
                bw.newLine();
            }
        } catch (IOException e) {
            System.out.println("Erro ao atualizar produtos: " + e.getMessage());
        }
    }
}
