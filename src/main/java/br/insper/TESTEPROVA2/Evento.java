package br.insper.TESTEPROVA2;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.HashSet;
import java.util.Set;

@Data
@Document(collection = "eventos")
public class Evento {
    @Id
    private String id;
    private String nome;
    private String descricao;
    private int maxConvidados;
    private String criadorCpf;
    private Set<String> convidados = new HashSet<>(); // Para armazenar os CPFs dos convidados

    public boolean podeAdicionarConvidado() {
        return this.convidados.size() < this.maxConvidados;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public int getMaxConvidados() {
        return maxConvidados;
    }

    public void setMaxConvidados(int maxConvidados) {
        this.maxConvidados = maxConvidados;
    }

    public String getCriadorCpf() {
        return criadorCpf;
    }

    public void setCriadorCpf(String criadorCpf) {
        this.criadorCpf = criadorCpf;
    }

    public Set<String> getConvidados() {
        return convidados;
    }

    public void setConvidados(Set<String> convidados) {
        this.convidados = convidados;
    }
}