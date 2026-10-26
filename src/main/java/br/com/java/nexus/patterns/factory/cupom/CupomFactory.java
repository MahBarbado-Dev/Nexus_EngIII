package br.com.java.nexus.patterns.factory.cupom;

import br.com.java.nexus.model.cliente.Cliente;
import br.com.java.nexus.model.venda.Cupom;
import br.com.java.nexus.model.venda.enums.TipoCupom;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Padrão GoF: Factory (Criacional)
 * Encapsula e centraliza a criação e inicialização consistente dos diferentes tipos de cupons (Troca vs Promocional),
 * garantindo a aplicação padronizada de regras como formato de código, prazo de expiração e limites de uso.
 */
@Component
public class CupomFactory {

    private static final int VALIDADE_MESES_TROCA = 6;
    private static final int VALIDADE_PADRAO_DIAS_PROMO = 30;

    /**
     * Fabrica um cupom de troca gerado a partir do troco/excedente de uma compra.
     */
    public Cupom criarCupomTrocaExcedente(Cliente cliente, BigDecimal valor) {
        Cupom cupom = instanciarCupomTrocaBase(cliente, valor);
        cupom.setDescricao("Crédito gerado por valor excedente de cupons em uma compra.");
        return cupom;
    }

    /**
     * Fabrica um cupom de troca gerado a partir da aprovação de uma devolução.
     */
    public Cupom criarCupomTrocaDevolucao(Cliente cliente, Long devolucaoId, BigDecimal valor) {
        Cupom cupom = instanciarCupomTrocaBase(cliente, valor);
        cupom.setDescricao("Cupom de troca referente à devolução #" + devolucaoId);
        return cupom;
    }

    /**
     * Fabrica um cupom promocional para campanhas de marketing ou descontos pontuais.
     */
    public Cupom criarCupomPromocional(String codigo, BigDecimal valor, LocalDateTime validade, Integer maximoUsos, String descricao) {
        Cupom cupom = new Cupom();
        cupom.setCodigo(codigo != null ? codigo.trim().toUpperCase() : gerarCodigoPromocional());
        cupom.setTipo(TipoCupom.PROMOCIONAL);
        cupom.setValor(valor);
        cupom.setCliente(null); // Cupons promocionais são públicos por padrão
        cupom.setAtivo(true);
        cupom.setQuantidadeUsos(0);
        cupom.setMaximoUsos(maximoUsos != null ? maximoUsos : 100);
        cupom.setDataValidade(validade != null ? validade : LocalDateTime.now().plusDays(VALIDADE_PADRAO_DIAS_PROMO));
        cupom.setDescricao(descricao != null ? descricao : "Cupom promocional");
        return cupom;
    }

    private Cupom instanciarCupomTrocaBase(Cliente cliente, BigDecimal valor) {
        Cupom cupom = new Cupom();
        cupom.setCodigo(gerarCodigoCupomTroca());
        cupom.setTipo(TipoCupom.TROCA);
        cupom.setValor(valor);
        cupom.setCliente(cliente);
        cupom.setAtivo(true);
        cupom.setQuantidadeUsos(0);
        cupom.setMaximoUsos(1);
        cupom.setDataValidade(LocalDateTime.now().plusMonths(VALIDADE_MESES_TROCA));
        return cupom;
    }

    private String gerarCodigoCupomTroca() {
        return "TROCA-" + UUID.randomUUID().toString().replace("-", "").substring(0, 10).toUpperCase();
    }

    private String gerarCodigoPromocional() {
        return "PROMO-" + UUID.randomUUID().toString().replace("-", "").substring(0, 8).toUpperCase();
    }
}
