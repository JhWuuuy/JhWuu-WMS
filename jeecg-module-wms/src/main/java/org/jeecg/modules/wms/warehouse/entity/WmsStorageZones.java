package org.jeecg.modules.wms.warehouse.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.jeecg.common.aspect.annotation.Dict;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * @Description: 储区表
 * @Author: jeecg-boot
 * @Date:   2026-04-24
 * @Version: V1.0
 */
@Data
@TableName("wms_storage_zones")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@Schema(description="储区表")
public class WmsStorageZones implements Serializable {
    private static final long serialVersionUID = 1L;

	/**主键*/
	@TableId(type = IdType.ASSIGN_ID)
    @Schema(description = "主键")
    private String id;
	/**创建人*/
    @Schema(description = "创建人")
    private String createBy;
	/**创建日期*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @Schema(description = "创建日期")
    private Date createTime;
	/**更新人*/
    @Schema(description = "更新人")
    private String updateBy;
	/**更新日期*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @Schema(description = "更新日期")
    private Date updateTime;
	/**库区编码*/
	@Excel(name = "库区编码", width = 15)
    @Schema(description = "库区编码")
    private String zoneCode;
	/**库区名称*/
	@Excel(name = "库区名称", width = 15)
    @Schema(description = "库区名称")
    private String zoneName;
	/**库区类型*/
	@Excel(name = "库区类型", width = 15)
    @Schema(description = "库区类型")
    @Dict(dicCode = "zone_type")
    private String zoneType;
	/**状态: 创建,禁用, 启用*/
	@Excel(name = "状态: 创建,禁用, 启用", width = 15, dicCode = "dict_item_status")
	@Dict(dicCode = "wms_status")
    @Schema(description = "状态: 创建,禁用, 启用")
    private String status;
	/**是否可售库存 0-否, 1-是*/
	@Excel(name = "是否可售库存 0-否, 1-是", width = 15, dicCode = "yn")
	@Dict(dicCode = "yn")
    @Schema(description = "是否可售库存 0-否, 1-是")
    private String isSellable;
	/**所属仓库*/
	@Excel(name = "所属仓库", width = 15)
    @Schema(description = "所属仓库")
    private String warehouseId;
    /**所属仓库名称，用于显示*/
    @TableField(exist = false)
    @Schema(description = "所属仓库名称")
    private String warehouseName;
}
