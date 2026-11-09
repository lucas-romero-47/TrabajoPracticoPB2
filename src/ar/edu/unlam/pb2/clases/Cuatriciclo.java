package ar.edu.unlam.pb2.clases;

import java.time.Duration;
import java.time.LocalDateTime;

import ar.edu.unlam.pb2.enums.MarcaEnum;
import ar.edu.unlam.pb2.exceptions.CascoAsignadoException;
import ar.edu.unlam.pb2.exceptions.DebeTenerCascoParaPoderCalcularSuCostoDeSalidaException;
import ar.edu.unlam.pb2.interfaces.Asignable;

public class Cuatriciclo extends Vehiculo implements Asignable {
	private Casco casco;

	public Cuatriciclo(String patente, MarcaEnum marca, LocalDateTime horaIngreso) {
		super(patente, marca, horaIngreso);
		this.casco = null;
	}

	@Override
	public Boolean asignarCasco(Casco casco) throws CascoAsignadoException {
		validarQueNoTengaCascoAsignado();
		setCasco(casco);
		return true;
	}

	public void validarQueNoTengaCascoAsignado() throws CascoAsignadoException {
		if (getCasco() != null)
			throw new CascoAsignadoException();
	}

	@Override
	public Double calcularCosto(LocalDateTime horaDeSalida)
			throws DebeTenerCascoParaPoderCalcularSuCostoDeSalidaException {
		validarQueTengaCascoAsignado();
		double minutosExactos = Duration.between(getHoraIngreso(), horaDeSalida).toMinutes();
		return (minutosExactos / 60.0) * 800.0;
	}

	public void validarQueTengaCascoAsignado() throws DebeTenerCascoParaPoderCalcularSuCostoDeSalidaException {
		if (getCasco() == null)
			throw new DebeTenerCascoParaPoderCalcularSuCostoDeSalidaException();
	}

	public Casco getCasco() {
		return casco;
	}

	public void setCasco(Casco casco) {
		this.casco = casco;
	}

	@Override
	public int compareTo(Vehiculo vehiculo) {
		return getPatente().compareTo(vehiculo.getPatente());
	}
}