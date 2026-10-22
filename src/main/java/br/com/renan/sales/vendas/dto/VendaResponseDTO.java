package br.com.renan.sales.vendas.dto;

import br.com.renan.sales.vendas.entity.StatusVenda;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public record VendaResponseDTO(
        Long id,
        String codigo,
        Long clienteId,
        List<ItemVendaResponseDTO> itens,
        String enderecoEntrega,
        BigDecimal valorTotal,
        Instant dataVenda,
        StatusVenda status) {
}
