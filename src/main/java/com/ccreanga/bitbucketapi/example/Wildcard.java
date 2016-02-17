package com.ccreanga.bitbucketapi.example;

import java.util.regex.Pattern;

public class Wildcard {

    private static String wildcardToRegex(String wildcard){
        int len = wildcard.length();
        StringBuilder s = new StringBuilder(len*2);
        s.append('^');
        for (int i = 0; i < len; i++) {
            char c = wildcard.charAt(i);
            switch(c) {
                case '*':
                    s.append(".*");
                    break;
                case '?':
                    s.append(".");
                    break;
                case '(': case ')': case '[': case ']': case '$':
                case '^': case '.': case '{': case '}': case '|':
                case '\\':
                    s.append("\\");
                    s.append(c);
                    break;
                default:
                    s.append(c);
                    break;
            }
        }
        s.append('$');
        return(s.toString());
    }

    public static boolean matches(String text,String wildcard){
        return Pattern.matches(wildcardToRegex(wildcard),text);
    }




}
