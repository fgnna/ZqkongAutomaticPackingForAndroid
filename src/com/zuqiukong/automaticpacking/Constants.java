package com.zuqiukong.automaticpacking;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.servlet.ServletContext;

/**
 * 项目常量配置
 * @author jie
 *
 */
public class Constants 
{
	public static ServletContext mServletContext;  
	
	public static void log(String log)
	{
		if(null != mServletContext)
		{
			mServletContext.log(log);
		}
	}
	
	public static String WebPath = "";
	/**
	 * 0 无更新 
	 * 1 正在打beta包
	 * -1 打包失败 
	 */
	public static int IsPackingBeta = 0;
	public static String packingBetaErrorMsg = null;
	//项目打包的远程仓库名
	public static  String PROJECT_GIT_REMOTE;
	//项目打包的分支
	public static  String PROJECT_GIT_BRANCH;
	//项目BETA包的分支
	public static  String PROJECT_GIT_BRANCH_BETA;
	
	//项目源码所在目录
	public static  String PROJECT_PATH_BASE ;
	//用于检查最新版本的项目源码所在目录
	public static  String PROJECT_CHECK_VERSION_PATH ;
	//用于检查最新版本号的源码文件路径
	public static  String PROJECT_CHECK_VERSION_BUILD_GRADLE_PATH ;
	
	public static  String APK_PATH ;
	
	//用于打包的项目源码所在目录
	public static  String PROJECT_PATH ;
	//用于打包的项目源码所在目录
	public static  String PROJECT_PATH_BETA ;

	//用于编辑打包配置文件
	public static  String PROJECT_GRADLE_PATH ;
	//用于编辑打包配置文件
		public static  String PROJECT_GRADLE_BETA_PATH;

	//匹配打包时所需替换的包名字符的正则表达式：productFlavors {......}
	//final String Gradle_Profiles_Regex =  "productFlavors\\s*\\{{1}([\\w\\s/\\\\*]*\\{{1}[\\w\\s=\\[\\]\\\":]*\\}{1})*[\\s\\*/]*\\}{1}";
	public static  String Gradle_Profiles_Regex =  "productFlavors\\s*\\{{1}([\\w\\s/\\\\\\*]*\\{{1}[/\\w\\s=\\[\\]\\\",:]*\\}{1})*[\\s\\*/]*\\}{1}";
	//打包配置的替换文本格式 : 请替换 <channelName>为正确的渠道标识
	public static  String Gradle_Profiles_Text =  "productFlavors{ \n <channelName> { \n manifestPlaceholders = [ CHANNEL_NAME:\"<channelNameUpcase>\" ] \n } \n }";
	public static  String Gradle_Profiles_Text_Beta =  "productFlavors{  \n <channelName> {\n buildConfigField \"boolean\",\"LOG_DEBUG\",\"true\" \n manifestPlaceholders = [ CHANNEL_NAME:\"<channelNameUpcase>\" ] \n } \n }";
	
	

	
	
	
	
}
