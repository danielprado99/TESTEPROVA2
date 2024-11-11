package br.insper.TESTEPROVA2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ArtigoService {

    @Autowired
    private ArtigoRepository repository;

    @Autowired
    private TokenService tokenService;

    public Artigo criarArtigo(ArtigoRequest request, String token) {
        validateAdminAccess(token);

        Artigo artigo = new Artigo();
        artigo.setTitulo(request.getTitulo());
        artigo.setConteudo(request.getConteudo());
        artigo.setDataCriacao(LocalDateTime.now());

        return repository.save(artigo);
    }

    public void deletarArtigo(String id, String token) {
        validateAdminAccess(token);
        repository.deleteById(id);
    }

    public List<Artigo> listarArtigos(String token) {
        validateAccess(token);
        return repository.findAll();
    }

    public Artigo buscarArtigo(String id, String token) {
        validateAccess(token);
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Artigo não encontrado"));
    }

    private void validateAdminAccess(String token) {
        if (!tokenService.validateTokenAndRole(token, "ADMIN")) {
            throw new RuntimeException("Acesso não autorizado. Apenas administradores podem realizar esta ação.");
        }
    }

    private void validateAccess(String token) {
        if (!tokenService.validateTokenAndRole(token, "ADMIN") &&
                !tokenService.validateTokenAndRole(token, "DEVELOPER")) {
            throw new RuntimeException("Acesso não autorizado.");
        }
    }
}
