package com.yoursway.ide.ui.railsview.presentation;

import com.yoursway.ide.ui.railsview.presenters.ControllerPresenter;
import com.yoursway.ide.ui.railsview.presenters.FieldPresenter;
import com.yoursway.ide.ui.railsview.presenters.MigrationPresenter;
import com.yoursway.ide.ui.railsview.presenters.ModelPresenter;
import com.yoursway.ide.ui.railsview.presenters.ProjectPresenter;
import com.yoursway.model.rails.impl.RailsController;
import com.yoursway.model.rails.impl.RailsProject;

public class ElementPresenterFactory implements IPresenterFactory {
    
    private final IPresenterOwner owner;
    
    public ElementPresenterFactory(IPresenterOwner owner) {
        this.owner = owner;
    }
    
    public IElementPresenter createPresenter(Object element) {
        if (element instanceof IElementPresenter)
            return (IElementPresenter) element;
        if (element instanceof RailsProject)
            return new ProjectPresenter(owner, (RailsProject) element);
        if (element instanceof RailsController)
            return new ControllerPresenter(owner, (RailsController) element);
        //        if (element instanceof IRailsControllersFolder)
        //            return new ControllersFolderPresenter(owner, (IRailsControllersFolder) element);
        //        if (element instanceof IRailsAction)
        //            return new ActionPresenter(owner, (IRailsAction) element);
        //        if (element instanceof IRailsView)
        //            return new ViewPresenter(owner, (IRailsView) element);
        //        if (element instanceof IRailsPartial)
        //            return new PartialPresenter(owner, (IRailsPartial) element);
        if (element instanceof RailsModel)
            return new ModelPresenter(owner, (RailsModel) element);
        if (element instanceof RailsMigration)
            return new MigrationPresenter(owner, (RailsMigration) element);
        if (element instanceof DbField)
            return new FieldPresenter(owner, (DbField) element);
        throw new AssertionError("Unknown tree item type " + element.getClass().getName() + " found.");
    }
    
}