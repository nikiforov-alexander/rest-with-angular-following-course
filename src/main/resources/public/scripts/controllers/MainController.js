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

    // saves all tasks. Works in 2 steps:
    // 1. cycle through tasks and save or update them
    // and make tasks saved unedited
    // 2. show flash message
    $scope.saveTasks = function () {

        $scope.tasks.forEach(function (task) {
            if (task.edited) {
                TaskService.saveOrUpdate(task);
                task.edited = false;
            }
        });

        $scope.flash = {};
        $scope.flash.message = "Tasks" +
            " were successfully saved";
        $scope.flash.status = "SUCCESS";
        $timeout(function () {
            $scope.flash = null;
        }, 3000);
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
