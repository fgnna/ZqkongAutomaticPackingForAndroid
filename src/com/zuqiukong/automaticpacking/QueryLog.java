package com.zuqiukong.automaticpacking;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;

/**
 * Servlet implementation class QueryLog
 */
@WebServlet("/query")
public class QueryLog extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Gson gson = new Gson();
    /**
     * @see HttpServlet#HttpServlet()
     */
    public QueryLog() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
	{
		
		List<ChannelPojo>  list = Model.getInstance().queryAll();
		response.setHeader("content-type","text/html;charset=UTF-8");
		ResponseBasePojo<List<ChannelPojo>> responsePojo = new ResponseBasePojo<>(); 
		if(null !=list && 0 != list.size())
		{
			responsePojo.ret_code = 1;
			responsePojo.ret_msg = "";
			responsePojo.data = list;
			response.getWriter().append(gson.toJson(responsePojo));
		}
		else
		{
			responsePojo.ret_code = 0;
			responsePojo.ret_msg = "查询失败";
			response.getWriter().append(gson.toJson(responsePojo));
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
	{
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
