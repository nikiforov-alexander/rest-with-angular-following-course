// create angular application, second argument has to be []
// empty array
var tasksListApp = angular.module("tasksListApp", []);

// IMPORTANT : we have to add 'dataService' here, otherwise
// we'll get "can't find dataService" error
// $timeout is injected,  so that we can show/hide
// flash messages
tasksListApp.controller('MainController', function ($scope,
                                        dataService,
                                        $timeout) {

    // we set editing to false so that IDE
    // also sees it, and I think it is a good practice
    $scope.editing = false;

    // only this way response.data will be
    // available, and can be used to set
    // $scope.tasks
    dataService.getTasks(
        function (response) {
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
    // 2. we create "flash" object and set its "message"
    // and "status", depending on which different CSS
    // will be applied
    // 3. after 3000 ms flash will disappear
    // 4. @param index is taken from ng-repeat $index and
    // passed here in order to change tasks array
    // accordingly
    $scope.deleteTask = function (task, index) {
         // we call service only if task.id is not null
         if (task.id != null) {
             dataService.deleteTask(task);
         }
         // flash message part
         $scope.flash = {};
         $scope.flash.message = "Task '" + task.name +
             "' was successfully deleted";
         $scope.flash.status = "SUCCESS";
         $timeout(function () {
             $scope.flash = null;
         }, 3000);
         // update tasks array
         $scope.tasks.splice(index, 1);
    };

    // saves task. Works in 3 steps:
    // 1. actual dataService call to
    // make PUT request
    // 2. create flash message
    // 3. remove flash after 3000 ms
    $scope.saveTask = function (task) {
        // we call PUT or POST depending
        // whether we save new or update
        // old Task
        if (task.id == null) {
            dataService.saveNewTask(task);
        } else {
            dataService.updateTask(task);
        }
        // flash message part
        $scope.flash = {};
        $scope.flash.message = "Task '" + task.name +
            "' was successfully updated";
        $scope.flash.status = "SUCCESS";
        // remove flash after 3000 ms
        $timeout(function () {
            $scope.flash = null;
        }, 3000);
        // make task to be not-edited
        task.edited = false;
    };

    // adds new task and pushes at the end
    // of $scope.tasks array
    $scope.addNewTask = function () {
        var task = {
            name: "New Task",
            completed: false
        };
        $scope.tasks.push(task);
    }

});
// get tasks from server, service
tasksListApp.service('dataService', function ($http) {

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

    // update task method
    // that will simple work, see NOTE: with
    // $scope.saveTask = function ...
    // in controller
    this.updateTask = function (task) {
        $http.put('/api/v1/tasks/' + task.id, task);
    };

    // save new task method POST
    this.saveNewTask = function (task) {
        $http.post('/api/v1/tasks', task);
    }
});
