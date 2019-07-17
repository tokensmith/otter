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

## Implementation

#### ServletGateway

 - notFound
    - 404
 - onDispatchError
    - 415
 - onError
    - 400, 500
    
#### Group, RestGroup

 - onDispatchError
    - 415
 - onError
     - 400, 500
     
#### Location

- onDispatchError
    - 415
 - onError
     - 400, 500 
     
     
## Tasks

#### notFound
 - add interface to gateway, `notFound(regex, errorTarget)` or `notFound(regex, resource)`
   depends if we want to support adding betweens for not founds.
 - add regex to the, `Engine.errorRouteRunners`
 - punch it through to `Engine.errorRouteRunners`
 - do regex matching on `Engine.errorRouteRunners`
 - what happens when no match? have a default yo!

#### Gatway
 - rename `gateway.setErrorRoute` to `gateway.setDispatchError`
 - RestTarget, Target, RestErrorTarget, ErrorTarget.
 - add `Class<P>` to `RestErrorTarget`
 - modify builders to use `Class<P>` ^
 - add interface to include regex with errorTarget
 - add interface to include regex with restErrorTarget.
 
#### Group, RestGroup
 - add method `dispatchError` to builders
 - walk it through to LocationTranslators

#### Location
 - add method `dispatchError` to builders
 - walk it through to LocationTranslators
 

  
    