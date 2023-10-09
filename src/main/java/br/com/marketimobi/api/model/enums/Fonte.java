package br.com.marketimobi.api.model.enums;

public enum Fonte {
	
	ARIAL("Arial"),
	CALIBRI("Calibri"),
	BRUSH_SCRIPT("Brush Script"),
	TAHOMA("Tahoma"),
	TIMES_NEW_ROMAN("Times New Roman");
	
	private final String descricao;
	
	Fonte(String descricao) {
		this.descricao = descricao;
	}

	public String getDescricao() {
		return descricao;
	}
	
	public Fonte getEnumByDescricao(String descricao) {
		Fonte fonteEncontrada = null;
		for (Fonte f : Fonte.values()) {
			if(f.getDescricao().equals(descricao)) {
				fonteEncontrada = f;
				break;
			}
		}
		return fonteEncontrada;
	}

}
