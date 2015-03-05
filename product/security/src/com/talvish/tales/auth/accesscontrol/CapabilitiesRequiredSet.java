// ***************************************************************************
// *  Copyright 2015 Joseph Molnar
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
package com.talvish.tales.auth.accesscontrol;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * The annotation that allows multiple <code>CapabilitiesRequired</code> annotations
 * to exist on a method.
 * @author jmolnar
 *
 */
@Retention( RetentionPolicy.RUNTIME)
@Target( ElementType.METHOD )
public @interface CapabilitiesRequiredSet {
	/**
	 * The <code>CapabilitiesRequired</code> annotations on the method.
	 * @return the collection annotations
	 */
	CapabilitiesRequired[] value( );
}
