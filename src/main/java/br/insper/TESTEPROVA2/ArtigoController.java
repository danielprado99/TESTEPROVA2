package br.insper.TESTEPROVA2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/artigos")
public class ArtigoController {

    @Autowired
    private ArtigoService artigoService;

    @PostMapping
    public ResponseEntity<Artigo> criarArtigo(@RequestBody ArtigoRequest request, @RequestHeader("Authorization") String token) {
        return ResponseEntity.ok(artigoService.criarArtigo(request, token));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarArtigo(@PathVariable String id, @RequestHeader("Authorization") String token) {
        artigoService.deletarArtigo(id, token);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<Artigo>> listarArtigos(@RequestHeader("Authorization") String token) {
        return ResponseEntity.ok(artigoService.listarArtigos(token));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Artigo> buscarArtigo(@PathVariable String id, @RequestHeader("Authorization") String token) {
        return ResponseEntity.ok(artigoService.buscarArtigo(id, token));
    }
}
