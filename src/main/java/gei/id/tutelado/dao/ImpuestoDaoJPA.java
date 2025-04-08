package gei.id.tutelado.dao;

import gei.id.tutelado.configuracion.Configuracion;
import gei.id.tutelado.model.Impuesto;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.util.ArrayList;
import java.util.List;

public class ImpuestoDaoJPA implements ImpuestoDao {

    private EntityManagerFactory emf;
    private EntityManager em;

    @Override
    public void setup (Configuracion config) {
        this.emf = (EntityManagerFactory) config.get("EMF");
    }

    /* MO4.1 */
    @Override
    public Impuesto recuperaPorCodigo(Long codigo) {
        List<Impuesto> impuestos=new ArrayList<>();

        try {
            em = emf.createEntityManager();
            em.getTransaction().begin();

            impuestos = em.createNamedQuery("Impuesto.recuperaPorCodigo", Impuesto.class).setParameter("codigo", codigo).getResultList();

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

        return (impuestos.isEmpty()?null:impuestos.get(0));
    }

    @Override
    public Impuesto almacena(Impuesto impuesto) {

        try {
            em = emf.createEntityManager();
            em.getTransaction().begin();

            em.persist(impuesto);

            em.getTransaction().commit();
            em.close();

        } catch (Exception ex ) {
            if (em!=null && em.isOpen()) {
                if (em.getTransaction().isActive()) em.getTransaction().rollback();
                em.close();
                throw(ex);
            }
        }
        return impuesto;
    }

    @Override
    public void elimina(Impuesto impuesto) {
        try {

            em = emf.createEntityManager();
            em.getTransaction().begin();

            Impuesto impuestoTmp = em.find (Impuesto.class, impuesto.getId());
            em.remove (impuestoTmp);

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
    public Impuesto modifica(Impuesto impuesto) {

        try {

            em = emf.createEntityManager();
            em.getTransaction().begin();

            impuesto= em.merge (impuesto);

            em.getTransaction().commit();
            em.close();

        } catch (Exception ex ) {
            if (em!=null && em.isOpen()) {
                if (em.getTransaction().isActive()) em.getTransaction().rollback();
                em.close();
                throw(ex);
            }
        }
        return (impuesto);
    }
}


