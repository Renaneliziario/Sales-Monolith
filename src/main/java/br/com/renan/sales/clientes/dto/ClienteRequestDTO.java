package br.com.renan.sales.clientes.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record ClienteRequestDTO(
        @NotBlank
        @Size(min = 2, max = 100)
        String nome,

        @NotBlank
        @Pattern(regexp = "\\d{11}")
        String cpf,

        @NotBlank
        @Size(min = 10, max = 15)
        String tel,

        @NotBlank
        @Email
        @Size(max = 150)
        String email,

        @NotBlank
        @Size(max = 100)
        String endereco,

        @NotNull
        Integer numero,

        @NotBlank
        @Size(max = 50)
        String cidade,

        @NotBlank
        @Size(min = 2, max = 2)
        String estado
) {
}


