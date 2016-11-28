// create angular application, second argument has to be []
// empty array
angular.module("tasksListApp", [])

.controller('MainController', function ($scope) {

   // we set editing to false so that IDE
   // also sees it, and I think it is a good practice
   $scope.editing = false;

   $scope.addTask = function () {
        console.log("adding Task");
   };

   $scope.tasks = [
       {"name" : "Clean the House"},
       {"name" : "Wash the Dishes"},
       {"name" : "Call the Owner"},
       {"name" : "Create the App"},
       {"name" : "Test the App"}
   ]
});
