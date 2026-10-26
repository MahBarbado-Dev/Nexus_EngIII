package br.com.java.nexus.patterns.strategy.frete;

import br.com.java.nexus.model.carrinho.Carrinho;
import br.com.java.nexus.model.cliente.Endereco;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Padrão GoF: Strategy (Estratégia Concreta)
 * Aplica frete grátis caso o subtotal do carrinho atinja o valor mínimo promocional (ex: R$ 350,00).
 */
@Component
public class FreteGratisPromocionalStrategy implements CalculoFreteStrategy {

    private static final BigDecimal VALOR_MINIMO_FRETE_GRATIS = new BigDecimal("350.00");
    private final FretePadraoStrategy fretePadraoStrategy;

    public FreteGratisPromocionalStrategy(FretePadraoStrategy fretePadraoStrategy) {
        this.fretePadraoStrategy = fretePadraoStrategy;
    }

    @Override
    public TipoFrete getTipo() {
        return TipoFrete.PROMOCIONAL;
    }

    @Override
    public BigDecimal calcular(Carrinho carrinho, Endereco endereco) {
        if (carrinho != null && carrinho.getSubtotal() != null
                && carrinho.getSubtotal().compareTo(VALOR_MINIMO_FRETE_GRATIS) >= 0) {
            return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        }
        return fretePadraoStrategy.calcular(carrinho, endereco);
    }

    @Override
    public int getPrazoEntregaDias(Endereco endereco) {
        return fretePadraoStrategy.getPrazoEntregaDias(endereco);
    }
}
