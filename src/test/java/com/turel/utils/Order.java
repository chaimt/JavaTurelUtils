package com.turel.utils;

import java.util.Date;

/**
 * @author chaim
 * @since Jul 12, 2015
 */
public class Order {

	public static String FIELD_Id ="Id";
	private String Id;//id in salesforce
	public static String FIELD_AccountId ="AccountId";
	private String AccountId;
	private String RecordTypeId;
	public static String FIELD_Payment_Status__c ="Payment_Status__c";
	private String Payment_Status__c;
	private String Order_Status__c;
	public static String FIELD_Customer_Order_Number__c ="Customer_Order_Number__c";
	private Double Customer_Order_Number__c;//id from woocommerce
	private String Payment_Type__c;
	private Date Order_Date_and_Time__c;
	private String Status;//Draft/Activated - can't be changed
	private Date EffectiveDate;
	public static String FIELD_EffectiveDate = "EffectiveDate";
	private String Payment_Failure_Reason__c;
	private String Pricebook2Id;
	private String Order_Creator__c;
	private String Issue__c;
	private String Resolution__c;	
	private String Shipping_Method__c;
	private String Shipping_Company__c;
	private String NJ_Notes__c;

	private String billingCountry;
	private String billingStreet;
	private String billingCity;
	private String billingState;
	private String billingPostalCode;
	 
	private String shippingCountry;
	private String shippingStreet;
	private String shippingCity;
	private String shippingState;
	private String shippingPostalCode;
	
	private String Customer_Name__c;
	private String Customer_Phone__c;
	private String Customer_Email__c;
	
	private Date ETA__c;
	private Double Get_Eta_Retries__c;
	private String Tracking_Number_Error__c;
	private String Tracking_Number__c;
	
	private String Return_Shipping_Method__c;
	private Date Return_ETA__c;
	private String Return_Tracking_Number__c;
	private String Return_Tracking_Number_Error__c;
	private Date Deferred_Payment_Date__c;
	private Double Actual_Payment_Amount__c;
	private Date Actual_Payment_Date__c;
		
	public Order() {}

	public String getId() {
		return Id;
	}

	public void setId(String id) {
		Id = id;
	}

	public String getAccountId() {
		return AccountId;
	}

	public void setAccountId(String accountId) {
		AccountId = accountId;
	}

	public String getRecordTypeId() {
		return RecordTypeId;
	}

	public void setRecordTypeId(String recordTypeId) {
		RecordTypeId = recordTypeId;
	}

	public String getPayment_Status__c() {
		return Payment_Status__c;
	}

	public void setPayment_Status__c(String payment_Status__c) {
		Payment_Status__c = payment_Status__c;
	}

	public String getOrder_Status__c() {
		return Order_Status__c;
	}

	public void setOrder_Status__c(String order_Status__c) {
		Order_Status__c = order_Status__c;
	}

	public Double getCustomer_Order_Number__c() {
		return Customer_Order_Number__c;
	}

	public void setCustomer_Order_Number__c(Double customer_Order_Number__c) {
		Customer_Order_Number__c = customer_Order_Number__c;
	}

	public String getPayment_Type__c() {
		return Payment_Type__c;
	}

	public void setPayment_Type__c(String payment_Type__c) {
		Payment_Type__c = payment_Type__c;
	}

	public Date getOrder_Date_and_Time__c() {
		return Order_Date_and_Time__c;
	}

	public void setOrder_Date_and_Time__c(Date order_Date_and_Time__c) {
		Order_Date_and_Time__c = order_Date_and_Time__c;
	}

	public String getStatus() {
		return Status;
	}

	public void setStatus(String status) {
		Status = status;
	}

	public Date getEffectiveDate() {
		return EffectiveDate;
	}

	public void setEffectiveDate(Date effectiveDate) {
		EffectiveDate = effectiveDate;
	}

	public String getPayment_Failure_Reason__c() {
		return Payment_Failure_Reason__c;
	}

	public void setPayment_Failure_Reason__c(String payment_Failure_Reason__c) {
		Payment_Failure_Reason__c = payment_Failure_Reason__c;
	}

	public String getPricebook2Id() {
		return Pricebook2Id;
	}

	public void setPricebook2Id(String pricebook2Id) {
		Pricebook2Id = pricebook2Id;
	}

	public String getOrder_Creator__c() {
		return Order_Creator__c;
	}

	public void setOrder_Creator__c(String order_Creator__c) {
		Order_Creator__c = order_Creator__c;
	}

	public String getIssue__c() {
		return Issue__c;
	}

	public void setIssue__c(String issue__c) {
		Issue__c = issue__c;
	}

	public String getResolution__c() {
		return Resolution__c;
	}

	public void setResolution__c(String resolution__c) {
		Resolution__c = resolution__c;
	}

	public String getBillingCountry() {
		return billingCountry;
	}

	public void setBillingCountry(String billingCountry) {
		this.billingCountry = billingCountry;
	}

	public String getBillingCity() {
		return billingCity;
	}

	public void setBillingCity(String billingCity) {
		this.billingCity = billingCity;
	}

	public String getBillingState() {
		return billingState;
	}

