package com.yoursway.ide.application.view.impl;

import static com.yoursway.swt.additions.FormDataBuilder.formDataOf;
import static com.yoursway.swt.additions.YsSwtUtils.centerShellOnNearestMonitor;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import com.yoursway.databinding.SwtUpdater;
import com.yoursway.ide.application.controllers.mainwindow.ProjectTreeViewImpl;
import com.yoursway.ide.application.view.View;
import com.yoursway.ide.application.view.ViewCallback;
import com.yoursway.ide.application.view.ViewDefinition;
import com.yoursway.ide.application.view.ViewDefinitionFactory;
import com.yoursway.ide.application.view.mainwindow.MainWindow;
import com.yoursway.ide.application.view.mainwindow.MainWindowArea;
import com.yoursway.ide.application.view.mainwindow.MainWindowAreas;
import com.yoursway.ide.application.view.mainwindow.MainWindowCallback;
import com.yoursway.ide.application.view.mainwindow.MainWindowModel;
import com.yoursway.ide.application.view.mainwindow.MainWindowViewAreaVisitor;
import com.yoursway.swt.additions.YsStandardFonts;

public class MainWindowImpl implements MainWindow {
    
    private final MainWindowCallback callback;
    private final MainWindowModel windowModel;
    private Shell shell;
    private Composite projectComposite;
    private CTabFolder tabFolder;
    private CTabItem tabItem;
    private final MainWindowAreas areas;
    private final ViewDefinitionFactory viewDefinitions;
    
    public MainWindowImpl(Display display, final MainWindowModel windowModel, MainWindowCallback callback,
            MainWindowAreas areas, ViewDefinitionFactory viewDefinitions) {
        if (display == null)
            throw new NullPointerException("display is null");
        if (windowModel == null)
            throw new NullPointerException("windowModel is null");
        if (callback == null)
            throw new NullPointerException("callback is null");
        if (areas == null)
            throw new NullPointerException("areas is null");
        if (viewDefinitions == null)
            throw new NullPointerException("viewDefinitions is null");
        this.windowModel = windowModel;
        this.callback = callback;
        this.areas = areas;
        this.viewDefinitions = viewDefinitions;
        
        shell = new Shell(display);
        shell.setLayout(new FormLayout());
        
        projectComposite = new Composite(shell, SWT.NONE);
        formDataOf(projectComposite).left(0).right(0, 150).top(0).bottom(100);
        
        //        ViewSite projectListSite = viewSites.findOneByRole(ViewSiteRole.PROJECT_LIST);
        //        if (projectListSite != null)
        //            new 
        
        tabFolder = new CTabFolder(shell, SWT.TOP | SWT.CLOSE);
        //        tabFolder = new TabFolder(shell, SWT.TOP);
        formDataOf(tabFolder).left(projectComposite).right(100).top(0).bottom(100);
        tabFolder.setTabHeight(16); // 16px should be enough for everyone
        tabFolder.setFont(YsStandardFonts.miniFont());
        tabFolder.setMRUVisible(false); // no-no-no, David Blane, no-no-no
        tabFolder.setSimple(true); // no fancy space-eating curves
        
        tabItem = new CTabItem(tabFolder, SWT.NONE);
        tabItem.setText("application.rb");
        
        tabItem = new CTabItem(tabFolder, SWT.NONE);
        tabItem.setText("application.rb");
        
        tabItem = new CTabItem(tabFolder, SWT.NONE);
        tabItem.setText("application.rb");
        
        tabItem = new CTabItem(tabFolder, SWT.NONE);
        tabItem.setText("application.rb");
        
        //        helloLabel = new Label(shell, SWT.NONE);
        
        new SwtUpdater(shell) {
            protected void updateControl() {
                shell.setText(windowModel.projectLocation().getValue() + " - "
                        + windowModel.projectType().getValue().getDescriptiveName());
                //                helloLabel.setText("Hello, world to " + windowModel.projectLocation().getValue().getName() + "!");
                
                // needed for the text to be visible 
                shell.layout();
            }
        };
        
        shell.setSize(600, 300);
    }
    
    public MainWindowAreas definition() {
        return null;
    }
    
    public View bindView(ViewDefinition definition, final ViewCallback callback) {
        class Visitor implements MainWindowViewAreaVisitor {
            
            public View result = null;

            public void visitGeneralArea() {
            }

            public void visitProjectViewArea() {
                result = new CompositeView(projectComposite, callback);
            }
            
        };
        
        MainWindowArea area = (MainWindowArea) definition.area();
        Visitor visitor = new Visitor();
        area.accept(visitor);
        return visitor.result;
    }

    public void open() {
        centerShellOnNearestMonitor(shell);
        shell.open();
    }
    
}