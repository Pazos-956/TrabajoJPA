package gei.id.tutelado;

import gei.id.tutelado.configuracion.Configuracion;
import gei.id.tutelado.configuracion.ConfiguracionJPA;
import gei.id.tutelado.dao.ContribuyenteDao;
import gei.id.tutelado.dao.ContribuyenteDaoJPA;
import gei.id.tutelado.dao.DeclaracionDao;
import gei.id.tutelado.dao.DeclaracionDaoJPA;
import gei.id.tutelado.model.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.LazyInitializationException;
import org.junit.*;
import org.junit.rules.TestRule;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;

public class P01_Contribuyente {
    private Logger log = LogManager.getLogger("gei.id.tutelado");

    private static ProdutorDatosProba produtorDatos = new ProdutorDatosProba();

    private static Configuracion cfg;
    private static ContribuyenteDao contribDao;
    private static DeclaracionDao declaracionDao;

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

        contribDao = new ContribuyenteDaoJPA();
        declaracionDao = new DeclaracionDaoJPA();

        contribDao.setup(cfg);
        declaracionDao.setup(cfg);

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
        log.info("Limpando BD --------------------------------------------------------------------------------------------");
        produtorDatos.limpaBD();
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void test01_Recuperacion() {

        Contribuyente pf, pj;
        pf = new PersonaFisica();
        pj = new PersonaJuridica();


        log.info("");
        log.info("Configurando situación de partida do test -----------------------------------------------------------------------");

        produtorDatos.creaContribuyentesSoltos();
        produtorDatos.gravaContribuyentes();

        log.info("");
        log.info("Inicio do test --------------------------------------------------------------------------------------------------");
        log.info("Obxectivo: Proba de recuperación desde a BD de contribuyentes (sen entradas asociadas) por nif\n"
                + "\t\t\t\t Casos contemplados:\n"
                + "\t\t\t\t a) Recuperación por nif existente\n"
                + "\t\t\t\t b) Recuperacion por nif inexistente\n");

        // Situación de partida:
        // u0 desligado

        log.info("Probando recuperacion por nif EXISTENTE --------------------------------------------------");

        pj = contribDao.recuperaPorNif(produtorDatos.c2.getNif());

        if (pj instanceof PersonaJuridica) {
            // Acceder a los métodos específicos de PersonaJurídica
            Assert.assertEquals(produtorDatos.c2.getNif(), pj.getNif());
            Assert.assertEquals(produtorDatos.c2.getNombre(), pj.getNombre());
            Assert.assertEquals(produtorDatos.c2.getDireccion(), pj.getDireccion());
            // Acceder a otros métodos específicos de PersonaJurídica
            Assert.assertEquals(produtorDatos.c2.getRazonSocial(), ((PersonaJuridica) pj).getRazonSocial());
            Assert.assertEquals(produtorDatos.c2.getFechaConstitucion(), ((PersonaJuridica) pj).getFechaConstitucion());

        }

        pf = contribDao.recuperaPorNif(produtorDatos.c1.getNif());

        if (pf instanceof PersonaFisica) {
            // Acceder a los métodos específicos de PersonaFisica
            Assert.assertEquals(produtorDatos.c1.getNif(), pf.getNif());
            Assert.assertEquals(produtorDatos.c1.getNombre(), pf.getNombre());
            Assert.assertEquals(produtorDatos.c1.getDireccion(), pf.getDireccion());
            // Acceder a otros métodos específicos de PersonaFisica
            Assert.assertEquals(produtorDatos.c1.getFechaNacimiento(), ((PersonaFisica) pf).getFechaNacimiento());
            Assert.assertEquals(produtorDatos.c1.getEstadoCivil(), ((PersonaFisica) pf).getEstadoCivil());

        }



        log.info("");
        log.info("Probando recuperacion por nif INEXISTENTE -----------------------------------------------");

        pf = contribDao.recuperaPorNif("iwbvyhuebvuwebvi");
        Assert.assertNull (pf);

    }

