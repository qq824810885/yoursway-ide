package com.yoursway.ide.rhtml.internal.modelquery;

import org.eclipse.wst.sse.core.internal.provisional.IModelManager;
import org.eclipse.wst.xml.core.internal.contentmodel.CMDocument;
import org.eclipse.wst.xml.core.internal.contentmodel.CMElementDeclaration;
import org.eclipse.wst.xml.core.internal.contentmodel.modelquery.ModelQueryCMProvider;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMModel;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMNode;
import org.w3c.dom.Node;

import com.yoursway.ide.rhtml.core.internal.contentmodel.JSPCMDocumentFactory;

/**
 * CMDocument provider for HTML and JSP documents.
 */
public class JSPModelQueryCMProvider implements ModelQueryCMProvider {
    
    protected JSPModelQueryCMProvider() {
        super();
    }
    
    /**
     * Returns the CMDocument that corresponds to the DOM Node. or null if no
     * CMDocument is appropriate for the DOM Node.
     */
    public CMDocument getCorrespondingCMDocument(Node node) {
        CMDocument jcmdoc = null;
        if (node instanceof IDOMNode) {
            IDOMModel model = ((IDOMNode) node).getModel();
            String modelPath = model.getBaseLocation();
            if (modelPath != null && !IModelManager.UNMANAGED_MODEL.equals(modelPath)) {
                //                    DeploymentDescriptorPropertyCache.getInstance().getJSPVersion(
                //                        new Path(modelPath));
                jcmdoc = JSPCMDocumentFactory.getCMDocument();
            }
        }
        if (jcmdoc == null) {
            jcmdoc = JSPCMDocumentFactory.getCMDocument();
        }
        
        CMDocument result = null;
        try {
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                String elementName = node.getNodeName();
                
                // test to see if this node belongs to JSP's CMDocument (case
                // sensitive)
                CMElementDeclaration dec = (CMElementDeclaration) jcmdoc.getElements().getNamedItem(
                        elementName);
                if (dec != null) {
                    result = jcmdoc;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}
