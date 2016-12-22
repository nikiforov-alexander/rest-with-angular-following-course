
// get tasks from server, service
tasksListApp.service('TaskService', function ($http) {

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
