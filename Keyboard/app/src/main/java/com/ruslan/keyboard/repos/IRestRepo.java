package com.ruslan.keyboard.repos;

import com.ruslan.keyboard.entities.BaseEntity;

public interface IRestRepo<T extends BaseEntity>{
    T[] select();
    T select(Integer id);
    T insert(T entity);
    T update(Integer id, T entity);
    T delete(Integer id);
}
