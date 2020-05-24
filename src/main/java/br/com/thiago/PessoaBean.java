package br.com.thiago;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import br.com.thiago.dao.DaoGeneric;
import br.com.thiago.entidades.Pessoa;

@ViewScoped
@ManagedBean(name = "pessoaBean")
public class PessoaBean {
	
	private Pessoa pessoa = new Pessoa();
	private DaoGeneric<Pessoa> daoGeneric = new DaoGeneric<Pessoa>();
	private List<Pessoa> pessoas = new ArrayList<Pessoa>();
	
	public String salvar() {
		
		pessoa = daoGeneric.atualizar(pessoa);
		carregarLista();
		return "";
	}
	
	public void novo() {
		pessoa = new Pessoa();
	}
	
	public String novoBotao() {
		novo();
		return "";
	}
	
	public String remover() {
		daoGeneric.deletarPorID(pessoa);
		novo();
		carregarLista();
		return "";
	}
	
	@PostConstruct
	public void carregarLista() {
		pessoas = daoGeneric.listar(Pessoa.class);
	}

	public Pessoa getPessoa() {
		return pessoa;
	}

	public void setPessoa(Pessoa pessoa) {
		this.pessoa = pessoa;
	}

	public DaoGeneric<Pessoa> getDaoGeneric() {
		return daoGeneric;
	}

	public void setDaoGeneric(DaoGeneric<Pessoa> daoGeneric) {
		this.daoGeneric = daoGeneric;
	}
	public List<Pessoa> getPessoas() {
		return pessoas;
	}
	
}
