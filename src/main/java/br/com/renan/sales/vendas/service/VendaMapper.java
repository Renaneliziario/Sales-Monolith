package br.com.renan.sales.vendas.service;

import br.com.renan.sales.vendas.dto.ItemVendaResponseDTO;
import br.com.renan.sales.vendas.dto.VendaResponseDTO;
import br.com.renan.sales.vendas.entity.ItemVenda;
import br.com.renan.sales.vendas.entity.Venda;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class VendaMapper {

    public VendaResponseDTO criarResposta(Venda venda) {
        List<ItemVendaResponseDTO> itens = new ArrayList<>();

        for (ItemVenda item : venda.getItens()) {
            itens.add(new ItemVendaResponseDTO(
                    item.getId(),
                    item.getProdutoId(),
                    item.getCodigoProduto(),
                    item.getNome(),
                    item.getQuantidade(),
                    item.getValorUnitario()
            ));
        }

        return new VendaResponseDTO(
                venda.getId(),
                venda.getCodigo(),
                venda.getClienteId(),
                itens,
                venda.getEnderecoEntrega(),
                venda.getValorTotal(),
                venda.getDataVenda(),
                venda.getStatus()
        );
    }
}
