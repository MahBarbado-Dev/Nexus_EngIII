package br.com.java.nexus.patterns.strategy.frete;

public enum TipoFrete {
    PADRAO("Econômica (Padrão)"),
    EXPRESSO("Expresso"),
    PROMOCIONAL("Frete Grátis Promocional");

    private final String descricao;

    TipoFrete(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
