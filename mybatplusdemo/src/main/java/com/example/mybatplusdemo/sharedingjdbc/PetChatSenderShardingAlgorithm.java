package com.example.mybatplusdemo.sharedingjdbc;

import org.apache.shardingsphere.sharding.api.sharding.standard.PreciseShardingValue;
import org.apache.shardingsphere.sharding.api.sharding.standard.RangeShardingValue;
import org.apache.shardingsphere.sharding.api.sharding.standard.StandardShardingAlgorithm;

import java.text.SimpleDateFormat;
import java.util.*;

public class PetChatSenderShardingAlgorithm implements StandardShardingAlgorithm<Date> {

    @Override
    public void init(Properties properties) {
        // 可选的初始化配置
    }

    @Override
    public String getType() {
        return "CLASS_BASED";
    }

    @Override
    public Properties getProps() {
        return new Properties();
    }

    // 精确查询：例如 WHERE chat_date = '2025-05-06'
    @Override
    public String doSharding(Collection<String> availableTargetNames,
                             PreciseShardingValue<Date> shardingValue) {
        String suffix = new SimpleDateFormat("yyyyMM").format(shardingValue.getValue());
        for (String target : availableTargetNames) {
            if (target.endsWith(suffix)) {
                return target;
            }
        }
        return availableTargetNames.iterator().next(); // fallback
    }

    // 范围查询：例如 WHERE chat_date BETWEEN '2025-01-01' AND '2025-03-31'
    @Override
    public Collection<String> doSharding(Collection<String> availableTargetNames,
                                         RangeShardingValue<Date> rangeShardingValue) {
        Set<String> result = new HashSet<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM");

        Date lower = rangeShardingValue.getValueRange().hasLowerBound()
                ? rangeShardingValue.getValueRange().lowerEndpoint()
                : null;

        Date upper = rangeShardingValue.getValueRange().hasUpperBound()
                ? rangeShardingValue.getValueRange().upperEndpoint()
                : null;

        if (lower == null || upper == null) {
            return availableTargetNames; // 没法判断，返回所有表
        }

        Calendar cal = Calendar.getInstance();
        cal.setTime(lower);

        while (!cal.getTime().after(upper)) {
            String suffix = sdf.format(cal.getTime());
            for (String table : availableTargetNames) {
                if (table.endsWith(suffix)) {
                    result.add(table);
                }
            }
            cal.add(Calendar.MONTH, 1); // 向后推一个月
        }

        return result.isEmpty() ? availableTargetNames : result;
    }
}
