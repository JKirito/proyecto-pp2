package grc.vista;

import grc.controlador.GRCControlador;
import grc.modelo.GRCModelo;
import grc.servicios.IdiomaElegido;

import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.Scanner;

public class GRCVistaTexto extends Thread implements Observer
{
	private GRCControlador controller;
	private GRCModelo modelo;
	private GRCVista vista;
	private boolean salirModoTxt;
	private static final String KEY_DATOS_ACTUALIZADOS = "MSJ_DATOS_ACTUALIZADOS";
	private static final String KEY_SIN_RECOMENDACIONES = "MSJ_SIN_RECOMENDACIONES_CON_FILTROS_SELECC";
	private static final String KEY_BIENVENIDA = "MSJ_BIENVENIDA";
	private static final String KEY_RECOS_DISPONIBLES = "MSJ_RECOS_DISPONIBLES";
	private static final String KEY_AYUDA = "MSJ_AYUDA";
	private static final String KEY_INDICAR_TURNOS = "MSJ_INDICAR_TURNOS";
	private static final String KEY_OPCION_FILTRO_MAÑANA = "MSJ_OPCION_FILTRO_MAÑANA";
	private static final String KEY_OPCION_FILTRO_TARDE = "MSJ_OPCION_FILTRO_TARDE";
	private static final String KEY_OPCION_FILTRO_NOCHE = "MSJ_OPCION_FILTRO_NOCHE";
	private static final String KEY_OPCION_QUITAR_FILTROS = "MSJ_OPCION_QUITAR_FILTROS";
	private static final String KEY_OPCION_MOSTRAR_LISTA_RECOS = "MSJ_OPCION_MOSTRAR_LISTA_RECOS";
	private static final String KEY_OPCION_ACTUALIZAR = "MSJ_OPCION_ACTUALIZAR";
	private static final String KEY_OPCION_GUI = "MSJ_OPCION_GUI";
	private static final String KEY_OPCION_AYUDA = "MSJ_OPCION_AYUDA";
	private static final String KEY_OPCION_SALIR = "MSJ_OPCION_SALIR";
	private static final String KEY_SALUDO_MODO_TEXTO = "MSJ_SALUDO_MODO_TEXTO";
	private static final String KEY_INGRESE_OPCION_CORRECTA = "MSJ_INGRESE_OPCION_CORRECTA";
	private static final String KEY_NO_HAY_RECOS_DISPONIBLES = "MSJ_NO_HAY_RECOS_DISPONIBLES";
	private static final String KEY_CAMBIAR_IDIOMA = "MSJ_CAMBIAR_IDIOMA";

	private IdiomaElegido labels;
	private boolean actualizeYo;
	private String ultimoMsg;

	public GRCVistaTexto(GRCControlador controlador, GRCModelo model, GRCVista vista)
	{
		this.controller = controlador;
		this.modelo = model;
		this.vista = vista;
		this.salirModoTxt = false;
		this.ultimoMsg = "";
		this.actualizeYo = true;
		this.labels = new IdiomaElegido();
	}

	@Override
	public void update(Observable o, Object arg)
	{
		if (o instanceof GRCModelo)
		{
			this.modelo = (GRCModelo) o;
		}
		if (this.actualizeYo)
		{
			this.actualizeYo = false;
			return;
		}
		mostrarMensaje(labels.getIdiomaMsjs().getString(KEY_DATOS_ACTUALIZADOS));
	}

	private void mensajeInicial()
	{
		List<String> listaRecomendaciones = this.modelo.getListaRecomendacionesSugeridas();
		if (listaRecomendaciones.isEmpty())
		{
			mostrarMensaje((labels.getIdiomaMsjs().getString(KEY_SIN_RECOMENDACIONES)));
			return;
		}

		mostrarMensaje(listaRecomendaciones.get(0));
		System.out.println();
		mostrarMensaje(labels.getIdiomaMsjs().getString(KEY_RECOS_DISPONIBLES) + " "
				+ listaRecomendaciones.size());
	}

	public void menuPrincipal() throws Exception
	{
		limpiarPantalla();
		mostrarMensaje(labels.getIdiomaMsjs().getString(KEY_BIENVENIDA).toUpperCase());
		System.out.println();
		voyAActualizarModelo();
		this.controller.filtrarTurnos();
		System.out.println();
		mensajeInicial();
		mensajeAyuda();
		seleccionarOpcion();
	}

	private void mensajeAyuda()
	{
		mostrarMensaje(labels.getIdiomaMsjs().getString(KEY_AYUDA));

	}

