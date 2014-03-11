/*******************************************************************************
 * Copyright (c) 2000, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Thierry Lach - thierry.lach@bbdodetroit.com - Fix for Bug 37155
 *******************************************************************************/

package com.rohde_schwarz.cmake4cdt.ui;

import java.io.File;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.core.runtime.Assert;
import org.eclipse.debug.ui.StringVariableSelectionDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Shell;

/**
 * An abstract field editor for a string type preference that presents
 * a string input field with a change button to its right to edit the
 * input field's content. When the user presses the change button, the
 * abstract framework method <code>changePressed()</code> gets called
 * to compute a new string.
 */
public class DestdirFieldEditor extends StringFieldEditor {

    /**
     * The browse button
     */
    private Button browseButton;

    /**
     * The text for the browse button
     */
    private String browseButtonText;

    /**
     * The button to open the variables selection dialog 
     */
    private Button variablesButton;

    /**
     * The text for the variables selection button.
     */
    private String variablesButtonText;

    /**
     * Creates a new destdir field editor. 
     */
    public DestdirFieldEditor() {
    }

    private File filterPath = null;

    /**
     * Creates a destdir field editor.
     * 
     * @param name the name of the preference this field editor works on
     * @param labelText the label text of the field editor
     * @param parent the parent of the field editor's control
     */
    public DestdirFieldEditor(String name, String labelText,
            Composite parent) {
        init(name, labelText);
        createControl(parent);
    }

    /* (non-Javadoc)
     * Method declared on FieldEditor.
     */
    public void adjustForNumColumns(int numColumns) {
        ((GridData) getTextControl().getLayoutData()).horizontalSpan = numColumns - 3;
    }

    /**
     * Notifies that this field editor's browse button has been pressed.
     *
     * @return the new string to display, or <code>null</code> to leave the
     *  old string showing
     */
    public String browsePressed() {
        File f = new File(getTextControl().getText());
        if (!f.exists()) {
			f = null;
		}
        File d = getDirectory(f);
        if (d == null) {
			return null;
		}

        return d.getAbsolutePath();
   }

    public String variablesPressed() {
    	StringVariableSelectionDialog varDlg = new StringVariableSelectionDialog(getShell());
    	varDlg.open();
    	String variableExpression = varDlg.getVariableExpression();
    	
    	String currentDestdir = getTextControl().getText();
    	
    	if(variableExpression == null) {
    		return currentDestdir;
    	}
    	
    	Point pt = getTextControl().getSelection();
    	
    	String result = currentDestdir.substring(0, pt.x);
    	result = result.concat(variableExpression);
    	result = result.concat(currentDestdir.substring(pt.y, currentDestdir.length()));
    	
		return result;
    }

    /* (non-Javadoc)
     * Method declared on StringFieldEditor (and FieldEditor).
     */
    public void doFillIntoGrid(Composite parent, int numColumns) {
        super.doFillIntoGrid(parent, numColumns - 2);
        browseButton = getBrowseControl(parent);
        GridData browsegd = new GridData();
        browsegd.horizontalAlignment = GridData.FILL;
        int widthHint = convertHorizontalDLUsToPixels(browseButton, IDialogConstants.BUTTON_WIDTH);
        browsegd.widthHint = Math.max(widthHint, browseButton.computeSize(SWT.DEFAULT, SWT.DEFAULT, true).x);
        browseButton.setLayoutData(browsegd);

        variablesButton = getVarControl(parent);
        GridData vargd = new GridData();
        vargd.horizontalAlignment = GridData.FILL;
        widthHint = convertHorizontalDLUsToPixels(variablesButton, IDialogConstants.BUTTON_WIDTH);
        vargd.widthHint = Math.max(widthHint, variablesButton.computeSize(SWT.DEFAULT, SWT.DEFAULT, true).x);
        variablesButton.setLayoutData(vargd);
    }

