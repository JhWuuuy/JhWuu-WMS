package org.jeecg.modules.wms.goods.service.impl;

import org.jeecg.common.exception.JeecgBootException;
import org.jeecg.common.util.RedisUtil;
import org.jeecg.modules.wms.goods.entity.WmsCargoOwners;
import org.jeecg.modules.wms.goods.mapper.WmsCargoOwnersMapper;
import org.jeecg.modules.wms.goods.service.IWmsCargoOwnersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.transaction.annotation.Transactional;

/**
 * @Description: 货主表
 * @Author: jeecg-boot
 * @Date:   2026-04-22
 * @Version: V1.0
 */
@Service
public class WmsCargoOwnersServiceImpl extends ServiceImpl<WmsCargoOwnersMapper, WmsCargoOwners> implements IWmsCargoOwnersService {
    @Autowired
    private RedisUtil redisUtil;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void add(WmsCargoOwners wmsCargoOwners) {
        wmsCargoOwners.setOwnerCode(generateOwnerCode());
        save(wmsCargoOwners);
    }

/**
 * 生成货主编码的方法
 * 编码规则：C+5位序号，序号使用redis自增序号实现
 * @return 返回生成的货主编码，格式为"C"后跟5位数字
 */
    public String generateOwnerCode() {
        //编码规则：C+5位序号，序号使用redis自增序号实现
        //调用redis的incr函数获取自增序号
        long incr = 0;

        try {
            incr = redisUtil.incr("WMS_CARGO_OWNERS_CODE", 1);
        } catch (Exception e) {
            throw new JeecgBootException("生成货主编码失败");
        }

        //将自增序号转换为5位字符串，不足5位前面补0
        String code = "C" + String.format("%05d", incr);
        return code;
    }
}
