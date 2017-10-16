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
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

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

  // constants
  private static final String USER_AGENT = "niagara-leanovate/1.0";
  private static final String USERNAME_PASSWD = "kragha2000@gmail.com:123surya";
  private static final String GET_URL = "https://ragkrish.freshdesk.com/api/v2/tickets";
  private static final String GET_SEARCH_TKT_URL = "https://ragkrish.freshdesk.com/api/v2/search/tickets?query=";
  private static final String POST_URL = "https://ragkrish.freshdesk.com/api/v2/tickets";
  
  private static final int OK = 0;
  private static final int ERROR = -1;
  
  private static final int TKT_STATUS_OPEN = 2;
  private static final int TKT_STATUS_PENDING = 3;
  private static final int TKT_STATUS_RESOLVED = 4;
  
  private static final int TKT_PRTY_HIGH = 3;
 
  // public methods...

  /* START getAllTickets */
  private static void getAllTickets() throws IOException
  
  {
    URL obj = new URL(GET_URL);
    HttpURLConnection con = (HttpURLConnection) obj.openConnection();

    con.setRequestMethod("GET");
    con.setRequestProperty("User-Agent", USER_AGENT);    
    con.setRequestProperty("content-type", "application/json; charset=utf-8");
    
    String userpass = USERNAME_PASSWD;
    String basicAuth = "Basic " + 
        javax.xml.bind.DatatypeConverter.printBase64Binary(userpass.getBytes());
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
/* END getAllTickets */
  
  /* START searchTicket */  
  /* searches server for tkt with pointname as pointName. returns tkt:id if found. 
   * returns ERROR(-1) if not found */
  private static int searchOpenTicket(String pointName) throws IOException
  
  {
    String queryField = "pointname";
    String FilterGetUrl = GET_SEARCH_TKT_URL + "\"" +
        "((status:" + TKT_STATUS_OPEN + " OR " + "status:" + TKT_STATUS_PENDING + ")" +    
       " AND " + 
        "(" + queryField + ":" + "\'" + pointName + "\'" + "))" +
        "\"";
    FilterGetUrl = FilterGetUrl.replaceAll(" ", "%20");
    
    System.out.println("searchTkt: url: " + FilterGetUrl);    
    
    URL obj = new URL(FilterGetUrl);
    int retCode = OK;
    
    HttpURLConnection con = (HttpURLConnection) obj.openConnection();

    con.setRequestMethod("GET");
    con.setRequestProperty("User-Agent", USER_AGENT);    
    con.setRequestProperty("content-type", "application/json; charset=utf-8");
    
    String userpass = USERNAME_PASSWD;
    String basicAuth = "Basic " + 
        javax.xml.bind.DatatypeConverter.printBase64Binary(userpass.getBytes());
    con.setRequestProperty ("Authorization", basicAuth);    
        
    int responseCode = con.getResponseCode();
    System.out.println("SEARCHTKT: GET Response Code :: " + responseCode);

    BufferedReader in;
    
    if (responseCode == HttpURLConnection.HTTP_OK) 
    {   
      in = new BufferedReader(new InputStreamReader(con.getInputStream()));
      System.out.println("SEARCH TKT GET OK");
      retCode = OK;
    } 
    else 
    {
      in = new BufferedReader(new InputStreamReader(con.getErrorStream()));
      System.out.println("SEARCH TKT GET Error!!");
      retCode = ERROR;
    }

      String inputLine;
      StringBuffer response = new StringBuffer();

      while ((inputLine = in.readLine()) != null) {
        response.append(inputLine);
      }
      in.close();

      String rspString = response.toString();
      
      System.out.println("RESPONSE STRING: " + rspString);
      
      try
      {
        if (retCode == OK)
        {
          // we got results. 
          // check total > 0 if found. then look for result[] and find id field
          
          JSONObject rspJsonObj = new JSONObject(rspString);
          int id;
          
          int total = rspJsonObj.getInt("total");
          if (total > 0)
          {
            if (total > 1)
            {
              // v expect 1 tkt per point. this is unexpected when more than 1 tkt matches a point name. log it
              System.out.println("More than one tkt for same point found. unexpected error!");
            }
            // atleast 1 entry found. get 1st entry. 
            // assuming 1 unique match as we already searched on pointname. 
            // we are assuming
            // for each point, only 1 tkt will be created. ASSUME
            id = rspJsonObj.getJSONArray("results").getJSONObject(0).getInt("id");
            int status = rspJsonObj.getJSONArray("results").getJSONObject(0).getInt("status");
            System.out.println("ID: " + id + " status:" + status);    
            return id;
          }
          else
          {
            System.out.println("total 0. no open/pending tkts found with pointname: " 
                  + pointName);    
            return ERROR;
          }
        }
        else
        {
          return ERROR;
        }
      }  //try
      catch (JSONException e)
      {
        System.out.println("searchOpenTicket: json exception!!. Aborting search..");
        return ERROR;
      }
      
  }
/* END searchTicket */
  
/* START createTicket */
  private static int createTicket(String pointName, int priority) throws IOException 
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
      json.put("subject","Niagara Alarm Ticket: Fix AC/Heater via command centre");
      
 //     String[] tmpStrArray = new String[2];
 //     tmpStrArray[0] = pointName;   
 //     json.put("tags",  tmpStrArray);
      
      json.put("priority", priority);
      json.put("status", 2);

      Map<String, String> map = new HashMap<String, String>();
      map.put("pointname", pointName);      
      
      json.put("custom_fields", map);
 
      
      params = json.toString();
      System.out.println("params:"+ params);
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
/* END createTicket */  

/* START updateTicket */
  private static int updateTicket(int tktId, String pointName, int newStatus) throws IOException 
  {   
    int retCode = OK;
    
/*    int tktId = searchOpenTicket(pointName);
    // TODO: check also that tkt status is non-resolved..  
    
    if (tktId == ERROR)
    {
      // TODO: check if this is real scenario. ex: tkt got created when alarm happenned. 
      // operator resolved tkt. alarm later cleared. 
      System.out.println("updateTicket: Could not find ticket at server with point-name as: " + pointName);
      return ERROR;
    }
    // tkt found. send PUT and update status field
*/
    
    URL obj = new URL(POST_URL + "/" + tktId);  // PUT url/tickets/<id>
    HttpURLConnection con = (HttpURLConnection) obj.openConnection();
    con.setRequestMethod("PUT");
    con.setRequestProperty("User-Agent", USER_AGENT);

    con.setRequestProperty("content-type", "application/json; charset=utf-8");
    
    String userpass = USERNAME_PASSWD;
    String basicAuth = "Basic " + javax.xml.bind.DatatypeConverter.printBase64Binary(userpass.getBytes());
    con.setRequestProperty ("Authorization", basicAuth);               
    
    String params;
    try
    {
      JSONObject json = new JSONObject();

      // update just the status. leave remaining tkt fields as is
      json.put("status", newStatus);

      params = json.toString();
      System.out.println("updateTicket: params:"+ params);
    }
    catch (JSONException e)
    {
      System.out.println("updateTicket: PUT: json exception!!");
      System.out.println("updateTicket: Aborting PUT");
      return ERROR;
    }

    // For POST/PUT only - START
    con.setDoOutput(true);
    OutputStream os = con.getOutputStream();
    os.write(params.getBytes());
    os.flush();
    os.close();
    // For POST/PUT only - END

    int responseCode = con.getResponseCode();
    System.out.println("updateTicket: PUT Response Code :: " + responseCode);

    BufferedReader in;
    if (responseCode == HttpURLConnection.HTTP_OK) 
    {   
      in = new BufferedReader(new InputStreamReader(con.getInputStream()));
      System.out.println("updateTicket: PUT OK. Ticket Updated!!");
      retCode = OK;
    } 
    else 
    {
      in = new BufferedReader(new InputStreamReader(con.getErrorStream()));
      System.out.println("updateTicket: PUT ERROR!");
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
/* END updateTicket */  
  
  @Override
  public void handleAlarm(BAlarmRecord bAlarmRecord) {

    System.out.println("\n***LeanovateHeliosRx: HndlAlm: " + "AlmRec:" + bAlarmRecord); 

    if (bAlarmRecord.getSourceState().getOrdinal() == BSourceState.NORMAL)
    {
    	System.out.println("LeanovateHeliosRx: HndlAlm: " + "Normal State. Resolve Ticket...");
    	
    	// find if ticket was created earlier when alarm was raised, for this point name. if yes, update the
    	// ticket to RESOLVED. unique key to search tkts is pointName
    	try
    	{
    	    String pointName = bAlarmRecord.getSource().encodeToString();
    	    int tktId = searchOpenTicket(pointName);
    	    if (tktId != ERROR)
    	    {
            //  ticket exists. update ticket with status=resolved
    	      System.out.println("LeanovateHeliosRx: HndlAlm: Open Ticket exists with" + "ID:" 
    	            + tktId );

            System.out.println("LeanovateHeliosRx: HndlAlm: Tkt Update.. " + "  retCode:" 
                  + tktId + "  point-name: " + pointName + 
                  "  status to: " + TKT_STATUS_RESOLVED);
            int ret = updateTicket(tktId, pointName, TKT_STATUS_RESOLVED);
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
            return;
    	    }
    	}
    	catch (IOException e)
    	{
    	  System.out.println("LeanovateHeliosRx: sendGET IOException!!");
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
          int tktId = searchOpenTicket(pointName);
          if (tktId != ERROR)
          {
            //  ticket exists. dont do anything as point is in alarm still and tkt exists.
            System.out.println("LeanovateHeliosRx: HndlAlm: Open Ticket exists with" + "ID:" 
                  + tktId  + "  point-name: " + pointName +
                  "\nDoing nothing as ticket exists");
            return;
          }

          // no tkt exists for this point name. create one.
          // create ticket with unique ID as pointName (TODO: confirm if this is unique in setup)
          int result = createTicket(bAlarmRecord.getSource().encodeToString(), 
                                    TKT_PRTY_HIGH);
          if (result != OK)
          {
            System.out.println("LeanovateHeliosRx: sendPOST: Ticket Create Error!! " +
                "point-name: " + bAlarmRecord.getSource().encodeToString());
            return;
          }
      }
      catch (IOException e)
      {
        System.out.println("LeanovateHeliosRx: sendPOST: IOException!!");
      }            	
      
    }
  }
}
