package com.tatiane.centralpedidos.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Builder;
import lombok.ToString;
import lombok.EqualsAndHashCode;


@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = "pedido") // evita recursão
@EqualsAndHashCode(exclude = "pedido")
public class Produto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private Integer idProduto;

    private String nomeProduto;

    private Integer quantidadeProduto;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "id_pedido")
    @JsonBackReference
    private Pedido pedido;
}
