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

  // variables
  private int childId = 1;

  @Override
  public void handleAlarm(BAlarmRecord bAlarmRecord) {

    System.out.println("\n***LeanovateHeliosRx: HndlAlm: " + "AlmRec:" + bAlarmRecord); 
    
    System.out.println("Starting child thread with ID: " + this.childId);
    Runnable r = new BLeanovateAlarmConsumer(this.childId++, bAlarmRecord);
    Thread child = new Thread(r);
    child.setDaemon(true);
    child.start();    
  }
}
