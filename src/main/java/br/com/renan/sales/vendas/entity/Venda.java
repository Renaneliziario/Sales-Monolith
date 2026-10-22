package br.com.renan.sales.vendas.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Entity
@Table(name = "vendas")
public class Venda {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String codigo;

    @Column(nullable = false)
    private Long clienteId;

    @OneToMany(mappedBy = "venda", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemVenda> itens = new ArrayList<>();

    @Column(nullable = false, length = 200)
    private String enderecoEntrega;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal valorTotal;

    @Column(nullable = false)
    private Instant dataVenda;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatusVenda status;

    protected Venda() {
    }

    public Venda(String codigo, Long clienteId, String enderecoEntrega) {
        this.codigo = codigo;
        this.clienteId = clienteId;
        this.enderecoEntrega = enderecoEntrega;
        this.status = StatusVenda.INICIADA;
        this.dataVenda = Instant.now();
        this.valorTotal = BigDecimal.ZERO;
    }

    public Long getId() {
        return id;
    }

    public String getCodigo() {
        return codigo;
    }

    public Long getClienteId() {
        return clienteId;
    }

    public List<ItemVenda> getItens() {
        return Collections.unmodifiableList(itens);
    }

    public String getEnderecoEntrega() {
        return enderecoEntrega;
    }

    public BigDecimal getValorTotal() {
        return valorTotal;
    }

    public Instant getDataVenda() {
        return dataVenda;
    }

    public StatusVenda getStatus() {
        return status;
    }

    public void adicionarItem(Long produtoId, String codigoProduto, String nome,
                              Integer quantidade, BigDecimal valorUnitario) {
        if (this.status != StatusVenda.INICIADA) {
            throw new IllegalStateException(
                    "Não é possível adicionar produtos a uma venda com status: " + this.status);
        }

        if (quantidade == null || quantidade < 1) {
            throw new IllegalArgumentException("A quantidade do item deve ser pelo menos 1");
        }

        ItemVenda item = new ItemVenda(this, produtoId, codigoProduto, nome, quantidade, valorUnitario);
        this.itens.add(item);
        recalcularValorTotal();
    }

    public void finalizar() {
        if (this.status != StatusVenda.INICIADA) {
            throw new IllegalStateException(
                    "Não é possível finalizar uma venda com status: " + this.status);
        }

        this.status = StatusVenda.CONCLUIDA;
    }

    public void cancelar() {
        if (this.status == StatusVenda.CANCELADA) {
            throw new IllegalStateException("A venda já está cancelada");
        }

        this.status = StatusVenda.CANCELADA;
    }

    private void recalcularValorTotal() {
        BigDecimal total = BigDecimal.ZERO;

        for (ItemVenda item : this.itens) {
            total = total.add(item.calcularSubtotal());
        }

        this.valorTotal = total;
    }
}
