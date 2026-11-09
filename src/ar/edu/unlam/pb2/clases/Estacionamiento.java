package ar.edu.unlam.pb2.clases;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import ar.edu.unlam.pb2.enums.MedioDePagoEnum;
import ar.edu.unlam.pb2.exceptions.CascoAsignadoException;
import ar.edu.unlam.pb2.exceptions.CocheraInexistenteException;
import ar.edu.unlam.pb2.exceptions.CocheraNoEncontradaException;
import ar.edu.unlam.pb2.exceptions.CocheraOcupadaException;
import ar.edu.unlam.pb2.exceptions.DebeTenerCascoParaPoderCalcularSuCostoDeSalidaException;
import ar.edu.unlam.pb2.exceptions.NoHayVehiculosEstacionadosHaceMasDeMediaHoraException;
import ar.edu.unlam.pb2.exceptions.PatenteDuplicadaException;
import ar.edu.unlam.pb2.exceptions.VehiculoInvalidoParaAsignarCasco;
import ar.edu.unlam.pb2.exceptions.VehiculoNoSeEncuentraEnCochera;
import ar.edu.unlam.pb2.exceptions.VehiculoNuloException;
import ar.edu.unlam.pb2.interfaces.Asignable;

public class Estacionamiento {

	private Map<Integer, Vehiculo> cocheras;
	private List<Pago> pagos;

	public Estacionamiento() {
		this.cocheras = new HashMap<Integer, Vehiculo>();
		this.pagos = new ArrayList<>();
	}

	public Boolean estacionarVehiculo(Integer idCochera, Vehiculo vehiculo)
			throws CocheraOcupadaException, PatenteDuplicadaException, CocheraInexistenteException {
		validacionesAlEstacionar(idCochera, vehiculo);
		this.cocheras.put(idCochera, vehiculo);
		return true;
	}

	public Map<Integer, Vehiculo> cambiarVehiculoDeCochera(Integer idCocheraOrigen, Vehiculo vehiculo,
			Integer idCocheraDestino) throws CocheraOcupadaException, CocheraInexistenteException,
			VehiculoNoSeEncuentraEnCochera, VehiculoNuloException {
		validacionesAlCambiarVehiculoDeCochera(idCocheraOrigen, vehiculo, idCocheraDestino);
		Map<Integer, Vehiculo> cocheraDeOrigen = new HashMap<Integer, Vehiculo>();
		cocheras.put(idCocheraDestino, vehiculo);
		cocheras.replace(idCocheraOrigen, null);
		cocheraDeOrigen.put(idCocheraOrigen, null);
		return cocheraDeOrigen;
	}

	public Boolean asignarCascoAUnTipoDeVehiculo(Vehiculo vehiculo, Casco casco)
			throws CascoAsignadoException, VehiculoInvalidoParaAsignarCasco {
		if (vehiculo instanceof Asignable)
			return ((Asignable) vehiculo).asignarCasco(casco);
		throw new VehiculoInvalidoParaAsignarCasco();
	}

	public Pago calcularPago(Integer idCochera, LocalDateTime fechaSalida, Vehiculo vehiculo,
			MedioDePagoEnum medioDePago)
			throws VehiculoNoSeEncuentraEnCochera, DebeTenerCascoParaPoderCalcularSuCostoDeSalidaException {
		validarVehiculoEstacionadoEnCochera(idCochera, vehiculo);
		Double costo = vehiculo.calcularCosto(fechaSalida);
		Double costoFinal = aplicarDescuesto(medioDePago, costo);
		Pago pago = new Pago(vehiculo, costoFinal, medioDePago, fechaSalida);
		cocheras.replace(idCochera, null);
		return pago;
	}

	private Double aplicarDescuesto(MedioDePagoEnum medioDePago, Double costo) {
		Double montoFinal = costo;
		if (MedioDePagoEnum.EFECTIVO.equals(medioDePago))
			montoFinal = (costo * 0.90);
		return montoFinal;
	}

	public Boolean realizarPago(Pago pago)
			throws DebeTenerCascoParaPoderCalcularSuCostoDeSalidaException, VehiculoNoSeEncuentraEnCochera {
		return this.pagos.add(pago);
	}

	public Map<Integer, Vehiculo> buscarCochera(Integer idCochera) throws CocheraNoEncontradaException {
		Map<Integer, Vehiculo> cocheraLiberada = new HashMap<Integer, Vehiculo>();
		if (cocheras.containsKey(idCochera)) {
			cocheraLiberada.put(idCochera, cocheras.get(idCochera));
			return cocheraLiberada;
		}
		throw new CocheraNoEncontradaException();
	}

