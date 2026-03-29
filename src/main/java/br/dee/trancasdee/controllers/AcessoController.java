package br.dee.trancasdee.controllers;


import br.dee.trancasdee.models.Acesso.Acesso;
import br.dee.trancasdee.models.Acesso.AcessoRequest;
import br.dee.trancasdee.models.Acesso.AcessoResponse;
import br.dee.trancasdee.models.Acesso.AcessoUpdateRequest;
import br.dee.trancasdee.models.Ambientes;
import br.dee.trancasdee.models.Usuarios.Usuarios;
import br.dee.trancasdee.services.AcessoService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/acesso")
public class AcessoController {

    private final AcessoService acessoService;

    public AcessoController(AcessoService acessoService) {
        this.acessoService = acessoService;
    }

    @GetMapping
    public ResponseEntity<List<AcessoResponse>> findAll() {
        var aux = acessoService.findAll();
        return ResponseEntity.ok(aux.stream().map(AcessoResponse::new).toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity findById(@PathVariable Long id) {
        return ResponseEntity.ok(acessoService.findById(id));
    }

    @GetMapping("/sala/{salaId}")
    public ResponseEntity<Page<AcessoResponse>> findBySalaId(
            @PathVariable Long salaId,
            @RequestParam(defaultValue = "") String nome,
            @PageableDefault(size = 10, sort = "id") Pageable pageable) {
        Page<Acesso> page = acessoService.findAcessoBySalaIdPaged(salaId, nome, pageable);
        return ResponseEntity.ok(page.map(AcessoResponse::new));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AcessoResponse> update(@PathVariable Long id, @RequestBody AcessoUpdateRequest request) {
        Acesso acesso = acessoService.update(id, request);
        return ResponseEntity.ok(new AcessoResponse(acesso));
    }

    @PostMapping
    public ResponseEntity<AcessoResponse> create(@RequestBody AcessoRequest request) {
        Acesso acesso = acessoService.create(request);
        return ResponseEntity.ok(new AcessoResponse(acesso));
    }

    @PutMapping("/{id}/revogar")
    public ResponseEntity<AcessoResponse> revogar(@PathVariable Long id) {
        Acesso acesso = acessoService.revogar(id);
        return ResponseEntity.ok(new AcessoResponse(acesso));
    }

    @GetMapping("/usuario/{usuario}")
    public ResponseEntity<List<AcessoResponse>> findAcessoByUsuarios(@PathVariable Usuarios usuario) {
        var aux = acessoService.findAcessoByUsuarios(usuario);
        return ResponseEntity.ok(aux.stream().map(AcessoResponse::new).toList());
    }

    @GetMapping("/ambientes/{ambientes}")
    public ResponseEntity<List<AcessoResponse>> findAcessoByAmbientes(@PathVariable Ambientes ambientes) {
        var aux = acessoService.findAcessoByAmbientes(ambientes);
        return ResponseEntity.ok(aux.stream().map(AcessoResponse::new).toList());
    }

    @GetMapping("/uniqueID/{uniqueID}")
    public ResponseEntity<List<AcessoResponse>> findAcessoByUniqueID(@PathVariable Long uniqueID) {
        var aux = acessoService.findAcessoByUniqueID(uniqueID);
        return ResponseEntity.ok(aux.stream().map(AcessoResponse::new).toList());
    }

    @GetMapping("/sala/{sala}/uniqueID/{uniqueID}")
    public ResponseEntity<List<AcessoResponse>> findAcessoBySalaAndUniqueID(@PathVariable Long sala, @PathVariable String uniqueID) {
        var aux = acessoService.findAcessoBySalaAndUniqueID(sala, uniqueID);
        return ResponseEntity.ok(aux.stream().map(AcessoResponse::new).toList());
    }
}
