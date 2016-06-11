package com.zuqiukong.automaticpacking;
/**
 * 项目常量配置
 * @author jie
 *
 */
public class Constants 
{
	public static String WebPath = "";
	public static boolean IsPackingBeta = false;
	//项目打包的远程仓库名
	public static final String PROJECT_GIT_REMOTE= "auto";
	//项目打包的分支
	public static final String PROJECT_GIT_BRANCH = "master";
	//项目BETA包的分支
	public static final String PROJECT_GIT_BRANCH_BETA= "beta";
	
	//项目源码所在目录
	public static final String PROJECT_PATH_BASE = "/Users/mac/Desktop/develop/work";
	//用于检查最新版本的项目源码所在目录
	public static final String PROJECT_CHECK_VERSION_PATH = PROJECT_PATH_BASE + "/zuquikong_version";
	//用于检查最新版本号的源码文件路径
	public static final String PROJECT_CHECK_VERSION_BUILD_GRADLE_PATH = PROJECT_CHECK_VERSION_PATH + "/app/build.gradle";
	
	public static final String APK_PATH = "/app/build/outputs/apk";
	
	//用于打包的项目源码所在目录
	public static final String PROJECT_PATH = PROJECT_PATH_BASE + "/zuquikong_packing";
	//用于打包的项目源码所在目录
	public static final String PROJECT_PATH_BETA = PROJECT_PATH_BASE + "/zuquikong_beta";

	//用于编辑打包配置文件
	public static final String PROJECT_GRADLE_PATH = PROJECT_PATH + "/app/build.gradle";
	//用于编辑打包配置文件
		public static final String PROJECT_GRADLE_BETA_PATH = PROJECT_PATH_BETA + "/app/build.gradle";

	//匹配打包时所需替换的包名字符的正则表达式：productFlavors {......}
	//final String Gradle_Profiles_Regex =  "productFlavors\\s*\\{{1}([\\w\\s/\\\\*]*\\{{1}[\\w\\s=\\[\\]\\\":]*\\}{1})*[\\s\\*/]*\\}{1}";
	public static final String Gradle_Profiles_Regex =  "productFlavors\\s*\\{{1}([\\w\\s/\\\\\\*]*\\{{1}[/\\w\\s=\\[\\]\\\":]*\\}{1})*[\\s\\*/]*\\}{1}";
	//打包配置的替换文本格式 : 请替换 <channelName>为正确的渠道标识
	public static final String Gradle_Profiles_Text =  "productFlavors{ \n <channelName> { \n manifestPlaceholders = [ CHANNEL_NAME:\"<channelNameUpcase>\" ] \n } \n }";
}
