## Use Cases

Given a request
When I get to the route run for that request
And its a bad request (400)
Then handle the error

Given a request
When I get to the route run for that request
And its a server error (500)
Then handle the error

Given a request
When I get to the engine
And a location is not found (404)
Then handle the error

Given a request
When I get to the engine
And a unexpected exception occurs
Then handle the error (500)

## Questions

##### 1. Should RouteRun implementations have a map of status code -> route run?
    This is one option. Everything done in the intended RouteRun's betweens will be lost.
    the RouteRun interface expects an Ask and Answer which wont have Session or User.
    
##### 2. How could RouteRun preserve work done in betweens?
    By knowing the type to each error route runs then translate the intended req/resp to the 
    req/resp desired. This would add an interface to route run to expect a typed req/resp.
    
##### 3. How can #2 be done specifically?
    JsonRouteRun.executeResourceMethod() would need to throw an exception or return an either 
    cannot put these as ivars on a exception b/c they have generic parameters.
        RestBtwnRequest<U> btwnRequest
        RestBtwnResponse btwnResponse
        RestRequest<U, P> request
        RestResponse<P> response
    It would need to be an either.    


##### Can ErrorTarget and RestErrorTarget be retrofitted to account for this feature?

## Tasks

##### refactor RouteRun.executeResourceMethod to return an Either.
##### Add default 400 to RestLocationTranslator
##### Remove default 400 code in JsonRouteRun
##### Attempt to use a common interface for error handling.