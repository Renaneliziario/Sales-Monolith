package br.com.renan.sales.vendas.controller;

import br.com.renan.sales.vendas.dto.VendaRequestDTO;
import br.com.renan.sales.vendas.dto.VendaResponseDTO;
import br.com.renan.sales.vendas.service.BuscaVenda;
import br.com.renan.sales.vendas.service.CadastroVenda;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/venda")
public class VendaController {

    private final BuscaVenda buscaVenda;
    private final CadastroVenda cadastroVenda;

    public VendaController(BuscaVenda buscaVenda, CadastroVenda cadastroVenda) {
        this.buscaVenda = buscaVenda;
        this.cadastroVenda = cadastroVenda;
    }

    @GetMapping
    public ResponseEntity<Page<VendaResponseDTO>> buscar(Pageable pageable) {
        return ResponseEntity.ok(buscaVenda.buscar(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<VendaResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(buscaVenda.buscarPorId(id));
    }

    @PostMapping
    public ResponseEntity<VendaResponseDTO> cadastrar(@RequestBody @Valid VendaRequestDTO dto) {
        return ResponseEntity.ok(cadastroVenda.cadastrar(dto));
    }

    @PutMapping("/{id}/{codigoProduto}/{quantidade}/addProduto")
    public ResponseEntity<VendaResponseDTO> adicionarProduto(@PathVariable Long id,
                                                             @PathVariable String codigoProduto,
                                                             @PathVariable Integer quantidade) {
        return ResponseEntity.ok(cadastroVenda.adicionarProduto(id, codigoProduto, quantidade));
    }

    @PutMapping("/{id}/finalizar")
    public ResponseEntity<VendaResponseDTO> finalizar(@PathVariable Long id) {
        return ResponseEntity.ok(cadastroVenda.finalizar(id));
    }

    @PutMapping("/{id}/cancelar")
    public ResponseEntity<VendaResponseDTO> cancelar(@PathVariable Long id) {
        return ResponseEntity.ok(cadastroVenda.cancelar(id));
    }
}
