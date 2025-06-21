package br.com.votify.test;

import java.util.ArrayList;
import java.util.List;

public final class CharCollections {
    public static final char[] ISO_CHARS;

    static {
        List<Character> isoCharsList = new ArrayList<>();
        for (int i = 0; i < Character.MAX_VALUE; i++) {
            char c = (char)i;
            if (Character.isISOControl(c)) {
                isoCharsList.add(c);
            }
        }
        ISO_CHARS = new char[isoCharsList.size()];
        for (int i = 0; i < isoCharsList.size(); i++) {
            ISO_CHARS[i] = isoCharsList.get(i);
        }
    }
}
