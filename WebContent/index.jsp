<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";

%>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
<meta charset="utf-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta name="viewport" content="width=device-width, initial-scale=1">
<!-- 上述3个meta标签*必须*放在最前面，任何其他内容都*必须*跟随其后！ -->
<meta name="description" content="">
<meta name="author" content="jie">
<title>渠道打包</title>
<!-- Bootstrap core CSS -->
<link href="//cdn.bootcss.com/bootstrap/3.3.5/css/bootstrap.min.css"
	rel="stylesheet">
<!-- Custom styles for this template -->
<link href="http://v3.bootcss.com/examples/grid/grid.css"
	rel="stylesheet">
<!-- HTML5 shim and Respond.js for IE8 support of HTML5 elements and media queries -->
<!--[if lt IE 9]>
      <script src="//cdn.bootcss.com/html5shiv/3.7.2/html5shiv.min.js"></script>
      <script src="//cdn.bootcss.com/respond.js/1.4.2/respond.min.js"></script>
    <![endif]-->
<script src="//cdn.bootcss.com/jquery/1.11.3/jquery.min.js"></script>
<script src="//cdn.bootcss.com/bootstrap/3.3.5/js/bootstrap.min.js"></script>
<script type="text/javascript">
var basePath = "<%=basePath %>";

function query()
{
	$.ajax({
		  url: basePath+"query",
		  dataType: 'json',
		  success: function(response)
		  {
			  if(null != response.data)
			  {
				  	var htmlStr = "";
				  	for(i  in  response.data)
			  		{
				  		var channel = response.data[i];
				  		htmlStr += "<tr>";
				  		htmlStr += "<td>"+channel.id+"</td>";
				  		htmlStr += "<td>"+channel.channel_name+"</td>";
				  		htmlStr += "<td>"+channel.version+"</td>";
				  		htmlStr += "<td>"+channel.create_date+"</td>";
				  		htmlStr += "<td>"+channel.status+"</td>";
				  		htmlStr += "</tr>";
			  		}
				  $("#content_tbody").append(htmlStr)
			  }
		  }
		});
}

function submitChannel()
{
	$.ajax({
		  url: basePath+"submit",
		  data:{channel:$("#channel_name").val()},
		  dataType: 'json',
		  success: function(response)
		  {
			  alert (response.ret_code);
			  alert (response.ret_msg);
		  }
		});
}

</script>
</head>
<body onload="query()">
	<div class="container">

		<div class="page-header">
			<h1>足球控渠道打包系统</h1>
			<p class="lead">目前提供自动打渠道包、下载及记录查看，自动邮件推送暂未开发！</p>
		</div>

		<form class="navbar-form navbar-left" role="search">
			<h4 class="form-signin-heading">输入渠道包名称 :</h4>
			<div class="form-group">
				<input id="channel_name"  type="text" style="width: 300px;" class="form-control"
					placeholder="只能是数字和英文的组合，并且不能为纯数字">
			</div>
			<button id="submitButton" onclick="submitChannel();" type="button" class="btn btn-success">提交</button>
		</form>
	</div>
	<div class="container"  id="content">
		<h2 class="sub-header">日志</h2>
		<div class="table-responsive">
			<table class="table table-striped">
				<thead>
					<tr>
						<th>id</th>
						<th>渠道名</th>
						<th>打包版本号</th>
						<th>提交日期</th>
						<th>状态</th>
					</tr>
				</thead>
				<tbody id="content_tbody">
				
				</tbody>
			</table>
		</div>
	</div>
</body>
</html>

