package com.yrt.framework.web.page;

import java.io.Serializable;
import java.util.List;

import io.swagger.annotations.ApiModelProperty;

/**
 * 表格分页数据对象
 * 
 * @author Runner
 */
public class TableDataInfo<T> implements Serializable
{
    private static final long serialVersionUID = 1L;
    /** 总记录数 */
    @ApiModelProperty(value = "总记录数",required = true)
    private long total;
    /** 列表数据 */
    @ApiModelProperty(value = "列表数据",required = true)
    private List<T> rows;
    /** 消息状态码 */
    @ApiModelProperty(value = "消息状态码 0-成功 1-失败 ",required = true)
    private int code;

    /**
     * 表格数据对象
     */
    public TableDataInfo()
    {
    }

    /**
     * 分页
     * 
     * @param list 列表数据
     * @param total 总记录数
     */
    public TableDataInfo(List<T> list, int total)
    {
        this.rows = list;
        this.total = total;
    }

    public long getTotal()
    {
        return total;
    }

    public void setTotal(long total)
    {
        this.total = total;
    }

    public List<T> getRows()
    {
        return rows;
    }

    public void setRows(List<T> rows)
    {
        this.rows = rows;
    }

    public int getCode()
    {
        return code;
    }

    public void setCode(int code)
    {
        this.code = code;
    }
}
