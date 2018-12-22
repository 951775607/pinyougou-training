app.controller("cartController", function ($scope, cartService) {

    //获取用户信息
    $scope.getUsername = function () {
        cartService.getUsername().success(function (response) {
            $scope.username = response.username;

        });

    };

    //获取购物车列表数据
    $scope.findCartList = function () {
        cartService.findCartList().success(function (response) {
            $scope.cartList = response;

            //计算总数量和价格
            $scope.totalValue = cartService.subTotalValue(response);
        });

    };

    //加入购物车
    $scope.addItemToCartList = function (itemId, num) {
        cartService.addItemToCartList(itemId, num).success(function (response) {
            if(response.success){
                //加入购物车成功则刷新列表
                $scope.findCartList();
            } else {
                alert(response.message);
            }

        });

    };
});