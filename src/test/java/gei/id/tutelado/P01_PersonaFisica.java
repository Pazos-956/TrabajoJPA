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
            Assert.assertEquals(2, ((PersonaFisica) pf).getPersonaJuridicas().size());




        }
        pf = pfDao.recuperaPorNif(produtorDatos.pf2.getNif());

        if (pf instanceof PersonaFisica) {
            // Acceder a los métodos específicos de PersonaFisica
            Assert.assertEquals(produtorDatos.pf2.getNif(), pf.getNif());
            Assert.assertEquals(produtorDatos.pf2.getNombre(), pf.getNombre());
            Assert.assertEquals(produtorDatos.pf2.getDireccion(), pf.getDireccion());
            // Acceder a otros métodos específicos de PersonaFisica
            Assert.assertEquals(produtorDatos.pf2.getFechaNacimiento(), ((PersonaFisica) pf).getFechaNacimiento());
            Assert.assertEquals(produtorDatos.pf2.getEstadoCivil(), ((PersonaFisica) pf).getEstadoCivil());
            Assert.assertEquals(0, ((PersonaFisica) pf).getPersonaJuridicas().size());


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



        String novoEC = new String ("Casado");

        pf1 =(PersonaFisica) pfDao.recuperaPorNif(produtorDatos.pf1.getNif());
        Assert.assertNotEquals(novoEC, pf1.getEstadoCivil());
        pf1.setEstadoCivil(novoEC);

        pfDao.modifica(pf1);
        pf2 =(PersonaFisica) pfDao.recuperaPorNif(produtorDatos.pf1.getNif());
        Assert.assertEquals (novoEC, pf2.getEstadoCivil());

    }
    @Test
    public void test05_LAZY() {

        Contribuyente c;
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



        log.info("Probando (excepcion tras) recuperacion LAZY ---------------------------------------------------------------------");

        c = pfDao.recuperaPorNif(produtorDatos.pf2.getNif());
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

        c = pfDao.recuperaPorNif(produtorDatos.pf2.getNif());   // Contribuyente c con proxy sen inicializar
        c = pfDao.restauraDeclaraciones(c);						// Contribuyente c con proxy xa inicializado

        Assert.assertEquals(2, c.getDeclaraciones().size());
        Assert.assertEquals(produtorDatos.d1, c.getDeclaraciones().iterator().next());

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



        Assert.assertNotNull(pfDao.recuperaPorNif(produtorDatos.pf2.getNif()));
        Assert.assertNotNull(declDao.recuperaPorNumRef(produtorDatos.d1.getNumeroReferencia()));

        pfDao.elimina(produtorDatos.pf2);

        Assert.assertNull(pfDao.recuperaPorNif(produtorDatos.pf2.getNif()));
        Assert.assertNull(declDao.recuperaPorNumRef(produtorDatos.d1.getNumeroReferencia()));

    }
}