package edu.asu.diging.citesphere.core.export.proc.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.stereotype.Component;

import edu.asu.diging.citesphere.core.exceptions.ExportFailedException;
import edu.asu.diging.citesphere.core.export.ExportType;
import edu.asu.diging.citesphere.core.export.proc.Processor;
import edu.asu.diging.citesphere.core.model.bib.IAffiliation;
import edu.asu.diging.citesphere.core.model.bib.ICitation;
import edu.asu.diging.citesphere.core.model.bib.ICitationConceptTag;
import edu.asu.diging.citesphere.core.model.bib.ICreator;
import edu.asu.diging.citesphere.core.model.bib.IPerson;

@Component
public class CsvProcessor implements Processor {

    /* (non-Javadoc)
     * @see edu.asu.diging.citesphere.core.export.proc.impl.Processor#getSupportedType()
     */
    @Override
    public ExportType getSupportedType() {
        return ExportType.CSV;
    }
    
    @Override
    public String getFileExtension() {
        return "csv";
    }

    /* (non-Javadoc)
     * @see edu.asu.diging.citesphere.core.export.proc.impl.Processor#write(java.util.List, java.lang.Appendable)
     */
    @Override
    public void write(List<ICitation> citations, Appendable writer) throws ExportFailedException {

        CSVFormat format = CSVFormat.DEFAULT.withHeader("Key", "Group Id", "Group Name", "Type", "Title", "Date", "Authors",
                "Editors", "Other Creators", "Publication Title", "Volume", "Issue", "Pages", "Series", "Series Title",
                "URL", "Abstract", "Access Date", "Series Text", "Journal Abbreviation", "Language", "DOI", "ISSN",
                "Short Title", "Archive", "Archive Location", "Library Catalog", "Call Number", "Rights", "Date Added",
                "Date Modified", "Concept Tags", "Extra", "Version");
        try (CSVPrinter printer = new CSVPrinter(writer, format)) {
            for (ICitation citation : citations) {
                List<String> row = new ArrayList<>();
                row.add(citation.getKey());
                if (citation.getGroup() != null) {
                    row.add(citation.getGroup().getId() + "");
                    row.add(citation.getGroup().getName());
                } else {
                    row.add("");
                    row.add("");
                }
                row.add(citation.getItemType().getZoteroKey());
                row.add(citation.getTitle());
                row.add(citation.getDateFreetext());
                row.add(createPersonString(citation.getAuthors()));
                row.add(createPersonString(citation.getEditors()));
                row.add(createCreatorsString(citation.getOtherCreators()));
                row.add(citation.getPublicationTitle());
                row.add(citation.getVolume());
                row.add(citation.getIssue());
                row.add(citation.getPages());
                row.add(citation.getSeries());
                row.add(citation.getSeriesTitle());
                row.add(citation.getUrl());
                row.add(citation.getAbstractNote());
                row.add(citation.getAccessDate());
                row.add(citation.getSeriesText());
                row.add(citation.getJournalAbbreviation());
                row.add(citation.getLanguage());
                row.add(citation.getDoi());
                row.add(citation.getIssn());
                row.add(citation.getShortTitle());
                row.add(citation.getArchive());
                row.add(citation.getArchiveLocation());
                row.add(citation.getLibraryCatalog());
                row.add(citation.getCallNumber());
                row.add(citation.getRights());
                row.add(citation.getDateAdded());
                row.add(citation.getDateModified());
                row.add(createConceptsString(citation.getConceptTags()));
                row.add(citation.getExtra());
                row.add(citation.getVersion() + "");
                printer.printRecord(row);
            }
        } catch (IOException e) {
            throw new ExportFailedException("Could not initialize printer.", e);
        }
    }
    
    private String createPersonString(Set<IPerson> persons) {
        StringBuffer sb = new StringBuffer();
        for (IPerson person : persons) {
            addPerson(sb, person);
        }
        return sb.toString();
    }

    private void addPerson(StringBuffer sb, IPerson person) {
        sb.append(person.getName());
        sb.append(" {" + person.getUri() + "}");
        sb.append(" (");
        if (person.getAffiliations() != null) {
            for (IAffiliation aff : person.getAffiliations()) {
                sb.append(aff.getName());
                sb.append(" {" + aff.getUri() + "}");
                sb.append("; ");
            }
        }
        sb.append(")");
        sb.append("; ");
    }
    
    private String createCreatorsString(Set<ICreator> creators) {
        StringBuffer sb = new StringBuffer();
        for (ICreator creator : creators) {
            if (creator.getPerson() != null) {
                addPerson(sb, creator.getPerson());
            }
            sb.append(" [" + creator.getRole() + "]");
            sb.append(";");
        }
        return sb.toString();
    }
    
    private String createConceptsString(Set<ICitationConceptTag> conceptTags) {
        StringBuffer sb = new StringBuffer();
        if (conceptTags != null) {
            for (ICitationConceptTag tag : conceptTags) {
                sb.append(tag.getTypeName());
                sb.append(" [" + tag.getTypeUri() + "]: ");
                sb.append(tag.getConceptName());
                sb.append(" [" + tag.getConceptUri() + "]");
                sb.append("; ");            
            }
        }
        return sb.toString();
    }
}
