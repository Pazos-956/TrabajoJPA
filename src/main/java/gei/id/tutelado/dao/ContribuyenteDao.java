package gei.id.tutelado.dao;

import gei.id.tutelado.configuracion.Configuracion;
import gei.id.tutelado.model.Contribuyente;
import gei.id.tutelado.model.Declaracion;
import gei.id.tutelado.model.PersonaFisica;


import java.util.List;

public interface ContribuyenteDao {

    void setup (Configuracion config);

    // OPERACIONS CRUD BASICAS
    /* MO4.1 */ Contribuyente recuperaPorNif (String nif);
    /* MO4.2 */ Contribuyente almacena (Contribuyente contribuyente);
    /* MO4.3 */ void elimina (Contribuyente contribuyente);
    /* MO4.4 */ Contribuyente modifica (Contribuyente contribuyente);
    public Contribuyente restauraDeclaraciones(Contribuyente contribuyente);

    // CONSULTAS JPQL
    /* MO4.6.a */ //List<EntradaLog> recuperaTodasUsuario(Usuario u);
    public List<Declaracion> recuperaDeclaracionesContribuyente(Contribuyente contribuyente);
    public List<Contribuyente> contribuyentesPorEstadoCivil(String estadoCivil);
    public List<PersonaFisica> obtenerTodasPersonasFisicas();
    public List<Object> numeroDeclaracionesPorContribuyente();
}