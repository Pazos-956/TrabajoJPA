package gei.id.tutelado.dao;
import gei.id.tutelado.configuracion.Configuracion;
import gei.id.tutelado.model.Contribuyente;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.util.ArrayList;
import java.util.List;

public class ContribuyenteDaoJPA implements ContribuyenteDao{

    private EntityManagerFactory emf;
    private EntityManager em;

    @Override
    public void setup (Configuracion config) {
        this.emf = (EntityManagerFactory) config.get("EMF");
    }

    /* MO4.1 */
    @Override
    public Contribuyente recuperaPorNif(String nif) {
        List<Contribuyente> contribuyentes=new ArrayList<>();

        try {
            em = emf.createEntityManager();
            em.getTransaction().begin();

            contribuyentes = em.createNamedQuery("Contribuyente.recuperaPorNif", Contribuyente.class).setParameter("nif", nif).getResultList();

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

        return (contribuyentes.isEmpty()?null:contribuyentes.get(0));
    }

    @Override
    public Contribuyente almacena(Contribuyente contribuyente) {

        try {
            em = emf.createEntityManager();
            em.getTransaction().begin();

            em.persist(contribuyente);

            em.getTransaction().commit();
            em.close();

        } catch (Exception ex ) {
            if (em!=null && em.isOpen()) {
                if (em.getTransaction().isActive()) em.getTransaction().rollback();
                em.close();
                throw(ex);
            }
        }
        return contribuyente;
    }

    @Override
    public void elimina(Contribuyente contribuyente) {
        try {

            em = emf.createEntityManager();
            em.getTransaction().begin();

            Contribuyente contribTmp = em.find (Contribuyente.class, contribuyente.getId());
            em.remove (contribTmp);

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
    public Contribuyente modifica(Contribuyente contribuyente) {

        try {

            em = emf.createEntityManager();
            em.getTransaction().begin();

            contribuyente= em.merge (contribuyente);

            em.getTransaction().commit();
            em.close();

        } catch (Exception ex ) {
            if (em!=null && em.isOpen()) {
                if (em.getTransaction().isActive()) em.getTransaction().rollback();
                em.close();
                throw(ex);
            }
        }
        return (contribuyente);
    }



}
