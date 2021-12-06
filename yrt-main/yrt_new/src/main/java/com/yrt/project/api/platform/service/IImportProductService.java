package com.yrt.project.api.platform.service;

import java.util.List;
import java.util.concurrent.Future;

import com.yrt.project.modular.product.domain.ImportProduct;

public interface IImportProductService {

	String asyncImportData(List<ImportProduct> productList);
	Future<List<ImportProduct>> fetchImportTask(String id);

}
