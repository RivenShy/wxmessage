package com.example.mybatplusdemo.ma.base;

import com.baomidou.mybatisplus.core.enums.SqlMethod;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.baomidou.mybatisplus.core.toolkit.Assert;
import com.baomidou.mybatisplus.core.toolkit.ReflectionKit;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.mybatplusdemo.config.Func;
import com.example.mybatplusdemo.entity.DBEntity;
import com.example.mybatplusdemo.entity.PetBaseEntity;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.binding.MapperMethod;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.validation.annotation.Validated;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Validated
@Slf4j
public class PetBaseServiceImpl<M extends BaseMapper<T>, T extends DBEntity> extends ServiceImpl<M, T> implements PetBaseService<T> {
    public PetBaseServiceImpl() {
    }

    @Override
    public boolean deleteLogic(List<Long> ids) {
        return false;
    }

    @Override
    public boolean changeStatus(List<Long> ids, Integer status) {
        return false;
    }

    public boolean save(T entity) {
        try {
            this.saveEntityProperties(entity);
        } catch (Throwable e) {
            log.error("Exception occurred,msg: {}", e.getMessage(), e);
            throw new RuntimeException(e);
        }
        return super.save(entity);
    }

    public boolean saveBatch(Collection<T> entityList, int batchSize) {
        entityList.forEach(this::saveEntityProperties);
        return super.saveBatch(entityList, batchSize);
    }

    public <E extends DBEntity> void saveEntityProperties(E entity) {
        try {
//            Date now = DateUtils.getNowDate();
//            entity.setCreationDate(now);
//            entity.setLastUpdateDate(now);
//            Authentication authentication = SecurityUtils.getAuthentication();
//            // todo 定时任务 authentication 为null
//            if(authentication != null) {
//                LoginUser loginUser = SecurityUtils.getLoginUserForSave();
//                if(loginUser != null) {
//                    SysUser user = loginUser.getUser();
//                    SysDept sysDept = user.getDept();
//                    if (Func.isNotEmpty(user)) {
//                        entity.setCreatedBy(user.getId());
//                        entity.setCreateDept(user.getDeptId());
//                        entity.setLastUpdatedBy(user.getId());
//                        entity.setLastUpdateLogin(user.getId());
//                        if(Func.isNotEmpty(sysDept)) {
//                            entity.setCreateDept(sysDept.getId());
//                        }
//                    }
//                }
//            }
//            if (entity instanceof PetBaseEntity) {
//                ((PetBaseEntity)entity).setIsDelete(0);
//            }
//            if (Func.isEmpty(entity.getStatus())) {
//                entity.setStatus(0);
//            }
        } catch (Exception e) {
            log.error("Exception occurred,msg: {}", e.getMessage(), e);
            throw new RuntimeException(e.getMessage());
        }
    }


    public boolean updateById(T entity) {
        this.updateEntityProperties(entity);
        return super.updateById(entity);
    }

    public <E extends DBEntity> void updateEntityProperties(E entity) {
        try {
//            Authentication authentication = SecurityUtils.getAuthentication();
//            // todo 定时任务 authentication 为null
//            if(authentication != null) {
//                SysUser user = SecurityUtils.getLoginUser().getUser();
//                Date now = DateUtils.getNowDate();
//                entity.setLastUpdateDate(now);
//                if (Func.isNotEmpty(user)) {
//                    entity.setLastUpdatedBy(user.getId());
//                }
//            }
        } catch (Throwable e) {
            log.error("Exception occurred,msg: {}", e.getMessage(), e);
            throw new RuntimeException(e.getMessage());
        }
    }

    public boolean saveOrUpdateBatch(Collection<T> entityList, int batchSize) {
        TableInfo tableInfo = TableInfoHelper.getTableInfo(this.entityClass);
        Assert.notNull(tableInfo, "error: can not execute. because can not find cache of TableInfo for entity!", new Object[0]);
        String keyProperty = tableInfo.getKeyProperty();
        Assert.notEmpty(keyProperty, "error: can not execute. because can not find column for id from entity!", new Object[0]);
        return this.executeBatch(entityList, batchSize, (sqlSession, entity) -> {
            Object idVal = ReflectionKit.getFieldValue(entity, keyProperty);
            if (!StringUtils.checkValNull(idVal) && !Objects.isNull(this.getById((Serializable)idVal))) {
                MapperMethod.ParamMap<T> param = new MapperMethod.ParamMap();
                this.updateEntityProperties(entity);
                param.put("et", entity);
                sqlSession.update(tableInfo.getSqlStatement(SqlMethod.UPDATE_BY_ID.getMethod()), param);
            } else {
                this.saveEntityProperties(entity);
                sqlSession.insert(tableInfo.getSqlStatement(SqlMethod.INSERT_ONE.getMethod()), entity);
            }

        });
    }
}