	public Map<String, Double> generarReporteDeLaRecaudacion() {
		Map<String, Double> reporteDeLaRecaudacion = new HashMap<String, Double>();
		Double costoAuto = 0.0;
		Double costoMoto = 0.0;
		Double costoCamioneta = 0.0;
		Double costoCuatri = 0.0;
		for (Pago pago : pagos) {
			if (pago.getVehiculo() instanceof Moto)
				costoMoto += pago.getMonto();
			if (pago.getVehiculo() instanceof Cuatriciclo)
				costoCuatri += pago.getMonto();
			if (pago.getVehiculo() instanceof Auto)
				costoAuto += pago.getMonto();
			if (pago.getVehiculo() instanceof Camioneta)
				costoCamioneta += pago.getMonto();
		}
		reporteDeLaRecaudacion.put("Auto", costoAuto);
		reporteDeLaRecaudacion.put("Moto", costoMoto);
		reporteDeLaRecaudacion.put("Camioneta", costoCamioneta);
		reporteDeLaRecaudacion.put("Cuatriciclo", costoCuatri);
		return reporteDeLaRecaudacion;
	}

	public List<Vehiculo> obtenerVehiculosEstacionadosEntreDosFechas(LocalDateTime fechaDeInicio,
			LocalDateTime fechaDeFin) {
		List<Vehiculo> vehiculosEncontrados = new ArrayList<Vehiculo>();
		for (Vehiculo vehiculo : cocheras.values()) {
			if (fechaDeInicio.isBefore(vehiculo.getHoraIngreso()) && fechaDeFin.isAfter(vehiculo.getHoraIngreso()))
				vehiculosEncontrados.add(vehiculo);
		}
		return vehiculosEncontrados;
	}

	// VALIDACIONES

	public void validacionesAlEstacionar(Integer idCochera, Vehiculo vehiculo)
			throws PatenteDuplicadaException, CocheraOcupadaException, CocheraInexistenteException {
		validarPatenteDuplicada(vehiculo);
		validarCocheraOcupada(idCochera);
		validarCocheraInexistente(idCochera);
	}

	public void validarPatenteDuplicada(Vehiculo vehiculo) throws PatenteDuplicadaException {
		for (Vehiculo vehiculoEncontrado : cocheras.values()) {
			if (vehiculoEncontrado.getPatente().equals(vehiculo.getPatente()))
				throw new PatenteDuplicadaException();
		}
	}

	public void validarCocheraOcupada(Integer idCochera) throws CocheraOcupadaException {
		if (cocheras.containsKey(idCochera))
			throw new CocheraOcupadaException();
	}

	public void validarCocheraInexistente(Integer idCochera) throws CocheraInexistenteException {
		if (idCochera <= 0 || idCochera > 100)
			throw new CocheraInexistenteException();
	}

	public void validacionesAlCambiarVehiculoDeCochera(Integer idCocheraOrigen, Vehiculo vehiculo,
			Integer idCocheraDestino) throws CocheraInexistenteException, VehiculoNoSeEncuentraEnCochera,
			CocheraOcupadaException, VehiculoNuloException {
		validarCocheraInexistente(idCocheraOrigen);
		validarCocheraInexistente(idCocheraDestino);
		validarVehiculoEstacionadoEnCochera(idCocheraOrigen, vehiculo);
		validarCocheraOcupada(idCocheraDestino);
		validarVehiculoNoNulo(vehiculo);
	}

	private void validarVehiculoEstacionadoEnCochera(Integer idCocheraOrigen, Vehiculo vehiculo)
			throws VehiculoNoSeEncuentraEnCochera {
		if (cocheras.get(idCocheraOrigen) != vehiculo)
			throw new VehiculoNoSeEncuentraEnCochera();
	}

	private void validarVehiculoNoNulo(Vehiculo vehiculo) throws VehiculoNuloException {
		if (vehiculo.equals(null))
			throw new VehiculoNuloException();
	}

	public TreeSet<Vehiculo> ordenarVehiculosPorPatente() {
		TreeSet<Vehiculo> vehiculosOrdenadosPorPatente = new TreeSet<Vehiculo>();
		vehiculosOrdenadosPorPatente.addAll(cocheras.values());
		return vehiculosOrdenadosPorPatente;
	}

	public TreeSet<Vehiculo> ordenarVehiculosQueSuperanLaMediaHoraTiempoMayorAMenor() throws NoHayVehiculosEstacionadosHaceMasDeMediaHoraException {
		TreeSet<Vehiculo> vehiculosOrdenadosQueSuperanLaMediaHoraTiempoMayorAMenor = new TreeSet<Vehiculo>(
				new OrdenarVehiculosQueSuperanLaMediaHoraTiempoMayorAMenor());
		for (Vehiculo vehiculo : cocheras.values()) {
			if (Duration.between(vehiculo.getHoraIngreso(), LocalDateTime.now()).toMinutes() < 30) {
				throw new NoHayVehiculosEstacionadosHaceMasDeMediaHoraException();
			}
			if (Duration.between(vehiculo.getHoraIngreso(), LocalDateTime.now()).toMinutes() > 30)
				vehiculosOrdenadosQueSuperanLaMediaHoraTiempoMayorAMenor.add(vehiculo);
		}
		return vehiculosOrdenadosQueSuperanLaMediaHoraTiempoMayorAMenor;
	}

}