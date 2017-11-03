<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page isELIgnored="false"%>
<%
  String path = request.getContextPath();
  String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
  <base href="<%=basePath%>">

  <title>My JSP 'index.jsp' starting page</title>
  <meta http-equiv="pragma" content="no-cache">
  <meta http-equiv="cache-control" content="no-cache">
  <meta http-equiv="expires" content="0">
  <meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
  <meta http-equiv="description" content="This is my page">
  
  <script type="text/JavaScript"
	src="<%=request.getContextPath()%>/js/jquery-1.10.2.js"></script>

</head>

<body>
<form action="qd/index" method="get">
  <input type="text" name="keyWords" id="kw" />
  <input type="submit" value="千度一下">
  <div id="ts"><div>
  <input type="hidden" value="1" name="pageNum">
</form>
<c:if test="${! empty page.list }">
<h3>千度为您找到相关结果约${page.rowCount}个</h3>
<c:forEach items="${page.list}" var="bean">
  <%-- <img src="${bean.imgurl }" /> --%>
  <a href="${bean.docurl }">${bean.title}</a>
  <br/>
  <br/>
  <span>${bean.remark}</span>
  <br/>
  <br/>
</c:forEach>

<c:if test="${page.hasPrevious }">
  <a href="qd/index?pageNum=${page.previousPageNum }&keyWords=${keyWords}"> 上一页</a>
</c:if>
<c:forEach begin="${page.everyPageStart }" end="${page.everyPageEnd }" var="n">
  <a href="qd/index?pageNum=${n }&keyWords=${keyWords}"> ${n }</a> &nbsp;&nbsp;
</c:forEach>

<c:if test="${page.hasNext }">
  <a href="qd/index?pageNum=${page.nextPageNum }&keyWords=${keyWords}"> 下一页</a>
</c:if>
</c:if>

</body>
<script type="text/javascript">
 $(function(){
	 /* $('#kw').bind('input propertychange', function() { 
		 //进行相关操作 
		 var kw=$(this).val();
		 kw=kw.replace(/(^\s*)|(\s*$)/g, "");
		 if(kw.length==0){
			 $("#ts").html('');
			 return;
		 }else{
			 $.post("qd/complateKey",{"keyWords":kw},function(data){
				 $("#ts").html('');
				 var str="<ul>";
				 for (var i = 0; i < data.length; i++) {
					str+="<li>"+data[i]+"</li>"
				}
				 str+="</ul>";
				 $("#ts").append(str);
			 });
		 }
		}); */
	 
           $('#kw').on('input', function () {
               if ($(this).prop('comStart')) return;    // 中文输入过程中不截断
               var value = $(this).val();
               value=value.replace(/(^\s*)|(\s*$)/g, "");
               if(kw.length==0){
      			 $("#ts").html('');
      			 return;
      			 }
               $.post("qd/complateKey",{"keyWords":value},function(data){
  				 $("#ts").html('');
  				 var str="<ul>";
  				 for (var i = 0; i < data.length; i++) {
  					str+="<li>"+data[i]+"</li>"
  				}
  				 str+="</ul>";
  				 $("#ts").append(str);
  			 });
           }).on('compositionstart', function () {
               $(this).prop('comStart', true);
           }).on('compositionend', function () {
               $(this).prop('comStart', false);
               var value = $(this).val();
               value=value.replace(/(^\s*)|(\s*$)/g, "");
               if(kw.length==0){
      			 $("#ts").html('');
      			 return;
      			 }
               $.post("qd/complateKey",{"keyWords":value},function(data){
    				 $("#ts").html('');
    				 var str="<ul>";
    				 for (var i = 0; i < data.length; i++) {
    					str+="<li>"+data[i]+"</li>"
    				}
    				 str+="</ul>";
    				 $("#ts").append(str);
    			 });
           });
 });
</script>
</html>
