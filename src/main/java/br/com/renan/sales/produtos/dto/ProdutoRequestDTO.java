package br.com.renan.sales.produtos.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

public record ProdutoRequestDTO(

		Long id,

		@NotBlank @Size(min = 1, max = 50) String codigo,

		@NotBlank @Size(min = 1, max = 50) String nome,

		@NotBlank @Size(min = 1, max = 150) String descricao,

		@NotNull @PositiveOrZero BigDecimal valor,

		@NotNull @Min(0) Integer quantidade) {
}