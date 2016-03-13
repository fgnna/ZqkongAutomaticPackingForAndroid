package com.zuqiukong.automaticpacking;
/**
 * 项目常量配置
 * @author jie
 *
 */
public interface Constants 
{
	//项目打包的远程仓库名
	final String PROJECT_GIT_REMOTE= "origin";
	//项目打包的分支
	final String PROJECT_GIT_BRANCH = "master";
	
	//项目源码所在目录
	final String PROJECT_PATH_BASE = "/home/jie/automaticpacking";
	//用于检查最新版本的项目源码所在目录
	final String PROJECT_CHECK_VERSION_PATH = PROJECT_PATH_BASE + "/zuquikong_version";
	//用于检查最新版本号的源码文件路径
	final String PROJECT_CHECK_VERSION_BUILD_GRADLE_PATH = PROJECT_CHECK_VERSION_PATH + "/app/build.gradle";
	
	//用于打包的项目源码所在目录
	final String PROJECT_PATH = PROJECT_PATH_BASE + "/zuquikong";

	//用于编辑打包配置文件
	final String PROJECT_GRADLE_PATH = PROJECT_PATH + "/app/build.gradle";

	//匹配打包时所需替换的包名字符的正则表达式：productFlavors {......}
	//final String Gradle_Profiles_Regex =  "productFlavors\\s*\\{{1}([\\w\\s/\\\\*]*\\{{1}[\\w\\s=\\[\\]\\\":]*\\}{1})*[\\s\\*/]*\\}{1}";
	final String Gradle_Profiles_Regex =  "productFlavors\\s*\\{{1}([\\w\\s/\\\\\\*]*\\{{1}[\\w\\s=\\[\\]\\\":]*\\}{1})*[\\s\\*/]*\\}{1}";
	//打包配置的替换文本格式 : 请替换 <channelName>为正确的渠道标识
	final String Gradle_Profiles_Text =  "productFlavors{ \n <channelName> { \n manifestPlaceholders = [ CHANNEL_NAME:\"<channelName>\" ] \n } \n }";
}
