// create angular application, second argument has to be []
// empty array
angular.module("tasksListApp", [])

// IMPORTANT : we have to add 'dataService' here, otherwise
// we'll get "can't find dataService" error
// $timeout is injected,  so that we can show/hide
// flash messages
.controller('MainController', function ($scope,
                                        dataService,
                                        $timeout) {

    // we set editing to false so that IDE
    // also sees it, and I think it is a good practice
    $scope.editing = false;


    $scope.addTask = function () {
         console.log("adding Task");
    };

    // only this way response.data will be
    // available, and can be used to set
    // $scope.tasks
    dataService.getTasks(
        function (response) {
             console.log(response.data);
             $scope.tasks = response.data;
        }
    );

    // writing here
    // $scope.tasks = dataService.getTasks();
    // will NOT work, because this code is executed
    // before request is made

    // NOTE: methods from service will only be
    // available when we use them here in controller
    // simple deleteTask, without callback, works but we want
    // do stuff on success
    // we create "flash" object and set its "message"
    // and "status", depending on which different CSS
    // will be applied
    // after 3000 ms flash will disappear
    $scope.deleteTask = function (task) {
         // actual service call
         dataService.deleteTask(task);
         // flash message part
         $scope.flash = {};
         $scope.flash.message = "Task '" + task.name +
             "' was successfully deleted";
         $scope.flash.status = "SUCCESS";
         $timeout(function () {
             $scope.flash = null;
         }, 3000);
         // update tasks array
         var index = $scope.tasks.indexOf(task);
         $scope.tasks.splice(index, 1);
    }

})
// get tasks from server, service
.service('dataService', function ($http) {

    // here we define getTasks as a function that
    // takes callback as argument, and in controller
    // we pass "response" as argument = callback
    // because of nature oj AJAX requests
    // NOTE : we cannot use 'response' object with data
    // here directly
    this.getTasks = function(callback){
        $http.get('/api/v1/tasks')
            .then(callback)
    };

    // delete task method
    // that will simple work, see NOTE: with
    // $scope.deleteTask = function ...
    // in controller
    this.deleteTask = function (task) {
        $http.delete('/api/v1/tasks/' + task.id);
    };
});
