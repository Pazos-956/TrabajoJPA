package gei.id.tutelado;

import gei.id.tutelado.configuracion.Configuracion;
import gei.id.tutelado.configuracion.ConfiguracionJPA;
import gei.id.tutelado.dao.ContribuyenteDao;
import gei.id.tutelado.dao.ContribuyenteDaoJPA;
import gei.id.tutelado.dao.DeclaracionDao;
import gei.id.tutelado.dao.DeclaracionDaoJPA;
import gei.id.tutelado.model.Contribuyente;
import gei.id.tutelado.model.Declaracion;
import gei.id.tutelado.model.PersonaFisica;
import gei.id.tutelado.model.PersonaJuridica;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.LazyInitializationException;
import org.junit.*;
import org.junit.rules.TestRule;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;

public class P04_PersonaJuridica {
    private Logger log = LogManager.getLogger("gei.id.tutelado");

    private static ProdutorDatosProba produtorDatos = new ProdutorDatosProba();

    private static Configuracion cfg;
    private static ContribuyenteDao pjDao;
    private static DeclaracionDao declDao;


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

        pjDao = new ContribuyenteDaoJPA();
        pjDao.setup(cfg);

        declDao = new DeclaracionDaoJPA();
        declDao.setup(cfg);

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

        Contribuyente pj;
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

        pj = pjDao.recuperaPorNif(produtorDatos.pj1.getNif());

        if (pj instanceof PersonaJuridica) {
            // Acceder a los métodos específicos de PersonaJurídica
            Assert.assertEquals(produtorDatos.pj1.getNif(), pj.getNif());
            Assert.assertEquals(produtorDatos.pj1.getNombre(), pj.getNombre());
            Assert.assertEquals(produtorDatos.pj1.getDireccion(), pj.getDireccion());
            // Acceder a otros métodos específicos de PersonaJurídica
            Assert.assertEquals(produtorDatos.pj1.getRazonSocial(), ((PersonaJuridica) pj).getRazonSocial());
            Assert.assertEquals(produtorDatos.pj1.getFechaConstitucion(), ((PersonaJuridica) pj).getFechaConstitucion());

        }

        log.info("");
        log.info("Probando recuperacion por nif INEXISTENTE -----------------------------------------------");

        pj = pjDao.recuperaPorNif("iwbvyhuebvuwebvi");
        Assert.assertNull (pj);

    }
    @Test
    public void test02_Alta() {

        log.info("");
        log.info("Configurando situación de partida do test -----------------------------------------------------------------------");

        produtorDatos.creaContribuyentesSoltos();

        log.info("");
        log.info("Inicio do test --------------------------------------------------------------------------------------------------");
        log.info("Obxectivo: Proba de gravación na BD de nova persoa juridica.\n");

        // Situación de partida:
        // d1 transitorio

        Assert.assertNull(produtorDatos.pj1.getId());
        pjDao.almacena(produtorDatos.pj1);
        Assert.assertNotNull(produtorDatos.pj1.getId());
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

        Assert.assertNotNull(pjDao.recuperaPorNif(produtorDatos.pj1.getNif()));
        pjDao.elimina(produtorDatos.pj1);
        Assert.assertNull(pjDao.recuperaPorNif(produtorDatos.pj1.getNif()));
    }

    @Test
    public void test04_Modificacion() {

        PersonaJuridica pj1, pj2;

        log.info("");
        log.info("Configurando situación de partida do test -----------------------------------------------------------------------");

        produtorDatos.creaContribuyentesSoltos();
        produtorDatos.gravaContribuyentes();

        log.info("");
        log.info("Inicio do test --------------------------------------------------------------------------------------------------");
        log.info("Obxectivo: Proba de modificación da información básica de persona juridica\n");

        // Situación de partida:
        // pj1 desligado

        String novaRazon = new String ("Nova Razón Social");

        pj1 =(PersonaJuridica) pjDao.recuperaPorNif(produtorDatos.pj1.getNif());
        Assert.assertNotEquals(novaRazon, pj1.getRazonSocial());
        pj1.setRazonSocial(novaRazon);

        pjDao.modifica(pj1);
        pj2 =(PersonaJuridica) pjDao.recuperaPorNif(produtorDatos.pj1.getNif());
        Assert.assertEquals (novaRazon, pj2.getRazonSocial());

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

        c = pjDao.recuperaPorNif(produtorDatos.pj1.getNif());
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

        c = pjDao.recuperaPorNif(produtorDatos.pj1.getNif());   // Contribuyente c con proxy sen inicializar
        c = pjDao.restauraDeclaraciones(c);						// Contribuyente c con proxy xa inicializado

        Assert.assertEquals(2, c.getDeclaraciones().size());
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

        Assert.assertNotNull(pjDao.recuperaPorNif(produtorDatos.pj1.getNif()));
        Assert.assertNotNull(declDao.recuperaPorNumRef(produtorDatos.d1.getNumeroReferencia()));

        // Aqui o remove sobre u1 debe propagarse a e1A e e1B
        pjDao.elimina(produtorDatos.pj1);

        Assert.assertNull(pjDao.recuperaPorNif(produtorDatos.pj1.getNif()));
        Assert.assertNull(declDao.recuperaPorNumRef(produtorDatos.d1.getNumeroReferencia()));

    }
}