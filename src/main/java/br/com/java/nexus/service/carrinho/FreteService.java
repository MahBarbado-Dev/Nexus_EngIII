package br.com.java.nexus.service.carrinho;

import br.com.java.nexus.model.carrinho.Carrinho;
import br.com.java.nexus.model.cliente.Endereco;
import br.com.java.nexus.patterns.strategy.frete.CalculoFreteStrategy;
import br.com.java.nexus.patterns.strategy.frete.OpcaoFreteDTO;
import br.com.java.nexus.patterns.strategy.frete.TipoFrete;
import br.com.java.nexus.service.exception.ValidacaoException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * Contexto do Padrão GoF: Strategy
 * Delega o cálculo do valor e prazo do frete para a estratégia concreta selecionada.
 */
@Service
public class FreteService {

    private final Map<TipoFrete, CalculoFreteStrategy> estrategias = new EnumMap<>(TipoFrete.class);

    public FreteService(List<CalculoFreteStrategy> strategyList) {
        for (CalculoFreteStrategy strategy : strategyList) {
            this.estrategias.put(strategy.getTipo(), strategy);
        }
    }

    /**
     * Mantém compatibilidade com o cálculo padrão do sistema.
     */
    public BigDecimal calcularFrete(Carrinho carrinho, Endereco endereco) {
        return calcularFrete(carrinho, endereco, TipoFrete.PADRAO);
    }

    /**
     * Calcula o valor do frete utilizando uma estratégia específica (Strategy Pattern).
     */
    public BigDecimal calcularFrete(Carrinho carrinho, Endereco endereco, TipoFrete tipoFrete) {
        TipoFrete tipo = (tipoFrete != null) ? tipoFrete : TipoFrete.PADRAO;
        CalculoFreteStrategy strategy = estrategias.get(tipo);

        if (strategy == null) {
            throw new ValidacaoException("Estratégia de frete não encontrada para o tipo: " + tipo);
        }

        return strategy.calcular(carrinho, endereco);
    }

    /**
     * Retorna todas as cotações de frete disponíveis para o carrinho e endereço informados.
     */
    public List<OpcaoFreteDTO> listarOpcoesFrete(Carrinho carrinho, Endereco endereco) {
        List<OpcaoFreteDTO> opcoes = new ArrayList<>();
        for (CalculoFreteStrategy strategy : estrategias.values()) {
            try {
                BigDecimal valor = strategy.calcular(carrinho, endereco);
                int prazo = strategy.getPrazoEntregaDias(endereco);
                opcoes.add(new OpcaoFreteDTO(strategy.getTipo(), strategy.getTipo().getDescricao(), valor, prazo));
            } catch (Exception ignored) {
                // Se a estratégia não for aplicável, não adiciona na listagem
            }
        }
        return opcoes;
    }
}