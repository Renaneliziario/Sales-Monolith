package br.com.renan.sales.produtos.service;

import br.com.renan.sales.produtos.dto.ProdutoRequestDTO;
import br.com.renan.sales.produtos.dto.ProdutoResponseDTO;
import br.com.renan.sales.produtos.entity.Produto;
import br.com.renan.sales.produtos.repository.IProdutoRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CadastroProduto {

    private final IProdutoRepository produtoRepository;

    public CadastroProduto(IProdutoRepository produtoRepository) {
        this.produtoRepository = produtoRepository;
    }

    @Transactional
    public ProdutoResponseDTO cadastrar(ProdutoRequestDTO dto) {
        Produto produto = new Produto(
                dto.codigo(),
                dto.nome(),
                dto.descricao(),
                dto.valor(),
                dto.quantidade()
        );

        Produto produtoSalvo = produtoRepository.save(produto);

        return criarResposta(produtoSalvo);
    }

    @Transactional
    public ProdutoResponseDTO atualizar(ProdutoRequestDTO dto) {
        if (dto.id() == null) {
            throw new IllegalArgumentException("ID não pode ser nulo para atualização");
        }

        Produto produto = produtoRepository.findById(dto.id())
                .orElseThrow(() ->
                        new EntityNotFoundException("Produto não encontrado pelo id: " + dto.id())
                );

        produto.atualizarDados(
                dto.codigo(),
                dto.nome(),
                dto.descricao(),
                dto.valor(),
                dto.quantidade()
        );

        return criarResposta(produto);
    }

    @Transactional
    public void remover(Long id) {
        if (!produtoRepository.existsById(id)) {
            throw new EntityNotFoundException("Produto não encontrado pelo id: " + id);
        }

        produtoRepository.deleteById(id);
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
