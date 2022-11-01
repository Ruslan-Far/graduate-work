package org.server.resource;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.server.entity.User;

public class UserResource extends BaseResource
{
    private Integer id;
    private String login;
    private String password;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private WordResource[] wordResources;

    public UserResource() {}

    public UserResource(User user)
    {
        this.id = user.getId();
        this.login = user.getLogin();
        this.password = user.getPassword();
    }

    public Integer getId() { return id; }

    public void setId(Integer id)
    {
        this.id = id;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public WordResource[] getWordResources()
    {
        return wordResources;
    }

    public void setWordResources(WordResource[] wordResources) { this.wordResources = wordResources; }

    public User toEntity() { return new User(id, login, password); }
}
