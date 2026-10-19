package br.com.renan.sales.produtos.dto;

import java.math.BigDecimal;

public record ProdutoResponseDTO(
        Long id,
        String codigo,
        String nome,
        String descricao,
        BigDecimal valor,
        Integer quantidade
) {
}