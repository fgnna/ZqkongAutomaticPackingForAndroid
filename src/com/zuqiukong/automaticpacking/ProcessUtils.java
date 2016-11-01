package com.zuqiukong.automaticpacking;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.TimeUnit;

import com.sun.istack.internal.Nullable;

public class ProcessUtils 
{
	public static void exec(String[] cmds,String actionName,@Nullable LineMsgHandle lineMsgHandle) throws Exception
	{
		Constants.log("开始执行进程命令："+actionName+"************************************************");
		InputStream in = null;
		BufferedReader read = null;
		Process pro = null;
		try 
		{
			pro = Runtime.getRuntime().exec(cmds);
			final InputStream inError = pro.getErrorStream();
			new Thread(new Runnable(){
				
				public void run()
				{
					
					BufferedReader errorRead = new BufferedReader(new InputStreamReader(inError));
					String line = null;
					try {
						while ((line = errorRead.readLine()) != null) {
						}
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}).start();
			
			in = pro.getInputStream();
			read = new BufferedReader(new InputStreamReader(in));
			String line = null;
			while ((line = read.readLine()) != null) {
				Constants.log("消息："+line);
				if(null != lineMsgHandle)
				{
					lineMsgHandle.handleLine(line);
				}
			}
			/*
			if(pro.waitFor(4,TimeUnit.MINUTES ))//设置10分钟超时
			{
				in = pro.getInputStream();
				read = new BufferedReader(new InputStreamReader(in));
				String line = null;
				Constants.log("read.readLine()："); 
				while ((line = read.readLine()) != null) {
					System.out.println("消息："+line);
					if(null != lineMsgHandle)
					{
						lineMsgHandle.handleLine(line);
					}
				}
			}
			else
			{
				System.out.println("进程命令：超时");
			}
			*/
			
			
		}
		finally {
			
			if(null != read)
				read.close();
			if(null != in)
				in.close();
			if(null != pro)
				pro.destroyForcibly();
			
			Constants.log("结束执行进程命令："+actionName+"///////////////////////////////////////////////////////////");
		}
		
	}
	
	public interface LineMsgHandle
	{
		void handleLine(String line);
	}
}
