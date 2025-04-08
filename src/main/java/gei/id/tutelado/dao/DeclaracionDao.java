package gei.id.tutelado.dao;

import gei.id.tutelado.configuracion.Configuracion;
import gei.id.tutelado.model.Declaracion;

public interface DeclaracionDao {

    void setup (Configuracion config);

    // OPERACIONS CRUD BASICAS
    /* MO4.1 */ Declaracion recuperaPorNumRef (Long numeroReferencia);
    /* MO4.2 */ Declaracion almacena (Declaracion declaracion);
    /* MO4.3 */ void elimina (Declaracion declaracion);
    /* MO4.4 */ Declaracion modifica (Declaracion declaracion);
}
