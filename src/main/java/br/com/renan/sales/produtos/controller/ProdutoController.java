package br.com.renan.sales.produtos.controller;

import br.com.renan.sales.produtos.dto.ProdutoRequestDTO;
import br.com.renan.sales.produtos.dto.ProdutoResponseDTO;
import br.com.renan.sales.produtos.service.BuscaProduto;
import br.com.renan.sales.produtos.service.CadastroProduto;
import br.com.renan.sales.produtos.service.GestaoEstoque;
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
@RequestMapping("/produto")
public class ProdutoController {

    private final BuscaProduto buscaProduto;
    private final CadastroProduto cadastroProduto;
    private final GestaoEstoque gestaoEstoque;

    public ProdutoController(BuscaProduto buscaProduto,
                             CadastroProduto cadastroProduto,
                             GestaoEstoque gestaoEstoque) {
        this.buscaProduto = buscaProduto;
        this.cadastroProduto = cadastroProduto;
        this.gestaoEstoque = gestaoEstoque;
    }

    @GetMapping
    public ResponseEntity<Page<ProdutoResponseDTO>> buscar(Pageable pageable) {
        return ResponseEntity.ok(buscaProduto.buscar(pageable));
    }

    @PostMapping
    public ResponseEntity<ProdutoResponseDTO> cadastrar(@RequestBody @Valid ProdutoRequestDTO dto) {
        ProdutoResponseDTO resposta = cadastroProduto.cadastrar(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(resposta);
    }

    @GetMapping("/id/{id}")
    public ResponseEntity<ProdutoResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(buscaProduto.buscarPorId(id));
    }

    @GetMapping("/{codigo}")
    public ResponseEntity<ProdutoResponseDTO> buscarPorCodigo(@PathVariable String codigo) {
        return ResponseEntity.ok(buscaProduto.buscarPorCodigo(codigo));
    }

    @GetMapping("/isCadastrado/{id}")
    public ResponseEntity<Boolean> isCadastrado(@PathVariable Long id) {
        return ResponseEntity.ok(buscaProduto.isCadastrado(id));
    }

    @PutMapping
    public ResponseEntity<ProdutoResponseDTO> atualizar(@RequestBody @Valid ProdutoRequestDTO dto) {
        return ResponseEntity.ok(cadastroProduto.atualizar(dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remover(@PathVariable Long id) {
        cadastroProduto.remover(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{codigo}/estoque/baixa")
    public ResponseEntity<Void> baixarEstoque(@PathVariable String codigo,
                                              @RequestBody Integer quantidade) {
        gestaoEstoque.baixarEstoque(codigo, quantidade);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{codigo}/estoque/reposicao")
    public ResponseEntity<Void> reporEstoque(@PathVariable String codigo,
                                             @RequestBody Integer quantidade) {
        gestaoEstoque.reporEstoque(codigo, quantidade);
        return ResponseEntity.ok().build();
    }
}
