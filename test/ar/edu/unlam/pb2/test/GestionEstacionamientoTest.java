package ar.edu.unlam.pb2.test;

import static org.junit.Assert.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.junit.Before;
import org.junit.Test;

import ar.edu.unlam.pb2.clases.*;
import ar.edu.unlam.pb2.enums.*;
import ar.edu.unlam.pb2.exceptions.*;

public class GestionEstacionamientoTest {

	private Estacionamiento estacionamiento;
	private Vehiculo autoA;
	private Vehiculo autoB;
	private Vehiculo camionetaA;
	private Vehiculo motoA;
	private Vehiculo cuatricicloA;
	private Casco cascoA;
	private Casco cascoB;

	@Before
	public void setUp() {
		estacionamiento = new Estacionamiento();
		autoA = new Auto("AA123AA", MarcaEnum.FORD, LocalDateTime.of(2026, 11, 1, 16, 0));
		camionetaA = new Camioneta("BB123BB", MarcaEnum.CHEVROLET, LocalDateTime.of(2026, 11, 2, 16, 0));
		motoA = new Moto("CC123CC", MarcaEnum.HONDA, LocalDateTime.of(2026, 11, 3, 16, 0));
		cuatricicloA = new Cuatriciclo("DD123DD", MarcaEnum.YAMAHA, LocalDateTime.of(2026, 11, 4, 16, 0));
		cascoA = new Casco(1, TipoColor.BLANCO, "M");
		cascoB = new Casco(2, TipoColor.AMARILLO, "L");
	}

	@Test
	public void deberiaEstacionarUnAutoEnUnaCocheraLibre() throws EstacionamientoExcepciones {
		Boolean seEstaciono = estacionamiento.estacionarVehiculo(01, autoA);
		assertTrue(seEstaciono);
	}

	@Test(expected = PatenteDuplicadaException.class)
	public void noDeberiaPermitirEstacionarDosVehiculosConLaMismaPatente() throws EstacionamientoExcepciones {
		autoB = new Auto("AA123AA", MarcaEnum.FORD, LocalDateTime.of(2026, 11, 1, 16, 0));
		estacionamiento.estacionarVehiculo(01, autoA);
		estacionamiento.estacionarVehiculo(02, autoB);
	}

	@Test(expected = CocheraOcupadaException.class)
	public void noDeberiaPermitirEstacionarEnUnaCocheraOcupada() throws EstacionamientoExcepciones {
		estacionamiento.estacionarVehiculo(01, autoA);
		estacionamiento.estacionarVehiculo(01, camionetaA);
	}

	@Test(expected = CocheraInexistenteException.class)
	public void noDeberiaPermitirEstacionarEnUnaCocheraQueNoExiste() throws EstacionamientoExcepciones {
		estacionamiento.estacionarVehiculo(-10, autoA);
	}

	@Test
	public void deberiaCambiarUnVehiculoDeCocheraLiberandoLaCocheraDeOrigen() throws EstacionamientoExcepciones {
		estacionamiento.estacionarVehiculo(01, autoA);
		Map<Integer, Vehiculo> cocheraDeOrigen = estacionamiento.cambiarVehiculoDeCochera(01, autoA, 02);
		assertEquals(cocheraDeOrigen.get(01), null);
	}

	@Test(expected = CocheraOcupadaException.class)
	public void noDeberiaPermitirCambiarDeCocheraAUnaOcupada() throws EstacionamientoExcepciones {
		estacionamiento.estacionarVehiculo(01, autoA);
		estacionamiento.estacionarVehiculo(02, camionetaA);
		estacionamiento.cambiarVehiculoDeCochera(01, autoA, 02);
	}

	@Test(expected = CocheraInexistenteException.class)
	public void noDeberiaPermitirCambiarVehiculoAUnaCocheraInexistente() throws EstacionamientoExcepciones {
		estacionamiento.estacionarVehiculo(01, autoA);
		estacionamiento.cambiarVehiculoDeCochera(01, autoA, -1);
	}

