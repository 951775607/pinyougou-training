//定义品牌的处理器
app.controller("brandController", function ($scope, $http, $controller, brandService) {

    //继承处理器；参数1：要继承的处理器名称，参数2：传递本处理器的信息到父处理器
    $controller("baseController", {$scope:$scope});

    //查询品牌列表
    $scope.findAll = function () {
        brandService.findAll().success(function (response) {
            $scope.list = response;

        });

    };


    //根据分页信息查询数据
    $scope.findPage = function (page, rows) {
        brandService.findPage(page, rows).success(function (response) {
            //response是一个分页结果对象pageResult
            $scope.paginationConf.totalItems = response.total;

            //更新记录列表
            $scope.list = response.rows;

        }).error(function () {
            alert("加载数据失败！");
        });

    };

    //保存数据
    $scope.save = function () {
        var obj;

        if ($scope.entity.id != null) {
            obj = brandService.update($scope.entity);
        } else {
            obj = brandService.add($scope.entity);
        }

        obj.success(function (response) {
            if(response.success){
                //刷新列表
                $scope.reloadList();
            } else {
                alert(response.message);
            }

        });

    };

    //根据主键查询
    $scope.findOne = function (id) {
        brandService.findOne(id).success(function (response) {
            $scope.entity = response;

        });

    };

    //删除
    $scope.delete = function () {

        //1、判断是否已经选择了
        if ($scope.selectedIds.length < 1) {
            alert("请先选择要删除的记录");
            return;
        }

        //2、确认删除；如果点击 确认 则返回true,否则false
        if(confirm("确定要删除选择的记录吗？")){
            //3、删除
            brandService.delete($scope.selectedIds).success(function (response) {
                if(response.success){
                    //刷新列表
                    $scope.reloadList();
                    //清除数组
                    $scope.selectedIds = [];
                } else {
                    alert(response.message);
                }

            });
        }
    };

    //定义查询条件对象；如果不定义的话那么其提交到后台的内容为undefined，这个值无法转换为一个java对象；如果为{}则可以转换为品牌空对象
    $scope.searchEntity = {};
    //条件分页查询
    $scope.search = function (page, rows) {

        brandService.search(page, rows, $scope.searchEntity).success(function (response) {
            //更新分页导航条的总记录数
            $scope.paginationConf.totalItems = response.total;

            $scope.list = response.rows;

        });
    };
});
