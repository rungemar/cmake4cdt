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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.cdt.cmake.Activator;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.Widget;

/**
 * @author runge_m
 *
 */
public class ArchTable extends FieldEditor {

	private TableViewer tableViewer;
	private Composite tableParent;
	private List<ArchToolchainPair> archlist;

    /**
     * The button box containing the Add and Remove buttons;
     * <code>null</code> if none (before creation or after disposal).
     */
    private Composite buttonBox;

	private Button addButton;
    private Button removeButton;
    private SelectionListener selectionListener;

	
	public ArchTable(String name, String labelText, Composite parent) {
		init(name, labelText);
		createControl(parent);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.preference.FieldEditor#adjustForNumColumns(int)
	 */
	@Override
	protected void adjustForNumColumns(int numColumns) {
		Control labelCcontrol = getLabelControl();
		((GridData) labelCcontrol.getLayoutData()).horizontalSpan = numColumns;
		((GridData) getTableControl().getLayoutData()).horizontalSpan = numColumns - 1;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.preference.FieldEditor#doFillIntoGrid(org.eclipse.swt.widgets.Composite, int)
	 */
	@Override
	protected void doFillIntoGrid(Composite parent, int numColumns) {
		// doFillLabelIntoGrid(parent, numColumns);
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
		Control control = getLabelControl(parent);
        GridData gd = new GridData();
        gd.horizontalSpan = numColumns;
        control.setLayoutData(gd);

        Control table = createTableControl(parent);
        gd = new GridData(GridData.FILL_HORIZONTAL);
        gd.verticalAlignment = GridData.FILL;
        gd.horizontalSpan = numColumns - 1;
        gd.grabExcessHorizontalSpace = true;
        table.setLayoutData(gd);

        buttonBox = getButtonBoxControl(parent);
        gd = new GridData();
        gd.verticalAlignment = GridData.BEGINNING;
        buttonBox.setLayoutData(gd);

	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.preference.FieldEditor#doLoad()
	 */
	@Override
	protected void doLoad() {
		if ( archlist == null) {
			archlist = new ArrayList<ArchToolchainPair>();
		}
		
		String archsAndToolchains = Activator.getDefault().getPreferenceStore().getString(PreferenceConstants.P_TOOLCHAIN_FILES);

		String[] tokens = archsAndToolchains.split(";");
		for(int i=0; i < tokens.length - 1; ) {
			archlist.add( new ArchToolchainPair(tokens[i], tokens[i+1]));
			i+=2;
		}
		tableViewer.setInput( archlist );
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.preference.FieldEditor#doLoadDefault()
	 */
	@Override
	protected void doLoadDefault() {
		if ( archlist == null) {
			archlist = new ArrayList<ArchToolchainPair>();
		}
		
		String archsAndToolchains = Activator.getDefault().getPreferenceStore().getDefaultString(PreferenceConstants.P_TOOLCHAIN_FILES);

		String[] tokens = archsAndToolchains.split(";");
		for(int i=0; i < tokens.length - 1; ) {
			archlist.add( new ArchToolchainPair(tokens[i], tokens[i+1]));
			i+=2;
		}
		tableViewer.setInput( archlist );
		
		archlist.add( new ArchToolchainPair("host", ""));
		archlist.add( new ArchToolchainPair("x86", "/home/buildsys4/cmake/arm-toolchain.cmake"));
		archlist.add( new ArchToolchainPair("x86", "/home/buildsys4/cmake/ppc-toolchain.cmake"));
		archlist.add( new ArchToolchainPair("x86", "/home/buildsys4/cmake/ppc-e500v2-toolchain.cmake"));
		archlist.add( new ArchToolchainPair("x86", "/home/buildsys4/cmake/x86-toolchain.cmake"));
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.preference.FieldEditor#doStore()
	 */
	@Override
	protected void doStore() {
		if(archlist == null) return;
		
		String archsAndToolchains = new String();

		for(int i=0; i < archlist.size(); i++ ) {
			archsAndToolchains += archlist.get(i).getArchName() + ";" + archlist.get(i).getToolchainFile() + ";"; 
		}
		 Activator.getDefault().getPreferenceStore().setValue(PreferenceConstants.P_TOOLCHAIN_FILES, archsAndToolchains );
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.preference.FieldEditor#getNumberOfControls()
	 */
	@Override
	public int getNumberOfControls() {
		return 3;
	}
	
	@Override
	protected void createControl(Composite parent) {
		GridLayout ly = (GridLayout) parent.getLayout();
		doFillIntoGrid(parent, getNumberOfControls());
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
			
			tableViewer.setContentProvider(ArrayContentProvider.getInstance());
			
//			tableViewer.setInput( archlist );
		} else {
			checkParent(table, parent);
		}
		return table;
	}

	/**
	 * @param tableViewer
	 */
	private void createColumns(TableViewer tv) {
		// create a column for the architecture's name
		TableViewerColumn colArchName = new TableViewerColumn(tv, SWT.NONE);
		colArchName.getColumn().setWidth(200);
		colArchName.getColumn().setText("Architecture");
		colArchName.setLabelProvider(new ColumnLabelProvider() {
		  @Override
		  public String getText(Object element) {
		    ArchToolchainPair p = (ArchToolchainPair) element;
		    return p.getArchName();
		  }
		});
		colArchName.setEditingSupport(new ArchCellEditor(tableViewer)); 

		// create a column for the architecture's name
		TableViewerColumn colToolchainFile = new TableViewerColumn(tv, SWT.NONE);
		colToolchainFile.getColumn().setWidth(200);
		colToolchainFile.getColumn().setText("CMake toolchain file");
		colToolchainFile.setLabelProvider(new ColumnLabelProvider() {
		  @Override
		  public String getText(Object element) {
			    ArchToolchainPair p = (ArchToolchainPair) element;
			    return p.getToolchainFile();
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
	
    /**
     * Returns this field editor's button box containing the Add, Remove,
     * Up, and Down button.
     *
     * @param parent the parent control
     * @return the button box
     */
    public Composite getButtonBoxControl(Composite parent) {
        if (buttonBox == null) {
            buttonBox = new Composite(parent, SWT.NULL);
            GridLayout layout = new GridLayout();
            layout.marginWidth = 0;
            buttonBox.setLayout(layout);
            addButton = createPushButton(buttonBox, "ArchTableEditor.add");//$NON-NLS-1$
            removeButton = createPushButton(buttonBox, "ArchTableEditor.remove");//$NON-NLS-1$
            buttonBox.addDisposeListener(new DisposeListener() {
                public void widgetDisposed(DisposeEvent event) {
                    addButton = null;
                    removeButton = null;
                    buttonBox = null;
                }
            });

        } else {
            checkParent(buttonBox, parent);
        }

        selectionChanged();
        return buttonBox;
    }

    /**
     * Helper method to create a push button.
     * 
     * @param parent the parent control
     * @param key the resource name used to supply the button's label text
     * @return Button
     */
    private Button createPushButton(Composite parent, String key) {
        Button button = new Button(parent, SWT.PUSH);
        button.setText(JFaceResources.getString(key));
        button.setFont(parent.getFont());
        GridData data = new GridData(GridData.FILL_HORIZONTAL);
        int widthHint = convertHorizontalDLUsToPixels(button,
                IDialogConstants.BUTTON_WIDTH);
        data.widthHint = Math.max(widthHint, button.computeSize(SWT.DEFAULT,
                SWT.DEFAULT, true).x);
        button.setLayoutData(data);
        button.addSelectionListener(getSelectionListener());
        return button;
    }

    /**
     * Creates a selection listener.
     */
    public void createSelectionListener() {
        selectionListener = new SelectionAdapter() {
            public void widgetSelected(SelectionEvent event) {
                Widget widget = event.widget;
                if (widget == addButton) {
                    addPressed();
                } else if (widget == removeButton) {
                    removePressed();
                } else if (widget == getTableControl()) {
                    selectionChanged();
                }
            }
        };
    }
    
	/**
	 * 
	 */
	protected void selectionChanged() {
	}

	private void addPressed() {
		
	}

	private void removePressed() {
		Table table = getTableControl();
		table.remove(table.getSelectionIndices());
	}
    
    /**
     * Returns this field editor's selection listener.
     * The listener is created if nessessary.
     *
     * @return the selection listener
     */
    private SelectionListener getSelectionListener() {
        if (selectionListener == null) {
			createSelectionListener();
		}
        return selectionListener;
    }


}
