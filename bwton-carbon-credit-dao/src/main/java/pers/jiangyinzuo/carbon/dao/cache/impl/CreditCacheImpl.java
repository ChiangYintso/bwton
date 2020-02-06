package pers.jiangyinzuo.carbon.dao.cache.impl;

import io.lettuce.core.LettuceFutures;
import io.lettuce.core.Range;
import io.lettuce.core.RedisFuture;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Repository;
import pers.jiangyinzuo.carbon.dao.cache.BaseCache;
import pers.jiangyinzuo.carbon.dao.cache.CreditCache;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static pers.jiangyinzuo.carbon.common.TimeUtil.getTodayTimestamp;
import static pers.jiangyinzuo.carbon.dao.cache.KeyBuilder.*;

/**
 * @author Jiang Yinzuo
 */
@Repository
@Log4j2
public class CreditCacheImpl extends BaseCache implements CreditCache {

    private static final long EXPIRE_SPAN = 24 * 3600 * 1000L;

    @Override
    public List<Long> getCredits(Collection<Long> usersId, String span) {

        List<Future<String>> futures = new ArrayList<>();
        for (Long id : usersId) {
            futures.add(cmdAsync.get(userCredit(id, span)));
        }
        LettuceFutures.awaitAll(10, TimeUnit.SECONDS, futures.toArray(new Future[0]));
        List<Long> result = new ArrayList<>(futures.size());

        try {
            String credit;
            for (Future<String> future : futures) {
                credit = future.get();
                result.add(credit == null ? 0 : Long.parseLong(credit));
            }
        } catch (InterruptedException | ExecutionException e) {
            log.error("error");
            Thread.currentThread().interrupt();
        }
        return result;
    }

    @Override
    public List<List<Integer>> getCreditCollectedRecord(Long friendId) {
        List<RedisFuture<List<String>>> futures = new ArrayList<>(7);
        for (int i = 1; i <= 7; ++i) {
            futures.add(cmdAsync.lrange(userCreditRecord(friendId, i), 0, -1));
        }
        List<List<Integer>> collectedRecords = new ArrayList<>(7);

        LettuceFutures.awaitAll(10, TimeUnit.SECONDS, futures.toArray(new RedisFuture[0]));
        int i = 0;
        for (var future : futures) {
            List<Integer> record = new ArrayList<>(7);
            try {
                List<String> recordStr = future.get();
                for (var str : recordStr) {
                    record.add(Integer.parseInt(str));
                }
            } catch (InterruptedException | ExecutionException e) {
                Thread.currentThread().interrupt();
            }
            collectedRecords.set(i++, record);
        }
        return collectedRecords;
    }

    @Override
    public void collectCredits(Long userId, Long collectedUserId, Integer credit) {
        cmdAsync.incrby(userCredit(userId, "total"), credit);
        cmdAsync.incrby(userCredit(userId, "today"), credit);
        cmdAsync.incrby(userCredit(userId, "week"), credit);

        int dayOfWeek = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);

        String userCreditRecord = userCreditRecord(userId, dayOfWeek);
        cmdAsync.rpush(userCreditRecord, credit.toString()).thenRun(() -> cmdAsync.expireat(userCreditRecord, getTodayTimestamp()));
    }

    @Override
    public void addCreditDrop(Long userId, Long credit, Long matureSpanMillis) {
        long cur = System.currentTimeMillis();
        String key = userCreditDrops(userId);
        cmdAsync.zadd(key,  cur + matureSpanMillis.doubleValue(), dropValue(credit, cur));
        cmdAsync.pexpire(key, matureSpanMillis + EXPIRE_SPAN * 2);
        cmdAsync.zremrangebyscore(key, Range.create(0, cur - EXPIRE_SPAN));
    }

    @Override
    public Long removeCreditDrop(Long pickedUserId, String value) {
        return cmdSync.zrem(userCreditDrops(pickedUserId), value);
    }

    private String dropValue(Long credit, Long cur) {
        return cur + "." + credit;
    }
}
