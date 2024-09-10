package edu.asu.diging.citesphere.core.util;

import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.List;
import edu.asu.diging.citesphere.model.bib.IGilesUpload;

public interface IGilesUtil {

    void convertFilesToBytesList(List<byte[]> fileBytes, MultipartFile[] files);

    void createJobObjectNode(ObjectNode root, IGilesUpload job);
}