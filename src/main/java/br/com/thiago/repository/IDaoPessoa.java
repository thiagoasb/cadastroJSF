package br.com.thiago.repository;

import br.com.thiago.entidades.Pessoa;

public interface IDaoPessoa {
	
	Pessoa consultarUsuario(String login, String senha);
}
