'use strict';

// IMPORTANT : we have to add 'TaskService' here, otherwise
// we'll get "can't find TaskService" error
// $timeout is injected,  so that we can show/hide
// flash messages
tasksListApp.controller('MainController', function ($scope,
                                                    TaskService,
                                                    $timeout) {

    // we set editing to false so that IDE
    // also sees it, and I think it is a good practice
    $scope.editing = false;

    // only this way response.data will be
    // available, and can be used to set
    // $scope.tasks
    TaskService.getTasks(
        function (response) {
             $scope.tasks = response.data;
        }
    );

    // writing here
    // $scope.tasks = TaskService.getTasks();
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
             TaskService.deleteTask(task);
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
    // 1. actual TaskService call to
    // make PUT request
    // 2. create flash message
    // 3. remove flash after 3000 ms
    $scope.saveTask = function (task) {
        // we call PUT or POST depending
        // whether we save new or update
        // old Task
        if (task.id == null) {
            TaskService.saveNewTask(task);
        } else {
            TaskService.updateTask(task);
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
