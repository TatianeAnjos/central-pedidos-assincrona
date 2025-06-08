package com.tatiane.centralpedidos.repository;

import com.tatiane.centralpedidos.entities.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long> {
    @Query(
            value = "SELECT * FROM Pedido  u WHERE u.email_cliente = ?",
            nativeQuery = true)
    List<Pedido> findByEmail(String emailCliente);
}
