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

package org.eclipse.cdt.cmake.ui;

import org.eclipse.osgi.util.NLS;

/**
 * @author runge_m
 *
 */
public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.eclipse.cdt.cmake.ui.messages"; //$NON-NLS-1$
	public static String CMakeProjectGenerator_0 = null;
	public static String ArchToolchainPairEditor_Arch;
	public static String ArchToolchainPairEditor_Browse;
	public static String ArchToolchainPairEditor_NewArchToolchainFilePair;
	public static String ArchToolchainPairEditor_ToolchainFile;
	public static String ArchTableEditor_Add;
	public static String ArchTableEditor_Remove;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
