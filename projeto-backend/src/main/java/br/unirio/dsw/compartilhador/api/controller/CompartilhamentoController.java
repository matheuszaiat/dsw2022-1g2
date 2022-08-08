package br.unirio.dsw.compartilhador.api.controller;

import java.util.Optional;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.unirio.dsw.compartilhador.api.model.Compartilhamento;
import br.unirio.dsw.compartilhador.api.model.ItemCompartilhado;
import br.unirio.dsw.compartilhador.api.model.Usuario;
import br.unirio.dsw.compartilhador.api.repository.CompartilhamentoRepository;
import br.unirio.dsw.compartilhador.api.repository.ItemCompartilhadoRepository;
import br.unirio.dsw.compartilhador.api.repository.UsuarioRepository;
import br.unirio.dsw.compartilhador.api.utils.ValidationUtils;
import br.unirio.dsw.compartilhador.api.utils.spring.ControllerResponse;
import br.unirio.dsw.compartilhador.api.utils.spring.PageDTO;
import br.unirio.dsw.compartilhador.api.utils.spring.ResponseData;
import lombok.Data;

/**
 * Controlador com as ações de compartilhamento de itens
 * 
 * @author User
 */

@RestController
@RequestMapping("/api/compartilhamento")
@CrossOrigin(origins="*")

public class CompartilhamentoController {
	
	private static final Logger log = LoggerFactory.getLogger(ItemCompartilhadoController.class);

	@Autowired
	private CompartilhamentoRepository compartilhamentoRepositorio;
	
	@Autowired
	private UsuarioRepository usuarioRepositorio;

	@Autowired
	private ItemCompartilhadoRepository itemRepositorio;
	
	@Value("${app.hostname}")
	private String hostname;
	
	
	/**
	 * Ação que cria um novo compartilhamento
	 */
	@PostMapping(value = "/novo")
	public ResponseEntity<ResponseData> novo(@RequestBody CompartilhamentoRegistroForm form, BindingResult result)
	{
		
		Usuario usuario = usuarioRepositorio.findByEmail(form.getEmail());
		
		if (usuario == null)
			return ControllerResponse.fail("email", "Não existe um usuário com esse email.");
		
		ItemCompartilhado item = itemRepositorio.findByNome(form.getItem());
		
		if (item == null)
			return ControllerResponse.fail("item", "Não existe um item com esse nome.");
		
		if (ValidationUtils.validDate(form.getDataInicio()) == false || ValidationUtils.validDate(form.getDataTermino()) == false)
			return ControllerResponse.fail("dataTermino", "Alguma data não está no formato correto.");

		LocalDate dataInicio = LocalDate.parse(form.getDataInicio());
		LocalDate dataTermino = LocalDate.parse(form.getDataTermino());
		
		if (dataInicio.isAfter(dataTermino))
			return ControllerResponse.fail("dataTermino", "Data de Início é depois da Data de Término");
		
		Compartilhamento compartilhamento = new Compartilhamento();
		
		compartilhamento.setUsuario(usuario);
		compartilhamento.setItem(item);
		compartilhamento.setDataInicio(dataInicio);
		compartilhamento.setDataTermino(dataTermino);
		
		compartilhamentoRepositorio.save(compartilhamento);
		
		return ControllerResponse.success();
	}
	
	/**
	 * Ação que cancela um compartilhamento
	 */	
	@DeleteMapping(value = "/{id}")
	public ResponseEntity<ResponseData> cancela(@PathVariable("id") long id)
	{
		
		log.info("Cancelando um compartilhamento: {}", id);

		String username = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		if (username == null)
			return ControllerResponse.fail("Não há um usuário logado no sistema.");

        Usuario usuario = usuarioRepositorio.findByEmail(username);

        if (usuario == null)
			return ControllerResponse.fail("Não foi possível recuperar os dados do usuário a partir das credenciais.");

        Optional<Compartilhamento> compartilhamento = compartilhamentoRepositorio.findById(id);
        
        if (!compartilhamento.isPresent())
			return ControllerResponse.fail("O compartilhamento não foi encontrado.");
        
        if (compartilhamento.get().isCanceladoDono() == true || compartilhamento.get().isCanceladoUsuario() == true)
			return ControllerResponse.fail("O compartilhamento já foi cancelado.");
        
        // Verifica se quem cancelou foi o usuário que compartilhou ou o usuário para o qual o item foi compartilhado
        if (usuario.getId().equals(compartilhamento.get().getUsuario().getId()))
        	compartilhamento.get().setCanceladoUsuario(true);
        else
        	compartilhamento.get().setCanceladoDono(true);
        	
        compartilhamentoRepositorio.save(compartilhamento.get());
		return ControllerResponse.success();
	}
	
	/**
	 * Ação que aceita ou rejeita um compartilhamento de um usuário
	 */
	@PostMapping(value="/aceita")
	public ResponseEntity<ResponseData> aceita(@RequestParam long id, @RequestParam boolean aceita)
	{
		log.info("Aceitando ou rejeitando um compartilhamento: {}", id);
		log.info("Aceita?: {}", aceita);

		String username = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		if (username == null)
			return ControllerResponse.fail("Não há um usuário logado no sistema.");

        Usuario usuario = usuarioRepositorio.findByEmail(username);

        if (usuario == null)
			return ControllerResponse.fail("Não foi possível recuperar os dados do usuário a partir das credenciais.");

        Optional<Compartilhamento> compartilhamento = compartilhamentoRepositorio.findById(id);		
		
        if (!compartilhamento.isPresent())
			return ControllerResponse.fail("O compartilhamento não foi encontrado.");
        
        //Verifica se o compartilhamento está em aberto
        if ( !compartilhamento.get().isRejeitado() && !compartilhamento.get().isCanceladoDono()
        											&& !compartilhamento.get().isCanceladoUsuario()) {
			
        	//Verifica se é para aceitar ou rejeitar
        	if (!compartilhamento.get().isAceito() && aceita == true) {
        		compartilhamento.get().setAceito(true);
        	} else {
        		compartilhamento.get().setAceito(false);
        		compartilhamento.get().setRejeitado(true);
        	}
        	
        } else {
        	return ControllerResponse.fail("O compartilhamento não está aberto");
        }
		
        compartilhamentoRepositorio.save(compartilhamento.get());
		return ControllerResponse.success();
	}
	
