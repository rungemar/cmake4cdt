package com.rohde_schwarz.cmake4cdt.ui;


import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.preference.ListEditor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.jface.window.Window;

public class AvailArchsEditor extends ListEditor {

	public AvailArchsEditor(String pAvailArchs, String string,
			Composite fieldEditorParent) {
		super(pAvailArchs, string, fieldEditorParent );
	}

	@Override
	protected String createList(String[] items) {
		StringBuilder result = new StringBuilder();
		
		for (int i=0; i<items.length; i++) {
	       	result.append(items[i]);
	       	result.append(';');
	    }
		
		return result.toString();
	}

	@Override
	protected String getNewInputObject() {

		IInputValidator validator = new IInputValidator() {
			public String isValid(String newText) {
				if(newText.contains("\t \n\r"))
					return "No whitespace characters allowed in architecture name";
				else
					return null;
			}
		};
		InputDialog dialog = new InputDialog(getShell(), 
				"Name of new architecture", 
				"Please specify the name of the new architectire. \n" +
				"I must a name known to buildif, e.g. arm, ppc, x86", 
				null, 
				validator);
		if(dialog.open() == Window.OK) {
			return dialog.getValue();
		}else{
			return null;
		}
	}


	@Override
	protected String[] parseString(String stringList) {
		String[] resultItems = stringList.split(";");
		return resultItems;
	}
	
}
