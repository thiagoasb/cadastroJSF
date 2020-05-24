package br.com.thiago;

import javax.persistence.Persistence;

public class TesteJPA {
	
	//Teste inicial para verificar o bom funcionamento do JPA
	public static void main(String[] args) {
		Persistence.createEntityManagerFactory("projetojsf");
	}
}