    /**
     * Get the change control. Create it in parent if required.
     * @param parent
     * @return Button
     */
    public Button getBrowseControl(Composite parent) {
        if (browseButton == null) {
            browseButton = new Button(parent, SWT.PUSH);
            if (browseButtonText == null) {
				browseButtonText = "Browse ..."; //$NON-NLS-1$
			}
            browseButton.setText(browseButtonText);
            browseButton.setFont(parent.getFont());
            browseButton.addSelectionListener(new SelectionAdapter() {
                public void widgetSelected(SelectionEvent evt) {
                    String newValue = browsePressed();
                    if (newValue != null) {
                        setStringValue(newValue);
                    }
                }
            });
            browseButton.addDisposeListener(new DisposeListener() {
                public void widgetDisposed(DisposeEvent event) {
                    browseButton = null;
                }
            });
        } else {
            checkParent(browseButton, parent);
        }
        return browseButton;
    }

    /**
     * Get the variables control. Create it in parent if required.
     * @param parent
     * @return Button
     */
    public Button getVarControl(Composite parent) {
        if (variablesButton == null) {
        	variablesButton = new Button(parent, SWT.PUSH);
            if (variablesButtonText == null) {
            	variablesButtonText = "Variables ..."; //$NON-NLS-1$
			}
            variablesButton.setText(variablesButtonText);
            variablesButton.setFont(parent.getFont());
            variablesButton.addSelectionListener(new SelectionAdapter() {
                public void widgetSelected(SelectionEvent evt) {
                    String newValue = variablesPressed();
                    if (newValue != null) {
                        setStringValue(newValue);
                    }
                }
            });
            variablesButton.addDisposeListener(new DisposeListener() {
                public void widgetDisposed(DisposeEvent event) {
                	variablesButton = null;
                }
            });
        } else {
            checkParent(variablesButton, parent);
        }
        return variablesButton;
    }

    /* (non-Javadoc)
     * Method declared on FieldEditor.
     */
    public int getNumberOfControls() {
        return 4;
    }

    /**
     * Returns this field editor's shell.
     *
     * @return the shell
     */
    public Shell getShell() {
        if (browseButton == null) {
			return null;
		}
        return browseButton.getShell();
    }

    /**
     * Sets the text of the change button.
     *
     * @param text the new text
     */
    public void setbrowseButtonText(String text) {
        Assert.isNotNull(text);
        browseButtonText = text;
        if (browseButton != null) {
			browseButton.setText(text);
			Point prefSize = browseButton.computeSize(SWT.DEFAULT, SWT.DEFAULT);
			GridData data = (GridData)browseButton.getLayoutData();
			data.widthHint = Math.max(SWT.DEFAULT, prefSize.x);
		}
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.preference.FieldEditor#setEnabled(boolean, org.eclipse.swt.widgets.Composite)
     */
    public void setEnabled(boolean enabled, Composite parent) {
        super.setEnabled(enabled, parent);
        if (browseButton != null) {
            browseButton.setEnabled(enabled);
        }
    }
    
    /**
     * Helper that opens the directory chooser dialog.
     * @param startingDirectory The directory the dialog will open in.
     * @return File File or <code>null</code>.
     * 
     */
    private File getDirectory(File startingDirectory) {

        DirectoryDialog fileDialog = new DirectoryDialog(getShell(), SWT.OPEN | SWT.SHEET);
        if (startingDirectory != null) {
			fileDialog.setFilterPath(startingDirectory.getPath());
		}
        else if (filterPath != null) {
        	fileDialog.setFilterPath(filterPath.getPath());
        }
        String dir = fileDialog.open();
        if (dir != null) {
            dir = dir.trim();
            if (dir.length() > 0) {
				return new File(dir);
			}
        }

        return null;
    }

    /**
     * Sets the initial path for the Browse dialog.
     * @param path initial path for the Browse dialog
     * @since 3.6
     */
    public void setFilterPath(File path) {
    	filterPath = path;
    }


}
