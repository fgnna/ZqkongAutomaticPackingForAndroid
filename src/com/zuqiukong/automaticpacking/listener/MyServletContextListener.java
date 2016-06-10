package com.zuqiukong.automaticpacking.listener;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import com.zuqiukong.automaticpacking.Constants;
import com.zuqiukong.automaticpacking.taskheadler.QueuesHeadler;

/**
 * 
 * @author jie
 *
 */
public class MyServletContextListener  implements ServletContextListener 
{

	@Override
	public void contextDestroyed(ServletContextEvent arg0)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void contextInitialized(ServletContextEvent arg0) 
	{
		Constants.WebPath = arg0.getServletContext().getRealPath("/")+"apk";
		new Thread(new QueuesHeadler()).start();
		
		
	}

}
