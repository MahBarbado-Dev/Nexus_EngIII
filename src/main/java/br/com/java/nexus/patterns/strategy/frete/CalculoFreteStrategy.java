package br.com.java.nexus.patterns.strategy.frete;

import br.com.java.nexus.model.carrinho.Carrinho;
import br.com.java.nexus.model.cliente.Endereco;

import java.math.BigDecimal;

/**
 * Padrão GoF: Strategy (Comportamental)
 * Define a interface comum para os diferentes algoritmos de cálculo de frete.
 */
public interface CalculoFreteStrategy {

    /**
     * Identificador da modalidade de frete calculada por esta estratégia.
     */
    TipoFrete getTipo();

    /**
     * Executa o algoritmo de cálculo do valor do frete.
     */
    BigDecimal calcular(Carrinho carrinho, Endereco endereco);

    /**
     * Retorna a estimativa de prazo de entrega em dias úteis.
     */
    int getPrazoEntregaDias(Endereco endereco);
}
