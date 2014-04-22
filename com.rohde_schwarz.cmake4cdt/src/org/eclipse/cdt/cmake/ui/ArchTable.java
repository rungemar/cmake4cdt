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

import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Tree;

/**
 * @author runge_m
 *
 */
public class ArchTable extends FieldEditor {

	private TableViewer tableViewer;
	private Composite tableParent;
	
	public ArchTable(String name, String labelText, Composite parent) {
		init(name, labelText);
		createControl(parent);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.preference.FieldEditor#adjustForNumColumns(int)
	 */
	@Override
	protected void adjustForNumColumns(int numColumns) {
		Control control = getLabelControl();
		if (control != null) {
			((GridData) control.getLayoutData()).horizontalSpan = numColumns;
			((GridData) getTableControl().getLayoutData()).horizontalSpan = numColumns;
		} else {
			((GridData) getTableControl().getLayoutData()).horizontalSpan = numColumns;
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.preference.FieldEditor#doFillIntoGrid(org.eclipse.swt.widgets.Composite, int)
	 */
	@Override
	protected void doFillIntoGrid(Composite parent, int numColumns) {
		doFillLabelIntoGrid(parent, numColumns);
		doFillBoxIntoGrid(parent, numColumns);
	}

	protected void doFillLabelIntoGrid(Composite parent, int numColumns) {
		String text = getLabelText();
		if (text != null && text.length() > 0) {
			Control control = getLabelControl(parent);
			GridData gd = new GridData();
			gd.horizontalSpan = numColumns;
			control.setLayoutData(gd);
		}
	}

	protected void doFillBoxIntoGrid(Composite parent, int numColumns) {
		GridData gd;
		Control list = createTableControl(parent);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.verticalAlignment = GridData.FILL;
		gd.horizontalSpan = numColumns;
		gd.grabExcessHorizontalSpace = true;
		gd.grabExcessVerticalSpace = true;
		list.setLayoutData(gd);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.preference.FieldEditor#doLoad()
	 */
	@Override
	protected void doLoad() {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.preference.FieldEditor#doLoadDefault()
	 */
	@Override
	protected void doLoadDefault() {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.preference.FieldEditor#doStore()
	 */
	@Override
	protected void doStore() {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.preference.FieldEditor#getNumberOfControls()
	 */
	@Override
	public int getNumberOfControls() {
		return 2;
	}
	
	@Override
	protected void createControl(Composite parent) {
		GridLayout ly = (GridLayout) parent.getLayout();
		doFillIntoGrid(parent, ly.numColumns);
	}
	
	public Table createTableControl(Composite parent) {
		Table table = getTableControl();
		if (table == null) {
			tableParent = parent;
			tableViewer = new TableViewer(parent, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL | SWT.H_SCROLL | SWT.FULL_SELECTION);
			createColumns(tableViewer);
			table = tableViewer.getTable();
		    table.setHeaderVisible(true);
		    table.setLinesVisible(true);
			table.setFont(parent.getFont());
			tableViewer.setComparator(new ViewerComparator());
		} else {
			checkParent(table, parent);
		}
		return table;
	}

	/**
	 * @param tableViewer2
	 */
	private void createColumns(TableViewer tv) {
		// create a column for the architecture's name
		TableViewerColumn colArchName = new TableViewerColumn(tv, SWT.NONE);
		colArchName.getColumn().setWidth(200);
		colArchName.getColumn().setText("Architecture");
		colArchName.setLabelProvider(new ColumnLabelProvider() {
		  @Override
		  public String getText(Object element) {
		    // Person p = (Person) element;
		    return "x86";
		  }
		});

		// create a column for the architecture's name
		TableViewerColumn colToolchainFile = new TableViewerColumn(tv, SWT.NONE);
		colToolchainFile.getColumn().setWidth(200);
		colToolchainFile.getColumn().setText("CMake toolchain file");
		colToolchainFile.setLabelProvider(new ColumnLabelProvider() {
		  @Override
		  public String getText(Object element) {
		    // Person p = (Person) element;
		    return "Toolchain File";
		  }
		});

	}

	/**
	 * @return
	 */
	private Table getTableControl() {
		if(tableViewer != null) {
			return tableViewer.getTable();
		}
		else {
			return null;
		}
	}



}
