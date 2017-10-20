package com.leanovate.helios;

import javax.baja.alarm.*;

import java.io.IOException;

public class BLeanovateAlarmConsumer implements Runnable
{
  private int childId;
  private ThreadDataShare threadDataShare;

  private static final int OK = 0;
  private static final int ERROR = -1;
  
  private static final int TKT_STATUS_OPEN = 2;
  private static final int TKT_STATUS_PENDING = 3;
  private static final int TKT_STATUS_RESOLVED = 4;
  
  private static final int TKT_PRTY_HIGH = 3;
  
  public BLeanovateAlarmConsumer(int childId, ThreadDataShare threadDataShare, BAlarmRecord bAlarmRecord)
  {
    this.childId = childId;
    this.threadDataShare = threadDataShare;
    this.threadDataShare.alarmObject = bAlarmRecord;
  }

  public static boolean gotoServer = true;
  
  public void run()
  {
    System.out.println("BLeanovateAlarmConsumer child thread with ID: " + this.childId + " starts work now...");    
    
    if (gotoServer)
    {
      int ret = processAlarm(this.threadDataShare.alarmObject);
      if (ret == OK)
        System.out.println("BLeanovateAlarmConsumer: processAlarm OK");
      else
        System.out.println("BLeanovateAlarmConsumer: processAlarm ERROR!");
    }
    else
    {
      // simulate by a delay for testing only
      try
      {
        Thread.sleep(8000);
      }
      
      catch (InterruptedException e)
      {
        System.out.println("InterruptedException");
        threadDataShare.threadFinished = true;
      }
    }
    threadDataShare.threadFinished = true;
    System.out.println("BLeanovateAlarmConsumer child thread with ID: " + this.childId + " completed work now...");    
  }
  
  private int processAlarm(BAlarmRecord bAlarmRecord)
  {
    BLeanovateTicketHandler ticketHandler = new BLeanovateTicketHandler();
    
    if (bAlarmRecord.getSourceState().getOrdinal() == BSourceState.NORMAL)
    {
      System.out.println("LeanovateHeliosRx: HndlAlm: " + "Normal State. Resolve Ticket...");
      
      // find if ticket was created earlier when alarm was raised, for this point name. if yes, update the
      // ticket to RESOLVED. unique key to search tkts is pointName
      try
      {
          String pointName = bAlarmRecord.getSource().encodeToString();
          int tktId = ticketHandler.searchOpenTicket(pointName);
          if (tktId != ERROR)
          {
            //  ticket exists. update ticket with status=resolved
            System.out.println("LeanovateHeliosRx: HndlAlm: Open Ticket exists with" + "ID:" 
                  + tktId );
  
            System.out.println("LeanovateHeliosRx: HndlAlm: Tkt Update.. " + "  retCode:" 
                  + tktId + "  point-name: " + pointName + 
                  "  status to: " + TKT_STATUS_RESOLVED);
            int ret = ticketHandler.updateTicket(tktId, pointName, TKT_STATUS_RESOLVED);
            if (ret == ERROR)
            {
              System.out.println("LeanovateHeliosRx: HndlAlm: Tkt Update Error!!.. retCode: " 
                    + ret);
            }
            else            
            {
              System.out.println("LeanovateHeliosRx: HndlAlm: Tkt Update Success!!.. retCode: " 
                    + ret);
            }
          }
          else
          {
            System.out.println("LeanovateHeliosRx: HndlAlm: Ticket doesnt exit.  " +
                "Abnormal condition!.. " + "tktId:" + tktId + 
                "  point-name: " + pointName);
            // ticket for this point is not present now. either it was not created when alarm happennend, or 
            // has already been resolved or deleted in ticketing system. log and move on. nothing to do. 
            // ideally this should not happen
            return ERROR;
          }
      }
      catch (IOException e)
      {
        System.out.println("LeanovateHeliosRx: sendGET IOException!!");
        return ERROR;
      }
    }
    else
    {
      // alarm state! -> check and create ticket if not existing
      System.out.println("LeanovateHeliosRx: HndlAlm: " + "Alarm State!!. Create Ticket... " +
          "point-name: " + bAlarmRecord.getSource().encodeToString());
      try
      {
          // search if a tkt already exists for this pointname. happens when normal->alarm->fault happens for example
          String pointName = bAlarmRecord.getSource().encodeToString();
          int tktId = ticketHandler.searchOpenTicket(pointName);
          if (tktId != ERROR)
          {
            //  ticket exists. dont do anything as point is in alarm still and tkt exists.
            System.out.println("LeanovateHeliosRx: HndlAlm: Open Ticket exists with" + "ID:" 
                  + tktId  + "  point-name: " + pointName +
                  "\nDoing nothing as ticket exists");
            return ERROR;
          }
  
          // no tkt exists for this point name. create one.
          // create ticket with unique ID as pointName (TODO: confirm if this is unique in setup)
          int result = ticketHandler.createTicket(bAlarmRecord.getSource().encodeToString(), 
                                    TKT_PRTY_HIGH);
          if (result != OK)
          {
            System.out.println("LeanovateHeliosRx: sendPOST: Ticket Create Error!! " +
                "point-name: " + bAlarmRecord.getSource().encodeToString());
            return ERROR;
          }
      }
      catch (IOException e)
      {
        System.out.println("LeanovateHeliosRx: sendPOST: IOException!!");
        return ERROR;
      }             
      
    }
    return OK;
  }  
}
