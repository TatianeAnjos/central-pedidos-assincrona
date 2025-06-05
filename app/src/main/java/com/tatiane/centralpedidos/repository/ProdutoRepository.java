package com.tatiane.centralpedidos.repository;

import com.tatiane.centralpedidos.entities.Produto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProdutoRepository extends JpaRepository<Produto, Long> {

    @Query(
            value = "SELECT * FROM PRODUTO u WHERE u.id_pedido = 1",
            nativeQuery = true)
    List<Produto> findByIdPedido(Long idPedido);
}
