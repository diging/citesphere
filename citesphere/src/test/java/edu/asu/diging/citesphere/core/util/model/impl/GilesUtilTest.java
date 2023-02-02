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
    public void test_convertFilesToBytesList() {
        List<byte[]> fileBytes = new ArrayList<>();
        MockMultipartFile[] files = new MockMultipartFile[2];
        files[0] = new MockMultipartFile("data1", "filename1.txt", "text/plain", "some1 xml".getBytes());
        files[1] = new MockMultipartFile("data2", "filename2.txt", "text/plain", "some2 xml".getBytes());
        gilesUtilToTest.convertFilesToBytesList(fileBytes, files);
        assertTrue(fileBytes.size() == 2);
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
