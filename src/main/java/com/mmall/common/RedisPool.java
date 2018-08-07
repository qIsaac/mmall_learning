package com.mmall.common;

import com.mmall.util.PropertiesUtil;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * Created by Isaac on 2018/8/7.
 */
public class RedisPool {
    private static JedisPool pool; //jedis连接池
    private static Integer maxTotal = Integer.parseInt(PropertiesUtil.getProperty("redis.max.total","20")); //最大连接数
    private static Integer maxIdle = Integer.parseInt(PropertiesUtil.getProperty("redis.max.idle","10")); //jedispool中最大的idle(空闲)状态的实例个数
    private static Integer minIdle = Integer.parseInt(PropertiesUtil.getProperty("redis.min.idle","2")); //jedispool中最小的idle(空闲)状态的实例个数

    private static Boolean testOnBorrow = Boolean.parseBoolean(PropertiesUtil.getProperty("redis.test.borrow","true")); //在borrow一个jedis的实例的时候,是否进行验证操作，如果为true，则得到的jedis实例是可用的
    private static Boolean testOnReturn = Boolean.parseBoolean(PropertiesUtil.getProperty("redis.test.return","true")); //在return一个jedis的实例的时候,是否进行验证操作，如果为true，则放回的jedispool实例是可用的

    private static String redisIp = PropertiesUtil.getProperty("redis.ip"); //ip
    private static Integer redisPort = Integer.parseInt(PropertiesUtil.getProperty("redis.port")); //port
    private static void initPool(){
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxTotal(maxTotal);
        config.setMaxIdle(maxIdle);
        config.setMinIdle(minIdle);

        config.setTestOnBorrow(testOnBorrow);
        config.setTestOnReturn(testOnReturn);

        config.setBlockWhenExhausted(true);
        pool = new JedisPool(config, redisIp, redisPort, 1000*2);

    }
    static{
        initPool();
    }
    public static Jedis getJedis(){
        return pool.getResource();
    }
    public static void returnBrokenResource(Jedis jedis){
        pool.returnBrokenResource(jedis);
    }
    public static void returnResource(Jedis jedis){
        pool.returnResource(jedis);
    }


    public static void main(String[] args) {
        Jedis jedis = pool.getResource();
        jedis.set("isackey","isacvalue");
        returnResource(jedis);

        pool.destroy();//临时调用，销毁所有连接
        System.out.println("program is end");
    }
}
