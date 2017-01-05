/*
Copyright 2016, 2017 Institut National de la Recherche Agronomique

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/


package alvisnlp.app.cli;


public class XSLTAnsiEscape {
	private static final String BLACK = "\u001B[30m";
	private static final String RED = "\u001B[31m";
	private static final String GREEN = "\u001B[32m";
	private static final String YELLOW = "\u001B[33m";
	private static final String BLUE = "\u001B[34m";
	private static final String MAGENTA = "\u001B[35m";
	private static final String CYAN = "\u001B[36m";
	private static final String WHITE = "\u001B[37m";
	private static final String NO_COLOR = "\u001B[39m";
	private static final String BRIGHT = "\u001B[1m";
	private static final String NO_BRIGHT = "\u001B[22m";
	private static final String RESET = "\u001B[0m";
	private static final String UNDERLINE = "\u001B[4m";
	private static final String NO_UNDERLINE = "\u001B[24m";
	private static final String ITALIC = "\u001B[3m";
	private static final String NO_ITALIC = "\u001B[23m";
	
	public static String reset() {
		return RESET;
	}

	public static String noColor() {
		return NO_COLOR;
	}
	
	public static String noBright() {
		return NO_BRIGHT;
	}
	
	public static String noUnderline() {
		return NO_UNDERLINE;
	}
	
	public static String noItalic() {
		return NO_ITALIC;
	}
	
	public static String black() {
		return BLACK;
	}

	public static String red() {
		return RED;
	}

	public static String green() {
		return GREEN;
	}

	public static String yellow() {
		return YELLOW;
	}

	public static String blue() {
		return BLUE;
	}

	public static String magenta() {
		return MAGENTA;
	}

	public static String cyan() {
		return CYAN;
	}

	public static String white() {
		return WHITE;
	}

	public static String bright() {
		return BRIGHT;
	}

	public static String underline() {
		return UNDERLINE;
	}
	
	public static String italic() {
		return ITALIC;
	}
	
	public static String black(String s) {
		return BLACK + s + NO_COLOR;
	}

	public static String red(String s) {
		return RED + s + NO_COLOR;
	}

	public static String green(String s) {
		return GREEN + s + NO_COLOR;
	}

	public static String yellow(String s) {
		return YELLOW + s + NO_COLOR;
	}

	public static String blue(String s) {
		return BLUE + s + NO_COLOR;
	}

	public static String magenta(String s) {
		return MAGENTA + s + NO_COLOR;
	}

	public static String cyan(String s) {
		return CYAN + s + NO_COLOR;
	}

	public static String white(String s) {
		return WHITE + s + NO_COLOR;
	}

	public static String bright(String s) {
		return BRIGHT + s + NO_BRIGHT;
	}

	public static String underline(String s) {
		return UNDERLINE + s + NO_UNDERLINE;
	}
	
	public static String italic(String s) {
		return ITALIC + s + NO_ITALIC;
	}
}
