package gei.id.tutelado.dao;

import gei.id.tutelado.configuracion.Configuracion;
import gei.id.tutelado.model.Impuesto;

public interface ImpuestoDao {

    void setup (Configuracion config);

    // OPERACIONS CRUD BASICAS
    /* MO4.1 */ Impuesto recuperaPorCodigo (Long codigo);
    /* MO4.2 */ Impuesto almacena (Impuesto impuesto);
    /* MO4.3 */ void elimina (Impuesto impuesto);
    /* MO4.4 */ Impuesto modifica (Impuesto declaracion);
}
