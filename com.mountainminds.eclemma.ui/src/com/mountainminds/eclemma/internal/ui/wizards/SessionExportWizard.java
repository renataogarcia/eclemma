/*******************************************************************************
 * Copyright (c) 2006 Mountainminds GmbH & Co. KG
 * This software is provided under the terms of the Eclipse Public License v1.0
 * See http://www.eclipse.org/legal/epl-v10.html.
 *
 * $Id: $
 ******************************************************************************/
package com.mountainminds.eclemma.internal.ui.wizards;

import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IExportWizard;
import org.eclipse.ui.IWorkbench;

import com.mountainminds.eclemma.internal.ui.EclEmmaUIPlugin;

/**
 * The export wizard for coverage sessions.
 * 
 * @author Marc R. Hoffmann
 * @version $Revision: $
 */
public class SessionExportWizard extends Wizard implements IExportWizard {
  
  private static final String SETTINGSID = "SessionExportWizard"; //$NON-NLS-1$
  
  private SessionExportPage1 page1;
  
  public SessionExportWizard() {
    IDialogSettings pluginsettings = EclEmmaUIPlugin.getInstance().getDialogSettings();
    IDialogSettings wizardsettings = pluginsettings.getSection(SETTINGSID);
    if (wizardsettings == null) {
      wizardsettings = pluginsettings.addNewSection(SETTINGSID);
    }
    setDialogSettings(wizardsettings);
  }

  /* (non-Javadoc)
   * @see org.eclipse.ui.IWorkbenchWizard#init(org.eclipse.ui.IWorkbench, org.eclipse.jface.viewers.IStructuredSelection)
   */
  public void init(IWorkbench workbench, IStructuredSelection selection) {
    // TODO Auto-generated method stub
    
  }
  
  
  
  public void addPages() {
    addPage(page1 = new SessionExportPage1());
  }



  /* (non-Javadoc)
   * @see org.eclipse.jface.wizard.Wizard#performFinish()
   */
  public boolean performFinish() {
    page1.saveWidgetValues();
    // TODO Auto-generated method stub
    return true;
  }



}