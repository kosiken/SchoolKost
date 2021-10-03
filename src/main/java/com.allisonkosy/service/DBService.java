package com.allisonkosy.service;

import com.allisonkosy.App;
import com.allisonkosy.entity.Model;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;

public abstract class DBService<T extends Model> {
    private Session session;
    public DBService(Session s) {
        session = s;
    }
    protected T getItem(String itemName, String itemCriteria, Object crietriaValue){
        String builder = "from " + itemName +
                " item where item." +
                itemCriteria +
                " = :criteria";
       Query query = session.createQuery(builder);
      if(crietriaValue instanceof String) query.setString("criteria",(String) crietriaValue);
      else if(crietriaValue instanceof Long) query.setLong("criteria", (Long) crietriaValue);

      return (T) query.uniqueResult();

    }

    protected Query createQuery(String s) {
        return session.createQuery(s);
    }

    protected List<T> getAllObjects(String itemName, String itemCriteria, Object crietriaValue) {
        String builder = "from " + itemName +
                " item where item." +
                itemCriteria +
                " = :criteria";
        Query query = session.createQuery(builder);
        query.setParameter("criteria", crietriaValue);
        List<T> values  = query.list();
        return values;

    }

    protected List<T> getAllObjects(String itemName) {

        Query query = session.createQuery("from " + itemName);
        List<T>  ans= query.list();
        return ans;
    }

    protected T persistObject(T object) {
        try {
            session.beginTransaction();
            session.save(object);
            session.getTransaction().commit();

            return object;
        }
        catch (Exception e){

            App.LOGGER.error(e.getMessage());

            return null;

        }
    }

    protected List<T> persistObjects(List<T> objects) {
        List<T> ret = null;

        try {
            int i = 0;
            Transaction tx = session.beginTransaction();
            for (T o : objects) {
                session.save(o);
                if( i%20 == 0) {
                    session.flush();
                    session.clear();
                }
                i++;
            }
            tx.commit();
            ret = objects;
        }
        catch (Exception e) {
            App.LOGGER.error(e.getMessage());
        }
        return ret;
    }

    protected boolean deleteObject(Long id, Class<T> tClass) {
       try {
           T o= (T) session.get(tClass, id);
           session.beginTransaction();
           session.delete(o);
           session.getTransaction().commit();
           App.LOGGER.info("deleted " + o.getId());
           return true;
       }
       catch (Exception e) {
           App.LOGGER.error(e.getMessage());
           return false;
       }
    }

    protected T updateRecord(T object) {
        session.beginTransaction();
        session.saveOrUpdate(object);
        session.getTransaction().commit();
        return object;
    }

    protected boolean dropTable(String s) {
        Query query = session.createQuery("delete from " +  s);
        try {
            query.executeUpdate();
            return true;
        }
        catch (Exception e) {
            return false;
        }
    }
}
