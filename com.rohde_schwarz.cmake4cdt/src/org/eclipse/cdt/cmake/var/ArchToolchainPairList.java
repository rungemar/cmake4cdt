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

package org.eclipse.cdt.cmake.var;

import java.util.ArrayList;

import org.eclipse.cdt.cmake.Activator;
import org.eclipse.cdt.cmake.ui.PreferenceConstants;


/**
 * @author runge_m
 *
 */
public class ArchToolchainPairList extends ArrayList<ArchToolchainPair> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public void doLoad( boolean loadDefault ) {
		String archsAndToolchains;
		if(loadDefault == true) {
			archsAndToolchains = Activator.getDefault().getPreferenceStore().getDefaultString(PreferenceConstants.P_TOOLCHAIN_FILES);
		}
		else {
			archsAndToolchains = Activator.getDefault().getPreferenceStore().getString(PreferenceConstants.P_TOOLCHAIN_FILES);
		}

		String[] tokens = archsAndToolchains.split(";");
		for(int i=0; i < tokens.length - 1; ) {
			add( new ArchToolchainPair(tokens[i], tokens[i+1]));
			i+=2;
		}
	}

	public void doLoad( ) {
		doLoad(false);
	}
	
	public void doStore() {
		String archsAndToolchains = new String();

		for(int i=0; i < size(); i++ ) {
			archsAndToolchains += get(i).getArchName() + ";" + get(i).getToolchainFile() + ";"; 
		}
		Activator.getDefault().getPreferenceStore().setValue(PreferenceConstants.P_TOOLCHAIN_FILES, archsAndToolchains );

	}

	/**
	 * @param argument
	 * @return
	 */
	public String get(String argument) {
		for(int i=0; i < size(); i++ ) {
			if( get(i).getArchName().equals(argument) ) {
				return get(i).getToolchainFile();
			}
		}
		return null;
	}
}
