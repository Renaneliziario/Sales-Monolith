package br.com.renan.sales.vendas.repository;

import br.com.renan.sales.vendas.entity.Venda;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VendaRepository extends JpaRepository<Venda, Long> {

    Optional<Venda> findByCodigo(String codigo);
}
