package br.dee.trancasdee.controllers;


import br.dee.trancasdee.models.Usuarios.Usuarios;
import br.dee.trancasdee.respositories.UsuariosRepository;
import br.dee.trancasdee.services.UsuariosService;
import org.springframework.data.domain.Page;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/usuarios")
public class UsuariosController {

    private final UsuariosService usuariosService;
    private final UsuariosRepository usuariosRepository;

    public UsuariosController(UsuariosService usuariosService, UsuariosRepository usuariosRepository) {
        this.usuariosService = usuariosService;
        this.usuariosRepository = usuariosRepository;
    }

    @GetMapping
    public ResponseEntity<Page<Usuarios>> findAll(
            @RequestParam(defaultValue = "") String nome,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(usuariosRepository.findAllFiltered(nome, pageable));
    }

    @GetMapping("/busca")
    public ResponseEntity<List<Usuarios>> busca(@RequestParam String nome) {
        return ResponseEntity.ok(usuariosService.buscaPorNome(nome));
    }

    @GetMapping("/{id}")
    public ResponseEntity findById(@PathVariable Long id) {
        return ResponseEntity.ok(usuariosService.findById(id));
    }

    @GetMapping("/disponiveis")
    public ResponseEntity<Page<Usuarios>> findDisponiveis(
            @RequestParam(defaultValue = "") String nome,
            @RequestParam Long salaId,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(usuariosService.findDisponiveisBySala(nome, salaId, pageable));
    }

    @PostMapping
    public ResponseEntity<Usuarios> create(@RequestBody Usuarios usuarios) {
        return ResponseEntity.ok(usuariosService.save(usuarios));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Usuarios> updateUniqueID(@PathVariable Long id, @RequestBody Map<String, Long> uniqueIDRequest) {
        Long uniqueID = uniqueIDRequest.get("UniqueID");
        Usuarios updatedUsuarios = usuariosService.updateUniqueID(id, uniqueID);
        return ResponseEntity.ok(updatedUsuarios);
    }


}
