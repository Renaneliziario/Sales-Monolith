package br.com.renan.sales.vendas.service;

import br.com.renan.sales.vendas.dto.VendaResponseDTO;
import br.com.renan.sales.vendas.entity.Venda;
import br.com.renan.sales.vendas.repository.VendaRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class BuscaVenda {

    private final VendaRepository vendaRepository;
    private final VendaMapper vendaMapper;

    public BuscaVenda(VendaRepository vendaRepository, VendaMapper vendaMapper) {
        this.vendaRepository = vendaRepository;
        this.vendaMapper = vendaMapper;
    }

    public Page<VendaResponseDTO> buscar(Pageable pageable) {
        Page<Venda> vendas = vendaRepository.findAll(pageable);
        return vendas.map(vendaMapper::criarResposta);
    }

    public VendaResponseDTO buscarPorId(Long id) {
        return vendaMapper.criarResposta(buscarEntidadePorId(id));
    }

    public VendaResponseDTO buscarPorCodigo(String codigo) {
        Venda venda = vendaRepository.findByCodigo(codigo)
                .orElseThrow(() ->
                        new EntityNotFoundException("Venda não encontrada pelo código: " + codigo)
                );

        return vendaMapper.criarResposta(venda);
    }

    public boolean isCadastrado(Long id) {
        return vendaRepository.existsById(id);
    }

    Venda buscarEntidadePorId(Long id) {
        return vendaRepository.findById(id)
                .orElseThrow(() ->
                        new EntityNotFoundException("Venda não encontrada pelo id: " + id)
                );
    }
}
