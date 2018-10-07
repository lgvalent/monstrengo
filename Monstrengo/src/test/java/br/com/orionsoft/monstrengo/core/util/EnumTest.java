package br.com.orionsoft.monstrengo.core.util;

public enum EnumTest {
	ENUM1(1l),
	ENUM2(2l);
	
		private Long id;
	
	private EnumTest(Long id) {
		this.id = id;
	}

	public Long getId() {
		return id;
	}
}
