package org.jeecg.modules.wms.warehouse.service.impl;

import com.aliyuncs.utils.StringUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.jeecg.common.exception.JeecgBootException;
import org.jeecg.modules.wms.config.WarehouseDictEnum;
import org.jeecg.modules.wms.warehouse.entity.WmsWarehouses;
import org.jeecg.modules.wms.warehouse.mapper.WmsWarehousesMapper;
import org.jeecg.modules.wms.warehouse.service.IWmsWarehousesService;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.transaction.annotation.Transactional;

/**
 * @Description: 仓库表
 * @Author: jeecg-boot
 * @Date:   2026-04-24
 * @Version: V1.0
 */
@Service
public class WmsWarehousesServiceImpl extends ServiceImpl<WmsWarehousesMapper, WmsWarehouses> implements IWmsWarehousesService {

/**
 * 添加仓库信息
 * @param wmsWarehouses 仓库对象，包含需要添加的仓库信息
 * @throws RuntimeException 当仓库代码已存在时抛出异常
 */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void add(WmsWarehouses wmsWarehouses) {
    // 查询仓库代码是否已存在
        LambdaQueryWrapper<WmsWarehouses> queryWrapper = new LambdaQueryWrapper<WmsWarehouses>()
                .eq(WmsWarehouses::getWarehouseCode, wmsWarehouses.getWarehouseCode());

        if (count(queryWrapper) > 0) {
            throw new JeecgBootException("仓库代码已存在");
        }

        wmsWarehouses.setStatus(WarehouseDictEnum.STATUS_CREATED.getCode());
        save(wmsWarehouses);
    }

    /**
     * 修改仓库信息
     * @param wmsWarehouses 仓库对象，包含需要修改的仓库信息
     * @throws RuntimeException 当仓库代码与除本身以外仓库重复时报错
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void edit(WmsWarehouses wmsWarehouses) {
        LambdaQueryWrapper<WmsWarehouses> queryWrapper = new LambdaQueryWrapper<WmsWarehouses>()
                .eq(WmsWarehouses::getWarehouseCode, wmsWarehouses.getWarehouseCode())
                .ne(WmsWarehouses::getId, wmsWarehouses.getId());

        if (count(queryWrapper) > 0) {
            throw new JeecgBootException("仓库代码已存在");
        }

        updateById(wmsWarehouses);
    }

    /**
     * 启用仓库
     * @param id 仓库ID
     * @throws RuntimeException 当仓库不存在或仓库已启用时抛出异常
     */
    @Override
    public void enable(String id) {
        if (StringUtils.isEmpty(id)) {
            throw new JeecgBootException("仓库ID为空");
        }

        WmsWarehouses wmsWarehouses = getById(id);
        if (wmsWarehouses == null) {
            throw new JeecgBootException("仓库不存在");
        }

        if (wmsWarehouses.getStatus().equals(WarehouseDictEnum.STATUS_ACTIVE.getCode())) {
            throw new JeecgBootException("仓库已启用");
        }

        wmsWarehouses.setStatus(WarehouseDictEnum.STATUS_ACTIVE.getCode());
        updateById(wmsWarehouses);
    }

    /**
     * 禁用仓库
     * @param id 仓库ID
     * @throws RuntimeException 当仓库不存在或仓库已禁用时抛出异常
     */
    @Override
    public void disable(String id) {
        if (StringUtils.isEmpty(id)) {
            throw new JeecgBootException("仓库ID为空");
        }

        WmsWarehouses wmsWarehouses = getById(id);
        if (wmsWarehouses == null) {
            throw new JeecgBootException("仓库不存在");
        } else if (wmsWarehouses.getStatus().equals(WarehouseDictEnum.STATUS_INACTIVE.getCode())) {
            throw new JeecgBootException("仓库已禁用");
        } else if (wmsWarehouses.getStatus().equals(WarehouseDictEnum.STATUS_CREATED.getCode())) {
            throw new JeecgBootException("仓库为启用状态方可禁用");
        }
        wmsWarehouses.setStatus(WarehouseDictEnum.STATUS_INACTIVE.getCode());
        updateById(wmsWarehouses);
    }
}
