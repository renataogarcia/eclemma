/*******************************************************************************
 * Copyright (c) 2006 Mountainminds GmbH & Co. KG
 * This software is provided under the terms of the Eclipse Public License v1.0
 * See http://www.eclipse.org/legal/epl-v10.html.
 *
 * $Id$
 ******************************************************************************/
package com.mountainminds.eclemma.internal.ui.launching;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.ui.AbstractLaunchConfigurationTab;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.ui.JavaElementSorter;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;

import com.mountainminds.eclemma.core.IClassFiles;
import com.mountainminds.eclemma.internal.ui.EclEmmaUIPlugin;
import com.mountainminds.eclemma.internal.ui.UIMessages;

/**
 * The "Coverage" tab of the launch configuration dialog.
 * 
 * @author Marc R. Hoffmann
 * @version $Revision$
 */
public class CoverageTab extends AbstractLaunchConfigurationTab {

  private final boolean inplaceonly;

  public CoverageTab(boolean inplaceonly) {
    this.inplaceonly = inplaceonly;
  }

  private CheckboxTableViewer classesviewer;
  private Button buttonInplaceInstrumentation;
  
  private final ClassesSelection classesselection = new ClassesSelection();

  public List getPackageFragmentRoots(IClassFiles[] classfiles) {
    Set elements = new HashSet();
    for (int i = 0; i < classfiles.length; i++) {
      elements.addAll(Arrays.asList(classfiles[i].getPackageFragmentRoots()));
    }
    return new ArrayList(elements);
  }
  
  public void createControl(Composite parent) {
    parent = new Composite(parent, SWT.NONE);
    GridLayout layout = new GridLayout();
    layout.verticalSpacing = 0;
    parent.setLayout(layout);
    setControl(parent);
    createInstrumentedClasses(parent);
  }

  private void createInstrumentedClasses(Composite parent) {
    Group group = new Group(parent, SWT.NONE);
    group.setLayoutData(new GridData(GridData.FILL_BOTH));
    group.setText(UIMessages.CoverageTab_groupInstrumentedClassesLabel);
    GridLayout layout = new GridLayout();
    group.setLayout(layout);
    classesviewer = CheckboxTableViewer.newCheckList(group, SWT.BORDER);
    classesviewer.setContentProvider(new ArrayContentProvider());
    classesviewer.setSorter(new JavaElementSorter());
    classesviewer.addFilter(new ViewerFilter() {
      public boolean select(Viewer viewer, Object parentElement, Object element) {
        IPackageFragmentRoot root = (IPackageFragmentRoot) element;
        boolean inplace = classesselection.getInplace();
        try {
          return !inplace || root.getKind() == IPackageFragmentRoot.K_SOURCE;
        } catch (JavaModelException e) {
          EclEmmaUIPlugin.log(e);
          return false;
        }
      }
    });
    classesviewer.setLabelProvider(new ClasspathLabelProvider());
    classesviewer.getControl().setLayoutData(new GridData(GridData.FILL_BOTH));
    classesviewer.addCheckStateListener(new ICheckStateListener() {
      public void checkStateChanged(CheckStateChangedEvent event) {
        classesselection.setSelection((IPackageFragmentRoot) event.getElement(), event.getChecked()); 
        classesviewer.setCheckedElements(classesselection.getSelectedRoots());
        setDirty(true);
        updateLaunchConfigurationDialog();
      }
    });
    
    
    buttonInplaceInstrumentation = new Button(group, SWT.CHECK);
    buttonInplaceInstrumentation.setText(UIMessages.CoverageTab_buttonInplaceIntrLabel);
    buttonInplaceInstrumentation.setEnabled(!inplaceonly);
    buttonInplaceInstrumentation.addSelectionListener(new SelectionAdapter() {
      public void widgetSelected(SelectionEvent e) {
        classesselection.setInplace(buttonInplaceInstrumentation.getSelection());
        classesviewer.refresh();
        classesviewer.setCheckedElements(classesselection.getSelectedRoots());
        setDirty(true);
        updateLaunchConfigurationDialog();
      }
    });
  }

  public void setDefaults(ILaunchConfigurationWorkingCopy configuration) {
    // nothing to do
  }

  public void initializeFrom(ILaunchConfiguration configuration) {
    try {
      classesselection.init(configuration, inplaceonly);
      buttonInplaceInstrumentation.setSelection(classesselection.getInplace());
      classesviewer.setInput(classesselection.getAllRoots());
      classesviewer.setCheckedElements(classesselection.getSelectedRoots());
    } catch (CoreException e) {
      EclEmmaUIPlugin.log(e);
    }
  }

  public void performApply(ILaunchConfigurationWorkingCopy configuration) {
    classesselection.save(configuration);
  }

  public String getName() {
    return UIMessages.CoverageTab_title;
  }

  public Image getImage() {
    return EclEmmaUIPlugin.getImage(EclEmmaUIPlugin.EVIEW_COVERAGE);
  }

}
