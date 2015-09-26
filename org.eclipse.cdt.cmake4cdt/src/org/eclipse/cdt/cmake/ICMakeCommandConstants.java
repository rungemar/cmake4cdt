/*******************************************************************************
 * Copyright (c) 2015 Ericsson and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.cdt.cmake;

/**
 * Constants related to the CMake command
 *
 * @noimplement This interface is not intended to be implemented by clients.
 * @noextend This interface is not intended to be extended by clients.
 */
public interface ICMakeCommandConstants {
	public static final String GENERATOR_NMAKE_MAKEFILES = "NMake Makefiles";
	public static final String GENERATOR_NMAKE_MAKEFILES_JOM = "NMake Makefiles JOM";
	public static final String GENERATOR_MSYS_MAKEFILES = "MSYS Makefiles";
	public static final String GENERATOR_MINGW_MAKEFILES = "MinGW Makefiles";
	public static final String GENERATOR_UNIX_MAKEFILES = "Unix Makefiles";
	public static final String GENERATOR_NINJA = "Ninja";
}
