package org.server.resource;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.server.entity.Collocation;

public class CollocationResource extends BaseResource
{
    private Integer id;
    private Integer prevId;
    private Integer nextId;
    private Integer count;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private WordResource[] wordResources;

    public CollocationResource() {}

    public CollocationResource(Collocation collocation)
    {
        this.id = collocation.getId();
        this.prevId = collocation.getPrevId();
        this.nextId = collocation.getNextId();
        this.count = collocation.getCount();
    }

    public Integer getId()
    {
        return id;
    }

    public void setId(Integer id)
    {
        this.id = id;
    }

    public Integer getPrevId()
    {
        return prevId;
    }

    public void setPrevId(Integer prevId)
    {
        this.prevId = prevId;
    }

    public Integer getNextId()
    {
        return nextId;
    }

    public void setNextId(Integer nextId)
    {
        this.nextId = nextId;
    }

    public Integer getCount()
    {
        return count;
    }

    public void setCount(Integer count)
    {
        this.count = count;
    }

    public WordResource[] getWordResources() { return wordResources; }

    public void setWordResources(WordResource[] wordResources) { this.wordResources = wordResources; }

    public Collocation toEntity()
    {
        return new Collocation(id, prevId, nextId, count);
    }
}