	public void setBillingState(String billingState) {
		this.billingState = billingState;
	}

	public String getShippingCountry() {
		return shippingCountry;
	}

	public void setShippingCountry(String shippingCountry) {
		this.shippingCountry = shippingCountry;
	}

	public String getShippingCity() {
		return shippingCity;
	}

	public void setShippingCity(String shippingCity) {
		this.shippingCity = shippingCity;
	}

	public String getShippingState() {
		return shippingState;
	}

	public void setShippingState(String shippingState) {
		this.shippingState = shippingState;
	}

	public String getShipping_Method__c() {
		return Shipping_Method__c;
	}

	public void setShipping_Method__c(String shipping_Method__c) {
		Shipping_Method__c = shipping_Method__c;
	}

	public String getShipping_Company__c() {
		return Shipping_Company__c;
	}

	public void setShipping_Company__c(String shipping_Company__c) {
		Shipping_Company__c = shipping_Company__c;
	}
	
	public String getNJ_Notes__c() {
		return NJ_Notes__c;
	}
	
	public void setNJ_Notes__c(String nJ_Notes__c) {
		NJ_Notes__c = nJ_Notes__c;
	}

	public String getBillingStreet() {
		return billingStreet;
	}

	public void setBillingStreet(String billingStreet) {
		this.billingStreet = billingStreet;
	}

	public String getBillingPostalCode() {
		return billingPostalCode;
	}

	public void setBillingPostalCode(String billingPostalCode) {
		this.billingPostalCode = billingPostalCode;
	}

	public String getShippingStreet() {
		return shippingStreet;
	}

	public void setShippingStreet(String shippingStreet) {
		this.shippingStreet = shippingStreet;
	}

	public String getShippingPostalCode() {
		return shippingPostalCode;
	}

	public void setShippingPostalCode(String shippingPostalCode) {
		this.shippingPostalCode = shippingPostalCode;
	}

	public Date getETA__c() {
		return ETA__c;
	}

	public void setETA__c(Date eTA__c) {
		ETA__c = eTA__c;
	}

	public Double getGet_Eta_Retries__c() {
		return Get_Eta_Retries__c;
	}

	public void setGet_Eta_Retries__c(double get_Eta_Retries__c) {
		Get_Eta_Retries__c = get_Eta_Retries__c;
	}

	public String getTracking_Number_Error__c() {
		return Tracking_Number_Error__c;
	}

	public void setTracking_Number_Error__c(String tracking_Number_Error__c) {
		Tracking_Number_Error__c = tracking_Number_Error__c;
	}

	public String getTracking_Number__c() {
		return Tracking_Number__c;
	}

	public void setTracking_Number__c(String tracking_Number__c) {
		Tracking_Number__c = tracking_Number__c;
	}

	public String getReturn_Shipping_Method__c() {
		return Return_Shipping_Method__c;
	}

	public void setReturn_Shipping_Method__c(String return_Shipping_Method__c) {
		Return_Shipping_Method__c = return_Shipping_Method__c;
	}

	public Date getReturn_ETA__c() {
		return Return_ETA__c;
	}

	public void setReturn_ETA__c(Date return_ETA__c) {
		Return_ETA__c = return_ETA__c;
	}

	public String getReturn_Tracking_Number__c() {
		return Return_Tracking_Number__c;
	}

	public void setReturn_Tracking_Number__c(String return_Tracking_Number__c) {
		Return_Tracking_Number__c = return_Tracking_Number__c;
	}

	public String getReturn_Tracking_Number_Error__c() {
		return Return_Tracking_Number_Error__c;
	}

	public void setReturn_Tracking_Number_Error__c(String return_Tracking_Number_Error__c) {
		Return_Tracking_Number_Error__c = return_Tracking_Number_Error__c;
	}

	public Date getDeferred_Payment_Date__c() {
		return Deferred_Payment_Date__c;
	}

	public void setDeferred_Payment_Date__c(Date deferred_Payment_Date__c) {
		Deferred_Payment_Date__c = deferred_Payment_Date__c;
	}

	public String getCustomer_Name__c() {
		return Customer_Name__c;
	}
	
	public void setCustomer_Name__c(String customer_Name__c) {
		Customer_Name__c = customer_Name__c;
	}

	public String getCustomer_Phone__c() {
		return Customer_Phone__c;
	}

	public void setCustomer_Phone__c(String customer_Phone__c) {
		Customer_Phone__c = customer_Phone__c;
	}

	public String getCustomer_Email__c() {
		return Customer_Email__c;
	}

	public void setCustomer_Email__c(String customer_Email__c) {
		Customer_Email__c = customer_Email__c;
	}

	public Double getActual_Payment_Amount__c() {
		return Actual_Payment_Amount__c;
	}

	public void setActual_Payment_Amount__c(Double actual_Payment_Amount__c) {
		Actual_Payment_Amount__c = actual_Payment_Amount__c;
	}

	public Date getActual_Payment_Date__c() {
		return Actual_Payment_Date__c;
	}

	public void setActual_Payment_Date__c(Date actual_Payment_Date__c) {
		Actual_Payment_Date__c = actual_Payment_Date__c;
	}
	
	

}