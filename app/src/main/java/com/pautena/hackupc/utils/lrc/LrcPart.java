package com.pautena.hackupc.utils.lrc;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by pautenavidal on 4/3/17.
 */

public class LrcPart {
    private List<String> lines;
    private String time = null;

    public LrcPart() {
        lines = new ArrayList<>();
    }

    public List<String> getLines() {
        return lines;
    }

    public boolean isEmpty() {
        return lines.isEmpty();
    }

    public void addLine(String line) {
        String[] parts = line.split("]");
        String timestamp = parts[0];
        String content = parts[1];

        lines.add(content);
        if (time == null) {
            time = timestamp.substring(1,timestamp.length()-1);
        }
    }

    public String getTime() {
        return time;
    }
}
