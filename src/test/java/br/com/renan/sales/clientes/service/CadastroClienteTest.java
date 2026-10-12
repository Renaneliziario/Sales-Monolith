package br.com.renan.sales.clientes.service;

import br.com.renan.sales.clientes.dto.ClienteRequestDTO;
import br.com.renan.sales.clientes.dto.ClienteResponseDTO;
import br.com.renan.sales.clientes.entity.Cliente;
import br.com.renan.sales.clientes.repository.ClienteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CadastroClienteTest {

    @Mock
    private ClienteRepository clienteRepository;

    private CadastroCliente cadastroCliente;

    @BeforeEach
    void preparar() {
        cadastroCliente = new CadastroCliente(clienteRepository);
    }

    @Test
    void atualizarNaoDeveAlterarCpf() {
        Cliente cliente = new Cliente(
                "Cliente Teste",
                "12345678901",
                "11999999999",
                "cliente.teste@example.com",
                "Rua das Flores",
                10,
                "São Paulo",
                "SP"
        );

        ClienteRequestDTO novosDados = new ClienteRequestDTO(
                "Cliente Atualizado",
                "99999999999",
                "11888888888",
                "cliente.atualizado@example.com",
                "Rua Nova",
                20,
                "São Paulo",
                "SP"
        );

        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));

        ClienteResponseDTO resposta = cadastroCliente.atualizar(1L, novosDados);

        assertEquals("12345678901", cliente.getCpf());
        assertEquals("12345678901", resposta.cpf());
        assertEquals("Cliente Atualizado", cliente.getNome());
        assertEquals("cliente.atualizado@example.com", cliente.getEmail());
    }
}
