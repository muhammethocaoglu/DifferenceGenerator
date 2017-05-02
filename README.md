# DifferenceGenerator

There are three endpoints in the microservice and these are listed with their usages. 

1. leftDiff endpoint with request mapping "/v1/diff/{id}/left" is called as follows:
   <host>/v1/diff/testId/left with following json including a base64 encoded string in request body:
   
   { 
      "content" : "dGVzdA=="
   } 
   
   and "Content-Type" with value "application/json" in header. Finally, it returns saved BinaryData in json format as follows:
   
   {
      "dbId": "4287ed29-bd3c-45af-844d-80b571c7adf0",
      "id": "testId",
      "side": "left",
      "content": "dGVzdA=="
   }
   
 2. rightDiff endpoint with request mapping "/v1/diff/{id}/right" is called as follows:
   <host>/v1/diff/testId/right with following json including a base64 encoded string in request body:
   
   { 
      "content" : "a3Jhdg=="
   } 
   
   and "Content-Type" with value "application/json" in header. Finally, it returns saved BinaryData in json format as follows: 
   
   {
      "dbId": "8f95b206-1ffd-4961-9a9c-bba2969352b1",
      "id": "testId",
      "side": "right",
      "content": "a3Jhdg=="
   }
   
   
 3. difference endpoint with request mapping "/v1/diff/{id}" is called as follows:
   <host>/v1/diff/testId. Finally, it returns different parts between two strings saved to repository 
   using leftDiff and rightDiff endpoints with offset and length of each part as follows: 
   
   {
      "diffArray": [
        {
          "offset": 0,
          "length": 4
        },
        {
        "offset": 5,
        "length": 1
        }
     ]
   }
   
   
   
