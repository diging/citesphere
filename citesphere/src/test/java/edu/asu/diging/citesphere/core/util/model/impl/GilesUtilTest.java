package edu.asu.diging.citesphere.core.util.model.impl;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockMultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import edu.asu.diging.citesphere.core.util.impl.GilesUtil;
import edu.asu.diging.citesphere.model.bib.IGilesUpload;
import edu.asu.diging.citesphere.model.bib.impl.GilesUpload;

public class GilesUtilTest {
    @InjectMocks
    private GilesUtil gilesUtilToTest;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void test_createJobObjectNode() {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode root = mapper.createObjectNode(); 
        IGilesUpload job = new GilesUpload();
        job.setProgressId("test_progress_id");
        gilesUtilToTest.createJobObjectNode(root, job);
    }
}
