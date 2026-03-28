package br.dee.trancasdee.controllers;


import br.dee.trancasdee.models.Usuarios.Usuarios;
import br.dee.trancasdee.services.UsuariosService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/usuarios")
public class UsuariosController {

    private final UsuariosService usuariosService;

    public UsuariosController(UsuariosService usuariosService) {
        this.usuariosService = usuariosService;
    }

    @GetMapping
    public ResponseEntity findAll() {
        return ResponseEntity.ok(usuariosService.findAll());
    }

    @GetMapping("/busca")
    public ResponseEntity<List<Usuarios>> busca(@RequestParam String nome) {
        return ResponseEntity.ok(usuariosService.buscaPorNome(nome));
    }

    @GetMapping("/{id}")
    public ResponseEntity findById(@PathVariable Long id) {
        return ResponseEntity.ok(usuariosService.findById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Usuarios> updateUniqueID(@PathVariable Long id, @RequestBody Map<String, Long> uniqueIDRequest) {
        Long uniqueID = uniqueIDRequest.get("UniqueID");
        Usuarios updatedUsuarios = usuariosService.updateUniqueID(id, uniqueID);
        return ResponseEntity.ok(updatedUsuarios);
    }


}
