package br.com.technexus.model;

import java.util.ArrayList;
import java.util.List;

public class Loja {

    private List<Produto> catalogo = new ArrayList<>();

    public void cadastrar(Produto p) {
        this.catalogo.add(p);
    }

    // Exercicio 3: proibido usar for e if -> resolvido com filter + toList
    public List<Produto> buscarPorCategoria(String catDesejada) {
        return this.catalogo.stream()
                .filter(p -> p.getCategoria().equalsIgnoreCase(catDesejada))
                .toList();
    }

    // Exercicio 4: extrai o preco de cada produto e soma tudo
    public double calcularPatrimonioTotal() {
        return this.catalogo.stream()
                .mapToDouble(Produto::getPreco)
                .sum();
    }

    // Exercicio 5: pipeline completo -> filtra a categoria, extrai o preco e soma
    public double calcularTotalPorCategoria(String catDesejada) {
        return this.catalogo.stream()
                .filter(p -> p.getCategoria().equalsIgnoreCase(catDesejada))
                .mapToDouble(Produto::getPreco)
                .sum();
    }
}
