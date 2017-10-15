package com.leanovate.helios;

import javax.baja.alarm.*;

import javax.baja.nre.annotations.NiagaraType;
import javax.baja.sys.Sys;
import javax.baja.sys.Type;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import org.json.JSONObject;
import org.json.JSONException;



/**
 * Created by e283233 on 10/4/2017.
 */
@NiagaraType
public class BMyRecipient extends BAlarmRecipient {
/*+ ------------ BEGIN BAJA AUTO GENERATED CODE ------------ +*/
/*@ $com.abc.test1.BMyRecipient(2979906276)1.0$ @*/
/* Generated Wed Oct 04 15:12:14 IST 2017 by Slot-o-Matic (c) Tridium, Inc. 2012 */

////////////////////////////////////////////////////////////////
// Type
////////////////////////////////////////////////////////////////
  
  @Override
  public Type getType() { return TYPE; }

  public static final Type TYPE = Sys.loadType(BMyRecipient.class);

/*+ ------------ END BAJA AUTO GENERATED CODE -------------- +*/

  private static final String USER_AGENT = "niagara-leanovate/1.0";

  private static final String USERNAME_PASSWD = "kragha2000@gmail.com:123surya";
  
  private static final String GET_URL = "https://ragkrish.freshdesk.com/api/v2/tickets";

  private static final String POST_URL = "https://ragkrish.freshdesk.com/api/v2/tickets";

  private static final int INVALID_TICKET_ID = -1;
  
  private static final int OK = 0;
  private static final int ERROR = -1;
  
/* START sendGET */
  private static void sendGET() throws IOException
  
  {
    URL obj = new URL(GET_URL);
    HttpURLConnection con = (HttpURLConnection) obj.openConnection();

    con.setRequestMethod("GET");
    con.setRequestProperty("User-Agent", USER_AGENT);    
    con.setRequestProperty("content-type", "application/json; charset=utf-8");
    
    String userpass = USERNAME_PASSWD;
    String basicAuth = "Basic " + javax.xml.bind.DatatypeConverter.printBase64Binary(userpass.getBytes());
    con.setRequestProperty ("Authorization", basicAuth);    
        
    int responseCode = con.getResponseCode();
    System.out.println("GET Response Code :: " + responseCode);

    BufferedReader in;
    if (responseCode == HttpURLConnection.HTTP_OK) 
    {   
      in = new BufferedReader(new InputStreamReader(con.getInputStream()));
      System.out.println("GET OK");
    } 
    else 
    {
      in = new BufferedReader(new InputStreamReader(con.getErrorStream()));
      System.out.println("GET request ERROR!");
    }
    
      String inputLine;
      StringBuffer response = new StringBuffer();

      while ((inputLine = in.readLine()) != null) {
        response.append(inputLine);
      }
      in.close();

      System.out.println(response.toString());
  }
/* END sendGET */
  
  /* START searchTicket */  
  private static int searchTicket(String pointName) throws IOException
  
