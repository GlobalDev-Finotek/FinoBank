package finotek.global.dev.talkbank_ca.widget;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by magyeong-ug on 13/03/2017.
 */

public class SecureKeyboard {


	private List<String> getCompleteRandomizedSeq() {
		ArrayList<String> completeSets = new ArrayList<>();

		for (int i = 0; i < 10; ++i) {
			completeSets.add(String.valueOf(i));
		}
		return completeSets;
	}

	private List<String> createRandomizedStringSeq() {
		ArrayList<String> numStrings = new ArrayList<>();
		List<String> completeSets = getCompleteRandomizedSeq();

		Collections.shuffle(completeSets);
		LinkedList<String> stringLinkedList = new LinkedList<>(completeSets);
		while(!stringLinkedList.isEmpty()) {
			numStrings.add(stringLinkedList.pop());
		}

		return numStrings;
	}
}
