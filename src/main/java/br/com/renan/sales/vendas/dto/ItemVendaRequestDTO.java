package br.com.renan.sales.vendas.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ItemVendaRequestDTO(

        @NotBlank(message = "O código do produto é obrigatório")
        String codigoProduto,

        @NotNull(message = "A quantidade é obrigatória")
        @Min(value = 1, message = "A quantidade deve ser pelo menos 1")
        Integer quantidade) {
}
