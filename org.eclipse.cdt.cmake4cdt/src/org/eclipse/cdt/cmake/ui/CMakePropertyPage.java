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

import org.eclipse.cdt.ui.newui.AbstractPage;


public class CMakePropertyPage extends AbstractPage {
	@Override
	protected boolean isSingle() { return true; }
}




//
//	public CMakePropertyPage() {
////		IResource resource = (IResource) getElement();
////		IProject activeProject = resource.getProject();
////		IEclipsePreferences projectProperties = new ProjectScope(activeProject).getNode("com.rohde_schwarz.buildif.scope");
//	}
//
//	@Override
//	protected Control createContents(Composite parent) {
//		Composite myComposite = new Composite(parent, SWT.NONE);
//		GridLayout mylayout = new GridLayout();
//		mylayout.marginHeight = 1;
//		mylayout.marginWidth = 1;
//		myComposite.setLayout(mylayout);
//
//		deviceSpecificBtn = new Button(myComposite, SWT.CHECK);
//		deviceSpecificBtn.setLayoutData(new GridData());
//		deviceSpecificBtn.setText("Project is instrument specific");
//		deviceSpecificBtn.setSelection( getIsInstrumentSpecific() );
//
//		Label buildDirLabel = new Label(myComposite, SWT.NONE);
//		buildDirLabel.setLayoutData(new GridData());
//		buildDirLabel.setText("Build in:");
//
//		useWorkspaceSettings = new Button(myComposite, SWT.CHECK);
//		useWorkspaceSettings.setLayoutData(new GridData());
//		useWorkspaceSettings.setText("Use Workspace settings for build dir");
//		useWorkspaceSettings.setSelection( getIsWorkspaceSpecific() );
//		useWorkspaceSettings.addSelectionListener(new SelectionAdapter() {
//		      public void widgetSelected(SelectionEvent event) {
//		    	  setIsWorkspaceSpecific( ((Button) event.getSource()).getSelection()  );
//		      }
//		});
//		
//		buildDirEntry = new Composite(myComposite, SWT.NONE);
//		
//		GridLayout bdlayout = new GridLayout(3, false);
//		bdlayout.marginHeight = 1;
//		bdlayout.marginWidth = 1;
//		buildDirEntry.setLayout(bdlayout);
//
//		buildDirTextField = new Text(buildDirEntry, SWT.BORDER);
//		buildDirTextField.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
//		buildDirTextField.setText( getProjectSpecificBuildDir() );
//
//		Button browseBtn = new Button(buildDirEntry, SWT.PUSH);
//		browseBtn.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));
//		browseBtn.setText("Browse");
//		browseBtn.addSelectionListener(new SelectionAdapter() {
//            public void widgetSelected(SelectionEvent evt) {
//                String newValue = browsePressed();
//                if (newValue != null) {
//                	buildDirTextField.setText(newValue);
//                }
//            }
//        });
//
//		Button varsBtn = new Button(buildDirEntry, SWT.PUSH);
//		varsBtn.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));
//		varsBtn.setText("Variables");
//		varsBtn.addSelectionListener(new SelectionAdapter() {
//            public void widgetSelected(SelectionEvent evt) {
//                String newValue = variablesPressed();
//                if (newValue != null) {
//                	buildDirTextField.setText(newValue);
//                }
//            }
//        });
//
//
//		buildDirEntry.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
//		setIsWorkspaceSpecific( getIsWorkspaceSpecific() );
//
//		return myComposite;
//	}
//	
//	private boolean getIsWorkspaceSpecific() {
//		Object input = getElement();
//		IResource resource = null;
//		if (input instanceof IResource) {
//		    resource = (IResource) getElement();
//			IProject activeProject = resource.getProject();
//			IEclipsePreferences projectProperties = new ProjectScope(activeProject).getNode("com.rohde_schwarz.buildif.scope");
//			boolean value = false;
//			if (projectProperties != null) {
//				value = projectProperties.getBoolean(CMakePropertyConstants.P_USE_WORKSPACE_BUILDDIR_SETTINGS, false);
//			}
//			return value;
//		}
//		else {
//			return false;
//		}
//	}
//
//	private void setIsWorkspaceSpecific(boolean isSpecific) {
//		useWorkspaceSettings.setSelection(isSpecific);
//		buildDirEntry.setEnabled(!isSpecific);
//		for (Control child : buildDirEntry.getChildren()) {
//			  child.setEnabled(!isSpecific);
//		}
//	}
//
//	protected boolean getIsInstrumentSpecific() {
//		Object input = getElement();
//		IResource resource = null;
//		if (input instanceof IResource) {
//            resource = (IResource)input;
//            IProject activeProject = resource.getProject();
//            IEclipsePreferences projectProperties = new ProjectScope(activeProject).getNode("com.rohde_schwarz.buildif.scope");
//            boolean value = false;
//			if (projectProperties != null) {
//				value = projectProperties.getBoolean(CMakePropertyConstants.P_IS_DEVICE_SPECIFIC, false);
//			}
//			return value;
//		}
//		else {
//			return false;
//		}
//	}
//	
//	protected String getProjectSpecificBuildDir() {
//		Object input = getElement();
//		IResource resource = null;
//		if (input instanceof IResource) {
//		    resource = (IResource) getElement();
//			IProject activeProject = resource.getProject();
//			IEclipsePreferences projectProperties = new ProjectScope(activeProject).getNode("com.rohde_schwarz.buildif.scope");
//			String buildDirStr = Activator.getDefault().getPreferenceStore().getString(PreferenceConstants.P_BUILDDIR);
//			if (projectProperties != null) {
//				buildDirStr = projectProperties.get(CMakePropertyConstants.P_BUILD_PATH, buildDirStr);
//			}
//			return buildDirStr;
//		}
//		else {
//			return new String();
//		}
//	}
//	
//	public String browsePressed() {
//		File f = new File(buildDirTextField.getText());
//		if (!f.exists()) {
//			f = null;
//		}
//		File d = getDirectory(f);
//		if (d == null) {
//			return null;
//		}
//
//		return d.getAbsolutePath();
//	}
//
//	public String variablesPressed() {
//		StringVariableSelectionDialog varDlg = new StringVariableSelectionDialog(getShell());
//		varDlg.open();
//		String variableExpression = varDlg.getVariableExpression();
//
//		String currentDestdir = buildDirTextField.getText();
//
//		if(variableExpression == null) {
//			return currentDestdir;
//		}
//
//		Point pt = buildDirTextField.getSelection();
//
//		String result = currentDestdir.substring(0, pt.x);
//		result = result.concat(variableExpression);
//		result = result.concat(currentDestdir.substring(pt.y, currentDestdir.length()));
//
//		return result;
//	}
//
//    /**
//     * Helper that opens the directory chooser dialog.
//     * @param startingDirectory The directory the dialog will open in.
//     * @return File File or <code>null</code>.
//     * 
//     */
//    private File getDirectory(File startingDirectory) {
//
//        DirectoryDialog fileDialog = new DirectoryDialog(getShell(), SWT.OPEN | SWT.SHEET);
//        if (startingDirectory != null) {
//			fileDialog.setFilterPath(startingDirectory.getPath());
//		}
//        else if (filterPath != null) {
//        	fileDialog.setFilterPath(filterPath.getPath());
//        }
//        String dir = fileDialog.open();
//        if (dir != null) {
//            dir = dir.trim();
//            if (dir.length() > 0) {
//				return new File(dir);
//			}
//        }
//
//        return null;
//    }
//
//
//	
//	public void performApply() {
//		Object input = getElement();
//		IResource resource = null;
//		if (input instanceof IResource) {
//		    resource = (IResource) getElement();
//    		IProject activeProject = resource.getProject();
//	    	IEclipsePreferences projectProperties = new ProjectScope(activeProject).getNode("com.rohde_schwarz.buildif.scope");
//
//			if (projectProperties != null) {
//				try {
//					projectProperties.putBoolean(CMakePropertyConstants.P_IS_DEVICE_SPECIFIC, deviceSpecificBtn.getSelection());
//					projectProperties.putBoolean(CMakePropertyConstants.P_USE_WORKSPACE_BUILDDIR_SETTINGS, useWorkspaceSettings.getSelection());
//					if(!getIsWorkspaceSpecific()) {
//						// build dir is project specific
//						projectProperties.put(CMakePropertyConstants.P_BUILD_PATH, buildDirTextField.getText());
//					}
//					projectProperties.flush();
//				}
//				catch(BackingStoreException beex) {
//				}
//			}
//		}
//	}
//
//	public void performDefaults() {
//		deviceSpecificBtn.setSelection(false);
//		setIsWorkspaceSpecific(true);
//		String buildDirStr = Activator.getDefault().getPreferenceStore().getString(PreferenceConstants.P_BUILDDIR);
//		buildDirTextField.setText( buildDirStr );
//	}
//
//	public boolean performOk() {
//		performApply();
//		return true;
//	}
//	
//	public boolean performCancel() {
//		System.out.println("performCancel");
//		return true;
//	}
//
//
//}
