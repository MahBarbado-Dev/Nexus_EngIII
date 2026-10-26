package br.com.java.nexus.patterns.builder.pedido;

import br.com.java.nexus.model.carrinho.Carrinho;
import br.com.java.nexus.model.carrinho.ItemCarrinho;
import br.com.java.nexus.model.cliente.Cliente;
import br.com.java.nexus.model.cliente.Endereco;
import br.com.java.nexus.model.pagamento.Pagamento;
import br.com.java.nexus.model.pedido.ItemPedido;
import br.com.java.nexus.model.pedido.Pedido;
import br.com.java.nexus.model.pedido.enums.StatusPedido;
import br.com.java.nexus.service.exception.ValidacaoException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Padrão GoF: Builder (Criacional)
 * Fornece uma interface fluente para a construção passo-a-passo e validação consistente
 * do agregado complexo Pedido (com seus itens, pagamentos, endereços e valores fiscais).
 */
public class PedidoBuilder {

    private Cliente cliente;
    private Endereco enderecoEntrega;
    private Pagamento pagamento;
    private StatusPedido status = StatusPedido.EM_PROCESSAMENTO;
    private LocalDateTime dataCriacao = LocalDateTime.now();
    private BigDecimal valorFrete = BigDecimal.ZERO;
    private BigDecimal descontoPromocional = BigDecimal.ZERO;
    private String codigoCupomPromocional;
    private final List<ItemPedido> itens = new ArrayList<>();

    public static PedidoBuilder novoPedido() {
        return new PedidoBuilder();
    }

    public PedidoBuilder paraCliente(Cliente cliente) {
        this.cliente = cliente;
        return this;
    }

    public PedidoBuilder comEnderecoEntrega(Endereco enderecoEntrega) {
        this.enderecoEntrega = enderecoEntrega;
        return this;
    }

    public PedidoBuilder comPagamento(Pagamento pagamento) {
        this.pagamento = pagamento;
        return this;
    }

    public PedidoBuilder comStatus(StatusPedido status) {
        if (status != null) {
            this.status = status;
        }
        return this;
    }

    public PedidoBuilder comFrete(BigDecimal frete) {
        this.valorFrete = (frete != null) ? frete : BigDecimal.ZERO;
        return this;
    }

    public PedidoBuilder comDescontoPromocional(BigDecimal descontoPromocional) {
        this.descontoPromocional = (descontoPromocional != null) ? descontoPromocional : BigDecimal.ZERO;
        return this;
    }

    public PedidoBuilder comCupomPromocional(String codigoCupomPromocional) {
        if (codigoCupomPromocional != null && !codigoCupomPromocional.isBlank()) {
            this.codigoCupomPromocional = codigoCupomPromocional.trim();
        }
        return this;
    }

    public PedidoBuilder adicionarItem(ItemPedido item) {
        if (item != null) {
            this.itens.add(item);
        }
        return this;
    }

    public PedidoBuilder comItensDoCarrinho(Carrinho carrinho) {
        if (carrinho == null || carrinho.isVazio()) {
            throw new ValidacaoException("Não é possível gerar itens de pedido a partir de um carrinho vazio.");
        }

        for (ItemCarrinho itemCarrinho : carrinho.getItens()) {
            if (itemCarrinho == null || itemCarrinho.getProduto() == null) {
                throw new ValidacaoException("Existe um item inválido no carrinho.");
            }
            if (itemCarrinho.getQuantidade() == null || itemCarrinho.getQuantidade() <= 0) {
                throw new ValidacaoException("Existe um item com quantidade inválida no carrinho.");
            }
            if (itemCarrinho.getProduto().getPreco() == null || itemCarrinho.getProduto().getPreco().compareTo(BigDecimal.ZERO) <= 0) {
                throw new ValidacaoException("O produto " + itemCarrinho.getProduto().getTitulo() + " possui preço inválido.");
            }

            ItemPedido itemPedido = new ItemPedido(
                    itemCarrinho.getProduto(),
                    itemCarrinho.getQuantidade(),
                    itemCarrinho.getProduto().getPreco()
            );
            this.itens.add(itemPedido);
        }
        return this;
    }

    /**
     * Constrói o objeto Pedido, garantindo integridade e consistência interna.
     */
    public Pedido build() {
        validarIntegridade();

        Pedido pedido = new Pedido();
        pedido.setCliente(this.cliente);
        pedido.setEnderecoEntrega(this.enderecoEntrega);
        pedido.setPagamento(this.pagamento);
        pedido.setStatus(this.status);
        pedido.setDataCriacao(this.dataCriacao);
        pedido.setValorFrete(this.valorFrete);
        pedido.setDescontoPromocional(this.descontoPromocional);
        pedido.setCodigoCupomPromocional(this.codigoCupomPromocional);

        for (ItemPedido item : this.itens) {
            pedido.adicionarItem(item);
        }

        pedido.recalcularValores();
        return pedido;
    }

    private void validarIntegridade() {
        if (this.cliente == null) {
            throw new ValidacaoException("Cliente é obrigatório para a construção do pedido.");
        }
        if (this.enderecoEntrega == null) {
            throw new ValidacaoException("Endereço de entrega é obrigatório para a construção do pedido.");
        }
        if (this.pagamento == null) {
            throw new ValidacaoException("Pagamento é obrigatório para a construção do pedido.");
        }
        if (this.itens.isEmpty()) {
            throw new ValidacaoException("O pedido deve possuir ao menos um item.");
        }
    }
}
