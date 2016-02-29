package com.zuqiukong.automaticpacking.pojo;
/**
 * 统一的数据返回格式
 * @author jie
 */
public class  ResponseBasePojo<T> 
{
	
	public int ret_code;
	public T data;
	public String ret_msg;
}
