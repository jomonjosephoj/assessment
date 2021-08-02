package com.assessment.controllers;

import com.assessment.exception.CustomException;
import com.assessment.pojos.InventoryObj;
import com.assessment.pojos.SupplyObj;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.extern.log4j.Log4j2;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@Log4j2
public class AssessmentController {

    @Autowired
    ObjectMapper om;

    private final String dateFormat = "yyyy-MM-dd";

    private final SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);

    private final String supplyDateFormatStr = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";

    private final SimpleDateFormat supplyDateFormat = new SimpleDateFormat(supplyDateFormatStr);

    //current date 2021-02-19.
    @PostMapping("getinvPicture")
    public String getInvPicture(@RequestBody String req) throws JsonProcessingException, ParseException {
        JSONObject jo = (JSONObject) JSONValue.parse(req);
        log.info("Input Str" + req);
        String productId = jo.get("productId").toString();
        String prodName = jo.get("prodName").toString();
        String reqDateStr = jo.get("reqDate").toString();
        Date reqDate = sdf.parse(reqDateStr);
        List<InventoryObj> invList = getInventory(productId.trim());
        Date maxDate = sdf.parse("2021-03-30");
        Date minDate = sdf.parse("2021-03-19");
        if(reqDate.after(maxDate)||reqDate.before(minDate)){
            throw new CustomException("Date Outside the Allowed Range");
        }
        Calendar endDate = new GregorianCalendar();
        endDate.setTime(reqDate);
        endDate.add(Calendar.DATE,10);
        Map<String,Double> availability = new HashMap<>();
        if(invList!=null){
            availability.put("value" , 0.0);
            invList.stream().filter(inv ->
                    (inv.getAvailDate().before(endDate.getTime())||inv.getAvailDate().equals(endDate))).
                    forEach(inv ->availability.put("value",availability.get("value") + inv.getAvailQty()));
        }
        JSONObject outJson = new JSONObject();
        outJson.put("productId",productId);
        outJson.put("prodName",prodName);
        outJson.put("availQty",availability.get("value"));
        return outJson.toString();
    }

    public List<InventoryObj> getInventory(String productId) throws ParseException {

        Map<String,List<InventoryObj>> dataList = new HashMap<>();
        List<InventoryObj> inv = new ArrayList<>();
        InventoryObj obj1 = new InventoryObj("Prod1","Shirt","EACH", 10.0, sdf.parse("2021-03-19"));
        InventoryObj obj2 = new InventoryObj("Prod1","Shirt","EACH", 20.0, sdf.parse("2021-03-21"));
        InventoryObj obj3= new InventoryObj("Prod1","Shirt","EACH", 20.0, sdf.parse("2021-03-29"));
        inv.add(obj1);
        inv.add(obj2);
        inv.add(obj3);
        dataList.put("Prod1",inv);
        return dataList.get(productId);
    }

    //current date 2021-02-19.
    @PostMapping("updateSupply")
    public SupplyObj updateSupply(@RequestBody String req) throws JsonProcessingException, ParseException {
        JSONObject jo = (JSONObject) JSONValue.parse(req);
        log.info("Input Str" + req);
        String productId = jo.get("productId").toString();
        String quantityStr = jo.get("quantity").toString();
        String reqDateStr = jo.get("updateTimeStamp").toString();
       // supplyDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date reqDate = supplyDateFormat.parse(reqDateStr);
        Double qty = Double.parseDouble(quantityStr);
        SupplyObj sup = getSupplyObject(productId);
        SupplyObj out = new SupplyObj();
        if(sup!=null){
            log.info("sup.getUpdateTimeStamp() " + sup.getUpdateTimeStamp());
            log.info("reqDate "+ reqDate);
            if(sup.getUpdateTimeStamp().before(reqDate)){
                out.setProductid(productId);
                out.setAvailQty(sup.getAvailQty());
                out.setUpdateTimeStamp(sup.getUpdateTimeStamp());
                out.setStatus("Out Of Sync Update");
            } else {
                out.setProductid(productId);
                out.setAvailQty(sup.getAvailQty()+qty);
                out.setUpdateTimeStamp(reqDate);
                out.setStatus("Updated");
            }
        }
        return out;
    }


    public SupplyObj getSupplyObject(String productid) throws ParseException {
      //  supplyDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        Map<String,SupplyObj> map = new HashMap<>();
        SupplyObj supplyObj1 = new SupplyObj("Product1",supplyDateFormat.parse("2021-03-16T08:53:48.616Z"),10.0,"");
        map.put("Product1",supplyObj1);
        SupplyObj supplyObj2 = new SupplyObj("Product2",supplyDateFormat.parse("2021-03-16T08:59:48.616Z"),5.0,"");
        map.put("Product2",supplyObj2);
        SupplyObj supplyObj3 = new SupplyObj("Product3",supplyDateFormat.parse("2021-03-16T09:10:48.616Z"),30.0,"");
        map.put("Product3",supplyObj3);
        SupplyObj supplyObj4 = new SupplyObj("Product4",supplyDateFormat.parse("2021-03-16T09:10:48.616Z"),2.0,"");
        map.put("Product4",supplyObj4);
        return map.get(productid);
    }
}
