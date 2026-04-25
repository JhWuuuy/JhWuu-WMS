package org.jeecg.modules.wms.warehouse.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.modules.wms.warehouse.entity.WmsStorageLocations;
import org.jeecg.modules.wms.warehouse.entity.WmsStorageZones;
import org.jeecg.modules.wms.warehouse.entity.WmsWarehouses;
import org.jeecg.modules.wms.warehouse.mapper.WmsStorageLocationsMapper;
import org.jeecg.modules.wms.warehouse.service.IWmsStorageLocationsService;
import org.jeecg.modules.wms.warehouse.service.IWmsStorageZonesService;
import org.jeecg.modules.wms.warehouse.service.IWmsWarehousesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Description: 储位表
 * @Author: jeecg-boot
 * @Date:   2026-04-24
 * @Version: V1.0
 */
@Service
public class WmsStorageLocationsServiceImpl extends ServiceImpl<WmsStorageLocationsMapper, WmsStorageLocations> implements IWmsStorageLocationsService {
    @Autowired
    private IWmsWarehousesService wmsWarehousesService;
    @Autowired
    private IWmsStorageZonesService wmsStorageZonesService;

    @Override
    /**
     * 获取仓库位置分页列表
     * @param wmsStorageLocations 仓库位置查询条件对象
     * @param pageNo 当前页码
     * @param pageSize 每页显示条数
     * @param HttpServletRequest HTTP请求对象，用于获取请求参数
     * @return 返回分页后的仓库位置列表数据
     */
    public IPage<WmsStorageLocations> getLocationPageList(WmsStorageLocations wmsStorageLocations, Integer pageNo, Integer pageSize, HttpServletRequest req) {
        // 初始化查询条件包装器
        QueryWrapper<WmsStorageLocations> queryWrapper = QueryGenerator.initQueryWrapper(wmsStorageLocations, req.getParameterMap());
        // 创建分页对象
        Page<WmsStorageLocations> page = new Page<WmsStorageLocations>(pageNo, pageSize);

        // 执行分页查询
        IPage<WmsStorageLocations> pageList = page(page, queryWrapper);
        // 如果查询结果为空，直接返回
        if (pageList.getRecords().size() <= 0) {
            return pageList;
        }

        // 获取所有储位的仓库ID列表
        List<String> warehouseIds = pageList.getRecords().stream().map(WmsStorageLocations::getWarehouseId)
                .filter(id -> id != null && !id.isEmpty())
                .distinct()
                .toList();
        // 获取所有储位的储区ID列表
        List<String> zoneIds = pageList.getRecords().stream().map(WmsStorageLocations::getZoneId)
                .filter(id -> id != null && !id.isEmpty())
                .distinct()
                .toList();

        // 将仓库信息和储区信息转为map
        Map<String, String> warehouseMap = warehouseIds.isEmpty()?
                new HashMap<>() :
                wmsWarehousesService.listByIds(warehouseIds).stream()
                        .collect(Collectors.toMap(WmsWarehouses::getId, WmsWarehouses::getWarehouseName));
        Map<String, String> zoneMap = zoneIds.isEmpty()?
                new HashMap<>() :
                wmsStorageZonesService.listByIds(zoneIds).stream()
                        .collect(Collectors.toMap(WmsStorageZones::getId, WmsStorageZones::getZoneName));

        // 将仓库名称和储区名称添加到储位对象中
        pageList.getRecords().forEach(item -> {
            item.setWarehouseName(warehouseMap.getOrDefault(item.getWarehouseId(), ""));
            item.setZoneName(zoneMap.getOrDefault(item.getZoneId(), ""));
        });

        return pageList;
    }
}
