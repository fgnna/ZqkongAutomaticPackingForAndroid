package com.zuqiukong.automaticpacking.listener;


import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Timer;
import java.util.TimerTask;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import com.zuqiukong.automaticpacking.Constants;
import com.zuqiukong.automaticpacking.taskheadler.ChannelTaskBeta;
import com.zuqiukong.automaticpacking.taskheadler.QueuesHeadler;

/**
 * 
 * @author jie
 *
 */
public class MyServletContextListener  implements ServletContextListener 
{
	
	private Timer timer = null; 
    public void contextDestroyed(ServletContextEvent event)  
    {  
    	QueuesHeadler.cannel();
        timer.cancel();  
        event.getServletContext().log("定时器销毁");  
        Constants.mServletContext = null;
    }  



	@Override
	public void contextInitialized(ServletContextEvent arg0) 
	{
		Constants.mServletContext = arg0.getServletContext();
		Constants.WebPath = arg0.getServletContext().getRealPath("/")+"apk";
		
		new Thread(new QueuesHeadler()).start();
		
		timer = new Timer(true);  
	    timer.schedule(new MyTask(),1000,60000); 
	}

	public static class MyTask extends TimerTask{  
        @Override  
        public void run() 
        {  
        	Constants.log("执行beta版更新:"+Constants.IsPackingBeta);
        	if(1 != Constants.IsPackingBeta )
			{
				int code = checkNewcode();
				if(1 == code)
				{
					Constants.IsPackingBeta = 1;
					new Thread(new Runnable() 
					{
						@Override
						public void run() 
						{
							new ChannelTaskBeta().doWrok();
						}
					}).start();
				}
				else if(2 == code)
				{
					Constants.IsPackingBeta = 0;
				}
				else
				{
					Constants.IsPackingBeta = -1;
				}
			}
        }  
        
        
        /**
    	 * 
    	 * @return 1 有更新 2 无更新 3 查询失败
    	 */
    	private int checkNewcode()
    	{
    		String[] cmdCheckoutSource = {"git","-C",Constants.PROJECT_PATH_BETA,"checkout","-f"};
    		String[] cmdUpdateSource = {"git","-C",Constants.PROJECT_PATH_BETA ,"pull",Constants.PROJECT_GIT_REMOTE,Constants.PROJECT_GIT_BRANCH_BETA};  
    		int hasNew = 1;
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
    				if(line.indexOf("Already up-to-date") >= 0)
    				{
    					hasNew = 2;
    					break;
    				}
    			}  
    			//Fast-forward
    			read.close();
    			in.close();
    			
    						
    			in = pro.getErrorStream();  
    			read = new BufferedReader(new InputStreamReader(in));  
    			line = null;  
    			while((line = read.readLine())!=null)
    			{  
    			
    				if(line.indexOf("Could not resolve host") >=0)
    				{
    					Constants.log("拉取新代码失败"+line);
    					Constants.packingBetaErrorMsg = "拉取新代码失败"+line;
    					hasNew = 3;
    					break;
    				}
    			}  
    			
    			read.close();
    			in.close();
    			pro.destroy();
    		} catch (Exception e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		}  
    		
    		return hasNew;
    	}
    } 
	
}
