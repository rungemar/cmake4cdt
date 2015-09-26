/*******************************************************************************
 * Copyright (c) 2014 Rohde & Schwarz GmbH & Co. KG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Martin Runge - initial implementation of cmake support
 *******************************************************************************/

package org.eclipse.cdt.cmake;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.runtime.Platform;


public class CMakeInfo extends HashMap<String,String> {
	
	private static final long serialVersionUID = 1L;
	private String cmakeVersion = null;
	
	private static final String knownGenerators[][] = {
		//{"Borland Makefiles", "Generates Borland makefiles."},
		{ICMakeCommandConstants.GENERATOR_NMAKE_MAKEFILES,   "Generates NMake makefiles.", Platform.OS_WIN32},
		{ICMakeCommandConstants.GENERATOR_NMAKE_MAKEFILES_JOM, "Generates JOM makefiles.", Platform.OS_WIN32},
		//{"Watcom WMake", "Generates Watcom WMake makefiles.", Platform.OS_WIN32},
		{ICMakeCommandConstants.GENERATOR_MSYS_MAKEFILES, "Generates MSYS makefiles.", Platform.OS_WIN32},
		{ICMakeCommandConstants.GENERATOR_MINGW_MAKEFILES, "Generates a make file for use with mingw32-make.", Platform.OS_WIN32},
		{ICMakeCommandConstants.GENERATOR_UNIX_MAKEFILES, "Generates standard UNIX makefiles.", Platform.OS_LINUX+","+Platform.OS_WIN32+","+Platform.OS_MACOSX+"unix"},
		{ICMakeCommandConstants.GENERATOR_NINJA, "Generates build.ninja files (experimental).", Platform.OS_LINUX+","+Platform.OS_WIN32+","+Platform.OS_MACOSX},
		{"", "", ""}
	};
	

	/**
	 * @return the cmakeVersion
	 */
	public String getCMakeVersion() {
		return cmakeVersion;
	}
	
	/**
	 * @return the cmakeVersion
	 */
	public void setCMakeVersion(String ver) {
		cmakeVersion = ver;
	}
	
	public static String[][] getKnownGenerators() {
		int size = 0;
		String currentPlatform = Platform.getOS();
		ArrayList gens = new ArrayList<String[]>(knownGenerators.length);
		
		for(int i = 0; ; i++) {
			String name = knownGenerators[i][0];
			String desc = knownGenerators[i][1];
			String platforms = knownGenerators[i][2];
			
			if(name.isEmpty()) break;

			if(platforms.contains(currentPlatform)) {
				String entry[] = {name, name };
				gens.add(entry);
			}
		}

		String[][] retArray = (String[][]) gens.toArray(new String[gens.size()][2]); 
		return retArray;
	}
	
	void parseStdOut(String stdout) {
		BufferedReader br = new BufferedReader(new StringReader(stdout));

		boolean versionFound = false;
		boolean generatorsStarted = false;
		
		String lastline = null;
		
		String line=null;
		try {
			Pattern cmakeVersionPattern = Pattern.compile("^cmake version (.*)$");

			while( (line=br.readLine()) != null )
			{
				if(!versionFound) {
					Matcher matcher = cmakeVersionPattern.matcher(line);
					while (matcher.find()) {
						cmakeVersion = matcher.group(1);
					}
				}

				if(line.startsWith("Generators")) {
					generatorsStarted = true;
				}
				
				if(generatorsStarted) {
					String genName;
					String genDesc;
					
					int eqidx = line.indexOf('=');
					if(eqidx != -1) {
						genName = line.substring(0, eqidx - 1).trim();
						genDesc = line.substring(eqidx + 1).trim();
						if(genName.isEmpty()) {
							genName = lastline.trim();
						}
						put(genName, genDesc);
					}
				}
				
				lastline = line;
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}

	void parseStdErr(String stderr) {
		
	}


}
