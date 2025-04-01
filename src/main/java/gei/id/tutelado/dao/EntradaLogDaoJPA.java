package gei.id.tutelado.dao;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.NamedQuery;

import gei.id.tutelado.configuracion.Configuracion;
import gei.id.tutelado.model.EntradaLog;
import gei.id.tutelado.model.Usuario;

public class EntradaLogDaoJPA implements EntradaLogDao {

	private EntityManagerFactory emf; 
	private EntityManager em;
    
	@Override
	public void setup (Configuracion config) {
		this.emf = (EntityManagerFactory) config.get("EMF");
	}


	/* MO4.1 */
	@Override
	public EntradaLog recuperaPorCodigo(String codigo) {

		List<EntradaLog> entradas= new ArrayList<>();

		try {
			em = emf.createEntityManager();
			em.getTransaction().begin();

			entradas = em.createNamedQuery("EntradaLog.recuperaPorCodigo", EntradaLog.class)
					.setParameter("codigo", codigo).getResultList();

			em.getTransaction().commit();
			em.close();
		} catch (Exception ex ) {
			if (em!=null && em.isOpen()) {
				if (em.getTransaction().isActive()) em.getTransaction().rollback();
				em.close();
				throw(ex);
			}
		}
		return (entradas.isEmpty()?null:entradas.get(0));
	}

	/* MO4.2 */
	@Override
	public EntradaLog almacena(EntradaLog log) {
		try {
				
			em = emf.createEntityManager();
			em.getTransaction().begin();

			em.persist(log);

			em.getTransaction().commit();
			em.close();
		
		} catch (Exception ex ) {
			if (em!=null && em.isOpen()) {
				if (em.getTransaction().isActive()) em.getTransaction().rollback();
				em.close();
				throw(ex);
			}
		}
		return log;
	}

	/* MO4.3 */
	@Override
	public void elimina(EntradaLog log) {
		try {

			em = emf.createEntityManager();
			em.getTransaction().begin();

			EntradaLog logTmp = em.find (EntradaLog.class, log.getId());
			em.remove (logTmp);

			em.getTransaction().commit();
			em.close();

		} catch (Exception ex ) {
			if (em!=null && em.isOpen()) {
				if (em.getTransaction().isActive()) em.getTransaction().rollback();
				em.close();
				throw(ex);
			}
		}
	}

	/* MO4.4 */
	@Override
	public EntradaLog modifica(EntradaLog log) {
		try {

			em = emf.createEntityManager();		
			em.getTransaction().begin();

			log = em.merge (log);

			em.getTransaction().commit();
			em.close();
			
		} catch (Exception ex ) {
			if (em!=null && em.isOpen()) {
				if (em.getTransaction().isActive()) em.getTransaction().rollback();
				em.close();
				throw(ex);
			}
		}
		return log;
	}

	/* MO4.6.a */
	@Override
	public List<EntradaLog> recuperaTodasUsuario(Usuario u) {
		List <EntradaLog> entradas=null;

		try {
			em = emf.createEntityManager();
			em.getTransaction().begin();

			entradas = em.createQuery("SELECT e FROM Usuario u JOIN u.entradasLog e WHERE u=:u ORDER BY e.dataHora DESC", EntradaLog.class).setParameter("u", u).getResultList();

			em.getTransaction().commit();
			em.close();	

		}
		catch (Exception ex ) {
			if (em!=null && em.isOpen()) {
				if (em.getTransaction().isActive()) em.getTransaction().rollback();
				em.close();
				throw(ex);
			}
		}

		return entradas;
	}
}
