/*
 * YUI Compressor
 * Author: Julien Lecomte <jlecomte@yahoo-inc.com>
 * Copyright (c) 2007, Yahoo! Inc. All rights reserved.
 * Code licensed under the BSD License:
 *     http://developer.yahoo.net/yui/license.txt
 */

package org.qrone.r7;

import jargs.gnu.CmdLineParser;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;

import org.qrone.r7.app.AwtImageBufferService;
import org.qrone.r7.parser.HTML5Deck;
import org.qrone.r7.parser.HTML5OM;
import org.qrone.r7.resolver.FileResolver;
import org.qrone.r7.resolver.URIResolver;
import org.qrone.util.QrONEUtils;

public class QrONECompressor {
	
	public static HTML5Deck deck;
	public static boolean verbose;
	

    public static void main(String args[]) {
    	if(args.length == 0 
    			&& System.getProperty("os.name").toLowerCase().indexOf("windows") >= 0){
    		QrONEApp.main(args);
            System.exit(0); 
    	} 
    	
    	long timer = System.currentTimeMillis();

        CmdLineParser parser = new CmdLineParser();
        CmdLineParser.Option helpOpt     = parser.addBooleanOption('h', "help");
        CmdLineParser.Option languageOpt = parser.addStringOption('l', "lang");
        CmdLineParser.Option recurseOpt  = parser.addBooleanOption('r', "recurse");
        CmdLineParser.Option imagedirOpt = parser.addStringOption('i', "img-basedir");
        CmdLineParser.Option noImagesOpt = parser.addBooleanOption('n', "noimages");
        CmdLineParser.Option charsetOpt  = parser.addStringOption("charset");
        CmdLineParser.Option verboseOpt  = parser.addBooleanOption('v', "verbose");

        try {
			parser.parse(args);
		} catch (CmdLineParser.OptionException e) {
            usage();
            System.exit(1);
        }
		
        Boolean help = (Boolean) parser.getOptionValue(helpOpt);
        if (help != null && help.booleanValue()) {
            usage();
            System.exit(0);
        }
        
        boolean noimages = parser.getOptionValue(noImagesOpt) != null;

        verbose = parser.getOptionValue(verboseOpt) != null;

        String charset = (String) parser.getOptionValue(charsetOpt);
        if (charset == null || !Charset.isSupported(charset)) {
            //charset = System.getProperty("file.encoding");
            if (charset == null) {
                charset = "UTF-8";
            }
            if (verbose) {
                System.err.println("[INFO] Using charset " + charset);
            }
        }
        
        String lang			  = (String) parser.getOptionValue(languageOpt);
        
        if(lang == null) lang = "html";
        else lang = lang.toLowerCase();
        
        /*
        Writer out = null;
		try {
			out = new OutputStreamWriter(System.out, charset);
		} catch (UnsupportedEncodingException e) {
            System.err.println("[ERROR] Using charset " + charset);
            System.exit(0);
		}
        */
        String imgdir = (String) parser.getOptionValue(imagedirOpt);

        boolean recurse = parser.getOptionValue(recurseOpt) != null;

        String[] fileArgs = parser.getRemainingArgs();
        if(fileArgs.length < 1){
            usage();
            System.exit(0);
        }
        
        File target = new File(fileArgs[0]).getAbsoluteFile();
        File basedir;
        String path;
        if(target.isDirectory()){
        	basedir = target;
        	path = "";
        }else{
        	basedir = target.getParentFile();
        	path = target.getName();
        }
        
        URIResolver resolver = new FileResolver(basedir);
        if(deck == null)
        	deck = new HTML5Deck(resolver, new AwtImageBufferService());

        long extime = System.currentTimeMillis();
		ExtensionIndex ei = new ExtensionIndex();
		ei.find();
		ei.pack(resolver);
		ei.extend(deck);
		extime = System.currentTimeMillis() - extime;
		if (verbose) {
            System.err.println("[INFO] Class finding time " + extime + "ms");
        }
		if (verbose) {
            System.err.println("[INFO] Pack extension classes.");
        }
		
        try {
	    	if(imgdir != null){
	    		deck.getSpriter().setBaseURI(QrONEUtils.relativize(new URI(fileArgs[0]),new URI(imgdir)));
	    	}
		} catch (URISyntaxException e) {
			e.printStackTrace();
            System.exit(0);
		}
        
        compile(deck, target, path, lang, recurse);
        
		if(!noimages){
			if (verbose) {
	            System.err.println("[INFO] Writing sprite images.");
	        }
			
			try {
				deck.getSpriter().create();
			} catch (IOException e) {
	            System.err.println("[ERROR] Creating sprite images.");
	            System.exit(0);
			}
			
			if (verbose) {
	            System.err.println("[INFO] Writing sprite images done.");
	        }
		}
		
		if (verbose) {
	    	timer = System.currentTimeMillis() - timer;
            System.err.println("\n[INFO] Compilation time " + timer + "ms");
        }
    }
    
	public static void compile(HTML5Deck deck, File file, String path, String lang, boolean recurse){
		if(file.isDirectory()){
			if (verbose) {
	            System.err.println("[INFO] Directory " + path);
	        }
			File[] files = file.listFiles();
			for (int i = 0; i < files.length; i++) {
				if( files[i].isDirectory() && recurse && !files[i].getName().startsWith("."))
					compile(deck, files[i], path + "/" + files[i].getName(), lang, recurse);
				else if( files[i].getName().indexOf("-min.") < 0 &&
						(files[i].getName().endsWith(".html") || files[i].getName().endsWith(".htm")) )
					compile(deck, files[i], path + "/" + files[i].getName(), lang, recurse);
			}
		}else{
			if (verbose) {
	            System.err.println("[INFO] Parsing " + path);
	        }
			try {
				HTML5OM xom = deck.compile(new URI(path));
				xom.serialize();
			} catch (URISyntaxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			/*
	        if(lang == null) lang = "html";
	        else lang = lang.toLowerCase();
	        
			String basename = file.getName().substring(0, 
					file.getName().indexOf('.'));
			File outfile = new File(file.getParentFile(),basename + "-min." + lang);
		
			
			FileWriter out = null;
			try {
				out = new FileWriter(outfile);
			} catch (IOException e1) {
		        System.err.println("[ERROR] Opening file " + outfile.getName());
		        System.exit(0);
			}
			
		    try {
				
				HTML5OM xom = deck.compile(new URI(path));
				out.write(xom.serialize());
				//xom.serialize(lang);
				if (verbose) {
		            System.err.println("[INFO] Writing " + outfile.getName() + " done.");
		        }
			} catch (IOException e) {
				e.printStackTrace();
		        System.err.println("[ERROR] Parsing file " + file.getName());
		        System.exit(0);
			} catch (URISyntaxException e) {
				e.printStackTrace();
		        System.err.println("[ERROR] Parsing file " + file.getName());
		        System.exit(0);
			} finally {
				try {
					out.flush();
					out.close();
				} catch (IOException e) {
				}
			}
			*/
		}
	}
	
	
    private static void usage() {
        System.out.println(
                "\nUsage: java -jar qrone-x.y.z.jar [options] <file>\n\n"

                        + "Options\n"
                        + "  -h, --help                Displays this information\n"
                        + "  -v, --verbose             Display informational messages and warnings\n"
                        + "  --charset <charset>       Read the input file using <charset>\n"
                        + "\n"
                        + "  -l <language>             Output language, default is 'html'\n"
                        + "  -i, --img-basedir <dir>   CSS Sprite image directory\n"
                        + "  -u, --img-baseurl <url>   CSS Sprite image base url\n"
                        
                        );
    }
}
