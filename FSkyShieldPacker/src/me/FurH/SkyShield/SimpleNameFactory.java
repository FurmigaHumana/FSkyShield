package me.FurH.SkyShield;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author Luis
 */
public class SimpleNameFactory {

    private static final List<String> cache_lower = new ArrayList<String>();

    private final char[]  chars;
    private int index = 0;

    public SimpleNameFactory() {

        List<Character> temp = new ArrayList<Character>();

        char[] characters = new char[] {
            'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
            'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'
        };

        for (char c : characters) {
            temp.add(c);
        }
        
        Collections.shuffle(temp);
        
        this.chars = new char[ temp.size() ];
        
        for (int j1 = 0; j1 < chars.length; j1++) {
            chars[ j1 ] = temp.get( j1 );
        }
    }
    
    public String nextName()
    {
        return name(index++);
    }

    private String name(int index) {

        List<String> cachedNames = cache_lower;

        if (index < cachedNames.size()) {
            return cachedNames.get(index);
        }

        String name = generate(index);
        cachedNames.add(index, name);

        return name;
    }

    private String next(int index) {
        String ret = "";

        int max = chars.length;

        int baseIndex   = index / max;
        int offset      = index % max;

        char next = chars[ offset ];
        
        if (baseIndex == 0) {
            ret += next;
        } else {
            ret += next(baseIndex-1) + next;
        }
        
        return ret;
    }

    private String generate(int index) {

        int p_index = 0;
        String pattern = "#";

        return pattern.replace("#", (next(index - p_index)));
    }
}
