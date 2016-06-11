package com.zuqiukong.automaticpacking.action;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.zuqiukong.automaticpacking.Constants;
import com.zuqiukong.automaticpacking.pojo.ChannelPojo;
import com.zuqiukong.automaticpacking.pojo.ResponseBasePojo;
import com.zuqiukong.automaticpacking.taskheadler.ChannelTaskBeta;
@WebServlet("/updatebeta")
public class UpdateBetaAction extends HttpServlet
{
	private Gson gson = new Gson();
	/**
	 * 
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
	{
		response.setHeader("content-type","text/html;charset=UTF-8");
		String channel_name = request.getParameter("reqCode");
		
		/*
		 *  ret_code 1 正在打包 0 无任何更新
		 */
		ResponseBasePojo<ChannelPojo> responsePojo = new ResponseBasePojo<ChannelPojo>();
		if(null == channel_name || "".equals(channel_name))
		{
			responsePojo.ret_code = Constants.IsPackingBeta?1:0;
			
		}
		else
		{
			if(Constants.IsPackingBeta )
			{
				responsePojo.ret_code = 1;
			}
			else
			{
				if(checkNewcode())
				{
					responsePojo.ret_code = 1;
					Constants.IsPackingBeta = true;
					new Thread(new Runnable() 
					{
						@Override
						public void run() 
						{
							new ChannelTaskBeta().doWrok();
						}
					}).start();
				}
				else
				{
					responsePojo.ret_code = 0;
				}
			}
		}
		responsePojo.ret_msg = getWhatNew();
		response.getWriter().append(gson.toJson(responsePojo));
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}
	
	
	
	
	private boolean checkNewcode()
	{
		String[] cmdCheckoutSource = {"git","-C",Constants.PROJECT_PATH_BETA,"checkout","-f"};
		String[] cmdUpdateSource = {"git","-C",Constants.PROJECT_PATH_BETA ,"pull",Constants.PROJECT_GIT_REMOTE,Constants.PROJECT_GIT_BRANCH_BETA};  
		boolean hasNew = true;
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
				if("Already up-to-date".indexOf(line) >= 0)
				{
					hasNew = false;
					break;
				}
			}  
			//Fast-forward
			read.close();
			in.close();
			pro.destroy();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  
		
		return hasNew;
	}
	private String getWhatNew()
	{
		/*
		 * 查看最近10次提交中，带‘betaMag:’字样的提交说明信息
		 * git log -10 --pretty=format:"%s" | grep 'betaMsg:'
		 */
		String[] cmdLog = {"git","-C",Constants.PROJECT_PATH_BETA,"log","-10","--pretty=format:'%s'","--grep","betaMsg:"};
		StringBuilder whatNews = new StringBuilder();
		Process pro=null;
        try 
        {
        	pro = Runtime.getRuntime().exec(cmdLog);  
			pro.waitFor();
			InputStream in = pro.getInputStream();  
			BufferedReader read = new BufferedReader(new InputStreamReader(in));
			String line = null;  
			while((line = read.readLine())!=null)
			{  
				System.out.println(line);  
				whatNews.append(line+"<br>");
			}  
			pro.destroy();
			read.close();
			in.close();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  
        catch(Exception e)
        {
        	e.printStackTrace();
        }
        finally {
        	if(null != pro)
        		pro.destroy();
		}
        System.out.println("5");
		return whatNews.toString();
		
	}
}