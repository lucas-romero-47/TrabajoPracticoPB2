package ar.edu.unlam.pb2.clases;

import java.time.LocalDateTime;
import java.util.Objects;

import ar.edu.unlam.pb2.enums.MarcaEnum;
import ar.edu.unlam.pb2.exceptions.CascoAsignadoException;
import ar.edu.unlam.pb2.exceptions.DebeTenerCascoParaPoderCalcularSuCostoDeSalidaException;

public abstract class Vehiculo implements Comparable<Vehiculo> {

	private String patente;
	private MarcaEnum marca;
	private LocalDateTime horaIngreso;
	private LocalDateTime horaSalida;

	public Vehiculo(String patente, MarcaEnum marca, LocalDateTime horaIngreso) {
		this.patente = patente;
		this.marca = marca;
		this.horaIngreso = horaIngreso;
		this.horaSalida = null;
	}

	public String getPatente() {
		return patente;
	}

	public MarcaEnum getMarca() {
		return marca;
	}

	public LocalDateTime getHoraIngreso() {
		return horaIngreso;
	}

	public LocalDateTime getHoraSalida() {
		return horaSalida;
	}

	@Override
	public int hashCode() {
		return Objects.hash(patente);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		Vehiculo other = (Vehiculo) obj;
		return Objects.equals(patente, other.patente);
	}

	public abstract Double calcularCosto(LocalDateTime horaDeSalida)
			throws DebeTenerCascoParaPoderCalcularSuCostoDeSalidaException;

}