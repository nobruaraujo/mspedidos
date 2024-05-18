# :package: Microsserviço - Pedidos

## Introdução
Projeto desenvolvido com intuito de estudar Microsserviços.

Esta solução replica um sistema de e-commerce onde há um microsserviço de pedidos que se comunica com outro microsserviço chamado produtos.

> [!NOTE]
> Este microsserviço foi desenvolvido em conjunto com o projeto https://github.com/nobruaraujo/msproduto

> [!IMPORTANT]
> Para rodar esta aplicação, tenha o mongodb rodando no Docker.

## Arquitetura do projeto
01. Cliente cria um pedido no microsserviço de Gerenciamento de Pedidos

02. O microsserviço de Gerenciamento de Pedidos faz uma chamada ao microsserviço de Gerenciamento de Produtos para obter detalhes e verificar a disponibilidade dos produtos selecionados.

03. Se os produtos estão disponíveis, o pedido é criado e uma chamada é feita novamente ao microsserviço de Gerenciamento de Produtos para atualizar as quantidades de estoque.

04. O pedido é, então, processado.

![image](https://github.com/nobruaraujo/mspedidos/assets/49958155/68537751-892b-41d4-b92a-dfab87e8c00c)
