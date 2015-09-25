/*******************************************************************************
 * Copyright (c) 2015 Ericsson and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.cdt.cmake;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.cdt.core.ErrorParserManager;
import org.eclipse.cdt.core.IErrorParser;
import org.eclipse.cdt.core.IMarkerGenerator;
import org.eclipse.core.filesystem.URIUtil;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;

/**
 * Error Parser for CMake command on stdout
 */
public class CMakeErrorParserStdOut implements IErrorParser/*, IErrorParser4 */{

	public static final String ID = "org.eclipse.cdt.cmake.errorParserStdout"; //$NON-NLS-1$

	// See also "/path/to/CMakeFiles/CMakeOutput.log".
	private static Pattern seeAlso = Pattern.compile("See also \"(.*)\".");

	private int lineNumber = 0;

	@Override
	public boolean processLine(String line, ErrorParserManager eoParser) {

		Matcher seeAlsoMatcher = seeAlso.matcher(line);
		if (seeAlsoMatcher.matches()) {
			String message = seeAlsoMatcher.group();
			String path = seeAlsoMatcher.group(1);
			IPath externalPath = new Path(path);
			IFile[] foundFiles = ResourcesPlugin.getWorkspace().getRoot().findFilesForLocationURI(URIUtil.toURI(externalPath));
			IFile resource = foundFiles.length > 0 && foundFiles[0] != null && foundFiles[0].exists() ? foundFiles[0] : null;
			if (resource != null) {
				externalPath = null;
			}
			eoParser.generateExternalMarker(resource, lineNumber, message, IMarkerGenerator.SEVERITY_INFO, "", externalPath);
			return true;
		}

		return false;
	}

//	TODO: Uncomment once CDT contains IErrorParser4
//	@Override
//	public int getStreamType() {
//		return IErrorParser4.PARSE_STDOUT;
//	}
}
