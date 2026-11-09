package ar.edu.unlam.pb2.clases;

import java.time.Duration;
import java.time.LocalDateTime;

import ar.edu.unlam.pb2.enums.MarcaEnum;

public class Camioneta extends Vehiculo {

	public Camioneta(String patente, MarcaEnum marca, LocalDateTime horaIngreso) {
		super(patente, marca, horaIngreso);
	}

	@Override
	public Double calcularCosto(LocalDateTime horaDeSalida) {
		Double recargo = 50.0;
		double minutosExactos = Duration.between(getHoraIngreso(), horaDeSalida).toMinutes();
		return (minutosExactos / 60.0) * 1000.0 * (1 + recargo / 100);
	}
	
	@Override
	public int compareTo(Vehiculo vehiculo) {
		return getPatente().compareTo(vehiculo.getPatente());
	}
}