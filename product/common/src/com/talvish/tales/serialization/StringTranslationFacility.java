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
package com.talvish.tales.serialization;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.BitSet;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.joda.time.DateTime;

import com.google.common.base.Preconditions;
import com.talvish.tales.businessobjects.ObjectId;
import com.talvish.tales.parts.reflection.JavaType;
import com.talvish.tales.parts.translators.BitSetToStringTranslator;
import com.talvish.tales.parts.translators.BooleanToStringTranslator;
import com.talvish.tales.parts.translators.ByteArrayToStringTranslator;
import com.talvish.tales.parts.translators.EnumToStringTranslator;
import com.talvish.tales.parts.translators.ObjectToStringTranslator;
import com.talvish.tales.parts.translators.StringToBigDecimalTranslator;
import com.talvish.tales.parts.translators.StringToBitSetTranslator;
import com.talvish.tales.parts.translators.StringToBooleanTranslator;
import com.talvish.tales.parts.translators.StringToByteArrayTranslator;
import com.talvish.tales.parts.translators.StringToDateTimeTranslator;
import com.talvish.tales.parts.translators.StringToDoubleTranslator;
import com.talvish.tales.parts.translators.StringToEnumTranslator;
import com.talvish.tales.parts.translators.StringToFloatTranslator;
import com.talvish.tales.parts.translators.StringToIntegerTranslator;
import com.talvish.tales.parts.translators.StringToLocalDateTimeTranslator;
import com.talvish.tales.parts.translators.StringToLocalDateTranslator;
import com.talvish.tales.parts.translators.StringToLongTranslator;
import com.talvish.tales.parts.translators.StringToObjectIdTranslator;
import com.talvish.tales.parts.translators.StringToStringTranslator;
import com.talvish.tales.parts.translators.StringToUuidTranslator;
import com.talvish.tales.parts.translators.StringToOffsetDateTimeTranslator;
import com.talvish.tales.parts.translators.Translator;
import com.talvish.tales.parts.translators.UuidToStringTranslator;
import com.talvish.tales.system.Facility;

/**
 * A helper class for dealing with translating to/from strings in a generic fashion.
 * @author jmolnar
 *
 */
public final class StringTranslationFacility implements Facility {
	private final Map< JavaType, Translator> toStringTranslators = new ConcurrentHashMap<>( 16, 0.75f, 1 );
	private final Map< JavaType, Translator> fromStringTranslators = new ConcurrentHashMap<>( 16, 0.75f, 1 );

