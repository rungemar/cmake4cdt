/*******************************************************************************
 * Copyright (c) 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.cdt.cmake.ui.wizards;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;

import org.eclipse.cdt.managedbuilder.core.IToolChain;
import org.eclipse.cdt.managedbuilder.core.ManagedBuildManager;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.URIUtil;
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FileFieldEditor;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.progress.WorkbenchJob;



public class ImportCMakeWizardPage extends WizardPage implements IWizardPage {
		
	protected Text cmakelistsLocation;
	protected Text projectName;
	protected Button langcpp;
	protected IWorkspaceRoot root;
	
	protected boolean projectNameSetByUser;

	
	public ImportCMakeWizardPage(String pageName) {
		super(pageName);
		setTitle(pageName); //NON-NLS-1
		setDescription("Import existing code with CMakeLists.txt into the workspace as CMake Project"); //NON-NLS-1
		
		root = ResourcesPlugin.getWorkspace().getRoot();
		projectNameSetByUser = false;
	}

	
	 /* (non-Javadoc)
	 * @see org.eclipse.ui.dialogs.WizardNewFileCreationPage#createLinkTarget()
	 */
	protected void createLinkTarget() {
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.dialogs.WizardNewFileCreationPage#getInitialContents()
	 */
	protected InputStream getInitialContents() {
//		try {
//			return new FileInputStream(new File(cmakeListLocEditor.getStringValue()));
//		} catch (FileNotFoundException e) {
			return null;
//		}
	}
	
	@Override
	public void createControl(Composite parent) {
		Composite comp = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		comp.setLayout(layout);
		comp.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		addSourceSelector(comp);
		addProjectNameSelector(comp);
		addLanguageSelector(comp);
		setControl(comp);
	}

	public void addSourceSelector(Composite parent) {
		Composite comp = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.numColumns = 3;
		comp.setLayout(layout);
		comp.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

		Label l1 = new Label(comp, SWT.NONE);
		l1.setLayoutData(new GridData());
		l1.setText("Location:");
		
		cmakelistsLocation = new Text(comp, SWT.BORDER);
		cmakelistsLocation.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		cmakelistsLocation.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				validatePage();
			}
		});

		Button browse = new Button(comp, SWT.NONE);
		browse.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, true));
		browse.setText("Browse ...");
		browse.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				FileDialog dialog = new FileDialog(cmakelistsLocation.getShell());
				String[] filterExt = { "CMakeLists.txt", "*.txt", "*.cmake", "*.*" };
				dialog.setFilterExtensions(filterExt);
				setMessage("Dialog message");
				String dir = dialog.open();
				if (dir != null)
					cmakelistsLocation.setText(dir);
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
	}

	public void addProjectNameSelector(Composite parent) {
		Composite comp = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		comp.setLayout(layout);
		comp.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

		Label l = new Label(comp, SWT.NONE);
		l.setLayoutData(new GridData());
		l.setText("Project Name:");
		
		projectName = new Text(comp, SWT.BORDER);
		projectName.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		projectName.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				validatePage();
				if (getProjectName().isEmpty()) {
				}
			}
		});
		
		// Note that the modify listener gets called not only when the user enters text but also when we
		// programatically set the field. This listener only gets called when the user modifies the field
		projectName.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
			}
		});
	}
	
	public void addLanguageSelector(Composite parent) {
		Composite comp = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		comp.setLayout(layout);
		comp.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

		Label l = new Label(comp, SWT.NONE);
		l.setLayoutData(new GridData());
		l.setText("CMake is used to build C and C++ projects. If your project is witten in pure C and does not contain C++ code, uncheck C++ here");

		langcpp = new Button(comp, SWT.CHECK);
		langcpp.setText("Project contains C++ Code"); //$NON-NLS-1$
		langcpp.setSelection(true);
	}

	/**
	 * Validates the contents of the page, setting the page error message and Finish button state accordingly
	 * 
	 * @since 8.1
	 */
	protected void validatePage() {
		// Don't generate an error if project name or location is empty, but do disable Finish button.  
		String msg = null;
		boolean complete = true; // ultimately treated as false if msg != null
		
		String name = getProjectName();
		if (name.isEmpty()) {
			complete = false;
		}
		else {
			IStatus status = ResourcesPlugin.getWorkspace().validateName(name, IResource.PROJECT);
			if (!status.isOK()) {
			    msg = status.getMessage();
			}
			else {
				IProject project = root.getProject(name);
				if (project.exists()) {
					msg = "A project named '" + name + "' already exists in this workspace";

				}
	        }
		}
		if (msg == null) {
			String cmakeListsFile = getCMakeList();
			if (cmakeListsFile.isEmpty()) {
				complete = false;
			}
			else {
				final File file= new File(cmakeListsFile);
				if (file.isDirectory()) {
					msg = "is a directory, please select the CMakeLists.txt of the project to import";
				}
				else {
					if(file.exists()) {
						final File projDir = file.getParentFile();
						// Ensure we can create files in the directory.
						if (!projDir.canWrite()) {
							msg = "no write permissions to the the project";
						}
						File dotproject = new File(projDir, ".project");
						if(dotproject.exists()) {
							msg = "Selected location already contains an eclipse project (.project file). Import it using 'File->Import...->General->Existing Projects into Workspace' instead.";
						}
						if(!projectNameSetByUser) {
							// Set the project name to the directory name but not if the user has supplied a name
							// (bugzilla 368987). Use a job to ensure proper sequence of activity, as setting the Text
							// will invoke the listener, which will invoke this method.
							if (!projectNameSetByUser && !name.equals(projDir.getName())) {
								WorkbenchJob wjob = new WorkbenchJob("update project name") { //$NON-NLS-1$
									@Override
									public IStatus runInUIThread(IProgressMonitor monitor) {
										if (!projectName.isDisposed()) {
											projectName.setText(projDir.getName());
										}
										return Status.OK_STATUS;
									}
								};
								wjob.setSystem(true);
								wjob.schedule();
							}
						}
						else {
							msg = "'" + cmakeListsFile + "' does not exist";
						}
					}
				}
			}
		}
		setErrorMessage(msg);
		setPageComplete((msg == null) && complete);
	}


	public String getProjectName() {
		return projectName.getText().trim();
	}

	public String getLocation() {
		File f = new File(cmakelistsLocation.getText().trim());
		return f.getParent();
	}
	
	public String getCMakeList() {
		return cmakelistsLocation.getText().trim();
	}

	public boolean isCPP() {
		return langcpp.getSelection();
	}


	/**
	 * @return
	 */
	public URI getLocationURI() {
		URI locURI = null;
		try {
			locURI = new URI("file", null, getLocation(), null );
		} catch (URISyntaxException e) {
			e.printStackTrace();
			locURI = null;
		}
		
		return locURI;
	}
}
