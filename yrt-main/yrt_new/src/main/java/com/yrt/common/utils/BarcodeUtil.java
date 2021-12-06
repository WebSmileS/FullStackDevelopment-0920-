package com.yrt.common.utils;

import com.yrt.common.exception.ValidateException;
import com.yrt.common.utils.barcode.ElementStrings;
import com.yrt.common.utils.barcode.ElementStrings.ParseResult;

public class BarcodeUtil {

	public static ParseResult parseGS1Barcode(String code) throws ValidateException{
		code = code.replace("(", "");
		code = code.replace(")", "");
		ParseResult result = ElementStrings.parse(code);
		if(result.isPartial()) {
			throw new ValidateException("错误的条码!");
		}
		return result;
	}
}
