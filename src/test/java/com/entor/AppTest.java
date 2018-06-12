package com.entor;

import static org.junit.Assert.assertTrue;

import com.entor.po.Email;
import com.entor.po.User;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.management.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.Date;
import java.util.List;

public class AppTest 
{
    private Session session;

    @Before
    public void before() {
        //获取加载配置管理类
        Configuration configuration = new Configuration();

        //不给参数就默认加载hibernate.cfg.xml文件，
        configuration.configure();

        //创建Session工厂对象
        SessionFactory factory = configuration.buildSessionFactory();

        //得到Session对象
        session = factory.openSession();
    }

    @After
    public void after() {
        session.close();
    }

    @Test
    public void testSave() {
        // 创建User
        User user = new User();
        user.setUsername("L");
        user.setPassword("123");
        user.setCellphone("110");

        // 开启事务
        Transaction sx = session.beginTransaction();
        // 保存对象
        session.save(user);
        sx.commit();
    }

    @Test
    public void testJPQL() {
        // 创建jpql查询对象
        javax.persistence.Query query = session.createQuery("from User u where u.id = :id");

        // 2.设置参数
        query.setParameter("id", 3);

        // 3.执行查询并获得查询的结果
        User user = (User) query.getSingleResult();

        System.out.println(user.getUsername());


        query = session.createQuery("from User");
        System.out.println(query.getResultList());
    }

    @Test
    public void testHQL() {
        // 创建HQL查询对象
        org.hibernate.query.Query<User> userQuery = session.createQuery("from User u where u.username = :username", User.class);

        // 设置参数
        userQuery.setParameter("username", "L");

        // 执行查询并获得查询的结果
        userQuery.list().forEach(user -> System.out.println(user.getUsername()));

        userQuery = session.createQuery("from User u where u.id = :id", User.class);
        System.out.println(userQuery
                    .setParameter("id", 3)
                    .uniqueResult()
                    .getUsername());
    }

    @Test
    public void testCriteria4() {
        // 创建(Criteria)标准化查询对象
        List list = session.createCriteria("com.entor.po.User").list();
        list.forEach(System.out::println);

        list = session.createCriteria(User.class)
                .addOrder(Order.asc("cellphone"))
                .add(Restrictions.eq("password", "123"))
                .list();
        list.forEach(System.out::println);

        session.createCriteria(User.class)
                .setFirstResult(0)
                .setMaxResults(2)
                .list()
                .forEach(System.out::println);
    }

    @Test
    public void testCriteria5() {
        // 获得CriteriaBuilder标准查询对象构建器
        CriteriaBuilder builder = session.getCriteriaBuilder();

        // 构建一个标准查询对象
        CriteriaQuery<User> criteriaQuery = builder.createQuery(User.class);

        // 创建Root
        Root<User> root = criteriaQuery.from(User.class);

        // 构建查询
        criteriaQuery.select(root);
        criteriaQuery.where(builder.equal(root.get("username"), "L"));

        session.createQuery(criteriaQuery)
                .list()
                .forEach(System.out::println);
    }

    @Test
    public void testOneToOne() {
        Transaction tx = session.beginTransaction();

        // 查询一个存在用户
        User user = session.get(User.class, 2);
        System.out.println("用户" + user);

        // 创建一对一的关联关系
        Email email = new Email();
        email.setAddress("qq.com");
        email.setCreateDate(new Date());

        // 设置一对一的关联关系
        email.setUser(user);

        session.save(email);
        tx.commit();
    }
}
