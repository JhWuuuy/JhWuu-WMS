package org.jeecg.modules.wms.goods.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.jeecg.common.exception.JeecgBootException;
import org.jeecg.common.system.vo.SelectTreeModel;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.wms.goods.entity.WmsProductCategories;
import org.jeecg.modules.wms.goods.mapper.WmsProductCategoriesMapper;
import org.jeecg.modules.wms.goods.service.IWmsProductCategoriesService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 生成分类编码的工具方法
 */
class CategoryCodeGenerator {
    /**
     * 生成下一个两位编码
     * @param currentCode 当前最大编码
     * @return 下一个编码
     */
    public static String generateNextTwoDigitCode(String currentCode) {
        // 初始化数字为1，作为默认值
        int num = 1;
        // 检查当前编码是否为null或空字符串
        if (currentCode != null && !currentCode.isEmpty()) {
            try {
                // 尝试将当前编码转换为整数并加1
                num = Integer.parseInt(currentCode) + 1;
            } catch (NumberFormatException e) {
                // 如果转换失败，则使用默认值1
                num = 1;
            }
        }
        // 将数字格式化为两位数，不足两位前面补0
        return String.format("%02d", num);
    }
}

/**
 * @Description: 商品类别
 * @Author: jeecg-boot
 * @Date:   2026-04-26
 * @Version: V1.0
 */
@Service
public class WmsProductCategoriesServiceImpl extends ServiceImpl<WmsProductCategoriesMapper, WmsProductCategories> implements IWmsProductCategoriesService {

	@Override
	public void addWmsProductCategories(WmsProductCategories wmsProductCategories) {
	   //新增时设置hasChild为0
	    wmsProductCategories.setHasChild(IWmsProductCategoriesService.NOCHILD);
		if(oConvertUtils.isEmpty(wmsProductCategories.getParentId())){
			wmsProductCategories.setParentId(IWmsProductCategoriesService.ROOT_PID_VALUE);
		}else{
			//如果当前节点父ID不为空 则设置父节点的hasChildren 为1
			WmsProductCategories parent = baseMapper.selectById(wmsProductCategories.getParentId());
			if(parent!=null && !"1".equals(parent.getHasChild())){
				parent.setHasChild("1");
				baseMapper.updateById(parent);
			}
		}
		// 自动生成分类编码
		String categoryCode = generateCategoryCode(wmsProductCategories.getParentId());
		wmsProductCategories.setCategoryCode(categoryCode);
		baseMapper.insert(wmsProductCategories);
	}
	
	@Override
	public void updateWmsProductCategories(WmsProductCategories wmsProductCategories) {
		WmsProductCategories entity = this.getById(wmsProductCategories.getId());
		if(entity==null) {
			throw new JeecgBootException("未找到对应实体");
		}
		String old_pid = entity.getParentId();
		String new_pid = wmsProductCategories.getParentId();
		if(!old_pid.equals(new_pid)) {
			updateOldParentNode(old_pid);
			if(oConvertUtils.isEmpty(new_pid)){
				wmsProductCategories.setParentId(IWmsProductCategoriesService.ROOT_PID_VALUE);
			}
			if(!IWmsProductCategoriesService.ROOT_PID_VALUE.equals(wmsProductCategories.getParentId())) {
				baseMapper.updateTreeNodeStatus(wmsProductCategories.getParentId(), IWmsProductCategoriesService.HASCHILD);
			}
		}
		baseMapper.updateById(wmsProductCategories);
	}
	
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void deleteWmsProductCategories(String id) throws JeecgBootException {
		//查询选中节点下所有子节点一并删除
        id = this.queryTreeChildIds(id);
        if(id.indexOf(",")>0) {
            StringBuffer sb = new StringBuffer();
            String[] idArr = id.split(",");
            for (String idVal : idArr) {
                if(idVal != null){
                    WmsProductCategories wmsProductCategories = this.getById(idVal);
                    String pidVal = wmsProductCategories.getParentId();
                    //查询此节点上一级是否还有其他子节点
                    List<WmsProductCategories> dataList = baseMapper.selectList(new QueryWrapper<WmsProductCategories>().eq("parent_id", pidVal).notIn("id",Arrays.asList(idArr)));
                    boolean flag = (dataList == null || dataList.size() == 0) && !Arrays.asList(idArr).contains(pidVal) && !sb.toString().contains(pidVal);
                    if(flag){
                        //如果当前节点原本有子节点 现在木有了，更新状态
                        sb.append(pidVal).append(",");
                    }
                }
            }
            //批量删除节点
            baseMapper.deleteBatchIds(Arrays.asList(idArr));
            //修改已无子节点的标识
            String[] pidArr = sb.toString().split(",");
            for(String pid : pidArr){
                this.updateOldParentNode(pid);
            }
        }else{
            WmsProductCategories wmsProductCategories = this.getById(id);
            if(wmsProductCategories==null) {
                throw new JeecgBootException("未找到对应实体");
            }
            updateOldParentNode(wmsProductCategories.getParentId());
            baseMapper.deleteById(id);
        }
	}
	
	@Override
    public List<WmsProductCategories> queryTreeListNoPage(QueryWrapper<WmsProductCategories> queryWrapper) {
        List<WmsProductCategories> dataList = baseMapper.selectList(queryWrapper);
        List<WmsProductCategories> mapList = new ArrayList<>();
        for(WmsProductCategories data : dataList){
            String pidVal = data.getParentId();
            //递归查询子节点的根节点
            if(pidVal != null && !IWmsProductCategoriesService.NOCHILD.equals(pidVal)){
                WmsProductCategories rootVal = this.getTreeRoot(pidVal);
                if(rootVal != null && !mapList.contains(rootVal)){
                    mapList.add(rootVal);
                }
            }else{
                if(!mapList.contains(data)){
                    mapList.add(data);
                }
            }
        }
        return mapList;
    }

