package edu.asu.diging.citesphere.web.user.export;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import edu.asu.diging.citesphere.core.exceptions.ExportFailedException;
import edu.asu.diging.citesphere.core.exceptions.ExportTooBigException;
import edu.asu.diging.citesphere.core.exceptions.ExportTypeNotSupportedException;
import edu.asu.diging.citesphere.core.exceptions.GroupDoesNotExistException;
import edu.asu.diging.citesphere.core.exceptions.ZoteroHttpStatusException;
import edu.asu.diging.citesphere.core.export.ExportType;
import edu.asu.diging.citesphere.core.export.IExportManager;
import edu.asu.diging.citesphere.user.IUser;

@Controller
public class ExportJobController {
    
    @Autowired
    private IExportManager exportManager;

    @RequestMapping(value = { "/auth/group/{zoteroGroupId}/job/export",
            "/auth/group/{zoteroGroupId}/collection/{collectionId}/job/export" }, method=RequestMethod.POST)
    public String startExport(Authentication authentication, Model model, @PathVariable("zoteroGroupId") String groupId,
            @PathVariable(value="collectionId", required=false) String collectionId) throws ZoteroHttpStatusException {

        try {
            exportManager.distributedExport(ExportType.CSV, (IUser) authentication.getPrincipal(), groupId, collectionId);
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
        } catch (ExportTooBigException e) {
            model.addAttribute("show_alert", true);
            model.addAttribute("alert_type", "danger");
            model.addAttribute("alert_msg", "You tried to export more than 300 records, which is currently the limit.");
        }
        
        return "redirect:/auth/exports";
    }
}
