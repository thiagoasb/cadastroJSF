package br.com.thiago;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import br.com.thiago.dao.DaoGeneric;
import br.com.thiago.entidades.Lancamento;
import br.com.thiago.entidades.Pessoa;
import br.com.thiago.repository.IDaoLancamento;
import br.com.thiago.repository.IdaoLancamentoImpl;

@ViewScoped
@ManagedBean(name = "lancamentoBean")
public class LancamentoBean {
	
	private Lancamento lancamento = new Lancamento();
	private DaoGeneric<Lancamento> daoGeneric = new DaoGeneric<Lancamento>();
	private List<Lancamento> lancamentos = new ArrayList<Lancamento>();
	private IDaoLancamento daoLancamento = new IdaoLancamentoImpl();
	
	public String salvar() {
		
		FacesContext context = FacesContext.getCurrentInstance();
		ExternalContext externalContext = context.getExternalContext();
		Pessoa usuario = (Pessoa) externalContext.getSessionMap().get("usuarioLogado");
		lancamento.setUsuario(usuario);
		lancamento = daoGeneric.atualizar(lancamento);
		
		carregarLista();
		
		return "";
	}
	
	public void novo() {
		lancamento = new Lancamento();
	}
	
	public String novoBotao() {
		novo();
		return "";
	}
	
	public String remover() {
		daoGeneric.deletarPorID(lancamento);
		novo();
		carregarLista();
		return "";
	}
	
	@PostConstruct
	private void carregarLista() {
		FacesContext context = FacesContext.getCurrentInstance();
		ExternalContext externalContext = context.getExternalContext();
		Pessoa usuario = (Pessoa) externalContext.getSessionMap().get("usuarioLogado");
		
		lancamentos = daoLancamento.consultarUsuarioLogado(usuario.getId());
	}
	
	public Lancamento getLancamento() {
		return lancamento;
	}
	public void setLancamento(Lancamento lancamento) {
		this.lancamento = lancamento;
	}
	public DaoGeneric<Lancamento> getDaoGeneric() {
		return daoGeneric;
	}
	public void setDaoGeneric(DaoGeneric<Lancamento> daoGeneric) {
		this.daoGeneric = daoGeneric;
	}
	public List<Lancamento> getLancamentos() {
		return lancamentos;
	}
	public void setLancamentos(List<Lancamento> lancamentos) {
		this.lancamentos = lancamentos;
	}
	
	
}
