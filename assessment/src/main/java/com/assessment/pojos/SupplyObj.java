package com.assessment.pojos;

import lombok.*;

import java.util.Date;

@Getter @Setter @ToString @AllArgsConstructor @NoArgsConstructor
public class SupplyObj {
    String productid;

    Date updateTimeStamp;
    Double availQty;
    String status;
}
