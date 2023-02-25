package com.ruslan.keyboard.entities;

public class BaseEntity {

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

    public void setId(Integer id)
    {
        this.id = id;
    }
}
