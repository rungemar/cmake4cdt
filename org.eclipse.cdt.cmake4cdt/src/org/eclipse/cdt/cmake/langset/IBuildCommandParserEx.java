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

package org.eclipse.cdt.cmake.langset;

import org.eclipse.cdt.core.language.settings.providers.ICBuildOutputParser;
import org.eclipse.cdt.core.language.settings.providers.ILanguageSettingsProvider;
import org.eclipse.core.resources.IResource;


public interface IBuildCommandParserEx extends ICBuildOutputParser, ILanguageSettingsProvider {

	class CompileUnitInfo {
		private String parsedResourceName;
		private IResource currentResource;

		
		CompileUnitInfo(String rcName, IResource rc) {
			parsedResourceName=rcName;
			currentResource=rc;
		}
		/**
		 * @return the parsedResourceName
		 */
		public String getParsedResourceName() {
			return parsedResourceName;
		}

		/**
		 * @param parsedResourceName the parsedResourceName to set
		 */
		public void setParsedResourceName(String parsedResourceName) {
			this.parsedResourceName = parsedResourceName;
		}

		/**
		 * @return the currentResource
		 */
		public IResource getCurrentResource() {
			return currentResource;
		}

		/**
		 * @param currentResource the currentResource to set
		 */
		public void setCurrentResource(IResource currentResource) {
			this.currentResource = currentResource;
		}
	}
	
	public CompileUnitInfo getCompileUnitInfo();
	
}
