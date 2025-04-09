package gei.id.tutelado;

import gei.id.tutelado.configuracion.Configuracion;
import gei.id.tutelado.configuracion.ConfiguracionJPA;
import gei.id.tutelado.dao.ContribuyenteDao;
import gei.id.tutelado.dao.ContribuyenteDaoJPA;
import gei.id.tutelado.dao.ImpuestoDao;
import gei.id.tutelado.dao.ImpuestoDaoJPA;
import gei.id.tutelado.model.Contribuyente;
import gei.id.tutelado.model.Impuesto;
import gei.id.tutelado.model.PersonaFisica;
import gei.id.tutelado.model.PersonaJuridica;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.*;
import org.junit.rules.TestRule;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;

public class P03_Impuesto {
    private Logger log = LogManager.getLogger("gei.id.tutelado");

    private static ProdutorDatosProba produtorDatos = new ProdutorDatosProba();

    private static Configuracion cfg;
    private static ImpuestoDao impuestoDao;

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

        impuestoDao = new ImpuestoDaoJPA();
        impuestoDao.setup(cfg);

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

        Impuesto i;


        log.info("");
        log.info("Configurando situación de partida do test -----------------------------------------------------------------------");

        produtorDatos.crearImpuestosSueltos();
        produtorDatos.gravaImpuestos();

        log.info("");
        log.info("Inicio do test --------------------------------------------------------------------------------------------------");
        log.info("Obxectivo: Proba de recuperación desde a BD de contribuyentes (sen entradas asociadas) por nif\n"
                + "\t\t\t\t Casos contemplados:\n"
                + "\t\t\t\t a) Recuperación por nif existente\n"
                + "\t\t\t\t b) Recuperacion por nif inexistente\n");

        // Situación de partida:
        // u0 desligado

        log.info("Probando recuperacion por nif EXISTENTE --------------------------------------------------");

        i = impuestoDao.recuperaPorCodigo(produtorDatos.i1.getCodigo());
        Assert.assertEquals(produtorDatos.i1.getCodigo(), i.getCodigo());
        Assert.assertEquals(produtorDatos.i1.getDescripcion(), i.getDescripcion());
        Assert.assertEquals(produtorDatos.i1.getTipo(), i.getTipo());

        i = impuestoDao.recuperaPorCodigo(produtorDatos.i2.getCodigo());
        Assert.assertEquals(produtorDatos.i2.getCodigo(), i.getCodigo());
        Assert.assertEquals(produtorDatos.i2.getDescripcion(), i.getDescripcion());
        Assert.assertEquals(produtorDatos.i2.getTipo(), i.getTipo());





        log.info("");
        log.info("Probando recuperacion por codigo INEXISTENTE -----------------------------------------------");

        i = impuestoDao.recuperaPorCodigo(5L);
        Assert.assertNull (i);

    }

    @Test
    public void test03_Eliminacion() {

        log.info("");
        log.info("Configurando situación de partida do test -----------------------------------------------------------------------");

        produtorDatos.crearImpuestosSueltos();
        produtorDatos.gravaImpuestos();


        log.info("");
        log.info("Inicio do test --------------------------------------------------------------------------------------------------");
        log.info("Obxectivo: Proba de eliminación da BD de contribuyente sen entradas asociadas\n");

        // Situación de partida:
        // c1 desligado

        Assert.assertNotNull(impuestoDao.recuperaPorCodigo(produtorDatos.i1.getCodigo()));
        impuestoDao.elimina(produtorDatos.i1);
        Assert.assertNull(impuestoDao.recuperaPorCodigo(produtorDatos.i1.getCodigo()));
    }
}
