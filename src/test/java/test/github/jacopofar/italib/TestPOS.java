/**
 * 
 */
package test.github.jacopofar.italib;

import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.github.jacopofar.italib.ItalianModel;

/**
 * @author j.farina
 *
 */
public class TestPOS {

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	private ItalianModel im;
	private String stmt;


	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		im=new ItalianModel();
		stmt="il tuo cane mangiò la mela che ti avevo lasciato, e questo mi farebbe arrabbiare molto se non sapessi che non l'ha fatto apposta";
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testTokenizer() {
		String[] tokens = im.tokenize(stmt);
		
		//tokens presence
		for(String t:tokens)
			assertTrue("the tokenizer gave a token not actually in the statement. Token:"+t+" statement:'"+stmt+"'",stmt.contains(t));
	
		//tokens order
		int lastPos=-1;
		for(String t:tokens){
			assertTrue("tokens order has changed!",stmt.indexOf(t, lastPos)!=-1);
			lastPos=stmt.indexOf(t, lastPos);
		}


	}

}
