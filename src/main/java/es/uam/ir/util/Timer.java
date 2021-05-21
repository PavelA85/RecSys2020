/*
 * Copyright (C) 2020 Information Retrieval Group at Universidad Autónoma
 * de Madrid, http://ir.ii.uam.es.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package es.uam.ir.util;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Rocío Cañamares
 * @author Pablo Castells
 */
public class Timer {

    private static final Map<Object, Long> now = new HashMap<>();
    private static final Map<Object, Long> prev = new HashMap<>();

    public static final String ANSI_RESET = "\u001B[0m";

    public static final String ANSI_BLACK_BACKGROUND = "\u001B[40m";
    public static final String ANSI_RED_BACKGROUND = "\u001B[30;41m";
    public static final String ANSI_GREEN_BACKGROUND = "\u001B[30;42m";
    public static final String ANSI_YELLOW_BACKGROUND = "\u001B[30;43m";
    public static final String ANSI_BLUE_BACKGROUND = "\u001B[44m";
    public static final String ANSI_PURPLE_BACKGROUND = "\u001B[30;45m";
    public static final String ANSI_CYAN_BACKGROUND = "\u001B[30;46m";
    public static final String ANSI_WHITE_BACKGROUND = "\u001B[30;47m";

    /**
     * @param obj
     * @param msg
     */
    public static void start(Object obj, String... msg) {
        if (msg.length > 0) {
            System.out.println(msg[0]);
//            System.out.println((char) 27 + "[30;46m" + msg[0] + "0m");
//            System.out.print((char) 27 + "[0m");
        }
        prev.put(obj, System.currentTimeMillis());
    }

    /**
     *
     */

    public static void done(Object obj, String msg) {
        done(obj, msg, "green");
    }

    /**
     * @param obj
     * @param msg
     */
    public static void done(Object obj, String msg, String color) {
        now.put(obj, System.currentTimeMillis());
        long ms = now.get(obj) - prev.get(obj);
        int s = (int) (ms / 1000);
        int min = (int) (ms / (1000 * 60));
        int h = (int) (ms / (1000 * 60 * 60));
        int d = (int) (ms / (1000 * 60 * 60 * 24));
        s -= min * 60;
        min -= h * 60;
        h -= d * 24;
        StringBuilder done = new StringBuilder().append(msg).append(" (");
        if (d > 0) {
            done.append(d + " days ");
        }
        if (h > 0) {
            done.append(h + "h ");
        }
        if (min > 0) {
            done.append(min + "min ");
        }
        done.append(s + "s)");

        if (color == "green") {
            System.out.println(ANSI_GREEN_BACKGROUND + done + (char) 27 + ANSI_RESET);
        } else if (color == "red") {
            System.out.println(ANSI_RED_BACKGROUND + done + (char) 27 + ANSI_RESET);
        } else if (color == "yellow") {
            System.out.println(ANSI_YELLOW_BACKGROUND + done + (char) 27 + ANSI_RESET);
        } else if (color == "purple") {
            System.out.println(ANSI_PURPLE_BACKGROUND + done + (char) 27 + ANSI_RESET);
        } else {
            System.out.println(ANSI_BLUE_BACKGROUND + done + (char) 27 + ANSI_RESET);
        }
        prev.put(obj, now.get(obj));
    }

    /*    *//**
     * @param msg
     *//*
    public static void start(String... msg) {
        start(0, msg);
    }

    *//**
     * @param msg
     *//*
    public static void done(String msg) {
        done(0, msg);
    }*/

}
