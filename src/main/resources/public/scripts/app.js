// create angular application, second argument has to be []
// empty array
angular.module("tasksListApp", [])

// IMPORTANT : we have to add 'dataService' here, otherwise
// we'll get "can't find dataService" error
.controller('MainController', function ($scope, dataService) {

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
    }
});