	@Test(expected = VehiculoNoSeEncuentraEnCochera.class)
	public void noDeberiaPermitirMoverUnvehiculoQueNoSeEncuentraEnLaCocheraOrigen() throws EstacionamientoExcepciones {
		estacionamiento.estacionarVehiculo(03, autoA);
		estacionamiento.cambiarVehiculoDeCochera(01, autoA, 03);
	}

	@Test
	public void deberiaAsignarUnCascoAUnaMoto() throws EstacionamientoExcepciones {
		Boolean seAsigno = estacionamiento.asignarCascoAUnTipoDeVehiculo(motoA, cascoA);
		assertTrue(seAsigno);
	}

	@Test
	public void deberiaAsignarUnCascoAUnCuadriciclo() throws EstacionamientoExcepciones {
		Boolean seAsigno = estacionamiento.asignarCascoAUnTipoDeVehiculo(cuatricicloA, cascoA);
		assertTrue(seAsigno);
	}

	@Test(expected = CascoAsignadoException.class)
	public void noDeberiaPermitirAsignarUnCascoAUnaMotoQueYaTieneUnCascoAsignado() throws EstacionamientoExcepciones {
		estacionamiento.asignarCascoAUnTipoDeVehiculo(cuatricicloA, cascoA);
		estacionamiento.asignarCascoAUnTipoDeVehiculo(cuatricicloA, cascoA);
	}

	@Test(expected = VehiculoInvalidoParaAsignarCasco.class)
	public void noDeberiaPermitirAsignarUnCascoAUnAuto() throws EstacionamientoExcepciones {
		estacionamiento.asignarCascoAUnTipoDeVehiculo(autoA, cascoA);
	}

	@Test
	public void deberiaCalcularseCorrectamenteElCostoDeEstacionamientoSegunElTipoDeVehiculo()
			throws EstacionamientoExcepciones {
		// AUTO
		estacionamiento.estacionarVehiculo(01, autoA);
		Double costoAuto = autoA.calcularCosto(LocalDateTime.of(2026, 11, 1, 18, 0));
		assertEquals(2000, costoAuto, 0.0);
		// CAMIONETA
		estacionamiento.estacionarVehiculo(02, camionetaA);
		Double costoCamioneta = camionetaA.calcularCosto(LocalDateTime.of(2026, 11, 2, 18, 0));
		assertEquals(3000, costoCamioneta, 0.0);
		// MOTO
		estacionamiento.estacionarVehiculo(03, motoA);
		estacionamiento.asignarCascoAUnTipoDeVehiculo(motoA, cascoA);
		Double costoMoto = motoA.calcularCosto(LocalDateTime.of(2026, 11, 3, 18, 0));
		assertEquals(1000, costoMoto, 0.0);
		// CUATRICICLO
		estacionamiento.estacionarVehiculo(04, cuatricicloA);
		estacionamiento.asignarCascoAUnTipoDeVehiculo(cuatricicloA, cascoB);
		Double costoCuatriciclo = cuatricicloA.calcularCosto(LocalDateTime.of(2026, 11, 4, 18, 0));
		assertEquals(1600, costoCuatriciclo, 0.0);
	}

	@Test(expected = DebeTenerCascoParaPoderCalcularSuCostoDeSalidaException.class)
	public void noDeberiaPoderCalcularCostoDeVehiculoSiNoTieneUnCascoAsignado() throws EstacionamientoExcepciones {
		estacionamiento.estacionarVehiculo(04, cuatricicloA);
		LocalDateTime salidaCuatriciclo = LocalDateTime.of(2026, 11, 4, 18, 0);
		cuatricicloA.calcularCosto(salidaCuatriciclo);
	}

	@Test
	public void deberiaAplicarDescuentoAlPagarEnEfectivo() throws EstacionamientoExcepciones {
		LocalDateTime salidaAuto = LocalDateTime.of(2026, 11, 1, 18, 0);
		estacionamiento.estacionarVehiculo(01, autoA);
		Pago pago = estacionamiento.calcularPago(01, salidaAuto, autoA, MedioDePagoEnum.EFECTIVO);
		estacionamiento.realizarPago(pago);
		assertEquals(pago.getMonto(), 1800, 0.0);
	}