  {
    URL obj = new URL(GET_URL);
    int retCode = OK;
    
    HttpURLConnection con = (HttpURLConnection) obj.openConnection();

    con.setRequestMethod("GET");
    con.setRequestProperty("User-Agent", USER_AGENT);    
    con.setRequestProperty("content-type", "application/json; charset=utf-8");
    
    String userpass = USERNAME_PASSWD;
    String basicAuth = "Basic " + javax.xml.bind.DatatypeConverter.printBase64Binary(userpass.getBytes());
    con.setRequestProperty ("Authorization", basicAuth);    
        
    int responseCode = con.getResponseCode();
    System.out.println("GET Response Code :: " + responseCode);

    BufferedReader in;
    if (responseCode == HttpURLConnection.HTTP_OK) 
    {   
      in = new BufferedReader(new InputStreamReader(con.getInputStream()));
      System.out.println("GET OK");
    } 
    else 
    {
      in = new BufferedReader(new InputStreamReader(con.getErrorStream()));
      System.out.println("GET request ERROR!");
    }
    
      String inputLine;
      StringBuffer response = new StringBuffer();

      while ((inputLine = in.readLine()) != null) {
        response.append(inputLine);
      }
      in.close();

      System.out.println(response.toString());
  }
/* END searchTicket */
  
/* START sendPOST */
  private static int sendPOST(String pointName, int priority) throws IOException 
  {   
    int retCode = OK;
    URL obj = new URL(POST_URL);
    HttpURLConnection con = (HttpURLConnection) obj.openConnection();
    con.setRequestMethod("POST");
    con.setRequestProperty("User-Agent", USER_AGENT);

    con.setRequestProperty("content-type", "application/json; charset=utf-8");
    
    String userpass = USERNAME_PASSWD;
    String basicAuth = "Basic " + javax.xml.bind.DatatypeConverter.printBase64Binary(userpass.getBytes());
    con.setRequestProperty ("Authorization", basicAuth);               
    
    String params;
    try
    {
      JSONObject json = new JSONObject();
      json.put("email", "test@test.com");
      json.put("description", "Temperature Alarm detected by Niagara!");
      json.put("subject","Fix AC/Heater via command centre");
      json.put("point-name", pointName);
      json.put("priority", priority);
      json.put("priority", 1);
      json.put("status", 2);

      params = json.toString();
    }
    catch (JSONException e)
    {
      System.out.println("sendPOST: json exception!!");
      System.out.println("Aborting sendPOST");
      return ERROR;
    }

    // For POST only - START
    con.setDoOutput(true);
    OutputStream os = con.getOutputStream();
    os.write(params.getBytes());
    os.flush();
    os.close();
    // For POST only - END

    int responseCode = con.getResponseCode();
    System.out.println("POST Response Code :: " + responseCode);

    BufferedReader in;
    if (responseCode == HttpURLConnection.HTTP_CREATED) 
    {   
      in = new BufferedReader(new InputStreamReader(con.getInputStream()));
      System.out.println("POST OK. Ticket Created!!");
      retCode = OK;
    } 
    else 
    {
      in = new BufferedReader(new InputStreamReader(con.getErrorStream()));
      System.out.println("POST request ERROR!");
      retCode = ERROR;
    }    
    
      String inputLine;
      StringBuffer response = new StringBuffer();

      while ((inputLine = in.readLine()) != null) {
        response.append(inputLine);
      }
      in.close();

      System.out.println(response.toString());
      return retCode;
  }  
/* END sendPOST */  
  
  @Override
  public void handleAlarm(BAlarmRecord bAlarmRecord) {

    System.out.print("LeanovateHeliosRx: HndlAlm" + "AlmRec:" + bAlarmRecord + "\n"); 

    if (bAlarmRecord.getSourceState().getOrdinal() == BSourceState.NORMAL)
    {
    	System.out.print("LeanovateHeliosRx: HndlAlm: " + "Normal State. Resolve Ticket... \n");
    	
    	// find if ticket was created earlier when alarm was raised, for this point name. if yes, update the
    	// ticket to RESOLVED. unique key to search tkts is pointName
    	try
    	{
    	    int tktId = searchTicket(bAlarmRecord.getSource().encodeToString());
    	    if (tktId != INVALID_TICKET_ID)
    	    {
    	      System.out.print("LeanovateHeliosRx: HndlAlm: Ticket already exists with" + "ID:" + tktId + "\n");
    	      //  TODO: ticket exists. update ticket with status=resolved
    	      
    	    }
    	    else
    	    {
            System.out.print("LeanovateHeliosRx: HndlAlm: Ticket doesnt exit.  Abnormal condition!! \n");    	      
    	      // ticket for this point is not present now. either it was not created when alarm happenned, or 
    	      // has already been resolved or deleted in ticketing system. log and move on. nothing to do. 
    	      // ideally this should not happen
            return;
    	    }
    	}
    	catch (IOException e)
    	{
    	  System.out.print("LeanovateHeliosRx: sendGET IOException!! \n");
    	}
    }
    else 
    {
    	System.out.print("LeanovateHeliosRx: HndlAlm: " + "Alarm State!!. POST Ticket... \n");
      try
      {
          // create ticket with unique ID as pointName (TODO: confirm if this is unique in setup)
          int result = sendPOST(bAlarmRecord.getSource().encodeToString(), bAlarmRecord.getPriority());
          if (result != OK)
          {
            System.out.print("LeanovateHeliosRx: sendPOST: Ticket Create Error!! \n");
            return;
          }
      }
      catch (IOException e)
      {
        System.out.print("LeanovateHeliosRx: sendPOST: IOException!! \n");
      }            	
      
    }
  }
}
