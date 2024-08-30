package com.example.mybatplusdemo.config;

import cn.hutool.core.collection.CollectionUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.lang.Nullable;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
//import org.springrabbit.core.tool.utils.CollectionUtil;
//import org.springrabbit.core.tool.utils.Func;
//import org.springrabbit.ras.constant.RasConstant;

import java.lang.reflect.Array;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.*;
import java.util.stream.Collectors;

@Slf4j
public class BatchDataProcessUtils {

    public static final int PAGE_SIZE_200 = 200;

    /**
     * 以分页的方式，多次批量获取数据，
     * @param function
     * @param <T>
     * @param <R>
     * @return
     */
    public static <T, R> List<R> listDataByPage(List<T> params, Function<List<T>, List<R>> function) {
        return listDataByPage(params, PAGE_SIZE_200, function);
    }

    /**
     * 以分页的方式，多次批量获取数据，
     * @param function
     * @param <T>
     * @param <R>
     * @return
     */
    public static <T, R> List<R> listDataByPage(List<T> params, int pageSize, Function<List<T>, List<R>> function) {
        List<R> rList = new ArrayList<>();
        if(CollectionUtils.isEmpty(params)) {
            return rList;
        }
        long pageNum = 0;
        List<T> partyParams;
        do {
            long skipNum = pageNum++ * pageSize;
            partyParams = params.stream().skip(skipNum).limit(pageSize).collect(Collectors.toList());
            if(CollectionUtil.isEmpty(partyParams)) {
                break;
            }
            rList.addAll(function.apply(partyParams));
        } while (partyParams.size() % pageSize == 0);
        return rList;
    }

    /**
     * 以分页的方式，多次批量处理数据，
     * @param consumer
     * @param <T>
     * @return
     */
    public static <T> void processDataByPage(List<T> params, Consumer<List<T>> consumer) {
        processDataByPage(params, PAGE_SIZE_200, consumer);
    }

    public static <T> void processDataByPage(List<T> params, int pageSize, Consumer<List<T>> consumer) {
        if(isEmpty(params)) {
            return;
        }
        long pageNum = 0;
        List<T> partyParams;
        do {
            long skipNum = pageNum++ * pageSize;
            partyParams = params.stream().skip(skipNum).limit(pageSize).collect(Collectors.toList());
            if(isEmpty(partyParams)) {
                break;
            }
            try {
                consumer.accept(partyParams);
            } catch (Throwable t) {
                log.error("processDataByPage执行失败：{}", t.getMessage());
            }
        } while (partyParams.size() % pageSize == 0);
    }

    public static <T> int sumIntByPage(List<T> params,  ToIntFunction<List<T>> function) {
        return sumIntByPage(params, PAGE_SIZE_200, function);
    }

    public static <T> int sumIntByPage(List<T> params, int pageSize, ToIntFunction<List<T>> function) {
        if(CollectionUtils.isEmpty(params)) {
            return 0;
        }
        int number = 0;
        long pageNum = 0;
        List<T> partyParams;
        do {
            long skipNum = pageNum++ * pageSize;
            partyParams = params.stream().skip(skipNum).limit(pageSize).collect(Collectors.toList());
            if(CollectionUtil.isEmpty(partyParams)) {
                break;
            }
            number = function.applyAsInt(partyParams) + number;
        } while (partyParams.size() % pageSize == 0);
        return number;
    }

    public static <T> long sumLongByPage(List<T> params, ToLongFunction<List<T>> function) {
        return sumLongByPage(params, PAGE_SIZE_200, function);
    }

    public static <T> long sumLongByPage(List<T> params, int pageSize, ToLongFunction<List<T>> function) {
        if(CollectionUtils.isEmpty(params)) {
            return 0;
        }
        long number = 0;
        long pageNum = 0;
        List<T> partyParams;
        do {
            long skipNum = pageNum++ * pageSize;
            partyParams = params.stream().skip(skipNum).limit(pageSize).collect(Collectors.toList());
            if(CollectionUtil.isEmpty(partyParams)) {
                break;
            }
            number = function.applyAsLong(partyParams) + number;
        } while (partyParams.size() % pageSize == 0);
        return number;
    }

