package br.com.thiago.repository;


import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

import br.com.thiago.entidades.Pessoa;
import br.com.thiago.jpautil.JPAUtil;

public class IDaoPessoaImpl implements IDaoPessoa {

	@Override
	public Pessoa consultarUsuario(String login, String senha) {
		
		Pessoa pessoa = null;
			
		EntityManager entityManager = JPAUtil.getEntityManager();
		EntityTransaction transaction = entityManager.getTransaction();
		transaction.begin();
			
		pessoa = (Pessoa) entityManager.createQuery("SELECT p FROM Pessoa p WHERE p.login = '"
				+ login + "' AND p.senha = '" + senha + "'").getSingleResult();
			
		transaction.commit();
		entityManager.close();
			
		return pessoa;
		
		
	}

}
