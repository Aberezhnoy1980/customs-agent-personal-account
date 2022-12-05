(function () {
    angular
        .module('account', ['ngRoute', 'ngStorage'])
        .config(config)
        .run(run);

    function config($routeProvider) {
        $routeProvider
            .when('/account', {
                templateUrl: 'account/account.html',
                controller: 'accountController'
            })
            .when('/admin', {
                templateUrl: 'admin/admin.html',
                controller: 'adminController'
            })
            .otherwise({
                redirectTo: '/'
            });
    }

    function run($rootScope, $http, $localStorage) {
        if ($localStorage.accountUser) {
            try {
                let jwt = $localStorage.accountUser.token;
                let payload = JSON.parse(atob(jwt.split('.')[1]));
                let currentTime = parseInt(new Date().getTime() / 1000);
                if (currentTime > payload.exp) {
                    console.log("Token is expired!!!");
                    delete $localStorage.accountUser;
                    $http.defaults.headers.common.Authorization = '';
                }
            } catch (e) {
            }

            if ($localStorage.accountUser){
                $http.defaults.headers.common.Authorization = 'Bearer ' + $localStorage.accountUser.token;
            }

        }
    }
})();

angular.module('account').controller('indexController', function ($rootScope, $scope, $http, $location, $localStorage) {
    $scope.tryToAuth = function () {
        $http.post('http://localhost:5555/auth/api/v1/secure/authenticate', $scope.user)
            .then(function successCallback(response) {
                if (response.data.token) {
                    $http.defaults.headers.common.Authorization = 'Bearer ' + response.data.token;
                    $localStorage.accountUser = {username: $scope.user.username, token: response.data.token};

                    $scope.user.username = null;
                    $scope.user.password = null;

                    $location.path('/account');
                }
            }, function errorCallback(response) {
                $scope.errorData = response.data;
                alert($scope.errorData.message)
            });
    };

    $scope.tryToLogout = function () {
        $scope.clearUser();
        $location.path('/');
    };

    $scope.clearUser = function () {
        delete $localStorage.accountUser;
        $http.defaults.headers.common.Authorization = '';
    };

    $rootScope.isUserLoggedIn = function () {
        return !!$localStorage.accountUser;
    };

    $rootScope.isAdmin = function (){
        if($localStorage.accountUser) {
            let jwt = $localStorage.accountUser.token
            let payload = JSON.parse(atob(jwt.split('.')[1]));
            payload.roles.forEach(role => {
                if (role === 'ROLE_ADMIN') {
                    console.log("Neo is here")
                    return true;
                }
            });
        }else{
            return false;
        }
    }

});