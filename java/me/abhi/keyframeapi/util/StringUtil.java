package me.abhi.keyframeapi.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class StringUtil {

    public static boolean contains(String[] array, String compare) {
        List<String> list = new ArrayList<>();
        Arrays.stream(array).forEach(s -> list.add(s.toLowerCase()));
        return list.contains(compare.toLowerCase());
    }

    public static String join(List<String> arguments, final int from, int to, final char delimiter) {
        if (to > arguments.size() - 1 || to < 1) {
            to = arguments.size() - 1;
        }
        final StringBuilder builder = new StringBuilder();
        for (int i = from; i <= to; ++i) {
            builder.append(arguments.get(i));
            if (i != to) {
                builder.append(delimiter);
            }
        }
        return builder.toString();
    }

    public static String join(List<String> arguments, final int from, final char delimiter) {
        return join(arguments, from, -1, delimiter);
    }

    public static String join(List<String> arguments, final int from) {
        return join(arguments, from, ' ');
    }

    public static String join(List<String> arguments, final char delimiter) {
        return join(arguments, 0, delimiter);
    }

    public static String join(List<String> arguments) {
        return join(arguments, ' ');
    }

    public static String join(List<String> pieces, String separator) {
        StringBuilder buffer = new StringBuilder();
        Iterator iter = pieces.iterator();

        while (iter.hasNext()) {
            buffer.append((String) iter.next());
            if (iter.hasNext()) {
                buffer.append(separator);
            }
        }

        return buffer.toString();
    }

    public static String convertToTitleCaseIteratingChars(String text) {
        if (text == null || text.isEmpty()) {
            return text;
        }

        StringBuilder converted = new StringBuilder();

        boolean convertNext = true;
        for (char ch : text.toCharArray()) {
            if (Character.isSpaceChar(ch)) {
                convertNext = true;
            } else if (convertNext) {
                ch = Character.toTitleCase(ch);
                convertNext = false;
            } else {
                ch = Character.toLowerCase(ch);
            }
            converted.append(ch);
        }

        return converted.toString();
    }
}
