package com.assessment.pojos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

@Getter @Setter @ToString @AllArgsConstructor
public class InventoryObj {
    String productid;
    String prodName;
    String UOM;
    Double availQty;
    Date availDate;
}
