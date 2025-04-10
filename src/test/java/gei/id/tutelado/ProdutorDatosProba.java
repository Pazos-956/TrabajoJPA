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

	public PersonaFisica pf1,pf2;
	public PersonaJuridica pj1,pj2,pj3;
	public List<Contribuyente> listaxeC;

	public Declaracion d1,d2;
	public List<Declaracion> listaxeD;


	
	
	
	public void Setup (Configuracion config) {
		this.emf=(EntityManagerFactory) config.get("EMF");
	}


	public void creaContribuyentesSoltos() {

		Set <PersonaJuridica> lista_pj = new HashSet<>();
		// Crear 4 contribuyentes EN MEMORIA: pf1, pf2, pj1, pj2
		// SIN declaraciones y asinando una persona física como representante de dos personas jurídicas

		this.pf1 = new PersonaFisica();
		this.pf1.setNif("000A");
		this.pf1.setNombre("Persona física uno");
		this.pf1.setDireccion("Barcelona 090");
		this.pf1.setFechaNacimiento(LocalDate.of(2003,11,5));
		this.pf1.setEstadoCivil("Soltero");

		this.pj1 = new PersonaJuridica();
		this.pj1.setNif("000B");
		this.pj1.setNombre("Persona jurídica uno");
		this.pj1.setDireccion("Madrid 111");
		this.pj1.setRazonSocial("PJ uno SL");
		this.pj1.setFechaConstitucion(LocalDate.of(2000,5,15));

		this.pf2 = new PersonaFisica();
		this.pf2.setNif("001A");
		this.pf2.setNombre("Persona física dos");
		this.pf2.setDireccion("Barcelona 091");
		this.pf2.setFechaNacimiento(LocalDate.of(2003,10,5));
		this.pf2.setEstadoCivil("Casado");

		this.pj2 = new PersonaJuridica();
		this.pj2.setNif("001B");
		this.pj2.setNombre("Persona jurídica dos");
		this.pj2.setDireccion("Madrid 112");
		this.pj2.setRazonSocial("PJ dos SL");
		this.pj2.setFechaConstitucion(LocalDate.of(2005,5,15));

		this.pj3 = new PersonaJuridica();
		this.pj3.setNif("002B");
		this.pj3.setNombre("Persona jurídica tres");
		this.pj3.setDireccion("Madrid 113");
		this.pj3.setRazonSocial("PJ tres SL");
		this.pj3.setFechaConstitucion(LocalDate.of(2009,5,15));

		lista_pj.add(pj1);
		lista_pj.add(pj2);

		this.pf1.setPersonaJuridicas(lista_pj);

		this.listaxeC= new ArrayList<Contribuyente> ();
		this.listaxeC.add(0, pf1);
		this.listaxeC.add(1, pj1);
		this.listaxeC.add(2, pf2);
		this.listaxeC.add(3, pj2);
		this.listaxeC.add(4,pj3);



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

		this.pf2.addDeclaracion(this.d1);
		this.pf2.addDeclaracion(this.d2);

	}


	


	

	public void limpaBD () {
		EntityManager em=null;
		try {
			em = emf.createEntityManager();
			em.getTransaction().begin();
			
			Iterator <Contribuyente> itC = em.createQuery("SELECT c from Contribuyente c", Contribuyente.class).getResultList().iterator();


			while (itC.hasNext()) em.remove(itC.next());
			/*
			// Non é necesario porque establecemos  propagacion do remove
			// Se desactivamos propagación, descomentar
			Iterator <EntradaLog> itL = em.createQuery("SELECT e from EntradaLog e", EntradaLog.class).getResultList().iterator();
			while (itL.hasNext()) em.remove(itL.next());		
			*/

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
