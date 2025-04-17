package br.dee.trancasdee.controllers;

import br.dee.trancasdee.models.Ambientes;
import br.dee.trancasdee.services.AmbientesService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/ambientes")
public class AmbientesController {

    private final AmbientesService ambientesService;

    public AmbientesController(AmbientesService ambientesService) {
        this.ambientesService = ambientesService;
    }

    @GetMapping
    public ResponseEntity findAll() {
        return ResponseEntity.ok(ambientesService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity findById(@PathVariable Long id) {
        return ResponseEntity.ok(ambientesService.findById(id));
    }

    @GetMapping("/sala/{sala}")
    public ResponseEntity findAmbientesBySala(@PathVariable Ambientes ambientes){
        return ResponseEntity.ok(ambientesService.findAmbientesBySala(ambientes));
    }

    public ResponseEntity save(Ambientes ambientes){
        return ResponseEntity.ok(ambientesService.save(ambientes));
    }

}
