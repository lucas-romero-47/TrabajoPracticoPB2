package ar.edu.unlam.pb2.clases;

import java.util.Comparator;

public class OrdenarVehiculosQueSuperanLaMediaHoraTiempoMayorAMenor implements Comparator<Vehiculo> {
	@Override
	public int compare(Vehiculo vehiculo1, Vehiculo vehiculo2) {
		return vehiculo1.getHoraIngreso().compareTo(vehiculo2.getHoraIngreso());
	}

}
