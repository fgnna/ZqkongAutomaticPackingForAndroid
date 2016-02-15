package com.zuqiukong.automaticpacking;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;

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
			
			if(Model.getInstance().insert(channel_name, "1.0.0"))
			{
				responsePojo.ret_code = 1;
				responsePojo.ret_msg = "";
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

}
