package br.com.thiago.repository;

import java.util.List;

import br.com.thiago.entidades.Lancamento;

public interface IDaoLancamento {
	
	List<Lancamento> consultarUsuarioLogado(Long codUser);
}
