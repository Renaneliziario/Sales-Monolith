package br.com.renan.sales.vendas.service;

import br.com.renan.sales.clientes.service.BuscaCliente;
import br.com.renan.sales.produtos.dto.ProdutoResponseDTO;
import br.com.renan.sales.produtos.service.BuscaProduto;
import br.com.renan.sales.produtos.service.GestaoEstoque;
import br.com.renan.sales.vendas.dto.ItemVendaRequestDTO;
import br.com.renan.sales.vendas.dto.VendaRequestDTO;
import br.com.renan.sales.vendas.dto.VendaResponseDTO;
import br.com.renan.sales.vendas.entity.ItemVenda;
import br.com.renan.sales.vendas.entity.Venda;
import br.com.renan.sales.vendas.repository.VendaRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CadastroVenda {

    private final VendaRepository vendaRepository;
    private final VendaMapper vendaMapper;
    private final BuscaCliente buscaCliente;
    private final BuscaProduto buscaProduto;
    private final GestaoEstoque gestaoEstoque;

    public CadastroVenda(VendaRepository vendaRepository,
                         VendaMapper vendaMapper,
                         BuscaCliente buscaCliente,
                         BuscaProduto buscaProduto,
                         GestaoEstoque gestaoEstoque) {
        this.vendaRepository = vendaRepository;
        this.vendaMapper = vendaMapper;
        this.buscaCliente = buscaCliente;
        this.buscaProduto = buscaProduto;
        this.gestaoEstoque = gestaoEstoque;
    }

    @Transactional
    public VendaResponseDTO cadastrar(VendaRequestDTO dto) {
        if (!buscaCliente.isCadastrado(dto.clienteId())) {
            throw new EntityNotFoundException("Cliente não encontrado: " + dto.clienteId());
        }

        Venda venda = new Venda(dto.codigo(), dto.clienteId(), dto.enderecoEntrega());

        for (ItemVendaRequestDTO itemDTO : dto.itens()) {
            adicionarItemNaVenda(venda, itemDTO.codigoProduto(), itemDTO.quantidade());
        }

        Venda vendaSalva = vendaRepository.save(venda);

        return vendaMapper.criarResposta(vendaSalva);
    }

    @Transactional
    public VendaResponseDTO adicionarProduto(Long id, String codigoProduto, Integer quantidade) {
        Venda venda = buscarPorId(id);

        adicionarItemNaVenda(venda, codigoProduto, quantidade);

        return vendaMapper.criarResposta(venda);
    }

    @Transactional
    public VendaResponseDTO finalizar(Long id) {
        Venda venda = buscarPorId(id);

        venda.finalizar();

        return vendaMapper.criarResposta(venda);
    }

    @Transactional
    public VendaResponseDTO cancelar(Long id) {
        Venda venda = buscarPorId(id);

        for (ItemVenda item : venda.getItens()) {
            gestaoEstoque.reporEstoque(item.getCodigoProduto(), item.getQuantidade());
        }

        venda.cancelar();

        return vendaMapper.criarResposta(venda);
    }

    private void adicionarItemNaVenda(Venda venda, String codigoProduto, Integer quantidade) {
        ProdutoResponseDTO produto = buscaProduto.buscarPorCodigo(codigoProduto);

        venda.adicionarItem(
                produto.id(),
                produto.codigo(),
                produto.nome(),
                quantidade,
                produto.valor()
        );

        gestaoEstoque.baixarEstoque(codigoProduto, quantidade);
    }

    private Venda buscarPorId(Long id) {
        return vendaRepository.findById(id)
                .orElseThrow(() ->
                        new EntityNotFoundException("Venda não encontrada pelo id: " + id)
                );
    }
}
