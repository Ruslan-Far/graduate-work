package org.server.controller;

import org.server.entity.Word;
import org.server.repository.CollocationsRepository;
import org.server.repository.UsersRepository;
import org.server.repository.WordsRepository;
import org.server.resource.CollocationResource;
import org.server.resource.UserResource;
import org.server.resource.WordResource;
import org.springframework.web.bind.annotation.*;
import java.util.Arrays;

@CrossOrigin
@RestController
@RequestMapping(value = "/words")
public class WordsController
{
    private final UsersRepository usersRepository;
    private final WordsRepository wordsRepository;
    private final CollocationsRepository collocationsRepository;

    public WordsController(UsersRepository usersRepository, WordsRepository wordsRepository, CollocationsRepository collocationsRepository)
    {
        this.usersRepository = usersRepository;
        this.wordsRepository = wordsRepository;
        this.collocationsRepository = collocationsRepository;
    }


    @RequestMapping(value = "", method = RequestMethod.GET)
    WordResource[] getAll(@RequestParam(required = false) Integer userId,
                          @RequestParam(required = false) Object expand)
    {
        System.out.println("IIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIII");
        Word[] wordEntities = userId == null ?
                wordsRepository.select() :
                wordsRepository.selectByUserId(userId);
        return Arrays.stream(wordEntities)
                .map(entity ->
                {
                    WordResource wordResource = new WordResource(entity);
                    if (expand != null)
                    {
                        wordResource.setUserResource(new UserResource(usersRepository.select(entity.getUserId())));
                        wordResource.setCollocationResources(Arrays.stream(collocationsRepository
                                .selectByPrevIdNextId(entity.getId(), entity.getId()))
                                .map(e -> new CollocationResource(e))
                                .toArray(CollocationResource[]::new));
                    }
                    return wordResource;
                }).toArray(WordResource[]::new);
    }


    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    WordResource get(@PathVariable Integer id,
                     @RequestParam(required = false) Object expand)
    {
        Word wordEntity = wordsRepository.select(id);
        if (wordEntity == null)
            return null;
        WordResource wordResource = new WordResource(wordEntity);
        if (expand != null)
        {
            wordResource.setUserResource(new UserResource(usersRepository.select(wordEntity.getUserId())));
            wordResource.setCollocationResources(Arrays.stream(collocationsRepository
                    .selectByPrevIdNextId(wordEntity.getId(), wordEntity.getId()))
                    .map(e -> new CollocationResource(e))
                    .toArray(CollocationResource[]::new));
        }
        return wordResource;
    }


    @RequestMapping(value = "", method = RequestMethod.POST)
    WordResource post(@RequestBody WordResource wordResource)
    {
        System.out.println("PPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPP");
        System.out.println(wordResource);
        Word wordEntity = wordsRepository.insert(wordResource.toEntity());
        if (wordEntity == null)
            return null;
        return new WordResource(wordEntity);
    }


    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    WordResource put(@PathVariable Integer id, @RequestBody WordResource wordResource)
    {
        Word wordEntity = wordsRepository.update(id, wordResource.toEntity());
        if (wordEntity == null)
            return null;
        return new WordResource(wordEntity);
    }


    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    WordResource delete(@PathVariable Integer id)
    {
        Word wordEntity = wordsRepository.delete(id);
        if (wordEntity == null)
            return null;
        return new WordResource(wordEntity);
    }
}
