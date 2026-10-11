package br.com.renan.sales.clientes.repository;

import br.com.renan.sales.clientes.entity.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {

}