package br.com.renan.sales.produtos.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "produtos")
public class Produto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String codigo;

    @Column(nullable = false, length = 50)
    private String nome;

    @Column(nullable = false, length = 150)
    private String descricao;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal valor;

    @Column(nullable = false)
    private Integer quantidade;

    protected Produto() {
    }

    public Produto(String codigo, String nome, String descricao, BigDecimal valor, Integer quantidade) {
        this.codigo = codigo;
        this.nome = nome;
        this.descricao = descricao;
        this.valor = valor;
        this.quantidade = quantidade;
    }

    public Long getId() {
        return id;
    }

    public String getCodigo() {
        return codigo;
    }

    public String getNome() {
        return nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public Integer getQuantidade() {
        return quantidade;
    }

    public void atualizarDados(String codigo, String nome, String descricao,
                               BigDecimal valor, Integer quantidade){
        this.codigo = codigo;
        this.nome = nome;
        this.descricao = descricao;
        this.valor = valor;
        this.quantidade = quantidade;
    }

    public void baixarEstoque(Integer quantidadeBaixa) {
        if (quantidadeBaixa == null || quantidadeBaixa <= 0) {
            throw new IllegalArgumentException("A quantidade para baixa deve ser maior que zero");
        }

        if (this.quantidade < quantidadeBaixa) {
            throw new IllegalArgumentException("Estoque insuficiente para o produto: " + this.codigo);
        }

        this.quantidade = this.quantidade - quantidadeBaixa;
    }

    public void reporEstoque(Integer quantidadeReposicao) {
        if (quantidadeReposicao == null || quantidadeReposicao <= 0) {
            throw new IllegalArgumentException("A quantidade para reposição deve ser maior que zero");
        }

        this.quantidade = this.quantidade + quantidadeReposicao;
    }
}
