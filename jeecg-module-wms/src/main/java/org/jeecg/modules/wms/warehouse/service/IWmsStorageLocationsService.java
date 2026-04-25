package org.jeecg.modules.wms.warehouse.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import jakarta.servlet.http.HttpServletRequest;
import org.jeecg.modules.wms.warehouse.entity.WmsStorageLocations;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @Description: 储位表
 * @Author: jeecg-boot
 * @Date:   2026-04-24
 * @Version: V1.0
 */
public interface IWmsStorageLocationsService extends IService<WmsStorageLocations> {

    IPage<WmsStorageLocations> getLocationPageList(WmsStorageLocations wmsStorageLocations, Integer pageNo, Integer pageSize, HttpServletRequest req);
}
