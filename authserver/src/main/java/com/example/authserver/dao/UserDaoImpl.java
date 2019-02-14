package com.example.authserver.dao;

import com.example.authserver.model.User;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;

/**
 * Created by tomo on 2019-02-10.
 */
@Repository("userDao")
@Transactional
public class UserDaoImpl extends AbstractDao<String, User> implements UserDao {
    @Override
    public User findById(String id) {
        return getByKey(id);
    }

    @Override
    public User findByEmail(String email) {
        Criteria criteria = createEntityCriteria();
        criteria.add(Restrictions.eq("email", email));
        return (User) criteria.uniqueResult();
    }

    @Override
    public User findByUsername(String username) {
        Criteria criteria = createEntityCriteria();
        criteria.add(Restrictions.eq("username", username));
        return (User) criteria.uniqueResult();
    }
}
