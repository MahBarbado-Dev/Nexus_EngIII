package br.com.java.nexus.repository.produto;

import br.com.java.nexus.model.produto.Produto;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProdutoRepository
        extends JpaRepository<Produto, Long> {

    /*
     * ==========================================================
     * CONSULTAS
     * ==========================================================
     */

    Optional<Produto> findByIsbn(
            String isbn
    );

    List<Produto> findByAtivoTrueOrderByTituloAsc();

    List<Produto> findByAtivoTrueAndEstoqueGreaterThanOrderByTituloAsc(
            Integer estoque
    );

    List<Produto> findByTituloContainingIgnoreCaseAndAtivoTrueOrderByTituloAsc(
            String titulo
    );
}
