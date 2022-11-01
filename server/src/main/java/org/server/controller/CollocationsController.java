package org.server.controller;

import org.server.entity.Collocation;
import org.server.repository.CollocationsRepository;
import org.server.repository.UsersRepository;
import org.server.repository.WordsRepository;
import org.server.resource.CollocationResource;
import org.server.resource.WordResource;
import org.springframework.web.bind.annotation.*;
import java.util.Arrays;

@CrossOrigin
@RestController
@RequestMapping(value = "/collocations")
public class CollocationsController
{
    private final UsersRepository usersRepository;
    private final WordsRepository wordsRepository;
    private final CollocationsRepository collocationsRepository;

    public CollocationsController(UsersRepository usersRepository, WordsRepository wordsRepository, CollocationsRepository collocationsRepository)
    {
        this.usersRepository = usersRepository;
        this.wordsRepository = wordsRepository;
        this.collocationsRepository = collocationsRepository;
    }


    @RequestMapping(value = "", method = RequestMethod.GET)
    CollocationResource[] getAll(@RequestParam(required = false) Integer userId,
                                 @RequestParam(required = false) Object expand)
    {
        Collocation[] collocationEntities = userId == null ?
                collocationsRepository.select() :
                collocationsRepository.selectByUserId(userId);
        return Arrays.stream(collocationEntities)
                .map(entity ->
                {
                    CollocationResource collocationResource = new CollocationResource(entity);
                    if (expand != null)
                    {
                        WordResource[] wordResources = new WordResource[2];
                        wordResources[0] = new WordResource(wordsRepository.select(entity.getPrevId()));
                        wordResources[1] = new WordResource(wordsRepository.select(entity.getNextId()));
                        collocationResource.setWordResources(wordResources);
                    }
                    return collocationResource;
                }).toArray(CollocationResource[]::new);
    }


    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    CollocationResource get(@PathVariable Integer id, @RequestParam(required = false) Object expand)
    {
        Collocation collocationEntity = collocationsRepository.select(id);
        if (collocationEntity == null)
            return null;
        CollocationResource collocationResource = new CollocationResource(collocationEntity);
        if (expand != null)
        {
            WordResource[] wordResources = new WordResource[2];
            wordResources[0] = new WordResource(wordsRepository.select(collocationEntity.getPrevId()));
            wordResources[1] = new WordResource(wordsRepository.select(collocationEntity.getNextId()));
            collocationResource.setWordResources(wordResources);
        }
        return collocationResource;
    }


    @RequestMapping(value = "", method = RequestMethod.POST)
    CollocationResource post(@RequestBody CollocationResource collocationResource)
    {
        Collocation collocationEntity = collocationsRepository.insert(collocationResource.toEntity());
        if (collocationEntity == null)
            return null;
        CollocationResource collocationResource2 = new CollocationResource(collocationEntity);
        WordResource[] wordResources = new WordResource[2];
        wordResources[0] = new WordResource(wordsRepository.select(collocationEntity.getPrevId()));
        wordResources[1] = new WordResource(wordsRepository.select(collocationEntity.getNextId()));
        collocationResource2.setWordResources(wordResources);
        return collocationResource2;
    }


    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    CollocationResource put(@PathVariable Integer id, @RequestBody CollocationResource collocationResource)
    {
        Collocation collocationEntity = collocationsRepository.update(id, collocationResource.toEntity());
        if (collocationEntity == null)
            return null;
        CollocationResource collocationResource2 = new CollocationResource(collocationEntity);
        WordResource[] wordResources = new WordResource[2];
        wordResources[0] = new WordResource(wordsRepository.select(collocationEntity.getPrevId()));
        wordResources[1] = new WordResource(wordsRepository.select(collocationEntity.getNextId()));
        collocationResource2.setWordResources(wordResources);
        return collocationResource2;
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    CollocationResource delete(@PathVariable Integer id)
    {
        Collocation collocationEntity = collocationsRepository.delete(id);
        if (collocationEntity == null)
            return null;
        return new CollocationResource(collocationEntity);
    }
}
