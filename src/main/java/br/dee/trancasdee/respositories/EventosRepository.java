package br.dee.trancasdee.respositories;

import br.dee.trancasdee.models.Eventos;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface EventosRepository extends JpaRepository<Eventos, Long> {


}
