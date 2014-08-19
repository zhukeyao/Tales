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
package com.tales.samples.websiteservice;

import com.tales.contracts.services.http.RequestParam;
import com.tales.contracts.services.http.ResourceContract;
import com.tales.contracts.services.http.ResourceOperation;
import com.tales.contracts.services.http.ResourceResult;
import com.tales.services.Status;

/***
 * This a very simple contract with two operations. One that will
 * dump 'hello world' and the other demonstrating a query strong or 
 * post body parameter being echoed back.
 * @author Joseph Molnar
 *
 */
@ResourceContract( name="com.tales.simple_contract", versions={ "20140124" } )
public class SimpleResource {
	/**
	 * An HTTP GET operation that simple returns the string 'hello world'.
	 */
	@ResourceOperation( name="hello_world", path="GET : hello" )
	public ResourceResult<String> hello( ) {
		ResourceResult<String> result = new ResourceResult<String>();
		result.setResult( "hello world", Status.OPERATION_COMPLETED );
		result.addHeader( "Access-Control-Allow-Origin", "*" );
		
		// TODO: make it so we can not have to do this origin setting thing
		//       but instead make it so it validates 
		//       look here: http://stackoverflow.com/questions/1653308/access-control-allow-origin-multiple-origin-domains
		return result;
	}
	
	/**
	 * An operation that can be run using an HTTP GET or HTTP POST.
	 * It requires a query string or post body parameter called 'value' 
	 * and the value sent in will be echo back in the response.
	 * @param theValue
	 * @return
	 */
	@ResourceOperation( name="echo", path="GET | POST : echo")
	public ResourceResult<String> echo( @RequestParam( name="value" )String theValue ) {
		ResourceResult<String> result = new ResourceResult<String>();
		result.setResult( theValue, Status.OPERATION_COMPLETED );
		result.addHeader( "Access-Control-Allow-Origin", "*" );
		
		// TODO: make it so we can not have to do this origin setting thing
		//       but instead make it so it validates 
		//       look here: http://stackoverflow.com/questions/1653308/access-control-allow-origin-multiple-origin-domains
		return result;
	}
}