	// TODO: if I move JsonTypeReference out and call it SerializedTypeXXX then it could be used here as well
	/**
	 * Default constructor.
	 */
	public StringTranslationFacility( ) {
		// the from string translators . . .
		
		// TODO: why am I using empty string for the null value in the XXXtoStringTranslators?
		
		Translator toIntegerTranslator = new StringToIntegerTranslator( true, null, null );
		Translator fromObjectTranslator = new ObjectToStringTranslator( "" );
		
		this.registerTranslators( new JavaType( Integer.class ), toIntegerTranslator, fromObjectTranslator );
		this.registerTranslators( new JavaType( int.class ), toIntegerTranslator, fromObjectTranslator );

		Translator toLongTranslator = new StringToLongTranslator( true, null, null );
		this.registerTranslators( new JavaType( Long.class ), toLongTranslator, fromObjectTranslator );
		this.registerTranslators( new JavaType( long.class ), toLongTranslator, fromObjectTranslator );

		Translator toFloatTranslator = new StringToFloatTranslator( true, null, null );
		this.registerTranslators( new JavaType( Float.class ), toFloatTranslator, fromObjectTranslator );
		this.registerTranslators( new JavaType( float.class ), toFloatTranslator, fromObjectTranslator );

		Translator toDoubleTranslator = new StringToDoubleTranslator( true, null, null );
		this.registerTranslators( new JavaType( Double.class ), toDoubleTranslator, fromObjectTranslator );
		this.registerTranslators( new JavaType( double.class ), toDoubleTranslator, fromObjectTranslator );

		Translator toBigDecimalTranslator = new StringToBigDecimalTranslator( true, null, null );
		this.registerTranslators( new JavaType( BigDecimal.class ), toBigDecimalTranslator, fromObjectTranslator );

		
		Translator toBooleanTranslator = new StringToBooleanTranslator( true, null, null );
		Translator fromBooleanTranslator = new BooleanToStringTranslator( "" );
		this.registerTranslators( new JavaType( Boolean.class ), toBooleanTranslator, fromBooleanTranslator );
		this.registerTranslators( new JavaType( boolean.class ), toBooleanTranslator, fromBooleanTranslator );

		
		Translator toDateTimeTranslator = new StringToDateTimeTranslator( true, null, null );
		this.registerTranslators( new JavaType( DateTime.class ), toDateTimeTranslator, fromObjectTranslator );

		Translator toOffsetDateTimeTranslator = new StringToOffsetDateTimeTranslator( true,  null,  null );
		this.registerTranslators( new JavaType( OffsetDateTime.class ), toOffsetDateTimeTranslator, fromObjectTranslator );		
		Translator toLocalDateTimeTranslator = new StringToLocalDateTimeTranslator( true,  null,  null );
		this.registerTranslators( new JavaType( LocalDateTime.class ), toLocalDateTimeTranslator, fromObjectTranslator );		
		Translator toLocalDateTranslator = new StringToLocalDateTranslator( true,  null,  null );
		this.registerTranslators( new JavaType( LocalDate.class ), toLocalDateTranslator, fromObjectTranslator );

		
		Translator toStringTranslator = new StringToStringTranslator( true, "", null );
		Translator fromStringTranslator = new StringToStringTranslator( true, "", null );
		this.registerTranslators( new JavaType( String.class ), toStringTranslator, fromStringTranslator );
		
		Translator toUUIDTranslator = new StringToUuidTranslator(true, null, null); // TODO: empty should maybe be an empty UUID?
		Translator fromUUIDTranslator = new UuidToStringTranslator( "" );
		this.registerTranslators( new JavaType( UUID.class ), toUUIDTranslator, fromUUIDTranslator );
		
		Translator toBitSetTranslator = new StringToBitSetTranslator( true, null, null );
		Translator fromBitSetTranslator = new BitSetToStringTranslator( "" );
		this.registerTranslators( new JavaType( BitSet.class ), toBitSetTranslator, fromBitSetTranslator );

		Translator toByteArrayTranslator = new StringToByteArrayTranslator( true, null, null ); // TODO: empty should maybe be an empty array? (though that isn't immutable, so maybe I do something in the class?)
		Translator fromByteArrayTranslator = new ByteArrayToStringTranslator( "" );
		this.registerTranslators( new JavaType( byte[].class ), toByteArrayTranslator, fromByteArrayTranslator );

		Translator toObjectIdTranslator = new StringToObjectIdTranslator( true, null, null );
		this.registerTranslators( new JavaType( ObjectId.class ), toObjectIdTranslator, fromObjectTranslator );		
	}

	/***
	 * This method is used to add translators into the manager for ensuring proper conversion.
	 * @param theType the class to register for
	 * @param theGenericType the generic type information, if it makes sense 
	 * @param fromStringTranslator the from translator 
	 * @param toStringTranslator the to translator
	 */
	public final void registerTranslators( JavaType theType, Translator fromStringTranslator, Translator toStringTranslator ) {
		Preconditions.checkNotNull( theType, "need a type" );
		Preconditions.checkNotNull( fromStringTranslator, "need a from-string  translator" );
		Preconditions.checkNotNull( toStringTranslator, "need a to-string translator" );
		
		fromStringTranslators.put( theType, fromStringTranslator );
		toStringTranslators.put( theType, toStringTranslator );
	}
	
	/**
	 * Gets a translator to translate values from strings to values.
	 * @param theType the type to get a translator for
	 * @param theGenericType the generic type information of the type to get a translator for 
	 * @return the translator, or {@code null} if one was not found
	 */
	public final Translator getFromStringTranslator( JavaType theType ) {
		Preconditions.checkNotNull( theType, "need a type to get a translator");
		Translator translator = fromStringTranslators.get( theType );
		
		if( translator == null && theType.getUnderlyingClass().isEnum( ) ) {
			// enums translators are generated as needed but then saved
			translator = new StringToEnumTranslator( theType.getUnderlyingClass() );
			fromStringTranslators.put( theType, translator );
		}
		return translator;
	}

	/**
	 * Gets a translator to translator values from an object type to a string.
	 * @param theType the type to get a translator for
	 * @param theGenericType the generic type information of the type to get a translator for 
	 * @return the translator, or {@code null} if one was not found
	 */
	public final Translator getToStringTranslator( JavaType theType ) {
		Preconditions.checkNotNull( theType, "need a type to get a translator");
		Translator translator = toStringTranslators.get( theType );
		
		if( translator == null && theType.getUnderlyingClass( ).isEnum( ) ) {
			// enums translators are generated as needed but then saved
			translator = new EnumToStringTranslator( theType.getUnderlyingClass() );
			toStringTranslators.put( theType, translator );
		}
		return translator;
	}
}
