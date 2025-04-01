package gei.id.tutelado.dao;

import java.util.List;

import gei.id.tutelado.configuracion.Configuracion;
import gei.id.tutelado.model.Usuario;

public interface UsuarioDao {
    	
	void setup (Configuracion config);
	
	// OPERACIONS CRUD BASICAS
	/* MO4.1 */ Usuario recuperaPorNif (String nif);
	/* MO4.2 */ Usuario almacena (Usuario user);
	/* MO4.3 */ void elimina (Usuario user);
	/* MO4.4 */ Usuario modifica (Usuario user);

	// OPERACIONS POR ATRIBUTOS LAZY
	/* MO4.5 */ Usuario restauraEntradasLog (Usuario user);
			// Recibe un usuario coa coleccion de entradas de log como proxy SEN INICIALIZAR
			// Devolve unha copia do usuario coa coleccion de entradas de log INICIALIZADA

	// CONSULTAS JPQL
			//* Aqui irian o resto das consultas que se piden no enunciado (non implementadas)

}
