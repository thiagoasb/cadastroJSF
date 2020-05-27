package br.com.thiago.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

import br.com.thiago.jpautil.JPAUtil;

public class DaoGeneric<E> {

	public void salvar(E entidade) {

		EntityManager entityManager = JPAUtil.getEntityManager();
		EntityTransaction transaction = entityManager.getTransaction();
		transaction.begin();

		entityManager.persist(entidade);

		transaction.commit();
		entityManager.close();
	}

	public E atualizar(E entidade) {

		EntityManager entityManager = JPAUtil.getEntityManager();
		EntityTransaction transaction = entityManager.getTransaction();
		transaction.begin();

		E retorno = entityManager.merge(entidade);

		transaction.commit();
		entityManager.close();
		
		return retorno;
	}
	
	public void deletar(E entidade) {

		EntityManager entityManager = JPAUtil.getEntityManager();
		EntityTransaction transaction = entityManager.getTransaction();
		transaction.begin();
		//Gera um erro: "Remove Detached instance..."
		entityManager.remove(entidade);

		transaction.commit();
		entityManager.close();
	}
	
	public void deletarPorID(E entidade) {

		EntityManager entityManager = JPAUtil.getEntityManager();
		EntityTransaction transaction = entityManager.getTransaction();
		transaction.begin();
		
		Object id = JPAUtil.getPrimaryKey(entidade);
		entityManager.createQuery("DELETE FROM " + entidade.getClass().getCanonicalName() + " WHERE id = " + id)
			.executeUpdate();

		transaction.commit();
		entityManager.close();
	}
	
	public List<E> listar(Class<E> entidade) {
		
		EntityManager entityManager = JPAUtil.getEntityManager();
		EntityTransaction transaction = entityManager.getTransaction();
		transaction.begin();
		
		List<E> retorno = entityManager.createQuery("FROM " + 
				entidade.getName()).getResultList();
		
		transaction.commit();
		entityManager.close();
		
		return retorno;
	}
	
	public E consultar(Class<E> entidade, String codigo) {
		EntityManager entityManager = JPAUtil.getEntityManager();
		EntityTransaction transaction = entityManager.getTransaction();
		transaction.begin();
		
		E objeto = (E) entityManager.find(entidade, Long.parseLong(codigo));
		transaction.commit();
		
		return objeto;
		
	}

}
