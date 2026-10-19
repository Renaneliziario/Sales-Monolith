package br.com.renan.sales.produtos.entity;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ProdutoTest {

    private Produto criarProduto(int quantidadeInicial) {
        return new Produto(
                "TEC-001",
                "Teclado",
                "Teclado mecanico ABNT2",
                new BigDecimal("250.00"),
                quantidadeInicial
        );
    }

    @Test
    void deveCriarProdutoComOsDadosInformados() {
        Produto produto = criarProduto(10);

        assertEquals("TEC-001", produto.getCodigo());
        assertEquals("Teclado", produto.getNome());
        assertEquals("Teclado mecanico ABNT2", produto.getDescricao());
        assertEquals(new BigDecimal("250.00"), produto.getValor());
        assertEquals(10, produto.getQuantidade().intValue());
    }

    @Test
    void deveBaixarEstoqueQuandoHaQuantidadeSuficiente() {
        Produto produto = criarProduto(10);

        produto.baixarEstoque(4);

        assertEquals(6, produto.getQuantidade().intValue());
    }

    @Test
    void devePermitirBaixarTodoOEstoqueDisponivel() {
        Produto produto = criarProduto(10);

        produto.baixarEstoque(10);

        assertEquals(0, produto.getQuantidade().intValue());
    }

    @Test
    void naoDeveBaixarAlemDoEstoqueDisponivel() {
        Produto produto = criarProduto(10);

        IllegalArgumentException erro = assertThrows(
                IllegalArgumentException.class,
                () -> produto.baixarEstoque(11)
        );

        assertEquals("Estoque insuficiente para o produto: TEC-001", erro.getMessage());
        assertEquals(10, produto.getQuantidade().intValue());
    }

    @Test
    void naoDeveBaixarQuantidadeZeroOuNegativa() {
        Produto produto = criarProduto(10);

        assertThrows(IllegalArgumentException.class, () -> produto.baixarEstoque(0));
        assertThrows(IllegalArgumentException.class, () -> produto.baixarEstoque(-5));
        assertThrows(IllegalArgumentException.class, () -> produto.baixarEstoque(null));

        assertEquals(10, produto.getQuantidade().intValue());
    }

    @Test
    void deveReporEstoque() {
        Produto produto = criarProduto(10);

        produto.reporEstoque(5);

        assertEquals(15, produto.getQuantidade().intValue());
    }

    @Test
    void naoDeveReporQuantidadeZeroOuNegativa() {
        Produto produto = criarProduto(10);

        assertThrows(IllegalArgumentException.class, () -> produto.reporEstoque(0));
        assertThrows(IllegalArgumentException.class, () -> produto.reporEstoque(-5));
        assertThrows(IllegalArgumentException.class, () -> produto.reporEstoque(null));

        assertEquals(10, produto.getQuantidade().intValue());
    }

    @Test
    void deveAtualizarTodosOsDadosEditaveis() {
        Produto produto = criarProduto(10);

        produto.atualizarDados(
                "TEC-002",
                "Teclado Pro",
                "Teclado mecanico sem fio",
                new BigDecimal("399.90"),
                25
        );

        assertEquals("TEC-002", produto.getCodigo());
        assertEquals("Teclado Pro", produto.getNome());
        assertEquals("Teclado mecanico sem fio", produto.getDescricao());
        assertEquals(new BigDecimal("399.90"), produto.getValor());
        assertEquals(25, produto.getQuantidade().intValue());
    }
}