    @Test
    public void test03_Eliminacion() {

        log.info("");
        log.info("Configurando situación de partida do test -----------------------------------------------------------------------");

        produtorDatos.creaContribuyentesSoltos();
        produtorDatos.gravaContribuyentes();


        log.info("");
        log.info("Inicio do test --------------------------------------------------------------------------------------------------");
        log.info("Obxectivo: Proba de eliminación da BD de contribuyente sen entradas asociadas\n");

        // Situación de partida:
        // c1 desligado

        Assert.assertNotNull(contribDao.recuperaPorNif(produtorDatos.c1.getNif()));
        contribDao.elimina(produtorDatos.c1);
        Assert.assertNull(contribDao.recuperaPorNif(produtorDatos.c1.getNif()));
    }
    @Test
    public void test05_LAZY() {

        Contribuyente c;
        Declaracion d;
        Boolean excepcion;

        log.info("");
        log.info("Configurando situación de partida do test -----------------------------------------------------------------------");

        produtorDatos.crearContribuyentesconDeclaraciones();
        produtorDatos.gravaContribuyentes();

        log.info("Inicio do test --------------------------------------------------------------------------------------------------");
        log.info("Obxectivo: Proba da recuperación de propiedades LAZY\n"
                + "\t\t\t\t Casos contemplados:\n"
                + "\t\t\t\t a) Recuperación de usuario con colección (LAZY) de entradas de log \n"
                + "\t\t\t\t b) Carga forzada de colección LAZY da dita coleccion\n"
                + "\t\t\t\t c) Recuperacion de entrada de log solta con referencia (EAGER) a usuario\n");

        // Situación de partida:
        // u1, e1A, e1B desligados

        log.info("Probando (excepcion tras) recuperacion LAZY ---------------------------------------------------------------------");

        c = contribDao.recuperaPorNif(produtorDatos.c1.getNif());
        log.info("Acceso a entradas de log de usuario");
        try	{
            Assert.assertEquals(1, c.getDeclaraciones().size());
            Assert.assertEquals(produtorDatos.d1, c.getDeclaraciones().iterator().next());
            excepcion=false;
        } catch (LazyInitializationException ex) {
            excepcion=true;
            log.info(ex.getClass().getName());
        };
        Assert.assertTrue(excepcion);

        log.info("");
        log.info("Probando carga forzada de coleccion LAZY ------------------------------------------------------------------------");

        c = contribDao.recuperaPorNif(produtorDatos.c1.getNif());   // Contribuyente c con proxy sen inicializar
        c = contribDao.restauraDeclaraciones(c);						// Contribuyente c con proxy xa inicializado

        Assert.assertEquals(1, c.getDeclaraciones().size());
        Assert.assertEquals(produtorDatos.d1, c.getDeclaraciones().iterator().next());

/*
		log.info("");
		log.info("Probando acceso a referencia EAGER ------------------------------------------------------------------------------");

		e = logDao.recuperaPorCodigo(produtorDatos.e1A.getCodigo());
		Assert.assertEquals(produtorDatos.u1, e.getUsuario());
*/
    }

    @Test
    public void test07_Propagacion_Remove() {

        log.info("");
        log.info("Configurando situación de partida do test -----------------------------------------------------------------------");

        produtorDatos.crearContribuyentesconDeclaraciones();
        produtorDatos.gravaContribuyentes();

        log.info("");
        log.info("Inicio do test --------------------------------------------------------------------------------------------------");
        log.info("Obxectivo: Proba de eliminación de de usuario con entradas de log asociadas\n");

        // Situación de partida:
        // u1, e1A, e1B desligados

        Assert.assertNotNull(contribDao.recuperaPorNif(produtorDatos.c1.getNif()));
        Assert.assertNotNull(declaracionDao.recuperaPorNumRef(produtorDatos.d1.getNumeroReferencia()));

        // Aqui o remove sobre u1 debe propagarse a e1A e e1B
        contribDao.elimina(produtorDatos.c1);

        Assert.assertNull(contribDao.recuperaPorNif(produtorDatos.c1.getNif()));
        Assert.assertNull(declaracionDao.recuperaPorNumRef(produtorDatos.d1.getNumeroReferencia()));

    }
}
