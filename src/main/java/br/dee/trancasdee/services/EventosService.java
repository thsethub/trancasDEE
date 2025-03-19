package br.dee.trancasdee.services;


import br.dee.trancasdee.models.Eventos;
import br.dee.trancasdee.respositories.EventosRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EventosService {

        private final EventosRepository eventosRepository;

        public EventosService(EventosRepository eventosRepository) {
            this.eventosRepository = eventosRepository;
        }

        public List<Eventos> findAll() {
            return eventosRepository.findAll();
        }

        public Eventos findById(Long id) {
            return eventosRepository.findById(id).orElse(null);
        }

        public Eventos save(Eventos eventos){
            return eventosRepository.save(eventos);
        }
}
