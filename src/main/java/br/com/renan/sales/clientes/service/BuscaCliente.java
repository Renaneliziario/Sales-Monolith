package br.com.renan.sales.clientes.service;

import br.com.renan.sales.clientes.dto.ClienteResponseDTO;
import br.com.renan.sales.clientes.entity.Cliente;
import br.com.renan.sales.clientes.repository.ClienteRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class BuscaCliente {

    private final ClienteRepository clienteRepository;

    public BuscaCliente(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    public Page<ClienteResponseDTO> buscar(Pageable pageable) {
        Page<Cliente> clientes = clienteRepository.findAll(pageable);
        return clientes.map(this::criarResposta);
    }

    public ClienteResponseDTO buscarPorId(Long id) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() ->
                        new EntityNotFoundException("Cliente não encontrado pelo id: " + id)
                );

        return criarResposta(cliente);
    }

    public boolean isCadastrado(Long id) {
        return clienteRepository.existsById(id);
    }

    private ClienteResponseDTO criarResposta(Cliente cliente) {
        return new ClienteResponseDTO(
                cliente.getId(),
                cliente.getNome(),
                cliente.getEmail(),
                cliente.getCpf()
        );
    }
}