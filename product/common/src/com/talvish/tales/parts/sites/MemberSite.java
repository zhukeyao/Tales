// ***************************************************************************
// *  Copyright 2012 Joseph Molnar
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
package com.talvish.tales.parts.sites;

import java.lang.annotation.Annotation;

/**
 * {@link DataSite} extension that targets members of a class.
 * @author jmolnar
 *
 */
public interface MemberSite extends DataSite {
	/**
	 * The class that contains the member this site targets
	 * @return the class containing the member
	 */
	Class<?> getContainingType( ); // TODO: I do not like this being a class
	
	/**
	 * The name of the member this site targets.
	 * @return the name of the member
	 */
	String getName( );

	/**
     * Returns the annotation on the field for the specified type.
     * @param theAnnotationClass the class of the annotation to get
     * @return the annotation or null if it doesn't exist
     */
    <A extends Annotation> A getAnnotation( Class<A> theAnnotationClass );
}
