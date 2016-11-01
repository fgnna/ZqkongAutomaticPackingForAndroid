package com.zuqiukong.automaticpacking.listener;


import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import com.zuqiukong.automaticpacking.Constants;
import com.zuqiukong.automaticpacking.ProcessUtils;
import com.zuqiukong.automaticpacking.ProcessUtils.LineMsgHandle;
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
		
		initProperties(arg0);
		
		
		new Thread(new QueuesHeadler()).start();
		
		timer = new Timer(true);  
	    timer.schedule(new MyTask(),1000,60000); 
	}

	/**
	 * 读取配置文件，初始化打包环境变量
	 */
	private void initProperties(ServletContextEvent arg1) 
	{
		Properties pps = new Properties();
		try {
			InputStream in = new BufferedInputStream(new FileInputStream(arg1.getServletContext().getRealPath("/")+"WEB-INF/classes/setting.properties"));
			pps.load(in);
			Constants.PROJECT_GIT_REMOTE = pps.getProperty("PROJECT_GIT_REMOTE");
			Constants.PROJECT_GIT_BRANCH = pps.getProperty("PROJECT_GIT_BRANCH");
			Constants.PROJECT_GIT_BRANCH_BETA = pps.getProperty("PROJECT_GIT_BRANCH_BETA");
			
			Constants.PROJECT_PATH_BASE = pps.getProperty("PROJECT_PATH_BASE");
			
			Constants.PROJECT_CHECK_VERSION_PATH = Constants.PROJECT_PATH_BASE+pps.getProperty("PROJECT_CHECK_VERSION_PATH");
			
			Constants.PROJECT_CHECK_VERSION_BUILD_GRADLE_PATH = Constants.PROJECT_CHECK_VERSION_PATH+ pps.getProperty("BUILD_GRADLE_PATH");
			
			Constants.APK_PATH = pps.getProperty("APK_PATH");
			
			Constants.PROJECT_PATH = Constants.PROJECT_PATH_BASE+pps.getProperty("PROJECT_PATH");
			Constants.PROJECT_PATH_BETA = Constants.PROJECT_PATH_BASE+pps.getProperty("PROJECT_PATH_BETA");
			
			Constants.PROJECT_GRADLE_PATH = Constants.PROJECT_PATH+pps.getProperty("BUILD_GRADLE_PATH");
			Constants.PROJECT_GRADLE_BETA_PATH = Constants.PROJECT_PATH_BETA+pps.getProperty("BUILD_GRADLE_PATH");
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static class MyTask extends TimerTask{  
        private int hasNew;


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
    		hasNew = 1;
            try 
            {
            	ProcessUtils.exec(cmdCheckoutSource,"还源beta代码",null);
            	ProcessUtils.exec(cmdUpdateSource,"更新beta代码",new  LineMsgHandle() 
            	{
					@Override
					public void handleLine(String line) {
						if(line.indexOf("Already up-to-date") >= 0)
	    				{
	    					hasNew = 2;
	    				}
						else if(line.indexOf("Could not resolve host") >=0)
	    				{
	    					Constants.log("拉取新代码失败"+line);
	    					Constants.packingBetaErrorMsg = "拉取新代码失败"+line;
	    					hasNew = 3;
	    				}
					}
				});
    						
    		} catch (Exception e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		}  
    		
    		return hasNew;
    	}
    } 
	
}