    public static <T> double sumDoubleByPage(List<T> params, ToDoubleFunction<List<T>> function) {
        return sumDoubleByPage(params, PAGE_SIZE_200, function);
    }

    public static <T> double sumDoubleByPage(List<T> params, int pageSize, ToDoubleFunction<List<T>> function) {
        if(CollectionUtils.isEmpty(params)) {
            return 0D;
        }
        double number = 0D;
        long pageNum = 0;
        List<T> partyParams;
        do {
            long skipNum = pageNum++ * pageSize;
            partyParams = params.stream().skip(skipNum).limit(pageSize).collect(Collectors.toList());
            if(CollectionUtil.isEmpty(partyParams)) {
                break;
            }
            number = function.applyAsDouble(partyParams) + number;
        } while (partyParams.size() % pageSize == 0);
        return number;
    }

    /**
     * 并行获取数据
     * @param taskExecutor
     * @param timeout
     * @param unit
     * @param params
     * @param function
     * @param <T>
     * @param <R>
     * @return
     */
    public static <T, R> List<R> concurrentListData(ThreadPoolTaskExecutor taskExecutor, long timeout, TimeUnit unit, List<T> params, Function<T, List<R>> function) {
        CountDownLatch countDownLatch = new CountDownLatch(params.size());
        List<Future<List<R>>> futures = new ArrayList<>();
        for(T param : params) {
            futures.add(taskExecutor.submit(() -> {
                try {
                    return function.apply(param);
                } finally {
                    countDownLatch.countDown();
                }
            }));
        }
        try {
            countDownLatch.await(timeout, unit);
        } catch (InterruptedException e) {
            throw new RuntimeException(e.getMessage());
        }
        List<R> rList = new ArrayList<>();
        for(Future<List<R>> future : futures) {
            try {
                rList.addAll(future.get());
            } catch (InterruptedException | ExecutionException e) {
                log.error(e.getMessage());
            }
        }
        return rList;
    }

    public static <T, R> List<R> concurrentListDataByPage(ThreadPoolTaskExecutor executorService, long timeout, TimeUnit unit, int pageSize, List<T> params, Function<List<T>, List<R>> function) {
        List<R> rList = new ArrayList<>();
        if (CollectionUtil.isEmpty(params)) {
            return rList;
        }
        long pageNum = 0;
        int count = (params.size() / pageSize) + 1;
        CountDownLatch countDownLatch = new CountDownLatch(count);
        List<Future<List<R>>> futures = new ArrayList<>();
        List<T> keyS;
        do {
            long skipNum = pageNum++ * pageSize;
            keyS = params.stream().skip(skipNum).limit(pageSize).collect(Collectors.toList());
            if (CollectionUtil.isEmpty(keyS)) {
                break;
            }
            List<T> finalKeyS = keyS;
            futures.add(executorService.submit(() -> {
                try {
                    return function.apply(finalKeyS);
                } finally {
                    countDownLatch.countDown();
                }
            }));
        } while(keyS.size() % pageSize == 0);
        try {
            countDownLatch.await(timeout, unit);
        } catch (InterruptedException e) {
            throw new RuntimeException(e.getMessage());
        }
        for (Future<List<R>> future : futures) {
            try {
                rList.addAll(future.get());
            } catch (InterruptedException | ExecutionException e) {
                log.error(e.getMessage());
            }
        }
        return rList;
    }

