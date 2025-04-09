package gei.id.tutelado;

import gei.id.tutelado.configuracion.Configuracion;
import gei.id.tutelado.configuracion.ConfiguracionJPA;

import gei.id.tutelado.dao.DeclaracionDao;
import gei.id.tutelado.dao.DeclaracionDaoJPA;
import gei.id.tutelado.model.Declaracion;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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
}
