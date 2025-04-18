package br.dee.trancasdee.controllers;


import br.dee.trancasdee.models.Acesso.Acesso;
import br.dee.trancasdee.models.Acesso.AcessoResponse;
import br.dee.trancasdee.models.Ambientes;
import br.dee.trancasdee.models.Usuarios.Usuarios;
import br.dee.trancasdee.services.AcessoService;
import org.apache.coyote.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
