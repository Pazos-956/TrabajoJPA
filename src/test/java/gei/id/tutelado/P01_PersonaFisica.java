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

public class P01_PersonaFisica {
    private Logger log = LogManager.getLogger("gei.id.tutelado");

    private static ProdutorDatosProba produtorDatos = new ProdutorDatosProba();

    private static Configuracion cfg;
    private static ContribuyenteDao pfDao;
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

        pfDao = new ContribuyenteDaoJPA();
        pfDao.setup(cfg);

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

        Contribuyente pf;
        pf = new PersonaFisica();


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

        pf = pfDao.recuperaPorNif(produtorDatos.pf1.getNif());

        if (pf instanceof PersonaFisica) {
            // Acceder a los métodos específicos de PersonaFisica
            Assert.assertEquals(produtorDatos.pf1.getNif(), pf.getNif());
            Assert.assertEquals(produtorDatos.pf1.getNombre(), pf.getNombre());
            Assert.assertEquals(produtorDatos.pf1.getDireccion(), pf.getDireccion());
            // Acceder a otros métodos específicos de PersonaFisica
            Assert.assertEquals(produtorDatos.pf1.getFechaNacimiento(), ((PersonaFisica) pf).getFechaNacimiento());
            Assert.assertEquals(produtorDatos.pf1.getEstadoCivil(), ((PersonaFisica) pf).getEstadoCivil());

        }



        log.info("");
        log.info("Probando recuperacion por nif INEXISTENTE -----------------------------------------------");

        pf = pfDao.recuperaPorNif("iwbvyhuebvuwebvi");
        Assert.assertNull (pf);

    }

    @Test
    public void test02_Alta() {

        log.info("");
        log.info("Configurando situación de partida do test -----------------------------------------------------------------------");

        produtorDatos.creaContribuyentesSoltos();

        log.info("");
        log.info("Inicio do test --------------------------------------------------------------------------------------------------");
        log.info("Obxectivo: Proba de gravación na BD de nova persoa física.\n");

        // Situación de partida:
        // pf1 transitorio


        Assert.assertNull(produtorDatos.pf1.getId());
        pfDao.almacena(produtorDatos.pf1);
        Assert.assertNotNull(produtorDatos.pf1.getId());
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

        Assert.assertNotNull(pfDao.recuperaPorNif(produtorDatos.pf1.getNif()));
        pfDao.elimina(produtorDatos.pf1);
        Assert.assertNull(pfDao.recuperaPorNif(produtorDatos.pf1.getNif()));
    }

    @Test
    public void test04_Modificacion() {

        PersonaFisica pf1, pf2;

        log.info("");
        log.info("Configurando situación de partida do test -----------------------------------------------------------------------");

        produtorDatos.creaContribuyentesSoltos();
        produtorDatos.gravaContribuyentes();

        log.info("");
        log.info("Inicio do test --------------------------------------------------------------------------------------------------");
        log.info("Obxectivo: Proba de modificación da información básica de persona fisica\n");

        // Situación de partida:
        // i1 desligado

        String novoEC = new String ("Casado");

        pf1 =(PersonaFisica) pfDao.recuperaPorNif(produtorDatos.pf1.getNif());
        Assert.assertNotEquals(novoEC, pf1.getEstadoCivil());
        pf1.setEstadoCivil(novoEC);

        pfDao.modifica(pf1);
        pf2 =(PersonaFisica) pfDao.recuperaPorNif(produtorDatos.pf1.getNif());
        Assert.assertEquals (novoEC, pf2.getEstadoCivil());

    }
}