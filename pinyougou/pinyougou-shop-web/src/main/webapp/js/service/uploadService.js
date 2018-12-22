app.service("uploadService",function ($http) {

    this.uploadFile = function () {
        //创建表单对象
        var formData = new FormData();
        //添加表单项
        formData.append("file", file.files[0]);
        return $http({
            url:"../upload.do",
            method:"post",
            data:formData,
            headers:{"Content-Type": undefined},
            transformRequest: angular.identity
        });
    };
});