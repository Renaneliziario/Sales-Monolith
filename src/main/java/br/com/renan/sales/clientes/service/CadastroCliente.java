
package br.com.renan.sales.clientes.service;

import br.com.renan.sales.clientes.dto.ClienteRequestDTO;
import br.com.renan.sales.clientes.dto.ClienteResponseDTO;
import br.com.renan.sales.clientes.entity.Cliente;
import br.com.renan.sales.clientes.repository.ClienteRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CadastroCliente {

    private final ClienteRepository clienteRepository;

    public CadastroCliente(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    @Transactional
    public ClienteResponseDTO cadastrar(ClienteRequestDTO dto) {
        Cliente cliente = new Cliente(
                dto.nome(),
                dto.cpf(),
                dto.tel(),
                dto.email(),
                dto.endereco(),
                dto.numero(),
                dto.cidade(),
                dto.estado()
        );

        Cliente clienteSalvo = clienteRepository.save(cliente);

        return criarResposta(clienteSalvo);
    }

    @Transactional
    public ClienteResponseDTO atualizar(Long id, ClienteRequestDTO dto) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() ->
                        new EntityNotFoundException("Cliente não encontrado pelo id: " + id)
                );

        cliente.atualizarDados(
                dto.nome(),
                dto.tel(),
                dto.email(),
                dto.endereco(),
                dto.numero(),
                dto.cidade(),
                dto.estado()
        );

        return criarResposta(cliente);
    }

    @Transactional
    public void remover(Long id) {
        if (!clienteRepository.existsById(id)) {
            throw new EntityNotFoundException("Cliente não encontrado pelo id: " + id);
        }

        clienteRepository.deleteById(id);
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