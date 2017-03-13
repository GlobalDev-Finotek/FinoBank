package finotek.global.dev.talkbank_ca;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Random;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {

  ArrayList<String> completeSets = new ArrayList<>();

  @Test
  public void testGetRandomNumberSeq() throws Exception {

	  System.out.print(createRandomizedStringSeq());
	  assertEquals(true, completeSets.containsAll(createRandomizedStringSeq()));
	  assertEquals(true, completeSets.size() == createRandomizedStringSeq().size());
  }

  private List<String> createRandomizedStringSeq() {
	  ArrayList<String> numStrings = new ArrayList<>();

	  Collections.shuffle(completeSets);
	  LinkedList<String> stringLinkedList = new LinkedList<>(completeSets);
	  while(!stringLinkedList.isEmpty()) {
		  numStrings.add(stringLinkedList.pop());
	  }

	  return numStrings;
  }

  @Before
  public void init() {

    completeSets = new ArrayList<>();

	  for (int i = 0; i < 10; ++i) {
		  completeSets.add(String.valueOf(i));
	  }

  }
}