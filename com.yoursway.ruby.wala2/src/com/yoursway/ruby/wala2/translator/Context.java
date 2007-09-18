/**
 * 
 */
package com.yoursway.ruby.wala2.translator;

import com.ibm.wala.cast.tree.CAst;

class Context {
    
    private CAst astBuilder;
    
    public Context(CAst astBuilder) {
        super();
        this.astBuilder = astBuilder;
    }
    
    public CAst astBuilder() {
        return astBuilder;
    }
    
}