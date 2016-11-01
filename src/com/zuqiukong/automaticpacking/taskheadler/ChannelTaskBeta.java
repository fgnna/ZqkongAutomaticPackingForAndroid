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
 * Beta版的任务逻辑处理器
 * @author jie
 *
 */
public class ChannelTaskBeta 
{	
	
	private String channelName = "beta";
	private String version = "beta";
	protected boolean buildSuccessful = false;
	
	public ChannelTaskBeta()
	{

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
		String[] cmdCheckoutSource = {"git","-C",Constants.PROJECT_PATH_BETA ,"checkout","-f"};
		String[] cmdUpdateSource = {"git","-C",Constants.PROJECT_PATH_BETA ,"pull",Constants.PROJECT_GIT_REMOTE,Constants.PROJECT_GIT_BRANCH_BETA};  
        try 
        {
        	ProcessUtils.exec(cmdCheckoutSource,"还原beta源码",null);
        	ProcessUtils.exec(cmdUpdateSource,"更新beta到最新版本",null);
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
		InputStream in = null;
		OutputStream out = null ;
		try
		{
			in = new FileInputStream(Constants.PROJECT_GRADLE_BETA_PATH);
			BufferedReader read = new BufferedReader(new InputStreamReader(in,"UTF-8"));
			
			StringBuilder contentString = new StringBuilder();
			String line = "";
			int lineCount = 0;
			while(( line = read.readLine() ) != null )
			{  
				contentString.append(line).append("\n");
			}
			read.close();
			read = null;
			in.close();
			in = null;
			
			BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(new File(Constants.PROJECT_GRADLE_BETA_PATH))) ;
			String newBuild = contentString.toString()
					.replaceAll(Constants.Gradle_Profiles_Regex,Constants.Gradle_Profiles_Text_Beta.replace("<channelName>", channelName).replace("<channelNameUpcase>",channelName.toUpperCase() ))
					.replace("defaultConfig.versionName", "\"" + version+"\"");
			Constants.log("配置beta打包设置："+newBuild);
			bufferedWriter.write(newBuild);
			bufferedWriter.flush();
			bufferedWriter.close();
		}catch (Exception e)
		{
			e.printStackTrace();
		}
		finally {
			try
			{
				if(null != in)
					in.close();
				if(null != out)
					out.close();
			}catch(Exception e)
			{
				
			}
	
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

		
		String[] cmdClean = {Constants.PROJECT_PATH_BETA +"/gradlew","-p",Constants.PROJECT_PATH_BETA ,"clean"};
	
		//打包
		String[] cmdDebugPacking = {Constants.PROJECT_PATH_BETA +"/gradlew","-p",Constants.PROJECT_PATH_BETA ,"assemble"+channelName+"Debug"};
		String[] cmdReleasePacking = {Constants.PROJECT_PATH_BETA +"/gradlew","-p",Constants.PROJECT_PATH_BETA ,"assemble"+channelName+"Release"};
        try 
        {
        	ProcessUtils.exec(cmdClean,"打包前清除clean()",null);
        	ProcessUtils.exec(cmdDebugPacking,"beta打包Debug版",null);
        	ProcessUtils.exec(cmdReleasePacking,"beta打包Release版",new LineMsgHandle() 
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
				putFile();
				Constants.IsPackingBeta = 0;
				Constants.log("打包完成");
			}
			else
			{
				Constants.IsPackingBeta = -1;
				Constants.log("打包失败");
				Constants.packingBetaErrorMsg = "打包失败";
				
			}
		}  catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Constants.IsPackingBeta = -1;
			Constants.log("打包失败");
			Constants.packingBetaErrorMsg = "打包失败";
		}  
	}
	
	/**
	 * 把打包文件放置到下载目录
	 */
	private void putFile()
	{
		String apkName = "/zuqiukong_"+channelName+"_debug_"+version+".apk";
		String apkPackgaPath = Constants.PROJECT_PATH_BETA + Constants.APK_PATH + apkName;
		
		String apkReleaseName = "/zuqiukong_"+channelName+"_release_"+version+".apk";
		String apkReleasePackgaPath = Constants.PROJECT_PATH_BETA + Constants.APK_PATH + apkReleaseName;

		
		String[] cmdCopyApk = {"cp",apkPackgaPath,Constants.WebPath};
		String[] cmdCopyReleaseApk = {"cp",apkReleasePackgaPath,Constants.WebPath};
        try 
        {
        	
        	ProcessUtils.exec(cmdCopyApk,"拷贝beta Debug apk到web目录",null);
        	ProcessUtils.exec(cmdCopyReleaseApk,"拷贝beta ReleaseApk apk到web目录",null);
		
		} 
        catch (Exception e) 
        {
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
