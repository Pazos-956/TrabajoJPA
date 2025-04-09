package gei.id.tutelado;

import gei.id.tutelado.configuracion.Configuracion;
import gei.id.tutelado.configuracion.ConfiguracionJPA;
import gei.id.tutelado.dao.*;
import gei.id.tutelado.model.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.runners.MethodSorters;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;

import java.lang.Exception;
import java.util.Iterator;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class P03_Consultas {

    private Logger log = LogManager.getLogger("gei.id.tutelado");

    private static ProdutorDatosProba produtorDatos = new ProdutorDatosProba();
    
    private static Configuracion cfg;
    private static ContribuyenteDao contrDao;
    private static DeclaracionDao declDao;
	private static ImpuestoDao impDao;
    
    @Rule
    public TestRule watcher = new TestWatcher() {
       protected void starting(Description description) {
    	   log.info("");
    	   log.info("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");
    	   log.info("Iniciando test: " + description.getMethodName());
    	   log.info("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");
       }
       protected void finished(Description description) {
    	   log.info("");
    	   log.info("-----------------------------------------------------------------------------------------------------------------------------------------");
    	   log.info("Finalizado test: " + description.getMethodName());
    	   log.info("-----------------------------------------------------------------------------------------------------------------------------------------");
       }
    };
    
    
    @BeforeClass
    public static void init() throws Exception {
    	cfg = new ConfiguracionJPA();
    	cfg.start();

    	declDao = new DeclaracionDaoJPA();
    	impDao = new ImpuestoDaoJPA();
		contrDao = new ContribuyenteDaoJPA();
    	declDao.setup(cfg);
    	impDao.setup(cfg);
		contrDao.setup(cfg);
    	
    	produtorDatos = new ProdutorDatosProba();
    	produtorDatos.Setup(cfg);
    }
    
    @AfterClass
    public static void endclose() throws Exception {
    	cfg.endUp();    	
    }
    
	
	@Before
	public void setUp() throws Exception {		
		log.info("");	
		log.info("Limpando BD -----------------------------------------------------------------------------------------------------");
		produtorDatos.limpaBD();
	}

	@After
	public void tearDown() throws Exception {
	}	


	@Test
	public void test08_INNER_JOIN() {

		List<Declaracion> listaC;

		log.info("");
		log.info("Configurando situación de partida do test -----------------------------------------------------------------------");

		produtorDatos.crearDeclaracionesConContribuyente();
		produtorDatos.gravaContribuyentes();

		log.info("");
		log.info("Inicio do test --------------------------------------------------------------------------------------------------");
		log.info("Obxectivo: Proba da consulta recuperaDeclaracionesContribuyente\n");


		listaC = contrDao.recuperaDeclaracionesContribuyente(produtorDatos.pj1);
		Assert.assertEquals(0, listaC.size());

		listaC = contrDao.recuperaDeclaracionesContribuyente(produtorDatos.pf1);
		Assert.assertEquals(2, listaC.size());
		Assert.assertEquals(produtorDatos.d1, listaC.get(0));
		Assert.assertEquals(produtorDatos.d2, listaC.get(1));

	}
	@Test
	public void test08_OUTER_JOIN() {

		List<Declaracion> listaC;

		log.info("");
		log.info("Configurando situación de partida do test -----------------------------------------------------------------------");

		produtorDatos.crearDeclaracionesImpuestos();
		produtorDatos.gravaImpuestos();
		produtorDatos.gravaContribuyentes();



		log.info("");
		log.info("Inicio do test --------------------------------------------------------------------------------------------------");
		log.info("Obxectivo: Proba da consulta \n");


		listaC = declDao.recuperaDeclaracionesNulas();
		Assert.assertEquals(1, listaC.size());
		Assert.assertEquals(produtorDatos.d2, listaC.get(0));
		Assert.assertEquals(0,listaC.get(0).getImpuesto().size());

	}
	@Test
	public void test08_Subclase() {

		List<Contribuyente> listaC;

		log.info("");
		log.info("Configurando situación de partida do test -----------------------------------------------------------------------");

		produtorDatos.crearDeclaracionesConContribuyente();
		produtorDatos.gravaContribuyentes();

		log.info("");
		log.info("Inicio do test --------------------------------------------------------------------------------------------------");
		log.info("Obxectivo: Proba da consulta contribuyentesPorEstadoCivil\n");

		// Situación de partida:
		// c1, d1, d2 desligados

		listaC = contrDao.contribuyentesPorEstadoCivil("Soltero");
		Assert.assertEquals(1, listaC.size());

		Assert.assertEquals(produtorDatos.pf1, listaC.get(0));
		Assert.assertNotEquals(produtorDatos.pf2, listaC.get(0));

	}

	@Test
	public void test08_Agregacion() {

		List<Object> listaC;

		log.info("");
		log.info("Configurando situación de partida do test -----------------------------------------------------------------------");

		produtorDatos.crearImpuestos();
		produtorDatos.gravaImpuestos();

		log.info("");
		log.info("Inicio do test --------------------------------------------------------------------------------------------------");
		log.info("Obxectivo: Proba da consulta cantidadPorTipoImpuesto\n");


		listaC = impDao.cantidadPorTipoImpuesto();
		Assert.assertEquals(3, listaC.size());

		Iterator<Object> i = listaC.iterator();
		while (i.hasNext()) {
			Object[] valores = (Object[]) i.next();
			if (valores[0].equals(TipoImpuesto.IBI)) {
				Assert.assertEquals(1L, valores[1]);
			} else if (valores[0].equals(TipoImpuesto.IRPF)) {
				Assert.assertEquals(1L, valores[1]);
			} else if (valores[0].equals(TipoImpuesto.IVA)) {
				Assert.assertEquals(2L, valores[1]);
			}
		}
	}


}