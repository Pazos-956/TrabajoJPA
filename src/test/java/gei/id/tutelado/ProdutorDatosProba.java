package gei.id.tutelado;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import gei.id.tutelado.configuracion.Configuracion;
import gei.id.tutelado.model.*;

public class ProdutorDatosProba {


	// Crea un conxunto de obxectos para utilizar nos casos de proba
	
	private EntityManagerFactory emf=null;
	
	public Usuario u0, u1;
	public List<Usuario> listaxeU;
	
	public EntradaLog e1A, e1B;
	public List<EntradaLog> listaxeE;


	public PersonaFisica c1;
	public PersonaJuridica c2;
	public List<Contribuyente> listaxeC;

	public Declaracion d1,d2;
	public List<Declaracion> listaxeD;

	public Impuesto i1,i2;
	public Set<Impuesto> listaxeI;
	
	
	
	public void Setup (Configuracion config) {
		this.emf=(EntityManagerFactory) config.get("EMF");
	}
	
	public void creaUsuariosSoltos() {

		// Crea dous usuarios EN MEMORIA: u0, u1
		// SEN entradas de log
		
		this.u0 = new Usuario();
        this.u0.setNif("000A");
        this.u0.setNome("Usuario cero");
        this.u0.setDataAlta(LocalDate.now());

        this.u1 = new Usuario();
        this.u1.setNif("111B");
        this.u1.setNome("Usuaria un");
        this.u1.setDataAlta(LocalDate.now());

        this.listaxeU = new ArrayList<Usuario> ();
        this.listaxeU.add(0,u0);
        this.listaxeU.add(1,u1);        

	}

	public void creaContribuyentesSoltos() {

		// Crea dous contribuyentes EN MEMORIA: c0, c1
		// SIN declaraciones

		this.c1 = new PersonaFisica();
		this.c1.setNif("000A");
		this.c1.setNombre("Contribuyente cero");
		this.c1.setDireccion("Barcelona 090");
		this.c1.setFechaNacimiento(LocalDate.of(2003,11,5));
		this.c1.setEstadoCivil("Soltero");

		this.c2 = new PersonaJuridica();
		this.c2.setNif("001A");
		this.c2.setNombre("Contribuyente uno");
		this.c2.setDireccion("Madrid 111");
		this.c2.setRazonSocial("Contribuyente uno SL");
		this.c2.setFechaConstitucion(LocalDate.of(2000,5,15));

		this.listaxeC= new ArrayList<Contribuyente> ();
		this.listaxeC.add(0,c1);
		this.listaxeC.add(1,c2);

	}

	public void gravaContribuyentes() {
		EntityManager em=null;
		try {
			em = emf.createEntityManager();
			em.getTransaction().begin();

			Iterator<Contribuyente> itC = this.listaxeC.iterator();
			while (itC.hasNext()) {
				Contribuyente c = itC.next();
				em.persist(c);
				// DESCOMENTAR SE A PROPAGACION DO PERSIST NON ESTA ACTIVADA
				/*
				Iterator<EntradaLog> itEL = u.getEntradasLog().iterator();
				while (itEL.hasNext()) {
					em.persist(itEL.next());
				}
				*/
			}
			em.getTransaction().commit();
			em.close();
		} catch (Exception e) {
			if (em!=null && em.isOpen()) {
				if (em.getTransaction().isActive()) em.getTransaction().rollback();
				em.close();
				throw (e);
			}
		}
	}

	public void crearDeclaracionesSoltos() {

		this.d1 = new Declaracion();
		this.d1.setNumeroReferencia(1L);
		this.d1.setFechaPresentacion(LocalDate.of(2025,1,10));
		this.d1.setImporte(105.55f);

		this.d2 = new Declaracion();
		this.d2.setNumeroReferencia(2L);
		this.d2.setFechaPresentacion(LocalDate.of(2025,1,10));
		this.d2.setImporte(200.55f);


	}


	public void crearContribuyentesconDeclaraciones(){
		this.creaContribuyentesSoltos();
		this.crearDeclaracionesSoltos();
		System.out.println(this.d1);

		this.c1.addDeclaracion(this.d1);
		this.c2.addDeclaracion(this.d2);

	}

	public void asignarImpuestosaDeclaraciones(){
		this.crearImpuestosSueltos();

		Impuesto primero = this.listaxeI.iterator().next();

		// Crear un set nuevo con solo ese impuesto
		Set<Impuesto> soloUno = new HashSet<>();
		soloUno.add(primero);

		this.d1.setImpuesto(soloUno);

	}
	
	public void creaEntradasLogSoltas () {

		// Crea duas entradas de log EN MEMORIA: e1a, e1b
		// Sen usuario asignado (momentaneamente)
		
		this.e1A=new EntradaLog();
        this.e1A.setCodigo("E001");
        this.e1A.setDescricion ("Modificado contrasinal por defecto");
        this.e1A.setDataHora(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));

