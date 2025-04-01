package gei.id.tutelado;

import gei.id.tutelado.configuracion.Configuracion;
import gei.id.tutelado.configuracion.ConfiguracionJPA;
import gei.id.tutelado.dao.EntradaLogDao;
import gei.id.tutelado.dao.EntradaLogDaoJPA;
import gei.id.tutelado.dao.UsuarioDao;
import gei.id.tutelado.dao.UsuarioDaoJPA;
import gei.id.tutelado.model.EntradaLog;
import gei.id.tutelado.model.Usuario;

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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.LazyInitializationException;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class P02_Usuarios_Entradas {

    private Logger log = LogManager.getLogger("gei.id.tutelado");

    private static ProdutorDatosProba produtorDatos = new ProdutorDatosProba();
    
    private static Configuracion cfg;
    private static UsuarioDao usuDao;
    private static EntradaLogDao logDao;
    
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

    	usuDao = new UsuarioDaoJPA();
    	logDao = new EntradaLogDaoJPA();
    	usuDao.setup(cfg);
    	logDao.setup(cfg);
    	
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
    public void test01_Recuperacion() {
   	
    	EntradaLog e;
    	
    	log.info("");	
		log.info("Configurando situación de partida do test -----------------------------------------------------------------------");

		produtorDatos.creaUsuariosConEntradasLog();
    	produtorDatos.gravaUsuarios();


		log.info("Inicio do test --------------------------------------------------------------------------------------------------");
    	log.info("Obxectivo: Proba da recuperación (por codigo) de entradas de log soltas\n"   
		+ "\t\t\t\t Casos contemplados:\n"
		+ "\t\t\t\t a) Recuperación por codigo existente\n"
		+ "\t\t\t\t b) Recuperacion por codigo inexistente\n");     	

    	// Situación de partida:
    	// u1, e1A, e1B desligados
    	
		log.info("Probando recuperacion por codigo EXISTENTE --------------------------------------------------");

    	e = logDao.recuperaPorCodigo(produtorDatos.e1A.getCodigo());

    	Assert.assertEquals (produtorDatos.e1A.getCodigo(),     e.getCodigo());
    	Assert.assertEquals (produtorDatos.e1A.getDescricion(), e.getDescricion());
    	Assert.assertEquals (produtorDatos.e1A.getDataHora(),   e.getDataHora());

    	log.info("");	
		log.info("Probando recuperacion por codigo INEXISTENTE --------------------------------------------------");
    	
    	e = logDao.recuperaPorCodigo("iwbvyhuebvuwebvi");
    	Assert.assertNull (e);

    } 	

    @Test
    public void test02_Alta() {


    	log.info("");	
		log.info("Configurando situación de partida do test -----------------------------------------------------------------------");

		produtorDatos.creaUsuariosSoltos();
    	produtorDatos.gravaUsuarios();
    	produtorDatos.creaEntradasLogSoltas();

    	log.info("");	
		log.info("Inicio do test --------------------------------------------------------------------------------------------------");
    	log.info("Obxectivo: Proba da gravación de entradas de log soltas\n"   
    			+ "\t\t\t\t Casos contemplados:\n"
    			+ "\t\t\t\t a) Primeira entrada de log vinculada a un usuario\n"
    			+ "\t\t\t\t b) Nova entrada de log para un usuario con entradas previas\n");     	

    	// Situación de partida:
    	// u1 desligado    	
    	// e1A, e1B transitorios

    	produtorDatos.u1.engadirEntradaLog(produtorDatos.e1A);
		
    	log.info("");	
		log.info("Gravando primeira entrada de log dun usuario --------------------------------------------------------------------");
    	Assert.assertNull(produtorDatos.e1A.getId());
    	logDao.almacena(produtorDatos.e1A);
    	Assert.assertNotNull(produtorDatos.e1A.getId());

    	produtorDatos.u1.engadirEntradaLog(produtorDatos.e1B);

    	log.info("");	
		log.info("Gravando segunda entrada de log dun usuario ---------------------------------------------------------------------");
    	Assert.assertNull(produtorDatos.e1B.getId());
    	logDao.almacena(produtorDatos.e1B);
    	Assert.assertNotNull(produtorDatos.e1B.getId());

    }

    @Test 
    public void test03_Eliminacion() {
    	
    	log.info("");	
		log.info("Configurando situación de partida do test -----------------------------------------------------------------------");

    	produtorDatos.creaUsuariosConEntradasLog();
    	produtorDatos.gravaUsuarios();

    	log.info("");	
		log.info("Inicio do test --------------------------------------------------------------------------------------------------");
    	log.info("Obxectivo: Proba de eliminación de entrada de log solta (asignada a usuario)\n");
    	
    	// Situación de partida:
    	// e1A desligado

		Assert.assertNotNull(logDao.recuperaPorCodigo(produtorDatos.e1A.getCodigo()));
    	logDao.elimina(produtorDatos.e1A);    	
		Assert.assertNull(logDao.recuperaPorCodigo(produtorDatos.e1A.getCodigo()));

    } 	
    
    @Test 
    public void test04_Modificacion() {

    	EntradaLog e1, e2;
    	String novaDescricion;
    	
    	log.info("");	
		log.info("Configurando situación de partida do test -----------------------------------------------------------------------");
  
		produtorDatos.creaUsuariosConEntradasLog();
    	produtorDatos.gravaUsuarios();

    	log.info("");	
		log.info("Inicio do test --------------------------------------------------------------------------------------------------");
    	log.info("Obxectivo: Proba de modificación da información dunha entrada de log solta\n");
 
    	
    	// Situación de partida:
    	// e1A desligado
    	
		novaDescricion = new String ("Descricion nova");

		e1 = logDao.recuperaPorCodigo(produtorDatos.e1A.getCodigo());

		Assert.assertNotEquals(novaDescricion, e1.getDescricion());
    	e1.setDescricion(novaDescricion);

    	logDao.modifica(e1);    	
    	
		e2 = logDao.recuperaPorCodigo(produtorDatos.e1A.getCodigo());
		Assert.assertEquals (novaDescricion, e2.getDescricion());

    	// NOTA: Non probamos modificación de usuario da entrada porque non ten sentido no dominio considerado

    }

	@Test
	public void test05_LAZY() {

		Usuario u;
		EntradaLog e;
		Boolean excepcion;

		log.info("");
		log.info("Configurando situación de partida do test -----------------------------------------------------------------------");

		produtorDatos.creaUsuariosConEntradasLog();
		produtorDatos.gravaUsuarios();

		log.info("Inicio do test --------------------------------------------------------------------------------------------------");
		log.info("Obxectivo: Proba da recuperación de propiedades LAZY\n"
				+ "\t\t\t\t Casos contemplados:\n"
				+ "\t\t\t\t a) Recuperación de usuario con colección (LAZY) de entradas de log \n"
				+ "\t\t\t\t b) Carga forzada de colección LAZY da dita coleccion\n"
				+ "\t\t\t\t c) Recuperacion de entrada de log solta con referencia (EAGER) a usuario\n");

		// Situación de partida:
		// u1, e1A, e1B desligados

		log.info("Probando (excepcion tras) recuperacion LAZY ---------------------------------------------------------------------");

		u = usuDao.recuperaPorNif(produtorDatos.u1.getNif());
		log.info("Acceso a entradas de log de usuario");
		try	{
			Assert.assertEquals(2, u.getEntradasLog().size());
			Assert.assertEquals(produtorDatos.e1A, u.getEntradasLog().first());
			Assert.assertEquals(produtorDatos.e1B, u.getEntradasLog().last());
			excepcion=false;
		} catch (LazyInitializationException ex) {
			excepcion=true;
			log.info(ex.getClass().getName());
		};
		Assert.assertTrue(excepcion);

		log.info("");
		log.info("Probando carga forzada de coleccion LAZY ------------------------------------------------------------------------");

		u = usuDao.recuperaPorNif(produtorDatos.u1.getNif());   // Usuario u con proxy sen inicializar
		u = usuDao.restauraEntradasLog(u);						// Usuario u con proxy xa inicializado

		Assert.assertEquals(2, u.getEntradasLog().size());
		Assert.assertEquals(produtorDatos.e1A, u.getEntradasLog().first());
		Assert.assertEquals(produtorDatos.e1B, u.getEntradasLog().last());
/*
		log.info("");
		log.info("Probando acceso a referencia EAGER ------------------------------------------------------------------------------");

		e = logDao.recuperaPorCodigo(produtorDatos.e1A.getCodigo());
		Assert.assertEquals(produtorDatos.u1, e.getUsuario());
*/
	}

	@Test
    public void test06_EAGER() {
    	
    	EntradaLog e;
    	Boolean excepcion;
    	
    	log.info("");	
		log.info("Configurando situación de partida do test -----------------------------------------------------------------------");

		produtorDatos.creaUsuariosConEntradasLog();
    	produtorDatos.gravaUsuarios();

		log.info("Inicio do test --------------------------------------------------------------------------------------------------");
    	log.info("Obxectivo: Proba da recuperación de propiedades EAGER\n");   

    	// Situación de partida:
    	// u1, e1A, e1B desligados
    	
		log.info("Probando (que non hai excepcion tras) acceso inicial a propiedade EAGER fora de sesion ----------------------------------------");
    	
    	e = logDao.recuperaPorCodigo(produtorDatos.e1A.getCodigo());  
		log.info("Acceso a usuario de entrada de log");
    	try	{
        	Assert.assertEquals(produtorDatos.u1, e.getUsuario());
        	excepcion=false;
    	} catch (LazyInitializationException ex) {
    		excepcion=true;
    		log.info(ex.getClass().getName());
    	};    	
    	Assert.assertFalse(excepcion);    
    } 	

/*
	@Test
	public void test07_Propagacion_Persist() {


		log.info("");
		log.info("Configurando situación de partida do test -----------------------------------------------------------------------");

		produtorDatos.creaUsuariosSoltos();
		produtorDatos.creaEntradasLogSoltas();
		produtorDatos.u1.engadirEntradaLog(produtorDatos.e1A);
		produtorDatos.u1.engadirEntradaLog(produtorDatos.e1B);


		log.info("");
		log.info("Inicio do test --------------------------------------------------------------------------------------------------");
		log.info("Obxectivo: Proba da gravación de novo usuario con entradas (novas) de log asociadas\n");

		// Situación de partida:
		// u1, e1A, e1B transitorios

		Assert.assertNull(produtorDatos.u1.getId());
		Assert.assertNull(produtorDatos.e1A.getId());
		Assert.assertNull(produtorDatos.e1B.getId());

		log.info("Gravando na BD usuario con entradas de log ----------------------------------------------------------------------");

		// Aqui o persist sobre u1 debe propagarse a e1A e e1B
		usuDao.almacena(produtorDatos.u1);

		Assert.assertNotNull(produtorDatos.u1.getId());
		Assert.assertNotNull(produtorDatos.e1A.getId());
		Assert.assertNotNull(produtorDatos.e1B.getId());
	}
*/
	@Test
	public void test07_Propagacion_Remove() {

		log.info("");
		log.info("Configurando situación de partida do test -----------------------------------------------------------------------");

		produtorDatos.creaUsuariosConEntradasLog();
		produtorDatos.gravaUsuarios();

		log.info("");
		log.info("Inicio do test --------------------------------------------------------------------------------------------------");
		log.info("Obxectivo: Proba de eliminación de de usuario con entradas de log asociadas\n");

		// Situación de partida:
		// u1, e1A, e1B desligados

		Assert.assertNotNull(usuDao.recuperaPorNif(produtorDatos.u1.getNif()));
		Assert.assertNotNull(logDao.recuperaPorCodigo(produtorDatos.e1A.getCodigo()));
		Assert.assertNotNull(logDao.recuperaPorCodigo(produtorDatos.e1B.getCodigo()));

		// Aqui o remove sobre u1 debe propagarse a e1A e e1B
		usuDao.elimina(produtorDatos.u1);

		Assert.assertNull(usuDao.recuperaPorNif(produtorDatos.u1.getNif()));
		Assert.assertNull(logDao.recuperaPorCodigo(produtorDatos.e1A.getCodigo()));
		Assert.assertNull(logDao.recuperaPorCodigo(produtorDatos.e1B.getCodigo()));

	}
/*
	@Test
    public void test09_Excepcions() {
    	
    	Boolean excepcion;
    	
    	log.info("");	
		log.info("Configurando situación de partida do test -----------------------------------------------------------------------");

		produtorDatos.creaUsuariosSoltos();
		produtorDatos.gravaUsuarios();
		produtorDatos.creaEntradasLogSoltas();		
		produtorDatos.u1.engadirEntradaLog(produtorDatos.e1A);		
		logDao.almacena(produtorDatos.e1A);
		
    	log.info("");	
		log.info("Inicio do test --------------------------------------------------------------------------------------------------");
    	log.info("Obxectivo: Proba de violacion de restricions not null e unique\n"   
    			+ "\t\t\t\t Casos contemplados:\n"
    			+ "\t\t\t\t a) Gravación de entrada con usuario nulo\n"
    			+ "\t\t\t\t b) Gravación de entrada con codigo nulo\n"
    			+ "\t\t\t\t c) Gravación de entrada con codigo duplicado\n");

    	// Situación de partida:
    	// u0, u1 desligados
    	// e1A desligado, e1B transitorio (e sen usuario asociado)
    	
		log.info("Probando gravacion de entrada con usuario nulo ------------------------------------------------------------------");
    	try {
    		logDao.almacena(produtorDatos.e1B);
        	excepcion=false;
    	} catch (Exception ex) {
    		excepcion=true;
    		log.info(ex.getClass().getName());
    	}
    	Assert.assertTrue(excepcion);

    	// Ligar entrada a usuario para poder probar outros erros
		produtorDatos.u1.engadirEntradaLog(produtorDatos.e1B);
    	    	
    	log.info("");	
		log.info("Probando gravacion de entrada con codigo nulo -------------------------------------------------------------------");
		produtorDatos.e1B.setCodigo(null);
    	try {
        	logDao.almacena(produtorDatos.e1B);
        	excepcion=false;
    	} catch (Exception ex) {
    		excepcion=true;
    		log.info(ex.getClass().getName());
    	}
    	Assert.assertTrue(excepcion);

    	log.info("");	
		log.info("Probando gravacion de entrada con codigo duplicado --------------------------------------------------------------");
		produtorDatos.e1B.setCodigo(produtorDatos.e1A.getCodigo());
    	try {
        	logDao.almacena(produtorDatos.e1B);
        	excepcion=false;
    	} catch (Exception ex) {
    		excepcion=true;
    		log.info(ex.getClass().getName());
    	}
    	Assert.assertTrue(excepcion);

    } 	
*/
}