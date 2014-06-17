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

import java.io.File;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.cdt.cmake.var.ArchToolchainPair;
import org.eclipse.core.internal.runtime.DataArea;
import org.eclipse.core.runtime.Assert;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;


public class ArchToolchainPairEditor extends Dialog {

	private Text txtArch;
	private Text txtToolchainFile;

	private String arch;
	private String toolchainFile;

	private Shell parentShell;

	public ArchToolchainPairEditor(Shell parentShell) {
		super(parentShell);
		this.parentShell = parentShell;
	}
	
	public String getArch() {
		return arch;
	}
	
	public String getToolchainFile() {
		return toolchainFile;
	}
	
	public ArchToolchainPair getPair() {
		return new ArchToolchainPair(getArch(), getToolchainFile());
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite area = (Composite) super.createDialogArea(parent);
		Composite container = new Composite(area, SWT.NONE);
		container.setLayoutData(new GridData(GridData.FILL_BOTH));
		GridLayout layout = new GridLayout(3, false);
		container.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		container.setLayout(layout);

		createHeaders(container);
		createEditors(container);

		return area;
	}

	private void createHeaders(Composite container) {

		GridData dataArch = new GridData();
		dataArch.grabExcessHorizontalSpace = true;
		dataArch.horizontalAlignment = GridData.FILL;
		dataArch.verticalAlignment = GridData.FILL;

		Label lbtArch = new Label(container, SWT.NONE);
		lbtArch.setText(Messages.ArchToolchainPairEditor_Arch);
		lbtArch.setLayoutData(dataArch);

		Label lbtToolchainFile = new Label(container, SWT.NONE);
		lbtToolchainFile.setText(Messages.ArchToolchainPairEditor_ToolchainFile);
		lbtToolchainFile.setLayoutData(dataArch);

		Label lbtEmpty = new Label(container, SWT.NONE);
		// lbtToolchainFile.setText("");
		lbtEmpty.setLayoutData(dataArch);
	}

	private void createEditors(Composite container) {
		GridData dataToolchainFile = new GridData();
		dataToolchainFile.grabExcessHorizontalSpace = true;
		dataToolchainFile.horizontalAlignment = GridData.FILL;
		dataToolchainFile.verticalAlignment = GridData.FILL;
		
		txtArch = new Text(container, SWT.BORDER);
		txtArch.setLayoutData(dataToolchainFile);
		txtArch.addModifyListener( new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				arch = txtArch.getText();
			}
		});

		txtToolchainFile = new Text(container, SWT.BORDER);
		txtToolchainFile.setLayoutData(dataToolchainFile);
		txtToolchainFile.addModifyListener( new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				toolchainFile = txtToolchainFile.getText();
			}
		});
		
		Button browseBtn = new Button(container, SWT.PUSH);
		browseBtn.setText(Messages.ArchToolchainPairEditor_Browse);
		browseBtn.setLayoutData(dataToolchainFile);
		browseBtn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				FileDialog fd = new FileDialog(parentShell);
				String result = fd.open();
				if(result != null) {
					txtToolchainFile.setText(result);
				}
			}
	    });
		
	}


	// overriding this methods allows you to set the
	// title of the custom dialog
	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText(Messages.ArchToolchainPairEditor_NewArchToolchainFilePair);
	}

	@Override
	protected Point getInitialSize() {
		return new Point(450, 200);
	}

} 

