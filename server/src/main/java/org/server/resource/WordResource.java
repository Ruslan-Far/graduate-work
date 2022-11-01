package org.server.resource;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.server.entity.Word;

public class WordResource extends BaseResource
{
    private Integer id;
    private Integer userId;
    private String word;
    private Integer count;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private UserResource userResource;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private CollocationResource[] collocationResources;

    public WordResource() {}

    public WordResource(Word word)
    {
        this.id = word.getId();
        this.userId = word.getUserId();
        this.word = word.getWord();
        this.count = word.getCount();
    }

    public Integer getId() { return id; }

    public void setId(Integer id)
    {
        this.id = id;
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

    public UserResource getUserResource() { return userResource; }

    public void setUserResource(UserResource userResource) { this.userResource = userResource; }

    public CollocationResource[] getCollocationResources() { return collocationResources; }

    public void setCollocationResources(CollocationResource[] collocationResources) { this.collocationResources = collocationResources; }

    public Word toEntity()
    {
        return new Word(id, userId, word, count);
    }
}

