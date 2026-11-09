package ar.edu.unlam.pb2.clases;

import ar.edu.unlam.pb2.enums.TipoColor;

public class Casco {
	private Integer id;
	private TipoColor color;
	private String talle;

	public Casco(Integer id, TipoColor color, String talle) {
		this.id = id;
		this.color = color;
		this.talle = talle;
	}

}