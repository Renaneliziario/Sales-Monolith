package br.com.renan.sales.produtos.service;

import br.com.renan.sales.produtos.entity.Produto;
import br.com.renan.sales.produtos.repository.IProdutoRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class GestaoEstoque {

    private final IProdutoRepository produtoRepository;

    public GestaoEstoque(IProdutoRepository produtoRepository) {
        this.produtoRepository = produtoRepository;
    }

    @Transactional
    public void baixarEstoque(String codigo, Integer quantidade) {
        Produto produto = buscarPorCodigo(codigo);
        produto.baixarEstoque(quantidade);
    }

    @Transactional
    public void reporEstoque(String codigo, Integer quantidade) {
        Produto produto = buscarPorCodigo(codigo);
        produto.reporEstoque(quantidade);
    }

    private Produto buscarPorCodigo(String codigo) {
        return produtoRepository.findByCodigo(codigo)
                .orElseThrow(() ->
                        new EntityNotFoundException("Produto não encontrado: " + codigo)
                );
    }
}
