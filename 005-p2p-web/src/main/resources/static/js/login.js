$(function () {


	//获取短信验证码
	$("#dateBtn1").on("click",function () {
		var phone = $.trim($("#phone").val());
		var loginPassword = $.trim($("#loginPassword").val());

		if ("" == phone) {
			$("#showId").html("请输入手机号码");
		} else if (!/^1[1-9]\d{9}$/.test(phone)) {
			$("#showId").html("请输入正确的手机号码");
		} else if ("" == loginPassword) {
			$("#showId").html("请输入登录密码");
		} else {
			$("#showId").html("");

			$.ajax({
				url:contextPath+"/loan/messageCode",
				type:"get",
				data:"phone="+phone,
				success:function (data) {
					if (data.code == 1) {
						alert("您的短信验证码是:" + data.data);
						$.leftTime(60, function (d) {
							if (d.status) {
								$("#dateBtn1").addClass("on");
								$("#dateBtn1").html((d.s == "00" ? "60" : d.s) + "秒后获取");
							} else {
								$("#dateBtn1").removeClass("on");
								$("#dateBtn1").html("获取验证码");
							}
						});
					} else {
						$("#showId").html(data.message);
					}
				},
				error:function () {
					$("#showId").html("短信平台异常,请稍后重试");
				}
			});
		}
	});


	//登录
	$("#loginBtn").on("click",function () {
		var phone = $.trim($("#phone").val());
		var loginPassword = $.trim($("#loginPassword").val());
		var messageCode = $.trim($("#messageCode").val());

		var redirectURL = $("#redirectURL").val();
		if ("" == phone) {
			$("#showId").html("请输入手机号码");
		} else if ("" == loginPassword) {
			$("#showId").html("请输入登录密码");
		} else if ("" == messageCode) {
			$("#showId").html("请输入短信验证码");
		} else {
			$("#showId").html("");

			$("#loginPassword").val($.md5(loginPassword));

			$.ajax({
				url:contextPath+"/loan/login",
				type:"post",
				data:{
					"phone":phone,
					"loginPassword":$.md5(loginPassword),
					"messageCode":messageCode
				},
				success:function (data) {
					if (data.code == 1) {
						window.location.href = redirectURL;
					} else {
						$("#loginPassword").val("");
						$("#showId").html(data.message);
					}
				},
				error:function () {
					$("#loginPassword").val("");
					$("#showId").html("短信平台异常,请稍后重试");
				}
			});
		}
	});


});
