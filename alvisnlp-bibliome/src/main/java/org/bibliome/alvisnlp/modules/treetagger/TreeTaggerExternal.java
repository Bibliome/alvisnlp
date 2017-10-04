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


package org.bibliome.alvisnlp.modules.treetagger;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.Map;
import java.util.logging.Logger;

import org.bibliome.util.Strings;

import alvisnlp.module.Annotable;
import alvisnlp.module.Module;
import alvisnlp.module.ModuleException;
import alvisnlp.module.ProcessingContext;
import alvisnlp.module.lib.External;

// TODO: Auto-generated Javadoc
/**
 * The Class TreeTaggerExternal.
 * 
 * @author rbossy
 */
public class TreeTaggerExternal<T extends Annotable> implements External<T> {

    /** The owner. */
    private final Module<T>             owner;

    private final File tmpDir;
    
    /** The tree tagger executable. */
    private final File               treeTaggerExecutable;

    /** The par file. */
    private final File               parFile;

    /** The lexicon. */
    private final Map<String,String> lexicon;

    /** The silent. */
    private boolean                  silent      = false;

    /** The input file. */
    private File                     inputFile   = null;

    /** The output file. */
    private File                     outputFile  = null;

    /** The print stream. */
    private PrintStream              printStream = null;
//    private Writer writer = null;

    /** The reader. */
    private BufferedReader           reader      = null;

    /** The pos tag. */
    private String                   posTag      = null;

    /** The lemma. */
    private String                   lemma       = null;

    private final ProcessingContext<T> ctx;
    
    /**
     * Instantiates a new tree tagger external.
     * @param ctx TODO
     * @param owner
     *            the owner
     * @param treeTaggerExecutable
     *            the tree tagger executable
     * @param parFile
     *            the par file
     * @param lexicon
     *            the lexicon
     */
    public TreeTaggerExternal(ProcessingContext<T> ctx, Module<T> owner, File tmpDir, File treeTaggerExecutable, File parFile, Map<String,String> lexicon) {
        super();
        this.ctx = ctx;
        this.owner = owner;
        this.tmpDir = tmpDir;
        this.treeTaggerExecutable = treeTaggerExecutable;
        this.parFile = parFile;
        this.lexicon = lexicon;
    }

    /**
     * Open input.
     * 
     * @param name
     *            the name
     * @param charset
     *            the charset
     * 
     * @throws FileNotFoundException
     *             the file not found exception
     * @throws UnsupportedEncodingException
     *             the unsupported encoding exception
     */
    public void openInput(String name, String charset) throws FileNotFoundException, UnsupportedEncodingException {
        inputFile = new File(tmpDir, name + ".txt");
        outputFile = new File(tmpDir, name + ".ttg");
        printStream = new PrintStream(inputFile, charset);
//        OutputStream os = new FileOutputStream(inputFile);
//        Charset charsetObj = Charset.forName(charset);
//        CharsetEncoder encoder = charsetObj.newEncoder();
//        encoder.onUnmappableCharacter(CodingErrorAction.REPLACE);
//        encoder.replaceWith(new byte[] { '_' });
//        writer = new OutputStreamWriter(os, encoder);
    }

    /**
     * Adds the input token.
     * 
     * @param token
     *            the token
     * @param posTag
     *            the pos tag
     * @param lemma
     *            the lemma
     * @throws IOException 
     */
    public void addInputToken(String token, String posTag, String lemma) {
        if (printStream == null) {
			throw new IllegalStateException();
		}
//        if (writer == null) {
//			throw new IllegalStateException();
//		}
        token = Strings.normalizeSpace(token);
        if (token.trim().isEmpty())
        	token = ".";
        if (posTag == null) {
            if (lexicon.containsKey(token)) {
				printStream.printf("%s\t%s\n", token, lexicon.get(token));
//            	writer.write(token + '\t' + lexicon.get(token) + '\n');
			} else {
				printStream.println(token);
//				writer.write(token + '\n');
			}
        } else {
//        	System.err.printf("'%s'\t'%s' 1 '%s'\n", token, Strings.normalizeSpace(posTag), lemma == null ? token : Strings.normalizeSpace(lemma));
			printStream.printf("%s\t%s 1 %s\n", token, Strings.normalizeSpace(posTag), lemma == null ? token : Strings.normalizeSpace(lemma));
//			writer.write(token + '\t' + Strings.normalizeSpace(posTag) + " 1 " + (lemma == null ? token : Strings.normalizeSpace(lemma)) + '\n');
//			writer.flush();
		}
    }

