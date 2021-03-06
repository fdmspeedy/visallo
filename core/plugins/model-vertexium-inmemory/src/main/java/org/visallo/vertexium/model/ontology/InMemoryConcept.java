package org.visallo.vertexium.model.ontology;

import org.visallo.core.exception.VisalloException;
import org.visallo.core.model.ontology.Concept;
import org.visallo.core.model.ontology.OntologyProperties;
import org.visallo.core.model.ontology.OntologyProperty;
import org.visallo.core.util.JSONUtil;
import org.vertexium.Authorizations;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryConcept extends Concept {
    private String title;
    private String color;
    private String displayName;
    private String displayType;
    private String titleFormula;
    private String subtitleFormula;
    private String timeFormula;
    private String conceptIRI;
    private List<String> addRelatedConceptWhiteList;
    private byte[] glyphIcon;
    private byte[] mapGlyphIcon;
    private boolean userVisible = true;
    private Boolean searchable;
    private Boolean addable;
    private Map<String, String> metadata = new HashMap<String, String>();
    private List<String> intents = new ArrayList<>();

    public InMemoryConcept(String conceptIRI, String parentIRI) {
        super(parentIRI, new ArrayList<OntologyProperty>());
        this.conceptIRI = conceptIRI;
    }

    @Override
    public String getIRI() {
        return this.conceptIRI;
    }

    @Override
    public String[] getIntents() {
        return this.intents.toArray(new String[this.intents.size()]);
    }

    @Override
    public void addIntent(String intent, Authorizations authorizations) {
        this.intents.add(intent);
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public boolean hasGlyphIconResource() {
        return glyphIcon != null;
    }

    @Override
    public String getColor() {
        return color;
    }

    @Override
    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String getDisplayType() {
        return displayType;
    }

    @Override
    public String getTitleFormula() {
        return titleFormula;
    }

    @Override
    public Boolean getSearchable() {
        return searchable;
    }

    @Override
    public String getSubtitleFormula() {
        return this.subtitleFormula;
    }

    @Override
    public String getTimeFormula() {
        return this.timeFormula;
    }

    @Override
    public boolean getUserVisible() {
        return userVisible;
    }

    @Override
    public Map<String, String> getMetadata() {
        return this.metadata;
    }

    @Override
    public List<String> getAddRelatedConceptWhiteList() {
        return addRelatedConceptWhiteList;
    }

    @Override
    public void setProperty(String name, Object value, Authorizations authorizations) {
        if (OntologyProperties.COLOR.getPropertyName().equals(name)) {
            this.color = (String) value;
        } else if (OntologyProperties.DISPLAY_TYPE.getPropertyName().equals(name)) {
            this.displayType = (String) value;
        } else if (OntologyProperties.TITLE_FORMULA.getPropertyName().equals(name)) {
            this.titleFormula = (String) value;
        } else if (OntologyProperties.SUBTITLE_FORMULA.getPropertyName().equals(name)) {
            this.subtitleFormula = (String) value;
        } else if (OntologyProperties.TIME_FORMULA.getPropertyName().equals(name)) {
            this.timeFormula = (String) value;
        } else if (OntologyProperties.USER_VISIBLE.getPropertyName().equals(name)) {
            this.userVisible = (Boolean) value;
        } else if (OntologyProperties.GLYPH_ICON.getPropertyName().equals(name)) {
            this.glyphIcon = (byte[]) value;
        } else if (OntologyProperties.MAP_GLYPH_ICON.getPropertyName().equals(name)) {
            this.mapGlyphIcon = (byte[]) value;
        } else if (OntologyProperties.TITLE.getPropertyName().equals(name)) {
            this.title = (String) value;
        } else if (OntologyProperties.DISPLAY_NAME.getPropertyName().equals(name)) {
            this.displayName = (String) value;
        } else if (OntologyProperties.ADD_RELATED_CONCEPT_WHITE_LIST.getPropertyName().equals(name)) {
            this.addRelatedConceptWhiteList = JSONUtil.toStringList(JSONUtil.parseArray((String) value));
        } else if (OntologyProperties.SEARCHABLE.getPropertyName().equals(name)) {
            if (value instanceof Boolean) {
                this.searchable = (Boolean) value;
            } else {
                this.searchable = Boolean.parseBoolean((String) value);
            }
        } else if (OntologyProperties.ADDABLE.getPropertyName().equals(name)) {
            if (value instanceof Boolean) {
                this.addable = (Boolean) value;
            } else {
                this.addable = Boolean.parseBoolean((String) value);
            }
        } else {
            metadata.put(name, value.toString());
        }
    }

    @Override
    public void removeProperty(String name, Authorizations authorizations) {
        if (OntologyProperties.TITLE_FORMULA.getPropertyName().equals(name)) {
            this.titleFormula = null;
        } else if (OntologyProperties.SUBTITLE_FORMULA.getPropertyName().equals(name)) {
            this.subtitleFormula = null;
        } else if (OntologyProperties.TIME_FORMULA.getPropertyName().equals(name)) {
            this.timeFormula = null;
        } else {
            throw new VisalloException("Remove not implemented for property " + name);
        }
    }

    @Override
    public byte[] getGlyphIcon() {
        return glyphIcon;
    }

    @Override
    public byte[] getMapGlyphIcon() {
        return mapGlyphIcon;
    }

    public String getConceptIRI() {
        return conceptIRI;
    }
}
