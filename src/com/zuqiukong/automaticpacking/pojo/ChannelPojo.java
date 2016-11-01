package com.zuqiukong.automaticpacking.pojo;

import java.sql.Timestamp;
/**
 * 渠道表对象
 * @author jie
 *
 */
public class ChannelPojo 
{
	/**
	 * 状态字典
	 */
	public static final int STATUS_PENDING = 0; //待处理
	public static final int STATUS_PROCESSING = 1; //正在打包
	public static final int STATUS_SUCCESS = 2; //完成
	public static final int STATUS_FAILED = 3; //失败
	
	public String id; 
	public String channel_name;
	public String version;
	public Timestamp create_date;
	public Timestamp update_date;
	public int status;

}
