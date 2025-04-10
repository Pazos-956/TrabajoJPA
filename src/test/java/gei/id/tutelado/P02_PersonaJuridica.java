package gei.id.tutelado;

import gei.id.tutelado.configuracion.Configuracion;
import gei.id.tutelado.configuracion.ConfiguracionJPA;
import gei.id.tutelado.dao.ContribuyenteDao;
import gei.id.tutelado.dao.ContribuyenteDaoJPA;
import gei.id.tutelado.dao.DeclaracionDao;
import gei.id.tutelado.dao.DeclaracionDaoJPA;
import gei.id.tutelado.model.Contribuyente;
import gei.id.tutelado.model.PersonaJuridica;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.*;
import org.junit.rules.TestRule;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;

public class P02_PersonaJuridica {
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
        // pj3 desligado

        Assert.assertNotNull(pjDao.recuperaPorNif(produtorDatos.pj3.getNif()));
        pjDao.elimina(produtorDatos.pj3);
        Assert.assertNull(pjDao.recuperaPorNif(produtorDatos.pj3.getNif()));
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



}