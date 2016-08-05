angular.module('TodoApp', ['ngRoute']).config(['$routeProvider', function ($routeProvider) {
    $routeProvider.
    when('/', {templateUrl: '/tpl/lists.html', controller: ListCtrl}).
    when('/add-task', {templateUrl: '/tpl/add-new.html', controller: AddCtrl}).
    when('/edit/:id', {templateUrl: '/tpl/edit.html', controller: EditCtrl}).
    otherwise({redirectTo: '/'});
}]);

function ListCtrl($scope, $http) {
    $http.get('/api/tasks').success(function (data) {
        $scope.tasks = data.tasks;
    });

    var eb = new EventBus("/eventbus/");
    eb.onopen = function () {
        eb.registerHandler("app.to.client", function (err, msg) {
            $scope.tasks = msg.body.tasks;
            $scope.$apply();
            console.log("Tasks are updated, refreshing... ");
            console.log("Tasks : " + JSON.stringify($scope.tasks))
        });
    };
}

function AddCtrl($scope, $http, $location) {
    $scope.master = {};
    $scope.activePath = null;

    $scope.add_new = function (task, AddNewForm) {
        $http.post('/api/tasks', task).success(function () {
            $scope.reset();
            $scope.activePath = $location.path('/');
        });

        $scope.reset = function () {
            $scope.task = angular.copy($scope.master);
        };

        $scope.reset();
    };
}

function EditCtrl($scope, $http, $location, $routeParams) {
    var id = $routeParams.id;
    $scope.activePath = null;

    $http.get('/api/tasks/' + id).success(function (data) {
        $scope.task = data;
    });

    $scope.update = function (task) {
        $http.put('/api/tasks/' + id, task).success(function (data) {
            $scope.task = data;
            $scope.activePath = $location.path('/');
        });
    };

    $scope.delete = function (task) {
        var deleteTask = confirm('Are you absolutely sure you want to delete ?');
        if (deleteTask) {
            $http.delete('/api/tasks/' + id)
                .success(function(data, status, headers, config) {
                    $scope.activePath = $location.path('/');
                }).
            error(function(data, status, headers, config) {
                console.log("error");
            });
        }
    };
}