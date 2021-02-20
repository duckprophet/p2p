//错误提示
function showError(id,msg) {
	$("#"+id+"Ok").hide();
	$("#"+id+"Err").html("<i></i><p>"+msg+"</p>");
	$("#"+id+"Err").show();
	$("#"+id).addClass("input-red");
}
//错误隐藏
function hideError(id) {
	$("#"+id+"Err").hide();
	$("#"+id+"Err").html("");
	$("#"+id+"Ok").hide();
	$("#"+id).removeClass("input-red");
}
//显示成功
function showSuccess(id) {
	$("#"+id+"Err").hide();
	$("#"+id+"Err").html("");
	$("#"+id+"Ok").show();
	$("#"+id).removeClass("input-red");
}


//打开注册协议弹层
function alertBox(maskid,bosid){
	$("#"+maskid).show();
	$("#"+bosid).show();
}
//关闭注册协议弹层
function closeBox(maskid,bosid){
	$("#"+maskid).hide();
	$("#"+bosid).hide();
}

//注册协议确认
$(function() {
	$("#agree").click(function(){
		var ischeck = document.getElementById("agree").checked;
		if (ischeck) {
			$("#btnRegist").attr("disabled", false);
			$("#btnRegist").removeClass("fail");
		} else {
			$("#btnRegist").attr("disabled","disabled");
			$("#btnRegist").addClass("fail");
		}
	});


	//验证手机号码
	$("#phone").on("blur",function () {
		//获取手机号码
		var phone = $.trim($("#phone").val());

		//判断手机号码是否为空
		if ("" == phone) {
			showError("phone","请输入手机号码");
		} else if (!/^1[1-9]\d{9}$/.test(phone)) {
			showError("phone", "请输入正确的手机号码");
		} else {
			//验证手机号码的方法只接受post请求
			//data 传递的参数,可以拼接key=value&...字符串,也可以使用json格式
			
			$.ajax({
				url:contextPath+"/loan/checkPhone",
				type:"get",
				data:"phone="+phone,
				success:function (data) {
					if (data.code == 1) {
						showSuccess("phone");
					} else {
						showError("phone",data.message);
					}
				},
				error:function () {
					showError("phone","系统繁忙,请稍后重试");
				}
			});
		}
	});

	//验证登录密码
	$("#loginPassword").on("blur",function () {
		//获取登录密码
		var loginPassword = $.trim($("#loginPassword").val());

		//判断密码是否为空
		if ("" == loginPassword) {
			showError("loginPassword","请设置登录密码");
		} else if (!/^[0-9a-zA-Z]+$/.test(loginPassword)) {
			showError("loginPassword","登录密码只可使用字母和数字");
		} else if (!/^(([a-zA-Z]+[0-9]+)|([0-9]+[a-zA-Z]+))[a-zA-Z0-9]*/.test(loginPassword)) {
			showError("loginPassword","登录密码必须同时包含英文和数字");
		} else if (loginPassword.length < 6 || loginPassword.length > 20) {
			showError("loginPassword", "密码长度应为6-20位");
		} else {
			showSuccess("loginPassword");
		}
	});

	$("#messageCode").on("blur",function () {
		var messageCode = $.trim($("#messageCode").val());
		if ("" == messageCode) {
			showError("messageCode", "请输入短信以信码");
		} else {
			showSuccess("messageCode");
		}
	});

	//注册按钮
	$("#btnRegist").on("click",function () {

		//失去焦点
		$("#phone").blur();
		$("#loginPassword").blur();
		$("#messageCode").blur();

		/*var flag = true;
		$("div[id$='Err']").each(function () {
			// this 是指当前循环的对象,是一个dom对象
			var errorText = $(this).text();

			if ("" != errorText) {
				flag = false;
				return ;
			}
		});

		if (flag) {
			alert("提交的注册请求");
		}*/

		//return 是中止
		//return false 是中止并返回一个false


		/*alert($("div[id$='Err']").html());
		alert($("div[id$='Err']").text());*/

		var errorTexts = $("div[id$='Err']").text();

		if ("" == errorTexts) {
			var phone = $.trim($("#phone").val());
			//获取到的密码是一个明文
			var loginPassword = $.trim($("#loginPassword").val());
			var messageCode = $.trim($("#messageCode").val());

			//在前端对路径就进行加密操作,使用的是jquery提供的MD方法
			$("#loginPassword").val($.md5(loginPassword));


			$.ajax({
				url:contextPath+"/loan/register",
				type:"post",
				data:{
					"phone":phone,
					"loginPassword":$.md5(loginPassword),
					"messageCode":messageCode
				},
				success:function (data) {
					if (data.code == 1) {
						window.location.href = contextPath + "/loan/page/realName";
					} else {
						$("#loginPassword").val("");
						$("#messageCode").val("");
						hideError("messageCode");
						hideError("loginPassword");
						showError("messageCode",data.message);
					}

				},
				error:function () {
					$("#loginPassword").val("");
					$("#messageCode").val("");
					hideError("messageCode");
					hideError("loginPassword");
					showError("messageCode","系统繁忙,请稍后重试")
				}
			});
		}



	});


	$("#messageCodeBtn").on("click",function () {

		if (!$("#messageCodeBtn").hasClass("on")) {

			$("#phone").blur();
			var phoneErrText = $("#phoneErr").text();
			if ("" != phoneErrText) {
				return;
			}

			$("#loginPassword").blur();
			var loginPasswordErrText = $("#loginPasswordErr").text();
			if ("" != loginPasswordErrText) {
				return;
			}

			var phone = $.trim($("#phone").val());

			$.ajax({
				url:contextPath+"/loan/messageCode",
				type:"get",
				data:"phone="+phone,
				success:function (data) {
					if (data.code == 1) {
						alert("您的短信验证码是:" + data.data);
						$.leftTime(60,function (d) {
							if (d.status) {
								$("#messageCodeBtn").addClass("on");
								$("#messageCodeBtn").html((d.s == "00"?"60":d.s) + "秒后获取");
							} else {
								$("#messageCodeBtn").removeClass("on");
								$("#messageCodeBtn").html("获取验证码");
							}
						});

					} else {
						showError("messageCode",data.message);
					}
				},
				error:function () {
					showError("messageCode","短信平台异常,请稍后重试");
				}
			});



		}

	});
});
