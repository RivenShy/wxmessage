//package com.example.mybatplusdemo.sharedingjdbc;
//
//
//import org.apache.shardingsphere.sharding.api.sharding.standard.StandardShardingAlgorithm;
//import org.apache.shardingsphere.sharding.api.sharding.standard.PreciseShardingValue;
//import org.apache.shardingsphere.sharding.api.sharding.standard.RangeShardingValue;
//
//import java.sql.Timestamp;
//import java.time.LocalDateTime;
//import java.util.Collection;
//import java.util.Properties;
//
//public class MonthShardingAlgorithm implements StandardShardingAlgorithm<Timestamp> {
//
//    @Override
//    public String doSharding(Collection<String> availableTargetNames, PreciseShardingValue<Timestamp> shardingValue) {
//        LocalDateTime dateTime = shardingValue.getValue().toLocalDateTime();
//        String suffix = String.format("%04d%02d", dateTime.getYear(), dateTime.getMonthValue());
//
//        for (String tableName : availableTargetNames) {
//            if (tableName.endsWith(suffix)) {
//                return tableName;
//            }
//        }
//        throw new UnsupportedOperationException("No table found for suffix: " + suffix);
//    }
//
//    @Override
//    public Collection<String> doSharding(Collection<String> availableTargetNames,
//                                         RangeShardingValue<Timestamp> shardingValue) {
//        // 返回所有表，表示 range 查询时扫描所有
//        return availableTargetNames;
//    }
//
//    @Override
//    public String getType() {
//        return "class_based";
//    }
//
//    @Override
//    public Properties getProps() {
//        return new Properties();
//    }
//
//    @Override
//    public void init(Properties properties) {
//        // 根据传入的 Properties 初始化
//    }
//}
