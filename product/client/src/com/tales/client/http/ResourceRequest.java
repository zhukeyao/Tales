// ***************************************************************************
// *  Copyright 2014 Joseph Molnar
// *
// *  Licensed under the Apache License, Version 2.0 (the "License");
// *  you may not use this file except in compliance with the License.
// *  You may obtain a copy of the License at
// *
// *      http://www.apache.org/licenses/LICENSE-2.0
// *
// *  Unless required by applicable law or agreed to in writing, software
// *  distributed under the License is distributed on an "AS IS" BASIS,
// *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// *  See the License for the specific language governing permissions and
// *  limitations under the License.
// ***************************************************************************
package com.tales.client.http;

import java.net.HttpCookie;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.client.api.Request;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.tales.communication.CommunicationException;
import com.tales.parts.translators.TranslationException;
import com.tales.serialization.UrlEncoding;

/**
 * The class representing a request to a Tales-enabled service.
 * These objects are meant to last the life of the call of a request.
 * This class is generated by using a ResourceClient.createRequest method.
 * @author jmolnar
 *
 */
public class ResourceRequest {
	private static final Logger logger = LoggerFactory.getLogger( ResourceRequest.class );
	
	private final ResourceClient client;
	private final ResourceMethod method;
	private final Request request;
	private final Object[] pathParameters;
	private final Map<String,String> bodyParameters;
	
	/**
	 * Constructor called by the ResourceClient to indicate a request is going to be tempted.
	 * @param theClient the ResourceClient that is created the request
	 * @param theMethod the ResourceMethod that is being called
	 * @param thePathParameters the path parameters needed to make the request
	 */
	protected ResourceRequest( ResourceClient theClient, ResourceMethod theMethod, Object ... thePathParameters ) {
		Preconditions.checkNotNull( theClient, "theClient" );
		Preconditions.checkNotNull( theMethod, "theMethod" );
		Preconditions.checkArgument( ( ( thePathParameters == null || thePathParameters.length == 0 ) && theMethod.getPathParameters().size( ) == 0 ) || thePathParameters.length == theMethod.getPathParameters().size(), "the number of path parameters give doesn't match the number that were defined");
		
		client = theClient;
		method = theMethod;
		
		// setup the path parameters
		if( thePathParameters != null ) {
			pathParameters = new Object[ thePathParameters.length ];
			
			// for the path parameters I need to 
			ResourceMethodParameter parameter;
			for( int index = 0; index < thePathParameters.length; index += 1 ) {
				parameter = theMethod.getPathParameters( ).get( index );
				// we are URL encoding the path parameter here instead of relying on it happening in the translator
				pathParameters[ index ] = UrlEncoding.encode( ( String )parameter.getTranslator().translate( thePathParameters[ index ] ) ); 
			}
		} else {
			pathParameters = null;
		}
		
		// setup the storage for body parameters
		bodyParameters = new HashMap<String, String>( theMethod.getBodyParameters().size( ) );
		
		// setup the underlying jetty HTTP client
		request = client.getHttpClient()
		.newRequest( String.format( method.getMethodUrl(), pathParameters ) )
		.method( method.getHttpVerb().getValue() ); 
	}

	/**
	 * Sets the data to use for a particular query string parameter. 
	 * @param theName the name of the parameter
	 * @param theValue the value to to use (not yet translated)
	 * @return the request, to make it easy to chain a set of calls like this together
	 */
	public ResourceRequest setQueryParameter( String theName, Object theValue ) {
		ResourceMethodParameter parameter = method.getQueryParameters( ).get( theName );
		// check if we got something (which will also verify we don't have a null name)
		Preconditions.checkNotNull( parameter, "Parameter '%s' is either missing or not set.", theName );
		// then set the value
		request.param( theName, ( String )parameter.getTranslator().translate( theValue ) );
		return this;
	}


	/**
	 * Sets the data to use for a particular post body parameter. 
	 * @param theName the name of the parameter
	 * @param theValue the value to to use (not yet translated)
	 * @return the request, to make it easy to chain a set of calls like this together
	 */
	public ResourceRequest setBodyParameter( String theName, Object theValue ) {
		ResourceMethodParameter parameter = method.getBodyParameters( ).get( theName );
		// check if we got something (which will also verify we don't have a null name)
		Preconditions.checkNotNull( parameter, "Parameter '%s' is either missing or not set.", theName );
		// then set the value (for storage at least)
		this.bodyParameters.put( theName, ( String )parameter.getTranslator().translate( theValue ) );
		return this;
	}
	