	/**
	 * Ação que lista todos os compartilhamentos de um usuário
	 */
	@GetMapping(value = "/lista")
	public ResponseEntity<ResponseData> lista(@RequestParam int page, @RequestParam int per_page)
	{
		
		log.info("Listando compartilhamentos de um usuário");
		String username = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		if (username == null)
			return ControllerResponse.fail("Não há um usuário logado no sistema.");

        Usuario usuario = usuarioRepositorio.findByEmail(username);

		if (usuario == null)
			return ControllerResponse.fail("Não foi possível recuperar os dados do usuário a partir das credenciais.");
		
		Pageable pageable = PageRequest.of(page-1, per_page);
		Page<Compartilhamento> compartilhamentos = compartilhamentoRepositorio.findByUsuarioId(usuario.getId(), pageable);

		PageDTO<CompartilhamentoDTO> result = new PageDTO<CompartilhamentoDTO>(compartilhamentos.getTotalElements(), page, per_page);
		
		compartilhamentos.forEach(compartilhamento -> {
			CompartilhamentoDTO dto = new CompartilhamentoDTO();
			
			String status = "Aberto.";
			
			if (compartilhamento.isAceito()) { status = "Aceito."; }
			if (compartilhamento.isRejeitado()) { status = "Rejeitado."; }
			if (compartilhamento.isCanceladoUsuario()) { status = "Cancelado pelo Usuário."; }
			if (compartilhamento.isCanceladoDono()) { status = "Cancelado pelo Dono."; }
			
			
			dto.setId(compartilhamento.getId());
			dto.setItemNome(compartilhamento.getItem().getNome());
			dto.setNomeUsuario(compartilhamento.getItem().getUsuario().getNome()); //DONO DO ITEM
			dto.setDataInicio(compartilhamento.getDataInicio().toString());
			dto.setDataTermino(compartilhamento.getDataTermino().toString());
			dto.setStatus(status);
			
			result.add(dto);
		});
		
		return ControllerResponse.success(result);
	}
	
	/**
	 * Ação que lista todos os usos um item compartilhado
	 */
	@GetMapping(value = "/usos")
	public ResponseEntity<ResponseData> usos(@RequestParam long itemId, @RequestParam int page, @RequestParam int per_page)
	{
		
		log.info("Listando compartilhamentos de um usuário");
		
		Pageable pageable = PageRequest.of(page-1, per_page);
		Page<Compartilhamento> compartilhamentos = compartilhamentoRepositorio.findByItemId(itemId, pageable);

		PageDTO<CompartilhamentoDTO> result = new PageDTO<CompartilhamentoDTO>(compartilhamentos.getTotalElements(), page, per_page);
		
		compartilhamentos.forEach(compartilhamento -> {
			CompartilhamentoDTO dto = new CompartilhamentoDTO();
			
			String status = "Aberto.";
			
			if (compartilhamento.isAceito()) { status = "Aceito."; }
			if (compartilhamento.isRejeitado()) { status = "Rejeitado."; }
			if (compartilhamento.isCanceladoUsuario()) { status = "Cancelado pelo Usuário."; }
			if (compartilhamento.isCanceladoDono()) { status = "Cancelado pelo Dono."; }
			
			
			dto.setId(compartilhamento.getId());
			dto.setItemNome(compartilhamento.getItem().getNome());
			dto.setNomeUsuario(compartilhamento.getUsuario().getNome()); // QUEM VAI RECEBER O ITEM
			dto.setDataInicio(compartilhamento.getDataInicio().toString());
			dto.setDataTermino(compartilhamento.getDataTermino().toString());
			dto.setStatus(status);
			
			result.add(dto);
		});
		
		return ControllerResponse.success(result);
	}
	
	/**
	 * Ação que retorna quantos itens em "Aberto" tem no sistema
	 */
	@GetMapping(value = "/abertos")
	public ResponseEntity<ResponseData> abertos () {
		log.info("Listando compartilhamentos de um usuário");
		String username = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		if (username == null)
			return ControllerResponse.fail("Não há um usuário logado no sistema.");

        Usuario usuario = usuarioRepositorio.findByEmail(username);

		if (usuario == null)
			return ControllerResponse.fail("Não foi possível recuperar os dados do usuário a partir das credenciais.");
		
		List<Compartilhamento> lista = compartilhamentoRepositorio.findByUsuarioIdAndAberto(usuario.getId());
		
		int result = lista.size();
		
		return ControllerResponse.success(result);
	}
	
}
/**
 * Formulário para registro de um novo compartilhamento
 * 
 * @author Grupo
 */
@Data class CompartilhamentoRegistroForm
{
	private String email;
	
	private String item;
	
	private String dataInicio;
	
	private String dataTermino;
}

/**
 * Representa um compartilhamento para o usuário no lado cliente
 * 	
 * @author Grupo
 */
@Data class CompartilhamentoDTO
{
	private long id;
	
	private String itemNome;
	
	private String nomeUsuario;
	
	private String dataInicio;
	
	private String dataTermino;
	
	private String status;
	
}
