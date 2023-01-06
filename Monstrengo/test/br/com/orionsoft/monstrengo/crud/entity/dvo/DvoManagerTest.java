package br.com.orionsoft.monstrengo.crud.entity.dvo;

import junit.framework.TestCase;
import br.com.orionsoft.monstrengo.crud.entity.dvo.DvoException;
import br.com.orionsoft.monstrengo.crud.entity.dvo.DvoManager;
import br.com.orionsoft.monstrengo.crud.entity.dvo.IDvo;
import br.com.orionsoft.monstrengo.crud.entity.dvo.IDvoManager;
import br.com.orionsoft.monstrengo.security.entities.ApplicationUserDvo;
/**
 * Esta classe oferece métodos básicos implementação
 * dos testes unitários.
 * 
 * @author Sergio
 */

public class DvoManagerTest extends TestCase { 

	protected void setUp() throws Exception {
		super.setUp();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testRegisterDvo() {
		
		try {
			IDvoManager dvoMgr = new DvoManager();

			IDvo dvo = new ApplicationUserDvo();
						
			//assertNull(dvoMgr.getDvoByEntity(entity));
			System.out.println("Map antes:  " + dvoMgr.getDvos());
			
			dvoMgr.registerDvo(dvo);
			System.out.println("Dvo Registrado!");
			System.out.println("O nome do cara eh --> " + dvoMgr.getDvos());
			System.out.println("Vou tentar registrar outro dvo sem remover o dvo que ja existe...");
			System.out.println("Removendo o cara!");
			dvoMgr.unregisterDvo(dvo);	
			System.out.println("Map depois da remoção --> "+ dvoMgr.getDvos());
			//***
			System.out.println("O map esta atualmente: " + dvoMgr.getDvos());
			
			System.out.println("Registrando outro cara!");
			
			IDvo dvo2 = new ApplicationUserDvo();
		
			dvoMgr.registerDvo(dvo2);
			System.out.println("Dvo2 Registrado!");
			System.out.println("Map agora: " + dvoMgr.getDvos());  
			
			
			//assertNotNull(dvoMgr.getDvoByEntity(entity));
		
		} catch (DvoException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void testUnregisterDvo() {
		
	}
}