    public static <T> void concurrentProcessDataByPage(Executor executor, long timeout, TimeUnit unit, int pageSize, Collection<T> params, Consumer<Collection<T>> function) {
        if (CollectionUtil.isEmpty(params)) {
            return;
        }
        long pageNum = 0;
        int count = (params.size() / pageSize) + 1;
        CountDownLatch countDownLatch = new CountDownLatch(count);
        List<T> keyS;
        do {
            long skipNum = pageNum++ * pageSize;
            keyS = params.stream().skip(skipNum).limit(pageSize).collect(Collectors.toList());
            if (CollectionUtil.isEmpty(keyS)) {
                break;
            }
            List<T> finalKeyS = keyS;
            executor.execute(() -> {
                try {
                    function.accept(finalKeyS);
                } finally {
                    countDownLatch.countDown();
                }
            });
        } while (keyS.size() % pageSize == 0);
        try {
            countDownLatch.await(timeout, unit);
        } catch (InterruptedException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * 并发求和短整型
     * @param taskExecutor
     * @param timeout
     * @param unit
     * @param params
     * @param function
     * @param <T>
     * @return
     */
    public static <T> int concurrentSumInt(ThreadPoolTaskExecutor taskExecutor, long timeout, TimeUnit unit, List<T> params, ToIntFunction<T> function) {
        if(CollectionUtils.isEmpty(params)) {
            return 0;
        }
        CountDownLatch countDownLatch = new CountDownLatch(params.size());
        List<Future<Integer>> futures = new ArrayList<>();
        for(T param : params) {
            futures.add(taskExecutor.submit(() -> {
                try {
                    return function.applyAsInt(param);
                } finally {
                    countDownLatch.countDown();
                }
            }));
        }
        try {
            countDownLatch.await(timeout, unit);
        } catch (InterruptedException e) {
            throw new RuntimeException(e.getMessage());
        }
        int number = 0;
        for(Future<Integer> future : futures) {
            try {
                number += future.get();
            } catch (InterruptedException | ExecutionException e) {
                log.error("concurrentSumInt：{}", e.getMessage());
            }
        }
        return number;
    }

    /**
     * 并发求和长整型
     * @param taskExecutor
     * @param timeout
     * @param unit
     * @param params
     * @param function
     * @param <T>
     * @return
     */
    public static <T> long concurrentSumLong(ThreadPoolTaskExecutor taskExecutor, long timeout, TimeUnit unit, List<T> params, ToLongFunction<T> function) {
        if(CollectionUtils.isEmpty(params)) {
            return 0L;
        }
        CountDownLatch countDownLatch = new CountDownLatch(params.size());
        List<Future<Long>> futures = new ArrayList<>();
        for(T param : params) {
            futures.add(taskExecutor.submit(() -> {
                try {
                    return function.applyAsLong(param);
                } finally {
                    countDownLatch.countDown();
                }
            }));
        }
        try {
            countDownLatch.await(timeout, unit);
        } catch (InterruptedException e) {
            throw new RuntimeException(e.getMessage());
        }
        long number = 0L;
        for(Future<Long> future : futures) {
            try {
                number += future.get();
            } catch (InterruptedException | ExecutionException e) {
                log.error("concurrentSumLong：{}", e.getMessage());
            }
        }
        return number;
    }


    /**
     * 并发求和浮点型
     * @param taskExecutor
     * @param timeout
     * @param unit
     * @param params
     * @param function
     * @param <T>
     * @return
     */
    public static <T> double concurrentSumDouble(ThreadPoolTaskExecutor taskExecutor, long timeout, TimeUnit unit, List<T> params, ToDoubleFunction<T> function) {
        if(CollectionUtils.isEmpty(params)) {
            return 0D;
        }
        CountDownLatch countDownLatch = new CountDownLatch(params.size());
        List<Future<Double>> futures = new ArrayList<>();
        for(T param : params) {
            futures.add(taskExecutor.submit(() -> {
                try {
                    return function.applyAsDouble(param);
                } finally {
                    countDownLatch.countDown();
                }
            }));
        }
        try {
            countDownLatch.await(timeout, unit);
        } catch (InterruptedException e) {
            throw new RuntimeException(e.getMessage());
        }
        double number = 0D;
        for(Future<Double> future : futures) {
            try {
                number += future.get();
            } catch (InterruptedException | ExecutionException e) {
                log.error("concurrentSumDouble：{}", e.getMessage());
            }
        }
        return number;
    }

    public static boolean isEmpty(@Nullable Object obj) {
        if (obj == null) {
            return true;
        } else if (obj instanceof Optional) {
            return !((Optional)obj).isPresent();
        } else if (obj instanceof CharSequence) {
            return ((CharSequence)obj).length() == 0;
        } else if (obj.getClass().isArray()) {
            return Array.getLength(obj) == 0;
        } else if (obj instanceof Collection) {
            return ((Collection)obj).isEmpty();
        } else {
            return obj instanceof Map ? ((Map)obj).isEmpty() : false;
        }
    }
}
