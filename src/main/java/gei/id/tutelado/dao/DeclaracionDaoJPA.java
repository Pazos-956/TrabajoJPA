package gei.id.tutelado.dao;

import gei.id.tutelado.configuracion.Configuracion;
import gei.id.tutelado.model.Contribuyente;
import gei.id.tutelado.model.Declaracion;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.util.ArrayList;
import java.util.List;

public class DeclaracionDaoJPA implements DeclaracionDao {

    private EntityManagerFactory emf;
    private EntityManager em;

    @Override
    public void setup (Configuracion config) {
        this.emf = (EntityManagerFactory) config.get("EMF");
    }

    /* MO4.1 */
    @Override
    public Declaracion recuperaPorNumRef(Long numeroReferencia) {
        List<Declaracion> declaraciones=new ArrayList<>();

        try {
            em = emf.createEntityManager();
            em.getTransaction().begin();

            declaraciones = em.createNamedQuery("Declaracion.recuperaPorNumeroReferencia", Declaracion.class).setParameter("numeroReferencia", numeroReferencia).getResultList();

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

        return (declaraciones.isEmpty()?null:declaraciones.get(0));
    }

    @Override
    public Declaracion almacena(Declaracion declaracion) {

        try {
            em = emf.createEntityManager();
            em.getTransaction().begin();

            em.persist(declaracion);

            em.getTransaction().commit();
            em.close();

        } catch (Exception ex ) {
            if (em!=null && em.isOpen()) {
                if (em.getTransaction().isActive()) em.getTransaction().rollback();
                em.close();
                throw(ex);
            }
        }
        return declaracion;
    }

    @Override
    public void elimina(Declaracion declaracion) {
        try {

            em = emf.createEntityManager();
            em.getTransaction().begin();

            Declaracion declaracionTmp = em.find (Declaracion.class, declaracion.getId());
            em.remove (declaracionTmp);

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

    @Override
    public Declaracion modifica(Declaracion declaracion) {

        try {

            em = emf.createEntityManager();
            em.getTransaction().begin();

            declaracion= em.merge (declaracion);

            em.getTransaction().commit();
            em.close();

        } catch (Exception ex ) {
            if (em!=null && em.isOpen()) {
                if (em.getTransaction().isActive()) em.getTransaction().rollback();
                em.close();
                throw(ex);
            }
        }
        return (declaracion);
    }

    public List<Declaracion> recuperaDeclaracionesNulas() {
        List<Declaracion> declaraciones=new ArrayList<>();

        try {
            em = emf.createEntityManager();
            em.getTransaction().begin();

            declaraciones = em.createQuery("SELECT d FROM Declaracion d LEFT JOIN d.impuesto imp WHERE imp IS NULL", Declaracion.class)
                    .getResultList();

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

        return (declaraciones);
    }
}