	@Test
	public void deberiaRegistrarUnPagoEnLaListaDePagosAlPagarUnaCochera() throws EstacionamientoExcepciones {
		LocalDateTime salidaAuto = LocalDateTime.of(2026, 11, 1, 18, 0);
		estacionamiento.estacionarVehiculo(01, autoA);
		Pago pago = estacionamiento.calcularPago(01, salidaAuto, autoA, MedioDePagoEnum.TARJETA);
		Boolean sePago = estacionamiento.realizarPago(pago);
		assertTrue(sePago);
	}

	@Test
	public void deberiaLiberarLaCocheraAlPagarElVehiculo() throws EstacionamientoExcepciones {
		LocalDateTime salidaAuto = LocalDateTime.of(2026, 11, 1, 18, 0);
		estacionamiento.estacionarVehiculo(01, autoA);
		Pago pago = estacionamiento.calcularPago(01, salidaAuto, autoA, MedioDePagoEnum.EFECTIVO);
		estacionamiento.realizarPago(pago);
		Map<Integer, Vehiculo> cocheraLiberada = estacionamiento.buscarCochera(01);
		assertEquals(cocheraLiberada.get(01), null);
	}

	@Test(expected = DebeTenerCascoParaPoderCalcularSuCostoDeSalidaException.class)
	public void noDeberiaPermitirPagarUnaMotoSinCascoAsignado() throws EstacionamientoExcepciones {
		LocalDateTime salidaAuto = LocalDateTime.of(2026, 11, 1, 18, 0);
		estacionamiento.estacionarVehiculo(01, motoA);
		Pago pago = estacionamiento.calcularPago(01, salidaAuto, motoA, MedioDePagoEnum.EFECTIVO);
		estacionamiento.realizarPago(pago);
	}

	@Test
	public void deberaGenerarReporteDeLaRecaudacionDeLosPagosRealizadosPorVehiculo() throws EstacionamientoExcepciones {
		// fechas de salidas
		LocalDateTime salidaAuto = LocalDateTime.of(2026, 11, 1, 18, 0);
		LocalDateTime salidaCamioneta = LocalDateTime.of(2026, 11, 2, 18, 0);
		LocalDateTime salidaMoto = LocalDateTime.of(2026, 11, 3, 18, 0);
		LocalDateTime salidaCuatriciclo = LocalDateTime.of(2026, 11, 4, 18, 0);

		// estacionar vehiculos
		estacionamiento.estacionarVehiculo(01, autoA);
		estacionamiento.estacionarVehiculo(02, camionetaA);
		estacionamiento.estacionarVehiculo(03, motoA);
		estacionamiento.estacionarVehiculo(04, cuatricicloA);

		// asignar cascos
		estacionamiento.asignarCascoAUnTipoDeVehiculo(motoA, cascoA);
		estacionamiento.asignarCascoAUnTipoDeVehiculo(cuatricicloA, cascoB);

		// calcular por cada tipo
		Pago pagoAuto = estacionamiento.calcularPago(01, salidaAuto, autoA, MedioDePagoEnum.TARJETA);
		Pago pagoCamioneta = estacionamiento.calcularPago(02, salidaCamioneta, camionetaA, MedioDePagoEnum.TARJETA);
		Pago pagoMoto = estacionamiento.calcularPago(03, salidaMoto, motoA, MedioDePagoEnum.TARJETA);
		Pago pagoCuatriciclo = estacionamiento.calcularPago(04, salidaCuatriciclo, cuatricicloA,
				MedioDePagoEnum.TARJETA);

		// realizar pagos
		estacionamiento.realizarPago(pagoAuto);
		estacionamiento.realizarPago(pagoCamioneta);
		estacionamiento.realizarPago(pagoMoto);
		estacionamiento.realizarPago(pagoCuatriciclo);

		// verificar pagos correspondientes
		Map<String, Double> reporteDeLaRecaudacion = estacionamiento.generarReporteDeLaRecaudacion();
		for (Map.Entry<String, Double> entry : reporteDeLaRecaudacion.entrySet()) {
			String key = entry.getKey();
			Double val = entry.getValue();
			switch (key) {
			case "Auto": {
				assertEquals(2000.0, val, 0.0);
				break;
			}
			case "Moto": {
				assertEquals(1000.0, val, 0.0);
				break;
			}
			case "Camioneta": {
				assertEquals(3000.0, val, 0.0);
				break;
			}
			case "Cuatriciclo": {
				assertEquals(1600.0, val, 0.0);
				break;
			}
			}
		}
	}

