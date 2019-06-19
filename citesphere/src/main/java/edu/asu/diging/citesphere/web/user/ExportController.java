package edu.asu.diging.citesphere.web.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import edu.asu.diging.citesphere.core.exceptions.ExportFailedException;
import edu.asu.diging.citesphere.core.exceptions.ExportTypeNotSupportedException;
import edu.asu.diging.citesphere.core.exceptions.GroupDoesNotExistException;
import edu.asu.diging.citesphere.core.export.ExportType;
import edu.asu.diging.citesphere.core.export.IExportManager;
import edu.asu.diging.citesphere.core.model.IUser;

@Controller
public class ExportController {
    
    @Autowired
    private IExportManager exportManager;

    @RequestMapping(value = { "/auth/group/{zoteroGroupId}/export",
            "/auth/group/{zoteroGroupId}/collection/{collectionId}/export" })
    public String startExport(Authentication authentication, Model model, @PathVariable("zoteroGroupId") String groupId,
            @PathVariable(value="collectionId", required=false) String collectionId) {

        try {
            exportManager.export(ExportType.CSV, (IUser) authentication.getPrincipal(), groupId);
        } catch (GroupDoesNotExistException e) {
            model.addAttribute("show_alert", true);
            model.addAttribute("alert_type", "danger");
            model.addAttribute("alert_msg", "Group does not exist.");
        } catch (ExportTypeNotSupportedException e) {
            model.addAttribute("show_alert", true);
            model.addAttribute("alert_type", "danger");
            model.addAttribute("alert_msg", "Type of export not supported.");
        } catch (ExportFailedException e) {
            model.addAttribute("show_alert", true);
            model.addAttribute("alert_type", "danger");
            model.addAttribute("alert_msg", "Export failed.");
        }
        
        return "auth/group/export";
    }
}
