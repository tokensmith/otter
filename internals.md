# Otter internals

## EntryFilter

This is the entry point of all requests, it is responsible for forwarding request to OtterEntryServlet or rendering a jsp.
Before it forwards to the OtterEntryServlet it prepends `/app/` to the path of the request.
Resources do not need to have `/app/` in the url as that gets scrubbed when requests get dispatched.

## OtterEntryServlet

This extends HttpServlet and handles all requests that should be processed by a Resource (Controller).
This must have `/app/*` for its url matcher - since the EntryFilter prepends that to all requests intended be handled by otter.
The EntryFilter does that in order to guarantee that OtterEntryServlet will handle incoming requests that are not intended for jsps.

## ReadListenerImpl

This is responsible for reading data asychronously.

## WriteListenerImpl

This is responsible for writing data asychronously.

## ServletGateway

This is the gateway into otter. No serlvet api concretes, interfaces, etc can go beyond this class.
- translates a servlet request to a otter request
- delegates to Engine, which in turn will delegate to resources.
- translates a otter response to a servlet api response.

## HttpServletRequestTranslator

Translates a HttpServletRequest to a otter request.

## HttpServletRequestMerger

Merges a otter response to a HttpServletRequest.
- otter presenter to HttpServletRequest attribute - so it can be referenced in a jsp. 

## HttpServletResponseMerger

Merges a otter response to to a HttpServletResponse. 
- otter headers to servlet api headers.
- otter cookies to servlet api cookies.
- otter status code to servlet api status code.

## Engine

Delegates to the dispatcher to find the resource that matches the request.
Executes the Between implementation for the before and after of the targeted resource.
Executes the correct method on a resource get, post, put, ..

## Route

A Route conatins:
- pattern - the regex to match for this resource.
- resource - the resource to execute.
- before - the between to execute before the resource is delegated to.
- after - the between to execute after the resource is delegated to.

## MatchedRoute

Returned from the dispatcher. It is a container for a Matcher and a Route.

## Between

The interface that all before and after tasks should implement. They are ivars to a route and are executed in the engine.

## PrepareCSRF

- Sets the CSRF cookie on a otter request
- Assigns the CSRF token value to request.csrfChallenge

## CheckCSRF

- checks that the cookie csrf value matches the form field value.
- throws a HaltException if they dont match.

## Dispatcher

This loops through routes to attempt to find one where a route's pattern matches the request url.
The Dispatcher's routes are assigned in the ServletGateway.
