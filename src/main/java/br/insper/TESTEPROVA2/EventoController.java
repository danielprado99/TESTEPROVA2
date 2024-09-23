package br.insper.TESTEPROVA2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/eventos")
public class EventoController {

    @Autowired
    private EventoService eventoService;

    // Cadastro de evento
    @PostMapping
    public ResponseEntity<Evento> cadastrarEvento(@RequestBody Evento evento) {
        Evento novoEvento = eventoService.cadastrarEvento(evento);
        return ResponseEntity.ok(novoEvento);
    }

    // Listagem de eventos (com filtro opcional por nome)
    @GetMapping
    public List<Evento> listarEventos(@RequestParam(required = false) String nome) {
        return eventoService.listarEventos(nome);
    }

    // Adicionar convidado a um evento
    @PostMapping("/{eventoId}/convidados")
    public ResponseEntity<Evento> adicionarConvidado(@PathVariable String eventoId, @RequestParam String cpf) {
        Evento eventoAtualizado = eventoService.adicionarConvidado(eventoId, cpf);
        return ResponseEntity.ok(eventoAtualizado);
    }
}