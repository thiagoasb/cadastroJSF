package br.com.thiago.repository;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

import br.com.thiago.entidades.Lancamento;
import br.com.thiago.jpautil.JPAUtil;

public class IdaoLancamentoImpl implements IDaoLancamento {

	@Override
	public List<Lancamento> consultarUsuarioLogado(Long codUser) {
		List<Lancamento> lista = null;
		
		EntityManager entityManager = JPAUtil.getEntityManager();
		EntityTransaction transaction = entityManager.getTransaction();
		transaction.begin();
		
		lista = entityManager.createQuery("FROM Lancamento WHERE usuario.id = "
				+ codUser).getResultList();
		
		transaction.commit();
		entityManager.close();
		
		return lista;
	}

}
