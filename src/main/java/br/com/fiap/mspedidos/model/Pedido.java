package br.com.fiap.mspedidos.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@Document("pedido")
public class Pedido {

    @Id
    private String id;
    private String nomeCliente;
    private List<ItemPedido> itemPedido;
    private double valorTotal;

}
