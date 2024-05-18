package br.com.fiap.mspedidos.service;

import br.com.fiap.mspedidos.model.ItemPedido;
import br.com.fiap.mspedidos.model.Pedido;
import br.com.fiap.mspedidos.repository.PedidoRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class PedidoService {

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    public Pedido criarPedido(Pedido pedido) {

        boolean produtosDisponiveis = verificarDisponibilidadeDeProdutos(pedido.getItemPedido());

        if (!produtosDisponiveis) {
            throw new NoSuchElementException("Um ou mais produtos não estão disponíveis");
        }

        double valorTotal = calcularValorTotal(pedido.getItemPedido());
        pedido.setValorTotal(valorTotal);

        atualizarEstoqueProdutos(pedido.getItemPedido());

        return pedidoRepository.save(pedido);
    }

    private void atualizarEstoqueProdutos(List<ItemPedido> itemPedidos) {
        for (ItemPedido itemPedido : itemPedidos) {
            Integer idProduto = itemPedido.getIdProduto();
            int quantidade = itemPedido.getQuantidade();

            restTemplate.put(
                    "http://localhost:8080/api/produtos/atualizar/estoque/{produtoId}/{quantidade}",
                    null,
                    idProduto,
                    quantidade
            );
        }
    }

    private double calcularValorTotal(List<ItemPedido> itemPedidos) {
        double valorTotal = 0.0;

        for (ItemPedido itemPedido : itemPedidos) {
            Integer idProduto = itemPedido.getIdProduto();
            int quantidade = itemPedido.getQuantidade();

            ResponseEntity<String> response = restTemplate.getForEntity(
                    "http://localhost:8080/api/produtos/{produtoId}",
                    String.class,
                    idProduto
            );

            if (response.getStatusCode() == HttpStatus.OK) {
                try {
                    JsonNode produtoJson = objectMapper.readTree(response.getBody());
                    double preco = produtoJson.get("preco").asDouble();
                    valorTotal += preco * quantidade;
                } catch (IOException e) {
                    // TODO tratar exceção
                }
            }
        }

        return valorTotal;
    }

    private boolean verificarDisponibilidadeDeProdutos(List<ItemPedido> itemPedidos) {
        for (ItemPedido itemPedido : itemPedidos) {
            Integer idProduto = itemPedido.getIdProduto();
            int quantidade = itemPedido.getQuantidade();

            ResponseEntity<String> response = restTemplate.getForEntity(
                    "http://localhost:8080/api/produtos/{produtoId}",
                    String.class,
                    idProduto
            );

            if (response.getStatusCode() == HttpStatus.NOT_FOUND) {
                throw new NoSuchElementException("Produto não encontrado");
            } else {
                try {
                    JsonNode produtoJson = objectMapper.readTree(response.getBody());
                    int quantidadeDeEstoque = produtoJson.get("quantidadeEstoque").asInt();

                    if (quantidadeDeEstoque < quantidade) {
                        return false;
                    }
                } catch (IOException e) {
                    //TODO tratar exceção
                }
            }
        }
        return true;
    }

    public List<Pedido> listarPedidos() {
        return pedidoRepository.findAll();
    }
}