	@Test
	public void deberiaObtenerLosVehiculosEstacionadosEntreDosFechas() throws EstacionamientoExcepciones {
		// estacionamos vehiculos
		estacionamiento.estacionarVehiculo(01, autoA);
		estacionamiento.estacionarVehiculo(02, camionetaA);
		estacionamiento.estacionarVehiculo(03, motoA);
		estacionamiento.estacionarVehiculo(04, cuatricicloA);

		// definimos entre que fechas queremos consultar
		LocalDateTime fechaDeInicio = LocalDateTime.of(2026, 11, 1, 0, 0);
		LocalDateTime fechaDeFin = LocalDateTime.of(2026, 11, 4, 0, 0);
		// llamamos a la funcion y validamos su contenido
		List<Vehiculo> vehiculos = estacionamiento.obtenerVehiculosEstacionadosEntreDosFechas(fechaDeInicio,
				fechaDeFin);
		assertEquals(vehiculos.getFirst(), autoA);
		assertEquals(vehiculos.getLast(), motoA);
	}

	@Test
	public void deberiaObtenerLosVehiculosOrdenadosPorPatenteUsandoTreeSetYComparable()
			throws EstacionamientoExcepciones {
		estacionamiento.estacionarVehiculo(04, autoA);
		estacionamiento.estacionarVehiculo(02, camionetaA);
		estacionamiento.estacionarVehiculo(01, motoA);
		estacionamiento.estacionarVehiculo(03, cuatricicloA);
		TreeSet<Vehiculo> vehiculosOrdenadosPorPatente = estacionamiento.ordenarVehiculosPorPatente();
		assertEquals(vehiculosOrdenadosPorPatente.getFirst(), autoA);
		assertEquals(vehiculosOrdenadosPorPatente.getLast(), cuatricicloA);
	}

	@Test
	public void deberiaObtenerLosVehiculosQueSuperanLaMediaHoraOrdenadosPorTiempoDeMayorAMenorUsandoTreeSetYComparator()
			throws EstacionamientoExcepciones{
		estacionamiento.estacionarVehiculo(04, autoA);
		estacionamiento.estacionarVehiculo(02, camionetaA);
		estacionamiento.estacionarVehiculo(01, motoA);
		estacionamiento.estacionarVehiculo(03, cuatricicloA);
		TreeSet<Vehiculo> vehiculosOrdenadosQueSuperanLaMediaHoraTiempoMayorAMenor = estacionamiento
				.ordenarVehiculosQueSuperanLaMediaHoraTiempoMayorAMenor();
		assertEquals(vehiculosOrdenadosQueSuperanLaMediaHoraTiempoMayorAMenor.getFirst(), autoA);
		assertEquals(vehiculosOrdenadosQueSuperanLaMediaHoraTiempoMayorAMenor.getLast(), cuatricicloA);

	}
	@Test (expected =  NoHayVehiculosEstacionadosHaceMasDeMediaHoraException.class)
	public void deberiaLanzarVehiculoNoSeAEstacionadoEnElRangoDeLaMediaHora()
			throws EstacionamientoExcepciones{
		Vehiculo autoC = new Auto("CA123AA", MarcaEnum.FORD, LocalDateTime.now());
		estacionamiento.estacionarVehiculo(01, autoC);
		TreeSet<Vehiculo> vehiculosOrdenadosQueSuperanLaMediaHoraTiempoMayorAMenor = estacionamiento
				.ordenarVehiculosQueSuperanLaMediaHoraTiempoMayorAMenor();
	}

}
