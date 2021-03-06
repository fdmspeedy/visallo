package org.visallo.web.clientapi.model;

import org.visallo.web.clientapi.util.ClientApiConverter;

import java.util.ArrayList;
import java.util.List;

public class ClientApiVertexFindRelatedResponse implements ClientApiObject {
    private long count;
    private List<ClientApiVertex> vertices = new ArrayList<ClientApiVertex>();

    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }

    public List<ClientApiVertex> getVertices() {
        return vertices;
    }

    @Override
    public String toString() {
        return ClientApiConverter.clientApiToString(this);
    }
}
