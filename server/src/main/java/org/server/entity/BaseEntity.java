package org.server.entity;

public class BaseEntity
{
    private Integer id;

    public BaseEntity(){}

    public BaseEntity(Integer id)
    {
        this.id = id;
    }

    public int getId()
    {
        return id;
    }

    public void setId()
    {
        this.id = id;
    }
}
