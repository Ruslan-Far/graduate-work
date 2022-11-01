package org.server.entity;

public class Word extends BaseEntity
{
    private Integer userId;
    private String word;
    private Integer count;

    public Word(Integer id, Integer userId, String word, Integer count)
    {
        super(id);
        this.userId = userId;
        this.word = word;
        this.count = count;
    }

    public Integer getUserId()
    {
        return userId;
    }

    public void setUserId(Integer userId)
    {
        this.userId = userId;
    }

    public String getWord()
    {
        return word;
    }

    public void setWord(String word)
    {
        this.word = word;
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
