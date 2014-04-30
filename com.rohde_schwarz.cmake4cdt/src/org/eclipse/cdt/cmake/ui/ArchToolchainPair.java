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

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public class ArchToolchainPair {
  private String archName;
  private String toolchainFile;
  private PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);

  public ArchToolchainPair() {
  }

  public ArchToolchainPair(String archName, String toolchainFile ) {
    super();
    this.archName = archName;
    this.toolchainFile = toolchainFile;
  }

  public void addPropertyChangeListener(String propertyName,
      PropertyChangeListener listener) {
    propertyChangeSupport.addPropertyChangeListener(propertyName, listener);
  }

  public void removePropertyChangeListener(PropertyChangeListener listener) {
    propertyChangeSupport.removePropertyChangeListener(listener);
  }

  public String getArchName() {
    return archName;
  }

  public String getToolchainFile() {
    return toolchainFile;
  }

  public void setArchName(String archName) {
    propertyChangeSupport.firePropertyChange("archName", this.archName,
        this.archName = archName);
  }

  public void setToolchainFile(String toolchainFile) {
    propertyChangeSupport.firePropertyChange("toolchainFile", this.toolchainFile,
        this.toolchainFile = toolchainFile);
  }

  @Override
  public String toString() {
    return archName + ";" + toolchainFile;
  }

}