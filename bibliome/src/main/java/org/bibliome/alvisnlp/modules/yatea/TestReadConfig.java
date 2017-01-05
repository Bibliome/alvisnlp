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


package org.bibliome.alvisnlp.modules.yatea;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bibliome.util.streams.FileSourceStream;
import org.bibliome.util.streams.SourceStream;

public class TestReadConfig {
	public static void main(String[] args) throws IOException {
		SourceStream src = new FileSourceStream("UTF-8", args[0]);
		Properties defaultConfig = new Properties();
		Properties options = new Properties();
		readConfig(src, defaultConfig, options);
		defaultConfig.list(System.err);
		options.list(System.err);
	}
	
	private static final Pattern COMMENT = Pattern.compile("#.*$");
	
	private static String removeComments(String s) {
		Matcher m = COMMENT.matcher(s);
		if (m.find()) {
			int hash = m.start();
			return s.substring(0, hash);
		}
		return s;
	}
	
	private static void readConfig(SourceStream source, Properties defaultConfig, Properties options) throws IOException {
		BufferedReader r = source.getBufferedReader();
		Properties current = null;
		LOOP: while (true) {
			String line = r.readLine();
			if (line == null) {
				break;
			}
			line = removeComments(line).trim();
			if (line.isEmpty()) {
				continue;
			}
			switch (line) {
				case "<DefaultConfig>":
					current = defaultConfig;
					continue LOOP;
				case "</DefaultConfig>":
					current = null;
					continue LOOP;
				case "<OPTIONS>":
					current = options;
					continue LOOP;
				case "</OPTIONS>":
					current = null;
					continue LOOP;
			}
			StringReader sr = new StringReader(line);
			current.load(sr);
		}
		r.close();
	}
}
