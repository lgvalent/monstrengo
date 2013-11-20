package br.com.orionsoft.basic.entities.commons;

import java.util.Calendar;

public enum Frequencia {
	UNICA("Única", 0, Calendar.DATE),
	DIARIA("Diária", 1, Calendar.DATE),
	SEMANAL("Semanal", 7, Calendar.DATE),
	MENSAL("Mensal", 1, Calendar.MONTH),
	BIMESTRAL("Bimestral", 2, Calendar.MONTH),
	TRIMESTRAL("Trimestral", 3, Calendar.MONTH),
	QUADRIMESTRAL("Quadrimestral", 4, Calendar.MONTH),
	SEMESTRAL("Semestral", 6, Calendar.MONTH),
	ANUAL("Anual", 1, Calendar.YEAR);

	public static final int COLUMN_DISCRIMINATOR_LENGTH = 20;

	private String nome;
	private Integer quantidade;
	private Integer unidade;

	private Frequencia(String nome, Integer quantidade, Integer unidade) {
		this.nome = nome;
		this.quantidade = quantidade;
		this.unidade = unidade;
	}

	@Override
	public String toString() {
		return nome;
	}

	public String getNome() {
		return nome;
	}

	public Integer getQuantidade() {
		return quantidade;
	}

	public Integer getUnidade() {
		return unidade;
	}
}
///**
// * Para que os serviços e processos funcionem o seguinte script sql
// * deve ser executado no banco de dados:<br>
// * INSERT INTO `financeiro_frequencia` VALUES (1, 'Única', 0, 5);<br>
// * INSERT INTO `financeiro_frequencia` VALUES (2, 'Diária', 1, 5);<br>
// * INSERT INTO `financeiro_frequencia` VALUES (3, 'Semanal', 7, 5);<br>
// * INSERT INTO `financeiro_frequencia` VALUES (4, 'Mensal', 1, 2);<br>
// * INSERT INTO `financeiro_frequencia` VALUES (5, 'Bimestral', 2, 2);<br>
// * INSERT INTO `financeiro_frequencia` VALUES (6, 'Trimestral', 3, 2);<br>
// * INSERT INTO `financeiro_frequencia` VALUES (7, 'Semestral', 6, 2);<br>
// * INSERT INTO `financeiro_frequencia` VALUES (8, 'Anual', 1, 1);<br>
// */
//@Entity
//@Table(name = "basic_frequencia")
//public class Frequencia {
//	/*
//	 * Constantes com o nomes das propriedades da classe para serem usadas no
//	 * código e evitar erro de digitação.
//	 */
//	public static final String NOME = "nome";
//	public static final String QUANTIDADE = "quantidade";
//	public static final String UNIDADE = "unidade";
//
//	private Long id = -1l;
//	private String nome;
//	private Integer quantidade;
//	private Integer unidade;
//
//	@Id
//	@GeneratedValue(strategy = GenerationType.IDENTITY)
//	public Long getId() {
//		return id;
//	}
//
//	public void setId(Long id) {
//		this.id = id;
//	}
//
//	@Column(length = 50)
//	public String getNome() {
//		return nome;
//	}
//
//	public void setNome(String nome) {
//		this.nome = nome;
//	}
//
//	@Column
//	public Integer getQuantidade() {
//		return quantidade;
//	}
//
//	public void setQuantidade(Integer quantidade) {
//		this.quantidade = quantidade;
//	}
//
//	@Column
//	public Integer getUnidade() {
//		return unidade;
//	}
//
//	public void setUnidade(Integer unidade) {
//		this.unidade = unidade;
//	}
//
//	@Override
//	public String toString() {
//		return this.nome;
//	}
//}