        this.e1B=new EntradaLog();
        this.e1B.setCodigo("E002");
        this.e1B.setDescricion ("Acceso a zona reservada");
        this.e1B.setDataHora(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));

        this.listaxeE = new ArrayList<EntradaLog> ();
        this.listaxeE.add(0,this.e1A);
        this.listaxeE.add(1,this.e1B);        

	}

	public void crearImpuestosSueltos(){
		this.i1=new Impuesto();
		this.i1.setCodigo(1L);
		this.i1.setDescripcion("Te vamos a sacar lo más grande");
		this.i1.setTipo(TipoImpuesto.IBI);

		this.i2=new Impuesto();
		this.i2.setCodigo(2L);
		this.i2.setDescripcion("Tú tampoco te libras");
		this.i2.setTipo(TipoImpuesto.IRPF);

		this.listaxeI = new HashSet<Impuesto>();
		this.listaxeI.add(this.i1);
		this.listaxeI.add(this.i2);

	}

	public void gravaImpuestos() {
		EntityManager em=null;
		try {
			em = emf.createEntityManager();
			em.getTransaction().begin();

			Iterator<Impuesto> itI = this.listaxeI.iterator();
			while (itI.hasNext()) {
				Impuesto i = itI.next();
				em.persist(i);
				// DESCOMENTAR SE A PROPAGACION DO PERSIST NON ESTA ACTIVADA
				/*
				Iterator<EntradaLog> itEL = u.getEntradasLog().iterator();
				while (itEL.hasNext()) {
					em.persist(itEL.next());
				}
				*/
			}
			em.getTransaction().commit();
			em.close();
		} catch (Exception e) {
			if (em!=null && em.isOpen()) {
				if (em.getTransaction().isActive()) em.getTransaction().rollback();
				em.close();
				throw (e);
			}
		}
	}
	
	public void creaUsuariosConEntradasLog () {

		this.creaUsuariosSoltos();
		this.creaEntradasLogSoltas();
		
        this.u1.engadirEntradaLog(this.e1A);
        this.u1.engadirEntradaLog(this.e1B);

	}
	
	public void gravaUsuarios() {
		EntityManager em=null;
		try {
			em = emf.createEntityManager();
			em.getTransaction().begin();

			Iterator<Usuario> itU = this.listaxeU.iterator();
			while (itU.hasNext()) {
				Usuario u = itU.next();
				em.persist(u);
				// DESCOMENTAR SE A PROPAGACION DO PERSIST NON ESTA ACTIVADA
				/*
				Iterator<EntradaLog> itEL = u.getEntradasLog().iterator();
				while (itEL.hasNext()) {
					em.persist(itEL.next());
				}
				*/
			}
			em.getTransaction().commit();
			em.close();
		} catch (Exception e) {
			if (em!=null && em.isOpen()) {
				if (em.getTransaction().isActive()) em.getTransaction().rollback();
				em.close();
				throw (e);
			}
		}	
	}
	
	public void limpaBD () {
		EntityManager em=null;
		try {
			em = emf.createEntityManager();
			em.getTransaction().begin();
			
			Iterator <Usuario> itU = em.createQuery("SELECT u from Usuario u", Usuario.class).getResultList().iterator();
			Iterator <Contribuyente> itC = em.createQuery("SELECT c from Contribuyente c", Contribuyente.class).getResultList().iterator();
			Iterator <Impuesto> itI = em.createQuery("SELECT i from Impuesto i", Impuesto.class).getResultList().iterator();


			while (itU.hasNext()) em.remove(itU.next());
			while (itC.hasNext()) em.remove(itC.next());
			while (itI.hasNext()) em.remove(itI.next());
			/*
			// Non é necesario porque establecemos  propagacion do remove
			// Se desactivamos propagación, descomentar
			Iterator <EntradaLog> itL = em.createQuery("SELECT e from EntradaLog e", EntradaLog.class).getResultList().iterator();
			while (itL.hasNext()) em.remove(itL.next());		
			*/
			
			em.createNativeQuery("UPDATE taboa_ids SET ultimo_valor_id=0 WHERE nome_id='idUsuario'" ).executeUpdate();
			em.createNativeQuery("UPDATE taboa_ids SET ultimo_valor_id=0 WHERE nome_id='idEntradaLog'" ).executeUpdate();

			em.getTransaction().commit();
			em.close();
		} catch (Exception e) {
			if (em!=null && em.isOpen()) {
				if (em.getTransaction().isActive()) em.getTransaction().rollback();
				em.close();
				throw (e);
			}
		}
	}
	
	
}
