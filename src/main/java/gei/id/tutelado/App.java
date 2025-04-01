package gei.id.tutelado;

import gei.id.tutelado.configuracion.Configuracion;
import gei.id.tutelado.configuracion.ConfiguracionJPA;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
    	Configuracion cfg;
    	cfg = new ConfiguracionJPA();
    	cfg.start();
    	cfg.endUp();
    }
}

