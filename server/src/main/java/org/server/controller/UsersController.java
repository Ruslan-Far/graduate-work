package org.server.controller;

import org.server.entity.User;
import org.server.repository.CollocationsRepository;
import org.server.repository.UsersRepository;
import org.server.repository.WordsRepository;
import org.server.resource.UserResource;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequestMapping(value = "/users")
public class UsersController
{
    private final UsersRepository usersRepository;
    private final WordsRepository wordsRepository;
    private final CollocationsRepository collocationsRepository;

    public UsersController(UsersRepository usersRepository, WordsRepository wordsRepository, CollocationsRepository collocationsRepository)
    {
        this.usersRepository = usersRepository;
        this.wordsRepository = wordsRepository;
        this.collocationsRepository = collocationsRepository;
    }


    @RequestMapping(value = "", method = RequestMethod.POST)
    UserResource auth(@RequestBody UserResource userResource)
    {
        User user = usersRepository.select(userResource.toEntity());
        return new UserResource(user);
    }


    @RequestMapping(value = "/reg", method = RequestMethod.POST)
    UserResource reg(@RequestBody UserResource userResource)
    {
        User user = usersRepository.insert(userResource.toEntity());
        return new UserResource(user);
    }
}
