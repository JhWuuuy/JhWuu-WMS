package org.jeecg.modules.wms.warehouse.service.impl;

import com.aliyuncs.utils.StringUtils;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import org.jeecg.common.exception.JeecgBootException;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.modules.wms.config.WarehouseDictEnum;
import org.jeecg.modules.wms.warehouse.entity.WmsStorageZones;
import org.jeecg.modules.wms.warehouse.entity.WmsWarehouses;
import org.jeecg.modules.wms.warehouse.mapper.WmsStorageZonesMapper;
import org.jeecg.modules.wms.warehouse.service.IWmsStorageZonesService;
import org.jeecg.modules.wms.warehouse.service.IWmsWarehousesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Description: 储区表
 * @Author: jeecg-boot
 * @Date:   2026-04-24
 * @Version: V1.0
 */
@Service
public class WmsStorageZonesServiceImpl extends ServiceImpl<WmsStorageZonesMapper, WmsStorageZones> implements IWmsStorageZonesService {
    @Autowired
    private IWmsWarehousesService wmsWarehousesService;

    /**
     * 获取仓库分区分页列表
     * @param wmsStorageZones 仓库分区对象
     * @param pageNo 当前页码
     * @param pageSize 每页显示数量
     * @param req 请求参数
     */
    @Override    // 标注重写父类方法
    public IPage<WmsStorageZones> getZonePageList(WmsStorageZones wmsStorageZones, Integer pageNo, Integer pageSize, HttpServletRequest req) {
        // 初始化查询条件包装器，根据传入的仓库区域对象和请求参数构建查询条件
        QueryWrapper<WmsStorageZones> queryWrapper = QueryGenerator.initQueryWrapper(wmsStorageZones, req.getParameterMap());
        // 创建分页对象，设置当前页码和每页显示数量
        Page<WmsStorageZones> page = new Page<WmsStorageZones>(pageNo, pageSize);

        // 使用分页和查询条件获取仓库分区分页列表
        IPage<WmsStorageZones> pageList = page(page, queryWrapper);
        // 如果分页结果为空，直接返回结果
        if (pageList.getRecords().isEmpty()) {
            return pageList;
        }

        // 获取所有仓库分区的仓库ID列表
        List<String> warehouseIds = pageList.getRecords().stream().map(WmsStorageZones::getWarehouseId)
                .filter(id -> id != null && !id.isEmpty())
                .distinct()
                .toList();
        // 如果仓库ID列表为空，直接返回结果
        if (warehouseIds.isEmpty()) {
            return pageList;
        }

        // 根据仓库ID列表查询所有仓库信息
        List<WmsWarehouses> wmsWarehouses = wmsWarehousesService.listByIds(warehouseIds);
        // 如果仓库信息为空，直接返回结果
        if (wmsWarehouses.isEmpty()) {
            return pageList;
        }

        // 将仓库信息转换为Map，键为仓库ID，值为仓库名称
        Map<String, String> warehouseMap = wmsWarehouses.stream().collect(Collectors.toMap(WmsWarehouses::getId, WmsWarehouses::getWarehouseName));
        // 将仓库名称添加到仓库分区对象中
        pageList.getRecords().forEach(item -> {
            item.setWarehouseName(warehouseMap.getOrDefault(item.getWarehouseId(), ""));
        });

        return pageList;
    }

    /**
     * 启用储区方法
     * @param id 储区ID
     * @throws JeecgBootException 当储区ID为空、储区不存在或储区已启用时抛出异常
     */
    @Override
    public void enable(String id) {
    // 检查储区ID是否为空
        if(StringUtils.isEmpty(id)) {
            throw new JeecgBootException("储区ID为空");
        }

    // 根据ID获取储区信息
        WmsStorageZones wmsStorageZones = getById(id);
    // 检查储区是否存在
        if(wmsStorageZones == null) {
            throw new JeecgBootException("储区不存在");
        }

    // 检查储区是否已启用
        if(wmsStorageZones.getStatus().equals(WarehouseDictEnum.STATUS_ACTIVE.getCode())) {
            throw new JeecgBootException("储区已启用");
        }

    // 设置储区状态为启用
        wmsStorageZones.setStatus(WarehouseDictEnum.STATUS_ACTIVE.getCode());
    // 更新储区信息
        updateById(wmsStorageZones);
    }

    /**
     * 禁用储区的方法
     * @param id 储区ID
     * @throws JeecgBootException 当储区ID为空、储区不存在或储区已禁用时抛出异常
     */
    @Override
    public void disable(String id) {
        // 检查储区ID是否为空
        if(StringUtils.isEmpty(id)) {
            throw new JeecgBootException("储区ID为空");
        }

        // 根据ID获取储区信息
        WmsStorageZones wmsStorageZones = getById(id);
        // 检查储区是否存在
        if(wmsStorageZones == null) {
            throw new JeecgBootException("储区不存在");
        }

        // 检查储区是否已禁用
        if(wmsStorageZones.getStatus().equals(WarehouseDictEnum.STATUS_INACTIVE.getCode())) {
            throw new JeecgBootException("储区已禁用");
        }

        // 设置储区状态为禁用
        wmsStorageZones.setStatus(WarehouseDictEnum.STATUS_INACTIVE.getCode());
        // 更新储区信息
        updateById(wmsStorageZones);
    }
}
