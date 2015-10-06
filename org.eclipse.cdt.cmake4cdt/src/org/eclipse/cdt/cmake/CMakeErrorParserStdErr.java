/*******************************************************************************
 * Copyright (c) 2015 Ericsson and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.cdt.cmake;

import java.net.URI;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.cdt.core.ErrorParserManager;
import org.eclipse.cdt.core.IErrorParser;
import org.eclipse.cdt.core.IMarkerGenerator;
import org.eclipse.cdt.utils.EFSExtensionManager;
import org.eclipse.core.filesystem.URIUtil;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;

/**
 * Error Parser for CMake command on stderr
 */
public class CMakeErrorParserStdErr implements IErrorParser/*, IErrorParser4 */{

	public static final String ID = "org.eclipse.cdt.cmake.errorParserStderr"; //$NON-NLS-1$

	enum ErrorParserState {
		NONE, INTRO, FIlE_PATH,
	}

	private ErrorParserState state = ErrorParserState.NONE;

	/*
	 * CMake Error: Error in cmake code at
	 * /Users/mark/Documents/dev/eclipse-old/workspace/runtime-CDT/testCMake2/
	 * CMakeLists.txt:32: Parse error. Function missing ending ")". End of file
	 * reached.
	 */
	private static Pattern multiLineErrorIntro = Pattern.compile("CMake Error:.*");
	private static Pattern multiLineErrorFilePath = Pattern.compile("(.*):(\\d+):");
	/*
	 * CMake Error at CMakeLists.txt:10 (seasdt):
     *   Unknown CMake command "seasdt".
	 */
	private static Pattern multiLineErrorIntroAndFilePath = Pattern.compile("CMake Error at (.*):(\\d+)\\s*.*:");
	private static Pattern multiLineErrorMessage = Pattern.compile(".*");;

	private String currentError = "";
	private String filePath = "";
	private int lineNumber = 0;

	@Override
	public boolean processLine(String line, ErrorParserManager eoParser) {

		switch (state) {
		case NONE: {
			if (multiLineErrorIntro.matcher(line).matches()) {
				currentError = line;
				state = ErrorParserState.INTRO;
			} else {
				Matcher matcher = multiLineErrorIntroAndFilePath.matcher(line);
				if (matcher.matches()) {
					filePath = matcher.group(1);
					try {
						lineNumber = Integer.parseInt(matcher.group(2));
					} catch (NumberFormatException e) {
						break;
					}
					currentError = "";
					state = ErrorParserState.FIlE_PATH;
				}
			}
			break;
		}
		case INTRO: {
			state = ErrorParserState.NONE;
			Matcher matcher = multiLineErrorFilePath.matcher(line);
			if (matcher.matches()) {
				filePath = matcher.group(1);
				try {
					lineNumber = Integer.parseInt(matcher.group(2));
				} catch (NumberFormatException e) {
					break;
				}
				currentError = "";
				state = ErrorParserState.FIlE_PATH;
			}
			break;
		}
		case FIlE_PATH:
			state = ErrorParserState.NONE;
			Matcher matcher = multiLineErrorMessage.matcher(line);
			if (matcher.matches()) {
				currentError += line;
				IPath path = new Path(filePath);
				URI uri = toURI(path, eoParser);
				path = URIUtil.toPath(uri);
				IFile[] foundFiles = ResourcesPlugin.getWorkspace().getRoot().findFilesForLocationURI(URIUtil.toURI(path));
				IFile resource = foundFiles.length > 0 && foundFiles[0] != null && foundFiles[0].exists() ? foundFiles[0] : null;
				IPath externalPath = resource == null ? path : null;
				eoParser.generateExternalMarker(resource, lineNumber, currentError, IMarkerGenerator.SEVERITY_ERROR_RESOURCE, "", externalPath);
				return true;
			}
			break;
		}

		return false;
	}

	/**
	 * Converts a location {@link IPath} to an {@link URI}.
	 * The returned URI uses the scheme and authority of the current working directory
	 * as returned by {@link #getWorkingDirectoryURI()}
	 *
	 * @param path - the path to convert to URI.
	 * @param errParserManager
	 * @return URI
	 */
	private URI toURI(IPath path, ErrorParserManager errParserManager) {
		URI uri = null;
		URI workingDirectoryURI = errParserManager.getWorkingDirectoryURI();
		if (path.isAbsolute()) {
			uri = EFSExtensionManager.getDefault().createNewURIFromPath(workingDirectoryURI, path.toString());
		} else {
			uri = EFSExtensionManager.getDefault().append(workingDirectoryURI, path.toString());
		}

		return uri;
	}

//	TODO: Uncomment once CDT contains IErrorParser4
//	@Override
//	public int getStreamType() {
//		return IErrorParser4.PARSE_STDERR;
//	}
}
