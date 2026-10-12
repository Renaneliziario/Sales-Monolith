package br.com.renan.sales.clientes.dto;

public record ClienteResponseDTO(
        Long id,
        String nome,
        String email,
        String cpf
) {
}