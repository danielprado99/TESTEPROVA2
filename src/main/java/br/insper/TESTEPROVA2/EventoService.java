package br.insper.TESTEPROVA2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;

@Service
public class EventoService {

    @Autowired
    private EventoRepository eventoRepository;

    @Autowired
    private RestTemplate restTemplate;

    private final String API_USUARIO_URL = "http://184.72.80.215:8080/usuario/";

    /**
     * Cadastro de um novo evento. Verifica se o criador existe.
     */
    public Evento cadastrarEvento(Evento evento) {
        // Verifica se o criador existe na API externa
        if (!verificarUsuario(evento.getCriadorCpf())) {
            throw new IllegalArgumentException("Criador do evento não encontrado.");
        }

        // Salva o evento no MongoDB
        return eventoRepository.save(evento);
    }

    /**
     * Listar todos os eventos ou filtrar por nome.
     */
    public List<Evento> listarEventos(String nome) {
        if (nome != null && !nome.isEmpty()) {
            return eventoRepository.findByNomeContaining(nome);  // Filtra pelo nome
        }
        return eventoRepository.findAll();  // Retorna todos os eventos
    }

    /**
     * Adicionar um convidado ao evento. Verifica se o convidado existe.
     */
    public Evento adicionarConvidado(String eventoId, String cpf) {
        // Verifica se o convidado existe na API externa
        if (!verificarUsuario(cpf)) {
            throw new IllegalArgumentException("Convidado não encontrado.");
        }

        // Busca o evento pelo ID
        Optional<Evento> eventoOptional = eventoRepository.findById(eventoId);
        if (eventoOptional.isPresent()) {
            Evento evento = eventoOptional.get();

            // Verifica se o evento pode receber mais convidados
            if (!evento.podeAdicionarConvidado()) {
                throw new IllegalArgumentException("Evento já atingiu o número máximo de convidados");
            }

            // Adiciona o CPF ao conjunto de convidados
            evento.getConvidados().add(cpf);
            return eventoRepository.save(evento);
        }

        throw new IllegalArgumentException("Evento não encontrado.");
    }

    /**
     * Método para verificar a existência de um usuário via API externa.
     */
    boolean verificarUsuario(String cpf) {
        try {
            // Faz uma requisição HTTP GET para verificar se o usuário existe
            String url = API_USUARIO_URL + cpf;
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

            // Verifica se a resposta foi um sucesso (status 200)
            return response.getStatusCode().is2xxSuccessful();
        } catch (Exception e) {
            // Se houver algum erro (404 ou outro), retorna false
            return false;
        }
    }
}