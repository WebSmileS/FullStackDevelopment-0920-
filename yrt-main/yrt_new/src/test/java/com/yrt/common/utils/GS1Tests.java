/*
 * Copyright 2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.yrt.common.utils;

import org.junit.Test;

import com.yrt.common.utils.barcode.ApplicationIdentifier;
import com.yrt.common.utils.barcode.ElementStrings;

public class GS1Tests {

    @Test
    public void testLengthConstant() {
//    	ElementStrings.ParseResult result = ElementStrings.parse("0106934635793300112102071741020121FCC03500017");
//    	ElementStrings.ParseResult result = ElementStrings.parse("01069346357632281120052010FAK0674");
    	ElementStrings.ParseResult result = BarcodeUtil.parseGS1Barcode("(01)06934635763228(11)200520(10)FAK0674");
    	System.out.println(result.getString(ApplicationIdentifier.GTIN));
    	System.out.println(result.getDate(ApplicationIdentifier.PRODUCTION_DATE));
    	System.out.println(result.getDate(ApplicationIdentifier.EXPIRATION_DATE));
    	System.out.println(result.getString(ApplicationIdentifier.BATCH_OR_LOT_NUMBER));
    	System.out.println(result.getString(ApplicationIdentifier.SERIAL_NUMBER));
    }
}
