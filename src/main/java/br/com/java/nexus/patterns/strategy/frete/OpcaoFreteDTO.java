package br.com.java.nexus.patterns.strategy.frete;

import java.math.BigDecimal;

public record OpcaoFreteDTO(
        TipoFrete tipo,
        String nome,
        BigDecimal valor,
        int prazoDias
) {}