	private void mostrarAyuda()
	{
		System.out.println();
		System.out.println(labels.getIdiomaMsjs().getString(KEY_INDICAR_TURNOS));
		System.out.println("1 - " + labels.getIdiomaMsjs().getString(KEY_OPCION_FILTRO_MAÑANA));
		System.out.println("2 - " + labels.getIdiomaMsjs().getString(KEY_OPCION_FILTRO_TARDE));
		System.out.println("3 - " + labels.getIdiomaMsjs().getString(KEY_OPCION_FILTRO_NOCHE));
		System.out.println("4 - " + labels.getIdiomaMsjs().getString(KEY_OPCION_QUITAR_FILTROS));
		System.out.println("T - "
				+ labels.getIdiomaMsjs().getString(KEY_OPCION_MOSTRAR_LISTA_RECOS));
		System.out.println("R - " + labels.getIdiomaMsjs().getString(KEY_OPCION_ACTUALIZAR));
		System.out.println("V - " + labels.getIdiomaMsjs().getString(KEY_OPCION_GUI));
		System.out.println("H - " + labels.getIdiomaMsjs().getString(KEY_OPCION_AYUDA));
		System.out.println("I - " + labels.getIdiomaMsjs().getString(KEY_CAMBIAR_IDIOMA));
		System.out.println("0 - " + labels.getIdiomaMsjs().getString(KEY_OPCION_SALIR));
	}

	private void seleccionarOpcion()
	{
		String opcionUsuario = "x";
		boolean cerrarApp = false;
		while (!salirModoTxt)
		{
			opcionUsuario = getOpcionUsuario();
			switch (opcionUsuario) {
				case "1" :
					voyAActualizarModelo();
					controller.filtrarManiana(true);
					actualizar();
					break;
				case "2" :
					voyAActualizarModelo();
					controller.filtrarTarde(true);
					actualizar();
					break;
				case "3" :
					voyAActualizarModelo();
					controller.filtrarNoche(true);
					actualizar();
					break;
				case "4" :
					voyAActualizarModelo();
					controller.cambiarEstadoFiltroManiana(false);
					voyAActualizarModelo();
					controller.cambiarEstadoFiltroTarde(false);
					voyAActualizarModelo();
					controller.cambiarEstadoFiltroNoche(false);
					voyAActualizarModelo();
					controller.filtrarTurnos();
					actualizar();
					break;
				case "t" :
					mostrarListaRecomendaciones();
					break;
				case "T" :
					mostrarListaRecomendaciones();
					break;
				case "r" :
					actualizar();
					break;
				case "R" :
					actualizar();
					break;
				case "v" :
					ejecutarVista();
					break;
				case "V" :
					ejecutarVista();
					break;
				case "h" :
					mostrarAyuda();
					break;
				case "H" :
					mostrarAyuda();
					break;
				case "i" :
					cambiarIdioma();
					break;
				case "I" :
					cambiarIdioma();
					break;
				case "0" :
					mostrarMensaje(labels.getIdiomaMsjs().getString(KEY_SALUDO_MODO_TEXTO));
					cerrarApp = true;
					break;
				default :
					mostrarMensaje(labels.getIdiomaMsjs().getString(KEY_INGRESE_OPCION_CORRECTA));
					break;
			}
		}

		if (cerrarApp)
		{
			System.exit(0);
		} else if (!salirModoTxt)
		{
			// mensajeInicial();
			seleccionarOpcion();
		}
	}

	private void ejecutarVista()
	{
		this.vista.showVista();
	}

	private void mostrarListaRecomendaciones()
	{
		int i = 1;
		for (String r : this.modelo.getListaRecomendacionesSugeridas())
		{
			System.out.println(i + " - " + r);
			i++;
		}
		if (i == 1)
		{
			mostrarMensaje(labels.getIdiomaMsjs().getString(KEY_NO_HAY_RECOS_DISPONIBLES));
		}
	}

	private void actualizar()
	{
		mensajeInicial();
	}

	private String getOpcionUsuario()
	{
		mensajeAyuda();
		@SuppressWarnings("resource")
		String entradaTeclado = new Scanner(System.in).nextLine();
		return entradaTeclado;
	}

	private void limpiarPantalla()
	{
		System.out.println();
		System.out.println();
		System.out.println();
		System.out.println();
		System.out.println();
		System.out.println();
		System.out.println();
		System.out.println();
		System.out.println();
		System.out.println();
		System.out.println();
		System.out.println();
		System.out.println();
		System.out.println();
		System.out.println();
		System.out.println();
		System.out.println();
		System.out.println();
		System.out.println();
		System.out.println();
		System.out.println();
		System.out.println();
		System.out.println();
		System.out.println();
		System.out.println();
		System.out.println();
		System.out.println();
		System.out.println();
		System.out.println();
	}

	@Override
	public void run()
	{
		try
		{
			menuPrincipal();
		} catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void voyAActualizarModelo()
	{
		this.actualizeYo = true;
	}

	public void mostrarMensaje(String msg)
	{
		if (this.ultimoMsg.equals(msg))
			return;
		this.ultimoMsg = msg;
		System.out.println(msg);
	}

	private void cambiarIdioma()
	{
		this.labels.cambiarIdiomaMsjs();
	}
}
