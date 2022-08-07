package br.unirio.dsw.compartilhador.api.repository;

import java.util.List;

import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import br.unirio.dsw.compartilhador.api.model.Compartilhamento;

@Transactional(readOnly = true)
@NamedQueries({
	@NamedQuery(name = "CompartilhamentoRepository.bagaca", query = "SELECT ic FROM ItemCompartilhado ic WHERE ic.usuario.id = :ownerId") 
})
public interface CompartilhamentoRepository extends JpaRepository<Compartilhamento, Long>
{
	List<Compartilhamento> findByUsuarioId(Long usuarioId);

	Page<Compartilhamento> findByUsuarioId(Long usuarioId, Pageable pageable);

	List<Compartilhamento> findByUsuarioIdAndAceito(Long usuarioId, boolean aceito);

	// Achar compartilhamentos de um item
	@Query("SELECT co FROM Compartilhamento co WHERE co.itemcompartilhado.id = :itemId")
	Page<Compartilhamento> findByItemId(@Param("itemId") Long itemId, Pageable pageable);
	
	// Achar número de compartilhamento em aberto
	@Query("SELECT co FROM Compartilhamento co WHERE co.usuario.id = :usuarioId AND co.aceito = false AND co.rejeitado = false AND co.cancelado_dono = false AND co.cancelado_usuario = false")
	List<Compartilhamento> findByUsuarioIdAndAberto(@Param("usuarioId") Long usuarioId);
}