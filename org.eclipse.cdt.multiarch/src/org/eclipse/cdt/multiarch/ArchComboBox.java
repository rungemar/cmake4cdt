package org.eclipse.cdt.multiarch;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.beans.BeanProperties;
import org.eclipse.core.databinding.beans.BeansObservables;
import org.eclipse.core.databinding.observable.list.IObservableList;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.menus.WorkbenchWindowControlContribution;
import org.eclipse.cdt.multiarch.Activator;
import org.eclipse.cdt.multiarch.PreferenceConstants;
import org.eclipse.jface.databinding.swt.IWidgetValueProperty;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.jface.databinding.swt.WidgetProperties;

public class ArchComboBox extends WorkbenchWindowControlContribution {

	public ArchComboBox() {
		// TODO Auto-generated constructor stub
	}

	public ArchComboBox(String id) {
		super(id);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected Control createControl(Composite parent) {
		final Combo combo = new Combo(parent, SWT.NONE | SWT.DROP_DOWN | SWT.READ_ONLY);
		 
		String archsString = Activator.getDefault().getPreferenceStore().getString(PreferenceConstants.P_AVAIL_TARGET_ARCHS);
		String currentArchsStr = Activator.getDefault().getPreferenceStore().getString(PreferenceConstants.P_CURRENT_TARGET_ARCH);
		int currentIndex = 0;
		
		String[] items = archsString.split(";");
		
		for (int i=0; i < items.length; i++) {
			combo.add(items[i]);
			if(currentArchsStr.equals(items[i])) {
				currentIndex = i;
			}
	    }
		
		Settings settings = Activator.getDefault().getSettings();
		DataBindingContext bindingContext = new DataBindingContext();

		// inform Settings about changed selection
		IWidgetValueProperty prop = WidgetProperties.selection();
		IObservableValue widgetValue = prop.observe(combo);
		IObservableValue modelValue = BeanProperties.value(Settings.class, PreferenceConstants.P_CURRENT_TARGET_ARCH ).observe(settings);
		bindingContext.bindValue(widgetValue, modelValue);

		// Change Combo's items if list of available architectures was modified in preference page.
		IObservableList widgetList = SWTObservables.observeItems(combo);
		IObservableList modelList = BeansObservables.observeList(settings, PreferenceConstants.P_AVAIL_TARGET_ARCHS);

		bindingContext.bindList(modelList, widgetList );

		combo.select(currentIndex);

		return combo;	}

}
