package com.yoursway.ide.application.model;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Lists.newArrayListWithExpectedSize;
import static com.yoursway.ide.application.model.DocumentAdditionReason.CREATED;
import static com.yoursway.ide.application.model.DocumentAdditionReason.OPENED;
import static com.yoursway.utils.Listeners.newListenersByIdentity;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

import com.yoursway.ide.application.model.projects.types.ProjectType;
import com.yoursway.utils.Listeners;

public class Project implements DocumentOwner {
    
    private final ProjectOwner owner;
    private final File location;
    private final ProjectType type;
    
    private final Collection<Document> documents = newArrayList();
    
    private transient Listeners<ProjectListener> listeners = newListenersByIdentity();
    
    public synchronized void addListener(ProjectListener listener) {
        listeners.add(listener);
    }
    
    public synchronized void removeListener(ProjectListener listener) {
        listeners.remove(listener);
    }
    
    public Project(ProjectOwner owner, ProjectType type, File location) {
        if (owner == null)
            throw new NullPointerException("owner is null");
        if (type == null)
            throw new NullPointerException("type is null");
        if (location == null)
            throw new NullPointerException("location is null");
        
        this.owner = owner;
        this.type = type;
        this.location = location;
    }
    
    public File getLocation() {
        return location;
    }
    
    public ProjectType getType() {
        return type;
    }
    
    public void openDocument(File file) {
        Document document = findDocument(file);
        if (document == null)
            addDocument(new Document(this, file), OPENED);
        else
            for(ProjectListener listener : listeners)
                listener.documentAlreadyOpen(document);
            
    }
    
	public void createDocument() {
		File file = findNewUniqueName();
		addDocument(new Document(this, file), CREATED);
    }
    
	private File findNewUniqueName() {
		return new File(location.getPath()+"/untitled");
	}
	
	private Document findDocument(File file) {
        for (Document document : documents)
            try {
                if (document.file().getCanonicalFile().equals(file.getCanonicalFile()))
                    return document;
            } catch (IOException e) {
                if (document.file().getAbsoluteFile().equals(file.getAbsolutePath()))
                    return document;
            }
        return null;
    }
    
    private void addDocument(Document document, DocumentAdditionReason reason) {
        documents.add(document);
        for (ProjectListener listener : listeners)
            listener.documentAdded(document, reason);
    }

    public void close() {
        owner.closeProject(this);
        for (ProjectListener listener : listeners)
            listener.closed();
    }

	public Project project() {
		return this;
	}
	
	public Collection<File> findAllFiles() {
		Collection<File> result = newArrayListWithExpectedSize(100);
		findAllFilesIn(result, location);
		return result;
	}

	private void findAllFilesIn(Collection<File> result, File container) {
		File[] children = container.listFiles();
		if (children != null)
			for (File child : children)
				if (child.isFile())
					maybeEnlistFile(result, child);
				else if (child.isDirectory())
					findAllFilesIn(result, child);
	}

	private void maybeEnlistFile(Collection<File> result, File child) {
		if (child.getName().startsWith("."))
			return;
		result.add(child);
	}
    
}
