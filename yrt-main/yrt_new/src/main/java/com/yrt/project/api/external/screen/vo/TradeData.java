package com.yrt.project.api.external.screen.vo;

import com.yrt.project.api.external.screen.dto.ProductTrade;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TradeData {
    private String type;
    private List<ProductTrade> list;
}
