package br.com.renan.sales.produtos.service;

import br.com.renan.sales.produtos.dto.ProdutoResponseDTO;
import br.com.renan.sales.produtos.entity.Produto;
import br.com.renan.sales.produtos.repository.IProdutoRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class BuscaProduto {

    private final IProdutoRepository produtoRepository;

    public BuscaProduto(IProdutoRepository produtoRepository) {
        this.produtoRepository = produtoRepository;
    }

    public Page<ProdutoResponseDTO> buscar(Pageable pageable) {
        Page<Produto> produtos = produtoRepository.findAll(pageable);
        return produtos.map(this::criarResposta);
    }

    public ProdutoResponseDTO buscarPorId(Long id) {
        Produto produto = produtoRepository.findById(id)
                .orElseThrow(() ->
                        new EntityNotFoundException("Produto não encontrado pelo id: " + id)
                );

        return criarResposta(produto);
    }

    public ProdutoResponseDTO buscarPorCodigo(String codigo) {
        Produto produto = produtoRepository.findByCodigo(codigo)
                .orElseThrow(() ->
                        new EntityNotFoundException("Produto não encontrado pelo código: " + codigo)
                );

        return criarResposta(produto);
    }

    public boolean isCadastrado(Long id) {
        return produtoRepository.existsById(id);
    }

    private ProdutoResponseDTO criarResposta(Produto produto) {
        return new ProdutoResponseDTO(
                produto.getId(),
                produto.getCodigo(),
                produto.getNome(),
                produto.getDescricao(),
                produto.getValor(),
                produto.getQuantidade()
        );
    }
}
