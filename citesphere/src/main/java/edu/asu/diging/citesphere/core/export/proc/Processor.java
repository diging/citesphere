package edu.asu.diging.citesphere.core.export.proc;

import java.util.Iterator;

import edu.asu.diging.citesphere.core.exceptions.ExportFailedException;
import edu.asu.diging.citesphere.core.export.ExportType;
import edu.asu.diging.citesphere.model.bib.ICitation;

public interface Processor {

    ExportType getSupportedType();

    void write(Iterator<ICitation> citations, Appendable writer) throws ExportFailedException;

    String getFileExtension();

}