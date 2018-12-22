app.controller("baseController", function ($scope) {
    //初始化分页导航条的参数
    $scope.paginationConf = {
        //页号
        currentPage: 1,
        //页大小
        itemsPerPage: 10,
        //总记录
        totalItems: 0,
        //页大小选择
        perPageOptions: [10, 20, 30, 40, 50],
        //如果页号发生改变的事件
        onChange:function(){
            $scope.reloadList();
        }
    };

    $scope.reloadList = function () {
        //$scope.findPage($scope.paginationConf.currentPage, $scope.paginationConf.itemsPerPage);
        $scope.search($scope.paginationConf.currentPage, $scope.paginationConf.itemsPerPage);

    };

    //已选择了的id的数组
    $scope.selectedIds = [];

    //选择或者反选
    $scope.updateSelection = function ($event, id) {
        //$event.target最初触发事件的DOM元素。
        if($event.target.checked){
            //如果是选中复选框，应该将当前点击了的品牌的id 记录到 选择了的id数组中；
            $scope.selectedIds.push(id);
        } else {
            //如果是反选复选框，应该将当前点击的品牌的id从 选择了的id数组中 删除
            var index = $scope.selectedIds.indexOf(id);

            //参数1：要删除的元素的索引号，参数2：删除的个数
            $scope.selectedIds.splice(index, 1);
        }

    };

    //将一个json列表字符串中的某个属性的值串起来返回
    $scope.jsonToString = function (jsonArrayStr, key) {
        var str = "";
        //将字符串转换为json
        var jsonArray = JSON.parse(jsonArrayStr);
        for (var i = 0; i < jsonArray.length; i++) {
            var jsonObj = jsonArray[i];
            if (str.length > 0) {
                str += "," + jsonObj[key];
            } else {
                str = jsonObj[key];
            }
        }

        return str;

    };

});