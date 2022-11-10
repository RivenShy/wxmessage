
layui.use([ 'element', 'jquery', 'laydate', 'form'],function() {
    var $ = layui.jquery;
    var element = layui.element;
    var laydate = layui.laydate;
    var form = layui.form;
//			//监听提交事件
//			form.on('submit(demo1)', function(data){
//				  //获取请求数据
//				  var oldPwd = data.field.oldPwd;
//				  var newPwd = data.field.newPwd;
//				  var confirmPwd = data.field.confirmPwd;
//				  //使用ajax发送请求
//				  $.ajax({
//					  url:"/user/changePwd",
//					  type:"post",
//					  data:data.field,
//					  dataType:"json",
//					  success:function(data){
//					  }
//				  });
//				return false;  //阻止表单跳转。如果需要表单跳转，去掉这段即可。
//			   });
    $("#toBind").click(function(){
//      alert("tobind");
      $.ajax({
          url:"/wx/getQRcode",
//          type:"post",
          type:"get",
//          data:data.field,
          dataType:"json",
          success:function(data){
                console.log("data:" + JSON.stringify(data));
                console.log("data.qrCodeUrl:" + data.qrCodeUrl)
                $("#qrcode").attr("src", data.qrCodeUrl);
                $("#qrcode").show();
                $("#confirmBind").show();
                $("#tips").show();
          }
      });
    });

    $("#confirmBind").click(function(){
      $.ajax({
          url:"/wx/confirmBind",
          type:"get",
          dataType:"json",
          success:function(data){
                console.log("data:" + JSON.stringify(data));
          }
      });
    });
});
