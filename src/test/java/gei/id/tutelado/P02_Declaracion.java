package gei.id.tutelado;

import gei.id.tutelado.configuracion.Configuracion;
import gei.id.tutelado.configuracion.ConfiguracionJPA;

import gei.id.tutelado.dao.DeclaracionDao;
import gei.id.tutelado.dao.DeclaracionDaoJPA;
import gei.id.tutelado.model.Declaracion;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.LazyInitializationException;
import org.junit.*;
import org.junit.rules.TestRule;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;

public class P02_Declaracion {
    private Logger log = LogManager.getLogger("gei.id.tutelado");

    private static ProdutorDatosProba produtorDatos = new ProdutorDatosProba();

    private static Configuracion cfg;
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

        declaracionDao = new DeclaracionDaoJPA();
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

        Declaracion d;



        log.info("");
        log.info("Configurando situación de partida do test -----------------------------------------------------------------------");

        produtorDatos.crearContribuyentesconDeclaraciones();
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

        d = declaracionDao.recuperaPorNumRef(produtorDatos.d1.getNumeroReferencia());
        Assert.assertEquals(d.getNumeroReferencia(), produtorDatos.d1.getNumeroReferencia());
        Assert.assertEquals(d.getFechaPresentacion(), produtorDatos.d1.getFechaPresentacion());
        Assert.assertEquals(d.getContribuyente(), produtorDatos.d1.getContribuyente());

        d = declaracionDao.recuperaPorNumRef(produtorDatos.d2.getNumeroReferencia());
        Assert.assertEquals(d.getNumeroReferencia(), produtorDatos.d2.getNumeroReferencia());
        Assert.assertEquals(d.getFechaPresentacion(), produtorDatos.d2.getFechaPresentacion());
        Assert.assertEquals(d.getContribuyente(), produtorDatos.d2.getContribuyente());





        log.info("");
        log.info("Probando recuperacion por numeroReferencia INEXISTENTE -----------------------------------------------");

        d = declaracionDao.recuperaPorNumRef(4L);
        Assert.assertNull (d);

    }

    @Test
    public void test02_Alta() {


        log.info("");
        log.info("Configurando situación de partida do test -----------------------------------------------------------------------");

        produtorDatos.creaContribuyentesSoltos();
        produtorDatos.gravaContribuyentes();
        produtorDatos.crearDeclaracionesSoltos();

        log.info("");
        log.info("Inicio do test --------------------------------------------------------------------------------------------------");
        log.info("Obxectivo: Proba da gravación de declaraciones\n"
                + "\t\t\t\t Casos contemplados:\n"
                + "\t\t\t\t a) Primera declaración del contribuyente\n"
                + "\t\t\t\t b) Nueva declaración del contribuyente\n");

        // Situación de partida:
        // pf1 desligado
        // d1, d2 transitorios

        produtorDatos.pf1.addDeclaracion(produtorDatos.d1);

        log.info("");
        log.info("Gravando primeira declaracion --------------------------------------------------------------------");
        Assert.assertNull(produtorDatos.d1.getId());
        declaracionDao.almacena(produtorDatos.d1);
        Assert.assertNotNull(produtorDatos.d1.getId());

        produtorDatos.pf1.addDeclaracion(produtorDatos.d2);

        log.info("");
        log.info("Gravando segunda declaracion ---------------------------------------------------------------------");
        Assert.assertNull(produtorDatos.d2.getId());
        declaracionDao.almacena(produtorDatos.d2);
        Assert.assertNotNull(produtorDatos.d2.getId());

    }

    @Test
    public void test03_Eliminacion() {

        log.info("");
        log.info("Configurando situación de partida do test -----------------------------------------------------------------------");

        produtorDatos.crearContribuyentesconDeclaraciones();
        produtorDatos.gravaContribuyentes();


        log.info("");
        log.info("Inicio do test --------------------------------------------------------------------------------------------------");
        log.info("Obxectivo: Proba de eliminación da BD de contribuyente sen entradas asociadas\n");

        // Situación de partida:
        // d1 desligado

        Assert.assertNotNull(declaracionDao.recuperaPorNumRef(produtorDatos.d1.getNumeroReferencia()));
        declaracionDao.elimina(produtorDatos.d1);
        Assert.assertNull(declaracionDao.recuperaPorNumRef(produtorDatos.d1.getNumeroReferencia()));
    }
    @Test
    public void test04_Modificacion() {

        Declaracion d1, d2;
        float novoImporte  = 500.0f;

        log.info("");
        log.info("Configurando situación de partida do test -----------------------------------------------------------------------");

        produtorDatos.crearContribuyentesconDeclaraciones();
        produtorDatos.gravaContribuyentes();

        log.info("");
        log.info("Inicio do test --------------------------------------------------------------------------------------------------");
        log.info("Obxectivo: Proba de modificación da información dunha declaración solta\n");


        // Situación de partida:
        // d1 desligado

        d1 = declaracionDao.recuperaPorNumRef(produtorDatos.d1.getNumeroReferencia());

        Assert.assertNotEquals(novoImporte, d1.getImporte());
        d1.setImporte(novoImporte);

        declaracionDao.modifica(d1);

        d2 = declaracionDao.recuperaPorNumRef(produtorDatos.d1.getNumeroReferencia());
        Assert.assertEquals (novoImporte, d2.getImporte(), 0.01f);

    }
    @Test
    public void test06_EAGER() {

        Declaracion d;
        Boolean excepcion;

        log.info("");
        log.info("Configurando situación de partida do test -----------------------------------------------------------------------");

        produtorDatos.crearContribuyentesconDeclaraciones();
        produtorDatos.gravaContribuyentes();

        log.info("Inicio do test --------------------------------------------------------------------------------------------------");
        log.info("Obxectivo: Proba da recuperación de propiedades EAGER\n");

        // Situación de partida:
        // c1, d1, d2 desligados

        log.info("Probando (que non hai excepcion tras) acceso inicial a propiedade EAGER fora de sesion ----------------------------------------");

        d = declaracionDao.recuperaPorNumRef(produtorDatos.d1.getNumeroReferencia());
        log.info("Acceso a contribuyente de una declaración");
        try	{
            Assert.assertEquals(produtorDatos.pj1, d.getContribuyente());
            excepcion=false;
        } catch (LazyInitializationException ex) {
            excepcion=true;
            log.info(ex.getClass().getName());
        };
        Assert.assertFalse(excepcion);
    }

}