    @Override
    public List<SelectTreeModel> queryListByCode(String parentCode) {
        String pid = ROOT_PID_VALUE;
        if (oConvertUtils.isNotEmpty(parentCode)) {
            LambdaQueryWrapper<WmsProductCategories> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(WmsProductCategories::getParentId, parentCode);
            List<WmsProductCategories> list = baseMapper.selectList(queryWrapper);
            if (list == null || list.size() == 0) {
                throw new JeecgBootException("该编码【" + parentCode + "】不存在，请核实!");
            }
            if (list.size() > 1) {
                throw new JeecgBootException("该编码【" + parentCode + "】存在多个，请核实!");
            }
            pid = list.get(0).getId();
        }
        return baseMapper.queryListByPid(pid, null);
    }

    @Override
    public List<SelectTreeModel> queryListByPid(String pid) {
        if (oConvertUtils.isEmpty(pid)) {
            pid = ROOT_PID_VALUE;
        }
        return baseMapper.queryListByPid(pid, null);
    }

	/**
	 * 根据所传pid查询旧的父级节点的子节点并修改相应状态值
	 * @param pid
	 */
	private void updateOldParentNode(String pid) {
		if(!IWmsProductCategoriesService.ROOT_PID_VALUE.equals(pid)) {
			Long count = baseMapper.selectCount(new QueryWrapper<WmsProductCategories>().eq("parent_id", pid));
			if(count==null || count<=1) {
				baseMapper.updateTreeNodeStatus(pid, IWmsProductCategoriesService.NOCHILD);
			}
		}
	}

	/**
     * 递归查询节点的根节点
     * @param pidVal
     * @return
     */
    private WmsProductCategories getTreeRoot(String pidVal){
        WmsProductCategories data =  baseMapper.selectById(pidVal);
        if(data != null && !IWmsProductCategoriesService.ROOT_PID_VALUE.equals(data.getParentId())){
            return this.getTreeRoot(data.getParentId());
        }else{
            return data;
        }
    }

    /**
     * 根据id查询所有子节点id
     * @param ids
     * @return
     */
    private String queryTreeChildIds(String ids) {
        //获取id数组
        String[] idArr = ids.split(",");
        StringBuffer sb = new StringBuffer();
        for (String pidVal : idArr) {
            if(pidVal != null){
                if(!sb.toString().contains(pidVal)){
                    if(sb.toString().length() > 0){
                        sb.append(",");
                    }
                    sb.append(pidVal);
                    this.getTreeChildIds(pidVal,sb);
                }
            }
        }
        return sb.toString();
    }

    /**
     * 递归查询所有子节点
     * @param pidVal
     * @param sb
     * @return
     */
    private StringBuffer getTreeChildIds(String pidVal,StringBuffer sb){
        List<WmsProductCategories> dataList = baseMapper.selectList(new QueryWrapper<WmsProductCategories>().eq("parent_id", pidVal));
        if(dataList != null && dataList.size()>0){
            for(WmsProductCategories tree : dataList) {
                if(!sb.toString().contains(tree.getId())){
                    sb.append(",").append(tree.getId());
                }
                this.getTreeChildIds(tree.getId(),sb);
            }
        }
        return sb;
    }

    /**
     * 生成分类编码
     * 第一级分类为两位编号（01、02、03...）
     * 第二级分类为一级分类编码+二级分类编码(2位)
     * 依次类推
     *
     * @param parentId 父节点ID
     * @return 生成的分类编码
     */
    @Override
    public String generateCategoryCode(String parentId) {
        // 如果是根节点（一级分类）
        if (IWmsProductCategoriesService.ROOT_PID_VALUE.equals(parentId)) {
            // 查询所有一级分类的最大编码
            String maxCode = this.selectMaxCategoryCodeByParentId(parentId);
            // 生成下一个两位编码
            return CategoryCodeGenerator.generateNextTwoDigitCode(maxCode);
        } else {
            // 获取父节点的编码
            WmsProductCategories parent = baseMapper.selectById(parentId);
            if (parent == null) {
                throw new JeecgBootException("父节点不存在");
            }
            String parentCode = parent.getCategoryCode();
            if (parentCode == null || parentCode.isEmpty()) {
                throw new JeecgBootException("父节点编码为空");
            }
            // 查询同级分类的最大编码
            String maxCode = this.selectMaxCategoryCodeByParentId(parentId);
            String suffix;
            if (maxCode == null || maxCode.isEmpty()) {
                suffix = "01";
            } else {
                // 从最大编码中提取后两位
                suffix = maxCode.substring(maxCode.length() - 2);
                // 生成下一个两位编码
                suffix = CategoryCodeGenerator.generateNextTwoDigitCode(suffix);
            }
            // 返回父节点编码+两位后缀
            return parentCode + suffix;
        }
    }

    String selectMaxCategoryCodeByParentId(String parentId) {
        String maxCode = baseMapper.selectOne(new QueryWrapper<WmsProductCategories>()
                .eq("parent_id", parentId)
                .orderByDesc("category_code")
                .last("limit 1")).getCategoryCode();

        return maxCode;
    }
}
