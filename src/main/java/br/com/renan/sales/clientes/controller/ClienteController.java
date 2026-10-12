package br.com.renan.sales.clientes.controller;

import br.com.renan.sales.clientes.dto.ClienteRequestDTO;
import br.com.renan.sales.clientes.dto.ClienteResponseDTO;
import br.com.renan.sales.clientes.service.BuscaCliente;
import br.com.renan.sales.clientes.service.CadastroCliente;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/clientes")
public class ClienteController {

    private final BuscaCliente buscaCliente;
    private final CadastroCliente cadastroCliente;

    public ClienteController(BuscaCliente buscaCliente, CadastroCliente cadastroCliente) {
        this.buscaCliente = buscaCliente;
        this.cadastroCliente = cadastroCliente;
    }

    @GetMapping
    public ResponseEntity<Page<ClienteResponseDTO>> buscar(Pageable pageable) {
        return ResponseEntity.ok(buscaCliente.buscar(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClienteResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(buscaCliente.buscarPorId(id));
    }

    @GetMapping("/is-cadastrado/{id}")
    public ResponseEntity<Boolean> isCadastrado(@PathVariable Long id) {
        return ResponseEntity.ok(buscaCliente.isCadastrado(id));
    }

    @PostMapping
    public ResponseEntity<ClienteResponseDTO> cadastrar(
            @RequestBody @Valid ClienteRequestDTO dto) {
        ClienteResponseDTO resposta = cadastroCliente.cadastrar(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(resposta);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ClienteResponseDTO> atualizar(
            @PathVariable Long id,
            @RequestBody @Valid ClienteRequestDTO dto) {
        return ResponseEntity.ok(cadastroCliente.atualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remover(@PathVariable Long id) {
        cadastroCliente.remover(id);
        return ResponseEntity.noContent().build();
    }
}
