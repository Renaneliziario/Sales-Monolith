package br.com.renan.sales.vendas.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public record VendaRequestDTO(

        @NotBlank(message = "O código da venda é obrigatório")
        @Size(max = 50)
        String codigo,

        @NotNull(message = "O cliente é obrigatório")
        Long clienteId,

        @NotEmpty(message = "A venda deve ter ao menos um item")
        @Valid
        List<ItemVendaRequestDTO> itens,

        @NotBlank(message = "O endereço de entrega é obrigatório")
        @Size(max = 200)
        String enderecoEntrega) {
}
