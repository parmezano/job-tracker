package com.jobtracker.repository;

import java.util.ArrayList;
import java.util.List;

public class CsvUtils {
    static String escapeField(String value){
        if (value == null) {
            return "";
        }

        String normalized = value.replace("\r\n", "\n").replace("\n", "\\n");

        boolean needsQuoting = normalized.contains(",") || normalized.contains("\"");
        if (needsQuoting) {
            String escaped = normalized.replace("\"", "\"\"");
            return "\"" + escaped + "\"";
        }
        return normalized;
    }

    static String unescapeField(String value){
        return value.replace("\\n", "\n");
    }

    static String toCsvLine(List<String> fields){
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < fields.size(); i++) {
            if (i > 0) {
                sb.append(",");
            }
            sb.append(escapeField(fields.get(i)));
        }
        return sb.toString();
    }

    public static List<String> parseLine(String line){
        List<String> result = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean insideQuotes = false;

        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);

            if (insideQuotes) {
                if (c == '"') {
                    // It could be either the end of a quotation mark or a screened quotation mark ""
                    boolean nextIsQuote = i + 1 < line.length() && line.charAt(i + 1) == '"';
                    if (nextIsQuote) {
                        current.append('"');
                        i++; // omit the second quotation mark in the pair
                    } else {
                        insideQuotes = false;
                    }
                } else {
                    current.append(c);
                }
            } else {
                if (c == '"') {
                    insideQuotes = true;
                } else if (c == ',') {
                    result.add(current.toString());
                    current.setLength(0);
                } else {
                    current.append(c);
                }
            }
        }
        result.add(current.toString());
        return result;
    }
}
