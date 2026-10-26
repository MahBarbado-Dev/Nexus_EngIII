package br.com.java.nexus.patterns.strategy.frete;

import br.com.java.nexus.model.carrinho.Carrinho;
import br.com.java.nexus.model.cliente.Endereco;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Padrão GoF: Strategy (Estratégia Concreta)
 * Implementa o cálculo de entrega expressa (acréscimo de 60% sobre a tarifa base e prazo prioritário de 1 a 2 dias).
 */
@Component
public class FreteExpressoStrategy implements CalculoFreteStrategy {

    private final FretePadraoStrategy fretePadraoStrategy;
    private static final BigDecimal MULTIPLICADOR_EXPRESSO = new BigDecimal("1.60");

    public FreteExpressoStrategy(FretePadraoStrategy fretePadraoStrategy) {
        this.fretePadraoStrategy = fretePadraoStrategy;
    }

    @Override
    public TipoFrete getTipo() {
        return TipoFrete.EXPRESSO;
    }

    @Override
    public BigDecimal calcular(Carrinho carrinho, Endereco endereco) {
        BigDecimal freteBase = fretePadraoStrategy.calcular(carrinho, endereco);
        return freteBase.multiply(MULTIPLICADOR_EXPRESSO).setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    public int getPrazoEntregaDias(Endereco endereco) {
        return 2;
    }
}
