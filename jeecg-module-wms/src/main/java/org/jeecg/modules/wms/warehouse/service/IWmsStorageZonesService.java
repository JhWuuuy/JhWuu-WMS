package org.jeecg.modules.wms.warehouse.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import jakarta.servlet.http.HttpServletRequest;
import org.jeecg.modules.wms.warehouse.entity.WmsStorageZones;

/**
 * @Description: 储区表
 * @Author: jeecg-boot
 * @Date:   2026-04-24
 * @Version: V1.0
 */
public interface IWmsStorageZonesService extends IService<WmsStorageZones> {


    IPage<WmsStorageZones> getZonePageList(WmsStorageZones wmsStorageZones, Integer pageNo, Integer pageSize, HttpServletRequest req);

    void enable(String id);

    void disable(String id);
}
