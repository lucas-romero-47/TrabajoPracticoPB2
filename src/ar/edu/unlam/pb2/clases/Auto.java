package ar.edu.unlam.pb2.clases;

import java.time.Duration;
import java.time.LocalDateTime;

import ar.edu.unlam.pb2.enums.MarcaEnum;

public class Auto extends Vehiculo {

	public Auto(String patente, MarcaEnum marca, LocalDateTime horaIngreso) {
		super(patente, marca, horaIngreso);
	}

	@Override
	public Double calcularCosto(LocalDateTime horaDeSalida) {
		double minutosExactos = Duration.between(getHoraIngreso(), horaDeSalida).toMinutes();
		return (minutosExactos / 60.0) * 1000.0;
	}
	
	@Override
	public int compareTo(Vehiculo vehiculo) {
		return getPatente().compareTo(vehiculo.getPatente());
	}
}