package com.zuqiukong.automaticpacking.action;

import java.io.BufferedReader;
import java.io.FileInputStream;
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
import com.zuqiukong.automaticpacking.model.Model;
import com.zuqiukong.automaticpacking.pojo.ChannelPojo;
import com.zuqiukong.automaticpacking.pojo.ResponseBasePojo;
import com.zuqiukong.automaticpacking.taskheadler.ChannelTask;
import com.zuqiukong.automaticpacking.taskheadler.QueuesHeadler;

/**
 * Servlet implementation class SubimtAutomatic
 */
@WebServlet("/submit")
public class SubmitAutomatic extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
	private static final  String CHANNEL_NAME = "channel"; 
	private Gson gson = new Gson();
    /**
     * @see HttpServlet#HttpServlet()
     */
    public SubmitAutomatic() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
	{
		response.setHeader("content-type","text/html;charset=UTF-8");
		ResponseBasePojo<ChannelPojo> responsePojo = new ResponseBasePojo<ChannelPojo>(); 
		String channel_name = request.getParameter(CHANNEL_NAME);
		if(null == channel_name || "".equals(channel_name))
		{
			responsePojo.ret_code = 0;
			responsePojo.ret_msg = "渠道名不能为空";
			response.getWriter().append(gson.toJson(responsePojo));
		}
		else
		{
			
			String versionName = getVersionName();
			
			if(null == versionName)
			{
				responsePojo.ret_code = 0;
				responsePojo.ret_msg = "提交失败，无法查询到版本号";
				response.getWriter().append(gson.toJson(responsePojo));
				return ;
			}
			
			ChannelPojo channelPojo = Model.getInstance().insert(channel_name, versionName); 
			if(null != channelPojo)
			{
				responsePojo.ret_code = 1;
				responsePojo.ret_msg = "";
				QueuesHeadler.addTask(new ChannelTask(channelPojo.id , channelPojo.channel_name,channelPojo.version));
				response.getWriter().append(gson.toJson(responsePojo));
			}
			else
			{
				responsePojo.ret_code = 0;
				responsePojo.ret_msg = "提交失败，可能存在相同版本的渠道名";
				response.getWriter().append(gson.toJson(responsePojo));
			}
		}
		
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}
	
	
	static final synchronized String getVersionName()
	{
		 String[] cmds = {"git","-C",Constants.PROJECT_CHECK_VERSION_PATH,"pull",Constants.PROJECT_GIT_REMOTE,Constants.PROJECT_GIT_BRANCH};  
	        try {
	        	Process pro = Runtime.getRuntime().exec(cmds);  
				pro.waitFor();
				InputStream in = pro.getInputStream();  
				BufferedReader read = new BufferedReader(new InputStreamReader(in));  
				String line = null;  
				while((line = read.readLine())!=null){  
					System.out.println(line);  
				}  
				read.close();
				in.close();
				
				in = new FileInputStream(Constants.PROJECT_CHECK_VERSION_BUILD_GRADLE_PATH);
				read = new BufferedReader(new InputStreamReader(in));
				while((line = read.readLine())!=null){  
					if(null != line && !"".equals(line) && line.indexOf("versionName") != -1)
					{
					   line=line.trim().replace("versionName ","").replaceAll("\"", "");
					   break;
					}
				}
				read.close();
				in.close();
				return line;
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}  
	        
		
		return null;
	}
	
	

}
