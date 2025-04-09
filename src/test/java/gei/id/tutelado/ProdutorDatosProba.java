package gei.id.tutelado;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

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

	public Declaracion d1, d2;
	public Impuesto i1, i2, i3, i4;
	public PersonaFisica pf1, pf2;
	public PersonaJuridica pj1;
	public List<Contribuyente> listaxeC;







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

	public void crearDeclaracionesSueltas () {
		this.d1 = new Declaracion();
		this.d2 = new Declaracion();

		this.d1.setImporte(1000.30f);
		this.d1.setFechaPresentacion(LocalDate.now());
		this.d1.setNumeroReferencia(123456789L);

		this.d2.setImporte(1000.30f);
		this.d2.setFechaPresentacion(LocalDate.now());
		this.d2.setNumeroReferencia(987654321L);
	}
	public void crearImpuestos () {
		this.i1 = new Impuesto();
		this.i2 = new Impuesto();
		this.i3 = new Impuesto();
		this.i4 = new Impuesto();

		this.i1.setCodigo(1L);
		this.i1.setDescripcion("impuesto 1");
		this.i1.setTipo(TipoImpuesto.IVA);

		this.i2.setCodigo(2L);
		this.i2.setDescripcion("impuesto 2");
		this.i2.setTipo(TipoImpuesto.IRPF);

		this.i3.setCodigo(3L);
		this.i3.setDescripcion("impuesto 3");
		this.i3.setTipo(TipoImpuesto.IVA);

		this.i4.setCodigo(4L);
		this.i4.setDescripcion("impuesto 4");
		this.i4.setTipo(TipoImpuesto.IBI);
	}

	public void crearContribuyentes () {
		this.pf1 = new PersonaFisica();
		this.pf2 = new PersonaFisica();
		this.pj1 = new PersonaJuridica();

		this.pf1.setNif("000A");
		this.pf1.setDireccion("Dirección 1");
		this.pf1.setNombre("Contribuyente 1");
		this.pf1.setEstadoCivil("Soltero");
		this.pf1.setFechaNacimiento(LocalDate.of(1990, 1, 1));

		this.pf2.setNif("000C");
		this.pf2.setDireccion("Dirección 3");
		this.pf2.setNombre("Contribuyente 3");
		this.pf2.setEstadoCivil("Casado");
		this.pf2.setFechaNacimiento(LocalDate.of(1990, 2, 1));

		this.pj1.setNif("000B");
		this.pj1.setDireccion("Dirección 2");
		this.pj1.setNombre("Contribuyente 2");
		this.pj1.setRazonSocial("Razón social 1");
		this.pj1.setFechaConstitucion(LocalDate.of(2000, 1, 1));

		this.listaxeC = new ArrayList<Contribuyente> ();
		this.listaxeC.add(0,pf1);
		this.listaxeC.add(1,pj1);
	}
	public void crearDeclaracionesConContribuyente () {

		this.crearDeclaracionesSueltas();
		this.crearContribuyentes();

		this.pf1.addDeclaracion(this.d1);
		this.pf1.addDeclaracion(this.d2);

	}
	public void crearDeclaracionesImpuestos () {
		this.crearDeclaracionesConContribuyente();
		this.crearImpuestos();
		Set<Impuesto> impuestos1 = this.d1.getImpuesto();
		impuestos1.add(this.i1);
		impuestos1.add(this.i2);

		this.d1.setImpuesto(impuestos1);
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
	public void gravaContribuyentes() {
		EntityManager em=null;
		try {
			em = emf.createEntityManager();
			em.getTransaction().begin();

			Iterator<Contribuyente> itC = this.listaxeC.iterator();
			while (itC.hasNext()) {
				Contribuyente c = itC.next();
				em.persist(c);

				Iterator<Declaracion> itD = c.getDeclaraciones().iterator();
				while (itD.hasNext()) {
					em.persist(itD.next());
				}
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
	public void gravaImpuestos() {
		EntityManager em=null;
		try {
			em = emf.createEntityManager();
			em.getTransaction().begin();

			em.persist(i1);
			em.persist(i2);
			em.persist(i3);
			em.persist(i4);

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

			Iterator <Contribuyente> itC = em.createQuery("SELECT c from Contribuyente c", Contribuyente.class).getResultList().iterator();
			while (itC.hasNext()) em.remove(itC.next());

			Iterator <Declaracion> itD = em.createQuery("SELECT d from Declaracion d", Declaracion.class).getResultList().iterator();
			while (itD.hasNext()) em.remove(itD.next());

			Iterator <Impuesto> itI = em.createQuery("SELECT i from Impuesto i", Impuesto.class).getResultList().iterator();
			while (itI.hasNext()) em.remove(itI.next());

			/*
			// Non é necesario porque establecemos  propagacion do remove
			// Se desactivamos propagación, descomentar
			Iterator <EntradaLog> itL = em.createQuery("SELECT e from EntradaLog e", EntradaLog.class).getResultList().iterator();
			while (itL.hasNext()) em.remove(itL.next());
			*/

			em.createNativeQuery("UPDATE taboa_ids SET ultimo_valor_id=0 WHERE nome_id='idDeclaracion'" ).executeUpdate();
			em.createNativeQuery("UPDATE taboa_ids SET ultimo_valor_id=0 WHERE nome_id='idConsultas'" ).executeUpdate();
			em.createNativeQuery("UPDATE taboa_ids SET ultimo_valor_id=0 WHERE nome_id='idImpuesto'" ).executeUpdate();

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
	public void limpaBD_2 () {
		EntityManager em=null;
		try {
			em = emf.createEntityManager();
			em.getTransaction().begin();
			
			Iterator <Usuario> itU = em.createQuery("SELECT u from Usuario u", Usuario.class).getResultList().iterator();
			while (itU.hasNext()) em.remove(itU.next());
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
