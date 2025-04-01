package gei.id.tutelado.dao;

import java.util.List;

import gei.id.tutelado.configuracion.Configuracion;
import gei.id.tutelado.model.EntradaLog;
import gei.id.tutelado.model.Usuario;

public interface EntradaLogDao {
    
	void setup (Configuracion config);
	
	// OPERACIONS CRUD BASICAS
	/* MO4.1 */ EntradaLog recuperaPorCodigo (String codigo);
	/* MO4.2 */ EntradaLog almacena (EntradaLog log);
	/* MO4.3 */ void elimina (EntradaLog log);
	/* MO4.4 */ EntradaLog modifica (EntradaLog log);

	// CONSULTAS JPQL
	/* MO4.6.a */ List<EntradaLog> recuperaTodasUsuario(Usuario u);

}