    /**
     * Close input.
     * @throws IOException 
     */
    public void closeInput() {
        printStream.close();
        printStream = null;
//        writer.close();
//        writer = null;
    }

    /**
     * Open output.
     * 
     * @param charset
     *            the charset
     * 
     * @throws UnsupportedEncodingException
     *             the unsupported encoding exception
     * @throws FileNotFoundException
     *             the file not found exception
     */
    public void openOutput(String charset) throws UnsupportedEncodingException, FileNotFoundException {
        if (reader != null) {
			throw new IllegalStateException();
		}
        if (outputFile == null) {
			throw new IllegalStateException();
		}
        reader = new BufferedReader(new InputStreamReader(new FileInputStream(outputFile), charset));
    }

    /**
     * Scan next line.
     * 
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    private void scanNextLine() throws IOException {
        if (reader == null) {
			throw new IllegalStateException();
		}
        String line = reader.readLine();
        if (line == null) {
            posTag = null;
            lemma = null;
            return;
        }
        int col = line.indexOf('\t');
        if (col < 0) {
            posTag = null;
            lemma = null;
            return;
        }
        posTag = line.substring(0, col).intern();
        lemma = line.substring(col + 1);
    }

    /**
     * Checks for next.
     * 
     * @return true, if successful
     * 
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    public boolean hasNext() throws IOException {
        scanNextLine();
        return posTag != null;
    }

    /**
     * Gets the pos tag.
     * 
     * @return the pos tag
     */
    public String getPosTag() {
        return posTag;
    }

    /**
     * Gets the lemma.
     * 
     * @return the lemma
     */
    public String getLemma() {
        return lemma;
    }

    /**
     * Close output.
     * 
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    public void closeOutput() throws IOException {
        if (reader == null) {
			throw new IllegalStateException();
		}
        reader.close();
        reader = null;
    }

    /**
     * Sets the silent.
     */
    public void setSilent() {
        silent = false;
    }

    @Override
    public String[] getCommandLineArgs() throws ModuleException {
        return new String[] {
                treeTaggerExecutable.getAbsolutePath(),
                "-quiet",
                "-lemma",
                "-pt-with-lemma",
                "-pt-with-prob",
                parFile.getAbsolutePath(),
                inputFile.getAbsolutePath(),
                outputFile.getAbsolutePath()
        };
    }

    /*
     * (non-Javadoc)
     * 
     * @see alvisnlp.module.lib.External#getEnvironment()
     */
    @Override
    public String[] getEnvironment() throws ModuleException {
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see alvisnlp.module.lib.External#getOwner()
     */
    @Override
    public Module<T> getOwner() {
        return owner;
    }

    /*
     * (non-Javadoc)
     * 
     * @see alvisnlp.module.lib.External#getWorkingDirectory()
     */
    @Override
    public File getWorkingDirectory() throws ModuleException {
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see alvisnlp.module.lib.External#processOutput(java.io.BufferedReader,
     * java.io.BufferedReader)
     */
    @Override
    public void processOutput(BufferedReader out, BufferedReader err) throws ModuleException {
        if (silent) {
			return;
		}
        Logger logger = owner.getLogger(ctx);
        try {
            logger.fine("tree-tagger standard error:");
            for (String line = err.readLine(); line != null; line = err.readLine()) {
                logger.fine("    " + line);
            }
            logger.fine("end of tree-tagger standard error");
        }
        catch (IOException ioe) {
            logger.warning("could not read tree-tagger standard error: " + ioe.getMessage());
        }
    }
}
