global without sharing  class SmsWS {
    WebService static String sendSMSFromAccount(String accountId, String message){
        Account[] accounts = [select Server__c, Name, Phone from Account where Id=:accountId];
        Account account = accounts!=null?accounts[0]:null;
        String phone = AccountUtils.getPhone(account);
        if (phone == null){
            return 'You must have a Phone Number in Primary Contact';
        }
        SMSUtils.sendAysncSMS(phone,message,'');
        return 'Sent';
    }

    WebService static String sendSMSFromCase(String caseId, String message){
        Case[] cases = [select Contact.MobilePhone, Contact.Phone from Case where Id=:caseId];
        Case myCase = cases!=null?cases[0]:null;
        Contact contact = myCase.Contact;
        String phone = contact.MobilePhone;
        if (phone == null){
            phone = contact.Phone;
        }
        if (phone == null){
            return 'You must have a Phone Number in Contact';
        }


        Task newTask = new Task();
        newTask.OwnerId = Userinfo.getUserId();
        newTask.Status = 'Completed';
        newTask.Subject = 'SMS to number ' + phone;
        newTask.Priority = 'Medium';
        newTask.Type = 'Text Message';
        newTask.ActivityDate = Date.today();
        newTask.WhoId = contact.Id;
        newTask.WhatId = myCase.Id;
        newTask.Description = message;
        insert newTask;
        System.debug('newTask: ' + newTask.Id);

        SMSUtils.sendAysncSMS(phone,message,newTask.Id);
        return 'Sent';
    }

    WebService static String sendSMSFromLead(String leadId, String message){
        System.debug('sendSMSFromLead: ' + leadId);
        Lead[] leads = [select MobilePhone, Phone from Lead where Id=:leadId];
        Lead myLead = leads!=null?leads[0]:null;
        String phone = myLead.MobilePhone;
        if (phone == null){
            phone = myLead.Phone;
        }
        if (phone == null){
            return 'You must have a Phone Number in Lead';
        }


        Task newTask = new Task();
        newTask.OwnerId = Userinfo.getUserId();
        newTask.Status = 'Completed';
        newTask.Subject = 'SMS to number ' + phone;
        newTask.Priority = 'Medium';
        newTask.Type = 'Text Message';
        newTask.ActivityDate = Date.today();
        newTask.WhoId = myLead.Id;
        newTask.Description = message;
        insert newTask;
        System.debug('newTask: ' + newTask.Id);

        SMSUtils.sendAysncSMS(phone,message,newTask.Id);
        return 'Sent';
    }

}