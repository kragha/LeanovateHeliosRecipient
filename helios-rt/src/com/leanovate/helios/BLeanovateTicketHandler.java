package com.leanovate.helios;

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

public class BLeanovateTicketHandler
{
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
  public void getAllTickets() throws IOException
  
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
  public int searchOpenTicket(String pointName) throws IOException
  
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
  public int createTicket(String pointName, int priority) throws IOException 
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
  public int updateTicket(int tktId, String pointName, int newStatus) throws IOException 
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

  
}
