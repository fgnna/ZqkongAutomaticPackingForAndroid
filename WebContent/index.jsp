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
<link href="css/bootstrap.min.css"
	rel="stylesheet">
<!-- Custom styles for this template -->
<link href="css/grid.css"
	rel="stylesheet">
<!-- HTML5 shim and Respond.js for IE8 support of HTML5 elements and media queries -->
<!--[if lt IE 9]>
      <script src="//cdn.bootcss.com/html5shiv/3.7.2/html5shiv.min.js"></script>
      <script src="//cdn.bootcss.com/respond.js/1.4.2/respond.min.js"></script>
    <![endif]-->
<script src="js/jquery.min.js"></script>
<script src="js/bootstrap.min.js"></script>
<script type="text/javascript" src="js/jquery.qrcode.js"></script>
<script type="text/javascript" src="js/qrcode.js"></script>
<script type="text/javascript">
var basePath = "<%=basePath %>";
var status_map = {0:"待处理",1:"正在打包",2:"完成",3:"失败"}

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
				  		htmlStr += "<td>"+channel.channel_name+"</td>";
				  		htmlStr += "<td>"+channel.version+"</td>";
				  		htmlStr += "<td>"+channel.create_date+"</td>";
				  		htmlStr += "<td>"+status_map[channel.status]+"</td>";
				  		if(2 == channel.status )
				  		{
				  			var apkName = "zuqiukong_"+channel.channel_name+"_release_"+channel.version+".apk";
				  			htmlStr += "<td><a href='apk/"+apkName+"'>"+apkName+"</a></td>";
				  		}
				  		else
				  		{
				  			htmlStr += "<td></td>";
				  		}
				  		//zuqiukong_a360_release_3.0.0.apk
				  		htmlStr += "</tr>";
			  		}
				  $("#content_tbody").append(htmlStr)
			  }
		  }
		});
}

function submitChannel()
{
	$("#submit_loading").show();
	$("#submitButton").hide();
	$.ajax({
		  url: basePath+"submit",
		  data:{channel:$("#channel_name").val()},
		  dataType: 'json',
		  success: function(response)
		  {
		
			  if(0 == response.ret_code)
			  {
				  alert (response.ret_msg);
			  }
			  else
			  {
				  query();
				  $("#channel_name").val("");
				  alert ("成功");
			  }
			  
			  $("#submit_loading").hide();
			  $("#submitButton").show();
			  
		  }
		});
}

function updateBeta(reqCode)
{
	$('#qrcode').hide();
	$('#qrcodeRelease').hide();
	$('#download_beta').hide();
	$('#update_beta').hide();
	$('#update_beta_loading').show();
	$.ajax({
		  url: basePath+"updatebeta",
		  data:{"reqCode":reqCode},
		  dataType: 'json',
		  success: function(response)
		  {
		
			  if(0 == response.ret_code)//无更新
			  {
				  $('#qrcode').show();
				  $('#qrcodeRelease').show();
				  $('#download_beta').show();
				  $('#update_beta').show();
				  $('#update_beta_loading').hide();
			  }
			  else//打包中
			  {
				  $('#qrcode').hide();
				  $('#qrcodeRelease').hide();
				  $('#download_beta').hide();
				  $('#update_beta').hide();
				  $('#update_beta_loading').show();
			  }
			  $("#what_new").html(response.ret_msg);
		  }
		});
}

function qccoded()
{
	$('#qrcode').qrcode({width: 128,height: 128,text: basePath+"apk/zuqiukong_beta_debug_beta.apk"});
	$('#qrcodeRelease').qrcode({width: 128,height: 128,text: basePath+"apk/zuqiukong_beta_release_beta.apk"});

}

</script>
</head>
<body onload="query();qccoded();updateBeta()">
	<div class="container">

		<div class="page-header">
			<h1>足球控渠道打包系统</h1>
			<p class="lead">目前提供自动打渠道包、下载及记录查看，自动邮件推送暂未开发！</p>
		</div>

	<div class="row" >
		<div class="container" >
		      <!-- Example row of columns -->
		      <div class="row" >
		        <div class="col-md-6" style="height:380px;" >
			        <form class="navbar-form navbar-left" role="search">
						<h4 class="form-signin-heading">输入渠道包名称 :</h4>
						<div class="form-group">
							<input id="channel_name"  type="text" style="width: 300px;" class="form-control"  value="sadfsadfasd"
								placeholder="只能是数字英文的组合，并且不能为纯数字">
						</div>
						<button id="submitButton" onclick="submitChannel();" type="button" class="btn btn-success">提交</button>
						<img id="submit_loading" hidden="true" alt="" width="50" height="30" src="image/loading2.gif"/>
					</form>
		        </div>
		        <div class="col-md-4" style="height:380px;overflow-x:scroll;">
		        	<h5 style="color:#208e48;">Beta版日志</h5>
			        <div id="what_new" >
				
				
				
					</div>
		       </div>
		        <div class="col-md-2" style="height:380px;">
						<h5 style="color:#208e48;">Beta版测试接口</h5>
						<div id="qrcode" hidden="true"></div>
						<img id="update_beta_loading" alt="" width="128" height="128" src="image/loading.gif"/>
						<a id="download_beta"  hidden="true" href="apk/zuqiukong_beta_debug_beta.apk">下载</a>
						<a id="update_beta" style="margin-left: 20px;"  hidden="true" onclick="updateBeta('1')">更新</a>
						<h5 style="color:#208e48;">Beta版正式接口</h5>
						<div id="qrcodeRelease" hidden="true"></div>
						
		        </div>
		 </div>
		</div>
	</div>
	<div class="row">
	<div class="container"  id="content">
		<h2 class="sub-header">日志</h2>
		<div class="table-responsive">
			<table class="table table-striped">
				<thead>
					<tr>
						<th>渠道名</th>
						<th>打包版本号</th>
						<th>提交日期</th>
						<th>状态</th>
						<th>下载</th>
					</tr>
				</thead>
				<tbody id="content_tbody">
				
				</tbody>
			</table>
		</div>
	</div>
	</div>
	
</body>
</html>

