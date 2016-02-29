package com.zuqiukong.automaticpacking.taskheadler;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

import com.zuqiukong.automaticpacking.Constants;

/**
 * 任务逻辑处理器
 * @author jie
 *
 */
public class ChannelTask 
{	
	
	private String channelId;
	private String channelName;
	private String version;
	
	public ChannelTask(String channelId,String channelName,String version)
	{
		this.channelId = channelId;
		this.channelName = channelName;
		this.version = version;
	}
	
	public void doWrok()
	{
		checkoutAndUpdate();
		setChannelName();
		assembleRelease();
		putFile();
		updateModel();
	}
	
	/**
	 * 还原源码并更新到最新版本
	 */
	private void checkoutAndUpdate()
	{
		String[] cmdCheckoutSource = {"git","-C",Constants.PROJECT_PATH ,"checkout","-f"};
		String[] cmdUpdateSource = {"git","-C",Constants.PROJECT_PATH ,"pull",Constants.PROJECT_GIT_REMOTE,Constants.PROJECT_GIT_BRANCH};  
        try 
        {
        	Process pro = Runtime.getRuntime().exec(cmdCheckoutSource);  
			pro.waitFor();
			pro.destroy();
			pro = Runtime.getRuntime().exec(cmdUpdateSource);
			pro.waitFor();
			InputStream in = pro.getInputStream();  
			BufferedReader read = new BufferedReader(new InputStreamReader(in));  
			String line = null;  
			while((line = read.readLine())!=null)
			{  
				System.out.println(line);  
			}  
			//Fast-forward
			//
			
			read.close();
			in.close();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  
        

	}
	
	/**
	 * 设置渠道名称
	 */
	private void setChannelName()
	{
		InputStream in;
		OutputStream out;
		try
		{
			in = new FileInputStream(Constants.PROJECT_GRADLE_PATH);
			BufferedReader read = new BufferedReader(new InputStreamReader(in));
			
			StringBuilder contentString = new StringBuilder();
			String line = "";
			int lineCount = 0;
			while(( line = read.readLine() ) != null )
			{  
				contentString.append(line);
			}
			System.out.println(contentString.toString());
			
			
			read.close();
			in.close();
			
		}catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	/**
	 * 进行打包
	 */
	private void assembleRelease()
	{
		
	}
	
	/**
	 * 把打包文件放置到下载目录
	 */
	private void putFile()
	{
		
	}
	
	/**
	 * 更新数据库
	 */
	private void updateModel()
	{
		
	}
	
}
