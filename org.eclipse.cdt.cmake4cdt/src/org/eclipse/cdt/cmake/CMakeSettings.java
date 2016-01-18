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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.HashMap;

public class CMakeSettings implements PropertyChangeListener { 

	private HashMap<String, CXCompInfo> mXCompInfos = new HashMap<String, CXCompInfo>();

//	public CMakeSettings() { 


//		mXCompInfos = new HashMap<String, CXCompInfo>();
//	}
	
	public CXCompInfo getXCompInfo(String projectName, String buildConfig) {
		if(mXCompInfos.containsKey(genKey(projectName, buildConfig) )) {
			return mXCompInfos.get(genKey(projectName, buildConfig));
		}
		else return null;
	}
	
	public void setXCompInfo(CXCompInfo xci) {
		mXCompInfos.put(xci.getConfigName(), xci);
	}
	
	String genKey(String projectName, String buildConfig) {
		String key = projectName + "/" + buildConfig;
		return key;
	}
	
	private PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);	

	public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
		propertyChangeSupport.addPropertyChangeListener(propertyName, listener);
	}

	public void removePropertyChangeListener(PropertyChangeListener listener) {
		propertyChangeSupport.removePropertyChangeListener(listener);
	}

	/* (non-Javadoc)
	 * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
	 */
	@Override
	public void propertyChange(PropertyChangeEvent arg0) {
		// TODO Auto-generated method stub
	}

}
