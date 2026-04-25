package org.jeecg.modules.wms.warehouse.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.jeecg.common.system.base.controller.JeecgController;
import org.jeecg.modules.wms.warehouse.entity.WmsStorageZones;
import org.jeecg.modules.wms.warehouse.service.IWmsStorageZonesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.Arrays;

 /**
 * @Description: 储区表
 * @Author: jeecg-boot
 * @Date:   2026-04-24
 * @Version: V1.0
 */
@Tag(name="储区表")
@RestController
@RequestMapping("/warehouse/wmsStorageZones")
@Slf4j
public class WmsStorageZonesController extends JeecgController<WmsStorageZones, IWmsStorageZonesService> {
	@Autowired
	private IWmsStorageZonesService wmsStorageZonesService;

	 /**
	  *  启用
	  *
	  * @param id
	  * @return
	  */
	 @AutoLog(value = "储区表-启用")
	 @Operation(summary="储区表-启用")
	 @RequiresPermissions("wms_storageZones:enable")
	 @RequestMapping(value = "/enable", method = {RequestMethod.PUT,RequestMethod.POST})
	 public Result<String> enable(@RequestParam(name="id",required=true) String id) {
		 wmsStorageZonesService.enable(id);
		 return Result.OK("启用成功!");
	 }

	 /**
	  * 禁用
	  *
	  * @param id
	  * @return
	  */
	 @AutoLog(value = "储区表-禁用")
	 @Operation(summary="储区表-禁用")
	 @RequiresPermissions("wms_storageZones:disable")
	 @RequestMapping(value = "/disable", method = {RequestMethod.PUT,RequestMethod.POST})
	 public Result<String> disable(@RequestParam(name="id",required=true) String id) {
		 wmsStorageZonesService.disable(id);
		 return Result.OK("禁用成功!");
	 }

	/**
	 * 分页列表查询
	 *
	 * @param wmsStorageZones
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@AutoLog(value = "储区表-分页列表查询")
	@Operation(summary="储区表-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<WmsStorageZones>> queryPageList(WmsStorageZones wmsStorageZones,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		IPage<WmsStorageZones> wmsStorageZonesIPage = wmsStorageZonesService.getZonePageList(wmsStorageZones, pageNo, pageSize, req);
		return Result.OK(wmsStorageZonesIPage);
	}

	/**
	 *   添加
	 *
	 * @param wmsStorageZones
	 * @return
	 */
	@AutoLog(value = "储区表-添加")
	@Operation(summary="储区表-添加")
	@RequiresPermissions("warehouse:wms_storage_zones:add")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody WmsStorageZones wmsStorageZones) {
		wmsStorageZonesService.save(wmsStorageZones);
		return Result.OK("添加成功！");
	}
	
	/**
	 *  编辑
	 *
	 * @param wmsStorageZones
	 * @return
	 */
	@AutoLog(value = "储区表-编辑")
	@Operation(summary="储区表-编辑")
	@RequiresPermissions("warehouse:wms_storage_zones:edit")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody WmsStorageZones wmsStorageZones) {
		wmsStorageZonesService.updateById(wmsStorageZones);
		return Result.OK("编辑成功!");
	}
	
	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "储区表-通过id删除")
	@Operation(summary="储区表-通过id删除")
	@RequiresPermissions("warehouse:wms_storage_zones:delete")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		wmsStorageZonesService.removeById(id);
		return Result.OK("删除成功!");
	}
	
	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "储区表-批量删除")
	@Operation(summary="储区表-批量删除")
	@RequiresPermissions("warehouse:wms_storage_zones:deleteBatch")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.wmsStorageZonesService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功!");
	}
	
	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	//@AutoLog(value = "储区表-通过id查询")
	@Operation(summary="储区表-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<WmsStorageZones> queryById(@RequestParam(name="id",required=true) String id) {
		WmsStorageZones wmsStorageZones = wmsStorageZonesService.getById(id);
		if(wmsStorageZones==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(wmsStorageZones);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param wmsStorageZones
    */
    @RequiresPermissions("warehouse:wms_storage_zones:exportXls")
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, WmsStorageZones wmsStorageZones) {
        return super.exportXls(request, wmsStorageZones, WmsStorageZones.class, "储区表");
    }

    /**
      * 通过excel导入数据
    *
    * @param request
    * @param response
    * @return
    */
    @RequiresPermissions("warehouse:wms_storage_zones:importExcel")
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        return super.importExcel(request, response, WmsStorageZones.class);
    }

}
