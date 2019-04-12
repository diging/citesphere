package edu.asu.diging.citesphere.web.tags;

import java.io.IOException;
import java.util.Iterator;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.SimpleTagSupport;
import edu.asu.diging.citesphere.core.model.bib.ICitation;
import edu.asu.diging.citesphere.core.model.bib.ICreator;

public class CreatorIteratorTag extends SimpleTagSupport {
    
    private ICitation citation;
    private String role;

    private String var;
    
    @Override
    public void doTag() throws JspException, IOException {
        if (citation != null) {
            Iterator<ICreator> it = citation.getOtherCreators(role).iterator();
            while (it.hasNext()) {
                getJspContext().setAttribute(var, it.next());
                if (it.hasNext()) {
                    getJspContext().setAttribute("lastIteration", false);
                } else {
                    getJspContext().setAttribute("lastIteration", true);
                }
                getJspBody().invoke(null);
            }
        }
    }

    public ICitation getCitation() {
        return citation;
    }

    public void setCitation(ICitation citation) {
        this.citation = citation;
    }
    
    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getVar() {
        return var;
    }

    public void setVar(String var) {
        this.var = var;
    }


}
