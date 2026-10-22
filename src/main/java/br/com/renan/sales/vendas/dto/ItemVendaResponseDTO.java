package br.com.renan.sales.vendas.dto;

import java.math.BigDecimal;

public record ItemVendaResponseDTO(
        Long id,
        Long produtoId,
        String codigoProduto,
        String nome,
        Integer quantidade,
        BigDecimal valorUnitario) {
}
