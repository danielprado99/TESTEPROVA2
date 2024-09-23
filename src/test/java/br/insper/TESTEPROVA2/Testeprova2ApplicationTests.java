package br.insper.TESTEPROVA2;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@SpringBootTest
class Testeprova2ApplicationTests {

	@Mock
	private EventoRepository eventoRepository;

	@Mock
	private RestTemplate restTemplate;

	@InjectMocks
	private EventoService eventoService;

	@BeforeEach
	public void setup() {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	public void testCadastrarEventoComCriadorValido() {
		Evento evento = new Evento();
		evento.setCriadorCpf("12345678900");

		// Mock da resposta da API externa
		when(restTemplate.getForEntity(anyString(), eq(String.class)))
				.thenReturn(new ResponseEntity<>(HttpStatus.OK));

		// Mock do comportamento do repositório
		when(eventoRepository.save(any(Evento.class))).thenReturn(evento);

		// Testando o cadastro de evento
		Evento result = eventoService.cadastrarEvento(evento);
		assertEquals(evento, result);
	}

	@Test
	public void testCadastrarEventoComCriadorInvalido() {
		Evento evento = new Evento();
		evento.setCriadorCpf("12345678900");

		// Mock de resposta de erro da API externa
		when(restTemplate.getForEntity(anyString(), eq(String.class)))
				.thenReturn(new ResponseEntity<>(HttpStatus.NOT_FOUND));

		// Testando exceção
		assertThrows(IllegalArgumentException.class, () -> {
			eventoService.cadastrarEvento(evento);
		});
	}

	@Test
	public void testAdicionarConvidadoComEventoValido() {
		Evento evento = new Evento();
		evento.setMaxConvidados(10);
		evento.setConvidados(new HashSet<>(Set.of("11111111111")));  // Usar HashSet mutável

		// Mock da resposta da API externa
		when(restTemplate.getForEntity(anyString(), eq(String.class)))
				.thenReturn(new ResponseEntity<>(HttpStatus.OK));

		// Mock do comportamento do repositório
		when(eventoRepository.findById(anyString())).thenReturn(Optional.of(evento));
		when(eventoRepository.save(any(Evento.class))).thenReturn(evento);

		// Testando a adição de convidado
		Evento result = eventoService.adicionarConvidado("evento1", "22222222222");
		assertTrue(result.getConvidados().contains("22222222222"));
	}

	@Test
	public void testAdicionarConvidadoEventoCheio() {
		Evento evento = new Evento();
		evento.setMaxConvidados(1); // O evento só pode ter 1 convidado
		evento.setConvidados(Set.of("11111111111")); // Já tem um convidado

		// Mock da resposta da API externa
		when(restTemplate.getForEntity(anyString(), eq(String.class)))
				.thenReturn(new ResponseEntity<>(HttpStatus.OK));

		// Mock do comportamento do repositório
		when(eventoRepository.findById(anyString())).thenReturn(Optional.of(evento));

		// Testando a adição de convidado em evento cheio
		assertThrows(IllegalArgumentException.class, () -> {
			eventoService.adicionarConvidado("evento1", "22222222222");
		});
	}

	@Test
	public void testCadastrarEventoComCpfInvalido() {
		Evento evento = new Evento();
		evento.setCriadorCpf("00000000000");

		// Mock de resposta da API externa, retornando 404 (usuário não encontrado)
		when(restTemplate.getForEntity(anyString(), eq(String.class)))
				.thenReturn(new ResponseEntity<>(HttpStatus.NOT_FOUND));

		// Testa se a exceção é lançada ao tentar cadastrar um evento com CPF inválido
		assertThrows(IllegalArgumentException.class, () -> {
			eventoService.cadastrarEvento(evento);
		});
	}

	@Test
	public void testListarEventosComFiltroNome() {
		// Mock do repositório para filtrar pelo nome
		when(eventoRepository.findByNomeContaining(anyString()))
				.thenReturn(List.of(new Evento()));

		// Testa a listagem filtrada
		List<Evento> eventos = eventoService.listarEventos("Evento Teste");
		assertFalse(eventos.isEmpty());  // Deve retornar um evento
		verify(eventoRepository, times(1)).findByNomeContaining("Evento Teste");
	}

	@Test
	public void testAdicionarConvidadoComCpfInvalido() {
		Evento evento = new Evento();
		evento.setMaxConvidados(10);
		evento.setConvidados(new HashSet<>(Set.of("11111111111")));

		// Mock da API externa retornando 404 (usuário não encontrado)
		when(restTemplate.getForEntity(anyString(), eq(String.class)))
				.thenReturn(new ResponseEntity<>(HttpStatus.NOT_FOUND));

		// Mock do comportamento do repositório para encontrar o evento
		when(eventoRepository.findById(anyString())).thenReturn(Optional.of(evento));

		// Testa se a exceção é lançada ao tentar adicionar um convidado com CPF inválido
		assertThrows(IllegalArgumentException.class, () -> {
			eventoService.adicionarConvidado("evento1", "00000000000");
		});
	}

	@Test
	public void testAdicionarConvidadoEventoLotado() {
		Evento evento = new Evento();
		evento.setMaxConvidados(1);
		evento.setConvidados(new HashSet<>(Set.of("11111111111"))); // Evento já com o número máximo de convidados

		// Mock da resposta da API externa
		when(restTemplate.getForEntity(anyString(), eq(String.class)))
				.thenReturn(new ResponseEntity<>(HttpStatus.OK));

		// Mock do comportamento do repositório
		when(eventoRepository.findById(anyString())).thenReturn(Optional.of(evento));

		// Testa se a exceção é lançada ao tentar adicionar um convidado a um evento lotado
		assertThrows(IllegalArgumentException.class, () -> {
			eventoService.adicionarConvidado("evento1", "22222222222");
		});
	}

	@Test
	public void testListarEventosSemFiltro() {
		// Mock do repositório para retornar uma lista de eventos
		when(eventoRepository.findAll()).thenReturn(List.of(new Evento()));

		// Testa a listagem sem passar o nome do evento (sem filtro)
		List<Evento> eventos = eventoService.listarEventos(null);
		assertFalse(eventos.isEmpty());  // Deve retornar um evento
		verify(eventoRepository, times(1)).findAll();
	}

	@Test
	public void testVerificarUsuarioComErroDeConexao() {
		// Simula uma exceção na chamada HTTP (por exemplo, erro de conexão)
		when(restTemplate.getForEntity(anyString(), eq(String.class)))
				.thenThrow(new RuntimeException("Erro de conexão"));

		// Testa se a verificação de usuário retorna false em caso de erro
		boolean resultado = eventoService.verificarUsuario("12345678900");
		assertFalse(resultado);  // A chamada falha, então deve retornar false
	}

	@Test
	public void testListarEventos() {
		eventoService.listarEventos(null);


	}
}