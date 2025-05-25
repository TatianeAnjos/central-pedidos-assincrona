package com.tatiane.centralpedidos.entities;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Builder;
import lombok.ToString;
import lombok.EqualsAndHashCode;

import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = "listaProdutos") // evita recursão
@EqualsAndHashCode(exclude = "listaProdutos")
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idPedido;

    private String nomeCliente;

    @OneToMany(mappedBy = "pedido", fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<Produto> listaProdutos;
}