	/**
	 * Sets the data to use for a particular cookie.
	 * The cookie passed in is used to define path, age, etc, while the value
	 * it stores will be over written the the value object passed in 
	 * @param theCookie the cookie value to use and set
	 * @param theValue the value to set the on the cookie (which will override the current cookies value)
	 * @return the request, to make it easy to chain a set of calls like this together
	 */
	public ResourceRequest setCookieParameter( HttpCookie theCookie, Object theValue ) {
		Preconditions.checkNotNull( theCookie, "cookie not given to set" );
		ResourceMethodParameter parameter = method.getCookieParameters( ).get( theCookie.getName( ) );
		// check if we got something (which will also verify we don't have a null name)
		Preconditions.checkNotNull( parameter, "Parameter '%s' is either missing or not set.", theCookie.getName() );
		// resets the value
		theCookie.setValue( ( String )parameter.getTranslator().translate( theValue ) );
		request.cookie( theCookie );
		return this;
	}

	/**
	 * Sets the data to use for a particular cookie. 
	 * @param theName the name of the cookie
	 * @param theValue the value to to use (not yet translated)
	 * @return the request, to make it easy to chain a set of calls like this together
	 */
	public ResourceRequest setCookieParameter( String theName, Object theValue ) {
		ResourceMethodParameter parameter = method.getCookieParameters( ).get( theName );
		// check if we got something (which will also verify we don't have a null name)
		Preconditions.checkNotNull( parameter, "Parameter '%s' is either missing or not set.", theName );
		// then set the value
		request.cookie( new HttpCookie( theName, ( String )parameter.getTranslator().translate( theValue ) ) ); 
		return this;
	}

	/**
	 * Sets the data to use for a particular header 
	 * @param theName the name of the header 
	 * @param theValue the value to to use (not yet translated)
	 * @return the request, to make it easy to chain a set of calls like this together
	 */
	public ResourceRequest setHeaderParameter( String theName, Object theValue ) {
		ResourceMethodParameter parameter = method.getHeaderParameters( ).get( theName );
		// check if we got something (which will also verify we don't have a null name)
		Preconditions.checkNotNull( parameter, "Parameter '%s' is either missing or not set.", theName );
		// then set the value
		request.header( theName, ( String )parameter.getTranslator().translate( theValue ) );
		return this;
	}
	
	/**
	 * This method is called to perform the actual request and return the server response.
	 * @return returns a structure containing the exact response from the service
	 * @throws InterruptedException this occurs if the the request is interrupted
	 */
	@SuppressWarnings("unchecked")
	public <T> ResourceResult<T> execute( ) throws InterruptedException {
		logger.info( 
				"Executing, synchronously, resource method '{}' from contract '{}'.", new Object[] {
						this.method.getName(),
						this.client.contractRoot } );
		long startTimestamp = System.nanoTime(); 
		ContentResponse response;
		try {
			// check to see if we have
			if( this.bodyParameters.size() > 0 ) {
				// create the content provider with the body parameters
				request.content( new BodyContentProvider( this.bodyParameters ) );
			}
			
			response = request.send( );

			// grab the response as a string, it should all be json, so let's interpret
			JsonElement jsonResult = client.jsonParser.parse( response.getContentAsString() );
			// now we need to convert what was returned as a result object ... BUT ..
			ResourceResult<T> objectResult = ( ResourceResult<T> )client.getResultType().getFromJsonTranslator().translate( jsonResult );
			// the actual result is not interpreted since we don't the type at registration time so we deal with the result
			// value separately
			JsonElement jsonReturn = jsonResult.getAsJsonObject().get( "return" );
			// we need to make sure we have a result and if not then we 
			// assume we didn't get a response
			if( jsonReturn != null )  {
				objectResult.setResult( ( T )client.getJsonFacility().fromJsonElement( jsonReturn, method.getReturnType(), null ) );
			} else {
				objectResult.setResult( null );
			}
	
			return objectResult;		

		} catch( JsonParseException e ) {
			throw new CommunicationException( String.format( "Invalid json response from '%s'.", this.method.getMethodUrl( ) ), e );
			
		} catch( TranslationException e ) {
			throw new CommunicationException( String.format( "Unexpected data while converting response from '%s'. Verify defined parameter and return types match what is sent on the wire.", this.method.getMethodUrl( ) ), e );
			
		} catch( TimeoutException e ) {
			throw new CommunicationException( String.format( "Timed out while communicating with '%s'.", e.getClass().getSimpleName(), this.method.getMethodUrl( ) ), e );
			
		} catch( ExecutionException e ) {
			Throwable cause = e.getCause();
			if( cause != null ) {
				throw new CommunicationException( 
						String.format( "A problem of type '%s' with message '%s' occurred while communicating with '%s'.", 
								cause.getClass().getSimpleName(), 
								cause.getMessage(), 
								this.method.getMethodUrl( ) ),
						e);
			} else {
				throw new CommunicationException( 
						String.format( "For some unknown reason, could not communicate with '%s'.",
								this.method.getMethodUrl( ) ), 
						e );
			}
		} finally {
			// status block handling would go here
			long executionTime = System.nanoTime( ) - startTimestamp;
			logger.info( 
					"Executed, synchronously, resource method '{}' from contract '{}' in {} ms.", new Object[] {
							this.method.getName(),
							this.client.contractRoot,
							( ( double )executionTime ) * 0.000001 } );
		}
	}
}
