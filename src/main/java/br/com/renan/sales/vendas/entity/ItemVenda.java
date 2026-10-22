package br.com.renan.sales.vendas.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.math.BigDecimal;

@Entity
@Table(name = "itens_venda")
public class ItemVenda {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "venda_id", nullable = false)
    private Venda venda;

    @Column(nullable = false)
    private Long produtoId;

    @Column(nullable = false, length = 50)
    private String codigoProduto;

    @Column(nullable = false, length = 100)
    private String nome;

    @Column(nullable = false)
    private Integer quantidade;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal valorUnitario;

    protected ItemVenda() {
    }

    ItemVenda(Venda venda, Long produtoId, String codigoProduto, String nome,
              Integer quantidade, BigDecimal valorUnitario) {
        this.venda = venda;
        this.produtoId = produtoId;
        this.codigoProduto = codigoProduto;
        this.nome = nome;
        this.quantidade = quantidade;
        this.valorUnitario = valorUnitario;
    }

    public Long getId() {
        return id;
    }

    public Venda getVenda() {
        return venda;
    }

    public Long getProdutoId() {
        return produtoId;
    }

    public String getCodigoProduto() {
        return codigoProduto;
    }

    public String getNome() {
        return nome;
    }

    public Integer getQuantidade() {
        return quantidade;
    }

    public BigDecimal getValorUnitario() {
        return valorUnitario;
    }

    public BigDecimal calcularSubtotal() {
        return this.valorUnitario.multiply(BigDecimal.valueOf(this.quantidade));
    }
}
