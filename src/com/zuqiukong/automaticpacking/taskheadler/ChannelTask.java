package com.zuqiukong.automaticpacking.taskheadler;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
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
			
			InputStream in = pro.getInputStream();  
			BufferedReader read = new BufferedReader(new InputStreamReader(in));  
			String line = null;  
			while((line = read.readLine())!=null)
			{  
				System.out.println(line);  
			}  
			read.close();
			in.close();
			pro.destroy();
			
			pro = Runtime.getRuntime().exec(cmdUpdateSource);
			pro.waitFor();
			in = pro.getInputStream();  
			read = new BufferedReader(new InputStreamReader(in));  
			line = null;  
			while((line = read.readLine())!=null)
			{  
				System.out.println(line);  
			}  
			//Fast-forward
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
				contentString.append(line).append("\n");
			}
			read.close();
			in.close();
			BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(new File(Constants.PROJECT_GRADLE_PATH))) ;
			bufferedWriter.write(contentString.toString().replaceAll(Constants.Gradle_Profiles_Regex,Constants.Gradle_Profiles_Text.replaceAll("<channelName>", channelName)));
			bufferedWriter.flush();
			bufferedWriter.close();
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
		
		//./gradlew clean
		// ./gradlew assemble<channelName>Release
		//打包前清除
		String[] cmdClean = {Constants.PROJECT_PATH +"/gradlew","-p",Constants.PROJECT_PATH ,"clean"};
		//打包
		String[] cmdPacking = {Constants.PROJECT_PATH +"/gradlew","-p",Constants.PROJECT_PATH ,"assemble"+channelName+"Release"};
        try 
        {
        	Process pro = Runtime.getRuntime().exec(cmdClean);  
			pro.waitFor();
			
			InputStream in = pro.getInputStream();  
			BufferedReader read = new BufferedReader(new InputStreamReader(in));  
			read.close();
			in.close();
			pro.destroy();
			
			String line = null;  
			pro = Runtime.getRuntime().exec(cmdPacking );
			pro.waitFor();
			in = pro.getInputStream();  
			read = new BufferedReader(new InputStreamReader(in));  
			line = null;  
			boolean buildSuccessful = false;
			while((line = read.readLine())!=null)
			{  
				System.out.println(line);
				if(line.indexOf("BUILD SUCCESSFUL")!= -1)
				{
					buildSuccessful = true;
				}
			}  
			//Fast-forward
			read.close();
			in.close();
			
			if(buildSuccessful)
			{
				System.out.println("打包完成");
			}
			else
			{
				System.out.println("打包失败");
			}
			
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  
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
