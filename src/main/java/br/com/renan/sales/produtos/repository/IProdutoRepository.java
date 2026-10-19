package br.com.renan.sales.produtos.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.renan.sales.produtos.entity.Produto;

public interface IProdutoRepository extends JpaRepository<Produto, Long>{
	
	Optional<Produto> findByCodigo(String codigo);

}
