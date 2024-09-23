package br.insper.TESTEPROVA2;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface EventoRepository extends MongoRepository<Evento, String> {
    List<Evento> findByNomeContaining(String nome);  // Para buscar eventos por nome
}