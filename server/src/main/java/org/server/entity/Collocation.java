package org.server.entity;

public class Collocation extends BaseEntity
{
    private Integer prevId;
    private Integer nextId;
    private Integer count;

    public Collocation(Integer id, Integer prevId, Integer nextId, Integer count)
    {
        super(id);
        this.prevId = prevId;
        this.nextId = nextId;
        this.count = count;
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
}
