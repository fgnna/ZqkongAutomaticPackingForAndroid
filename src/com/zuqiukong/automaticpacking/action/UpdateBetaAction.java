package com.zuqiukong.automaticpacking.action;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.logging.Logger;

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

import sun.rmi.runtime.Log;
@WebServlet("/updatebeta")
public class UpdateBetaAction extends HttpServlet
{
	private Gson gson = new Gson();
	/*
	 *  ret_code 1 正在打包 0 无任何更新 -1 异常
	 */
	ResponseBasePojo<ChannelPojo> responsePojo ;
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
	{
		response.setHeader("content-type","text/html;charset=UTF-8");
		response.setCharacterEncoding("UTF-8");
		String channel_name = request.getParameter("reqCode");
		
		
		responsePojo = new ResponseBasePojo<ChannelPojo>();
		responsePojo.ret_code = Constants.IsPackingBeta;
		responsePojo.ret_msg = getWhatNew();
		responsePojo.ret_error_msg = Constants.packingBetaErrorMsg;
		response.getWriter().append(gson.toJson(responsePojo));
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}
	
	private String getWhatNew()
	{
		/*
		 * 查看最近10次提交中，带‘betaMag:’字样的提交说明信息
		 * git log -10 --pretty=format:"%s" | grep 'betaMsg:'
		 */
		String[] cmdLog = {"git","-C",Constants.PROJECT_PATH_BETA,"log","-20","--date=format:%m-%d %H:%M","--pretty=format:%cd | %cn : %s","--grep","betaMsg:"};
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
		return whatNews.toString().replace("betaMsg:", "");
		
	}
}