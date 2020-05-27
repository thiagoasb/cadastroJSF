package br.com.thiago;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import javax.xml.bind.DatatypeConverter;

import com.google.gson.Gson;

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
	
	private Part arquivofoto;
	
	public String salvar() throws IOException{
		
		/*Processar imagem*/
		byte[] imagem = getByte(arquivofoto.getInputStream());
		pessoa.setFotoIconBase64Original(imagem); /*salva imagem original*/
		
		/*transformer em bufferimage - fluxo de um tipo manipulavel*/
		BufferedImage bufferedImage = ImageIO.read(new ByteArrayInputStream(imagem));
		
		/*Pega o tipo da imagem*/
		int type = bufferedImage.getType() == 0? 
				BufferedImage.TYPE_INT_ARGB : bufferedImage.getType();
		
		int largura = 200;
		int altura = 200;
		
		/*criar a miniatura*/
		BufferedImage resizedImage = new BufferedImage(altura, altura, type);
		Graphics2D g = resizedImage.createGraphics();
		g.drawImage(bufferedImage, 0, 0, largura, altura, null);
		g.dispose();
		
		/*Escrever novamente a imagem em tamanho menor*/
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		String extensao = arquivofoto.getContentType().split("\\/")[1]; /* image/png */
		ImageIO.write(resizedImage, extensao, baos);
		
		String miniImagem = "data:" + arquivofoto.getContentType() + ";base64," + 
				DatatypeConverter.printBase64Binary(baos.toByteArray());
		
		/*Processar imagem*/
		pessoa.setFotoIconBase64(miniImagem);
		pessoa.setExtensao(extensao);
		
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
	
	public String deslogar() {
		
		FacesContext context = FacesContext.getCurrentInstance();
		ExternalContext externalContext = context.getExternalContext();
		externalContext.getSessionMap().remove("usuarioLogado");
		
		HttpServletRequest httpServletRequest = (HttpServletRequest) context
				.getCurrentInstance().getExternalContext().getRequest();
		
		httpServletRequest.getSession().invalidate();
		
		return "index.jsf";
	}
	
	public boolean liberarAcesso(String perfil) {
		
		FacesContext context = FacesContext.getCurrentInstance();
		ExternalContext externalContext = context.getExternalContext();
		Pessoa usuario = (Pessoa) externalContext.getSessionMap().get("usuarioLogado");
		
		return usuario.getPerfilUser().equals(perfil);
	}
	
	public void pesquisaCep(AjaxBehaviorEvent event) {
		
		try {
			
			URL url = new URL("https://viacep.com.br/ws/" + pessoa.getCep() +"/json/");
			
			URLConnection connection = url.openConnection();
			InputStream is = connection.getInputStream();
			BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
			
			String cep = "";
			StringBuilder jsonCep = new StringBuilder();
			
			while ((cep = br.readLine()) != null) {
				jsonCep.append(cep);
			}
			
			Pessoa gsonAux = new Gson().fromJson(jsonCep.toString(), Pessoa.class);
						
			pessoa.setLogradouro(gsonAux.getLogradouro());
			pessoa.setComplemento(gsonAux.getComplemento());
			pessoa.setBairro(gsonAux.getBairro());
			pessoa.setLocalidade(gsonAux.getLocalidade());
			pessoa.setUf(gsonAux.getUf());
			pessoa.setUnidade(gsonAux.getUnidade());
			pessoa.setIbge(gsonAux.getIbge());
			pessoa.setGia(gsonAux.getGia());
			
			//System.out.println(jsonCep);
			
		}catch (Exception e) {
			mostrarMsg("Erro ao consultar o CEP!");
			e.printStackTrace();
		}
	}
	
	public void setArquivofoto(Part arquivofoto) {
		this.arquivofoto = arquivofoto;
	}
	public Part getArquivofoto() {
		return arquivofoto;
	}
	 /*Método que converte InputStream para array de bytes*/
	private byte[] getByte(InputStream is) throws IOException{
		
		int len;
		int size = 1024;
		byte[] buffer = null;
		
		if(is instanceof ByteArrayInputStream) {
			size = is.available();
			buffer = new byte[size];
			len = is.read(buffer, 0, size);
		} else {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			buffer = new byte[size];
			
			while ((len = is.read(buffer, 0, size)) != -1) {
				bos.write(buffer, 0, size);
			}
			
			buffer = bos.toByteArray();
		}
		
		return buffer;
	}
	
	public void download() throws IOException {
		Map<String, String> params = FacesContext.getCurrentInstance().getExternalContext()
				.getRequestParameterMap();
		String fileDownloadId = params.get("fileDownloadId");
		
		Pessoa pessoa = daoGeneric.consultar(Pessoa.class, fileDownloadId);
		
		HttpServletResponse response = (HttpServletResponse) FacesContext.getCurrentInstance()
				.getExternalContext().getResponse();
		response.addHeader("Content-Disposition", "attachment; filename=download." 
				+ pessoa.getExtensao());
		response.setContentType("application/octet-stream");
		response.setContentLength(pessoa.getFotoIconBase64Original().length);
		response.getOutputStream().write(pessoa.getFotoIconBase64Original());
		response.getOutputStream().flush();
		
		FacesContext.getCurrentInstance().responseComplete();
		
	}
}
