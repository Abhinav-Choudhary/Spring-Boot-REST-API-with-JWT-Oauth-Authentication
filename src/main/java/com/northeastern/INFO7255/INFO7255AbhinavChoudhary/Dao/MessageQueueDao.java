package com.northeastern.INFO7255.INFO7255AbhinavChoudhary.Dao;

import org.springframework.stereotype.Repository;

import redis.clients.jedis.Jedis;

@Repository
public class MessageQueueDao {

    public void addToQueue(String queue, String value) {
        try (Jedis jedis = new Jedis("localhost")) {
            jedis.lpush(queue, value);
        }
    }
}
