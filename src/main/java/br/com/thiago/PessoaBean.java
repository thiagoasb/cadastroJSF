package br.com.thiago;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import br.com.thiago.dao.DaoGeneric;
import br.com.thiago.entidades.Pessoa;
import br.com.thiago.repository.IDaoPessoa;
import br.com.thiago.repository.IDaoPessoaImpl;

@ViewScoped
@ManagedBean(name = "pessoaBean")
public class PessoaBean {
	
	private Pessoa pessoa = new Pessoa();
	private DaoGeneric<Pessoa> daoGeneric = new DaoGeneric<Pessoa>();
	private List<Pessoa> pessoas = new ArrayList<Pessoa>();
	private IDaoPessoa iDaoPessoa = new IDaoPessoaImpl();
	
	public String salvar() {
		
		pessoa = daoGeneric.atualizar(pessoa);
		carregarLista();
		mostrarMsg("Cadastrado com sucesso!");
		return "";
	}
	
	private void mostrarMsg(String msg) {
		FacesContext context = FacesContext.getCurrentInstance();
		FacesMessage message = new FacesMessage(msg);
		context.addMessage(null, message);
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
		mostrarMsg("Removido com sucesso!");
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
	
	public String logar() {
		Pessoa usuario = iDaoPessoa.consultarUsuario(pessoa.getLogin(), pessoa.getSenha());
		
		if(usuario != null) {
			//adicionar usuario na sessão "usuarioLogado"
			FacesContext context = FacesContext.getCurrentInstance();
			ExternalContext externalContext = context.getExternalContext();
			externalContext.getSessionMap().put("usuarioLogado", usuario);
			
			return "cadastrousuario.jsf";
		}
		return "index.jsf";
	}
	
	public boolean liberarAcesso(String perfil) {
		
		FacesContext context = FacesContext.getCurrentInstance();
		ExternalContext externalContext = context.getExternalContext();
		Pessoa usuario = (Pessoa) externalContext.getSessionMap().get("usuarioLogado");
		
		return usuario.getPerfilUser().equals(perfil);
	}
}
