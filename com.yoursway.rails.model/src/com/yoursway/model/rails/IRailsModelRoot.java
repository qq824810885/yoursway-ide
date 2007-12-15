package com.yoursway.model.rails;

import java.util.Collection;

import com.yoursway.model.repository.IHandle;
import com.yoursway.model.repository.IModelRoot;
import com.yoursway.model.resource.IResourceProject;

public interface IRailsModelRoot extends IModelRoot {
    
    IHandle<Collection<IRailsApplicationProject>> projects();
    
    IHandle<IRailsApplicationProject> mapProject(IResourceProject resourceProject);
    
}