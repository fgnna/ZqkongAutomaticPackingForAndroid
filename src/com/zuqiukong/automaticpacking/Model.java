package com.zuqiukong.automaticpacking;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
/**
 * 处理数据库操作
 * @author jie
 *
 */
public class Model 
{
	private static Model model;
	private static Connection conn;
	/**
	 * 数据库实例名
	 */
	private static final String DB_NAME= "zqk.db";
	
	private Model(){}
	public static synchronized Model  getInstance()
	{
		if(null == model)
		{
			//连接SQLite的JDBC
	         try {
				Class.forName("org.sqlite.JDBC");
				//建立一个数据库名zieckey.db的连接，如果不存在就在当前目录下创建之
				 conn = DriverManager.getConnection("jdbc:sqlite:"+DB_NAME);
				 createTable();
				 System.out.println("create Connection success!");
				 model = new Model();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				System.out.println("error:create Connection Failed!");
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				System.out.println("error:create Connection Failed!,请确定你的数据库已经安装及已经创建实例名为["+DB_NAME+"]的库:"+e.getMessage());
			}
		}
		return model;
	}
	
	/**
	 * 创建记录表
	 * @throws SQLException 
	 */
	private  static void createTable() throws SQLException
	{
		try 
		{
			Statement statement = conn.createStatement();
			//statement.executeUpdate("     drop table zqk_channel    ");
			statement.executeUpdate("     create table zqk_channel(  id  TEXT primary key , "
					+ "channel_name   TEXT  not null,  "
					+ "version   TEXT  not null,  "
					+ "create_date       DATETIME  not null,"
					+ "update_date    DATETIME  not null,"
					+ "status   INTEGER  not null)    ");
		}
		catch (SQLException e) 
		{
			if( -1 == e.getMessage().indexOf("table zqk_channel already exists"))
			{
				e.printStackTrace();
				System.out.println("error:创建表失败:"+e.getMessage());
				throw e;
			}
			System.out.println("success:channel表已经存在");
		}
		System.out.println("success:创建渠道记录表完成");
	}
	
	/**
	 * 新增一个任务
	 * @param channel_name
	 * @param version
	 * @return
	 */
	public boolean insert(String channel_name,String version)
	{
		try 
		{
			ChannelPojo channel = new ChannelPojo();
			channel.id = UUID.randomUUID().toString();
			channel.channel_name = channel_name;
			channel.version = version;
			channel.status = ChannelPojo.STATUS_PENDING;
			long time = System.currentTimeMillis();
			
			if(!queryIsExist(channel_name,version))
			{
				Statement statement = conn.createStatement();
				//statement.executeUpdate("     drop table zqk_channel    ");
				statement.executeUpdate(" insert into zqk_channel values("+getStringValue(channel.id)+","+getStringValue(channel.channel_name)+","+getStringValue(channel.version)+","+time+","+time+","+channel.status+");      ");
				return true;
			}
			else
			{
				System.out.println("数据已经存在:"+channel_name);
				return false;
			}
			
		}
		catch (SQLException e) 
		{
			e.printStackTrace();
			System.out.println("error:"+e.getMessage());
		}
		
		
		return false;
	}

	/**
	 * 查询是否已经存在相同的打包任务 
	 * @param channel_name
	 * @param version
	 * @return
	 * @throws SQLException 
	 */
	public boolean queryIsExist(String channel_name,String version) throws SQLException
	{
		ResultSet resultSet = null;
		try 
		{
			Statement statement = conn.createStatement();
			resultSet  = statement .executeQuery("select * from zqk_channel where channel_name = '"+channel_name+"' and version = '"+version+"';"); //查询数据 
			if(resultSet.next())
			{
				return true;
			}
			else
			{
				return false;
			}
			
		}
		finally 
		{
			if(null != resultSet )
			{
				try {
					resultSet.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	
	/**
	 * 查询所有任务 
	 * @return
	 * @throws SQLException 
	 */
	public List<ChannelPojo> queryAll()
	{
		ResultSet resultSet = null;
		try 
		{
			List<ChannelPojo> channelList = new LinkedList<>();
			
			Statement statement = conn.createStatement();
			resultSet  = statement .executeQuery("select * from zqk_channel order by update_date ;"); //查询数据 
			while(resultSet.next())
			{
				ChannelPojo channelt = new ChannelPojo();
				channelt.id = resultSet.getString(1);
				channelt.channel_name = resultSet.getString(2);
				channelt.version = resultSet.getString(3);
				channelt.create_date =  resultSet.getTimestamp(4);
				channelt.update_date = resultSet.getTimestamp(5);
				channelt.status = resultSet.getInt(6);
				channelList.add(channelt);
			}
			if(0 != channelList.size())
			{
				return channelList;
			}
			else
			{
				return null;
			}
		}
		catch (SQLException e) 
		{
			e.printStackTrace();
			System.out.println("error:查询失败"+e.getMessage());
			return null;
		}
		finally 
		{
			if(null != resultSet )
			{
				try {
					resultSet.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	
	/**
	 * 根据id获取
	 * @param channelId
	 */
	public void getChannelById(String channelId) {
		// TODO Auto-generated method stub
		
	}
	
	private String getStringValue(String value)
	{
		return "'"+value+"'";
	}
	
}
