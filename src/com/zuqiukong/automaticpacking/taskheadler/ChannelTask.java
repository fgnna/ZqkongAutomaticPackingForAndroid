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
import com.zuqiukong.automaticpacking.ProcessUtils;
import com.zuqiukong.automaticpacking.ProcessUtils.LineMsgHandle;
import com.zuqiukong.automaticpacking.model.Model;
import com.zuqiukong.automaticpacking.pojo.ChannelPojo;

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
	protected boolean buildSuccessful;
	
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
	        	ProcessUtils.exec(cmdCheckoutSource,"还原master源码",null);
	        	ProcessUtils.exec(cmdUpdateSource,"更新master到最新版本",null);
			} catch (Exception e) {
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
			BufferedReader read = new BufferedReader(new InputStreamReader(in,"UTF-8"));
			
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
			String newBuild = contentString.toString().replaceAll(
					Constants.Gradle_Profiles_Regex,Constants.Gradle_Profiles_Text.replace(
							"<channelName>", "main".equals(channelName)?"_main":channelName).replace("<channelNameUpcase>",channelName.toUpperCase() ));
			System.out.println(newBuild);
			bufferedWriter.write(newBuild);
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
		Model.getInstance().updateStatus(channelId, ChannelPojo.STATUS_PROCESSING);
		
		String[] cmdClean = {Constants.PROJECT_PATH +"/gradlew","-p",Constants.PROJECT_PATH ,"clean"};
		//打包
		String[] cmdPacking = {Constants.PROJECT_PATH +"/gradlew","-p",Constants.PROJECT_PATH ,"assemble"+("main".equals(channelName)?"_main":channelName)+"Release"};
        try 
        {
        	buildSuccessful = false;
        	ProcessUtils.exec(cmdClean,"打包前清除clean()",null);
        	ProcessUtils.exec(cmdPacking,"master打发布包",new LineMsgHandle()
        	{
				@Override
				public void handleLine(String line) 
				{
					if(line.indexOf("BUILD SUCCESSFUL")!= -1)
					{
						buildSuccessful  = true;
					}
				}
			});
        	
			if(buildSuccessful)
			{
				Model.getInstance().updateStatus(channelId, ChannelPojo.STATUS_SUCCESS);
				putFile();
				Constants.log("打包完成");
			}
			else
			{
				Model.getInstance().updateStatus(channelId, ChannelPojo.STATUS_FAILED);
				Constants.log("打包失败");
			}
			
		}  catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  
	}
	
	/**
	 * 把打包文件放置到下载目录
	 */
	private void putFile()
	{
		String apkName = "/zuqiukong_"+("main".equals(channelName)?"_main":channelName)+"_release_"+version+".apk";
		String apkPackgaPath = Constants.PROJECT_PATH + Constants.APK_PATH + apkName;

		String[] cmdCopyApk = {"cp",apkPackgaPath,Constants.WebPath};
        try 
        {
        	ProcessUtils.exec(cmdCopyApk,"拷贝apk到web目录",null);
		}  catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  
	}
	
	/**
	 * 更新数据库
	 */
	private void updateModel()
	{
		
	}
	
}
