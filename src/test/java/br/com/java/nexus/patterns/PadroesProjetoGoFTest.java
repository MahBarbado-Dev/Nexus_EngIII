package br.com.java.nexus.patterns;

import br.com.java.nexus.model.carrinho.Carrinho;
import br.com.java.nexus.model.carrinho.ItemCarrinho;
import br.com.java.nexus.model.cliente.Cliente;
import br.com.java.nexus.model.cliente.Endereco;
import br.com.java.nexus.model.cliente.enums.UF;
import br.com.java.nexus.model.pagamento.Pagamento;
import br.com.java.nexus.model.pedido.Pedido;
import br.com.java.nexus.model.pedido.enums.StatusPedido;
import br.com.java.nexus.model.produto.Produto;
import br.com.java.nexus.model.venda.Cupom;
import br.com.java.nexus.model.venda.enums.TipoCupom;
import br.com.java.nexus.patterns.builder.pedido.PedidoBuilder;
import br.com.java.nexus.patterns.factory.cupom.CupomFactory;
import br.com.java.nexus.patterns.strategy.frete.*;
import br.com.java.nexus.service.carrinho.FreteService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class PadroesProjetoGoFTest {

    // =========================================================================
    // 1. TESTES DO PADRÃO STRATEGY (Cálculo de Frete)
    // =========================================================================

    @Test
    @DisplayName("Strategy: Deve calcular frete padrão, expresso e promocional corretamente")
    void testStrategyFrete() {
        FretePadraoStrategy padrao = new FretePadraoStrategy();
        FreteExpressoStrategy expresso = new FreteExpressoStrategy(padrao);
        FreteGratisPromocionalStrategy gratis = new FreteGratisPromocionalStrategy(padrao);

        FreteService freteService = new FreteService(List.of(padrao, expresso, gratis));

        Endereco enderecoSP = new Endereco();
        enderecoSP.setUf(UF.SP);

        Produto produto = new Produto();
        produto.setId(1L);
        produto.setTitulo("Notebook Nexus");
        produto.setPreco(new BigDecimal("200.00"));
        produto.setPeso(2.0); // 2 kg

        Carrinho carrinho = new Carrinho();
        carrinho.adicionarItem(produto, 1);

        // Tarifa SP: 10.00 + (2kg * 2.00) = 14.00
        BigDecimal valorPadrao = freteService.calcularFrete(carrinho, enderecoSP, TipoFrete.PADRAO);
        assertEquals(new BigDecimal("14.00"), valorPadrao);

        // Expresso: 14.00 * 1.60 = 22.40
        BigDecimal valorExpresso = freteService.calcularFrete(carrinho, enderecoSP, TipoFrete.EXPRESSO);
        assertEquals(new BigDecimal("22.40"), valorExpresso);

        // Promocional abaixo de R$ 350 -> frete padrão (14.00)
        BigDecimal valorPromoMenor = freteService.calcularFrete(carrinho, enderecoSP, TipoFrete.PROMOCIONAL);
        assertEquals(new BigDecimal("14.00"), valorPromoMenor);

        // Adiciona mais um item para totalizar R$ 400.00 -> frete grátis (0.00)
        carrinho.adicionarItem(produto, 1);
        BigDecimal valorPromoMaior = freteService.calcularFrete(carrinho, enderecoSP, TipoFrete.PROMOCIONAL);
        assertEquals(new BigDecimal("0.00"), valorPromoMaior);
    }

    // =========================================================================
    // 2. TESTES DO PADRÃO FACTORY (Criação de Cupons)
    // =========================================================================

    @Test
    @DisplayName("Factory: Deve fabricar cupons de troca e promocionais com dados consistentes")
    void testFactoryCupom() {
        CupomFactory factory = new CupomFactory();
        Cliente cliente = new Cliente();
        cliente.setNome("Maria Silva");

        // Cupom Troca por Excedente
        Cupom cupomExcedente = factory.criarCupomTrocaExcedente(cliente, new BigDecimal("45.50"));
        assertNotNull(cupomExcedente.getCodigo());
        assertTrue(cupomExcedente.getCodigo().startsWith("TROCA-"));
        assertEquals(TipoCupom.TROCA, cupomExcedente.getTipo());
        assertEquals(new BigDecimal("45.50"), cupomExcedente.getValor());
        assertEquals(cliente, cupomExcedente.getCliente());
        assertTrue(cupomExcedente.isValido());

        // Cupom Troca por Devolução
        Cupom cupomDevolucao = factory.criarCupomTrocaDevolucao(cliente, 123L, new BigDecimal("100.00"));
        assertTrue(cupomDevolucao.getDescricao().contains("#123"));
        assertEquals(TipoCupom.TROCA, cupomDevolucao.getTipo());

        // Cupom Promocional
        Cupom cupomPromo = factory.criarCupomPromocional("DESCONTO10", new BigDecimal("10.00"), null, 50, "Campanha");
        assertEquals("DESCONTO10", cupomPromo.getCodigo());
        assertEquals(TipoCupom.PROMOCIONAL, cupomPromo.getTipo());
        assertNull(cupomPromo.getCliente()); // Cupom público
    }

    // =========================================================================
    // 3. TESTES DO PADRÃO BUILDER (Construção de Pedido)
    // =========================================================================

    @Test
    @DisplayName("Builder: Deve construir e validar o agregado Pedido de forma fluente")
    void testBuilderPedido() {
        Cliente cliente = new Cliente();
        cliente.setNome("João da Silva");

        Endereco endereco = new Endereco();
        endereco.setLogradouro("Rua das Flores");
        endereco.setUf(UF.SP);

        Pagamento pagamento = new Pagamento();
        pagamento.setValorOriginal(new BigDecimal("150.00"));

        Produto produto = new Produto();
        produto.setId(2L);
        produto.setTitulo("Teclado Mecânico");
        produto.setPreco(new BigDecimal("150.00"));

        Carrinho carrinho = new Carrinho();
        carrinho.adicionarItem(produto, 1);

        Pedido pedido = PedidoBuilder.novoPedido()
                .paraCliente(cliente)
                .comEnderecoEntrega(endereco)
                .comPagamento(pagamento)
                .comFrete(new BigDecimal("15.00"))
                .comDescontoPromocional(new BigDecimal("10.00"))
                .comCupomPromocional("PROMO10")
                .comItensDoCarrinho(carrinho)
                .build();

        assertNotNull(pedido);
        assertEquals(cliente, pedido.getCliente());
        assertEquals(endereco, pedido.getEnderecoEntrega());
        assertEquals(pagamento, pedido.getPagamento());
        assertEquals(StatusPedido.EM_PROCESSAMENTO, pedido.getStatus());
        assertEquals(1, pedido.getItens().size());
        assertEquals(new BigDecimal("150.00"), pedido.getSubtotal());
        assertEquals(new BigDecimal("15.00"), pedido.getValorFrete());
        assertEquals(new BigDecimal("10.00"), pedido.getDescontoPromocional());
        // Total esperado: 150.00 (subtotal) - 10.00 (desc) + 15.00 (frete) = 155.00
        assertEquals(new BigDecimal("155.00"), pedido.getValorTotal());
    }
}
