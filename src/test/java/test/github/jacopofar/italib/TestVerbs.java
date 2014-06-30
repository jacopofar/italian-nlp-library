package test.github.jacopofar.italib;

import static org.junit.Assert.*;

import java.util.Set;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;

import com.github.jacopofar.italib.ItalianModel;
import com.github.jacopofar.italib.ItalianVerbConjugation;
import com.github.jacopofar.italib.ItalianVerbConjugation.ConjugationException;

public class TestVerbs {

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	private ItalianModel im;

	@Before
	public void setUp() throws Exception {
		im=new ItalianModel();
	}

	@Test
	public void testConjugationOfIrregularVerb() throws ConjugationException {
		//andavamo,andare,indicative imperfect,p,1
		Set<ItalianVerbConjugation> vAndavos = im.getVerbs("andavamo");
		assertTrue("one and only one verb form must correspond to 'andavamo'",vAndavos.size()==1);
		//forcing recognition will return the same result since the verb is already in the database
		vAndavos = im.getVerbs("andavamo",true);
		assertTrue("one and only one verb form must correspond to 'andavamo'",vAndavos.size()==1);
		ItalianVerbConjugation andavamo = vAndavos.iterator().next();
		assertEquals("the infinitive of 'andavamo' is 'andare'",andavamo.getInfinitive(),"andare");
		assertEquals("the form of 'andavamo' is 'indicative imperfect'",andavamo.getMode(),"indicative imperfect");
		assertEquals("the person of 'andavamo' is 'p,1'",andavamo.getPerson(),1);
		assertEquals("the person of 'andavamo' is 'p,1'",andavamo.getNumber(),'p');

		andavamo.setMode("indicative present");
		andavamo.setNumber('s');
		andavamo.setPerson(1);
		assertEquals("the first-person singular indicative present is 'vado'",andavamo.getConjugated(),"vado");
	}
	
	@Test(expected=ConjugationException.class)
	public void testConjugationException() throws ConjugationException{
		ItalianVerbConjugation essere = im.getVerbs("erano").iterator().next();
		assertEquals("'erano' is a form of 'essere'",essere.getInfinitive(),"essere");
		essere.setMode("imperative");
		essere.setPerson(1);
		essere.setNumber('s');
		essere.getConjugated();
	}
	

}
