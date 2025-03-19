package br.dee.trancasdee.controllers;


import br.dee.trancasdee.models.Eventos;
import br.dee.trancasdee.services.EventosService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/eventos")
public class EventosController {

    private final EventosService eventosService;

    public EventosController(EventosService eventosService) {
        this.eventosService = eventosService;
    }

    @GetMapping
    public ResponseEntity findAll() {
        return ResponseEntity.ok(eventosService.findAll());
    }

    public ResponseEntity findById(Long id) {
        return ResponseEntity.ok(eventosService.findById(id));
    }

    public ResponseEntity save(Eventos eventos){
        return ResponseEntity.ok(eventosService.save(eventos));
    }



}
