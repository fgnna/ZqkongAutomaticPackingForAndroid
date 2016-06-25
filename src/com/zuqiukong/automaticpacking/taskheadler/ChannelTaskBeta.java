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
        	Process pro = Runtime.getRuntime().exec(cmdCheckoutSource);  
			pro.waitFor();
			
			InputStream in = pro.getInputStream();  
			BufferedReader read = new BufferedReader(new InputStreamReader(in,"UTF-8"));  
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
			read = new BufferedReader(new InputStreamReader(in,"UTF-8"));  
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
			in.close();
			BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(new File(Constants.PROJECT_GRADLE_BETA_PATH))) ;
			String newBuild = contentString.toString()
					.replaceAll(Constants.Gradle_Profiles_Regex,Constants.Gradle_Profiles_Text.replace("<channelName>", channelName).replace("<channelNameUpcase>",channelName.toUpperCase() ))
					.replace("defaultConfig.versionName", "\"" + version+"\"");
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

		
		String[] cmdClean = {Constants.PROJECT_PATH_BETA +"/gradlew","-p",Constants.PROJECT_PATH_BETA ,"clean"};
	
		//打包
		String[] cmdDebugPacking = {Constants.PROJECT_PATH_BETA +"/gradlew","-p",Constants.PROJECT_PATH_BETA ,"assemble"+channelName+"Debug"};
		String[] cmdReleasePacking = {Constants.PROJECT_PATH_BETA +"/gradlew","-p",Constants.PROJECT_PATH_BETA ,"assemble"+channelName+"Release"};
        try 
        {
        	System.out.println("执行清除");
        	Process pro = Runtime.getRuntime().exec(cmdClean);  
        	System.out.println("执行清除pro.waitFor()");
			pro.waitFor();
			System.out.println("执行清除pro.destroy");
			pro.destroy();
			System.out.println("清除完毕，开始打包");
        	pro = Runtime.getRuntime().exec(cmdDebugPacking );
        	pro.waitFor();
        	
			InputStream in = pro.getInputStream();  
			BufferedReader read = new BufferedReader(new InputStreamReader(in,"UTF-8"));  			
		
			String line = null;
			boolean buildSuccessful = false;
			while((line = read.readLine())!=null)
			{  
				System.out.println(line);
				if(line.indexOf("BUILD SUCCESSFUL")!= -1)
				{
					buildSuccessful = true;
				}
			}  
			
			if(!buildSuccessful)
			{
				read.close();
				in.close();
				in = pro.getErrorStream();  
				read = new BufferedReader(new InputStreamReader(in,"UTF-8"));  
				line = null;  
				while((line = read.readLine())!=null)
				{  
					System.out.println(line);
				}  
			}
			
			//Fast-forward
			read.close();
			in.close();
			pro.destroy();
			pro = Runtime.getRuntime().exec(cmdReleasePacking);  
			pro.waitFor();
			pro.destroy();
			
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
			pro.destroy();
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
		String apkName = "/zuqiukong_"+channelName+"_debug_"+version+".apk";
		String apkPackgaPath = Constants.PROJECT_PATH_BETA + Constants.APK_PATH + apkName;
		
		String apkReleaseName = "/zuqiukong_"+channelName+"_release_"+version+".apk";
		String apkReleasePackgaPath = Constants.PROJECT_PATH_BETA + Constants.APK_PATH + apkReleaseName;

		
		String[] cmdCopyApk = {"cp",apkPackgaPath,Constants.WebPath};
		String[] cmdCopyReleaseApk = {"cp",apkReleasePackgaPath,Constants.WebPath};
        try 
        {
        	Process pro = Runtime.getRuntime().exec(cmdCopyApk);  
			pro.waitFor();
			pro.destroy();
			pro = Runtime.getRuntime().exec(cmdCopyReleaseApk);  
			pro.waitFor();
			pro.destroy();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
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
