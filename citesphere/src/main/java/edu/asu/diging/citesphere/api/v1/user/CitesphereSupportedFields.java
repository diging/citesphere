package edu.asu.diging.citesphere.api.v1.user;

public enum CitesphereSupportedFields {
    KEY("key"),
    ITEM_TYPE("itemType"),
    AUTHORS("authors"),
    EDITORS("editors"),
    REFERENCES("references"),
    OTHER_CREATORS("otherCreators"),
    CONCEPT_TAGS("conceptTags")
    ;
    private final String fieldName;

    CitesphereSupportedFields(final String fieldName) {
        this.fieldName = fieldName;
    }

    @Override
    public String toString() {
        return fieldName;
    }
    
}
